package com.asl.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asl.entity.Imtrn;
import com.asl.mapper.ImtrnMapper;
import com.asl.service.ImtrnService;

@Service
public class ImtrnServiceImpl extends AbstractGenericService implements ImtrnService {

	@Autowired
	private ImtrnMapper imtrnMapper;

	@Override
	public long save(Imtrn imtrn) {
		if (imtrn == null || StringUtils.isBlank(imtrn.getXtype())
				|| StringUtils.isBlank(imtrn.getXtrnimtrn()))
			return 0;
		imtrn.setZid(sessionManager.getBusinessId());
		imtrn.setZauserid(getAuditUser());
		return imtrnMapper.saveImtrn(imtrn);
	}

	@Override
	public long update(Imtrn imtrn) {
		if (imtrn == null || StringUtils.isBlank(imtrn.getXimtrnnum()))
			return 0;
		imtrn.setZid(sessionManager.getBusinessId());
		imtrn.setZuuserid(getAuditUser());
		return imtrnMapper.updateImtrn(imtrn);
	}

	@Override
	public Imtrn findImtrnByXimtrnnum(String ximtrnnum) {
		if (StringUtils.isBlank(ximtrnnum))
			return null;

		return imtrnMapper.findImtrnByXimtrnnum(ximtrnnum, sessionManager.getBusinessId());
	}

	@Override
	public List<Imtrn> getAllImtrn() {
		
		return imtrnMapper.getAllImtrn(sessionManager.getBusinessId());
	}
	
	
}
