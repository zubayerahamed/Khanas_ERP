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
@Table(name = "landeducation")
@IdClass(LandEducationPK.class)
@EqualsAndHashCode(of = { "zid", "xperson","xrow"}, callSuper = false)
public class LandEducation extends AbstractModel<String> {

	private static final long serialVersionUID = 6947838252571102642L;
	
	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private String zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xperson")
	private String xperson;

	@Id
	@Basic(optional = false)
	@Column(name = "xrow")
	private int xrow;

	@Column(name = "xyear")
	private String xyear;

	@Column(name = "xexam")
	private String xexam;

	@Column(name = "xinstitude")
	private String xinstitude;

	@Column(name = "xmajor")
	private String xmajor;

	@Column(name = "xresult")
	private String xresult;
}
