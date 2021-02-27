package com.asl.controller;

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

import com.asl.entity.Xtrn;
import com.asl.enums.ResponseStatus;
import com.asl.service.ListService;
import com.asl.service.XtrnService;

/**
 * @author Zubayer Ahamed
 * @since Feb 27, 2021
 */
@Controller
@RequestMapping("/mastersetup/xtrn")
public class XtrnController extends ASLAbstractController {

	@Autowired private ListService listService;
	@Autowired private XtrnService xtrnService;

	@GetMapping
	public String loadXtrnPage(Model model) {
		model.addAttribute("xtrnTypes", listService.getList("CODE_TYPE", "XTRN_CODE"));
		model.addAttribute("xtrn", getDefaultXtrn());
		model.addAttribute("xtrnList", xtrnService.getAllXtrn());
		return "pages/mastersetup/xtrn/xtrn";
	}

	@GetMapping("/{xtypetrn}/{xtrn}")
	public String loadXtrnPage(@PathVariable String xtypetrn, @PathVariable String xtrn, Model model) {
		Xtrn x = xtrnService.findByXtypetrnAndXtrn(xtypetrn, xtrn);
		if(x == null) {
			return "redirect:/mastersetup/xtrn";
		}

		model.addAttribute("xtrnTypes", listService.getList("CODE_TYPE", "XTRN_CODE"));
		model.addAttribute("xtrn", x);
		model.addAttribute("xtrnList", xtrnService.getAllXtrn());
		return "pages/mastersetup/xtrn/xtrn";
	}

	private Xtrn getDefaultXtrn() {
		Xtrn xtrn = new Xtrn();
		xtrn.setXnum(1);
		xtrn.setXinc(1);
		return xtrn;
	}

	@PostMapping("/save")
	public @ResponseBody Map<String, Object> save(Xtrn xtrn, BindingResult bindingResult){
		if(xtrn == null || StringUtils.isBlank(xtrn.getXtypetrn()) || StringUtils.isBlank(xtrn.getXtrn())) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		// Modify xtrn code
		xtrn.setXtrn(xtrn.getXtrn().trim());

		// Validate Xtrn

		// if existing record
		Xtrn existXtrn = xtrnService.findByXtypetrnAndXtrn(xtrn.getXtypetrn(), xtrn.getXtrn());
		if(existXtrn != null) {
			BeanUtils.copyProperties(xtrn, existXtrn, "xtypetrn", "xtrn");
			long count = xtrnService.update(existXtrn);
			if(count == 0) {
				responseHelper.setStatus(ResponseStatus.ERROR);
				return responseHelper.getResponse();
			}
			responseHelper.setSuccessStatusAndMessage("Transaction code updated successfully");
			responseHelper.setRedirectUrl("/mastersetup/xtrn/" + existXtrn.getXtypetrn() + "/" + existXtrn.getXtrn());
			return responseHelper.getResponse();
		}

		// If new xtrn
		long count = xtrnService.save(xtrn);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}
		responseHelper.setSuccessStatusAndMessage("Transaction code saved successfully");
		responseHelper.setRedirectUrl("/mastersetup/xtrn/" + xtrn.getXtypetrn() + "/" + xtrn.getXtrn());
		return responseHelper.getResponse();
	}

	@PostMapping("/archive/{xtypetrn}/{xtrn}")
	public @ResponseBody Map<String, Object> archive(@PathVariable String xtypetrn, @PathVariable String xtrn){
		return doArchiveOrRestore(xtypetrn, xtrn, true);
	}

	@PostMapping("/restore/{xtypetrn}/{xtrn}")
	public @ResponseBody Map<String, Object> restore(@PathVariable String xtypetrn, @PathVariable String xtrn){
		return doArchiveOrRestore(xtypetrn, xtrn, false);
	}

	public Map<String, Object> doArchiveOrRestore(String xtypetrn, String xtrn, boolean archive){
		Xtrn xt = xtrnService.findByXtypetrnAndXtrn(xtypetrn, xtrn);
		if(xt == null) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		xt.setZactive(archive ? Boolean.FALSE : Boolean.TRUE);
		long count = xtrnService.update(xt);
		if(count == 0) {
			responseHelper.setStatus(ResponseStatus.ERROR);
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Transaction code updated successfully");
		responseHelper.setRedirectUrl("/mastersetup/xtrn/" + xtypetrn + "/" + xtrn);
		return responseHelper.getResponse();
	}
}
