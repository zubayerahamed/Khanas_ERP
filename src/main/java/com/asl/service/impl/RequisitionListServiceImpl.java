package com.asl.service.impl;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asl.entity.PoordDetail;
import com.asl.mapper.OrderRequisitionMapper;
import com.asl.model.BranchesRequisitions;
import com.asl.service.RequisitionListService;

@Service
public class RequisitionListServiceImpl extends AbstractGenericService implements RequisitionListService{

	@Autowired private OrderRequisitionMapper requisitionListMapper;

	@Override
	public List<BranchesRequisitions> getAllOpenBranchesRequisitions() {
		return requisitionListMapper.getAllOpenBranchesRequisitions(sessionManager.getBusinessId());
	}

	@Override
	public List<BranchesRequisitions> getAllBranchesRequisitions(Date xdate) {
		if(!Boolean.TRUE.equals(sessionManager.getZbusiness().getCentral())) return Collections.emptyList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return requisitionListMapper.getAllBranchesRequisitions(sdf.format(xdate), sessionManager.getBusinessId());
	}

	@Override
	public List<BranchesRequisitions> getAllBranchesRequisitionDetails(Date xdate) {
		if(!Boolean.TRUE.equals(sessionManager.getZbusiness().getCentral())) return Collections.emptyList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return requisitionListMapper.getAllBranchesRequisitionDetails(sdf.format(xdate), sessionManager.getBusinessId());
	}

	@Override
	public List<PoordDetail> getDetailListByXpornumAndBranchZid(String xpornum, String branchzid) {
		if(StringUtils.isBlank(xpornum) || StringUtils.isBlank(branchzid)) return Collections.emptyList();
		return requisitionListMapper.getDetailListByXpornumAndBranchZid(xpornum, branchzid);
	}

}
