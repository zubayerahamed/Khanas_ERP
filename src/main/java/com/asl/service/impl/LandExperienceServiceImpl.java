package com.asl.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asl.entity.LandExperience;
import com.asl.mapper.LandExperienceMapper;
import com.asl.service.LandExperienceService;


@Service
public class LandExperienceServiceImpl extends AbstractGenericService implements LandExperienceService {

	
	@Autowired
	private LandExperienceMapper landExperienceMapper;
	
	@Transactional
	@Override
	public long save(LandExperience landExperience) {
		if (landExperience == null)
			return 0;
		landExperience.setZid(sessionManager.getBusinessId());
		landExperience.setZauserid(getAuditUser());
		return landExperienceMapper.saveExPerson(landExperience);
	}

	@Transactional
	@Override
	public long update(LandExperience landExperience) {
		if (landExperience == null)
			return 0;
		landExperience.setZid(sessionManager.getBusinessId());
		landExperience.setZuuserid(getAuditUser());
		return landExperienceMapper.updateExPerson(landExperience);
	}

	@Override
	public List<LandExperience> getAllLandExperience() {
		return landExperienceMapper.getAllLandExperience(sessionManager.getBusinessId());
	}

	@Override
	public LandExperience findByLandExperiencePerson(String xperson) {
		if (StringUtils.isBlank(xperson))
			return null;
		return landExperienceMapper.findByLandExperiencePerson(xperson, sessionManager.getBusinessId());
	}
	
}
