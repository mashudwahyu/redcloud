/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package com.redcloud.plugin.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for RED_Sub
 *  @author iDempiere (generated) 
 *  @version Release 8.2 - $Id$ */
public class X_RED_Sub extends PO implements I_RED_Sub, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20220304L;

    /** Standard Constructor */
    public X_RED_Sub (Properties ctx, int RED_Sub_ID, String trxName)
    {
      super (ctx, RED_Sub_ID, trxName);
      /** if (RED_Sub_ID == 0)
        {
			setDescription (null);
			setM_Product_ID (0);
			setName (null);
			setRED_Main_ID (0);
			setRED_Sub_ID (0);
        } */
    }

    /** Load Constructor */
    public X_RED_Sub (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_RED_Sub[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException
    {
		return (org.compiere.model.I_C_UOM)MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
			.getPO(getC_UOM_ID(), get_TrxName());	}

	/** Set UOM.
		@param C_UOM_ID 
		Unit of Measure
	  */
	public void setC_UOM_ID (int C_UOM_ID)
	{
		if (C_UOM_ID < 1) 
			set_Value (COLUMNNAME_C_UOM_ID, null);
		else 
			set_Value (COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
	}

	/** Get UOM.
		@return Unit of Measure
	  */
	public int getC_UOM_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_UOM_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Comment/Help.
		@param Help 
		Comment or Hint
	  */
	public void setHelp (String Help)
	{
		set_Value (COLUMNNAME_Help, Help);
	}

	/** Get Comment/Help.
		@return Comment or Hint
	  */
	public String getHelp () 
	{
		return (String)get_Value(COLUMNNAME_Help);
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_RequisitionLine getM_RequisitionLine() throws RuntimeException
    {
		return (org.compiere.model.I_M_RequisitionLine)MTable.get(getCtx(), org.compiere.model.I_M_RequisitionLine.Table_Name)
			.getPO(getM_RequisitionLine_ID(), get_TrxName());	}

	/** Set Requisition Line.
		@param M_RequisitionLine_ID 
		Material Requisition Line
	  */
	public void setM_RequisitionLine_ID (int M_RequisitionLine_ID)
	{
		if (M_RequisitionLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_RequisitionLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_RequisitionLine_ID, Integer.valueOf(M_RequisitionLine_ID));
	}

	/** Get Requisition Line.
		@return Material Requisition Line
	  */
	public int getM_RequisitionLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_RequisitionLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name Product.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name Product.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_RED_Main getRED_Main() throws RuntimeException
    {
		return (I_RED_Main)MTable.get(getCtx(), I_RED_Main.Table_Name)
			.getPO(getRED_Main_ID(), get_TrxName());	}

	/** Set Redcloud Main.
		@param RED_Main_ID Redcloud Main	  */
	public void setRED_Main_ID (int RED_Main_ID)
	{
		if (RED_Main_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RED_Main_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RED_Main_ID, Integer.valueOf(RED_Main_ID));
	}

	/** Get Redcloud Main.
		@return Redcloud Main	  */
	public int getRED_Main_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RED_Main_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Redcloud Sub.
		@param RED_Sub_ID Redcloud Sub	  */
	public void setRED_Sub_ID (int RED_Sub_ID)
	{
		if (RED_Sub_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_RED_Sub_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_RED_Sub_ID, Integer.valueOf(RED_Sub_ID));
	}

	/** Get Redcloud Sub.
		@return Redcloud Sub	  */
	public int getRED_Sub_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_RED_Sub_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set RED_Sub_UU.
		@param RED_Sub_UU RED_Sub_UU	  */
	public void setRED_Sub_UU (String RED_Sub_UU)
	{
		set_ValueNoCheck (COLUMNNAME_RED_Sub_UU, RED_Sub_UU);
	}

	/** Get RED_Sub_UU.
		@return RED_Sub_UU	  */
	public String getRED_Sub_UU () 
	{
		return (String)get_Value(COLUMNNAME_RED_Sub_UU);
	}
}