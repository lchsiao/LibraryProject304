package sql;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import oracle.sql.DATE;


public class LibrarySQLUtil {
    
	// db fields
	private static Date today;
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
			PreparedStatement ps = conn.prepareStatement("INSERT INTO borrower (bid,bPass,bName,address,phone,emailAddress,sinOrStNo,expiryDate,bType) "
                                                         + "VALUES (seq_borrower.nextval,?,?,?,?,?,?,?,?)");
			ps.setString(1, password);
			ps.setString(2, name);
			ps.setString(3, address);
			ps.setString(4, phone);
			ps.setString(5, email);
			ps.setInt(6, Integer.parseInt(sinOrStdNo));
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
			ps.setDate(7, new java.sql.Date(cal.getTime().getTime()));
			ps.setString(8, type);
			ps.execute();
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
		return SUCCESS_STRING + "New borrower " +  "added.";
    }
    
    
	public static String checkOutItems(String bid, List<String> items) {
		//TODO
		String result = new String();
		ResultSet rs = null;
	    String borrowerType;
		try {
			PreparedStatement p = conn.prepareStatement("SELECT bid,bType FROM borrower WHERE bid=?");
			p.setString(1, bid);
			ResultSet temp = p.executeQuery();
			if (temp.next() == false) {
				return "Borrower ID not found.";
			}
			borrowerType = temp.getString(2);
			p.close();
		} catch (SQLException e2) {
			System.out.println(e2.getMessage());
			return "Borrower ID not found.";
		}
		
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT callNumber,copyNo,title FROM bookCopy,book WHERE bookCopy.callNumber=book.callNumber AND callNumber=? AND copyStatus='in'");
			PreparedStatement ps2 = conn.prepareStatement("UPDATE bookCopy SET copyStatus='out' WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps3 = conn.prepareStatement("INSERT INTO borrowing (borid,bid,callNumber,copyNo,outDate,inDate) VALUES (seq_borrowing.nextval,?,?,?,?,?)");
			
			for (int i = 0; i < items.size(); i++) {
				ps.setString(1,items.get(i));
				rs = ps.executeQuery();
				if (rs.next()) {
					ps2.setString(1, rs.getString(1));
					ps2.setInt(2, rs.getInt(2));
					ps2.executeUpdate();
					
					ps3.setString(1, bid);
					ps3.setString(2, rs.getString(1));
					ps3.setInt(3, rs.getInt(2));
					ps3.setDate(4, new java.sql.Date(today.getTime()));
					ps3.setDate(5, null);
					ps3.executeUpdate();
					
					result.concat("Successfully checked out " + rs.getString(3) + ". Due on " + getDueDate(today, borrowerType) + "\r\n");
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
    
	public static List<String[]> searchBooks(String title, String author, String subject) {
		String tempCallNumber, tempTitle;
		int numIn, numOut, numOnHold;
		List<String[]> result = new ArrayList<String[]>();
		ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null;
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT book.callNumber,title "
                                                         + "FROM book,hasAuthor,hasSubject "
                                                         + "WHERE book.callNumber=hasAuthor.callNumber AND book.callNumber=hasSubject.callNumber "
                                                         + "AND (title LIKE ? OR aName=? OR bookSubject=?)");
			PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT (*) "
                                                          + "FROM bookCopy,book "
                                                          + "WHERE bookCopy.callNumber=book.callNumber AND book.callNumber=? AND copyStatus='in'");
			PreparedStatement ps3 = conn.prepareStatement("SELECT COUNT (*) "
                                                          + "FROM bookCopy,book "
                                                          + "WHERE bookCopy.callNumber=book.callNumber AND book.callNumber=? AND copyStatus='out'");
			PreparedStatement ps4 = conn.prepareStatement("SELECT COUNT (*) "
                                                          + "FROM bookCopy,book "
                                                          + "WHERE bookCopy.callNumber=book.callNumber AND book.callNumber=? AND copyStatus='on hold'");
			ps.setString(1, "%" + title + "%");
			ps.setString(2, author);
			ps.setString(3, subject);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				tempCallNumber = rs.getString(1);
				tempTitle = rs.getString(2);
				ps2.setString(1, tempCallNumber);
				rs2 = ps2.executeQuery();
				if (rs2.next())
					numIn = rs2.getInt(1);
				else numIn = 0;
				ps3.setString(1, tempCallNumber);
				rs3 = ps3.executeQuery();
				if (rs3.next())
					numOut = rs3.getInt(1);
				else numOut = 0;
				ps4.setString(1, tempCallNumber);
				rs4 = ps4.executeQuery();
				if (rs4.next())
					numOnHold = rs4.getInt(1);
				else numOnHold = 0;
				String[] array = {tempTitle, tempCallNumber, Integer.toString(numIn), Integer.toString(numOut), Integer.toString(numOnHold)};
				result.add(array);
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
    
	public static String processReturn(String callNum, int copyNum) {
		// TODO method should mark item as "in", assess fine if item is overdue
		// if item is on hold request by another borrower, a message is sent to that borrower
		// return SUCCESS_STRING + "Item checked in."
		int borID;
		String bid, borrowerType;
		Date borrowedDate, dueDate;
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT borid,bid,outDate FROM borrowing WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps2 = conn.prepareStatement("UPDATE borrowing SET inDate=? WHERE borid=?");
			PreparedStatement ps3 = conn.prepareStatement("SELECT bid FROM holdRequest WHERE callNumber=?");
			PreparedStatement ps4 = conn.prepareStatement("UPDATE bookCopy SET status='in' WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps5 = conn.prepareStatement("UPDATE bookCopy SET status='on hold' WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps7 = conn.prepareStatement("SELECT bType FROM borrower WHERE bid=?");
			PreparedStatement ps8 = conn.prepareStatement("INSERT INTO fine (fid,amount,issuedDate,paidDate,borid)"
                                                          + "VALUES (seq_fine.nextval,?,?,?,?)");
			
			// look for the borrowing record
			ps.setString(1, callNum);
			ps.setInt(2, copyNum);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				return "Item was not borrowed.";
			}
			borID = rs.getInt(1);
			bid = rs.getString(2);
			borrowedDate = rs.getDate(3);
			ps.close();
			rs.close();
			// find the type of the borrower
			ps7.setString(1, bid);
			ResultSet rs7 = ps7.executeQuery();
			rs7.next();
			ps7.close();
			borrowerType = rs7.getString(1);
			rs7.close();
			dueDate = getDueDate(borrowedDate, borrowerType);
			if (today.after(dueDate)) {
				// assess the fine if overdue
				int fine = (int) ((Math.round((float)(today.getTime() - dueDate.getTime()))) * 0.05);
				ps8.setInt(2, fine);
				ps8.setDate(3, new java.sql.Date(today.getTime()));
				ps8.setDate(4, null);
				ps8.setInt(5, borID);
				ps8.executeUpdate();
				ps8.close();
			}
			// set the date the book was returned in the borrowing record
			ps2.setDate(1, new java.sql.Date(today.getTime()));
			ps2.setInt(2, borID);
			ps2.executeUpdate();
			ps2.close();
			// get the borrower ID of a borrower who put the book on hold
			ps3.setString(1, callNum);
			ResultSet rs3 = ps3.executeQuery();
			ps3.close();
			if (rs3.next()) {
				// get the name and address of this borrower
				PreparedStatement ps6 = conn.prepareStatement("SELECT bName,emailAddress FROM borrower WHERE bid=?");
				ps6.setString(1, rs3.getString(1));
				ResultSet rs6 = ps6.executeQuery();
				ps6.close();
				rs3.close();
				if (rs6.next()) {
					String name = rs6.getString(1);
					String email = rs6.getString(2);
					rs6.close();
					ps5.setString(1, callNum);
					ps5.setInt(2, copyNum);
					ps5.executeUpdate();
					ps5.close();
					conn.commit();
					return "Successfully returned. Notify " + name + " at " + email + " that his/her held book is available";
				}
			} else {
				ps4.setString(1, callNum);
				ps4.setInt(2, copyNum);
				ps4.executeUpdate();
				ps4.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "Error.";
		}
		return "Successfully returned.";
	}
	
	private static Date getDueDate(Date borrowDate, String borrowerType) {
		Date dueDate;
		if (borrowerType.equals("student")) {
			dueDate = new Date(borrowDate.getTime() + 1209600000);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(borrowDate);
			if (borrowerType.equals("staff")) {
				cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + 6);
				dueDate = cal.getTime();
			} else {
				cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + 12);
				dueDate = cal.getTime();
			}
		}
		return dueDate;
	}
    
	public static List<List<String[]>> checkAcct(String bid) {
		// TODO Auto-generated method stub
		List<List<String[]>> result = new ArrayList<List<String[]>>();
		List<String[]> borrows = new ArrayList<String[]>(), fines = new ArrayList<String[]>(), holds = new ArrayList<String[]>();
		String title, borrowerType, dueDate, fineAmount, callNum, copyNum;
		try {
			PreparedStatement p = conn.prepareStatement("SELECT bType FROM borrower WHERE bid=?");
			p.setString(1, bid);
			ResultSet r = p.executeQuery();
			if (!r.next())
				return null;
			borrowerType = r.getString(1);
			
			PreparedStatement ps = conn.prepareStatement("SELECT title,borrowing.callNumber,copyNo,outDate"
														 + " FROM borrowing,book "
                                                         + " WHERE book.callNumber=borrowing.callNumber AND bid=? AND inDate IS NULL");
			PreparedStatement ps2 = conn.prepareStatement("SELECT amount,callNumber"
                                                          + " FROM fine,borrowing"
                                                          + " WHERE fine.borid=borrowing.borid AND bid=?");
			PreparedStatement ps3 = conn.prepareStatement("SELECT book.callNumber,title"
                                                          + " FROM holdRequest,book"
                                                          + " WHERE holdRequest.callNumber = book.callNumber AND bid=?");
			ps.setString(1, bid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rs.getString(1);
				title = rs.getString(1);
				callNum = rs.getString(2);
				copyNum = Integer.toString(rs.getInt(3));
				dueDate = "" + getDueDate(rs.getDate(4), borrowerType) + "";
				String[] borrow = {callNum, copyNum, title, dueDate};
				borrows.add(borrow);
			}
			rs.close();
			ps.close();
			ps2.setString(1, bid);
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				fineAmount = Integer.toString(rs2.getInt(1));
				callNum = rs2.getString(2);
				String[] fine = {fineAmount, callNum};
				fines.add(fine);
			}
			rs2.close();
			ps2.close();
			ps3.setString(1,bid);
			ResultSet rs3 = ps3.executeQuery();
			while (rs3.next()) {
				callNum = rs3.getString(1);
				title = rs3.getString(2);
				String[] hold = {callNum, title};
				holds.add(hold);
			}
			rs3.close();
			ps3.close();
			result.add(borrows);
			result.add(fines);
			result.add(holds);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
    
	public static String holdRequest(String bid, String callNumber) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM bookCopy WHERE callNumber=? AND copyStatus='in'");
			ps.setString(1, callNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.close();
				ps.close();
				return "Hold request failed. There is a copy of this item available for borrowing.";
			}
			rs.close();
			ps.close();
			
			int tempCopyNo;
			PreparedStatement ps2 = conn.prepareStatement("SELECT copyNo FROM bookCopy WHERE callNumber=? and copyStatus='out'");
			PreparedStatement ps3 = conn.prepareStatement("INSERT INTO holdRequest (hid,bid,callNumber,issuedDate) VALUES (seq_holdRequest.nextval,?,?,?)");
			PreparedStatement ps4 = conn.prepareStatement("UPDATE bookCopy SET copyStatus='on hold' WHERE callNumber=? AND copyNo=?");
			
			ps2.setString(1, callNumber);
			ResultSet rs2 = ps2.executeQuery();
			if (!rs2.next()) {
				rs2.close();
				ps2.close();
				return "Hold request failed. All copies of this item are on hold.";
			}
			tempCopyNo = rs2.getInt(1);
			rs2.close();
			ps2.close();
			
			ps3.setString(1, bid);
			ps3.setString(2, callNumber);
			ps3.setDate(3, new java.sql.Date(today.getTime()));
			ps3.executeUpdate();
			ps3.close();
			
			ps4.setString(1, callNumber);
			ps4.setInt(2, tempCopyNo);
			ps4.executeUpdate();
			ps4.close();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Hold request for item " + callNumber + " was successful.";
	}
    
	public static String payFines(int borid, int amount) {
		// TODO Auto-generated method stub
		try {
			int moneyOwed;
			PreparedStatement ps = conn.prepareStatement("SELECT amount FROM fine WHERE borid=?");
			ps.setInt(1, borid);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				rs.close();
				ps.close();
				return "No fine is owed from this borrowing transaction.";
			}
			moneyOwed = rs.getInt(1);
			if ((moneyOwed - amount) <= 0) {
				PreparedStatement ps2 = conn.prepareStatement("DELETE FROM fine WHERE borid=?");
				ps2.setInt(1, borid);
				ps2.executeUpdate();
				ps2.close();
				conn.commit();
				return "Successfully paid fine.";
			}
			PreparedStatement ps3 = conn.prepareStatement("UPDATE fine SET amount=? WHERE borid=?");
			ps3.setInt(1, (moneyOwed - amount));
			ps3.setInt(2, borid);
			ps3.executeUpdate();
			ps3.close();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Thank you for your payment. Please pay the remainder of the fine soon.";
	}
	
	public static List<String[]> getOverdueItems() {
		// TODO Auto-generated method stub
		List<String[]> result = new ArrayList<String[]>();
		String borrowerType, email, callNum, copyNum;
		Date dueDate;
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT callNumber,copyNo,bType,emailAddress,outDate FROM borrowing,borrower WHERE borrowing.bid=borrower.bid AND inDate IS NULL");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				borrowerType = rs.getString(3);
				dueDate = getDueDate(rs.getDate(5), borrowerType);
				if (today.before(dueDate)) {
					email = rs.getString(4);
					callNum = rs.getString(1);
					copyNum = Integer.toString(rs.getInt(2));
					String[] overdue = {callNum, copyNum, email};
					result.add(overdue);
				}
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
    
	public static String getOverdueEmail(String callNumber, String borrowerid) {
		return "email";
		// TODO Auto-generated method stub
		
	}
    
	public static String addBook(String callNumber, String isbn, String title,
                                 String author, String publisher, String publishedYear) {
		// TODO Auto-generated method stub
		return null;
	}
    
	public static String generateBookReport(String subject) {
		// TODO Auto-generated method stub
		return null;
	}
    
	public static String listMostPopularItems(String year, String n) {
		// TODO Auto-generated method stub
		return null;
	}
}
