package com.redcloud.plugin.component;


import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.Env;

import com.redcloud.plugin.model.MRedMain;
import com.redcloud.plugin.model.MRedSub;


public class RedcloudModelFactory implements IModelFactory{
	CLogger log = CLogger.getCLogger(RedcloudModelFactory.class);
	
	@Override
	public Class<?> getClass(String tableName) {
		if(tableName.equalsIgnoreCase(MRedSub.Table_Name))
			return MRedSub.class;
		
		if(tableName.equalsIgnoreCase(MRedMain.Table_Name))
			return MRedMain.class;
		
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {

		Class<?> clazz = getClass(tableName);
		if (clazz == null)
			return null;

		PO model = null;
		Constructor<?> constructor = null;

		try {
			constructor = clazz.getDeclaredConstructor(new Class[] { Properties.class, int.class, String.class });
			model = (PO) constructor.newInstance(new Object[] { Env.getCtx(), Record_ID, trxName });
		} catch (Exception e) {
			if (log.isLoggable(Level.WARNING))
				log.warning(String.format("Plugin: %s -> Class can not be instantiated for table: %s", "REDCLOUD", tableName));
		}

		return model;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {

		Class<?> clazz = getClass(tableName);
		if (clazz == null)
			return null;

		PO model = null;
		Constructor<?> constructor = null;

		try {
			constructor = clazz.getDeclaredConstructor(new Class[] { Properties.class, ResultSet.class, String.class });
			model = (PO) constructor.newInstance(new Object[] { Env.getCtx(), rs, trxName });
		} catch (Exception e) {
			if (log.isLoggable(Level.WARNING))
				log.warning(String.format("Plugin: %s -> Class can not be instantiated for table: %s", "REDCLOUD", tableName));
		}

		return model;
	}
}