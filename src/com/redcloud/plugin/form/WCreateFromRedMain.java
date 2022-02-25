	/******************************************************************************
	 * Copyright (C) 2009 Low Heng Sin                                            *
	 * Copyright (C) 2009 Idalica Corporation                                     *
	 * This program is free software; you can redistribute it and/or modify it    *
	 * under the terms version 2 of the GNU General Public License as published   *
	 * by the Free Software Foundation. This program is distributed in the hope   *
	 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
	 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
	 * See the GNU General Public License for more details.                       *
	 * You should have received a copy of the GNU General Public License along    *
	 * with this program; if not, write to the Free Software Foundation, Inc.,    *
	 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
	 *****************************************************************************/

	package com.redcloud.plugin.form;

	import java.math.BigDecimal;
	import java.util.ArrayList;
	import java.util.Vector;
	import java.util.logging.Level;

	import org.adempiere.exceptions.AdempiereException;
	import org.adempiere.webui.apps.AEnv;
	import org.adempiere.webui.apps.form.WCreateFromWindow;
	//import org.adempiere.webui.apps.form.WCreateFromWindow;
	import org.adempiere.webui.component.Grid;
	import org.adempiere.webui.component.GridFactory;
	import org.adempiere.webui.component.Label;
	import org.adempiere.webui.component.ListModelTable;
	import org.adempiere.webui.component.Listbox;
	import org.adempiere.webui.component.ListboxFactory;
	import org.adempiere.webui.component.Panel;
	import org.adempiere.webui.component.Row;
	import org.adempiere.webui.component.Rows;
	import org.adempiere.webui.editor.WNumberEditor;
	import org.adempiere.webui.editor.WStringEditor;
	import org.adempiere.webui.event.ValueChangeEvent;
	import org.adempiere.webui.event.ValueChangeListener;
	import org.compiere.model.GridTab;
	import org.compiere.util.CLogger;
	import org.compiere.util.DisplayType;
	import org.compiere.util.Env;
	import org.compiere.util.KeyNamePair;
	import org.compiere.util.Msg;
	import org.zkoss.zk.ui.event.Event;
	import org.zkoss.zk.ui.event.EventListener;
	import org.zkoss.zk.ui.event.Events;
	import org.zkoss.zul.Vlayout;

	public class WCreateFromRedMain extends CreateFromRedMain implements EventListener<Event>, ValueChangeListener
	{
		private WCreateFromWindow window;
		public WCreateFromRedMain(GridTab gridTab) {
			super(gridTab);
			// TODO Auto-generated constructor stub
			log.info(getGridTab().toString());
			
			window = new WCreateFromWindow(this, getGridTab().getWindowNo());

			try
			{
				if (!dynInit())
					return;
				zkInit();
				setInitOK(true);
			}
			catch(Exception e)
			{
				log.log(Level.SEVERE, "", e);
				setInitOK(false);
				throw new AdempiereException(e.getMessage());
			}
			AEnv.showWindow(window);
		}

		/**	Logger			*/
		private CLogger log = CLogger.getCLogger(getClass());
	    
	    protected Label orgLabel = new Label();
	    protected Listbox organizationField = ListboxFactory.newDropdownListbox();
	    
	    protected Label productLabel = new Label();
	    protected Listbox productField = ListboxFactory.newDropdownListbox();

	    protected Label reqLabel = new Label();
	    protected Listbox requisitionField = ListboxFactory.newDropdownListbox();
		
		protected Label nameLabel = new Label();
		protected WStringEditor nameField = new WStringEditor();
		
		protected Label qtyLabel = new Label();
		protected WNumberEditor qtyField = new WNumberEditor();
		
		protected Label uomLabel = new Label();
		protected Listbox uomField = ListboxFactory.newDropdownListbox();
		
		protected int m_AD_Client_ID = 0;
	    
		/**
		 *  Dynamic Init
		 *  @throws Exception if Lookups cannot be initialized
		 *  @return true if initialized
		 */
		public boolean dynInit() throws Exception
		{
			log.config("");
			
			super.dynInit();
			
			window.setTitle(getTitle());

			nameField = new WStringEditor ("Name", false, false, true, 10, 30, null, null);
			nameField.getComponent().addEventListener(Events.ON_CHANGE, this);
			
			qtyField = new WNumberEditor("kst_qty", false, false, true,DisplayType.Quantity , "Qty");
			qtyField.setValue(Env.ZERO);
			
			initOrganizationDetails();
			loadRequisition();
			return true;
		}   //  dynInit
		
		protected void zkInit() throws Exception
		{ 
	    	orgLabel.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
	    	productLabel.setText(Msg.translate(Env.getCtx(), "M_Product"));
	        reqLabel.setText(Msg.translate(Env.getCtx(), "M_Requisition_ID"));
	        nameLabel.setText(Msg.getElement(Env.getCtx(), "Name", false));
	        qtyLabel.setText(Msg.getElement(Env.getCtx(), "Qty", false));
	        uomLabel.setText(Msg.translate(Env.getCtx(), "C_UOM_ID"));

			Vlayout vlayout = new Vlayout();
			vlayout.setVflex("1");
			vlayout.setWidth("100%");
	    	Panel parameterPanel = window.getParameterPanel();
			parameterPanel.appendChild(vlayout);
			
			Grid parameterStdLayout = GridFactory.newGridLayout();
	    	vlayout.appendChild(parameterStdLayout);
			
			Rows rows = (Rows) parameterStdLayout.newRows();
			
			Row row = rows.newRow();
			row.appendChild(orgLabel.rightAlign());
			row.appendChild(organizationField);

			productField.setHflex("1");
			row.appendChild(productLabel.rightAlign());
			row.appendChild(productField);
			
			row = rows.newRow();
			row.appendChild(nameLabel.rightAlign());
			row.appendChild(nameField.getComponent());

	        row.appendChild(reqLabel.rightAlign());
	        row.appendChild(requisitionField);
	        requisitionField.setHflex("1");
	        
	        row = rows.newRow();
			row.appendChild(qtyLabel.rightAlign());
			row.appendChild(qtyField.getComponent());
		
			row.appendChild(uomLabel.rightAlign());
	        row.appendChild(uomField);
	        uomField.setHflex("1");

			

		}

		private boolean 	m_actionActive = false;

		private int M_Product_ID;
		private int C_UOM_ID;

		private int M_Requisition_ID;

		private String m_name;
		
		private BigDecimal m_qty;
		
		/**
		 *  Action Listener
		 *  @param e event
		 * @throws Exception 
		 */
		public void onEvent(Event e) throws Exception
		{
			if (m_actionActive)
				return;
			m_actionActive = true;
			
			// Organization
	        if (e.getTarget().equals(organizationField))
	        {
	            KeyNamePair pp = organizationField.getSelectedItem().toKeyNamePair();
	            if (pp == null)
	                ;
	            else
	            {
	                AD_Org_ID = pp.getKey();
	                initRequisitionDetails();
	            }
	        }
			// Product Category
	        if (e.getTarget().equals(productField))
	        {
	            KeyNamePair pp = productField.getSelectedItem().toKeyNamePair();
	            if (pp == null)
	                ;
	            else
	            {
	                M_Product_ID = pp.getKey();
	                loadRequisition();
	            }
	        }
			
	        else if (e.getTarget().equals(requisitionField))
	        {
	            KeyNamePair pp = requisitionField.getSelectedItem().toKeyNamePair();
	            if (pp == null)
	                ;
	            else
	            {
	                M_Requisition_ID = pp.getKey();
	                loadRequisition();
	            }
	        }
			else if (e.getTarget().equals(nameField.getComponent()))
			{
				m_name = nameField.getDisplay();
				loadRequisition();
			}
			else if (e.getTarget().equals(qtyField.getComponent()))
			{
				m_qty = (BigDecimal)qtyField.getValue();
				loadRequisition();
			}
			else if (e.getTarget().equals(uomField))
				{
		            KeyNamePair pp = uomField.getSelectedItem().toKeyNamePair();
		            if (pp == null)
		                ;
		            else
		            {
		                C_UOM_ID = pp.getKey();
		                loadRequisition();
		            }
		        }
			m_actionActive = false;
		}
			
		/**
		 *  Change Listener
		 *  @param e event
		 */
		public void valueChange (ValueChangeEvent e)
		{
			if (log.isLoggable(Level.CONFIG)) log.config(e.getPropertyName() + "=" + e.getNewValue());

			log.warning(" test "+e.getPropertyName());
			if (e.getPropertyName().equals("Name"))
			{
				m_name = nameField.getDisplay();
				loadRequisition();
			}
			else if (e.getPropertyName().equals("Qty"))
			{
				m_qty = (BigDecimal)qtyField.getValue();
				loadRequisition();
			}
			window.tableChanged(null);
		}   //  vetoableChange

		/**
		 * Load Org
		 */
		private void initOrganizationDetails()
		{
		    organizationField.removeActionListener(this);
		    organizationField.removeAllItems();
		    
		    ArrayList<KeyNamePair> list = loadOrganizationData();
		    int index = 0;
			for(KeyNamePair knp : list){
				organizationField.addItem(knp);
				if(knp.getKey()==AD_Org_ID)
					organizationField.setSelectedIndex(index); // changed from 0 to AD_Org_ID
				index++;
				
			}
			// modified by @ZuhriUtama
			//organizationField.addActionListener(this);
			initRequisitionDetails(); // init Requisition
			// end modified by @ZuhriUtama
		}
		
				
		/**
		 * Load Requisition that are candidates for order
		 */
		private void initRequisitionDetails()
		{
		    requisitionField.removeActionListener(this);
		    requisitionField.removeAllItems();
		    //  None
		    KeyNamePair pp = new KeyNamePair(0,"");
		    requisitionField.addItem(pp);
		    
		    ArrayList<KeyNamePair> list = loadRequisitionByOrg(AD_Org_ID);
			for(KeyNamePair knp : list)
				requisitionField.addItem(knp);
			
		    requisitionField.setSelectedIndex(0);
		    requisitionField.addActionListener(this);
		    
		}
		
		/**
		 *  Load Data - Requisition
		 */
		protected void loadRequisition ()
		{
			loadTableOIS(getRequisitionData(AD_Org_ID, M_Requisition_ID, M_Product_ID, m_name, m_qty, C_UOM_ID));
		}
			
		/**
		 *  Load Order/Invoice/Shipment data into Table
		 *  @param data data
		 */
		protected void loadTableOIS (Vector<?> data)
		{
			window.getWListbox().clear();
			
			//  Remove previous listeners
			window.getWListbox().getModel().removeTableModelListener(window);
			//  Set Model
			ListModelTable model = new ListModelTable(data);
			model.addTableModelListener(window);
			window.getWListbox().setData(model, getOISColumnNames());
			//
			
			configureMiniTable(window.getWListbox());
		}   //  loadOrder
		
		public void showWindow()
		{
			window.setVisible(true);
		}
		
		public void closeWindow()
		{
			window.dispose();
		}

		@Override
		public Object getWindow() {
			return window;
		}
	}
