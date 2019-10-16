package menutester;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class MenuTest {

    static private final String newline = "\n";
	private JFrame frame;
	private JFileChooser fc;
	private JTextArea log;
	private JMenuItem mntmOpen;
	private JMenuItem mntmSave;
	
	public class MyClass implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
	        //Handle open button action.
	        if (e.getSource() == mntmOpen) {
	            int returnVal = fc.showOpenDialog(frame);

	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                //This is where a real application would open the file.
	                log.append("Opening: " + file.getName() + "." + newline);
	            } else {
	                log.append("Open command cancelled by user." + newline);
	            }
	            log.setCaretPosition(log.getDocument().getLength());

	        //Handle save button action.
	        } else if (e.getSource() == mntmSave) {
	            int returnVal = fc.showSaveDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                //This is where a real application would save the file.
	                log.append("Saving: " + file.getName() + "." + newline);
	            } else {
	                log.append("Save command cancelled by user." + newline);
	            }
	            log.setCaretPosition(log.getDocument().getLength());
	        }
	    }
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuTest window = new MenuTest();
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
	public MenuTest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 97, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
        //Create a file chooser
        fc = new JFileChooser();
		
		mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		
		mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(50, 50, 350, 200);
		frame.getContentPane().add(scrollPane);
		
		log = new JTextArea();
		log.setRows(2);
		scrollPane.setViewportView(log);
		log.setEditable(false);
		
		MyClass instanceOfMyClass = new MyClass();
		mntmOpen.addActionListener(instanceOfMyClass);
		mntmSave.addActionListener(instanceOfMyClass);
		

	}
}
