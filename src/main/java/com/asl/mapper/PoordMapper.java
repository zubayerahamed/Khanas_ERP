package com.asl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.asl.entity.PoordDetail;
import com.asl.entity.PoordHeader;

@Mapper
public interface PoordMapper {

	public long savePoordHeader(PoordHeader poordHeader);
	public long updatePoordHeader(PoordHeader poordHeader);
	public long updatePoordHeaderTotalAmt(PoordDetail poordDetail);

	public long savePoordDetail(PoordDetail poordDetail);
	public long updatePoordDetail(PoordDetail poordDetail);
	public long deletePoordDetail(PoordDetail poordDetail);

	public PoordHeader findPoordHeaderByXpornum(String xpornum, String zid);

	public PoordDetail findPoorddetailByXportNumAndXrow(String xpornum, int xrow, String zid);
	public List<PoordDetail> findPoorddetailByXpornum(String xpornum, String zid);

	public List<PoordHeader> getAllPoordHeader(String zid);
	public List<PoordHeader> getPoordHeadersByXtype(String xtype, String zid);

}