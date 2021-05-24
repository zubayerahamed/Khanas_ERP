package com.asl.entity;

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
@Table(name = "acgroup")
@IdClass(AcgroupPK.class)
@EqualsAndHashCode(of = { "zid", "xgroup" }, callSuper = false)
public class Acgroup extends AbstractModel<String>{

	private static final long serialVersionUID = 107018214168095842L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private String zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xgroup")
	private String xgroup;
	
	@Column(name = "Xdesc")
	private String Xdesc;
	
	@Column(name = "xacctype")
	private String xacctype;
	
	@Column(name = "xhrc1")
	private String xhrc1;
	
	@Column(name = "xhrc2")
	private String xhrc2;
	
	@Column(name = "xhrc3")
	private String xhrc3;
	
	@Column(name = "xhrc4")
	private String xhrc4;
	
	@Column(name = "xhrc5")
	private String xhrc5;
	
	@Column(name = "xnum")
	private Integer xnum;
	
	@Column(name = "xacc")
	private String xacc;
}
