package com.asl.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.asl.entity.Caitem;

@Component
public interface CaitemService {

	public long save(Caitem caitem);

	public long update(Caitem caitem);

	public List<Caitem> getAllCaitems();

	public List<Caitem> findByXcatitem(String xcatitem);

	public Caitem findByXitem(String xitem);

	public List<Caitem> searchCaitem(String hint);

	public List<Caitem> searchFinishedProductionCaitem(String hint);

	public List<Caitem> searchRawMaterialsCaitem(String hint);

	public List<Caitem> searchCentralCaitem(String hint);

	public List<Caitem> searchCentralCaitemForRequisition(String hint);

	public Caitem findCentralItemByXitem(String xitem);

	public List<Caitem> getWithoutProductionCaitems(String hint);

	public List<Caitem> searchItemName(String xdesc);

	public List<Caitem> getAllItemsWithoutRawMaterials();

	public List<Caitem> getAllRequisitionItems();
}
