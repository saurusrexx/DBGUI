import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.postgresql.util.PSQLException;


public class MainMenu {

	public JFrame frame;
	
	//Database Connection Variables
	static Connection con = null;
	static PreparedStatement st = null;
	static ResultSet rs = null;
	
	private JTable table;
	
	static PreparedStatement st1 = null;
	static ResultSet rs1 = null;
	
	public String t;

	//Log File Setup
	Logger log = Logger.getLogger("Error Log");
	FileHandler fh;
	static String current = System.getProperty("user.dir");
	
	public String urlNode = "URL";
	public String sortNode = "SORT";
	public String def = "";

	/**
	 * Launch the application.
	 * @return 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu window = new MainMenu();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public MainMenu() throws Exception {
		
		Preferences userPref = Preferences.userNodeForPackage(MainMenu.class);
		//userPref.put("urlNode", "url");
		
		if (userPref.get(urlNode, def).isEmpty()) {
			String check1 = JOptionPane.showInputDialog("Enter URL");
			System.out.println(check1);
			userPref.put(urlNode, check1);
			
		} if (userPref.get(sortNode, def).isEmpty()) {
			String check2 = JOptionPane.showInputDialog("Enter Default Sort");
			userPref.put(sortNode, check2);
		} else {
			initialize();
			
		}
		initialize();
	
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {
		//Initialize driver
		Class.forName("org.postgresql.Driver");
		
		Preferences currentPref = Preferences.userNodeForPackage(MainMenu.class);
		String url = currentPref.get(urlNode, def);
		String sort = currentPref.get(sortNode, def);
		
		//Ask for user input, and verify that it is correct
		//String user = JOptionPane.showInputDialog(frame, "Enter Username", url, JOptionPane.QUESTION_MESSAGE);
		//user.toString();
		
		//String pass = JOptionPane.showInputDialog(frame, "Input Password", url, JOptionPane.QUESTION_MESSAGE);
		
		/**
		 * Establishes the Log file
		 */
		fh = new FileHandler(current + "./ErrorLog.log", true);
		log.addHandler(fh);
		SimpleFormatter form = new SimpleFormatter();
		fh.setFormatter(form);

		/**
		 * Writes to the properties of the program
		 */
		String label = "Test";
		
		try {
			con = DriverManager.getConnection(url, "jeff", "1234");
		
			if (con !=null) {
				//log.info("Connected");
				//fh.close();
				//System.out.println("Success");
				label = ("Connected to " + con.getMetaData().getDatabaseProductName());
			}

		} catch (PSQLException e) {
			JOptionPane.showMessageDialog(null, "Could Not Load Database. Check your network connections and try again.", "DB GUI", 0);
			log.info("Network Error: No Connection");
		}
		
		//st = con.prepareStatement("SELECT * FROM test.inspection_room_inventory ORDER BY idnumber ASC");
		st = con.prepareStatement("SELECT * FROM test.inspection_room_inventory");
		rs = st.executeQuery();

		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel(label);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 4;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		table = new JTable(buildTableModel(rs));
		scrollPane.setViewportView(table);
		
		table.removeColumn(table.getColumnModel().getColumn(4));
		
		DefaultTableModel mdl = (DefaultTableModel) table.getModel();
		
		JButton btnNewButton = new JButton("Update Table");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
				table.getModel().addTableModelListener(new TableModelListener() {
					
					public void tableChanged(TableModelEvent e) {

						PreparedStatement stat = null;

						int i = table.getSelectedRow();
						int j = table.getSelectedColumn();	
						
						String colNam = table.getColumnName(j);
						System.out.println("Column Name: " + colNam);
						
						Object o = table.getModel().getValueAt(i, j);
						t = o.toString();
						
						Object ob = table.getModel().getValueAt(i, 4);
						String obs = ob.toString();
						System.out.println(obs);
						
						System.out.println("(" + j + "," + i + ")");
						
						System.out.println("Current Value: " + t);
						
						try {
							stat = con.prepareStatement("UPDATE test.inspection_room_inventory SET " + colNam + " " + "= " + "'" + t + "'" + " WHERE " + "key = " + "'" + obs + "'" );
							String a = stat.toString();
							System.out.println("Statement: " + a);
							rs = stat.executeQuery();
							mdl.fireTableDataChanged();
							
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
							
						}

					}
	
				});
	
			}

		});
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 3;
		frame.getContentPane().add(btnNewButton, gbc_btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Exit");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					int n = JOptionPane.showConfirmDialog(null, "Connection to server will now be closed.", "Alert", JOptionPane.OK_CANCEL_OPTION);
					
					if (n == JOptionPane.CANCEL_OPTION) {
						JOptionPane.getRootFrame().dispose();
					} 
					if (n == JOptionPane.OK_OPTION) {
						rs.close();
						st.close();
						con.close();
						//log.info("Connection Closed Successfully");
						System.out.println("Connection Closed");
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnNewButton_2 = new JButton("Refresh Table");
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {

				try {
					Runtime.getRuntime().exec("cmd.exe /c start " + "restart.bat");
					System.exit(0);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}
			
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_2.gridx = 2;
		gbc_btnNewButton_2.gridy = 3;
		frame.getContentPane().add(btnNewButton_2, gbc_btnNewButton_2);
		
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridx = 3;
		gbc_btnNewButton_1.gridy = 3;
		frame.getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				JOptionPane.showMessageDialog(null, "No Help Found");
			}
		});
		mnNewMenu.add(mntmHelp);
		
		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Settings dialog = new Settings();
				
				dialog.setVisible(true);
			}
		});
		mnNewMenu.add(mntmSettings);
		
		JMenu mnAdd = new JMenu("Edit");
		menuBar.add(mnAdd);
		
		JMenuItem mntmAddRow = new JMenuItem("Add Row");
		mnAdd.add(mntmAddRow);
		
		JMenu mnNewMenu_1 = new JMenu("Change Sort");
		mnAdd.add(mnNewMenu_1);
		
		JMenuItem mntmIdNumber = new JMenuItem("ID Number");
		mntmIdNumber.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			

			}
			
		});
		
		mnNewMenu_1.add(mntmIdNumber);
		
		JMenuItem mntmDescription = new JMenuItem("Description");
		mntmDescription.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
			
			}
		});
		mnNewMenu_1.add(mntmDescription);
		
		JMenuItem mntmLastDate = new JMenuItem("Last Date");
		mntmLastDate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
				
			}
		});
		mnNewMenu_1.add(mntmLastDate);
		
		JMenuItem mntmNextDate = new JMenuItem("Next Date");
		mntmNextDate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
			
			}
		});
		mnNewMenu_1.add(mntmNextDate);

	}
	
	
	/**
	 * Method for Displaying the JTable and populating it with the Database values
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public static TableModel buildTableModel(ResultSet rs) throws Exception {
		
		// TODO Auto-generated method stub
		ResultSetMetaData md = rs.getMetaData();
		
		Vector<String> colnam = new Vector<String>();
		
		int colCount = md.getColumnCount();
		for (int col = 1; col <= colCount; col++) {
			colnam.add(md.getColumnName(col));
		}
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int colInd = 1; colInd <= colCount; colInd++) {
				vector.add(rs.getObject(colInd));
				
			}
			
			data.add(vector);
			
		}	
		
		return new DefaultTableModel(data, colnam);
		
	}
	
}