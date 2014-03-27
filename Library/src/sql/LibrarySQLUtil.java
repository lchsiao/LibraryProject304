package sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class LibrarySQLUtil {

	// db fields
	private static String CONNECT_URL = "jdbc:oracle:thin:@localhost:1521:ug";
	private static String USER = "..."; 
	private static String PASSWORD = "...";

	private static Connection conn;
	
	// command strings
	public static final String SUCCESS_STRING = "SUCCESS";
	
	static {
		loadDriver();
		conn = getConnection();
	}
	
	public static void loadDriver() {
		try 
		{
			// Load the Oracle JDBC driver
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

	public static String addBorrower(String name, String password, String address, String phone,
			String email, String sinOrStdNo, String type) {
		//TODO
		// conn.prepareStatement... etc...
		
		return SUCCESS_STRING;
	}
}
