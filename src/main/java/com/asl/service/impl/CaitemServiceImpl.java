package com.asl.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asl.entity.Caitem;
import com.asl.mapper.CaitemMapper;
import com.asl.service.CaitemService;

@Service
public class CaitemServiceImpl extends AbstractGenericService implements CaitemService {
	
	@Autowired
	private CaitemMapper caitemMapper;

	@Override
	public long save(Caitem caitem) {
		if (caitem == null || StringUtils.isBlank(caitem.getXitem())) return 0;
		caitem.setZid(sessionManager.getBusinessId());
		return caitemMapper.saveCaitem(caitem);
	}

	@Override
	public long update(Caitem caitem) {
		if (caitem == null || StringUtils.isBlank(caitem.getXitem())) return 0;
		caitem.setZid(sessionManager.getBusinessId());
		return caitemMapper.updateCaitem(caitem);
	}

	@Override
	public List<Caitem> getAllCaitems() {
		return caitemMapper.getAllCaitems(sessionManager.getBusinessId());
	}

	@Override
	public List<Caitem> findByXcatitem(String xcatitem) {
		if (StringUtils.isBlank(xcatitem)) return Collections.emptyList();
		return caitemMapper.findByXcatitem(xcatitem, sessionManager.getBusinessId());
	}

	@Override
	public Caitem findByXitem(String xitem) {
		if (StringUtils.isBlank(xitem)) return null;

		return caitemMapper.findByXitem(xitem, sessionManager.getBusinessId());
	}

}