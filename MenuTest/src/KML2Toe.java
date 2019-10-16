import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JScrollPane;

public class KML2Toe {

	public class AboutDialog extends JDialog implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JButton okButton = new JButton("Close");

		public AboutDialog(Frame frame) {
			super(frame, "Info", true);

			JPanel panel = new JPanel();
	        panel.setBounds(0, 0, 300, 300);
	        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));	        
		    
			JLabel lblAuthor = new JLabel("Author: Jari Aaltonen");
			lblAuthor.setLabelFor(frmConvertKmlFile);
			lblAuthor.setFont(new Font("Tahoma", Font.PLAIN, 11));
//			lblAuthor.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(lblAuthor);
			
			JLabel lblVersion = new JLabel("Version: 1.0");
			lblVersion.setLabelFor(frmConvertKmlFile);
			lblVersion.setFont(new Font("Tahoma", Font.PLAIN, 11));
//			lblVersion.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(lblVersion);
		    
			Dimension minSize = new Dimension(5, 5);
			Dimension prefSize = new Dimension(5, 5);
			Dimension maxSize = new Dimension(Short.MAX_VALUE, 5);
			panel.add(new Box.Filler(minSize, prefSize, maxSize));
			
//			okButton.setAlignmentX(Component.BOTTOM_ALIGNMENT);
			
			JTextArea infoTextArea = new JTextArea();
			infoTextArea.append("This application converts a .KLM file into a .OSM file.\n");
			infoTextArea.append("Input file should be a .KML file contains a KML Polygon in XML format.\n");
			infoTextArea.append("Output file will be a corresponding XML file containing polygon in OSM format.\n");
			infoTextArea.append("\n");
			infoTextArea.append("Please be aware that:\n");
			infoTextArea.append("  KML files created with Google Earth are a derivative work from GE,\n");
			infoTextArea.append("  and as such, cannot be used in OSM.\n");
			infoTextArea.append("\n");
			infoTextArea.append("Please be absolutely sure that using those data in OSM is permitted by the author.\n");
			infoTextArea.append("If unsure, please seek advice on the \"legal\" or \"talk\" openstream mailing lists.\n");
			infoTextArea.setEditable(false);
			infoTextArea.setAlignmentX(LEFT_ALIGNMENT);
			panel.add(infoTextArea);
			
			panel.add(Box.createVerticalGlue());
		    panel.add(okButton);
		    getContentPane().add(panel);
		    okButton.addActionListener(this);
		    setPreferredSize(new Dimension(300, 200));
		    pack();
		    setLocationRelativeTo(frame);			
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			setVisible(false);
		}

	}

	public class Attribute {
		private String keyword;
		private String value;
		public Attribute() {
			keyword = null;
			value = null;
		}
		public Attribute(String keyword, String value) {
			this.keyword = keyword;
			this.value = value;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String toString() {
		    String s = keyword + "=\"" + value + "\"";
			return s;
		}
	}
	
	public class XMLNode {
		private String tag = null;
		private ArrayList<String> attributes = new ArrayList<String>();
		private boolean open = false;
		private String prefix = null;
		
		public XMLNode() {
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public ArrayList<String> getAttributes() {
			return attributes;
		}
		public void setAttributes(ArrayList<String> attributes) {
			this.attributes = attributes;
		}
		public boolean isOpen() {
			return open;
		}
		public void setOpen(boolean open) {
			this.open = open;
		}
		public String getPrefix() {
			return prefix;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
		public void addAttribute(Attribute a) {
			attributes.add(a.toString());
		}
		public void clearNode() {
			this.tag = "";
			this.open = false;
			this.setPrefix(null);
			this.attributes.clear();
		}
		public String toString() {
		    String s = null;
		    if (tag == null)
		    	return null;
		    //Start tag
		    s = "<" + tag;
		    
		    //attributes
		    for (int i=0; i < attributes.size(); i++) {
		    	s = s + " " + attributes.get(i);
		    }
		    
		    //end tag
		    if (this.isOpen()) {
		    	if (tag.startsWith("?"))
		    		s = s + "?";
			    s = s + ">";
		    }
		    else
			    s = s + "/>";
		    
			return s;
		}
		public String getLine() {
			String s = "";
			if (prefix != null)
				s = prefix;
			
		    s = s + this.toString();
		    s = s + "\n";
			return s;
		}
	}
	private JFrame frmConvertKmlFile;
	private JLabel lblStatus;
	private JLabel lblOsmfilename;
	private JLabel lblKmlfilename;
	private ArrayList<String> lat = new ArrayList<String>();
	private ArrayList<String> lon = new ArrayList<String>();
	private ArrayList< String> message = new ArrayList<String>();
	private File openFileDir = null;
	private File savedOsmFile = null;
	private File workDirectory = null;
	private JFileChooser fcOpen = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KML2Toe window = new KML2Toe();
					window.frmConvertKmlFile.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public KML2Toe() {
		workDirectory = restoreLocation();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmConvertKmlFile = new JFrame();
		frmConvertKmlFile.setTitle("Convert KML FILE to OSM File");
		frmConvertKmlFile.setBounds(100, 100, 900, 700);
		//frmConvertKmlFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmConvertKmlFile.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		
		frmConvertKmlFile.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
            	endingOperations();
            }
        });
		
		if (workDirectory == null) {
       		fcOpen = new JFileChooser();                 // first call
       		fcOpen.setCurrentDirectory(new File("."));
		}
        else {
        	fcOpen = new JFileChooser(workDirectory);    // last directory saved        
        }
        
        fcOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fcOpen.addChoosableFileFilter(new FileNameExtensionFilter("KML Documents", "kml"));
        fcOpen.setAcceptAllFileFilterUsed(true);        
        
        //Create a file chooser for save
        JFileChooser fcSave = new JFileChooser() {
			private static final long serialVersionUID = 1L;
			public void approveSelection() {
                File f = getSelectedFile();
                if (f.exists() && getDialogType() == SAVE_DIALOG) {
                    int result = JOptionPane.showConfirmDialog(this,
                            "The file exists, overwrite?", "Existing file",
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    switch (result) {
                    case JOptionPane.YES_OPTION:
                        super.approveSelection();
                        return;
                    case JOptionPane.CANCEL_OPTION:
                    	super.cancelSelection();
                        return;
                    default:
                        return;
                    }
                }
                super.approveSelection();
            }
        };
        
        fcSave.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fcSave.addChoosableFileFilter(new FileNameExtensionFilter("OSM Documents", "osm"));
        fcSave.setAcceptAllFileFilterUsed(true);        
        
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{110, 304, 0};
		gridBagLayout.rowHeights = new int[]{21, 14, 14, 0, 143, 14, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		frmConvertKmlFile.getContentPane().setLayout(gridBagLayout);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setMinimumSize(new Dimension(110, 30));
		GridBagConstraints gbc_menuBar = new GridBagConstraints();
		gbc_menuBar.anchor = GridBagConstraints.NORTH;
		gbc_menuBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_menuBar.insets = new Insets(0, 0, 5, 5);
		gbc_menuBar.gridx = 0;
		gbc_menuBar.gridy = 0;
		frmConvertKmlFile.getContentPane().add(menuBar, gbc_menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnFile.add(mntmSaveAs);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	endingOperations();
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		JMenuItem mntmInfo = new JMenuItem("Info");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 AboutDialog dialog = new AboutDialog(frmConvertKmlFile);
				 dialog.setSize(500,300);
				 Point p = frmConvertKmlFile.getLocation();
				 p.x = p.x + 108;
				 p.y = p.y + 98;
				 dialog.setLocation(p);
				 dialog.setModal(true);
				 dialog.setVisible(true);
			}
		});
		mnAbout.add(mntmInfo);
		
		
		JLabel lblInputFile = new JLabel("Input file:");
		lblInputFile.setLabelFor(frmConvertKmlFile);
		lblInputFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblInputFile = new GridBagConstraints();
		gbc_lblInputFile.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblInputFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblInputFile.gridx = 0;
		gbc_lblInputFile.gridy = 1;
		frmConvertKmlFile.getContentPane().add(lblInputFile, gbc_lblInputFile);
		
		lblKmlfilename = new JLabel("");
		lblKmlfilename.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblKmlfilename = new GridBagConstraints();
		gbc_lblKmlfilename.anchor = GridBagConstraints.NORTH;
		gbc_lblKmlfilename.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblKmlfilename.insets = new Insets(0, 0, 5, 0);
		gbc_lblKmlfilename.gridx = 1;
		gbc_lblKmlfilename.gridy = 1;
		frmConvertKmlFile.getContentPane().add(lblKmlfilename, gbc_lblKmlfilename);
		
		JLabel lblOutputFile = new JLabel("Output file:");
		lblOutputFile.setLabelFor(frmConvertKmlFile);
		lblOutputFile.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblOutputFile = new GridBagConstraints();
		gbc_lblOutputFile.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblOutputFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutputFile.gridx = 0;
		gbc_lblOutputFile.gridy = 2;
		frmConvertKmlFile.getContentPane().add(lblOutputFile, gbc_lblOutputFile);
		
		lblOsmfilename = new JLabel("");
		lblOsmfilename.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_lblOsmfilename = new GridBagConstraints();
		gbc_lblOsmfilename.anchor = GridBagConstraints.NORTH;
		gbc_lblOsmfilename.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblOsmfilename.insets = new Insets(0, 0, 5, 0);
		gbc_lblOsmfilename.gridx = 1;
		gbc_lblOsmfilename.gridy = 2;
		frmConvertKmlFile.getContentPane().add(lblOsmfilename, gbc_lblOsmfilename);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		frmConvertKmlFile.getContentPane().add(scrollPane, gbc_scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		lblStatus = new JLabel("Status");
		GridBagConstraints gbc_lblStatus = new GridBagConstraints();
		gbc_lblStatus.anchor = GridBagConstraints.NORTH;
		gbc_lblStatus.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblStatus.gridwidth = 2;
		gbc_lblStatus.gridx = 0;
		gbc_lblStatus.gridy = 5;
		frmConvertKmlFile.getContentPane().add(lblStatus, gbc_lblStatus);

		//Add listener for open menu
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmOpen) {
					int returnVal = fcOpen.showOpenDialog(mntmOpen); 
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fcOpen.getSelectedFile();
						workDirectory = file.getParentFile();
						
						fcOpen.setCurrentDirectory(file);
						openFileDir = fcOpen.getCurrentDirectory();
						if (file.getName().toLowerCase().endsWith(".kml")) {
							String pathname = file.getPath();
							lblStatus.setText("Opening: " + pathname + ".");
							lblKmlfilename.setText(pathname);
							String sCoordinates = readFile(file);
							parseCoordinates(sCoordinates.trim()); //Strip leading & trailing white spaces
							
							//Make Osm structure
							constructOsmStructure(message, lat, lon);

							//Populate text area
							for (int i = 0; i < message.size(); i++) 
								textArea.append(message.get(i));
							
							//Output file name
							String[] fileNameParts = pathname.toLowerCase().split("\\.kml", 2);
							String osmFilePathName = fileNameParts[0] + ".osm"; 
							lblOsmfilename.setText(osmFilePathName);
							//Save osmFilePathName here
							savedOsmFile = SaveFile(osmFilePathName, message);
						} else {
							lblStatus.setText("File: " + file.getName() + " is not a KML file.");
							lblKmlfilename.setText("");
							lblOsmfilename.setText("");
						}
		                //This is where a real application would open the file.
		            } else {
		                //log.append("Open command cancelled by user." + newline);
						lblStatus.setText("Open command cancelled by user.");
					}
				}
			}
		});
		//Add listener for save menu
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmSaveAs) {
					fcSave.setCurrentDirectory(openFileDir);
					fcSave.setSelectedFile(savedOsmFile);
		            int returnVal = fcSave.showSaveDialog(mntmSaveAs);
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fcSave.getSelectedFile();
		                String pathname = file.getPath();
		                
	        			//if savedOsmFile in exists, temove it
		                if (savedOsmFile != null) {
			        		try {
			        			if (savedOsmFile.exists()) {
			        				savedOsmFile.delete();    
			        		    }
			        		} catch (Exception ex) {
			        	           //do stuff with exception
			        				ex.printStackTrace();
			        		}
							lblStatus.setText("Saving: " + pathname + ".");
							lblOsmfilename.setText(pathname);
			                //This is where a real application would save the file.
							SaveFile(pathname, message);
		                } else {
							lblStatus.setText("KML file not opened, OSM file not found.");
		                	
		                }
		            } else {
						lblStatus.setText("Save command cancelled by user.");
		            }
		        }
			}
		});
	}
	private String readFile(File file) {
		Scanner scanner = null;
		String sCoordinates = null;

		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			while (scanner.hasNextLine()) {
				if (scanner.nextLine().contains("<coordinates>")) {
					sCoordinates = scanner.nextLine();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
		
		return sCoordinates;
	}
	
	private void parseCoordinates(String sCoordinates) {
		//Halkaistaan sCoordinates
		String[] coordinateParts = sCoordinates.split(" ");
		for (int i = 0; i < coordinateParts.length; i++) {
			String[] coordinates = coordinateParts[i].split(",");
			lon.add(coordinates[0]);
			lat.add(coordinates[1]);
		}
		return;
	}
	
	private File SaveFile(String fileName, ArrayList< String> message) {
	    File file = new File( fileName );
		try {
			//Remove file if exists
			if (file.exists()) {
				file.delete();    
		    }
			
			file.createNewFile();
	        FileWriter fw = new FileWriter(file, true);

	        BufferedWriter bw = new BufferedWriter(fw);	

	        for (int i = 0; i < message.size(); i++) {

	            bw.write(message.get(i));
	            //bw.newLine();
	        }

	        bw.close();
	        fw.close();	        
		} catch (IOException e) {
	           //do stuff with exception
				e.printStackTrace();
		}
		return file;
	}
	private void constructOsmStructure(ArrayList<String> msgLines,
			ArrayList<String> latitudes, ArrayList<String> longitudes) {
		XMLNode osmLine = new XMLNode();
		Attribute attr = new Attribute();
		String timestamp = getTimeStamp(); 
		Integer nodeID = 0;
		
		//Vakio <?xml version="1.0" encoding="UTF-8"?>
		osmLine.setTag("?xml");
		osmLine.setOpen(true);
		attr.setKeyword("version");
		attr.setValue("1.0");
		osmLine.addAttribute(attr);
		attr.setKeyword("encoding");
		attr.setValue("UTF-8");
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();
		
		//Vakio <osm version="0.6" generator="Online area editor 0.1.20130730">
		osmLine.setTag("osm");
		osmLine.setOpen(true);
		attr.setKeyword("version");
		attr.setValue("0.6");
		osmLine.addAttribute(attr);
		attr.setKeyword("generator");
		attr.setValue("Online area editor 0.1.20130730");
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();

		//<node id="-1" visible="true" timestamp="2016-09-27T15:55:23Z" lat="62.0083652" lon="21.6347081" version="1"/>
		for (int i = 0; i < latitudes.size(); i++) {
			osmLine.setPrefix("  "); // yksi sisennys
			osmLine.setTag("node");
			attr.setKeyword("id");
			nodeID = -1*(i+1);
			attr.setValue(nodeID.toString());
			osmLine.addAttribute(attr);
			attr.setKeyword("visible");
			attr.setValue("true");
			osmLine.addAttribute(attr);
			attr.setKeyword("timestamp");
			attr.setValue(timestamp);
			osmLine.addAttribute(attr);
			attr.setKeyword("lat");
			attr.setValue(latitudes.get(i));
			osmLine.addAttribute(attr);
			attr.setKeyword("lon");
			attr.setValue(longitudes.get(i));
			osmLine.addAttribute(attr);
			attr.setKeyword("version");
			attr.setValue("1");
			osmLine.addAttribute(attr);
			msgLines.add(osmLine.getLine());
			osmLine.clearNode();
		}

		// <way timestamp="2016-09-27T15:55:23Z" id="-419" visible="true" version="1">
		osmLine.setTag("way");
		osmLine.setOpen(true);
		osmLine.setPrefix("  "); // yksi sisennys

		attr.setKeyword("timestamp");
		attr.setValue(timestamp);
		osmLine.addAttribute(attr);

		attr.setKeyword("id");
		nodeID = nodeID - 1;
		attr.setValue(nodeID.toString());
		osmLine.addAttribute(attr);
		
		attr.setKeyword("visible");
		attr.setValue("true");
		osmLine.addAttribute(attr);

		attr.setKeyword("version");
		attr.setValue("1");
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();

		//  <tag k="name" v="Honkajoki (38950) Tarkistettu"/>
		osmLine.setTag("tag");
		osmLine.setPrefix("    "); // kaksi sisennystä

		attr.setKeyword("k");
		attr.setValue("name");
		osmLine.addAttribute(attr);

		attr.setKeyword("v");
		attr.setValue("Honkajoki (38950) Tarkistettu");
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();
		
		//  <tag k="number" v="38950"/>
		osmLine.setTag("tag");
		osmLine.setPrefix("    "); // kaksi sisennystä

		attr.setKeyword("k");
		attr.setValue("number");
		osmLine.addAttribute(attr);

		attr.setKeyword("v");
		attr.setValue("38950");
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();
		
		//<nd ref="-1"/>
		for (int i = 0; i < latitudes.size(); i++) {
			osmLine.setTag("nd");
			osmLine.setPrefix("    "); // kaksi sisennystä

			attr.setKeyword("ref");
			nodeID = -1*(i+1);
			attr.setValue(nodeID.toString());
			osmLine.addAttribute(attr);
			msgLines.add(osmLine.getLine());
			osmLine.clearNode();
		}

		osmLine.setTag("nd");
		osmLine.setPrefix("    "); // kaksi sisennystä

		//<nd ref="-1"/>
		attr.setKeyword("ref");
		nodeID = -1;
		attr.setValue(nodeID.toString());
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();

		//<tag k="area" v="yes"/>
		osmLine.setTag("tag");
		osmLine.setPrefix("    "); // kaksi sisennystä

		attr.setKeyword("k");
		attr.setValue("area");
		osmLine.addAttribute(attr);

		attr.setKeyword("v");
		attr.setValue("yes");
		osmLine.addAttribute(attr);
		msgLines.add(osmLine.getLine());
		osmLine.clearNode();
		
	    //</way>
		msgLines.add("  </way>\n");
	    //</osm>
		msgLines.add("</osm>\n");
		
		return;
	}
	private String getTimeStamp() {
		String t = null;
		Date now = new Date();

		// create time zone object     
		TimeZone timezone = TimeZone.getDefault();
		   
        // Get calendar set to current date and time
        Calendar cal = Calendar.getInstance(timezone);  
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        dateFormat.setCalendar(cal);  //explicitly set the calendar into the date formatter

        t = dateFormat.format(now);
		return t;
		
	}
	private void storeLocation(File directory) {
		try {
			FileOutputStream fos = new FileOutputStream("LastCall.txt");
  	      	ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(directory);
			oos.close();
		}
		catch(Exception e) {
		}
	}
	private File restoreLocation() {
		File directory = null;   // init to null
		try {
		      FileInputStream fis = new FileInputStream("LastCall.txt");
		      ObjectInputStream ois = new ObjectInputStream(fis);
			  directory = (File) ois.readObject();
			  ois.close();
		}
		catch (Exception e) { // OK no file
		}
		return directory;
	}
	private void endingOperations() {
        try {
            storeLocation(workDirectory);
        } catch(Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
	}
}
