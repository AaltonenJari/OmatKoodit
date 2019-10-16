package menutester;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Component;


public class JmenuTest {

	public class MyTable extends JTable {

	    protected String[] columnToolTips = {null,
                null,
                "The person's favorite color",
                "The person's favorite sport to participate in",
                "The number of years the person has played the sport",
                "If checked, the person eats no meat"};
		
		public MyTable(TableModel dm) {
			super(dm);
			// TODO Auto-generated constructor stub
		}

	            
        //Implement table cell tool tips.
        public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            int realColumnIndex = convertColumnIndexToModel(colIndex);

            if (realColumnIndex == 3) { //Sport column
                tip = "This person's favorite sport to "
                       + "participate in is: "
                       + getValueAt(rowIndex, colIndex);
            } else if (realColumnIndex == 5) { //Veggie column
                TableModel model = getModel();
                String firstName = (String)model.getValueAt(rowIndex,0);
                String lastName = (String)model.getValueAt(rowIndex,1);
                Boolean veggie = (Boolean)model.getValueAt(rowIndex,5);
                if (Boolean.TRUE.equals(veggie)) {
                    tip = firstName + " " + lastName
                          + " is a vegetarian";
                } else {
                    tip = firstName + " " + lastName
                          + " is not a vegetarian";
                }
            } else { 
                //You can omit this part if you know you don't 
                //have any renderers that supply their own tool 
                //tips.
                tip = super.getToolTipText(e);
            }
            return tip;
        }

        //Implement table header tool tips. 
        protected JTableHeader createDefaultTableHeader() {
            return new JTableHeader(columnModel) {
                public String getToolTipText(MouseEvent e) {
                    String tip = null;
                    java.awt.Point p = e.getPoint();
                    int index = columnModel.getColumnIndexAtX(p.x);
                    int realIndex = columnModel.getColumn(index).getModelIndex();
                    return columnToolTips[realIndex];
                }
            };
        }

	}

	public class TblTestActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
	        String command = e.getActionCommand();
	        //Cell selection is disabled in Multiple Interval Selection
	        //mode. The enabled state of cellCheck is a convenient flag
	        //for this status.
	        if ("Row Selection" == command) {
	            table.setRowSelectionAllowed(rowCheck.isSelected());
	            //In MIS mode, column selection allowed must be the
	            //opposite of row selection allowed.
	            if (!cellCheck.isEnabled()) {
	                table.setColumnSelectionAllowed(!rowCheck.isSelected());
	            }
	        } else if ("Column Selection" == command) {
	            table.setColumnSelectionAllowed(columnCheck.isSelected());
	            //In MIS mode, row selection allowed must be the
	            //opposite of column selection allowed.
	            if (!cellCheck.isEnabled()) {
	                table.setRowSelectionAllowed(!columnCheck.isSelected());
	            }
	        } else if ("Cell Selection" == command) {
	            table.setCellSelectionEnabled(cellCheck.isSelected());
	        } else if ("Multiple Interval Selection" == command) {
	            table.setSelectionMode(
	                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	            //If cell selection is on, turn it off.
	            if (cellCheck.isSelected()) {
	                cellCheck.setSelected(false);
	                table.setCellSelectionEnabled(false);
	            }
	            //And don't let it be turned back on.
	            cellCheck.setEnabled(false);
	        } else if ("Single Interval Selection" == command) {
	            table.setSelectionMode(
	                    ListSelectionModel.SINGLE_INTERVAL_SELECTION);
	            //Cell selection is ok in this mode.
	            cellCheck.setEnabled(true);
	        } else if ("Single Selection" == command) {
	            table.setSelectionMode(
	                    ListSelectionModel.SINGLE_SELECTION);
	            //Cell selection is ok in this mode.
	            cellCheck.setEnabled(true);
	        }

	        //Update checkboxes to reflect selection mode side effects.
	        rowCheck.setSelected(table.getRowSelectionAllowed());
	        columnCheck.setSelected(table.getColumnSelectionAllowed());
	        if (cellCheck.isEnabled()) {
	            cellCheck.setSelected(table.getCellSelectionEnabled());
	        }
		}

	}

	public class ColumnListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            output.append("COLUMN SELECTION EVENT. ");
            outputSelection();
		}

	}

	public class RowListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            output.append("ROW SELECTION EVENT. ");
            outputSelection();
		}

	}

	public class MyTableModel implements TableModel {
        private String[] columnNames = {"First Name",
                "Last Name",
                "Favorite Color",
                "Sport",
                "# of Years",
                "Vegetarian"};

        private Object[][] data = {
        		{"Kathy", "Smith", new Color(153, 0, 153),
        			"Snowboarding", new Integer(5), new Boolean(false)},
       			{"John", "Doe", new Color(51, 51, 153),
        			"Rowing", new Integer(3), new Boolean(true)},
       			{"Sue", "Black", new Color(51, 102, 51),
        			"Knitting", new Integer(2), new Boolean(false)},
       			{"Jane", "White", Color.red,
        			"Speed reading", new Integer(20), new Boolean(true)},
       			{"Joe", "Brown", Color.pink,
        			"Pool", new Integer(10), new Boolean(false)}
   		};

        public final Object[] longValues = {"Jane", "Kathy", Color.red,
                "None of the above",
                new Integer(20), Boolean.TRUE};
        
		@Override
		public void addTableModelListener(TableModelListener arg0) {
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
			return data.length;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return data[arg0][arg1];
		}

		@Override
		public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2) {
                return false;
            } else {
                return true;
            }			
		}

		@Override
		public void removeTableModelListener(TableModelListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);

		}
		private void fireTableCellUpdated(int row, int col) {
			// TODO Auto-generated method stub
			
		}

	}

	private TblTestActionListener tblTestActionListener;
	private JFrame frame;
	private JTable table;
	private ButtonGroup buttonGroup;
	private JCheckBox rowCheck;
	private JCheckBox columnCheck;
	private JCheckBox cellCheck;
	private JTextArea output;
	private boolean DEBUG = true;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JmenuTest window = new JmenuTest();
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
	public JmenuTest() {
		tblTestActionListener = new TblTestActionListener();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(240, 240, 240));
		frame.setBounds(100, 100, 600, 376);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		//Set up table & scroll pane
		JScrollPane scrollPane_1 = new JScrollPane();
		frame.getContentPane().add(scrollPane_1);
		table = new MyTable(new MyTableModel());
		scrollPane_1.setViewportView(table);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		
        //Set up widgets
		JLabel lblSelectionModel = new JLabel("Selection Mode");
		frame.getContentPane().add(lblSelectionModel);

        buttonGroup = new ButtonGroup();
		
		JRadioButton rdbtnMultipleIntervalSelection = new JRadioButton("Multiple Interval Selection");
		rdbtnMultipleIntervalSelection.setSelected(true);
		rdbtnMultipleIntervalSelection.addActionListener(tblTestActionListener);
        buttonGroup.add(rdbtnMultipleIntervalSelection);
		frame.getContentPane().add(rdbtnMultipleIntervalSelection);
		
		JRadioButton rdbtnSingleSelection = new JRadioButton("Single Selection");
		rdbtnMultipleIntervalSelection.addActionListener(tblTestActionListener);
        buttonGroup.add(rdbtnSingleSelection);
		frame.getContentPane().add(rdbtnSingleSelection);
		
		JRadioButton rdbtnSingleIntervalSelection = new JRadioButton("Single Interval Selection");
		rdbtnSingleIntervalSelection.addActionListener(tblTestActionListener);
        buttonGroup.add(rdbtnSingleIntervalSelection);
		frame.getContentPane().add(rdbtnSingleIntervalSelection);
		
		JLabel lblSelectionOptions = new JLabel("Selection Options");
		frame.getContentPane().add(lblSelectionOptions);
		
		rowCheck = new JCheckBox("Row Selection");
		rowCheck.setSelected(true);
		rowCheck.addActionListener(tblTestActionListener);
		frame.getContentPane().add(rowCheck);
		
		columnCheck = new JCheckBox("Column Selection");
		columnCheck.addActionListener(tblTestActionListener);
		frame.getContentPane().add(columnCheck);
		
		cellCheck = new JCheckBox("Cell Selection");
		cellCheck.setEnabled(false);
		cellCheck.addActionListener(tblTestActionListener);
		frame.getContentPane().add(cellCheck);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		
		output = new JTextArea();
		scrollPane.setViewportView(output);
		output.setTabSize(40);
		output.setRows(5);

		//--- Table customization
        //Set up column sizes.
        initColumnSizes(table);

        //Fiddle with the Sport column's cell editors/renderers.
        setUpSportColumn(table, table.getColumnModel().getColumn(3));
        
        //Set up stricter input validation for the integer column.
        table.setDefaultEditor(Integer.class,
                               new IntegerEditor(0, 100));
		
        //Set up renderer and editor for the Favorite Color column.
        table.setDefaultRenderer(Color.class,
                                 new ColorRenderer(true));
        table.setDefaultEditor(Color.class,
                               new ColorEditor());
	}
	
    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        MyTableModel model = (MyTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < 6; i++) {
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
 
            
            if (DEBUG) {
                output.append(String.format("Initializing width of column: %d, ", i));
                output.append(String.format("headerWidth = %d; ", headerWidth));
                output.append(String.format("cellWidth = %d\n", cellWidth));
            }
 
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    public void setUpSportColumn(JTable table,
            TableColumn sportColumn) {
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
    
	public void outputSelection() {
        output.append(String.format("Lead: %d, %d. ",
                table.getSelectionModel().getLeadSelectionIndex(),
                table.getColumnModel().getSelectionModel().
                    getLeadSelectionIndex()));
		
        output.append("Rows:");
        for (int c : table.getSelectedRows()) {
            output.append(String.format(" %d", c));
        }
        output.append(". Columns:");
        for (int c : table.getSelectedColumns()) {
            output.append(String.format(" %d", c));
        }
        output.append(".\n");
	}

}
