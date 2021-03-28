package com.asl.service;

import java.util.Date;
import java.util.List;

import com.asl.entity.PoordDetail;
import com.asl.model.BranchesRequisitions;

public interface RequisitionListService {

	public List<BranchesRequisitions> getAllOpenBranchesRequisitions();
	public List<BranchesRequisitions> getAllBranchesRequisitions(Date xdate);
	public List<BranchesRequisitions> getAllBranchesRequisitionDetails(Date xdate);
	public List<PoordDetail> getDetailListByXpornumAndBranchZid(String xpornum, String branchzid);
}
