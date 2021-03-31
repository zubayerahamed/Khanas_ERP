package com.asl.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.entity.Cacus;
import com.asl.entity.Opdodetail;
import com.asl.entity.PogrnDetail;
import com.asl.entity.PogrnHeader;
import com.asl.entity.PoordDetail;
import com.asl.entity.PoordHeader;
import com.asl.enums.CodeType;
import com.asl.enums.ResponseStatus;
import com.asl.enums.TransactionCodeType;
import com.asl.model.report.ItemDetails;
import com.asl.model.report.PurchaseOrder;
import com.asl.model.report.PurchaseReport;
import com.asl.service.CacusService;
import com.asl.service.ImtrnService;
import com.asl.service.PogrnService;
import com.asl.service.PoordService;
import com.asl.service.XcodesService;
import com.asl.service.XtrnService;

@Controller
@RequestMapping("/purchasing/poord")
public class PurchaseOrderController extends ASLAbstractController {

	@Autowired private XcodesService xcodeService;
	@Autowired private PoordService poordService;
	@Autowired private XtrnService xtrnService;
	@Autowired private PogrnService pogrnService;
	@Autowired private ImtrnService imtrnService;
	@Autowired private CacusService cacusService;

	@GetMapping
	public String loadPoordPage(Model model) {
		model.addAttribute("poordheader", getDefaultPoordHeader());
		model.addAttribute("poprefix", xtrnService.findByXtypetrn(TransactionCodeType.PURCHASE_ORDER.getCode()));
		model.addAttribute("allPoordHeader", poordService.getPoordHeadersByXtype(TransactionCodeType.PURCHASE_ORDER.getCode()));
		model.addAttribute("warehouses", xcodeService.findByXtype(CodeType.WAREHOUSE.getCode()));
		model.addAttribute("postatusList", xcodeService.findByXtype(CodeType.STATUS.getCode()));
		return "pages/purchasing/poord/poord";
	}

	@GetMapping("/{xpornum}")
	public String loadPoordPage(@PathVariable String xpornum, Model model) {
		PoordHeader data = poordService.findPoordHeaderByXpornum(xpornum); 
		if(data == null) data = getDefaultPoordHeader();

		model.addAttribute("poordheader", data);
		model.addAttribute("poprefix", xtrnService.findByXtypetrn(TransactionCodeType.PURCHASE_ORDER.getCode()));
		model.addAttribute("allPoordHeader", poordService.getPoordHeadersByXtype(TransactionCodeType.PURCHASE_ORDER.getCode()));
		model.addAttribute("warehouses", xcodeService.findByXtype(CodeType.WAREHOUSE.getCode()));
		model.addAttribute("postatusList", xcodeService.findByXtype(CodeType.STATUS.getCode()));
		model.addAttribute("poorddetailsList", poordService.findPoorddetailByXpornum(xpornum));
		return "pages/purchasing/poord/poord";
	}

	private PoordHeader getDefaultPoordHeader() {
		PoordHeader poord = new PoordHeader();
		poord.setXtype(TransactionCodeType.PURCHASE_ORDER.getCode());
		//poord.setXtype("Purchase");
		//poord.setXtypetrn("PO Number");
		poord.setXstatuspor("Open");
		poord.setXtotamt(BigDecimal.ZERO);
		return poord;
	}

	@PostMapping("/save")
	public @ResponseBody Map<String, Object> save(PoordHeader poordHeader, BindingResult bindingResult){
		if((poordHeader == null || StringUtils.isBlank(poordHeader.getXtype())) || StringUtils.isBlank(poordHeader.getXtrnpor()) && StringUtils.isBlank(poordHeader.getXpornum())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		// Validate
		if(StringUtils.isBlank(poordHeader.getXcus())) {
			responseHelper.setErrorStatusAndMessage("Please select a supplier to create purchase order!");
			return responseHelper.getResponse();
		}

		// if existing record
		PoordHeader existPoordHeader = poordService.findPoordHeaderByXpornum(poordHeader.getXpornum());
		if(existPoordHeader != null) {
			BeanUtils.copyProperties(poordHeader, existPoordHeader, "xpornum", "xtype", "xdate", "xtotamt");
			long count = poordService.update(existPoordHeader);
			if(count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			responseHelper.setSuccessStatusAndMessage("Purchase Order updated successfully");
			responseHelper.setRedirectUrl("/purchasing/poord/" + poordHeader.getXpornum());
			return responseHelper.getResponse();
		}

		// If new
		long count = poordService.save(poordHeader);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		responseHelper.setSuccessStatusAndMessage("Purchase Order created successfully");
		responseHelper.setRedirectUrl("/purchasing/poord/" + poordHeader.getXpornum());
		return responseHelper.getResponse();
	}

	@PostMapping("/archive/{xpornum}")
	public @ResponseBody Map<String, Object> archive(@PathVariable String xpornum){
		return doArchiveOrRestore(xpornum, true);
	}

	@PostMapping("/restore/{xpornum}")
	public @ResponseBody Map<String, Object> restore(@PathVariable String xpornum){
		return doArchiveOrRestore(xpornum, false);
	}

	public Map<String, Object> doArchiveOrRestore(String xpornum, boolean archive){
		PoordHeader poordHeader = poordService.findPoordHeaderByXpornum(xpornum);
		if(poordHeader == null || "GRN Created".equalsIgnoreCase(poordHeader.getXstatuspor())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		poordHeader.setZactive(archive ? Boolean.FALSE : Boolean.TRUE);
		long count = poordService.update(poordHeader);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Purchase order updated successfully");
		responseHelper.setRedirectUrl("/purchasing/poord/" + poordHeader.getXpornum());
		return responseHelper.getResponse();
	}

	@GetMapping("{xpornum}/poorddetail/{xrow}/show")
	public String openPoordDetailModal(@PathVariable String xpornum, @PathVariable String xrow, Model model) {

		model.addAttribute("purchaseUnit", xcodeService.findByXtype(CodeType.PURCHASE_UNIT.getCode()));

		if("new".equalsIgnoreCase(xrow)) {
			PoordDetail poorddetail = new PoordDetail();
			poorddetail.setXpornum(xpornum);
			poorddetail.setXqtyord(BigDecimal.ONE.setScale(2, RoundingMode.DOWN));
			poorddetail.setXrate(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN));
			poorddetail.setXlineamt(poorddetail.getXqtyord().multiply(poorddetail.getXrate()));
			model.addAttribute("poorddetail", poorddetail);
		} else {
			PoordDetail poorddetail = poordService.findPoorddetailByXportNumAndXrow(xpornum, Integer.parseInt(xrow));
			if(poorddetail == null) {
				poorddetail = new PoordDetail();
				poorddetail.setXpornum(xpornum);
				poorddetail.setXqtyord(BigDecimal.ONE.setScale(2, RoundingMode.DOWN));
				poorddetail.setXrate(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN));
				poorddetail.setXlineamt(poorddetail.getXqtyord().multiply(poorddetail.getXrate()));
			}
			model.addAttribute("poorddetail", poorddetail);
		}

		return "pages/purchasing/poord/poorddetailmodal::poorddetailmodal";
	}

	@PostMapping("/poorddetail/save")
	public @ResponseBody Map<String, Object> savePoorddetail(PoordDetail poordDetail){
		if(poordDetail == null || StringUtils.isBlank(poordDetail.getXpornum())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		// Check item already exist in detail list
		if(poordDetail.getXrow() == 0 && poordService.findPoorddetailByXpornumAndXitem(poordDetail.getXpornum(), poordDetail.getXitem()) != null) {
			responseHelper.setErrorStatusAndMessage("Item already added into detail list. Please add another one or update existing");
			return responseHelper.getResponse();
		}

		// modify line amount
		poordDetail.setXlineamt(poordDetail.getXqtyord().multiply(poordDetail.getXrate().setScale(2, RoundingMode.DOWN)));

		// if existing
		PoordDetail existDetail = poordService.findPoorddetailByXportNumAndXrow(poordDetail.getXpornum(), poordDetail.getXrow());
		if(existDetail != null) {
			BeanUtils.copyProperties(poordDetail, existDetail, "xpornum", "xrow");
			long count = poordService.updateDetail(existDetail);
			if(count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			responseHelper.setRedirectUrl("/purchasing/poord/" +  poordDetail.getXpornum());
			responseHelper.setSuccessStatusAndMessage("Order detail updated successfully");
			return responseHelper.getResponse();
		}

		// if new detail
		long count = poordService.saveDetail(poordDetail);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		responseHelper.setRedirectUrl("/purchasing/poord/" +  poordDetail.getXpornum());
		responseHelper.setSuccessStatusAndMessage("Order detail saved successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/poorddetail/{xpornum}")
	public String reloadPoordDetailTabble(@PathVariable String xpornum, Model model) {
		List<PoordDetail> detailList = poordService.findPoorddetailByXpornum(xpornum);
		model.addAttribute("poorddetailsList", detailList);
		PoordHeader header = new PoordHeader();
		header.setXpornum(xpornum);
		model.addAttribute("poordheader", header);
		return "pages/purchasing/poord/poord::poorddetailtable";
	}

	@PostMapping("{xpornum}/poorddetail/{xrow}/delete")
	public @ResponseBody Map<String, Object> deletePoordDetail(@PathVariable String xpornum, @PathVariable String xrow, Model model) {
		PoordDetail pd = poordService.findPoorddetailByXportNumAndXrow(xpornum, Integer.parseInt(xrow));
		if(pd == null) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		long count = poordService.deleteDetail(pd);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		responseHelper.setRedirectUrl("/purchasing/poord/" +  xpornum);
		return responseHelper.getResponse();
	}
	
	
	@GetMapping("/creategrn/{xpornum}")
	public @ResponseBody Map<String, Object> creategrn(@PathVariable String xpornum){
		if(StringUtils.isBlank(xpornum)) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		// Validate

		// Get PoordHeader record by Xpornum
		PoordHeader poordHeader = poordService.findPoordHeaderByXpornum(xpornum);
		//Get PO items to copy them in GRN.
		List<PoordDetail> poordDetailList = poordService.findPoorddetailByXpornum(xpornum);
		if(poordDetailList.size() == 0) {
			responseHelper.setErrorStatusAndMessage("Please add items to create grn!");
			return responseHelper.getResponse();
		}
		if(poordHeader != null) {
			PogrnHeader pogrnHeader = new PogrnHeader();
			BeanUtils.copyProperties(poordHeader, pogrnHeader, "xdate", "xtype", "xtrngrn", "xnote");
			pogrnHeader.setXpornum(xpornum);
			pogrnHeader.setXstatusgrn("Open");
			pogrnHeader.setXdate(new Date());
			pogrnHeader.setXtype(TransactionCodeType.GRN_NUMBER.getCode());
			pogrnHeader.setXtrngrn(TransactionCodeType.GRN_NUMBER.getdefaultCode());
			
			long count = pogrnService.save(pogrnHeader);
			if(count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			
			pogrnHeader = pogrnService.findPogrnHeaderByXpornum(xpornum);
			
			PogrnDetail pogrnDetail;
			for(int i=0; i< poordDetailList.size(); i++) {
				pogrnDetail = new PogrnDetail();
				//Copying PO items to GRN items.
				BeanUtils.copyProperties(poordDetailList.get(i), pogrnDetail, "xrow", "xnote");
				pogrnDetail.setXgrnnum(pogrnHeader.getXgrnnum());
				pogrnDetail.setXqtygrn(poordDetailList.get(i).getXqtyord());
				long nCount = pogrnService.saveDetail(pogrnDetail);
				
				// Update Inventory				
				if(nCount == 0) {
					responseHelper.setStatus(ResponseStatus.ERROR);
					return responseHelper.getResponse();
				}				
			}
			
			//Update PoordHeader Status
			poordHeader.setXstatuspor("Confirmed");
			long pCount = poordService.update(poordHeader);
			if(pCount == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}			
			 
			responseHelper.setSuccessStatusAndMessage("GRN created successfully");
			responseHelper.setRedirectUrl("/purchasing/pogrn/" + pogrnHeader.getXgrnnum());
			return responseHelper.getResponse();
		}	
		responseHelper.setStatus(ResponseStatus.ERROR);
		return responseHelper.getResponse();
	}
	

	@GetMapping("/print/{xpornum}")
	public ResponseEntity<byte[]> printDeliveryOrderWithDetails(@PathVariable String xpornum) {
		String message;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "html"));
		headers.add("X-Content-Type-Options", "nosniff");

		PoordHeader oh = poordService.findPoordHeaderByXpornum(xpornum);
		
		
		if (oh == null) {
			message = "Purchase Order not found to print";
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Cacus cacus = cacusService.findByXcus(oh.getXcus());
		
		//SalesOrderChalanReport orderReport = new SalesOrderChalanReport();

		PurchaseReport report = new PurchaseReport();
		report.setBusinessName(sessionManager.getZbusiness().getZorg());
		report.setBusinessAddress(sessionManager.getZbusiness().getXmadd());
		report.setReportName("Purchase Order");
		report.setFromDate(SDF.format(oh.getXdate()));
		report.setToDate(SDF.format(oh.getXdate()));
		report.setPrintDate(SDF.format(new Date()));
		//report.setCopyrightText("ASL");

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setOrderNumber(oh.getXpornum());
		purchaseOrder.setSupplier(cacus.getXcus());
		purchaseOrder.setSupplierName(cacus.getXorg());
		purchaseOrder.setSupplierAddress(cacus.getXmadd());
		purchaseOrder.setWarehouse(oh.getXwh());
		purchaseOrder.setDate(SDF.format(oh.getXdate()));

		List<PoordDetail> items = poordService.findPoorddetailByXpornum(oh.getXpornum());
		if (items != null && !items.isEmpty()) {
			items.parallelStream().forEach(it -> {
				ItemDetails item = new ItemDetails();
				item.setItemCode(it.getXitem());
				item.setItemName(it.getXitemdesc());
				item.setItemQty(it.getXqtyord().toString());
				item.setItemUnit(it.getXunitpur());
				item.setItemCategory(it.getXcatitem());
				item.setItemGroup(it.getXgitem());

				purchaseOrder.getItems().add(item);
			});
		}

		report.getPurchaseorders().add(purchaseOrder);

		byte[] byt = getPDFByte(report, "purchasereport.xsl");
		if (byt == null) {
			message = "Can't print report for Purchase Order : " + xpornum;
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		headers.setContentType(new MediaType("application", "pdf"));
		return new ResponseEntity<>(byt, headers, HttpStatus.OK);
	}
}
