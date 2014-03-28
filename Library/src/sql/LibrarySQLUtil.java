package sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class LibrarySQLUtil {
    
	// db fields
	private static final String CONNECT_URL = "jdbc:oracle:thin:@localhost:1521:ug";

	private static final String USER = "root"; 
	private static final String PASSWORD = "1234";

	private static Connection conn;
	
	// command strings
	public static final String SUCCESS_STRING = "Success. ";
	
	static {
		loadDriver();
		conn = getConnection();
	}
	
	private LibrarySQLUtil(){}
	
	public static void loadDriver() {
		try
		{
			// Load the JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
			System.exit(-1);
		}
        
	}
    
	private static Connection getConnection()
	{
		try {
			if (conn != null && !conn.isClosed()) {
				System.out.println("\nRetreiving existing connection");
				return conn;
			}
            
			conn = DriverManager.getConnection(CONNECT_URL, USER, PASSWORD);
			System.out.println("\nNew connection established");
			return conn;
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
		}
		
		return null;
	}

		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO borrower VALUES (?,?,?,?,?,?,?)");
			ps.setString(1, name);
			ps.setString(2, password);
			ps.setString(3, address);
			if (phone.length() == 0) {
				ps.setNull(4, java.sql.Types.INTEGER);
			} else {
                int phonenum = Integer.parseInt(phone);
                ps.setInt(4, phonenum);
			}
			ps.setString(5, email);
			ps.setInt(6, Integer.parseInt(sinOrStdNo));
			ps.setString(7, type);
			ps.executeUpdate();
		    conn.commit();
			ps.close();
		} catch (SQLException e) {
			try {
				System.out.println("SQLException: " + e.getMessage());
				conn.rollback();
			} catch (SQLException e1) {
				System.out.println("SQLException on rollback: " + e1.getMessage());
			}
		}

		// return SUCCESS_STRING + "New borrower <bid> added." on success, SQL error message if failed
		


	public static String checkOutItems(String bid, List<String> items) {
		//TODO
		// conn.prepareStatement... etc...
		// return SUCCESS_STRING + "Items <firstItem>, <secondItem>... checked-out." on success, SQL error message if failed
		
		return SUCCESS_STRING;
	}
}
