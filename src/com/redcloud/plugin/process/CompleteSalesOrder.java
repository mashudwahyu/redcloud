package com.redcloud.plugin.process;

import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import org.compiere.model.MOrder;

public class CompleteSalesOrder extends SvrProcess{
	
	private int p_C_Order_ID = 0;
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (name.equals("C_Order_ID"))
				p_C_Order_ID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	
	
	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub

		MOrder order = new MOrder(getCtx(), p_C_Order_ID, get_TrxName());
		try{
			order.processIt(DocAction.ACTION_Complete);
			order.saveEx();
			addLog("Success Complete SO "+order.getDocumentNo());
		}catch(AdempiereException ex){
			addLog("Failed Complete SO "+order.getDocumentNo());
		}		
		return null;
	}	
}
