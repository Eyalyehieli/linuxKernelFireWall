package packetsNetFilterDB;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class AddProtocolGUI extends GUI{
	
	SqliteDB sqlitedb;
	ArrayList<StructsTable> structs;
	int selectedRow;
	public AddProtocolGUI(int width,int height,String msg)
	{
		super(width,height,msg);
	}
	
	public void createGUI(String ip,int port,String protocolName,int activeStatus) throws SQLException 
	{
		sqlitedb=SqliteDB.getSqliteDBInstance();
		DefaultTableModel model=new DefaultTableModel(new Object[]{"Code", "Name"},0);
		JLabel nameLabel=super.createLabel(this.frm, "Protocol Name:", 350, 100, 200, 100);
		JTextField nameTextField=super.createTextField(this.frm, 500, 125, 200, 50);	
		
		JLabel ipLabel=super.createLabel(this.frm, "Ip:", 300, 200, 200, 50);
		JTextField ipTextField=super.createTextField(this.frm, 350, 200, 200, 50);
		
		JLabel portLabel=super.createLabel(this.frm, "Port:", 630, 200, 200, 50);
		JTextField portTextField=super.createTextField(this.frm, 680, 200, 200, 50);
		
		JLabel structsLabel=super.createLabel(this.frm, "Structs", 550, 250, 200, 100);
		
		JTable structsTable=super.createTable(this.frm, model,325, 350, 500, 500);
		
		JButton AddButton=super.createButton(this.frm, "Add", 950, 350, 200, 100);
		JButton EditButton=super.createButton(this.frm, "Edit", 950, 610, 200, 100);
		JButton RemoveButton=super.createButton(this.frm, "Remove", 950, 480, 200, 100);
		JButton FinishButton=super.createButton(this.frm, "click me if you have finished",850 ,50, 300, 100);
		FinishButton.setVisible(false);
		
		if(ip!=""&&port!=Integer.MIN_VALUE&&protocolName!=""&&activeStatus!=Integer.MIN_VALUE)
		{
			nameTextField.setText(protocolName);
			ipTextField.setText(ip);
			portTextField.setText(String.valueOf(port));
			for(int i=0;i<((DefaultTableModel) structsTable.getModel()).getRowCount();i++)
		  	{
		  		((DefaultTableModel) structsTable.getModel()).removeRow(i);
		  	}
		    ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
			protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
			structs=sqlitedb.GetAllStructsByProtocolId(protocol);
			for(StructsTable struct:structs)
			{
				((DefaultTableModel) structsTable.getModel()).addRow(new Object[]{String.valueOf(struct.getCode()),struct.getName()});
			}
			
			
	}
		
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				  try {
					TextChanged();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }
			  public void removeUpdate(DocumentEvent e) {
				  try {
						TextChanged();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			  }
			  public void insertUpdate(DocumentEvent e) {
				  try {
					TextChanged();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			  }

			  public void TextChanged() throws SQLException {
				  
				  	for(int i=0;i<((DefaultTableModel) structsTable.getModel()).getRowCount();i++)
				  	{
				  		((DefaultTableModel) structsTable.getModel()).removeRow(i);
				  	}
				    ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
					structs=sqlitedb.GetAllStructsByProtocolId(protocol);
					for(StructsTable struct:structs)
					{
						((DefaultTableModel) structsTable.getModel()).addRow(new Object[]{String.valueOf(struct.getCode()),struct.getName()});
					}
			     }
			  }
			);
		
		
		EditButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectedRow=structsTable.getSelectedRow();
				if(selectedRow!=-1)
				{
					AddStructGUI addStructGUI=new AddStructGUI(1200,800,"Add Struct");
					try {
					   
						int StructCode=Integer.valueOf(((DefaultTableModel) structsTable.getModel()).getValueAt(selectedRow, 0).toString());
						String StructName=((DefaultTableModel) structsTable.getModel()).getValueAt(selectedRow, 1).toString();
						addStructGUI.createGUI(StructName,StructCode,nameTextField.getText(),Integer.valueOf(portTextField.getText()),ipTextField.getText());
				}
					catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					try {
						String new_ip=ipTextField.getText();
						int new_port=Integer.valueOf(portTextField.getText());
						String protocol_name=nameTextField.getText();
						ProtocolTable protocol=new ProtocolTable(protocol_name);
						protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,-1));
						ConnectionTable connection=new ConnectionTable(ip,port);
						connection.setId(sqlitedb.GetConnectionIdByIpAndPort(connection, -1));
						sqlitedb.updateProtocolAndConnection(protocol,connection,protocol_name,new_ip,new_port);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		}
		});
		
		RemoveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try {
					int struct_id;
					int selected_row=structsTable.getSelectedRow();
					int code=Integer.valueOf(structsTable.getModel().getValueAt(selected_row, 0).toString());
					String name=structsTable.getModel().getValueAt(selected_row, 1).toString();
					ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol,0));
					struct_id=sqlitedb.GetStructIdByCodeAndProtocol(new StructsTable(name,code,protocol),0,0);
					sqlitedb.DeleteStructById(struct_id);
					 ((DefaultTableModel)structsTable.getModel()).removeRow(selected_row);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		}
		);
		
		AddButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AddStructGUI addStructGUI=new AddStructGUI(1200,800,"Add Struct");
				try {
					addStructGUI.createGUI("",Integer.MIN_VALUE,nameTextField.getText(),Integer.valueOf(portTextField.getText()),ipTextField.getText());
					
					/*for(int i=0;i<structsTable.getModel().getRowCount();i++)
				  	{
						((DefaultTableModel) structsTable.getModel()).removeRow(i);
				  	}
					ProtocolTable protocol=new ProtocolTable(nameTextField.getText());
					protocol.setId(sqlitedb.GetProtocolIdByProtocolName(protocol));
					structs=sqlitedb.GetAllStructsByProtocolId(protocol);
					for(StructsTable struct:structs)
					{
						((DefaultTableModel) structsTable.getModel()).addRow(new Object[]{String.valueOf(struct.getCode()),struct.getName()});
					}
					FinishButton.setVisible(true);*/
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1.toString());
				}
			}
		}
		);
		
		this.frm.setLayout(null); 
	    this.frm.setSize(this.width,this.height);   
	    this.frm.setVisible(true);
	    this.frm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

}
