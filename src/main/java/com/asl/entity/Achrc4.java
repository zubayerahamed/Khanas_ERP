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
@Table(name = "achrc4")
@IdClass(Achrc4PK.class)
@EqualsAndHashCode(of = { "zid", "xhrc4" }, callSuper = false)
public class Achrc4 extends AbstractModel<String>{
	
	private static final long serialVersionUID = -3861938671021034446L;
	
	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private String zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xhrc4")
	private String xhrc4;
	
	@Column(name = "xhrc1")
	private String xhrc1;
	
	@Column(name = "xdecs")
	private String xdecs;
	
	@Column(name = "xrow")
	private Integer xrow;
	
	@Column(name = "xacctype")
	private String xacctype;
	
	@Column(name = "xhrc2")
	private String xhrc2;

	@Column(name = "xhrc3")
	private String xhrc3;
}
