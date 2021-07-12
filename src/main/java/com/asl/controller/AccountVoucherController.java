package com.asl.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
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

import com.asl.entity.Acdef;
import com.asl.entity.Acdetail;
import com.asl.entity.Acheader;
import com.asl.enums.ResponseStatus;
import com.asl.enums.TransactionCodeType;
import com.asl.service.AcService;
import com.asl.service.AcdefService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/account/voucher")
public class AccountVoucherController extends ASLAbstractController{

	@Autowired private AcService acService;
	@Autowired private AcdefService acdefService;

	@GetMapping
	public String loadAccountVoucherPage(Model model) {

		model.addAttribute("acheader", getDefaultAcheader());
		model.addAttribute("voucherprefix", xtrnService.findByXtypetrn(TransactionCodeType.GL_VOUCHER.getCode(), Boolean.TRUE));
		model.addAttribute("vouchers", acService.getAllAcheader());

		return"pages/account/voucher/voucher";
	}

	private Acheader getDefaultAcheader() {
		Acheader acheader = new Acheader();
		acheader.setXtypetrn(TransactionCodeType.GL_VOUCHER.getCode());
		acheader.setXtrn(TransactionCodeType.GL_VOUCHER.getdefaultCode());
		acheader.setXstatusjv("Balanced");
		acheader.setXdate(new Date());
		acheader.setXwh("01");
		return acheader;
	}

	@GetMapping("/{xvoucher}")
	public String loadAccountVoucherPage(@PathVariable String xvoucher, Model model) {
		Acheader acheader = acService.findAcheaderByXvoucher(xvoucher);
		if(acheader == null) return "rdirect:/account/voucher";

		model.addAttribute("acheader", acheader);
		model.addAttribute("voucherprefix", xtrnService.findByXtypetrn(TransactionCodeType.GL_VOUCHER.getCode(), Boolean.TRUE));
		model.addAttribute("vouchers", acService.getAllAcheader());
		model.addAttribute("acdetails", acService.findAcdetailsByXvoucher(xvoucher));

		return"pages/account/voucher/voucher";
	}

	@PostMapping("/save")
	public @ResponseBody Map<String, Object> save(Acheader acheader, BindingResult bindingResult){
		// validate
		if(acheader.getXdate() == null) {
			responseHelper.setErrorStatusAndMessage("Voucher date required");
			return responseHelper.getResponse();
		}

		// set year and date based on xdate   
		Acdef acdef = acdefService.find();
		if(acdef == null) {
			responseHelper.setErrorStatusAndMessage("Account default is not set in this sytem");
			return responseHelper.getResponse();
		}

		// acdef cal
		Calendar acdefcal = Calendar.getInstance();
		acdefcal.set(Calendar.YEAR, Integer.parseInt(acdef.getXyear()));
		acdefcal.set(Calendar.DAY_OF_MONTH, acdef.getXper());
		acdefcal.set(Calendar.DATE, acdefcal.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("====> " + acdefcal.getTime());

		// voucher cal
		Calendar cal = Calendar.getInstance();
		cal.setTime(acheader.getXdate());

		System.out.println("Acdef : " + acdefcal.getTime());
		System.out.println("Selected : " + cal.getTime());
		
		SimpleDateFormat yformat = new SimpleDateFormat("yyyy");
		SimpleDateFormat mformat = new SimpleDateFormat("MM");
		if(acdefcal.get(Calendar.YEAR) - cal.get(Calendar.YEAR) == 0) {  // same year

			int mdif = acdefcal.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
			if(mdif == 0) {
				acheader.setXyear(cal.get(Calendar.YEAR));
				acheader.setXper(mdif + 1);
			} if(mdif < 0) {
				acheader.setXyear(cal.get(Calendar.YEAR));
				acheader.setXper((mdif * (-1)) + 1);
			} else if (mdif > 0) {
				acheader.setXyear(cal.get(Calendar.YEAR) - 1);
				acheader.setXper((12 - acdef.getXoffset()) + (acdef.getXoffset() - mdif) + 1);
			}

		} else if (acdefcal.get(Calendar.YEAR) - cal.get(Calendar.YEAR) < 0) {   // future year
			acheader.setXyear(cal.get(Calendar.YEAR) - 1);
			acheader.setXper((12 - acdef.getXoffset()) + cal.get(Calendar.MONTH) + 1);

//			int mdif = acdefcal.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
//			if(mdif == 0) {
//				acheader.setXyear(cal.get(Calendar.YEAR));
//				acheader.setXper(mdif + 1);
//			} if(mdif < 0) {
//				acheader.setXyear(cal.get(Calendar.YEAR));
//				acheader.setXper((mdif * (-1)) + 1);
//			} else if (mdif > 0) {
//				
//				acheader.setXper((12 - acdef.getXoffset()) + (acdef.getXoffset() - mdif) + 1);
//			}
			
		} else {   // prev year
			acheader.setXyear(cal.get(Calendar.YEAR));
			acheader.setXper((12 - acdef.getXoffset()) + cal.get(Calendar.MONTH) + 1);
		}
		
		
		
//		System.out.println("voucher year : " + sdf.format(cal.getTime()));
//		System.out.println("acdef year : " + acdef.getXyear());
//		System.out.println("Year diff : " + (Integer.parseInt(sdf.format(cal.getTime())) - Integer.parseInt(acdef.getXyear())));

		
		

		// if existing
		if(StringUtils.isNotBlank(acheader.getXvoucher())) {
			Acheader exist = acService.findAcheaderByXvoucher(acheader.getXvoucher());
			if(exist == null) {
				responseHelper.setErrorStatusAndMessage("Can't find voucher in this system");
				return responseHelper.getResponse();
			}

			BeanUtils.copyProperties(acheader, exist, "xvoucher","xstatusjv");
			long count = acService.updateAcheader(exist);
			if(count == 0) {
				responseHelper.setErrorStatusAndMessage("Can't update voucher");
				return responseHelper.getResponse();
			}

			responseHelper.setSuccessStatusAndMessage("Voucher updated successfully");
			responseHelper.setRedirectUrl("/account/voucher/" + exist.getXvoucher());
			return responseHelper.getResponse();
		}

		// if new
		acheader.setXstatusjv("Balanced");
		long count = acService.saveAcheader(acheader);
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't create voucher");
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Voucher created successfully");
		responseHelper.setRedirectUrl("/account/voucher/" + acheader.getXvoucher());
		return responseHelper.getResponse();
	}

	@GetMapping("{xvoucher}/voucherdetail/{xrow}/show")
	public String openVoucherDetailModal(@PathVariable String xvoucher, @PathVariable String xrow, Model model) {
		Acheader acheader = acService.findAcheaderByXvoucher(xvoucher);

		if("new".equalsIgnoreCase(xrow)) {
			Acdetail acdetail = new Acdetail();
			acdetail.setXvoucher(xvoucher);
			acdetail.setXdebit(BigDecimal.ZERO);
			acdetail.setXcredit(BigDecimal.ZERO);
			acdetail.setXwh(acheader != null ? acheader.getXwh() : "01");
			model.addAttribute("acdetail", acdetail);
		} else {
			Acdetail acdetail = acService.findAcdetailByXrowAndXvoucher(Integer.parseInt(xrow), xvoucher);
			model.addAttribute("acdetail", acdetail);
		}

		return "pages/account/voucher/voucherdetailmodal::voucherdetailmodal";
	}

	@PostMapping("/voucherdetail/save")
	public @ResponseBody Map<String, Object> savePoorddetail(Acdetail acdetail){
		if(StringUtils.isBlank(acdetail.getXvoucher())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		if(StringUtils.isBlank(acdetail.getXacc())) {
			responseHelper.setErrorStatusAndMessage("Account required");
			return responseHelper.getResponse();
		}

		if(acdetail.getXdebit() == null) acdetail.setXdebit(BigDecimal.ZERO);
		if(acdetail.getXcredit() == null) acdetail.setXcredit(BigDecimal.ZERO);

		if(acdetail.getXdebit().compareTo(BigDecimal.ZERO) == -1 || acdetail.getXcredit().compareTo(BigDecimal.ZERO) == -1) {
			responseHelper.setErrorStatusAndMessage("Invalid David or Credit amount");
			return responseHelper.getResponse();
		}

		if(acdetail.getXdebit().equals(BigDecimal.ZERO) && acdetail.getXcredit().equals(BigDecimal.ZERO)) {
			responseHelper.setErrorStatusAndMessage("Invalid David and Credit amount");
			return responseHelper.getResponse();
		}

		if(acdetail.getXdebit().compareTo(BigDecimal.ZERO) == 1 && acdetail.getXcredit().compareTo(BigDecimal.ZERO) == 1) {
			responseHelper.setErrorStatusAndMessage("You should enter only David or Credit amount");
			return responseHelper.getResponse();
		}

		if(acdetail.getXdebit().compareTo(BigDecimal.ZERO) == 1) {
			acdetail.setXprime(acdetail.getXdebit());
		} else {
			acdetail.setXprime(acdetail.getXcredit().multiply(BigDecimal.valueOf(-1)));
		}

		// if existing
		if(acdetail.getXrow() > 0) {
			Acdetail exist = acService.findAcdetailByXrowAndXvoucher(acdetail.getXrow(), acdetail.getXvoucher());
			if(exist == null) {
				responseHelper.setErrorStatusAndMessage("Can't find detail to do update");
				return responseHelper.getResponse();
			}

			BeanUtils.copyProperties(acdetail, exist, "xvoucher", "xrow");
			long count = acService.updateAcdetail(exist);
			if(count == 0) {
				responseHelper.setErrorStatusAndMessage("Can't update voucher detail");
				return responseHelper.getResponse();
			}
			responseHelper.setReloadSectionIdWithUrl("voucherdetailtable", "/account/voucher/voucherdetail/" + exist.getXvoucher());
			responseHelper.setSecondReloadSectionIdWithUrl("voucherform", "/account/voucher/voucherform/" + exist.getXvoucher());
			responseHelper.setSuccessStatusAndMessage("Voucher detail update successfully");
			return responseHelper.getResponse();
		}

		// if new
		long count = acService.saveAcdetail(acdetail);
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't save voucher detail");
			return responseHelper.getResponse();
		}
		responseHelper.setReloadSectionIdWithUrl("voucherdetailtable", "/account/voucher/voucherdetail/" + acdetail.getXvoucher());
		responseHelper.setSecondReloadSectionIdWithUrl("voucherform", "/account/voucher/voucherform/" + acdetail.getXvoucher());
		responseHelper.setSuccessStatusAndMessage("Voucher detail saved successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/voucherdetail/{xvoucher}")
	public String reloadVouvherDetailTabble(@PathVariable String xvoucher, Model model) {
		model.addAttribute("acheader", acService.findAcheaderByXvoucher(xvoucher));
		model.addAttribute("acdetails", acService.findAcdetailsByXvoucher(xvoucher));
		return"pages/account/voucher/voucher::voucherdetailtable";
	}

	@GetMapping("/voucherform/{xvoucher}")
	public String reloadVouvherForm(@PathVariable String xvoucher, Model model) {
		model.addAttribute("acheader", acService.findAcheaderByXvoucher(xvoucher));
		return"pages/account/voucher/voucher::voucherform";
	}

	@PostMapping("/posted/{xvoucher}")
	public @ResponseBody Map<String, Object> posted(@PathVariable String xvoucher){
		Acheader acheader = acService.findAcheaderByXvoucher(xvoucher);
		if(acheader == null) {
			responseHelper.setErrorStatusAndMessage("Voucher not found in this system");
			return responseHelper.getResponse();
		}

		// validate
		if(StringUtils.isBlank(acheader.getXstatusjv())) {
			responseHelper.setErrorStatusAndMessage("Voucher status invalid");
			return responseHelper.getResponse();
		}
		if(!"Balanced".equalsIgnoreCase(acheader.getXstatusjv())) {
			responseHelper.setErrorStatusAndMessage("Voucher status is not Balanced");
			return responseHelper.getResponse();
		}
		
		List<Acdetail> details = acService.findAcdetailsByXvoucher(xvoucher);
		if(details == null || details.isEmpty()) {
			responseHelper.setErrorStatusAndMessage("Voucher details is empty");
			return responseHelper.getResponse();
		}

		try {
			acService.procAcVoucherPost(acheader.getXyear(), acheader.getXper(), acheader.getXvoucher(), acheader.getXvoucher());
		} catch (Exception e) {
			log.error(ERROR, e.getMessage(), e);
			responseHelper.setErrorStatusAndMessage(e.getMessage());
			return responseHelper.getResponse();
		}

		responseHelper.setSecondReloadSectionIdWithUrl("voucherform", "/account/voucher/voucherform/" + acheader.getXvoucher());
		responseHelper.setReloadSectionIdWithUrl("voucherdetailtable", "/account/voucher/voucherdetail/" + acheader.getXvoucher());
		responseHelper.setSuccessStatusAndMessage("Posted successful");
		return responseHelper.getResponse();
	}

	@PostMapping("/unposted/{xvoucher}")
	public @ResponseBody Map<String, Object> unposted(@PathVariable String xvoucher){
		Acheader acheader = acService.findAcheaderByXvoucher(xvoucher);
		if(acheader == null) {
			responseHelper.setErrorStatusAndMessage("Voucher not found in this system");
			return responseHelper.getResponse();
		}

		// validate
		if(StringUtils.isBlank(acheader.getXstatusjv())) {
			responseHelper.setErrorStatusAndMessage("Voucher status invalid");
			return responseHelper.getResponse();
		}
		if(!"Posted".equalsIgnoreCase(acheader.getXstatusjv())) {
			responseHelper.setErrorStatusAndMessage("Voucher status is not Posted");
			return responseHelper.getResponse();
		}

		try {
			acService.procAcVoucherUnPost(acheader.getXyear(), acheader.getXper(), acheader.getXvoucher(), acheader.getXvoucher());
		} catch (Exception e) {
			log.error(ERROR, e.getMessage(), e);
			responseHelper.setErrorStatusAndMessage(e.getMessage());
			return responseHelper.getResponse();
		}

		responseHelper.setSecondReloadSectionIdWithUrl("voucherform", "/account/voucher/voucherform/" + acheader.getXvoucher());
		responseHelper.setReloadSectionIdWithUrl("voucherdetailtable", "/account/voucher/voucherdetail/" + acheader.getXvoucher());
		responseHelper.setSuccessStatusAndMessage("Unposted successful");
		return responseHelper.getResponse();
	}

	@PostMapping("/archive/{xvoucher}")
	public @ResponseBody Map<String, Object> archive(@PathVariable String xvoucher){
		return doArchiveOrRestore(xvoucher, true);
	}

	private Map<String, Object> doArchiveOrRestore(String xvoucher, boolean archive){
		Acheader acheader = acService.findAcheaderByXvoucher(xvoucher);
		if(acheader == null) {
			responseHelper.setErrorStatusAndMessage("Voucher not found in this system");
			return responseHelper.getResponse();
		}

		if("Posted".equalsIgnoreCase(acheader.getXstatusjv())) {
			responseHelper.setErrorStatusAndMessage("Voucher already posted");
			return responseHelper.getResponse();
		}

		List<Acdetail> details = acService.findAcdetailsByXvoucher(xvoucher);
		if(details != null && !details.isEmpty()) {
			responseHelper.setErrorStatusAndMessage("Voucher details is not empty. Delete all voucher details first");
			return responseHelper.getResponse();
		}

		long count = acService.deleteAcheader(acheader.getXvoucher());
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't delete Voucher");
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Voucher deleted successfully");
		responseHelper.setRedirectUrl("/account/voucher");
		return responseHelper.getResponse();
	}

	@PostMapping("{xvoucher}/voucherdetail/{xrow}/delete")
	public @ResponseBody Map<String, Object> deleteVoucherDetail(@PathVariable String xvoucher, @PathVariable String xrow, Model model) {
		Acdetail acdetail = acService.findAcdetailByXrowAndXvoucher(Integer.parseInt(xrow), xvoucher);
		if(acdetail == null) {
			responseHelper.setErrorStatusAndMessage("Voucher detail not found in this system");
			return responseHelper.getResponse();
		}

		long count = acService.deleteAcdetail(acdetail.getXrow(), acdetail.getXvoucher());
		if(count == 0) {
			responseHelper.setErrorStatusAndMessage("Can't delete voucher detail");
			return responseHelper.getResponse();
		}

		responseHelper.setReloadSectionIdWithUrl("voucherdetailtable", "/account/voucher/voucherdetail/" + xvoucher);
		responseHelper.setSecondReloadSectionIdWithUrl("voucherform", "/account/voucher/voucherform/" + xvoucher);
		responseHelper.setSuccessStatusAndMessage("Voucher detail saved successfully");
		return responseHelper.getResponse();
	}
}
