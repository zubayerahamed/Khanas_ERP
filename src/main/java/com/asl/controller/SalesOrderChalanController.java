package com.asl.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.entity.Caitem;
import com.asl.entity.Immofgdetail;
import com.asl.entity.Oporddetail;
import com.asl.entity.Opordheader;
import com.asl.entity.Zbusiness;
import com.asl.enums.ResponseStatus;
import com.asl.enums.TransactionCodeType;
import com.asl.model.report.AllSalesOrderChalanReport;
import com.asl.model.report.ItemDetails;
import com.asl.model.report.SalesOrderChalan;
import com.asl.service.CaitemService;
import com.asl.service.ImmofgdetailService;
import com.asl.service.MoService;
import com.asl.service.OpdoService;
import com.asl.service.OpordService;
import com.asl.service.XtrnService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zubayer Ahamed
 * @since Mar 9, 2021
 */
@Slf4j
@Controller
@RequestMapping("/salesninvoice/salesorderchalan")
public class SalesOrderChalanController extends ASLAbstractController {

	@Autowired private OpordService opordService;
	@Autowired private XtrnService xtrnService;
	@Autowired private ImmofgdetailService immofgdetailService;
	@Autowired private MoService moService;
	@Autowired private OpdoService opdoService;
	@Autowired private CaitemService caitemService;

	@GetMapping
	public String loadSalesOrderChalanPage(Model model) {
		model.addAttribute("productioncompleted", false);
		model.addAttribute("salesorderchalan", getDefaultOpordheader());
		model.addAttribute("salesorderchalanprefix", xtrnService.findByXtypetrnAndXtrn(TransactionCodeType.CHALAN_NUMBER.getCode(), TransactionCodeType.CHALAN_NUMBER.getdefaultCode(), Boolean.TRUE));
		model.addAttribute("salesorderchalanList", opordService.findAllOpordHeaderByXtypetrnAndXtrn(TransactionCodeType.CHALAN_NUMBER.getCode(), TransactionCodeType.CHALAN_NUMBER.getdefaultCode()));
		return "pages/salesninvoice/salesorderchalan/salesorderchalan";
	}

	@GetMapping("/{xordernum}")
	public String loadSalesOrderChalanPage(@PathVariable String xordernum, Model model) {
		Opordheader oh = opordService.findOpordHeaderByXordernum(xordernum);
		if(oh == null) return "redirect:/salesninvoice/salesorderchalan";

		model.addAttribute("productioncompleted", moService.isProductionProcessCompleted(oh.getXordernum()));
		model.addAttribute("salesorderchalan", oh);
		model.addAttribute("salesorderchalanprefix", xtrnService.findByXtypetrnAndXtrn(TransactionCodeType.CHALAN_NUMBER.getCode(), TransactionCodeType.CHALAN_NUMBER.getdefaultCode(), Boolean.TRUE));
		model.addAttribute("salesorderchalanList", opordService.findAllOpordHeaderByXtypetrnAndXtrn(TransactionCodeType.CHALAN_NUMBER.getCode(), TransactionCodeType.CHALAN_NUMBER.getdefaultCode()));

		List<Opordheader> allOpenAndConfirmesSalesOrders = new ArrayList<>();
		if("Open".equalsIgnoreCase(oh.getXstatus())) allOpenAndConfirmesSalesOrders.addAll(opordService.findAllSalesOrder(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode(), "Open", new Date()));
		allOpenAndConfirmesSalesOrders.addAll(opordService.findAllSalesOrderByChalan(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode(), xordernum));
		model.addAttribute("opensalesorders", allOpenAndConfirmesSalesOrders);
		model.addAttribute("chalandetails", opordService.findOporddetailByXordernum(xordernum));
		return "pages/salesninvoice/salesorderchalan/salesorderchalan";
	}

	private Opordheader getDefaultOpordheader() {
		Opordheader oh = new Opordheader();
		oh.setXtypetrn(TransactionCodeType.CHALAN_NUMBER.getCode());
		oh.setXdate(new Date());
		oh.setXstatus("Open");
		return oh;
	}

	@PostMapping("/save")
	public @ResponseBody Map<String, Object> save(Opordheader opordheader, BindingResult bindingResult, Model model){
		if(opordheader == null || StringUtils.isBlank(opordheader.getXtypetrn())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		// if existing
		Opordheader existOh = opordService.findOpordHeaderByXordernum(opordheader.getXordernum());
		if(existOh != null) {
			BeanUtils.copyProperties(opordheader, existOh, "xdate", "xstatus");
			long count = opordService.updateOpordHeader(existOh);
			if(count == 0) {
				responseHelper.setErrorStatusAndMessage("Chalan not updated");
				return responseHelper.getResponse();
			}
			responseHelper.setRedirectUrl("/salesninvoice/salesorderchalan/" + existOh.getXordernum());
			responseHelper.setSuccessStatusAndMessage("Chalan updated successfully");
			return responseHelper.getResponse();
		}

		// if new
		opordheader.setXstatus("Open");
		opordheader.setXdate(new Date());
		long count = opordService.saveOpordHeader(opordheader);
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Chalan not created");
			return responseHelper.getResponse();
		}

		responseHelper.setRedirectUrl("/salesninvoice/salesorderchalan");
		responseHelper.setSuccessStatusAndMessage("Chalan created successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/opensalesorder/query")
	public String reloadTableWithData(@RequestParam String xordernum, @RequestParam String date, Model model) throws ParseException {
		model.addAttribute("salesorderchalan", opordService.findOpordHeaderByXordernum(xordernum));
		List<Opordheader> allOpenAndConfirmesSalesOrders = new ArrayList<>();
		allOpenAndConfirmesSalesOrders.addAll(opordService.findAllSalesOrder(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode(), "Open", SDF.parse(date)));
		allOpenAndConfirmesSalesOrders.addAll(opordService.findAllSalesOrderByChalan(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode(), xordernum));
		model.addAttribute("opensalesorders", allOpenAndConfirmesSalesOrders);
		return "pages/salesninvoice/salesorderchalan/salesorderchalan::opensalesorderstable";
	}

	@PostMapping("/opensalesorder/query")
	public @ResponseBody Map<String, Object> queryForrequistionDetails(String xordernum, Date xdate, Model model){
		responseHelper.setReloadSectionIdWithUrl("opensalesorderstable", "/salesninvoice/salesorderchalan/opensalesorder/query?xordernum="+ xordernum +"&date=" + SDF.format(xdate));
		responseHelper.setStatus(ResponseStatus.SUCCESS);
		return responseHelper.getResponse();
	}

	@GetMapping("/ordreqdetails/{xordernum}/show")
	public String displayItemDetailsOfOrderRequistion(@PathVariable String xordernum, Model model) {
		model.addAttribute("oporddetailsList", opordService.findOporddetailByXordernum(xordernum));
		return "pages/salesninvoice/salesorderchalan/salesorderdetailmodal::salesorderdetailmodal";
	}

	@PostMapping("/salesorderconfirm/{chalan}/{xordernum}")
	public @ResponseBody Map<String, Object> confirmSalesOrderAndCreateChalanDetail(@PathVariable String chalan, @PathVariable String xordernum, Model model){
		Opordheader oh = opordService.findOpordHeaderByXordernum(xordernum);
		if(oh == null) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		if(StringUtils.isNotBlank(oh.getXchalanref())) {
			responseHelper.setErrorStatusAndMessage("Sales order already added to chalan : " + oh.getXchalanref() + " . Please reload this page again");
			return responseHelper.getResponse();
		}

		List<Oporddetail> details = opordService.findOporddetailByXordernum(xordernum);
		if(details == null || details.isEmpty()) {
			responseHelper.setErrorStatusAndMessage("This " + xordernum + " Sales Order has no item to add this chalan");
			return responseHelper.getResponse();
		}

		// create or update chalan detail first
		for(Oporddetail pd : details) {
			// if not production item, then don't add it to chalan
			Caitem caitem = caitemService.findByXitem(pd.getXitem());
			if(caitem == null || !caitem.isXproditem()) continue;

			// check chalan detail already exist using item
			Oporddetail existChalanDetail = opordService.findOporddetailByXordernumAndXitem(chalan, pd.getXitem());
			if(existChalanDetail != null) {  // update existing with qty
				existChalanDetail.setXqtyord(existChalanDetail.getXqtyord().add(pd.getXqtyord()));
				long countChalanDetail = opordService.updateOpordDetail(existChalanDetail);
				if(countChalanDetail == 0) {
					responseHelper.setErrorStatusAndMessage("Can't update chalan detail");
					return responseHelper.getResponse();
				}
			} else {  // create new detail
				Oporddetail chalanDetail = new Oporddetail();
				chalanDetail.setXordernum(chalan);
				chalanDetail.setXitem(pd.getXitem());
				chalanDetail.setXunit(pd.getXunit());
				chalanDetail.setXqtyord(pd.getXqtyord());
				chalanDetail.setXrate(pd.getXrate());
				long countChalanDetail = opordService.saveOpordDetail(chalanDetail);
				if(countChalanDetail == 0) {
					responseHelper.setErrorStatusAndMessage("Can't create chalan detail");
					return responseHelper.getResponse();
				}
			}
		}

		// now update sales order with chalan reference
		oh.setXchalanref(chalan);
		oh.setXstatus("Confirmed");
		long count = opordService.updateOpordHeader(oh);
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't Update Sales Order");
			return responseHelper.getResponse();
		}

		responseHelper.setReloadSectionIdWithUrl("opensalesorderstable", "/salesninvoice/salesorderchalan/opensalesorder/query?xordernum="+ chalan +"&date=" + SDF.format(oh.getXdate()));
		responseHelper.setSecondReloadSectionIdWithUrl("salesorderchalandetailtable", "/salesninvoice/salesorderchalan/chalandetail/" + chalan);
		responseHelper.setSuccessStatusAndMessage("Sales order confirmed");
		return responseHelper.getResponse();
	}

	@PostMapping("/salesorderrevoke/{chalan}/{xordernum}")
	public @ResponseBody Map<String, Object> revokeSalesOrder(@PathVariable String chalan, @PathVariable String xordernum, Model model){
		Opordheader oh = opordService.findOpordHeaderByXordernum(xordernum);
		if(oh == null) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		List<Oporddetail> details = opordService.findOporddetailByXordernum(xordernum);
		if(details == null || details.isEmpty()) {
			responseHelper.setErrorStatusAndMessage("This " + xordernum + " Sales Order has no item to remove from chalan");
			return responseHelper.getResponse();
		}

		// create or update chalan detail first
		for(Oporddetail pd : details) {
			// check chalan detail already exist using item
			Oporddetail existChalanDetail = opordService.findOporddetailByXordernumAndXitem(chalan, pd.getXitem());
			if(existChalanDetail == null) continue;

			// update existing with qty
			existChalanDetail.setXqtyord(existChalanDetail.getXqtyord().subtract(pd.getXqtyord()));
			long countChalanDetail = 0;
			if(BigDecimal.ZERO.equals(existChalanDetail.getXqtyord())) {
				countChalanDetail = opordService.deleteOpordDetail(existChalanDetail);
			} else {
				countChalanDetail = opordService.updateOpordDetail(existChalanDetail);
			}
			if(countChalanDetail == 0) {
				responseHelper.setErrorStatusAndMessage("Can't update chalan detail");
				return responseHelper.getResponse();
			}
		}

		// now update sales order with chalan reference
		oh.setXchalanref(null);
		oh.setXstatus("Open");
		long count = opordService.updateOpordHeader(oh);
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't Update Sales Order");
			return responseHelper.getResponse();
		}

		responseHelper.setReloadSectionIdWithUrl("opensalesorderstable", "/salesninvoice/salesorderchalan/opensalesorder/query?xordernum="+ chalan +"&date=" + SDF.format(oh.getXdate()));
		responseHelper.setSecondReloadSectionIdWithUrl("salesorderchalandetailtable", "/salesninvoice/salesorderchalan/chalandetail/" + chalan);
		responseHelper.setSuccessStatusAndMessage("Sales order revoked");
		return responseHelper.getResponse();
	}

	@GetMapping("/chalandetail/{xordernum}")
	public String reloadChalanDetailSection(@PathVariable String xordernum, Model model) {
		model.addAttribute("salesorderchalan", opordService.findOpordHeaderByXordernum(xordernum));
		model.addAttribute("chalandetails", opordService.findOporddetailByXordernum(xordernum));
		return "pages/salesninvoice/salesorderchalan/salesorderchalan::salesorderchalandetailtable";
	}

	@PostMapping("/lockchalan/{xordernum}")
	public @ResponseBody Map<String, Object> lockChalan(@PathVariable String xordernum, Model model){
		Opordheader oh = opordService.findOpordHeaderByXordernum(xordernum);
		if(oh == null) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		// transfer all chalan deails to immofgdetail
		List<Oporddetail> chalandetails = opordService.findOporddetailByXordernum(oh.getXordernum());
		for(Oporddetail c : chalandetails) {
			Immofgdetail id = new Immofgdetail();
			id.setXtornum(c.getXordernum());
			id.setXrow(c.getXrow());
			id.setXunit(c.getXunit());
			id.setXitem(c.getXitem());
			id.setXqtyord(c.getXqtyord());
			id.setXnote(c.getXlong());
			long count = immofgdetailService.save(id);
			if(count == 0) {
				log.error("ERROR is : {}", "Can't insert chaland details to Immofgdetail table for chalan " + c.getXordernum());
			}
		}

		// now lock chalan
		oh.setXstatus("Confirmed");
		long count = opordService.updateOpordHeader(oh);
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't lock Chalan");
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Chalan locked successfully");
		responseHelper.setRedirectUrl("/salesninvoice/salesorderchalan/" + xordernum);
		return responseHelper.getResponse();
	}

	@PostMapping("/createinvoice/{xordernum}")
	public @ResponseBody Map<String, Object> createInvoice(@PathVariable String xordernum, Model model){
		long count = opdoService.createSalesFromChalan(xordernum);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		responseHelper.setRedirectUrl("/salesninvoice/salesorderchalan/" + xordernum);
		responseHelper.setSuccessStatusAndMessage("Invoice created successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/print/{xordernum}")
	public ResponseEntity<byte[]> printChalan(@PathVariable String xordernum) {
		String message;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "html"));
		headers.add("X-Content-Type-Options", "nosniff");

		Opordheader oh = opordService.findOpordHeaderByXordernum(xordernum);
		if(oh == null) {
			message = "Chalan not found to do print";
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Oporddetail> details = opordService.findOporddetailByXordernum(xordernum);
		if(details == null || details.isEmpty()) {
			message = "Chalan Details is empty";
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("E, dd-MMM-yyyy");

		Zbusiness zb = sessionManager.getZbusiness();

		AllSalesOrderChalanReport report = new AllSalesOrderChalanReport();
		report.setBusinessName(zb.getZorg());
		report.setBusinessAddress(zb.getXmadd());
		report.setReportName("Sales Order Chalan Item Details Report : " + oh.getXordernum());
		report.setFromDate(sdf.format(oh.getXdate()));
		report.setToDate(sdf.format(oh.getXdate()));
		report.setPrintDate(sdf.format(new Date()));

		List<SalesOrderChalan> chalans = new ArrayList<>();
		SalesOrderChalan chalan = new SalesOrderChalan();
		chalan.setChalanName(oh.getXordernum());
		chalan.setChalanDate(sdf.format(oh.getXdate()));
		chalan.setStatus(oh.getXstatus());
		chalans.add(chalan);

		List<ItemDetails> items = new ArrayList<>();
		details.stream().forEach(d -> {
			ItemDetails id = new ItemDetails();
			id.setItemCode(d.getXitem());
			id.setItemName(d.getXdesc());
			id.setItemCategory(d.getXdesc());
			id.setItemQty(d.getXqtyord().toString());
			id.setItemUnit(d.getXunit());
			id.setItemCategory(d.getXcatitem());
			id.setItemGroup(d.getXgitem());
			items.add(id);
		});
		chalan.getItems().addAll(items);
		report.getChalans().addAll(chalans);

		byte[] byt = getPDFByte(report, "salesorderchalanitemdetailreport.xsl");
		if(byt == null) {
			message = "Can't generate pdf for chalan : " + xordernum;
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		headers.setContentType(new MediaType("application", "pdf"));
		return new ResponseEntity<>(byt, headers, HttpStatus.OK);
	}

	@GetMapping("/printsalesorders/{xordernum}")
	public ResponseEntity<byte[]> printChalanWithSalesOrderDetails(@PathVariable String xordernum) {
		String message;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("text", "html"));
		headers.add("X-Content-Type-Options", "nosniff");

		Opordheader oh = opordService.findOpordHeaderByXordernum(xordernum);
		if(oh == null) {
			message = "Chalan not found to do print";
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<Opordheader> salesOrders = opordService.findAllSalesOrderByChalan(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode(), xordernum);
		if(salesOrders == null || salesOrders.isEmpty()) {
			message = "Sales orders are not assigned with this chalan : " + xordernum;
			return new ResponseEntity<>(message.getBytes(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
		
		
		
		
		return null;
	}

}
