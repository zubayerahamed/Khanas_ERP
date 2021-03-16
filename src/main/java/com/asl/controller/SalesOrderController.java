package com.asl.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asl.enums.ResponseStatus;
import com.asl.enums.TransactionCodeType;
import com.asl.model.BranchesRequisitions;
import com.asl.service.OpordService;

/**
 * @author Zubayer Ahamed
 * @since Mar 9, 2021
 */
@Controller
@RequestMapping("/salesninvoice/salesorder")
public class SalesOrderController extends ASLAbstractController {

	@Autowired private OpordService opordService;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@GetMapping
	public String loadSalesOrderPage(Model model) {
		model.addAttribute("salesOrders", opordService.findAllOpordHeaderByXtypetrnAndXtrnAndXdate(TransactionCodeType.SALES_ORDER.getCode(), "SO-", new Date()));
		return "pages/salesninvoice/salesorders/salesorders";
	}

	@GetMapping("/query")
	public String reloadTableWithData(@RequestParam String date, Model model) throws ParseException {
		model.addAttribute("salesOrders", opordService.findAllOpordHeaderByXtypetrnAndXtrnAndXdate(TransactionCodeType.SALES_ORDER.getCode(), "SO-", sdf.parse(date)));
		return "pages/salesninvoice/salesorders/salesorders::salesordertable";
	}

	@PostMapping("/query")
	public @ResponseBody Map<String, Object> queryForrequistionDetails(Date xdate, Model model){
		responseHelper.setReloadSectionIdWithUrl("salesordertable", "/salesninvoice/salesorder/query?date=" + sdf.format(xdate));
		responseHelper.setStatus(ResponseStatus.SUCCESS);
		return responseHelper.getResponse();
	}

	@GetMapping("/detailmatrix")
	public String loadRqlsDetails(Model model) {
		generateMatrixData(new Date(), model);
		return "pages/salesninvoice/salesorders/salesordersmatrix";
	}

	@GetMapping("/query/matrix")
	public String reloadTableWithDataMatrix(@RequestParam String date, Model model) throws ParseException {
		generateMatrixData(sdf.parse(date), model);
		return "pages/salesninvoice/salesorders/salesordersmatrix::salesordersmatrixtable";
	}

	@PostMapping("/query/matrix")
	public @ResponseBody Map<String, Object> queryForrequistionDetailsMatrix(Date xdate, Model model){
		responseHelper.setReloadSectionIdWithUrl("salesordersmatrixtable", "/salesninvoice/salesorder/query/matrix?date=" + sdf.format(xdate));
		responseHelper.setStatus(ResponseStatus.SUCCESS);
		return responseHelper.getResponse();
	}

	@GetMapping("/ordreqdetails/{xordernum}/show")
	public String displayItemDetailsOfOrderRequistion(@PathVariable String xordernum, Model model) {
		model.addAttribute("oporddetailsList", opordService.findOporddetailByXordernum(xordernum));
		return "pages/salesninvoice/salesorders/salesorderdetailmodal::salesorderdetailmodal";
	}

	private void generateMatrixData(Date date, Model model) {
		List<TableColumn> distinctItems = new ArrayList<>();
		List<BranchRow> distinctBranch = new ArrayList<>();

		List<BranchesRequisitions> bqList = opordService.getSalesOrderMatrxi(date);

		Map<String, TableColumn> columnRowMap = new HashMap<>();
		Map<String, BranchRow> branchRowMap = new HashMap<>();
		bqList.stream().forEach(bq -> {
			String item = bq.getXitem();
			if(columnRowMap.get(item) != null) {
				TableColumn c = columnRowMap.get(item);
				c.setTotalQty(c.getTotalQty().add(bq.getXqtyord()));
				columnRowMap.put(item, c);
			} else {
				TableColumn c = new TableColumn();
				c.setXitem(item);
				c.setXdesc(bq.getXdesc());
				c.setTotalQty(bq.getXqtyord());
				c.setXunitpur(bq.getXunitpur());
				columnRowMap.put(item, c);
			}

			String zorg = bq.getZorg();
			if(branchRowMap.get(zorg) != null) {
				BranchRow br = branchRowMap.get(zorg);

				BranchItem bi = new BranchItem(bq.getXitem(), bq.getXqtyord());
				br.getItems().add(bi);

				br.setTotalItemOrdered(br.getTotalItemOrdered().add(bi.getXqtyord()));
			} else {
				BranchRow br = new BranchRow();
				br.setZorg(zorg);

				BranchItem bi = new BranchItem(bq.getXitem(), bq.getXqtyord());
				br.getItems().add(bi);

				br.setTotalItemOrdered(bi.getXqtyord());

				branchRowMap.put(zorg, br);
			}
		});

		columnRowMap.entrySet().stream().forEach(c -> distinctItems.add(c.getValue()));
		branchRowMap.entrySet().stream().forEach(b -> distinctBranch.add(b.getValue()));

		model.addAttribute("distinctBranch", distinctBranch);
		model.addAttribute("distinctItems", distinctItems);
		model.addAttribute("bqlsDetailsList", bqList);
	}
}