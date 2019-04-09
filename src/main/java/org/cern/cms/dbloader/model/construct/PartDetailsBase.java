package org.cern.cms.dbloader.model.construct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Field;
import java.math.BigInteger;

@MappedSuperclass
@Getter
@Setter
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PartDetailsBase {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "ANY_PARTS_DETAILS_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_PARTS_DETAILS_ID_SEQ", sequenceName = "ANY_PARTS_DETAILS_ID_SEQ", allocationSize = 20)
    @XmlTransient
    private BigInteger id;

    @OneToOne
    @JoinColumn(name = "PART_ID")
    @XmlTransient
    private Part part;

    public void copyProps(PartDetailsBase newDetails) throws IllegalAccessException {
        for (Field f : this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            for (Field nf : newDetails.getClass().getDeclaredFields()){
                if (nf.getName() == f.getName()){
                    nf.setAccessible(true);
                    Object newValue = nf.get(newDetails);
                    f.set(this, newValue);
                }
            }

        }
    }

    public abstract <T extends PartDetailsBase> T getRealClass(Class<T> clazz) throws Exception;

}
