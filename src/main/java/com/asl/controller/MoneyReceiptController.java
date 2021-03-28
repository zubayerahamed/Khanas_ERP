package com.asl.controller;

import java.math.BigDecimal;
import java.util.Date;
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

import com.asl.entity.Arhed;
import com.asl.enums.CodeType;
import com.asl.enums.ResponseStatus;
import com.asl.enums.TransactionCodeType;
import com.asl.service.ArhedService;
import com.asl.service.VataitService;
import com.asl.service.XcodesService;
import com.asl.service.XtrnService;

@Controller
@RequestMapping("/salesninvoice/moneyreceipt")
public class MoneyReceiptController extends ASLAbstractController{
	
	@Autowired
	private ArhedService arhedService;
	@Autowired
	private XtrnService xtrnService;
	@Autowired
	private XcodesService xcodeService;	
	@Autowired
	private VataitService vataitService;
	
	@GetMapping
	public String loadMoneyReceiptPage(Model model) {
		
		model.addAttribute("arhed", getDefaultArhed());		
		model.addAttribute("allArhed", arhedService.getAllArhedByXtrn(TransactionCodeType.ACCOUNT_MR.getdefaultCode()));
		model.addAttribute("arhedprefix", xtrnService.findByXtypetrn(TransactionCodeType.ACCOUNT_MR.getCode()));
		model.addAttribute("warehouses", xcodeService.findByXtype(CodeType.WAREHOUSE.getCode()));
		model.addAttribute("paymenttypeList", xcodeService.findByXtype(CodeType.PAYMENT_TYPE.getCode()));
		model.addAttribute("chequeStatusList", xcodeService.findByXtype(CodeType.CHEQUE_STATUS.getCode()));
		model.addAttribute("bankstatusList", xcodeService.findByXtype(CodeType.BANK_STATUS.getCode()));
		model.addAttribute("jvstatusList", xcodeService.findByXtype(CodeType.JOURNAL_VOUCHER_STATUS.getCode()));
		model.addAttribute("vataitList", vataitService.getAllVatait());
		
		return "pages/salesninvoice/moneyreceipt/arhed";
	}
	
	@GetMapping("/{xvoucher}")
	public String loadMoneyReceiptPage(@PathVariable String xvoucher, Model model) {
		
		Arhed data = arhedService.findArhedByXvoucher(xvoucher);
		if(data == null) data = getDefaultArhed();

		model.addAttribute("arhed", data);
		model.addAttribute("allArhed", arhedService.getAllArhedByXtrn(TransactionCodeType.ACCOUNT_MR.getdefaultCode()));
		model.addAttribute("arhedprefix", xtrnService.findByXtypetrn(TransactionCodeType.ACCOUNT_MR.getCode()));
		model.addAttribute("warehouses", xcodeService.findByXtype(CodeType.WAREHOUSE.getCode()));
		model.addAttribute("paymenttypeLiist", xcodeService.findByXtype(CodeType.PAYMENT_TYPE.getCode()));
		model.addAttribute("chequeStatusList", xcodeService.findByXtype(CodeType.CHEQUE_STATUS.getCode()));
		model.addAttribute("bankstatusList", xcodeService.findByXtype(CodeType.BANK_STATUS.getCode()));
		model.addAttribute("jvstatusList", xcodeService.findByXtype(CodeType.JOURNAL_VOUCHER_STATUS.getCode()));
		model.addAttribute("vataitList", vataitService.getAllVatait());
		
		return "pages/salesninvoice/moneyreceipt/arhed";
	}
	
	private Arhed getDefaultArhed() {
		Arhed arhed = new Arhed();
		arhed.setXdate(new Date());
		arhed.setXprime(BigDecimal.ZERO);
		arhed.setXbalprime(BigDecimal.ZERO);
		arhed.setXvatamt(BigDecimal.ZERO);
		arhed.setXaitamt(BigDecimal.ZERO);
		arhed.setXdiscprime(BigDecimal.ZERO);
		arhed.setXbase(BigDecimal.ZERO);
		arhed.setXvatait("No Vat");
		arhed.setXpaymentterm("Credit");
		arhed.setXtypetrn("Money Receipts");
		arhed.setXstatus("Open");
		arhed.setXtype(TransactionCodeType.ACCOUNT_MR.getCode());
		arhed.setXtrnarhed(TransactionCodeType.ACCOUNT_MR.getCode());

		return arhed;
	}
	
	@PostMapping("/save")
	public @ResponseBody Map<String, Object> save(Arhed arhed, BindingResult bindingResult){
		if((arhed == null)) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		// Validate
		
		//Modify transaction codes for arhed
		arhed.setXsign(+1);
		arhed.setXtype(TransactionCodeType.ACCOUNT_PAYMENT.getCode());
		arhed.setXtrnarhed(TransactionCodeType.ACCOUNT_PAYMENT.getdefaultCode());

		// if existing record
		Arhed existArhed = arhedService.findArhedByXvoucher(arhed.getXvoucher());
		if(existArhed != null) {
			BeanUtils.copyProperties(arhed, existArhed, "xvoucher", "xtype", "xdate");
			long count = arhedService.update(existArhed);
			if(count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			responseHelper.setSuccessStatusAndMessage("Payment updated successfully");
			responseHelper.setRedirectUrl("/purchasing/supplierpayment/" + arhed.getXvoucher());
			return responseHelper.getResponse();
		}

		// If new
		long count = arhedService.save(arhed);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		responseHelper.setSuccessStatusAndMessage("Voucher created successfully");
		responseHelper.setRedirectUrl("/purchasing/supplierpayment/" + arhed.getXvoucher());
		return responseHelper.getResponse();
	}
	
	@PostMapping("/archive/{xvoucher}")
	public @ResponseBody Map<String, Object> archive(@PathVariable String xvoucher){
		return doArchiveOrRestore(xvoucher, true);
	}

	@PostMapping("/restore/{xvoucher}")
	public @ResponseBody Map<String, Object> restore(@PathVariable String xvoucher){
		return doArchiveOrRestore(xvoucher, false);
	}

	public Map<String, Object> doArchiveOrRestore(String xvoucher, boolean archive){
		
		return null;
	}

	
}