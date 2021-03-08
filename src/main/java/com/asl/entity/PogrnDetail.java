package com.asl.entity;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "pogrndetail")
@IdClass(PogrnDetailPK.class)
@EqualsAndHashCode(of = { "zid", "xgrnnum", "xrow" }, callSuper = false)
public class PogrnDetail extends AbstractModel<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 208492314185601309L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private String zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xgrnnum")
	private String xgrnnum;
	
	@Id
	@Basic(optional = false)
	@Column(name = "xrow")
	private int xrow;

	@Column(name = "xdocrow")
	private String xdocrow;
	
	@Column(name = "xcomtype")
	private String xcomtype;
	
	@Column(name = "xitem")
	private String xitem;	
	
	@Column(name = "xqtygrn")
	private BigDecimal xqtygrn;
	
	@Column(name = "xrate")
	private BigDecimal xrate;
	
	@Column(name = "xlineamt")
	private BigDecimal xlineamt;
	
	@Column(name = "xqtyprn")
	private BigDecimal xqtyprn;
	
	@Column(name = "xunitpur")
	private String xunitpur;
	
	@Column(name = "xnote")
	private String xnote;
}
