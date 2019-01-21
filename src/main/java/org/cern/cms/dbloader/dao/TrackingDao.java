package org.cern.cms.dbloader.dao;

import org.cern.cms.dbloader.model.managemnt.AuditLog;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.construct.ext.Request;
import org.cern.cms.dbloader.model.construct.ext.RequestItem;
import org.cern.cms.dbloader.model.construct.ext.Shipment;
import org.cern.cms.dbloader.model.construct.ext.ShipmentItem;
import org.cern.cms.dbloader.model.managemnt.Location;
import org.hibernate.criterion.Restrictions;

@Log4j
public class TrackingDao extends DaoBase {

    @Inject
    public TrackingDao(@Assisted SessionManager sm) throws Exception {
        super(sm);
    }
    
    public void save(Request xmlRequest, AuditLog alog) throws Exception {
        
        if (xmlRequest.getName() == null || xmlRequest.getName().isEmpty()) {
            throw new XMLParseException(String.format("Request name not defined in %s", xmlRequest));
        }

        if (xmlRequest.getInstitutionName() == null || xmlRequest.getInstitutionName().isEmpty()) {
            throw new XMLParseException(String.format("Institution not defined in %s", xmlRequest));
        }
        
        if (xmlRequest.getLocationName() == null || xmlRequest.getLocationName().isEmpty()) {
            throw new XMLParseException(String.format("Location not defined in %s", xmlRequest));
        }

        Location location = resolveInstituteLocation(xmlRequest.getInstitutionName(), xmlRequest.getLocationName());

        if (xmlRequest.getItems() != null) {
            for (RequestItem item: xmlRequest.getItems()) {
                item.setKindOfPart(resolveKindOfPart(item.getKindOfPartName()));
            }
        }
        
        Request dbRequest = (Request) session.createCriteria(Request.class)
                .add(Restrictions.eq("name", xmlRequest.getName()))
                .add(Restrictions.eq("location", location))
                .uniqueResult();

        if (dbRequest == null) {
            
            dbRequest = xmlRequest;
            
            if (dbRequest.getStatus() == null) {
                dbRequest.setStatus("OPEN");
            }
            
            dbRequest.setLocation(location);
            
        } else {
            
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

        for (RequestItem dbItem: dbRequest.getItems()) {
            dbItem.setRequest(dbRequest);
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
                dbShipment.setStatus("PACKAGING");
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

        for (ShipmentItem dbItem: dbShipment.getItems()) {
            
            if (dbItem.getRequestName() != null) {

                RequestItem requestItem = (RequestItem) session.createCriteria(RequestItem.class)
                        .add(Restrictions.eq("kindOfPart", dbItem.getPart().getKindOfPart()))
                        .createCriteria("request")
                            .add(Restrictions.eq("name", dbItem.getRequestName()))
                            .add(Restrictions.eq("location", dbShipment.getToLocation()))
                        .uniqueResult();

                if (requestItem == null) {
                    throw new XMLParseException(String.format("Shipment item request not found for %s", dbItem));
                }

                dbItem.setRequestItem(requestItem);

            }
            
            if (dbItem.getShipment() == null) {
                dbItem.setShipment(dbShipment);
            }
            
        }
        
        session.save(dbShipment);
        
    }

    
}
