import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
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
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ToeXmlEditor {

	private JFrame frame;
	private File workDirectory = null;
	private JFileChooser fcOpen = null;
	private JFileChooser fcSave = null;
	private File openFileDir = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ToeXmlEditor window = new ToeXmlEditor();
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
	public ToeXmlEditor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Toe XML node editor");
		frame.setBounds(100, 100, 450, 300);
		//frame.setBounds(100, 100, 900, 700); // suurempi
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		

		frame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
            	endingOperations();
            }
        });
		
		workDirectory = restoreLocation();		
		if (workDirectory == null) {
       		fcOpen = new JFileChooser();                 // first call
       		fcOpen.setCurrentDirectory(new File("."));
		}
        else {
        	fcOpen = new JFileChooser(workDirectory);    // last directory saved        
        }
		
        fcOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fcOpen.addChoosableFileFilter(new FileNameExtensionFilter("KML Documents", "kml"));
        fcOpen.addChoosableFileFilter(new FileNameExtensionFilter("OSM Documents", "osm"));
        fcOpen.setAcceptAllFileFilterUsed(true);        

        //Create a file chooser for save
        fcSave = new JFileChooser() {
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

		if (workDirectory == null) {
			fcSave.setCurrentDirectory(new File("."));
		}
        else {
        	fcSave.setCurrentDirectory(workDirectory);
        }
        
        fcSave.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fcSave.addChoosableFileFilter(new FileNameExtensionFilter("OSM Documents", "osm"));
        fcSave.setAcceptAllFileFilterUsed(true);        
        
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnFile.add(mntmSaveAs);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmConvertNodes = new JMenuItem("Convert Nodes");
		mnEdit.add(mntmConvertNodes);
		
		JMenuItem mntmReverseNodes = new JMenuItem("Reverse Nodes");
		mnEdit.add(mntmReverseNodes);
		
		JMenuItem mntmClear = new JMenuItem("Clear");
		mnEdit.add(mntmClear);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JLabel lblNewLabel = new JLabel("XML Nodes");
		frame.getContentPane().add(lblNewLabel, BorderLayout.NORTH);

		JLabel lblStatusLabel = new JLabel("Status: ");
		frame.getContentPane().add(lblStatusLabel, BorderLayout.SOUTH);
		
		//Add listener for open menu
		mntmOpen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmOpen) {
	        		//Empty the text area
					textArea.setText(null);

					int returnVal = fcOpen.showOpenDialog(mntmOpen); 
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fcOpen.getSelectedFile();
						workDirectory = file.getParentFile();
						
						fcOpen.setCurrentDirectory(file);
						openFileDir = fcOpen.getCurrentDirectory();
						
						if (file.getName().toLowerCase().endsWith(".kml")) {
							//String inputFileName = file.getName();
							String pathname = file.getPath();
							lblStatusLabel.setText("Opening: " + pathname + ".");

							//Read file into a string
							String sCoordinates = readKMLFile(file);

							//Create XML tree
							OsmXmlTree osmTree = new OsmXmlTree();
							String osmXMLstring = osmTree.parseCoordinates(sCoordinates.trim()); //Strip leading & trailing white spaces
							osmTree.setInputXml(osmXMLstring);

							osmTree.xmlToLines();
							
							osmTree.parse();						
							ArrayList< XMLNode> osmNodes = osmTree.getNodes(); 
							
							//Populate text area
							for (int i = 0; i < osmNodes.size(); i++) {
								textArea.append(osmNodes.get(i).getLine());
							}
							
						} else if (file.getName().toLowerCase().endsWith(".osm")) {
			                //This is where a real application would open the file.
							//String inputFileName = file.getName();
							String pathname = file.getPath();
							lblStatusLabel.setText("Opening: " + pathname + ".");
							
							//Read file into a string
							String sInputLines = readFile(file);
							//Create XML tree
							OsmXmlTree osmTree = new OsmXmlTree(sInputLines);
							osmTree.xmlToLines();
							
							osmTree.parse();						
							ArrayList< XMLNode> osmNodes = osmTree.getNodes(); 
							
							//Populate text area
							for (int i = 0; i < osmNodes.size(); i++) {
								textArea.append(osmNodes.get(i).getLine());
							}
							
						} else {
							lblStatusLabel.setText("File: " + file.getName() + " is not a KML file.");
						}
		            } else {
		                //log.append("Open command cancelled by user." + newline);
		            	lblStatusLabel.setText("Open command cancelled by user.");
					}
				}
			}
		});
		//Add listener for save menu
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmSaveAs) {
					if (openFileDir != null) {
						fcSave.setCurrentDirectory(openFileDir);
						//fcSave.setSelectedFile(savedOutptuFile);
					}
		            int returnVal = fcSave.showSaveDialog(mntmSaveAs);
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                File file = fcSave.getSelectedFile();
		                String pathname = file.getPath();
		                
		        		lblStatusLabel.setText("Saving: " + pathname + ".");
		                //This is where a real application would save the file.
						//Save osmFilePathName here
		        		File savedOutptuFile = SaveFile(pathname, textArea.getText().toString());
		        		//Empty the text area
		        		textArea.setText(null);
		            } else {
		            	lblStatusLabel.setText("Save command cancelled by user.");
		            }
		        }
			}
		});

		//Add listener for Exit menu
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	endingOperations();
			}

		});
		
		//Add listener for Edit menu
		//Listener for Convert Nodes
		mntmConvertNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Create XML tree
				OsmXmlTree osmEditedTree = new OsmXmlTree(textArea.getText().toString());
				osmEditedTree.xmlToLines("<node");

				//Generate the tree
				ArrayList< XMLNode> osmNodes = osmEditedTree.getNodes(); 
				
				//Clear the text area
				textArea.setText(null);
				textArea.repaint();

				//Create new osm tree
				osmEditedTree.buildOsmTree();
				
				//Populate text area
				for (int i = 0; i < osmNodes.size(); i++) {
					textArea.append(osmNodes.get(i).getLine());
				}
			}
		});

		//Listener for Reverse Nodes
		mntmReverseNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Create XML tree
				OsmXmlTree osmEditedTree = new OsmXmlTree(textArea.getText().toString());
				osmEditedTree.xmlToLines("<node");

				//Generate the tree
				ArrayList< XMLNode> osmNodes = osmEditedTree.getNodes(); 
				
				//Reverse node lines
				osmEditedTree.reverseXmlLines();
				
				//Clear the text area
				textArea.setText(null);
				textArea.repaint();

				//Create new osm tree
				osmEditedTree.buildOsmTree();
				
				//Populate text area
				for (int i = 0; i < osmNodes.size(); i++) {
					textArea.append(osmNodes.get(i).getLine());
				}
			}
		});

		//Listener for Clear
		mntmClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Clear the text area
				textArea.setText(null);
				textArea.repaint();
			}
		});
	}
	private String readFile(File file) {
		Scanner scanner = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			scanner.close();
		}
		
		return sb.toString();
	}

	private String readKMLFile(File file) {
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
	
	private File SaveFile(String fileName, String message) {
	    File file = new File( fileName );
		try {
			//Remove file if exists
			if (file.exists()) {
				file.delete();    
		    }
			
			file.createNewFile();
	        FileWriter fw = new FileWriter(file, true);

	        BufferedWriter bw = new BufferedWriter(fw);	

            bw.write(message);

	        bw.close();
	        fw.close();	        
		} catch (IOException e) {
	           //do stuff with exception
				e.printStackTrace();
		}
		return file;
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
