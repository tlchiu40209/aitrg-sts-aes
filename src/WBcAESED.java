import java.awt.EventQueue;
import javax.swing.JFrame;

import java.io.*;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WBcAESED {
	
	static private String FILE_NAME;
	final static private String ALGORITHM = "AES";
	static private int KEY_SIZE;
	
	final private BlockCipher AESCipher = new AESEngine();
	private PaddedBufferedBlockCipher pbbc;
	private KeyParameter key;

	private JFrame frmAesPerformanceTest;
	private JTextField txtTimes;
	private JTextField txtElapse;
	private JLabel lblStatus;
	private JComboBox cbxAESK;
	private JTextField txtFilesize;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WBcAESED window = new WBcAESED();
					window.frmAesPerformanceTest.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WBcAESED() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAesPerformanceTest = new JFrame();
		frmAesPerformanceTest.setTitle("AES Performance Test");
		frmAesPerformanceTest.setBounds(100, 100, 450, 300);
		frmAesPerformanceTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmAesPerformanceTest.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel lblAes = new JLabel("AES:");
		lblAes.setBounds(22, 78, 46, 15);
		panel.add(lblAes);
		
		cbxAESK = new JComboBox();
		cbxAESK.setModel(new DefaultComboBoxModel(new String[] {"AES_128", "AES_192", "AES_256"}));
		cbxAESK.setBounds(114, 75, 213, 21);
		panel.add(cbxAESK);
		
		JLabel lblTimes = new JLabel("Times");
		lblTimes.setBounds(22, 128, 46, 15);
		panel.add(lblTimes);
		
		txtTimes = new JTextField();
		txtTimes.setBounds(114, 125, 213, 21);
		panel.add(txtTimes);
		txtTimes.setColumns(10);
		
		JButton btnExecute = new JButton("Execute");
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread execute = new Thread() {
					public void run() {
						try {
							lblStatus.setText("Running");							
							SecureRandom sr = new SecureRandom();
							int fileSize = Integer.parseInt(txtFilesize.getText());
							int testTimes = Integer.parseInt(txtTimes.getText());
							int selectedKeySpec = cbxAESK.getSelectedIndex();
							int keySize;
							if (selectedKeySpec == 0) {
								keySize = 128;
							} else if (selectedKeySpec == 1) {
								keySize = 192;
							} else {
								keySize = 256;
							}
							/***
							File file = new File("testFile");
							FileOutputStream fileOs = new FileOutputStream(file);
							FileInputStream fileIs;
							***/
							
							byte[] encrypt;
							byte[] decrypt;
							byte[] original = new byte[fileSize];
							long msbefore;
							long msafter;
							long encTotalElapse = 0;
							long decTotalElapse = 0;
							
							sr.nextBytes(original);
							KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
							keyGen.init(keySize);
							SecretKey secretKey = keyGen.generateKey();
							WBcAESED bcAESed = new WBcAESED();
							bcAESed.setPadding(new PKCS7Padding());
							bcAESed.setKey(secretKey.getEncoded());
							
							for (int i = 0; i < testTimes; i++) {
								msbefore = getCurrentTime();
								encrypt = bcAESed.encrypt(original);
								msafter = getCurrentTime();
								encTotalElapse = encTotalElapse + (msafter - msbefore);
								
								msbefore = getCurrentTime();
								decrypt = bcAESed.decrypt(encrypt);
								msafter = getCurrentTime();
								decTotalElapse = decTotalElapse + (msafter - msbefore);
							}
							
							float encResult = (float)encTotalElapse / (float)testTimes;
							float decResult = (float)decTotalElapse / (float)testTimes;
							String displayString = "AVENC: " + encResult + " ms / AVDEC: " + decResult + " ms";
							txtElapse.setText(displayString);
							lblStatus.setText("Ready");
							/**Write testFile**/
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				execute.start();
			}
		});
		btnExecute.setBounds(337, 228, 87, 23);
		panel.add(btnExecute);
		
		JLabel lblElapse = new JLabel("Elapse");
		lblElapse.setBounds(22, 176, 46, 15);
		panel.add(lblElapse);
		
		txtElapse = new JTextField();
		txtElapse.setBounds(114, 173, 213, 21);
		panel.add(txtElapse);
		txtElapse.setColumns(10);
		
		lblStatus = new JLabel("Ready");
		lblStatus.setBounds(22, 232, 46, 15);
		panel.add(lblStatus);
		
		JLabel lblFilesize = new JLabel("Filesize");
		lblFilesize.setBounds(22, 32, 46, 15);
		panel.add(lblFilesize);
		
		txtFilesize = new JTextField();
		txtFilesize.setBounds(114, 29, 213, 21);
		panel.add(txtFilesize);
		txtFilesize.setColumns(10);
		
		JLabel lblBytes = new JLabel("Bytes");
		lblBytes.setBounds(358, 32, 46, 15);
		panel.add(lblBytes);
	}
	
	public static long getCurrentTime() {
		Date today;
		today = new Date();
		return today.getTime();
	}
	public JLabel getLblStatus() {
		return lblStatus;
	}
	public JTextField getTxtTimes() {
		return txtTimes;
	}
	public JComboBox getCbxAESK() {
		return cbxAESK;
	}
	public JTextField getTxtElapse() {
		return txtElapse;
	}

	public JTextField getTxtFilesize() {
		return txtFilesize;
	}
	
	private byte[] aesED(byte[] input, boolean encrypt) throws Exception {
		pbbc.init(encrypt,  key);
		byte[] output = new byte[pbbc.getOutputSize(input.length)];
		int bytesWrittenOut = pbbc.processBytes(input,  0,  input.length,  output, 0);
		pbbc.doFinal(output,  bytesWrittenOut);
		return output;
	}
	
	public void setPadding(BlockCipherPadding bcp) {
		this.pbbc = new PaddedBufferedBlockCipher(AESCipher, bcp);
	}
	
	public void setKey(byte[] key) {
		this.key = new KeyParameter(key);
	}
	
	public byte[] encrypt(byte[] input) throws Exception {
		return aesED(input, true);
	}
	
	public byte[] decrypt(byte[] input) throws Exception {
		return aesED(input, false);
	}
	
}