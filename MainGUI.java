package packetsNetFilterDB;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class MainGUI extends GUI {
	
	SqliteDB sqlitedb;
	int selectedColumn;
	int selectedRow;
	ArrayList<FIreWallRulesTable> fireWallRules;
	public MainGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}

	@Override
	public void createGUI(JFrame frame) throws SQLException
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=new DefaultTableModel(new Object[]{"Ip", "Port","Protocol","Status"},0);
		sqlitedb.createTable("Protocols","id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(255) NOT NULL");
		sqlitedb.createTable("Connections","id INTEGER PRIMARY KEY AUTOINCREMENT,ip varchar(255) NOT NULL,port INTEGER NOT NULL");
		sqlitedb.createTable("Structs", "id INTEGER PRIMARY KEY AUTOINCREMENT,code INTEGER NOT NULL ,name varchar(255) NOT NULL,size INTEGER NOT NULL,protocol_id INTEGER NOT NULL,FOREIGN KEY(protocol_id) REFERENCES Protocols(id)");
		sqlitedb.createTable("StructFields","id INTEGER PRIMARY KEY AUTOINCREMENT,fieldName varchar(255) NOT NULL,type varchar(255) NOT NULL,minRange varchar(255) NOT NULL,maxRange varchar(255) NOT NULL,struct_id INTEGER NOT NULL,FOREIGN KEY(struct_id) REFERENCES Structs(id)");
		sqlitedb.createTable("FireWallRules","id INTEGER PRIMARY KEY AUTOINCREMENT,activeStatus INTEGER NOT NULL,connection_id INTEGER NOT NULL, protocol_id INTEGER NOT NULL,FOREIGN KEY(connection_id) REFERENCES Connections(id),FOREIGN KEY(protocol_id) REFERENCES Protocols(id)");
		String[] columns= {"Ip","Port","Protocol","Status"};
		//String[][] data= {{"1","2","3","4"},{"1","2","3","4"}};
		
		JLabel fireWallLabel=super.createLabel(this.frm, "Fire Wall Rules", 400, 50, 200, 100);
		//fireWallTableRules.setVisible(false);
		JButton AddButton=super.createButton(this.frm, "Add",950, 100, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove",950, 300, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit", 950, 500, 200, 100);
		
		JTable fireWallTableRules=super.createTable(this.frm,model,20,150,900,600);
		
		
		fireWallRules=sqlitedb.GetAllFireWallRules();
		GUI.deleteAllJtableRows(fireWallTableRules);
		for(FIreWallRulesTable fireWallRule:fireWallRules)
		{
			((DefaultTableModel) fireWallTableRules.getModel()).addRow(new Object[]{fireWallRule.getConnection().getIp(),fireWallRule.getConnection().getPort(),fireWallRule.getProtocol().getName(),fireWallRule.getActiveStatus()});
		}
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int fireWallRule_id;
				int selected_row=fireWallTableRules.getSelectedRow();
				String ip=fireWallTableRules.getValueAt(selected_row, 0).toString();
				int port=Integer.valueOf(fireWallTableRules.getValueAt(selected_row, 1).toString());
				String protocolName=fireWallTableRules.getValueAt(selected_row, 2).toString();
				int activeStatus=Integer.valueOf(fireWallTableRules.getValueAt(selected_row, 3).toString());
				ConnectionTable connection=new ConnectionTable(ip,port);
			try {
				connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
				ProtocolTable protocol=new ProtocolTable(protocolName);
				protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol, -1));
				FIreWallRulesTable fireWallRule=new FIreWallRulesTable(activeStatus,protocol,connection);
				
				
					fireWallRule_id=sqlitedb.GetFireWallRulesId(fireWallRule, -1);
					sqlitedb.DeleteFireWallRuleById(fireWallRule_id);
					((DefaultTableModel) fireWallTableRules.getModel()).removeRow(selected_row);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		);

		EditButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedColumn=fireWallTableRules.getSelectedColumn();
				if(selectedColumn!=3)
				{
					AddProtocolGUI addProtocolGUI=new AddProtocolGUI(1200,800,"Add Protocol");
					try {
						selectedRow=fireWallTableRules.getSelectedRow();
						String ip=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 0).toString();
						int port =Integer.valueOf(((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 1).toString());
						String protocolName=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 2).toString();
						int activeStatus=Integer.valueOf(((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 3).toString());
						addProtocolGUI.createGUI(ip,port,protocolName,activeStatus);
				}
					 catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					int fireWallRule_id;
					selectedRow=fireWallTableRules.getSelectedRow();
					System.out.println(fireWallTableRules.getModel().getValueAt(selectedRow, 3).toString());
					if(fireWallTableRules.getModel().getValueAt(selectedRow, 3).toString().equals("1"))
					{
						fireWallTableRules.getModel().setValueAt(0, selectedRow,3);
						String ip=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 0).toString();
						int port=Integer.valueOf(((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 1).toString());
						String protocolName=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 2).toString();
						ProtocolTable protocol=new ProtocolTable(protocolName);
						try {
							protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
							ConnectionTable connection=new ConnectionTable(ip,port);
							connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
							FIreWallRulesTable fireWallRule=new FIreWallRulesTable(0,protocol,connection);
							fireWallRule.setId(sqlitedb.GetFireWallRulesId(fireWallRule, -1));
							sqlitedb.updateFireWallRule(fireWallRule,0);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else
					{
						fireWallTableRules.getModel().setValueAt(1, selectedRow,3);
						String ip=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 0).toString();
						int port=Integer.valueOf(((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 1).toString());
						String protocolName=((DefaultTableModel) fireWallTableRules.getModel()).getValueAt(selectedRow, 2).toString();
						ProtocolTable protocol=new ProtocolTable(protocolName);
						try {
							protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
							ConnectionTable connection=new ConnectionTable(ip,port);
							connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));FIreWallRulesTable fireWallRule=new FIreWallRulesTable(0,protocol,connection);
							fireWallRule.setId(sqlitedb.GetFireWallRulesId(fireWallRule, -1));
							sqlitedb.updateFireWallRule(fireWallRule,1);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
		);
		
		AddButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						AddProtocolGUI addProtocolGUI=new AddProtocolGUI(1200,800,"Add Protocol");
						try {
							addProtocolGUI.createGUI("",Integer.MIN_VALUE,"",Integer.MIN_VALUE);
							fireWallRules=sqlitedb.GetAllFireWallRules();
							GUI.deleteAllJtableRows(fireWallTableRules);
							for(FIreWallRulesTable fireWallRule:fireWallRules)
							{
								((DefaultTableModel) fireWallTableRules.getModel()).addRow(new Object[]{fireWallRule.getConnection().getIp(),fireWallRule.getConnection().getPort(),fireWallRule.getProtocol().getName(),fireWallRule.getActiveStatus()});
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				);
		
		
		/*JButton InsertButton=super.createButton(this.frm, "Insert", 50, 300, 200, 200);
		JButton UpdateButton=super.createButton(this.frm, "Update",350 , 300, 200, 200);
		JButton DeleteButton=super.createButton(this.frm "Delete", 650, 300, 200, 200);
		JButton ShowButton=super.createButton(this.frm, "Show", 950, 300, 200, 200);
		
		ShowButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ShowGUI showGUI=new ShowGUI(1200,800,"ShowGUI");
				try {
					showGUI.createGUI();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.toString());
				}
			}
		});
		
		
		
		
		UpdateButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
				      UpdateGUI updateGUI=new UpdateGUI(1200,800,"UpdateGUI");
				      
				      try {
				    	  //frame.dispose();
						updateGUI.createGUI();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e1.toString());
					}
				}
		});
		
		
		
		
		InsertButton.addActionListener(new ActionListener() {
			 @Override
	   		  public void actionPerformed(ActionEvent e)
	   		  {
	   			InsertGUI insertGUI=new InsertGUI(1200,800,"InsertGUI");
	   			
	   			try {
	   				 //frame.dispose();
					 insertGUI.createGUI();
					 //frame.setVisible(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.toString());
				}
	   		  }
		    });
		DeleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DeleteGUI deleteGUI=new DeleteGUI(1200,800,"DeleteGUI");
				
				try {
					//frame.dispose();
					deleteGUI.createGUI();
					//frame.setVisible(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.toString());
				}
			}
					
			 		
				});
				*/
		
		frm.setLayout(null); 
		frm.setSize(this.width,this.height);   
		frm.setVisible(true);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
