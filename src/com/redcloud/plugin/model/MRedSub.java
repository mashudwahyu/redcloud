package com.redcloud.plugin.model;

import java.sql.ResultSet;
import java.util.Properties;


public class MRedSub extends X_RED_Sub{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2076611601274220946L;

	public MRedSub(Properties ctx, int Red_Sub_ID, String trxName) {
		super(ctx, Red_Sub_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MRedSub(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MRedSub(MRedMain main) {
		super(main.getCtx(), main.getRED_Main_ID(), main.get_TrxName());
		//setAD_Org_ID(main.getAD_Org_ID());
		
		setRED_Main_ID(main.getRED_Main_ID());
	}
}