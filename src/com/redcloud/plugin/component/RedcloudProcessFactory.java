package com.redcloud.plugin.component;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

import com.redcloud.plugin.process.CompleteSalesOrder;


public class RedcloudProcessFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {
		// TODO Auto-generated method stub
		if(className.equals("com.redcloud.plugin.process.CompleteSalesOrder"))
			return new CompleteSalesOrder(); 
		return null;
	}
}
