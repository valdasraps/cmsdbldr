package org.cern.cms.dbloader.model.serial.map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "POSITION_SCHEMAS")
@DiscriminatorValue("POSITION_SCHEMAS")
@Getter @Setter
public class PositionSchema extends AttrBase {
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    private Boolean deleted = false;
    
}
