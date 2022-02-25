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
	
	protected boolean isDevelopmentSample = false;
	
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

		String sqlStmt = "SELECT o.AD_Org_ID, o.Name from AD_Org o WHERE o.AD_Client_ID=?";

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


		String sqlStmt = "SELECT r.M_Requisition_ID, r.DocumentNo from M_Requisition r "
				+ " WHERE r.DocStatus in ('CO') "
				+ " AND EXISTS(SELECT 1 FROM M_RequisitionLine rl "
				+ " WHERE rl.M_Requisition_ID=r.M_Requisition_ID "
				+ " AND rl.AD_Org_ID=? AND rl.isActive = 'Y'"
				+ " AND EXISTS(SELECT 1 FROM C_DocType dt WHERE r.C_DocType_ID=dt.C_DocType_ID AND dt.docbasetype='POR' and dt.isactive='Y') "; 
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
	    
	protected Vector<Vector<Object>> getRequisitionData(int AD_Org_ID, int M_Requisition_ID, int M_Product_ID, String Name, BigDecimal Qty, int C_UOM_ID)
	{			
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuilder sqlStmt = new StringBuilder();

		sqlStmt.append(" SELECT a.M_RequisitionLine_ID, a.M_Product_ID, a.Name, a.Qty, a.C_UOM_ID");
		sqlStmt.append(" FROM ( ");
		sqlStmt.append(" SELECT rl.M_RequisitionLine_ID, rl.Qty, p.M_Product_ID, p.value||'_'||p.Name as Name, uom.C_UOM_ID");
		sqlStmt.append(" FROM M_RequisitionLine rl ");
		sqlStmt.append(" INNER JOIN M_Requisition r ON r.M_Requisition_ID=rl.M_Requisition_ID ");
		sqlStmt.append(" LEFT JOIN M_Product p ON p.M_Product_ID = rl.M_Product_ID ");
		sqlStmt.append(" INNER JOIN C_UOM uom ON (uom.C_UOM_ID=rl.C_UOM_ID) ");
		sqlStmt.append(" WHERE rl.AD_Org_ID=? AND rl.C_BPartner_ID=? AND r.DocStatus IN ('CO')");
		
		sqlStmt.append(" AND r.M_Warehouse_ID=? ");
		sqlStmt.append(" AND rl.IsActive='Y' ");
		sqlStmt.append(" AND rl.Qty <> 0 ");
		
		sqlStmt.append(" ) a ");
		sqlStmt.append(" GROUP BY a.M_RequisitionLine_ID, a.M_Product_ID, a.Name, a.Qty, a.C_UOM_ID ");
		sqlStmt.append(" HAVING a.Qty > 0");
		sqlStmt.append(" ORDER BY a.Name");
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
			if(M_Product_ID > 0){
				pstmt.setInt(index++, M_Product_ID);
			}
			if(Name!=null && !Name.equals("")){
				pstmt.setString(index++, Name);
			}
			if((Qty.intValue()>0)){
				pstmt.setBigDecimal(index++, Qty);
			}
			if(C_UOM_ID > 0){
				pstmt.setInt(index++, C_UOM_ID);
			}

			rs = pstmt.executeQuery();

			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>(6);
				line.add(false);   // 0-Selection
				KeyNamePair pp = new KeyNamePair(rs.getInt(1), rs.getString(2));
				line.add(pp); // 1-Requisition
				pp = new KeyNamePair(rs.getInt(3), rs.getString(4));
				line.add(pp); // 2-Product
				line.add(rs.getBigDecimal(5));  // 3-Qty
				line.add(rs.getInt(6)); //4-UOM
				
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
		miniTable.setColumnClass(1, String.class, true);       //  1-Requisition
		miniTable.setColumnClass(2, String.class, true);        //  2-Product
		miniTable.setColumnClass(3, BigDecimal.class, true);    //  3-Qty
		miniTable.setColumnClass(4, int.class, true);    	//  4-UOM
		//  Table UI
		miniTable.autoSize();

	}
	private MRedSub getRedSub(MRedMain redmain, int M_Requisition_ID, int M_Product_ID, String Name, BigDecimal qty, int c_UOM_ID) { 
		String where = "RED_Main_ID=? AND M_Requisition_ID=? AND M_Product_ID=? AND C_UOM_ID=?";
		List<Object> para = new ArrayList<Object>();
		para.add(redmain.get_ID());
		para.add(M_Requisition_ID);	
		para.add(M_Product_ID);				
		para.add(c_UOM_ID);
		
		where += " AND AND Name=? AND Qty=? ";
		para.add(Name);
		para.add(qty);

		MRedSub sub = new Query(redmain.getCtx(), MRedSub.Table_Name, where, redmain.get_TrxName())
		.setParameters(para)
		.setOnlyActiveRecords(true)
		.first();

		return sub;
	}
	public boolean save(IMiniTable miniTable, String trxName)
	{

		// Get Shipment
		int RED_Main_ID = ((Integer) getGridTab().getValue("RED_Main_ID")).intValue();
		MRedMain redmain = new MRedMain(Env.getCtx(), RED_Main_ID, trxName);
		if (log.isLoggable(Level.CONFIG)) log.config(redmain.toString());

		// Lines
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
				
				if (pp != null)
					M_RequisitionLine_ID = pp.getKey();
				
				if (log.isLoggable(Level.FINE)) log.fine("Product=" + M_Product_ID 
						+ ", RequisitionLine=" + M_RequisitionLine_ID);

				//	Check if already exist order line with product
				MRequisitionLine rLine = new MRequisitionLine(Env.getCtx(), M_RequisitionLine_ID, trxName);
				MProduct product = new MProduct(rLine.getCtx(), rLine.getM_Product_ID(), rLine.get_TrxName());
				MRedSub irs = getRedSub(redmain, M_RequisitionLine_ID, M_Product_ID, product.getName(), Qty, C_UOM_ID );  

				if(irs==null){
					irs = new MRedSub (redmain);
					
					irs.setM_RequisitionLine_ID(M_RequisitionLine_ID);
					irs.setM_Product_ID(M_Product_ID);
					irs.setName(product.getName());
					irs.setQty(Qty);
					irs.set_CustomColumn("kst_qty", Qty);
					irs.setC_UOM_ID(C_UOM_ID);
					
					
				}else{
					irs.setQty(Qty.add(irs.getQty()));
				}

				
			}   //   if selected
		}   //  for all rows
		return true;		

	}   //  saveOrder


	protected Vector<String> getOISColumnNames()
	{
		//  Header Info
		Vector<String> columnNames = new Vector<String>(5);
		columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
		columnNames.add(Msg.getElement(Env.getCtx(), "M_RequisitionLine_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "M_Product_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "Name"));
		columnNames.add(Msg.translate(Env.getCtx(), "Qty"));
		columnNames.add(Msg.translate(Env.getCtx(), "C_UOM_ID"));
		

		return columnNames;
	}

}
