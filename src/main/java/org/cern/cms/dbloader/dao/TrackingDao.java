package org.cern.cms.dbloader.dao;

import org.cern.cms.dbloader.model.managemnt.AuditLog;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.HashSet;
import java.util.Set;
import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.construct.ext.Request;
import org.cern.cms.dbloader.model.construct.ext.Request.RequestStatus;
import org.cern.cms.dbloader.model.construct.ext.RequestItem;
import org.cern.cms.dbloader.model.construct.ext.RequestStat;
import org.cern.cms.dbloader.model.construct.ext.Shipment;
import org.cern.cms.dbloader.model.construct.ext.Shipment.ShipmentStatus;
import org.cern.cms.dbloader.model.construct.ext.ShipmentItem;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.hibernate.criterion.Restrictions;

@Log4j
public class TrackingDao extends DaoBase {

    private static final String IN_TRANSITION_LOCATION = "In transition";
    private static final String IN_TRANSITION_INSTITUTION = "In transition";
    
    /**
     * In transition location cache.
     */
    private final Location inTransitionLocation;
    
    @Inject
    public TrackingDao(@Assisted SessionManager sm) throws Exception {
        super(sm);
        this.inTransitionLocation = resolveInstituteLocation(IN_TRANSITION_INSTITUTION, IN_TRANSITION_LOCATION);
    }
    
    /**
     * Processing request.
     * @param xmlRequest request object.
     * @param alog log object.
     * @throws Exception on any error.
     */
    public void save(Request xmlRequest, AuditLog alog) throws Exception {
        
        // Name has to be defined!
        if (xmlRequest.getName() == null || xmlRequest.getName().isEmpty()) {
            throw new XMLParseException(String.format("Request name not defined in %s", xmlRequest));
        }

        // Institution has to be defined!
        if (xmlRequest.getInstitutionName() == null || xmlRequest.getInstitutionName().isEmpty()) {
            throw new XMLParseException(String.format("Institution not defined in %s", xmlRequest));
        }
        
        // Location has to be defined!
        if (xmlRequest.getLocationName() == null || xmlRequest.getLocationName().isEmpty()) {
            throw new XMLParseException(String.format("Location not defined in %s", xmlRequest));
        }

        // Resolving location.
        Location location = resolveInstituteLocation(xmlRequest.getInstitutionName(), xmlRequest.getLocationName());

        // Resolving Kind of Conditions.
        if (xmlRequest.getItems() != null) {
            for (RequestItem item: xmlRequest.getItems()) {
                item.setKindOfPart(resolveKindOfPart(item.getKindOfPartName()));
            }
        }
        
        // Load Request from database if exists.
        Request dbRequest = (Request) session.createCriteria(Request.class)
                .add(Restrictions.eq("name", xmlRequest.getName()))
                .add(Restrictions.eq("location", location))
                .uniqueResult();

        // If in DB not found
        if (dbRequest == null) {
            
            dbRequest = xmlRequest;
            
            // Default status
            if (dbRequest.getStatus() == null) {
                dbRequest.setStatus(RequestStatus.OPEN);
            }
            
            // Setting location
            dbRequest.setLocation(location);
            
        } else {
            
            // Setting updates data
            
            if (xmlRequest.getStatus() != null) {
                dbRequest.setStatus(xmlRequest.getStatus());
            }
            
            if (xmlRequest.getComment() != null) {
                dbRequest.setComment(xmlRequest.getComment());
            }
            
            if (xmlRequest.getDate() != null) {
                dbRequest.setDate(xmlRequest.getDate());
            }
            
            if (xmlRequest.getPerson() != null) {
                dbRequest.setPerson(xmlRequest.getPerson());
            }

            if (xmlRequest.getItems() != null) {
                for (RequestItem xmlItem: xmlRequest.getItems()) {

                    boolean found = false;
                    for (RequestItem dbItem: dbRequest.getItems()) {

                        if (xmlItem.getKindOfPart().equals(dbItem.getKindOfPart())) {
                            found = true;

                            if (xmlItem.getQuantity() != null) {
                                dbItem.setQuantity(xmlItem.getQuantity());
                            }

                            if (xmlItem.getComment() != null) {
                                dbItem.setComment(xmlItem.getComment());
                            }

                            break;
                        }

                    }

                    if (!found) {
                        dbRequest.getItems().add(xmlItem);
                    }

                }
            }

            
        }

        // Set reverse link between items and container.
        for (RequestItem item: dbRequest.getItems()) {
            item.setRequest(dbRequest);
        }
        
        session.save(dbRequest);
        
    }

    public void save(Shipment xmlShipment, AuditLog alog) throws Exception {
        
        if (xmlShipment.getTrackingNumber() == null || xmlShipment.getTrackingNumber().isEmpty()) {
            throw new XMLParseException(String.format("Shipment tracking number not defined in %s", xmlShipment));
        }

        if (xmlShipment.getFromInstitutionName() != null && xmlShipment.getFromLocationName() != null) {
            xmlShipment.setFromLocation(resolveInstituteLocation(xmlShipment.getFromInstitutionName(), xmlShipment.getFromLocationName()));
        }

        if (xmlShipment.getToInstitutionName() != null && xmlShipment.getToLocationName() != null) {
            xmlShipment.setToLocation(resolveInstituteLocation(xmlShipment.getToInstitutionName(), xmlShipment.getToLocationName()));
        }
        
        if (xmlShipment.getItems() != null) {
            for (ShipmentItem item: xmlShipment.getItems()) {

                if ((item.getPart() != null && item.getPartAssembly() != null) ||
                    (item.getPart() == null && item.getPartAssembly() == null)) {
                    throw new XMLParseException(String.format("One and Only One of Part and PartAssembly must be defined for Shipment Item %s", item));
                }

                if (item.getPart() != null) {

                    item.setPart(resolvePart(item.getPart(), true));

                } else if (item.getPartAssembly() != null) {

                    item.setPart(resolvePartAssembly(item.getPartAssembly(), true));

                }
                
            }
        }
        
        Shipment dbShipment = (Shipment) session.createCriteria(Shipment.class)
                .add(Restrictions.eq("trackingNumber", xmlShipment.getTrackingNumber()))
                .uniqueResult();

        if (dbShipment == null) {
            
            dbShipment = xmlShipment;
            
            if (dbShipment.getStatus() == null) {
                dbShipment.setStatus(ShipmentStatus.PACKAGING);
            }
            
        } else {

            if (xmlShipment.getCompanyName() != null) {
                dbShipment.setCompanyName(xmlShipment.getCompanyName());
            }
            
            if (xmlShipment.getFromLocation() != null) {
                dbShipment.setFromLocation(xmlShipment.getFromLocation());
            }
            
            if (xmlShipment.getToLocation() != null) {
                dbShipment.setToLocation(xmlShipment.getToLocation());
            }
            
            if (xmlShipment.getStatus() != null) {
                dbShipment.setStatus(xmlShipment.getStatus());
            }
            
            if (xmlShipment.getComment() != null) {
                dbShipment.setComment(xmlShipment.getComment());
            }
            
            if (xmlShipment.getDate() != null) {
                dbShipment.setDate(xmlShipment.getDate());
            }
            
            if (xmlShipment.getPerson() != null) {
                dbShipment.setPerson(xmlShipment.getPerson());
            }

            if (xmlShipment.getItems() != null) {
                for (ShipmentItem xmlItem: xmlShipment.getItems()) {

                    boolean found = false;
                    for (ShipmentItem dbItem: dbShipment.getItems()) {

                        if (xmlItem.getPart().equals(dbItem.getPart())) {
                            found = true;

                            if (xmlItem.getComment() != null) {
                                dbItem.setComment(xmlItem.getComment());
                            }

                            if (xmlItem.getRequestItem() != null) {
                                dbItem.setRequestItem(xmlItem.getRequestItem());
                            }
                            
                            break;
                        }

                    }

                    if (!found) {
                        dbShipment.getItems().add(xmlItem);
                    }

                }
            }
            
        }

        Set<Request> requests = new HashSet<>();
        
        for (ShipmentItem item: dbShipment.getItems()) {
            
            if (item.getRequestName() != null) {

                RequestItem requestItem = (RequestItem) session.createCriteria(RequestItem.class)
                        .add(Restrictions.eq("kindOfPart", item.getPart().getKindOfPart()))
                        .createCriteria("request")
                            .add(Restrictions.eq("name", item.getRequestName()))
                            .add(Restrictions.eq("location", dbShipment.getToLocation()))
                        .uniqueResult();

                if (requestItem == null) {
                    throw new XMLParseException(String.format("Shipment item request not found for %s", item));
                }

                item.setRequestItem(requestItem);
                requests.add(requestItem.getRequest());

            }
            
            // Set reverse link between items and container.
            if (item.getShipment() == null) {
                item.setShipment(dbShipment);
            }

            switch (dbShipment.getStatus()) {
                
                case PACKAGING:

                    // New item!
                    if (item.getId() == null) {
                        
                        if (item.getPart().getLocation() == null) {
                            throw new XMLParseException(String.format("Shipment item location not defined %s", item));
                        }

                        if (!item.getPart().getLocation().equals(dbShipment.getFromLocation())) {
                            throw new XMLParseException(String.format("Shipment item location not match shipment source location %s", item));
                        }
                        
                    }
                    break;
                    
                case CANCELED:
                    
                    item.getPart().setLocation(dbShipment.getFromLocation());
                    break;
                    
                case SHIPPED:
                    
                    item.getPart().setLocation(inTransitionLocation);
                    break;

                case RECEIVED:
                    
                    item.getPart().setLocation(dbShipment.getToLocation());
                    break;
                    
            }
            
        }
        
        session.save(dbShipment);
        
        // Close completed requests
        requests.forEach((request) -> {
            RequestStat stat = request.getStatistics();
            
            if (request.getStatus() == RequestStatus.OPEN) {
                if (stat.getRequested() <= stat.getShipped()) {
                    request.setStatus(RequestStatus.CLOSED);
                    session.save(request);
                }
            }
            
        });
        
    }

    
}
