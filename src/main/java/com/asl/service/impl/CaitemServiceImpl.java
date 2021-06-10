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

	@Autowired private CaitemMapper caitemMapper;

	@Override
	public long save(Caitem caitem) {
		if (caitem == null || StringUtils.isBlank(caitem.getXtype())) return 0;
		caitem.setZid(getBusinessId());
		caitem.setZauserid(getAuditUser());
		return caitemMapper.saveCaitem(caitem);
	}

	@Override
	public long update(Caitem caitem) {
		if (caitem == null || StringUtils.isBlank(caitem.getXitem())) return 0;
		caitem.setZid(getBusinessId());
		caitem.setZuuserid(getAuditUser());
		return caitemMapper.updateCaitem(caitem);
	}

	@Override
	public List<Caitem> getAllCaitems() {
		return caitemMapper.getAllCaitems(getBusinessId());
	}

	@Override
	public List<Caitem> findByXcatitem(String xcatitem) {
		if (StringUtils.isBlank(xcatitem)) return Collections.emptyList();
		return caitemMapper.findByXcatitem(xcatitem, getBusinessId());
	}

	@Override
	public Caitem findByXitem(String xitem) {
		if (StringUtils.isBlank(xitem)) return null;
		return caitemMapper.findByXitem(xitem, getBusinessId());
	}

	@Override
	public List<Caitem> searchCaitem(String hint) {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.searchCaitem(hint.toUpperCase(), getBusinessId());
	}

	@Override
	public List<Caitem> searchCentralCaitem(String hint) {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.searchCentralCaitem(hint.toUpperCase(), getBusinessId());
	}

	public List<Caitem> searchCentralCaitemForRequisition(String hint)  {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.searchCentralCaitemForRequisition(hint.toUpperCase(), getBusinessId());
	}

	@Override
	public Caitem findCentralItemByXitem(String xitem) {
		if(StringUtils.isBlank(xitem)) return null;
		return caitemMapper.findCentralItemByXitem(xitem, getBusinessId());
	}

	@Override
	public List<Caitem> searchFinishedProductionCaitem(String hint) {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.searchFinishedProductionCaitem(hint.toUpperCase(), getBusinessId());
	}

	@Override
	public List<Caitem> searchRawMaterialsCaitem(String hint) {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.searchRawMaterialsCaitem(hint.toUpperCase(), getBusinessId());
	}

	@Override
	public List<Caitem> getWithoutProductionCaitems(String hint) {
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.getWithoutProductionCaitems(hint.toUpperCase(), getBusinessId());
	}

	@Override
	public List<Caitem> searchItemName(String hint){
		if(StringUtils.isBlank(hint)) return Collections.emptyList();
		return caitemMapper.searchItemName(hint.toUpperCase(), getBusinessId());
	}

	@Override
	public List<Caitem> getAllItemsWithoutRawMaterials() {
		return caitemMapper.getAllItemsWithoutRawMaterials(getBusinessId());
	}

	@Override
	public List<Caitem> getAllRequisitionItems() {
		return caitemMapper.getAllRequisitionItems(getBusinessId());
	}

}
