package TableFilterTester;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import menutester.JmenuTest.MyTable;
import menutester.JmenuTest.MyTableModel;
import menutester.JmenuTest.RowListener;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import net.miginfocom.swing.MigLayout;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.GroupLayout.Alignment;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;

public class TableFilterTest {

	public class TxtDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			newFilter();

		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			newFilter();

		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			newFilter();

		}
	    /** 
	     * Update the row filter regular expression from the expression in
	     * the text box.
	     */
	    private void newFilter() {
	        RowFilter<MyTableModel, Object> rf = null;
	        //If current expression doesn't parse, don't update.
	        try {
	            rf = RowFilter.regexFilter(textFilter.getText(), 0);
	        } catch (java.util.regex.PatternSyntaxException e) {
	            return;
	        }
	        sorter.setRowFilter(rf);
	    }

	}

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
             }
		}

	}

	public class MyTableModel implements TableModel {
		private String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};
		
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
			return data.length;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return data[arg0][arg1];
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
            data[row][col] = value;
            fireTableCellUpdated(row, col);
		}

		private void fireTableCellUpdated(int row, int col) {
			// TODO Auto-generated method stub
			
		}

	}

	private JFrame frame;
	private JScrollPane scrollPane;
	private JPanel form1;
	private JLabel lblStatus;
	private JTextField textFilter;
	private JLabel lblFilterText;
	private JTextField textStatus;
	private JTable table;
	private JPanel form2;
	private Component rigidArea;
	private Component rigidArea_1;
	private TableRowSorter<MyTableModel> sorter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TableFilterTest window = new TableFilterTest();
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
	public TableFilterTest() {
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
		
	    //Create a table with a sorter.
        MyTableModel model = new MyTableModel();
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
		scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(table);
		
        //Set up widgets
		form1 = new JPanel();
		frame.getContentPane().add(form1);
		form1.setLayout(new BoxLayout(form1, BoxLayout.X_AXIS));
		
		lblFilterText = new JLabel("Filter Text:");
		lblFilterText.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblFilterText.setHorizontalAlignment(SwingConstants.TRAILING);
		form1.add(lblFilterText);
		
		rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		form1.add(rigidArea_1);
		
		textFilter = new JTextField();
		textFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
		form1.add(textFilter);
		textFilter.setColumns(10);
		
        //Whenever filterText changes, invoke newFilter.
		textFilter.getDocument().addDocumentListener(new TxtDocumentListener());
		
		form2 = new JPanel();
		frame.getContentPane().add(form2);
		form2.setLayout(new BoxLayout(form2, BoxLayout.X_AXIS));
		
		lblStatus = new JLabel("Status:");
		lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);
		form2.add(lblStatus);
		lblStatus.setHorizontalAlignment(SwingConstants.TRAILING);
		
		rigidArea = Box.createRigidArea(new Dimension(38, 20));
		form2.add(rigidArea);
		
		textStatus = new JTextField();
		textStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
		form2.add(textStatus);
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
 
            
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
	}

}
