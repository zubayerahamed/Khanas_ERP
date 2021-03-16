package com.asl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.asl.entity.PoordDetail;
import com.asl.model.BranchesRequisitions;

@Mapper
public interface OrderRequisitionMapper {

	public List<BranchesRequisitions> getAllBranchesRequisitions(String xdate, String zid);
	public List<BranchesRequisitions> getAllBranchesRequisitionDetails(String xdate, String zid);
	public List<PoordDetail> getDetailListByXpornumAndBranchZid(String xpornum, String branchzid) ;
}