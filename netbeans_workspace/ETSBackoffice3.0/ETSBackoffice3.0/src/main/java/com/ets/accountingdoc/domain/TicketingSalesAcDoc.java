package com.ets.accountingdoc.domain;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yusuf
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Access(AccessType.FIELD)
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name = "tkt_sales_acdoc")
public class TicketingSalesAcDoc extends TicketingAcDoc implements Serializable{

    
}
