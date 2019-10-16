package TableFilterTester;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import TableFilterTester.TableFilterTest.MyTableModel;
import TableFilterTester.TableFilterTest.RowListener;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class JTableRow2 {

	private class RowElem {
		private String firstName;
		private String lastName;
		private int years;
		private String sport;
		private boolean veggie;
		
		public RowElem() {
			this.firstName = "";
			this.lastName = "";
			this.years = 0;
			this.sport = "";
			this.veggie = false;
		}

		public RowElem(String firstName, String lastName, String sport, int years, boolean veggie) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.sport = sport;
			this.years = years;
			this.veggie = veggie;
		}

		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public int getYears() {
			return years;
		}
		public void setYears(int years) {
			this.years = years;
		}
		public String getSport() {
			return sport;
		}
		public void setSport(String sport) {
			this.sport = sport;
		}
		public boolean isVeggie() {
			return veggie;
		}
		public void setVeggie(boolean veggie) {
			this.veggie = veggie;
		}
	
	}

    // create an element for editing
	RowElem editRow = new RowElem();
	
    // create an array list
    ArrayList<RowElem> dataElements = new ArrayList<>();
	

	public class RowListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			 int viewRow = table.getSelectedRow();
             if (viewRow < 0) {
                 //Selection got filtered away.
            	 textStatus.setText("");
             } else {
                 int modelRow = 
                     table.convertRowIndexToModel(viewRow);
                 textStatus.setText(
                     String.format("Selected Row in view: %d. " +
                         "Selected Row in model: %d.", 
                         viewRow, modelRow));
     	        
                 // Do something with the data...
                 modelToRow(modelRow, editRow);
             }
		}

	}
	
	
	public class MyTableModel implements TableModel {

		public MyTableModel() {
			super();
			RowElem dataRowElem = new RowElem("Kathy", "Smith", "Snowboarding", 
					new Integer(5), new Boolean(false));
			dataElements.add(dataRowElem);

			RowElem dataRowElem2 = new RowElem("John", "Doe", "Rowing", 
					new Integer(3), new Boolean(true));
			dataElements.add(dataRowElem2);
			
			RowElem dataRowElem3 = new RowElem("Sue", "Black", "Knitting", 
					new Integer(2), new Boolean(false));
			dataElements.add(dataRowElem3);
			
			RowElem dataRowElem4 = new RowElem("Jane", "White", "Speed reading", 
					new Integer(20), new Boolean(true));
			dataElements.add(dataRowElem4);
			
			RowElem dataRowElem5 = new RowElem("Joe", "Brown", "Pool", 
					new Integer(10), new Boolean(false));
			dataElements.add(dataRowElem5);
		}

		private String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};
		/*
		private Object[][] data = {
				{"Kathy", "Smith",
				"Snowboarding", new Integer(5), new Boolean(false)},
				{"John", "Doe",
				"Rowing", new Integer(3), new Boolean(true)},
				{"Sue", "Black",
				"Knitting", new Integer(2), new Boolean(false)},
				{"Jane", "White",
				"Speed reading", new Integer(20), new Boolean(true)},
				{"Joe", "Brown",
				"Pool", new Integer(10), new Boolean(false)}
				};
		 */
        public final Object[] longValues = {"Jane", "Kathy", 
                "None of the above",
                new Integer(20), Boolean.TRUE};

        @Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return getValueAt(0, arg0).getClass();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int arg0) {
			return columnNames[arg0];
		}

		@Override
		public int getRowCount() {
			return dataElements.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			Object value = null;
			
            switch (column) {
            case 0:
            	value = dataElements.get(row).firstName;
            	break;
            case 1:
            	value = dataElements.get(row).lastName;
            	break;
            case 2:
            	value = dataElements.get(row).sport;
            	break;
            case 3:
            	value = dataElements.get(row).years;
            	break;
            case 4:
            	value = dataElements.get(row).veggie;
            	break;
            }

			return value;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (arg1 < 2) {
                return false;
            } else {
                return true;
            }			
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			
			RowElem editedRow = new RowElem(dataElements.get(row).firstName,
					dataElements.get(row).lastName,
					dataElements.get(row).sport,
					dataElements.get(row).years,
					dataElements.get(row).veggie);
			
            switch (col) {
            case 0:
            	editedRow.setFirstName((String)value);
            	break;
            case 1:
            	editedRow.setLastName((String)value);
            	break;
            case 2:
            	editedRow.setSport((String)value);
            	break;
            case 3:
            	editedRow.setYears((int)value);
            	break;
            case 4:
            	editedRow.setVeggie((boolean)value);
            	break;
            }
			
            dataElements.set(row, editedRow);
            fireTableCellUpdated(row, col);
		}

		private void fireTableCellUpdated(int row, int col) {
			// TODO Auto-generated method stub
	        // Do something with the data...
			modelToRow(row, editRow);
		}

		public void removeRow(int i) {
			// TODO Auto-generated method stub
			dataElements.remove(i);
			
		}

		public void addRow(RowElem elemRow) {
			// TODO Auto-generated method stub
			dataElements.add(elemRow);
		}

	}

	private JFrame frame;
	private JTable table;
	private TableRowSorter<MyTableModel> sorter;
	private JTextField textFirstName;
	private JTextField textLastName;
	private JTextField textYears;

    private JTextField textStatus;
	private MyTableModel model;
	private JComboBox<String> comboSport;
	private JCheckBox chkVeggie;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JTableRow2 window = new JTableRow2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JTableRow2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

	    model = new MyTableModel();
        sorter = new TableRowSorter<MyTableModel>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
  		
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
        //For the purposes of this example, better to have a single
        //selection.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //When selection changes, provide user with row numbers for
        //both view and model.
		table.getSelectionModel().addListSelectionListener(new RowListener());
		
		//Set up table & scroll pane
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(table);
		
        //Set up widgets
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		textFirstName = new JTextField();
		panel.add(textFirstName);
		textFirstName.setColumns(10);
		
		textLastName = new JTextField();
		panel.add(textLastName);
		textLastName.setColumns(10);
		
		comboSport = new JComboBox<String>();
		comboSport.setEditable(true);
		comboSport.addItem(" ");
		comboSport.addItem("Snowboarding");
		comboSport.addItem("Rowing");
		comboSport.addItem("Knitting");
		comboSport.addItem("Speed reading");
		comboSport.addItem("Pool");
		comboSport.addItem("None of the above");
		panel.add(comboSport);
		
		textYears = new JTextField();
		panel.add(textYears);
		textYears.setColumns(10);
		
		chkVeggie = new JCheckBox("Vegetarian");
		panel.add(chkVeggie);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1);
		
		JButton btnAdd = new JButton("Add");
		panel_1.add(btnAdd);
		
	    // button add row
        btnAdd.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             
    			RowElem editedRow = new RowElem();
    			
              	editedRow.setFirstName(textFirstName.getText());
            	editedRow.setLastName(textLastName.getText());
            	editedRow.setSport(comboSport.getItemAt(comboSport.getSelectedIndex()));
            	int iYears = Integer.parseInt(textYears.getText());
            	editedRow.setYears(iYears);
            	editedRow.setVeggie(chkVeggie.isSelected());
      			 int viewRow = table.getSelectedRow();
                 if (viewRow >= 0) {
                     int modelRow = 
                         table.convertRowIndexToModel(viewRow);
         	        
                     // Do something with the data...
                     modelToRow(modelRow, editedRow);
                     // add row to the model
                     model.addRow(editedRow);
                 }
           	

                
                // add row to the model
                //model.addRow(row);
            }
        });		
		
		JButton btnDelete = new JButton("Delete");
		panel_1.add(btnDelete);
        btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            
                // i = the index of the selected row
                int i = table.getSelectedRow();
                if(i >= 0){
                    // remove a row from jtable
                    model.removeRow(i);
                }
                else{
                    System.out.println("Delete Error");
                }
            }
        });
		
		
		JButton btnUpdate = new JButton("Update");
		panel_1.add(btnUpdate);
	    // button update row
		btnUpdate.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             
    			RowElem editedRow = new RowElem();
    			
              	editedRow.setFirstName(textFirstName.getText());
            	editedRow.setLastName(textLastName.getText());
            	editedRow.setSport(comboSport.getItemAt(comboSport.getSelectedIndex()));
            	int iYears = Integer.parseInt(textYears.getText());
            	editedRow.setYears(iYears);
            	editedRow.setVeggie(chkVeggie.isSelected());
      			 int viewRow = table.getSelectedRow();
                 if (viewRow >= 0) {
                     int modelRow = 
                         table.convertRowIndexToModel(viewRow);
         	        
                     // Do something with the data...
                     dataElements.set(modelRow, editedRow);
                 }
            }
        });		
		
		
		textStatus = new JTextField();
		panel_1.add(textStatus);
		textStatus.setColumns(10);
		
		//--- Table customization
        //Set up column sizes.
        initColumnSizes(table);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(table, table.getColumnModel().getColumn(2));
		
	}
	private void setUpSportColumn(JTable table, TableColumn sportColumn) {
    	//Set up the editor for the sport cells.
    	JComboBox<String> comboBox = new JComboBox<String>();
    	comboBox.addItem("Snowboarding");
    	comboBox.addItem("Rowing");
    	comboBox.addItem("Knitting");
    	comboBox.addItem("Speed reading");
    	comboBox.addItem("Pool");
    	comboBox.addItem("None of the above");
    	sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

    	//Set up tool tips for the sport cells.
    	DefaultTableCellRenderer renderer =
    	new DefaultTableCellRenderer();
    	renderer.setToolTipText("Click for combo box");
    	sportColumn.setCellRenderer(renderer);
		
	}

	private void initColumnSizes(JTable table) {
        MyTableModel model = (MyTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < 5; i++) {
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
 
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, longValues[i],
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            int cellHeight = comp.getPreferredSize().height;
            
            int cellWidth1 = Math.max(headerWidth, cellWidth);
            column.setPreferredWidth(cellWidth1);
            
            switch (i) {
            case 0:
            	textFirstName.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 1:
            	textLastName.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 2:
            	comboSport.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 3:
            	textYears.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 4:
            	chkVeggie.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            }
        }
        
	}

	private void modelToRow(int row, RowElem elemRow) {
        	
		elemRow.setFirstName((String) model.getValueAt(row, 0));
        textFirstName.setText(elemRow.getFirstName());

        elemRow.setLastName((String) model.getValueAt(row, 1));
        textLastName.setText(elemRow.getLastName());
        
        elemRow.setYears( (int)model.getValueAt(row, 3));
        Integer iYears = elemRow.getYears();
        textYears.setText(iYears.toString());
        
        elemRow.setSport((String) model.getValueAt(row, 2));
        int anIndex = 0;
        for(int i = 0; i < comboSport.getItemCount(); i++) {
        	if(comboSport.getItemAt(i).equals(elemRow.getSport())){
        		anIndex = i;
        		break;
        	}     
        }       
        comboSport.setSelectedIndex(anIndex);

        elemRow.setVeggie((boolean) model.getValueAt(row, 4));
        chkVeggie.setSelected(elemRow.isVeggie());
        
       	return;
    }

}
