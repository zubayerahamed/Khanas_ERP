package com.asl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.asl.entity.LandSurvey;

@Mapper
public interface LandSurveyMapper {
	

	public long saveLandSurvey(LandSurvey landSurvey);
	
	public long updateLandSurvey(LandSurvey landSurvey);
	
	public long deleteDetail(LandSurvey landSurvey);
	
	public List<LandSurvey> getAllLandSurvey(String zid);
	
	public List<LandSurvey> findByXlandSurvey(String xland, String zid);
	
	public LandSurvey findLandSurveydetailByXlandAndXrow(String xland, int xrow, String zid);
	
	public LandSurvey findLandSurveyByXlandAndXperson(String xland, String xperson,  String zid);
}