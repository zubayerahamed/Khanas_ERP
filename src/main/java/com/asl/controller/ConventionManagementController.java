package com.asl.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.asl.service.CaitemService;
import com.asl.service.OpordService;

@Controller
@RequestMapping("/conventionmanagement")
public class ConventionManagementController extends ASLAbstractController {
	
	@Autowired
	OpordService opordService;
	@Autowired
	CaitemService caitemService;	
	
	@GetMapping
	public String loadConventionManagementMenuPage(Model model) {
		model.addAttribute("availableHalls", opordService.findAvailableHallsByDate(new Date()));
		model.addAttribute("bookedHalls", opordService.findBookedHallsByXfuncdate(new Date()));
		
		//model.addAttribute("allHalls", caitemService.searchCaitem("ICHL-"));
		
		return "pages/conventionmanagement/conventionmanagement";
	}


}
