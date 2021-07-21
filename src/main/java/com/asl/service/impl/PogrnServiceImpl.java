package com.asl.service.impl;

import java.util.Collections;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asl.entity.PogrnDetail;
import com.asl.entity.PogrnHeader;
import com.asl.mapper.PogrnMapper;
import com.asl.model.ServiceException;
import com.asl.service.PogrnService;

@Service
public class PogrnServiceImpl extends AbstractGenericService implements PogrnService {

	@Autowired
	private PogrnMapper pogrnMapper;

	@Transactional
	@Override
	public long save(PogrnHeader pogrnHeader) {
		if (pogrnHeader == null || StringUtils.isBlank(pogrnHeader.getXtypetrn())) return 0;
		pogrnHeader.setZid(sessionManager.getBusinessId());
		pogrnHeader.setZauserid(getAuditUser());
		return pogrnMapper.savePogrnHeader(pogrnHeader);
	}

	@Transactional
	@Override
	public long update(PogrnHeader pogrnHeader) {
		if (pogrnHeader == null || StringUtils.isBlank(pogrnHeader.getXgrnnum())) return 0;
		pogrnHeader.setZid(sessionManager.getBusinessId());
		pogrnHeader.setZuuserid(getAuditUser());
		return pogrnMapper.updatePogrnHeader(pogrnHeader);
	}

	@Transactional
	@Override
	public long updatePogrnHeaderTotalAmt(PogrnDetail pogrnDetail) {
		if(pogrnDetail == null) return 0;
		return pogrnMapper.updatePogrnHeaderTotalAmt(pogrnDetail);
	}

	@Transactional
	@Override
	public long updatePogrnHeaderTotalAmtAndGrandTotalAmt(String xgrnnum) {
		if(StringUtils.isBlank(xgrnnum)) return 0;
		return pogrnMapper.updatePogrnHeaderTotalAmtAndGrandTotalAmt(xgrnnum, sessionManager.getBusinessId());
	}

	@Override
	@Transactional
	public long saveDetail(PogrnDetail pogrnDetail) {
		if(pogrnDetail == null || StringUtils.isBlank(pogrnDetail.getXgrnnum())) return 0;
		pogrnDetail.setZid(sessionManager.getBusinessId());
		pogrnDetail.setZauserid(getAuditUser());
		long count = pogrnMapper.savePogrnDetail(pogrnDetail);
		if(count != 0) count = updatePogrnHeaderTotalAmtAndGrandTotalAmt(pogrnDetail.getXgrnnum());
		return count;
	}

	@Override
	@Transactional
	public long saveDetails(List<PogrnDetail> pogrnDetails) throws ServiceException {
		if(pogrnDetails == null || pogrnDetails.isEmpty()) return 0;
		long totalCount = 0;
		for(PogrnDetail pogrnDetail : pogrnDetails) {
			pogrnDetail.setZid(sessionManager.getBusinessId());
			pogrnDetail.setZauserid(getAuditUser());
			long count = pogrnMapper.savePogrnDetail(pogrnDetail);
			if(count == 0) throw new ServiceException("Can't save GRN details");
			totalCount += count;
		}
		return totalCount;
	}

	@Override
	@Transactional
	public long updateDetail(PogrnDetail pogrnDetail) {
		if(pogrnDetail == null || StringUtils.isBlank(pogrnDetail.getXgrnnum())) return 0;
		pogrnDetail.setZid(sessionManager.getBusinessId());
		pogrnDetail.setZuuserid(getAuditUser());
		long count = pogrnMapper.updatePogrnDetail(pogrnDetail);
		if(count != 0) count = updatePogrnHeaderTotalAmtAndGrandTotalAmt(pogrnDetail.getXgrnnum());
		return count;
	}

	@Override
	@Transactional
	public long deleteDetail(PogrnDetail pogrnDetail) {
		if(pogrnDetail == null) return 0;
		long count = pogrnMapper.deletePogrnDetail(pogrnDetail);
		if(count != 0) count = updatePogrnHeaderTotalAmtAndGrandTotalAmt(pogrnDetail.getXgrnnum());
		return count;
	}

	@Override
	public PogrnHeader findPogrnHeaderByXgrnnum(String xgrnnum) {
		if (StringUtils.isBlank(xgrnnum)) return null;
		return pogrnMapper.findPogrnHeaderByXgrnnum(xgrnnum, sessionManager.getBusinessId());
	}

	@Override
	public List<PogrnHeader> findPogrnHeaderByXpornum(String xpornum) {
		if (StringUtils.isBlank(xpornum)) return null;
		return pogrnMapper.findPogrnHeaderByXpornum(xpornum, sessionManager.getBusinessId());
	}

	@Override
	public PogrnDetail findPogrnDetailByXgrnnumAndXrow(String xgrnnum, int xrow) {
		if(StringUtils.isBlank(xgrnnum) || xrow == 0) return null;
		return pogrnMapper.findPogrnDetailByXgrnnumAndXrow(xgrnnum, xrow, sessionManager.getBusinessId());
	}

	@Override
	public List<PogrnDetail> findPogrnDetailByXgrnnum(String xgrnnum) {
		if(StringUtils.isBlank(xgrnnum)) return Collections.emptyList();
		return pogrnMapper.findPogrnDetailByXgrnnum(xgrnnum, sessionManager.getBusinessId());
	}

	@Override
	public List<PogrnHeader> getAllPogrnHeaders() {
		return pogrnMapper.getAllPogrnHeader(sessionManager.getBusinessId());
	}

	@Override
	@Transactional
	public void procInventory(String xgrnnum, String xpornum, String p_seq) {
		pogrnMapper.procInventory(sessionManager.getBusinessId(), sessionManager.getLoggedInUserDetails().getUsername(), xgrnnum, xpornum, p_seq);
	}

	@Override
	@Transactional
	public void procTransferPOtoAP(String xgrnnum, String p_seq) {
		pogrnMapper.procTransferPOtoAP(sessionManager.getBusinessId(), sessionManager.getLoggedInUserDetails().getUsername(), xgrnnum, p_seq);
	}
	
	@Override
	public List<PogrnHeader> searchPoord(String xpornum){
		return pogrnMapper.searchPoord(xpornum.toUpperCase(), sessionManager.getBusinessId());
	}

	@Override
	@Transactional
	public long archiveDetailsByXgrnnum(String xgrnnum) {
		if(StringUtils.isBlank(xgrnnum)) return 0;
		return pogrnMapper.archiveDetailsByXgrnnum(xgrnnum, sessionManager.getLoggedInUserDetails().getUsername(), sessionManager.getBusinessId());
	}

	@Override
	public long countOfPogrndetailByXgrnnum(String xgrnnum) {
		if(StringUtils.isBlank(xgrnnum)) return 0;
		return pogrnMapper.countOfPogrndetailByXgrnnum(xgrnnum, sessionManager.getBusinessId());
	}

	@Transactional
	@Override
	public long deletePogrnheader(String xgrnnum) {
		if(StringUtils.isBlank(xgrnnum)) return 0;
		return pogrnMapper.deletePogrnheader(xgrnnum, sessionManager.getBusinessId());
	}


	
	

}
