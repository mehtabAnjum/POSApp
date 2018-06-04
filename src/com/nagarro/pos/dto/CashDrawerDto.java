package com.nagarro.pos.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nagarro.pos.model.CashDrawer;

public class CashDrawerDto {
	
	@JsonProperty("cashdrawers")
	List<CashDrawer> list;
	boolean status;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<CashDrawer> getList() {
		return list;
	}

	public void setList(List<CashDrawer> list) {
		this.list = list;
	}

	public CashDrawerDto(List<CashDrawer> list, boolean status) {
		super();
		this.list = list;
		this.status = status;
	}

	

}
