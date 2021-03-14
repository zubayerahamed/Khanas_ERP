package com.asl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.asl.entity.ImtorDetail;
import com.asl.entity.ImtorHeader;

@Mapper
public interface ImtorMapper {
	
	public long saveImtorHeader(ImtorHeader imtorHeader);
	public long updateImtorHeader(ImtorHeader imtorHeader);
	
	public long saveImtorDetail(ImtorDetail imtorDetail);
	public long updateImtorDetail(ImtorDetail imtorDetail);
	public long deleteImtorDetail(ImtorDetail imtorDetail);
	
	public ImtorHeader findImtorHeaderByXtornum(String xtornum, String zid);
	public List<ImtorHeader> getAllImtorHeader(String zid);
	
	public ImtorDetail findImtorDetailByXtornumAndXrow(String xtornum, int xrow, String zid);
	public ImtorDetail findImtorDetailByXtornumAndXitem(String xtornum, String xitem, String zid);
	public List<ImtorDetail> findImtorDetailByXtornum(String xtornum, String zid);
	
}
