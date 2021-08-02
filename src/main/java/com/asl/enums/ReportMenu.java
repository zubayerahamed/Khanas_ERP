package com.asl.enums;

import java.util.Map;

import com.asl.model.ReportParamMap;

/**
 * @author Zubayer Ahamed
 * @since Dec 26, 2020
 */
public enum ReportMenu {

	// Procurements  -- M0300
	RM0301(999, "300", "Procurements", "Purchase Order Listing", ReportParamMap.RM0301, "Y", false, false, 1000),
	RM0302(999, "300", "Procurements", "Suggestion Report", ReportParamMap.RM0302, "Y", false, false, 1000),
	RM0303(999, "300", "Procurements", "Purchase Listing", ReportParamMap.RM0303, "Y", false, false, 1000),
	RM0304(999, "300", "Procurements", "Purchase Deviation Listing", ReportParamMap.RM0304, "Y", false, false, 1000),
	RM0305(999, "300", "Procurements", "GRN Listing", ReportParamMap.RM0305, "Y", false, false, 1000),
	RM0306(999, "300", "Procurements", "GRN Return Listing", ReportParamMap.RM0306, "Y", true, false, 1000),
	RM0307(999, "300", "Procurements", "Supplier Ledger Detail", ReportParamMap.RM0307, "Y", false, false, 1000),
	RM0308(999, "300", "Procurements", "Supplier Ledger Summary", ReportParamMap.RM0308, "Y", false, false, 1000),
	RM0309(999, "300", "Procurements", "Supplier Payment Listing", ReportParamMap.RM0309, "Y", false, false, 1000),
	RM0310(999, "300", "Procurements", "Supplier Opening Balanace", ReportParamMap.RM0310, "Y", false, false, 1000),
	RM0311(999, "300", "Procurements", "Supplier List", ReportParamMap.RM0311, "Y", false, false, 1000),
	RM0312(999, "300", "Procurements", "Purchase Return Listing", ReportParamMap.RM0312, "Y", false, false, 1000),
	RM0313(999, "300", "Procurements", "Daily Meat Purchasing & Receiving Status", ReportParamMap.RM0313, "Y", false, false, 1000),
	RM0314(999, "300", "Procurements", "Customer List", ReportParamMap.RM0314, "Y", false, false, 1000),
	

	// Sales & Invoicing  -- M0400
	RM0401(999, "400", "Sales & Invoicing", "Sales Order Listing", ReportParamMap.RM0401, "Y", false, false, 1000),
	RM0402(999, "400", "Sales & Invoicing", "Sales Listing Detail", ReportParamMap.RM0402, "Y", false, false, 1000),
	RM0403(999, "400", "Sales & Invoicing", "Sales Listing Summary", ReportParamMap.RM0403, "Y", false, false, 1000),
	RM0404(999, "400", "Sales & Invoicing", "Money Receipt Listing", ReportParamMap.RM0404, "Y", false, false, 1000),
	RM0405(999, "400", "Sales & Invoicing", "Customer Ledger Detail", ReportParamMap.RM0405, "Y", false, false, 1000),
	RM0406(999, "400", "Sales & Invoicing", "Customer Ledger Summary", ReportParamMap.RM0406, "Y", false, false, 1000),
	RM0407(999, "400", "Sales & Invoicing", "Both Party Ledger", ReportParamMap.RM0407, "Y", false, false, 1000),
	//RM0408(999, "400", "Sales & Invoicing", "Customer List", ReportParamMap.RM0408, "Y", false, false, 1000),
	//RM0409(999, "400", "Sales & Invoicing", "Receivable and Collection Statement", ReportParamMap.RM0409, "Y", false, false, 1000),
	RM0410(999, "400", "Sales & Invoicing", "Sales Return Listing", ReportParamMap.RM0410, "Y", false, false, 1000),
	RM0411(999, "400", "Sales & Invoicing", "Branch Distribution Report", ReportParamMap.RM0411, "Y", false, false, 1000),
	RM0412(999, "400", "Sales & Invoicing", "Distribution Deviation Report", ReportParamMap.RM0412, "Y", false, false, 1000),

	// Production  -- M0500
	RM0501(999, "500", "Production", "BOM List", ReportParamMap.RM0501, "Y", false, false, 1000),
	RM0502(999, "500", "Production", "Batch wise production", ReportParamMap.RM0502, "Y", false, false, 1000),
	RM0503(999, "500", "Production", "Batch wise material consumptions", ReportParamMap.RM0503, "Y", false, false, 1000),
	RM0504(999, "500", "Production", "Daily Production Report", ReportParamMap.RM0504, "Y", false, false, 1000),
	
	// Inventory  -- M0600
	RM0601(999, "600", "Inventory", "Current Stock", ReportParamMap.RM0601, "Y", false, false, 1000),
	RM0602(999, "600", "Inventory", "Date Wise Stock Status", ReportParamMap.RM0602, "Y", false, false, 1000),
	RM0603(999, "600", "Inventory", "Stock Low Status", ReportParamMap.RM0603, "Y", false, false, 1000),
	//RM0604(999, "600", "Inventory", "Inventory Consumption Report", ReportParamMap.RM0604, "Y", false, false, 1000),
	RM0605(999, "600", "Inventory", "Item Transaction Detail", ReportParamMap.RM0605, "Y", false, false, 1000),
	RM0606(999, "600", "Inventory", "Item Transaction Summary", ReportParamMap.RM0606, "Y", false, false, 1000),
	RM0607(999, "600", "Inventory", "Item List", ReportParamMap.RM0607, "Y", false, false, 1000),
	RM0608(999, "600", "Inventory", "Inventory Management Report", ReportParamMap.RM0608, "Y", false, false, 1000),
	//RM0609(999, "600", "Inventory", "Employee Listing Report", ReportParamMap.RM0609, "Y", false, false, 1000),
	//RM0610(999, "600", "Inventory", "User Listing Report", ReportParamMap.RM0610, "Y", false, false, 1000),
	//RM0611(999, "600", "Inventory", "Transfer order Listing Report", ReportParamMap.RM0611, "Y", false, false, 1000),
	 
	
	
	//Land Report --RM0700
	RM0701(999, "700", "Land Management", "Land Details Report", ReportParamMap.RM0701, "Y", false, false, 1000),
	RM0702(999, "700", "Land Management", "Land Information Report", ReportParamMap.RM0702, "Y", false, false, 1000),
	RM0703(999, "700", "Land Management", "Sales Listing Detail", ReportParamMap.RM0703, "Y", false, false, 1000),
	RM0704(999, "700", "Land Management", "Sales Listing Summary", ReportParamMap.RM0704, "Y", false, false, 1000),
	RM0705(999, "700", "Land Management", "Dag Wise Land Information Details", ReportParamMap.RM0705, "Y", false, false, 1000),
	RM0706(999, "700", "Land Management", "Member Wise Land Information Details", ReportParamMap.RM0706, "Y", false, false, 1000),
	RM0707(999, "700", "Land Management", "Money Receipt Listing", ReportParamMap.RM0707, "Y", false, false, 1000);

	private int seqn;
	private String group;
	private String groupName;
	private String description;
	private Map<String, String> paramMap;
	private String defaultAccess;
	private boolean fopEnabled;
	private boolean chunkDownload;
	private int chunkLimit;

	private ReportMenu(int seqn, String group, String groupName, String des, Map<String, String> paramMap, String defaultAccess, boolean fopEnabled, boolean chunkDownload, int chunkLimit) {
		this.seqn = seqn;
		this.group = group;
		this.groupName = groupName;
		this.description = des;
		this.paramMap = paramMap;
		this.defaultAccess = defaultAccess;
		this.fopEnabled = fopEnabled;
		this.chunkDownload = chunkDownload;
		this.chunkLimit = chunkLimit;
	}

	public int getSeqn() {
		return this.seqn;
	}

	public String getGroup() {
		return this.group;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public String getDescription() {
		return this.description;
	}

	public Map<String, String> getParamMap() {
		return this.paramMap;
	}

	public String getDefaultAccess() {
		return this.defaultAccess;
	}

	public boolean isFopEnabled() {
		return this.fopEnabled;
	}

	public boolean isChunkDownload() {
		return this.chunkDownload;
	}

	public int getChunkLimit() {
		return this.chunkLimit;
	}
}
