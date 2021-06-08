package com.asl.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.asl.entity.LandExperience;

@Component
public interface LandExperienceService {
	
	public long save(LandExperience landExperience);
	
	public long update(LandExperience landExperience);

	public List<LandExperience> getAllLandExperience();

	public LandExperience  findByLandExperiencePerson(String xperson);
	
}
