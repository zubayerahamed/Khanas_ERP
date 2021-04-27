package com.asl.service.impl;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asl.entity.Pocrndetail;
import com.asl.entity.Pocrnheader;
import com.asl.mapper.PocrnMapper;
import com.asl.model.ServiceException;
import com.asl.service.PocrnService;

@Service
public class PocrnServiceImpl extends AbstractGenericService implements PocrnService {
	
	@Autowired
	private PocrnMapper pocrnMapper;

	@Override
	public long save(Pocrnheader pocrnheader) {
		if(pocrnheader == null)
			return 0;
		pocrnheader.setZid(sessionManager.getBusinessId());
		pocrnheader.setZauserid(getAuditUser());
		return pocrnMapper.savePocrnHeader(pocrnheader);
	}

	@Override
	public long update(Pocrnheader pocrnheader) {
		if (pocrnheader == null || StringUtils.isBlank(pocrnheader.getXcrnnum())) return 0;
		if(StringUtils.isBlank(pocrnheader.getZid()))
			pocrnheader.setZid(sessionManager.getBusinessId());
		pocrnheader.setZuuserid(getAuditUser());
		return pocrnMapper.updatePocrnHeader(pocrnheader);
	}

	@Override
	public long saveDetail(Pocrndetail pocrndetail) {
		if(pocrndetail == null || StringUtils.isBlank(pocrndetail.getXcrnnum())) return 0;
		pocrndetail.setZid(sessionManager.getBusinessId());
		pocrndetail.setZauserid(getAuditUser());
		long count = pocrnMapper.savePocrnDetail(pocrndetail);

		return count;
	}

	@Override
	@Transactional
	public long saveDetails(List<Pocrndetail> pocrndetail) throws ServiceException {
		if(pocrndetail == null || pocrndetail.isEmpty()) return 0;
		long totalCount = 0;
		for(Pocrndetail pocrnDetail : pocrndetail) {
			pocrnDetail.setZid(sessionManager.getBusinessId());
			pocrnDetail.setZauserid(getAuditUser());
			long count = pocrnMapper.savePocrnDetail(pocrnDetail);
			if(count == 0) throw new ServiceException("Can't save CRN details");
			totalCount += count;
		}
		return totalCount;
	}

	@Override
	public long updateDetail(Pocrndetail pocrndetail) {
		if(pocrndetail == null || StringUtils.isBlank(pocrndetail.getXcrnnum())) return 0;
		pocrndetail.setZid(sessionManager.getBusinessId());
		pocrndetail.setZuuserid(getAuditUser());
		long count = pocrnMapper.updatePocrnDetail(pocrndetail);
		return count;
	}
	
	@Override
	public long deleteDetail(Pocrndetail pocrndetail) {
		if(pocrndetail == null) return 0;
		long count = pocrnMapper.deletePocrnDetail(pocrndetail);		
		return count;
	}
	
	@Override
	public List<Pocrnheader> getAllPocrnheader() {
		
		return pocrnMapper.getAllPocrnheader(sessionManager.getBusinessId());
	}

	@Override
	public List<Pocrndetail> findPocrnDetailByXcrnnum(String xcrnnum) {
		if(StringUtils.isBlank(xcrnnum)) return Collections.emptyList();
		return pocrnMapper.findPocrnDetailByXcrnnum(xcrnnum, sessionManager.getBusinessId());
	}

	@Override
	public Pocrnheader findPocrnHeaderByXcrnnum(String xcrnnum) {
		if(StringUtils.isBlank(xcrnnum)) 
			return null;		
		return pocrnMapper.findPocrnHeaderByXcrnnum(xcrnnum, sessionManager.getBusinessId());
	}

	@Override
	public Pocrnheader findPocrnHeaderByXgrnnum(String xgrnnum) {
		if(StringUtils.isBlank(xgrnnum)) return null;
		return pocrnMapper.findPocrnHeaderByXgrnnum(xgrnnum, sessionManager.getBusinessId());
	}

	@Override
	public Pocrndetail findPocrnDetailByXcrnnumAndXrow(String xcrnnum, int xrow) {
		if(StringUtils.isBlank(xcrnnum) || xrow == 0) return null;
		return pocrnMapper.findPocrnDetailByXcrnnumAndXrow(xcrnnum, xrow, sessionManager.getBusinessId());
	}

	@Override
	public void procConfirmCRN(String xcrnnum, String p_seq) {
		pocrnMapper.procConfirmCRN(sessionManager.getBusinessId(), sessionManager.getLoggedInUserDetails().getUsername(), xcrnnum, p_seq);
	}

	@Override
	public void procIssuePricing(String xtrnnum, String xwh, String p_seq) {
		pocrnMapper.procIssuePricing(sessionManager.getBusinessId(), sessionManager.getLoggedInUserDetails().getUsername(), xtrnnum, xwh, p_seq);
	}

	@Override
	public void procTransferPRtoAP(String xcrnnum, String p_seq) {
		pocrnMapper.procTransferPRtoAP(sessionManager.getBusinessId(), sessionManager.getLoggedInUserDetails().getUsername(), xcrnnum, p_seq);
	}	
	
	@Override
	public List<Pocrnheader> findPocrnXstatuscrn(String hint) {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return pocrnMapper.findPocrnXstatuscrn(hint.toUpperCase(), sessionManager.getBusinessId());
	}

}
