package sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import oracle.sql.DATE;


public class LibrarySQLUtil {
    
	// db fields
	private static int borrowingID = 1;
	private static Date today;
	private static Date borrowerDueDate;
	private static final String CONNECT_URL = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug";
	private static final String USER = "ora_d5l8";
	private static final String PASSWORD = "a52632056";
    
	private static Connection conn;
	
	// command strings
	public static final String SUCCESS_STRING = "Success.";
	
	static {
		loadDriver();
		conn = getConnection();
		today = new java.util.Date();
		borrowerDueDate = new Date(today.getTime() + 1209600000);
	}
	
	private LibrarySQLUtil(){}
	
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
			ex.printStackTrace();
		}
		
		return null;
	}
    
	public static String addBorrower(String name, String password, String address, String phone, String email, String sinOrStdNo, String type) {
		//TODO
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO borrower VALUES (?,?,?,?,?,?,?)");
			ps.setString(1, name);
			ps.setString(2, password);
			ps.setString(3, address);
			ps.setString(4, phone);
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
		return SUCCESS_STRING + "New borrower <bid> added.";
    }
    
    
	public static String checkOutItems(String bid, List<String> items) {
		//TODO
		String result = new String();
		ResultSet rs = null;
		try {
			PreparedStatement p = conn.prepareStatement("SELECT bid FROM borrower WHERE bid=?");
			ResultSet temp = p.executeQuery();
			if (temp.next() == false) {
				return "Borrower ID not found.";
			}
			p.close();
		} catch (SQLException e2) {
			System.out.println(e2.getMessage());
			return "Borrower ID not found.";
		}
		
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT callNumber,copyNo,title FROM bookCopy,book WHERE callNumber=? AND status='in'");
			PreparedStatement ps2 = conn.prepareStatement("UPDATE bookCopy SET status='in' WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps3 = conn.prepareStatement("INSERT INTO borrowing VALUES (?,?,?,?,?,?)");
			for (int i = 0; i < items.size(); i++) {
				ps.setString(1,items.get(i));
				rs = ps.executeQuery();
				if (rs.next()) {
					ps2.setString(1, rs.getString(1));
					ps2.setInt(2, rs.getInt(2));
					ps2.executeUpdate();
					
					ps3.setInt(1, borrowingID);
					ps3.setString(2, bid);
					ps3.setString(3, rs.getString(1));
					ps3.setInt(4, rs.getInt(2));
					ps3.setDate(5, new java.sql.Date(today.getTime()));
					ps3.setDate(6, new java.sql.Date(borrowerDueDate.getTime()));
					ps3.executeUpdate();
					
					result.concat("Successfully checked out " + rs.getString(3) + ". Due on " + borrowerDueDate + "\r\n");
					borrowingID++;
				}
			}
			conn.commit();
			if (rs != null)
				rs.close();
			ps.close();
			ps2.close();
			ps3.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				System.out.println("SQLException on rollback: " + e1.getMessage());
			}
		}
		
		return result;
	}
    
	public static String searchBooks(String title, String author, String subject) {
		String tempCallNumber, result;
		int numIn, numOut, numOnHold;
		ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null;
		result = new String("Your search found:\r\n");
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT callNumber "
                                                         + "FROM book,hasAuthor,hasSubject "
                                                         + "WHERE title LIKE ? OR name=? OR subject=?");
			PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT (*) "
                                                          + "FROM bookCopy,book "
                                                          + "WHERE callNumber=? AND status='in'");
			PreparedStatement ps3 = conn.prepareStatement("SELECT COUNT (*) "
                                                          + "FROM bookCopy,book "
                                                          + "WHERE callNumber=? AND status='out'");
			PreparedStatement ps4 = conn.prepareStatement("SELECT COUNT (*) "
                                                          + "FROM bookCopy,book "
                                                          + "WHERE callNumber=? AND status='on hold'");
			ps.setString(1, "%" + title + "%");
			ps.setString(2, author);
			ps.setString(3, subject);
			rs = ps.executeQuery();
			while (rs.next()) {
				tempCallNumber = rs.getString(1);
				rs2 = ps2.executeQuery();
				if (rs2.next())
					numIn = rs2.getInt(1);
				else numIn = 0;
				rs3 = ps3.executeQuery();
				if (rs3.next())
					numOut = rs3.getInt(1);
				else numOut = 0;
				rs4 = ps4.executeQuery();
				if (rs4.next())
					numOnHold = rs4.getInt(1);
				else numOnHold = 0;
				result.concat("CallNumber: " + tempCallNumber
                              + "   Copies in: " + numIn
                              + "   Copies out: " + numOut
                              + "   Copies on hold: " + numOnHold + "\r\n");
			}
			ps.close();
			if (rs != null) 
				rs.close();
			ps2.close();
			if (rs2 != null) 
				rs2.close();
			ps3.close();
			if (rs3 != null) 
				rs3.close();
			ps4.close();
			if (rs4 != null) 
				rs4.close();
		} catch (SQLException e) {
			try {
				conn.rollback();
				System.out.println(e.getMessage());
			} catch (SQLException e1) {
				System.out.println(e1.getMessage());
			}
		}
		return result;
	}

	public static String processReturn(String returnID) {
		// TODO method should mark item as "in", assess fine if item is overdue
			// if item is on hold request by another borrower, a message is sent to that borrower
		// return SUCCESS_STRING + "Item checked in."
		return SUCCESS_STRING;
	}

	public static String checkAcct(String bid) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String holdRequest(String bid, String callNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String payFines(String borid, String amount) {
		// TODO Auto-generated method stub
		return null;
	}
}
