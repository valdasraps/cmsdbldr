package org.cern.cms.dbloader.model.construct.ext;

import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

/**
 * Tracking Request statistics model class.
 * @author valdo
 */
@Entity
@Table(name = "REQUEST_STATS")
@Immutable
@Getter @Setter @ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class RequestStat {
    
    @Id
    @Column(name = "REQ_ID")
    private BigInteger id;

    @Basic
    @Column(name = "REQUESTED")
    private Integer requested;

    @Basic
    @Column(name = "PACKAGING")
    private Integer packaging;

    @Basic
    @Column(name = "SHIPPED")
    private Integer shipped;

    @Basic
    @Column(name = "CANCELED")
    private Integer canceled;
    
}