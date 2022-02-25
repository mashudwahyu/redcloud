package com.redcloud.plugin.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;

public class MRedMain extends X_RED_Main{

	private static final long serialVersionUID = -7124837675070240912L;

	public MRedMain(Properties ctx, int RED_Main_ID, String trxName) {
		super(ctx, RED_Main_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MRedMain(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

public MRedSub[] getLines(){
		
		List<MRedSub> list = new Query(getCtx(), MRedSub.Table_Name, COLUMNNAME_RED_Main_ID+"=?", get_TrxName())
							.setParameters(getRED_Main_ID())
							.setOnlyActiveRecords(true)
							.list();
		
		return list.toArray(new MRedSub[list.size()]);
	}
		
}
