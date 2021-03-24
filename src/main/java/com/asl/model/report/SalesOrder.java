package com.asl.model.report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2021
 */
@Data
@XmlRootElement(name = "salesorder")
@XmlAccessorType(XmlAccessType.FIELD)
public class SalesOrder {

	private String orderNumber;
	private String reqNumber;
	private String reqBranch;
	private String date;
	private String status;
	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	private List<ItemDetails> items = new ArrayList<>();
}
