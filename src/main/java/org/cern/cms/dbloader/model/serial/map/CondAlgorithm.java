package org.cern.cms.dbloader.model.xml.map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "COND_ALGORITHMS")
@DiscriminatorValue("COND_ALGORITHMS")
@Getter @Setter
public class CondAlgorithm extends AttrBase {
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name="IS_RECORD_DELETED")
    @Type(type="true_false")
    private Boolean deleted = false;
	
}
