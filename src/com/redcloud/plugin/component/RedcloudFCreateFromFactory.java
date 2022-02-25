package com.redcloud.plugin.component;

import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;
import com.redcloud.plugin.model.I_RED_Main;


import com.redcloud.plugin.form.WCreateFromRedMain;

public class RedcloudFCreateFromFactory implements ICreateFromFactory{

	@Override
	public ICreateFrom create(GridTab mTab) {
		// TODO Auto-generated method stub
		String tableName = mTab.getTableName();
		
		if(tableName.equals(I_RED_Main.Table_Name)){
			return new WCreateFromRedMain(mTab);
		}
		return null;
	}

}

