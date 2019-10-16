import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Speksi {

	private JFrame frame;
	private JTextField textTositeNumero;
	private JTextField textPvm;
	private JTextField textOstoPaikka;
	private JTextField textLuokitus;
	private JTextField textMaksutapa;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Speksi window = new Speksi();
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
	public Speksi() {
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
		
		JLabel lblKuitinTiedot = new JLabel("Kuitin tiedot");
		lblKuitinTiedot.setBounds(25, 11, 79, 14);
		frame.getContentPane().add(lblKuitinTiedot);
		
		JLabel lblTositenumero = new JLabel("Tositenumero:");
		lblTositenumero.setBounds(25, 61, 79, 14);
		frame.getContentPane().add(lblTositenumero);
		
		textTositeNumero = new JTextField();
		textTositeNumero.setBounds(114, 58, 86, 20);
		frame.getContentPane().add(textTositeNumero);
		textTositeNumero.setColumns(10);
		
		JLabel lblPvm = new JLabel("P\u00E4iv\u00E4m\u00E4\u00E4r\u00E4:");
		lblPvm.setBounds(25, 95, 79, 14);
		frame.getContentPane().add(lblPvm);
		
		textPvm = new JTextField();
		textPvm.setBounds(114, 89, 86, 20);
		frame.getContentPane().add(textPvm);
		textPvm.setColumns(10);
		
		JLabel lblOstopaikka = new JLabel("Ostopaikka:");
		lblOstopaikka.setBounds(25, 123, 79, 14);
		frame.getContentPane().add(lblOstopaikka);
		
		textOstoPaikka = new JTextField();
		textOstoPaikka.setBounds(114, 120, 86, 20);
		frame.getContentPane().add(textOstoPaikka);
		textOstoPaikka.setColumns(10);
		
		JLabel lblLuokitus = new JLabel("Luokitus:");
		lblLuokitus.setBounds(25, 154, 46, 14);
		frame.getContentPane().add(lblLuokitus);
		
		textLuokitus = new JTextField();
		textLuokitus.setBounds(114, 151, 86, 20);
		frame.getContentPane().add(textLuokitus);
		textLuokitus.setColumns(10);
		
		JLabel lblMaksutapa = new JLabel("Maksutapa:");
		lblMaksutapa.setBounds(25, 183, 79, 14);
		frame.getContentPane().add(lblMaksutapa);
		
		textMaksutapa = new JTextField();
		textMaksutapa.setBounds(114, 182, 86, 20);
		frame.getContentPane().add(textMaksutapa);
		textMaksutapa.setColumns(10);
	}
}
