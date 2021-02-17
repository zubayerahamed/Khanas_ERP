package com.asl.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.asl.entity.Users;
import com.asl.entity.ASLBusiness;
import com.asl.model.Business;
import com.asl.service.BusinessService;

/**
 * @author Zubayer Ahamed
 * @since Dec 30, 2020
 */
@Controller
@RequestMapping("/business")
public class BusinessDashboardController extends ASLAbstractController {

	private static final String OUTSIDE_USERS_NAME = "anonymousUser";

	@Autowired private BusinessService businessService;

	@SuppressWarnings("unchecked")
	@GetMapping
	public String loadBusinessDashboard(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (!OUTSIDE_USERS_NAME.equalsIgnoreCase(username)) {
			return "redirect:/";
		}

		List<Users> list = null;
		if(sessionManager.getFromMap("FAKE_LOGIN_USER") != null) {
			list = (List<Users>) sessionManager.getFromMap("FAKE_LOGIN_USER");
		}
		if(list == null || list.isEmpty()) {
			return "redirect:/";
		}

		List<Business> businesses = new ArrayList<>();
		for(Users xus : list) {
			ASLBusiness zb = businessService.findBById(xus.getBusinessId());
			if(zb == null) continue;
			businesses.add(new Business(xus.getUsername(), xus.getPswd(), zb.getBusinessId(), zb.getBusinessName()));
		} 

		model.addAttribute("businesses", businesses);

		return "pages/business/business-dashboard";
	}
}
