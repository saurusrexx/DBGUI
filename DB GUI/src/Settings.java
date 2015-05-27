import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class Settings extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField urlField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		try {
			Settings dialog = new Settings();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Properties prop = new Properties();
		Thread ct = Thread.currentThread();
		ClassLoader contextLoader = ct.getContextClassLoader();
		InputStream is = contextLoader.getResourceAsStream("config.properties");
		try {
			prop.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(prop.getProperty("user"));
	
	}

	/**
	 * Create the dialog.
	 * @throws FileNotFoundException 
	 */
	public Settings() {
		
		/**
		 * Reads from properties file
		 */		
		Properties prop = new Properties();
		//Thread ct = Thread.currentThread();
		//ClassLoader contextLoader = ct.getContextClassLoader();
		InputStream is = getClass().getResourceAsStream("config.properties");
		try {
			prop.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//String currentUser = prop.getProperty("user").toString();
		//String currentPass = prop.getProperty("pass").toString();
		String currentUrl = prop.getProperty("url").toString();
		
		/**
		 * Display the contents of the window
		 */
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblNewLabel_3 = new JLabel("To change the values of the settings, edit the fields below.");
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.gridwidth = 7;
			gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_3.gridx = 0;
			gbc_lblNewLabel_3.gridy = 0;
			contentPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		}
		{
			JLabel lblUrl = new JLabel("URL:");
			GridBagConstraints gbc_lblUrl = new GridBagConstraints();
			gbc_lblUrl.anchor = GridBagConstraints.EAST;
			gbc_lblUrl.insets = new Insets(0, 0, 0, 5);
			gbc_lblUrl.gridx = 0;
			gbc_lblUrl.gridy = 5;
			contentPanel.add(lblUrl, gbc_lblUrl);
		}
		{
			urlField = new JTextField();
			GridBagConstraints gbc_urlField = new GridBagConstraints();
			gbc_urlField.insets = new Insets(0, 0, 0, 5);
			gbc_urlField.fill = GridBagConstraints.HORIZONTAL;
			gbc_urlField.gridx = 1;
			gbc_urlField.gridy = 5;
			contentPanel.add(urlField, gbc_urlField);
			urlField.setColumns(10);
		}
		{
			JLabel lblNewLabel_2 = new JLabel(currentUrl);
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.gridx = 6;
			gbc_lblNewLabel_2.gridy = 5;
			contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					
					
					@Override
					public void mouseReleased(MouseEvent arg0) {
						try {
							//Thread ct = Thread.currentThread();
							//ClassLoader contextLoader = ct.getContextClassLoader();
							//InputStream is = contextLoader.getResourceAsStream("/config.properties");
							//Properties prop = new Properties();
							//prop.load(is);
							//is.close();
							
							String s = prop.getProperty("user").toString();
							String c = urlField.getText().toString();
							
							System.out.println(c + "nothing found");
							
							prop.setProperty("url", c);
							
							FileInputStream in = new FileInputStream("config.properties");
							Properties props = new Properties();
							props.load(in);
							in.close();

							FileOutputStream out = new FileOutputStream("config.properties");
							props.setProperty("country", "america");
							props.store(out, null);
							out.close();
							
						} catch (Exception e){
							
						}
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
			
		}
		//System.out.println();
		System.out.println(currentUrl);
		
	}

}
