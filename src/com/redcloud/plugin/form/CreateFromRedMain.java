package com.redcloud.plugin.form;

import org.compiere.grid.CreateFrom;
import org.compiere.model.GridTab;
import org.compiere.model.MProduct;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.MRequisitionLine;
import org.compiere.model.Query;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.sql.SQLException;

import org.compiere.apps.IStatusBar;

import com.redcloud.plugin.model.MRedMain;
import com.redcloud.plugin.model.MRedSub;
import org.compiere.util.KeyNamePair;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public abstract class CreateFromRedMain extends CreateFrom {
	
	protected MRedMain red_main = null;
	private int AD_Client_ID = 0;
	protected int AD_Org_ID = 0;
	private int RED_Main_ID = 0;
	
	
	public CreateFromRedMain(GridTab gridTab) {
		super(gridTab);
		if (log.isLoggable(Level.INFO)) log.info(gridTab.toString());
		// TODO Auto-generated constructor stub
	}
	
	/**
	 *  Dynamic Init
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		setTitle(Msg.getElement(Env.getCtx(), "RED_Main_ID", false) + " .. " + Msg.translate(Env.getCtx(), "CreateFrom"));

		AD_Client_ID = ((Integer) getGridTab().getValue("AD_Client_ID")).intValue();
		AD_Org_ID = ((Integer) getGridTab().getValue("AD_Org_ID")).intValue();
		RED_Main_ID = ((Integer) getGridTab().getValue("RED_Main_ID")).intValue();
		red_main = new MRedMain(Env.getCtx(), RED_Main_ID, null);
		
		return true;
	}   //  dynInit
	
	protected ArrayList<KeyNamePair> loadOrganizationData() {
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();

		String sqlStmt = "SELECT AD_Org_ID, Name from AD_Org WHERE AD_Client_ID=?";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sqlStmt, null);
			pstmt.setInt(1, AD_Client_ID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2)));
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, sqlStmt.toString(), e);
		} finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return list;
	}
	
	protected ArrayList<KeyNamePair> loadRequisitionByOrg(int AD_Org_ID) {
		ArrayList<KeyNamePair> list = new ArrayList<KeyNamePair>();

		String sqlStmt = " SELECT r.M_Requisition_ID, r.DocumentNo from M_Requisition r "
				+ " WHERE r.DocStatus in ('CO') "
				+ " AND EXISTS(SELECT 1 FROM M_RequisitionLine rl "
				+ " WHERE rl.M_Requisition_ID=r.M_Requisition_ID "
				+ " AND rl.AD_Org_ID=? AND rl.isActive = 'Y' ) "
				+ " AND r.TotalLines > 0 ";
		sqlStmt += " ORDER BY r.DocumentNo DESC ";

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = DB.prepareStatement(sqlStmt, null);
			pstmt.setInt(1, AD_Org_ID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new KeyNamePair(rs.getInt(1), rs.getString(2) )); //notice
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, sqlStmt.toString(), e);
		} finally{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}

		return list;
	}
	    
    
	protected Vector<Vector<Object>> getRequisitionData(int AD_Org_ID, int M_Requisition_ID)//, int M_Product_ID, BigDecimal Qty, int C_UOM_ID
	{			
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sqlStmt = new StringBuilder();
	
		//added by Syahnan@Kosta-Consulting 20160224
		sqlStmt.append(" SELECT rl.M_RequisitionLine_ID, r.DocumentNo, p.M_Product_ID, p.value||'_'||p.Name as Name, rl.Qty, uom.C_UOM_ID ");
		sqlStmt.append(" FROM M_RequisitionLine rl ");
		sqlStmt.append(" INNER JOIN M_Requisition r ON r.M_Requisition_ID=rl.M_Requisition_ID ");
		sqlStmt.append(" LEFT JOIN M_Product p ON p.M_Product_ID = rl.M_Product_ID ");
		sqlStmt.append(" INNER JOIN C_UOM uom ON (uom.C_UOM_ID=rl.C_UOM_ID) ");
		sqlStmt.append(" WHERE rl.AD_Org_ID=? AND r.DocStatus IN ('CO')");
		
		sqlStmt.append(" AND rl.IsActive='Y' ");
		sqlStmt.append(" AND rl.Qty <> 0 ");

		if(M_Requisition_ID>0)
			sqlStmt.append(" AND rl.M_Requisition_ID=? ");
		else
		{
			ArrayList<KeyNamePair> reqList = loadRequisitionByOrg(AD_Org_ID);
			if(reqList.size()>0) {
				sqlStmt.append(" AND rl.M_Requisition_ID IN (");
				int x=0;
				for(KeyNamePair req: reqList)
				{
					if(++x ==reqList.size())
						sqlStmt.append(req.getID()+") ");
					else
						sqlStmt.append(req.getID()+",");
				}
			}
		}
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sqlStmt.toString(), null);
			int index = 1;
			pstmt.setInt(index++, AD_Org_ID);
			if(M_Requisition_ID>0){
				pstmt.setInt(index++, M_Requisition_ID);
			}

			rs = pstmt.executeQuery();
	
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>(6);
				line.add(false);   // 0-Selection
				KeyNamePair pp = new KeyNamePair(rs.getInt(1), rs.getString(2)); // reqLineId, DocNo
				line.add(pp); // 1-Requisitionline
				pp = new KeyNamePair(rs.getInt(3), rs.getString(4));
				line.add(pp); // 2-Product
				line.add(rs.getBigDecimal(5));//Qty
				line.add(rs.getInt(6)); //4-UOM
//				
				data.add(line);
			}
		}
		catch (Exception ex)
		{
			log.log(Level.SEVERE, sqlStmt.toString(), ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	
		return data;
	}
	/**
	 *  List number of rows selected
	 */
	public void info(IMiniTable miniTable, IStatusBar statusBar)
	{

	}   //  infoInvoice

	protected void configureMiniTable (IMiniTable miniTable)
	{
		miniTable.setColumnClass(0, Boolean.class, false);      //  0-Selection
		miniTable.setColumnClass(1, String.class, true);       //  1-Requisitionline
		miniTable.setColumnClass(2, String.class, true);        //  2-Product
		miniTable.setColumnClass(3, BigDecimal.class, true);    //  3-Qty
		miniTable.setColumnClass(4, String.class, true);    	//  4-UOM
		//  Table UI
		miniTable.autoSize();

	}
	
	public boolean save(IMiniTable miniTable, String trxName)
	{
		// Get Shipment
		int RED_Main_ID = ((Integer) getGridTab().getValue("RED_Main_ID")).intValue();
		MRedMain redmain = new MRedMain(Env.getCtx(), RED_Main_ID, trxName);
		if (log.isLoggable(Level.CONFIG)) log.config(redmain.toString());

		for (int i = 0; i < miniTable.getRowCount(); i++)
		{
			if (((Boolean)miniTable.getValueAt(i, 0)).booleanValue()) {
				// variable values
				KeyNamePair pp = (KeyNamePair) miniTable.getValueAt(i, 1); // Requisition
				int M_RequisitionLine_ID = pp.getKey();
				pp = (KeyNamePair) miniTable.getValueAt(i, 2); // Product
				int M_Product_ID = pp.getKey();
				BigDecimal Qty = (BigDecimal) miniTable.getValueAt(i, 3); // Qty
				int C_UOM_ID = (int) miniTable.getValueAt(i, 4); // UOM
				
				MRequisitionLine rLine = new MRequisitionLine(Env.getCtx(), M_RequisitionLine_ID, trxName);
				MProduct product = new MProduct(rLine.getCtx(), rLine.getM_Product_ID(), rLine.get_TrxName());  
				MRedSub irs = new MRedSub(Env.getCtx(), 0, trxName);
				irs.setM_RequisitionLine_ID(M_RequisitionLine_ID);
				irs.setM_Product_ID(M_Product_ID);
				irs.setRED_Main_ID(RED_Main_ID);
				irs.setName(product.getName());
				irs.setQty(Qty);
				irs.setC_UOM_ID(C_UOM_ID);
				irs.saveEx();
				
			}   
		}
		return true;		

	}   //  saveOrder
	
//	private MRedSub getRedSub(MRedMain redmain, int M_RequisitionLine_ID) { 
//		String where = "RED_Main_ID=? AND M_RequisitionLine_ID=? ";
//		List<Object> para = new ArrayList<Object>();
//		para.add(redmain.getRED_Main_ID());
//		para.add(M_RequisitionLine_ID);	
//
//		MRedSub sub = new Query(redmain.getCtx(), MRedSub.Table_Name, where, redmain.get_TrxName())
//		.setParameters(para)
//		.setOnlyActiveRecords(true)
//		.first();
//
//		return sub;
//		}

	protected Vector<String> getOISColumnNames()
	{
		//  Header Info
		Vector<String> columnNames = new Vector<String>(4);
		columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
		columnNames.add(Msg.getElement(Env.getCtx(), "M_RequisitionLine_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "M_Product_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "Qty"));
		columnNames.add(Msg.translate(Env.getCtx(), "C_UOM_ID"));
		

		return columnNames;
	}

}
