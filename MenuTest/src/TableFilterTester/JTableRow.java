package TableFilterTester;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class JTableRow {

	private class RowElem {
		private String iD;
		private String firstName;
		private String lastName;
		private int years;
		private String sport;
		private boolean veggie;
		
		public RowElem() {
			this.iD = "";
			this.firstName = "";
			this.lastName = "";
			this.years = 0;
			this.sport = "";
			this.veggie = false;
		}

		public RowElem(String iD, String firstName, String lastName, String sport, int years, boolean veggie) {
			this.iD = iD;
			this.firstName = firstName;
			this.lastName = lastName;
			this.sport = sport;
			this.years = years;
			this.veggie = veggie;
		}

		public String getID() {
			return iD;
		}
		public void setID(String iD) {
			this.iD = iD;
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
	
	public class MyTableModel extends AbstractTableModel {

		private String[] columnNames = {"ID",
				"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};

		public final Object[] longValues = {"III/123", "Jane", "Kathy", 
	                "None of the above",
	                new Integer(20), Boolean.TRUE};

	    // create an array list
		private ArrayList<RowElem> dataElements;
		
		public MyTableModel() {
			dataElements = new ArrayList<RowElem>(); 
					
		}
		
		public MyTableModel(ArrayList<RowElem> dataElements)
	    {
	        this.dataElements = dataElements;
	    }
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public int getRowCount() {
			return dataElements.size();
		}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return getValueAt(0, arg0).getClass();
		}
		
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (arg1 < 3) {
                return false;
            } else {
                return true;
            }			
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			RowElem rowElement = getRowElement(rowIndex);
		    
		    switch (columnIndex)
		    {
		    	case 0: return rowElement.getID();
		        case 1: return rowElement.getFirstName();
		        case 2: return rowElement.getLastName();
		        case 3: return rowElement.getSport();
	            case 4: return rowElement.getYears();
	            case 5: return rowElement.isVeggie();
	            default: return null; 
		    }			
		}

		@Override
		public void setValueAt(Object value, int row, int col) {
			RowElem rowElement = getRowElement(row);
		    
		    switch (col)
		    {
		    	case 0: rowElement.setID((String)value); break;
		        case 1: rowElement.setFirstName((String)value); break;
		        case 2: rowElement.setLastName((String)value); break;
		        case 3: rowElement.setSport((String)value); break;
	            case 4: rowElement.setYears((int)value); break;
	            case 5: rowElement.setVeggie((boolean)value); break;
		    }
		 
		    modelToRow(row, rowElement);
		    fireTableCellUpdated(row, col);
		}
		
		//Implement custom methods
		public RowElem getRowElement(int row)
		{
		    return dataElements.get( row );
		}
		
		public void addRow(RowElem rowElement)
		{
			//Insert at the tail of the list
		    insertElement(getRowCount(), rowElement);
		}
		 
		public void insertElement(int row, RowElem rowElement)
		{
			dataElements.add(row, rowElement);
		    fireTableRowsInserted(row, row);
		}
		public void removeRow(int row)
		{
			dataElements.remove(row);
			fireTableRowsDeleted(row, row);
		}
		
		public void updateRow(int row, RowElem rowElement)
		{
			dataElements.set(row, rowElement);
			fireTableRowsUpdated(row, row);
		}
	}
	
	private JFrame frame;
	private JTable table;
	private TableRowSorter<MyTableModel> sorter;
	
	private JTextField textID;
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
					JTableRow window = new JTableRow();
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
	public JTableRow() {
		initialize();
	}

	private void initializeTable() {
	    // create an array list
	    ArrayList<RowElem> dataElements = new ArrayList<>();

	    RowElem dataRowElem = new RowElem("I/1", "Kathy", "Smith", "Snowboarding", 
				new Integer(5), new Boolean(false));
		dataElements.add(dataRowElem);

		RowElem dataRowElem2 = new RowElem("I/2", "John", "Doe", "Rowing", 
				new Integer(3), new Boolean(true));
		dataElements.add(dataRowElem2);
		
		RowElem dataRowElem3 = new RowElem("I/10", "Sue", "Black", "Knitting", 
				new Integer(2), new Boolean(false));
		dataElements.add(dataRowElem3);
		
		RowElem dataRowElem4 = new RowElem("I/11", "Jane", "White", "Speed reading", 
				new Integer(20), new Boolean(true));
		dataElements.add(dataRowElem4);
		
		RowElem dataRowElem5 = new RowElem("II/1", "Joe", "Brown", "Pool", 
				new Integer(10), new Boolean(false));
		dataElements.add(dataRowElem5);

		model = new MyTableModel(dataElements);
        sorter = new TableRowSorter<MyTableModel>(model);
        
        Comparator<String> idComparator = new Comparator<String>()
        {
    		@Override
    		public int compare(String arg0, String arg1) {
    			int card1 = cardIdToInteger(arg0, "##/999");
    			int card2 = cardIdToInteger(arg1, "##/999");
    			return card1 - card2;
    		}
        };
        sorter.setComparator(0, idComparator);

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
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		initializeTable();
		
		
		//Set up table & scroll pane
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(table);
		
        //Set up widgets
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		textID = new JTextField();
		panel.add(textID);
		textID.setColumns(10);

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
    			
    			editedRow.setID(textID.getText());
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
         	        
                     // add row to the model
                     model.addRow(editedRow);
                     table.clearSelection();
                 }
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
    			
              	editedRow.setID(textID.getText());
              	editedRow.setFirstName(textFirstName.getText());
            	editedRow.setLastName(textLastName.getText());
            	editedRow.setSport(comboSport.getItemAt(comboSport.getSelectedIndex()));
            	int iYears = Integer.parseInt(textYears.getText());
            	editedRow.setYears(iYears);
            	editedRow.setVeggie(chkVeggie.isSelected());
      			int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                     //int modelRow = table.convertRowIndexToModel(viewRow);
                     model.updateRow(viewRow, editedRow);
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
        setUpSportColumn(table, table.getColumnModel().getColumn(3));

        //Testaus
        /*
		try {
	        tester();
		} catch (Exception e) {
			e.printStackTrace();
		}
        */
		
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
            int cellHeight = comp.getPreferredSize().height;
            
            int cellWidth1 = Math.max(headerWidth, cellWidth);
            column.setPreferredWidth(cellWidth1);
            
            switch (i) {
            case 0:
            	textID.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 1:
            	textFirstName.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 2:
            	textLastName.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 3:
            	comboSport.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 4:
            	textYears.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            case 5:
            	chkVeggie.setPreferredSize(new Dimension(cellWidth1, cellHeight));
            	break;
            }
        }
        
	}

	private void modelToRow(int row, RowElem elemRow) {
        	
		elemRow.setID((String) model.getValueAt(row, 0));
		textID.setText(elemRow.getID());
		
		elemRow.setFirstName((String) model.getValueAt(row, 1));
        textFirstName.setText(elemRow.getFirstName());

        elemRow.setLastName((String) model.getValueAt(row, 2));
        textLastName.setText(elemRow.getLastName());
        
        elemRow.setYears( (int)model.getValueAt(row, 4));
        Integer iYears = elemRow.getYears();
        textYears.setText(iYears.toString());
        
        elemRow.setSport((String) model.getValueAt(row, 3));
        int anIndex = 0;
        for(int i = 0; i < comboSport.getItemCount(); i++) {
        	if(comboSport.getItemAt(i).equals(elemRow.getSport())){
        		anIndex = i;
        		break;
        	}     
        }       
        comboSport.setSelectedIndex(anIndex);

        elemRow.setVeggie((boolean) model.getValueAt(row, 5));
        chkVeggie.setSelected(elemRow.isVeggie());
        
       	return;
    }

	private void tester() {
		int card = 0;

        //Virheellinen
        try {
	        card = cardIdToInteger("AA23", "#999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("93", "#999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("A+993", "#999");
		} catch (Exception e) {
			e.printStackTrace();
		}

        try {
	        card = cardIdToInteger("+23", "#999");
		} catch (Exception e) {
			e.printStackTrace();
		}

        card = cardIdToInteger("D/3", "#/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("C/30", "#/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("B/302", "#/999");
        System.out.print("Kortti: " + card + "\n");

        card = cardIdToInteger("1/1", "99/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("3/10", "99/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("2/102", "99/999");
        System.out.print("Kortti: " + card + "\n");

        card = cardIdToInteger("1/1", "9/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("3/10", "9/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("2/102", "9/999");
        System.out.print("Kortti: " + card + "\n");

        card = cardIdToInteger("D3", "#999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("C30", "#999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("B302", "#999");
        System.out.print("Kortti: " + card + "\n");

        card = cardIdToInteger("3", "999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("30", "999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("302", "999");
        System.out.print("Kortti: " + card + "\n");

        //Test 1
		card = cardIdToInteger("IV/2", "##/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("III/20", "##/999");
        System.out.print("Kortti: " + card + "\n");
        card = cardIdToInteger("II/202", "##/999");
        System.out.print("Kortti: " + card + "\n");

        //Virheellinen
		try {
	        card = cardIdToInteger("D3", "#/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
	        card = cardIdToInteger("A/23", "##/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("D/3", "##/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("I/E", "##/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("I/4567", "##/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("I/A33", "##/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("I/456", "##/9999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("I/33", "###/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("I/33", "##/998");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("I/33", "#&/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//testi 2
		try {
	        card = cardIdToInteger("2/10", "999/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("I/10", "9/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("123/10", "9/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("-1/10", "9/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
	        card = cardIdToInteger("2/10", "8/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

        try {
	        card = cardIdToInteger("AA/23", "#/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("9/3", "#/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
	        card = cardIdToInteger("99/3", "#/999");
		} catch (Exception e) {
			e.printStackTrace();
		}

        try {
	        card = cardIdToInteger("+/23", "#/999");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * Tarkoitus: 	Kaksiosaisen tunnuksen muunto kokonaisluku muotoilun mukaisesti
	 * Parametrit: 	String cardId	Korttitunnus
	 * 				String idFormat	Tunnuksen muotoilu
	 * 					Arvot: 	"##/999î - Erotinmerkki "/", alkuosa roomalainen numero (< 100)
	 * 		 					î#/999î  - Erotinmerkki "/", alkuosa kirjain 'A'-'Z'
	 *  	  					î99/999î - Erotinmerkki "/", alkuosa numeroita '0'-'9'
	 * 		  					î9/999î  - Sama kuin î99/999î  
	 * 		  					î#999î   - Alkuosa kirjain 'A'-'Z'
	 * 		  					î999î    - Ei alkuosaa
	 * 					Tunnuksen loppuosa 999 on aina kokonaisluku v‰lilt‰ 0...999.
     * Paluuarvo: 	Alkuosa * 1000 + loppuosa (int)
	 */
	private int cardIdToInteger(String cardId, String idFormat) throws IllegalArgumentException {
		int idNumberLeft = 0;
		int idNumberRight = 0;
		int idNumber = 0;
		String idFormatLeft = ""; 
		String idFormatRight = ""; 
		String cardIdLeft = ""; 
		String cardIdRight = ""; 

		/* K‰sitell‰‰n merkkijonon alkuosa */
		//Jos idFormat sis‰lt‰‰ merkin í/í,
		if (idFormat.contains("/")) {
			//Tarkastetaan, ett‰ myˆs cardId sis‰lt‰‰ merkin í/í. Ellei, virhe.
			if (!cardId.contains("/")) {
				throw new IllegalArgumentException("String not in correct format");
			}
			//Halkaistaan idFormat ja cardId kahteen osaan
			String[] idFormatParts = idFormat.split("/", 2);
			idFormatLeft = idFormatParts[0]; 
			idFormatRight = idFormatParts[1]; 
			String[] cardIdParts = cardId.split("/", 2);
			cardIdLeft = cardIdParts[0]; 
			cardIdRight = cardIdParts[1]; 

			//Jos idFormatLeft alkaa merkill‰ í#í,
			if (idFormatLeft.startsWith("#")) {
				//Jos idFormatLeft pituus > 1,
				if (idFormatLeft.length() > 1) {
					//Tarkista, ett‰ idFormatLeft sis‰lt‰‰ vain merkki‰ í#í. Ellei, virhe.
					if (!idFormatLeft.matches("[#]{1,2}")) {
						throw new IllegalArgumentException("String not in correct format");
					} 
					//Muuntuuko cardIdLeft roomalaiseksi numeroksi:
					//Asetetaan idNumberLeft = romanToInteger(idFormatLeft)
					idNumberLeft = convertRomanToArabic(cardIdLeft);
					//Jos idNumberLeft < 1, virhe. (cardIdLeft ei roomalainen)
					//Jos idNumberLeft > 99, virhe. Merkkijonon alkuosa edustaa max. 2 merkki‰.
					if ((idNumberLeft < 1) || (idNumberLeft > 99)) {
						throw new IllegalArgumentException("String not in correct format");
					}
				} else {  // muuten (idFormatLeft pituus = 1)
					//Tarkista, ett‰ cardIdLeft sis‰lt‰‰ vain yhden merkin v‰lilt‰ íAí-íZí. Ellei, virhe.
					if (!cardIdLeft.matches("[A-Z]{1}")) {
						throw new IllegalArgumentException("String not in correct format");
					} 
					//Aseta idNumberLeft = cardIdLeft(0) ñ íAí + 1
					idNumberLeft = (int)(cardIdLeft.charAt(0) - 'A') + 1;
				} 
			} // end (jos idFormatLeft alkaa merkill‰ í#í)
			else if (idFormatLeft.startsWith("9")) { //muuten jos idFormatLeft alkaa merkill‰ í9í,
				// Tarkista, ett‰ idFormatLeft sis‰lt‰‰ vain merkki‰ í9í ja pituus v‰lill‰ 1 .. 2. Ellei, virhe
				if (!idFormatLeft.matches("[9]{1,2}")) {
					throw new IllegalArgumentException("String not in correct format");
				}
				// Tarkista, ett‰ cardIdLeft sis‰lt‰‰ vain merkkej‰ v‰lilt‰ í0í-í9í ja pituus v‰lill‰ 1 .. 2.
				if (!cardIdLeft.matches("[0-9]{1,2}")) {
					throw new IllegalArgumentException("String not in correct format");
				}
				// aseta idNumberLeft = parseInt(cardIdLeft)
				idNumberLeft = Integer.parseInt(cardIdLeft);
			} // end (jos idFormatLeft alkaa merkill‰ í9í)
			else { // muuten virhe
				throw new IllegalArgumentException("String not in correct format");
			}
		} //end (jos idFormat sis‰lt‰‰ merkin í/í)
		else {
			// Jos idFormat alkaa merkill‰ í#í,
			if (idFormat.startsWith("#")) {
				//Halkaistaan cardId kahteen osaan siten, ett‰ cardIdLeft 
				//sis‰lt‰‰ 1. merkin ja cardIdRight loput
				cardIdLeft = cardId.substring(0, 1);
				cardIdRight = cardId.substring(1);
				//Aseta idFormatRight = idFormat, josta on poistettu 1. merkki. 
				idFormatRight = idFormat.substring(1);
				//Tarkista, ett‰ cardIdLeft sis‰lt‰m‰ merkki on v‰lilt‰ íAí-íZí. Ellei, virhe.
				if (!cardIdLeft.matches("[A-Z]")) {
					throw new IllegalArgumentException("String not in correct format");
				} 
				//Aseta idNumberLeft = cardIdLeft(0) ñ íAí + 1
				idNumberLeft = (int)(cardIdLeft.charAt(0) - 'A') + 1;
			}
			else {
				//aseta idFormatRight = idFormat, cardIdRight = cardId, idNumberLeft = 0
				idFormatRight = idFormat;
				cardIdRight = cardId;
				idNumberLeft = 0;
			}
		}
		/* K‰sitell‰‰n merkkijonon loppuosa: */
	
		//Tarkista, ett‰ idFormatRight sis‰lt‰‰ vain merkki‰ í9í ja pituus v‰lill‰ 1 .. 3. Ellei, virhe.
		if (!idFormatRight.matches("[9]{1,3}")) {
			throw new IllegalArgumentException("String not in correct format");
		}
		//Tarkista, ett‰ cardIdRight sis‰lt‰‰ vain merkkej‰ v‰lilt‰ í0í-í9í ja pituus v‰lill‰ 1 .. 3. Ellei, virhe.
		if (!cardIdRight.matches("[0-9]{1,3}")) {
			throw new IllegalArgumentException("String not in correct format");
		}
		//Aseta idNumberRight = parseInt(idFormatRight). Jos muunnos ep‰onniostuu, lopetetaan.
		idNumberRight = Integer.parseInt(cardIdRight);
		
		//Asetetaan idNumber = idNumberLeft * 1000 + idNumberRight
		idNumber = idNumberLeft * 1000 + idNumberRight;	
		return idNumber;
	}
	
	public int convertRomanToArabic (String roman) {
		Hashtable<Character, Integer> ht = new Hashtable<Character, Integer>();
	    ht.put('I',1);
	    ht.put('X',10);
	    ht.put('C',100);
	    ht.put('M',1000);
	    ht.put('V',5);
	    ht.put('L',50);
	    ht.put('D',500);
        int decimal = 0;
        int romanNumber = 0;
        int prev = 0;

        String regex = "^(M{0,4})(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";
	    if (roman.matches(regex)) {
	        for (int i = roman.length()-1; i >= 0; i--){
	            romanNumber = ht.get(roman.charAt(i));
	            if(romanNumber < decimal && romanNumber != prev ){
	                decimal -= romanNumber;
	                prev = romanNumber;
	            } else {
	                decimal += romanNumber;
	                prev = romanNumber;
	            }
	        } 
	    }
        return decimal;
    }
}

