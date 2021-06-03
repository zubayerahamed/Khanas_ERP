package com.asl.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.entity.Caitem;
import com.asl.entity.Oporddetail;
import com.asl.entity.Opordheader;
import com.asl.entity.Vatait;
import com.asl.entity.Zbusiness;
import com.asl.enums.ResponseStatus;
import com.asl.enums.TransactionCodeType;
import com.asl.service.CaitemService;
import com.asl.service.OpordService;
import com.asl.service.VataitService;
import com.asl.service.XtrnService;
import com.asl.service.ZbusinessService;

@Controller
@RequestMapping("/salesninvoice/opord")
public class SalesOrderController extends ASLAbstractController {

	@Autowired private OpordService opordService;
	@Autowired private CaitemService caitemService;
	@Autowired private XtrnService xtrnService;
	@Autowired private VataitService vataitService;
	@Autowired private ZbusinessService businessService;

	@GetMapping
	public String loadSalesOrderPage(Model model) {

		model.addAttribute("opordheader", getDefaultOpordHeader());

		List<Opordheader> allHeaders = opordService.findAllOpordHeaderByXtypetrnAndXtrn(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode());
		allHeaders.stream().forEach(h -> {
			Zbusiness zb = businessService.findBById(h.getXcus());
			if(zb != null) {
				h.setBranchname(zb.getZorg());
			}
		});
		model.addAttribute("allOpordHeader", allHeaders);

		model.addAttribute("opordprefix", xtrnService.findByXtypetrn(TransactionCodeType.SALES_ORDER.getCode(), Boolean.TRUE));
		model.addAttribute("vataitList", vataitService.getAllVatait());

		return "pages/salesninvoice/salesorder/salesorder";
	}

	@GetMapping("/{xordernum}")
	public String loadSalesOrderPage(@PathVariable String xordernum, Model model) {
		Opordheader data = opordService.findOpordHeaderByXordernum(xordernum);
		if (data == null) return "redirect:/salesninvoice/opord";

		model.addAttribute("opordheader", data);
		model.addAttribute("allOpordHeader", opordService.findAllOpordHeaderByXtypetrnAndXtrn(TransactionCodeType.SALES_ORDER.getCode(), TransactionCodeType.SALES_ORDER.getdefaultCode()));

		model.addAttribute("opordprefix", xtrnService.findByXtypetrn(TransactionCodeType.SALES_ORDER.getCode(), Boolean.TRUE));
		model.addAttribute("vataitList", vataitService.getAllVatait());

		model.addAttribute("opordDetailsList", opordService.findOporddetailByXordernum(xordernum));

		return "pages/salesninvoice/salesorder/salesorder";
	}

	private Opordheader getDefaultOpordHeader() {
		Opordheader opordheader = new Opordheader();
		opordheader.setXtypetrn(TransactionCodeType.SALES_ORDER.getCode());
		opordheader.setXtrn(TransactionCodeType.SALES_ORDER.getdefaultCode());
		opordheader.setXstatus("Open");

		opordheader.setXtotamt(BigDecimal.ZERO);
		opordheader.setXgrandtot(BigDecimal.ZERO);
		opordheader.setXvatait("No Vat");
		opordheader.setXvatamt(BigDecimal.ZERO);
		opordheader.setXaitamt(BigDecimal.ZERO);
		opordheader.setXdiscamt(BigDecimal.ZERO);

		return opordheader;
	}

	@PostMapping("/save")
	public @ResponseBody Map<String, Object> save(Opordheader opordHeader, BindingResult bindingResult) {
		if (opordHeader == null || StringUtils.isBlank(opordHeader.getXtypetrn())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		// Validate
//		if (StringUtils.isBlank(opordHeader.getXcus())) {
//			responseHelper.setErrorStatusAndMessage("Please select a customer to create Sales Order");
//			return responseHelper.getResponse();
//		}

		Vatait vatait = vataitService.findVataitByXvatait(opordHeader.getXvatait());
		if(opordHeader.getXtotamt() == null) opordHeader.setXtotamt(BigDecimal.ZERO);
		if(StringUtils.isNotBlank(opordHeader.getXvatait()) && !"No Vat".equalsIgnoreCase(opordHeader.getXvatait()) && vatait != null) {
			if(opordHeader.getXvatamt() == null) opordHeader.setXvatamt((opordHeader.getXtotamt().multiply(vatait.getXvat())).divide(BigDecimal.valueOf(100)));
			if(opordHeader.getXaitamt() == null) opordHeader.setXaitamt((opordHeader.getXtotamt().multiply(vatait.getXait())).divide(BigDecimal.valueOf(100)));
		} else {
			if(opordHeader.getXvatamt() == null) opordHeader.setXvatamt(BigDecimal.ZERO);
			if(opordHeader.getXaitamt() == null) opordHeader.setXaitamt(BigDecimal.ZERO);
		}
		if(opordHeader.getXdiscamt() == null) opordHeader.setXdiscamt(BigDecimal.ZERO);
		BigDecimal grandTotal = (opordHeader.getXtotamt().add(opordHeader.getXvatamt()).add(opordHeader.getXaitamt())).subtract(opordHeader.getXdiscamt());
		opordHeader.setXgrandtot(grandTotal);


		// if existing record
		Opordheader existOpordHeader = opordService.findOpordHeaderByXordernum(opordHeader.getXordernum());
		if (existOpordHeader != null) {
			BeanUtils.copyProperties(opordHeader, existOpordHeader, "xordernum", "xtypetrn", "xtrn");
			long count = opordService.updateOpordHeader(existOpordHeader);
			if (count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			responseHelper.setSuccessStatusAndMessage("Sales Order updated successfully");
			responseHelper.setRedirectUrl("/salesninvoice/opord/" + opordHeader.getXordernum());
			return responseHelper.getResponse();
		}

		// If new
		long count = opordService.saveOpordHeader(opordHeader);
		if (count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		responseHelper.setSuccessStatusAndMessage("Sales Order created successfully");
		responseHelper.setRedirectUrl("/salesninvoice/opord/" + opordHeader.getXordernum());
		return responseHelper.getResponse();
	}

	@PostMapping("/archive/{xordernum}")
	public @ResponseBody Map<String, Object> archive(@PathVariable String xordernum){
		return doArchiveOrRestore(xordernum, true);
	}

	public Map<String, Object> doArchiveOrRestore(String xordernum, boolean archive){
		Opordheader opordHeader = opordService.findOpordHeaderByXordernum(xordernum);
		if(opordHeader == null) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		// archive all details first
		if(archive && !opordService.findOporddetailByXordernum(xordernum).isEmpty()) {
			long count2 = opordService.archiveAllOporddetailByXordernum(xordernum);
			if(count2 == 0) {
				responseHelper.setErrorStatusAndMessage("Can't archive details");
				return responseHelper.getResponse();
			}
		}

		// update opordheader zactive
		opordHeader.setZactive(archive ? Boolean.FALSE : Boolean.TRUE);
		long count = opordService.updateOpordHeader(opordHeader);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Salees order archived successfully");
		responseHelper.setRedirectUrl("/salesninvoice/opord/");
		return responseHelper.getResponse();
	}

	@GetMapping("/oporddetail/{xordernum}")
	public String reloadOpordDetailTable(@PathVariable String xordernum, Model model) {
		model.addAttribute("opordDetailsList", opordService.findOporddetailByXordernum(xordernum));
		model.addAttribute("opordheader", opordService.findOpordHeaderByXordernum(xordernum));
		return "pages/salesninvoice/salesorder/salesorder::oporddetailtable";
	}

	@GetMapping("/opordheaderform/{xordernum}")
	public String reloadOpdoheaderform(@PathVariable String xordernum, Model model) {
		Opordheader data = opordService.findOpordHeaderByXordernum(xordernum);
		if (data == null) return "redirect:/salesninvoice/opord";

		model.addAttribute("opordheader", data);
		model.addAttribute("opordprefix", xtrnService.findByXtypetrn(TransactionCodeType.SALES_ORDER.getCode(), Boolean.TRUE));
		model.addAttribute("vataitList", vataitService.getAllVatait());

		return "pages/salesninvoice/salesorder/salesorder::opordheaderform";
	}

	@GetMapping("/{xordernum}/oporddetail/{xrow}/show")
	public String openOpordDetailModal(@PathVariable String xordernum, @PathVariable String xrow, Model model) {

		if ("new".equalsIgnoreCase(xrow)) {
			Oporddetail oporddetail = new Oporddetail();
			oporddetail.setXordernum(xordernum);
			oporddetail.setXrate(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN));
			oporddetail.setXqtyord(BigDecimal.ONE.setScale(2, RoundingMode.DOWN));
			model.addAttribute("oporddetail", oporddetail);
		} else {
			Oporddetail oporddetail = opordService.findOporddetailByXordernumAndXrow(xordernum, Integer.parseInt(xrow));
			if (oporddetail == null) {
				oporddetail = new Oporddetail();
				oporddetail.setXordernum(xordernum);
				oporddetail.setXrate(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN));
				oporddetail.setXqtyord(BigDecimal.ONE.setScale(2, RoundingMode.DOWN));
			}
			model.addAttribute("oporddetail", oporddetail);
		}
		return "pages/salesninvoice/salesorder/oporddetailmodal::oporddetailmodal";
	}

	@PostMapping("/oporddetail/save")
	public @ResponseBody Map<String, Object> saveOporddetail(Oporddetail opordDetail) {
		if (opordDetail == null || StringUtils.isBlank(opordDetail.getXordernum())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		if(StringUtils.isBlank(opordDetail.getXitem())) {
			responseHelper.setErrorStatusAndMessage("Item not selected! Please select an item");
			return responseHelper.getResponse();
		}

		Caitem caitem = caitemService.findByXitem(opordDetail.getXitem());
		if(caitem == null) {
			responseHelper.setErrorStatusAndMessage("Item not found in the system");
			return responseHelper.getResponse();
		}

		if(opordDetail.getXqtyord() == null || opordDetail.getXqtyord().compareTo(BigDecimal.ZERO) == -1) {
			responseHelper.setErrorStatusAndMessage("Item quantity can't be less then zero");
			return responseHelper.getResponse();
		}

		// Check item already exist in detail list
		if (opordDetail.getXrow() == 0 && opordService.findOporddetailByXordernumAndXitem(opordDetail.getXordernum(), opordDetail.getXitem()) != null) {
			responseHelper.setErrorStatusAndMessage("Item already added into detail list. Please add another one or update existing");
			return responseHelper.getResponse();
		}

		// modify line amount
		opordDetail.setXdesc(caitem.getXdesc());
		opordDetail.setXcatitem(caitem.getXcatitem());
		opordDetail.setXgitem(caitem.getXgitem());
		opordDetail.setXlineamt(opordDetail.getXqtyord().multiply(opordDetail.getXrate()).setScale(2, RoundingMode.DOWN));

		// if existing
		Oporddetail existDetail = opordService.findOporddetailByXordernumAndXrow(opordDetail.getXordernum(), opordDetail.getXrow());
		if (existDetail != null) {
			BeanUtils.copyProperties(opordDetail, existDetail, "xordernum", "xrow");
			long count = opordService.updateOpordDetail(existDetail);
			if (count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			responseHelper.setReloadSectionIdWithUrl("oporddetailtable", "/salesninvoice/opord/oporddetail/" + opordDetail.getXordernum());
			responseHelper.setSecondReloadSectionIdWithUrl("opordheaderform", "/salesninvoice/opord/opordheaderform/" + opordDetail.getXordernum());
			responseHelper.setSuccessStatusAndMessage("Sales Order item updated successfully");
			return responseHelper.getResponse();
		}

		// if new detail
		long count = opordService.saveOpordDetail(opordDetail);
		if (count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		responseHelper.setReloadSectionIdWithUrl("oporddetailtable", "/salesninvoice/opord/oporddetail/" + opordDetail.getXordernum());
		responseHelper.setSecondReloadSectionIdWithUrl("opordheaderform", "/salesninvoice/opord/opordheaderform/" + opordDetail.getXordernum());
		responseHelper.setSuccessStatusAndMessage("Sales Order item saved successfully");
		return responseHelper.getResponse();
	}

	@PostMapping("{xordernum}/oporddetail/{xrow}/delete")
	public @ResponseBody Map<String, Object> deleteOpdoDetail(@PathVariable String xordernum, @PathVariable String xrow, Model model) {
		Oporddetail pd = opordService.findOporddetailByXordernumAndXrow(xordernum, Integer.parseInt(xrow));
		if (pd == null) {
			responseHelper.setErrorStatusAndMessage("Detail item can't found to do delete");
			return responseHelper.getResponse();
		}

		long count = opordService.deleteOpordDetail(pd);
		if (count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		responseHelper.setReloadSectionIdWithUrl("oporddetailtable", "/salesninvoice/opord/oporddetail/" + xordernum);
		responseHelper.setSecondReloadSectionIdWithUrl("opordheaderform", "/salesninvoice/opord/opordheaderform/" + xordernum);
		return responseHelper.getResponse();
	}

	@GetMapping("/itemdetail/{xitem}")
	public @ResponseBody Caitem getCentralItemDetail(@PathVariable String xitem){
		return caitemService.findByXitem(xitem);
	}

}
