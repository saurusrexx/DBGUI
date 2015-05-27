import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


public class MainMenu {

	public JFrame frame;
	//public TableModel tm;
	
	//Database Connection Variables
	static Connection con = null;
	static PreparedStatement st = null;
	static ResultSet rs = null;
	private JTable table;
	static ResultSet rst = null;
	
	//Log File Setup
	Logger log = Logger.getLogger("Error Log");
	FileHandler fh;
	static String current = System.getProperty("user.dir");

	/**
	 * Launch the application.
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
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() throws Exception {
		//Initialize driver
		Class.forName("org.postgresql.Driver");
		
		/** 
		 * Load the properties of the program
		 */
		Properties prop = new Properties();
		Thread ct = Thread.currentThread();
		ClassLoader contextLoader = ct.getContextClassLoader();
		InputStream is = contextLoader.getResourceAsStream("config.properties");
		prop.load(is);
		
		//Get Database URL from properties file
		String url = prop.getProperty("url").toString();
		
		//Ask for user input, and verify that it is correct
		String user = JOptionPane.showInputDialog(frame, "Enter Username", url, JOptionPane.QUESTION_MESSAGE);
		String pass = JOptionPane.showInputDialog(frame, "Input Password", url, JOptionPane.QUESTION_MESSAGE);
		
		/**
		 * Establishes the Log file
		 */
		fh = new FileHandler(current + "ErrorLog.log", true);
		log.addHandler(fh);
		SimpleFormatter form = new SimpleFormatter();
		fh.setFormatter(form);

		
		/**
		 * Writes to the properties of the program
		 */

		String label = "Test";

		con = DriverManager.getConnection(url, user, pass);
		
		if (con !=null) {
			log.info("Connected");
			//fh.close();
			System.out.println("Success");
			label = ("Connected to " + con.getMetaData().getDatabaseProductName());
		}
		else {
			System.out.println("Failure");
			label = "Not Connected";
		}
		
		st = con.prepareStatement("SELECT * FROM test.inspection_room_inventory ORDER BY idnumber ASC");
		rs = st.executeQuery();
		
		
		//con.setAutoCommit(false);
				
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel(label);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 2;
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
		
		table.getModel().addTableModelListener(new TableModelListener() {
			
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int col = e.getColumn();
			AbstractTableModel model = (AbstractTableModel)e.getSource();
			Object data = model.getValueAt(row, col);
			
			String s = data.toString();
			
			PreparedStatement stat = null;
			
			TableModel mdl = (TableModel)table.getModel();
			//table.removeColumn(table.getColumnModel().getColumn(0));
			//table.setModel(mdl);
			//table.setModel(model);
			//table.setModel(dataModel);
			//model.fireTableDataChanged();
			try {
				con.setAutoCommit(false);
				stat = con.prepareStatement("UPDATE test.inspection_room_inventory SET idnumber = " + s + ";" + "COMMIT;");
				//rs = stat.executeQuery();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//((AbstractTableModel) mdl).fireTableDataChanged();
			
			//System.out.println(e);
							
			}
		});
		
		JButton btnNewButton = new JButton("Update Table");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					//updateTable();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 2;
		frame.getContentPane().add(btnNewButton, gbc_btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Exit");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					int n = JOptionPane.showConfirmDialog(null, "Connection to server will now be closed.", "Alert", JOptionPane.OK_CANCEL_OPTION);
					
					if (n == JOptionPane.CANCEL_OPTION) {
						//JOptionPane.getRootFrame().dispose();
					} 
					if (n == JOptionPane.OK_OPTION) {
						rs.close();
						st.close();
						con.close();
						log.info("Connection Closed Successfully");
						System.out.println("Connection Closed");
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridx = 3;
		gbc_btnNewButton_1.gridy = 2;
		frame.getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmSwitchTables = new JMenuItem("Switch Tables");
		mnNewMenu.add(mntmSwitchTables);
		
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
		
		
	}
	
	/**
	 * Come back to later; probably irrelevant
	 */
	/*public boolean isCellEditable (int row, int col) {
		return true;
	}
	
	public void setValueAt(Object value, int row, int col) {
		rowData[row][col] = value;
		fireTableCellUpdated(row, col);
	}*/
	
	
	/**
	 * Attempt at editing the values in the db through the Jtable
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public static TableModel buildTableModel(ResultSet rs) throws Exception {
		
		//TableModel mdl = new TableModel();
		//Statement stmt = null;
		
		//int colInd = 0;
		
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
		
		//outputResultSet(rs);
		
		return new DefaultTableModel(data, colnam);
		
	}
	
}
