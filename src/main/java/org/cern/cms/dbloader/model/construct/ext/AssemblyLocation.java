package org.cern.cms.dbloader.model.construct.ext;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.IdClass;
import lombok.EqualsAndHashCode;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.model.managemnt.Location;

@Entity
@IdClass(AssemblyLocation.class)
@Table(name = "ASSEMBLY_LOCATIONS")
@Getter
@Setter
@ToString(callSuper = false)
@EqualsAndHashCode
public class AssemblyLocation implements Serializable {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID_SAME")
    private Location sameLocation;
    
    
    
}