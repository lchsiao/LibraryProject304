package sql;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LibrarySQLUtil {
    
	// db fields
	private static final String CONNECT_URL = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug";
	private static final String USER = "ora_d5l8";
	private static final String PASSWORD = "a52632056";
    
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

		Date today = new java.util.Date();
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO borrower (bid,bPass,bName,address,phone,emailAddress,sinOrStNo,expiryDate,bType) "
                                                         + "VALUES (seq_borrower.nextval,?,?,?,?,?,?,?,?)");
			ps.setString(1, password);
			ps.setString(2, name);
			ps.setString(3, address);
			ps.setString(4, phone);
			ps.setString(5, email);
			ps.setString(6, sinOrStdNo);
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
				conn.rollback();
				return e.getMessage();
			} catch (SQLException e1) {
				return e1.getMessage();
			}
		}
		return SUCCESS_STRING + "New borrower " +  "added.";
    }
    
    // checks out the items provided by the list parameter 
	public static String checkOutItems(String bid, List<String> items) {
        
		String result = "";
		ResultSet rs = null;
	    String borrowerType;
	    Date today = new java.util.Date();
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
			return e2.getMessage();
		}
		try {
			PreparedStatement p2 = conn.prepareStatement("SELECT * FROM fine,borrowing WHERE bid=? AND fine.borid=borrowing.borid");
			p2.setString(1, bid);
			ResultSet r2 = p2.executeQuery();
			if (r2.next()) {
				r2.close();
				p2.close();
				return "This borrower is blocked because he/she has an unpaid fine.";
			}
			r2.close();
			p2.close();
		} catch (SQLException e3) {
			return e3.getMessage();
		}
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT c.callNumber,copyNo,title,hid FROM " +
					"((SELECT a.callNumber, copyno, title FROM book a, bookcopy b WHERE a.callnumber=b.callNumber AND a.callnumber=?" +
					" AND (copystatus='in' OR copystatus='on-hold') ORDER BY copystatus DESC) c LEFT JOIN holdrequest d ON c.callnumber=d.callnumber AND d.bid=?)");
			PreparedStatement ps2 = conn.prepareStatement("UPDATE bookCopy"
														+ " SET copyStatus='out'"
														+ " WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps3 = conn.prepareStatement("INSERT INTO borrowing (borid,bid,callNumber,copyNo,outDate,inDate)"
														+ " VALUES (seq_borrowing.nextval,?,?,?,?,?)");
			PreparedStatement ps4 = conn.prepareStatement("DELETE FROM holdrequest WHERE hid=?");
			
			List<String> hidsToDelete = new ArrayList<String>();
			
			for (int i = 0; i < items.size(); i++) {
				ps.setString(1,items.get(i));
				ps.setString(2, bid);
				rs = ps.executeQuery();
				
				if (rs.next()) {
					
					// check if on-hold
					String hid = rs.getString(4);
					if (hid != null && !hid.isEmpty())
						hidsToDelete.add(hid);
					
					ps2.setString(1, rs.getString(1));
					ps2.setInt(2, rs.getInt(2));
					ps2.executeUpdate();
					
					ps3.setString(1, bid);
					ps3.setString(2, rs.getString(1));
					ps3.setInt(3, rs.getInt(2));
					ps3.setDate(4, new java.sql.Date(today.getTime()));
					ps3.setDate(5, null);
					ps3.executeUpdate();
					
					result = result + "Successfully checked out " + rs.getString(3) + ". Due on " + getDueDate(today, borrowerType) + "\r\n";
				}
			}
			conn.commit();
			if (rs != null)
				rs.close();
			ps.close();
			ps2.close();
			ps3.close();
			
			// delete hold requests
			for (String hid : hidsToDelete) {
				ps4.setString(1, hid);
				ps4.addBatch();
			
			}
			ps4.executeBatch();
			ps4.close();
			
		} catch (SQLException e) {
			try {
				conn.rollback();
				return e.getMessage();
			} catch (SQLException e1) {
				return e1.getMessage();
			}
		}
		
		if (result.isEmpty()) {
			return "All of the bookz you have selected are out or on-hold.";
		}
		return SUCCESS_STRING + "Here are the bookz you have checked out:\r\n" + result;
	}
    
	// find the books that match the title, author, and/or subject parameters
	public static List<String[]> searchBooks(String title, String author, String subject) {
		String tempCallNumber, tempTitle, tempStatus;
		int numIn, numOut, numOnHold;
		List<String[]> result = new ArrayList<String[]>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT book.callNumber,title "
                                                         + "FROM book,hasAuthor,hasSubject "
                                                         + "WHERE book.callNumber=hasAuthor.callNumber AND book.callNumber=hasSubject.callNumber "
                                                         + "AND (title LIKE ? OR aName=? OR bookSubject=?)");
			PreparedStatement ps2 = conn.prepareStatement("SELECT copyStatus,COUNT(*) "
                                                          + "FROM bookCopy "
                                                          + "WHERE callNumber=? "
                                                          + "GROUP BY copyStatus ORDER BY copyStatus");
			
			ps.setString(1, "%" + title + "%");
			ps.setString(2, author);
			ps.setString(3, subject);
			ResultSet rs = ps.executeQuery(), rs2 = null;
			
			while (rs.next()) {
                numIn = 0;
                numOut = 0;
                numOnHold = 0;
				tempCallNumber = rs.getString(1);
				tempTitle = rs.getString(2);
				ps2.setString(1, tempCallNumber);
				rs2 = ps2.executeQuery();
				while (rs2.next()) {
                    tempStatus = rs2.getString(1);
                    if (tempStatus.equals("in")) {
                        numIn = rs2.getInt(2);
                    }
                    if (tempStatus.equals("out")) {
                        numOut = rs2.getInt(2);
                    }
                    if (tempStatus.equals("on-hold")) {
                        numOnHold = rs2.getInt(2);
                    }
                }
                String[] array = {tempTitle, tempCallNumber, Integer.toString(numIn), Integer.toString(numOut), Integer.toString(numOnHold)};
                result.add(array);
			}
			ps.close();
		    rs.close();
			ps2.close();
			if (rs2 != null)
				rs2.close();
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
    
	// return the book with callNumber and copyNo that matchs the callNum and copyNum parameters
	public static String processReturn(String callNum, int copyNum) {
		int borID;
		String bid, borrowerType;
		Date borrowedDate, dueDate;
		Date today = new java.util.Date();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT borid,bid,outDate FROM borrowing WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps2 = conn.prepareStatement("UPDATE borrowing SET inDate=? WHERE borid=?");
			PreparedStatement ps3 = conn.prepareStatement("SELECT bid FROM holdRequest WHERE callNumber=? AND flag='false'");
			PreparedStatement ps4 = conn.prepareStatement("UPDATE bookCopy SET copyStatus='in' WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps5 = conn.prepareStatement("UPDATE bookCopy SET copyStatus='on-hold' WHERE callNumber=? AND copyNo=?");
			PreparedStatement ps5a = conn.prepareStatement("UPDATE holdRequest C SET flag='true' WHERE C.hid=(SELECT MIN(H.hid) from holdRequest H WHERE callNumber=? AND flag='false'");
			PreparedStatement ps7 = conn.prepareStatement("SELECT bType FROM borrower WHERE bid=?");
			PreparedStatement ps8 = conn.prepareStatement("INSERT INTO fine (fid,amount,issuedDate,paidDate,borid)"
                                                          + "VALUES (seq_fine.nextval,?,?,?,?)");
			PreparedStatement ps9 = conn.prepareStatement("Select title FROM book WHERE callNumber=?");
		
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
			
			ps7.setString(1, bid);
			ResultSet rs7 = ps7.executeQuery();
			rs7.next();
			borrowerType = rs7.getString(1);
			rs7.close();
			ps7.close();
			dueDate = getDueDate(borrowedDate, borrowerType);
			if (today.after(dueDate)) {
				// assess the fine if overdue
				int fine = (int) ((Math.round((float)((today.getTime() - dueDate.getTime())/86400000))) * 1);
				ps8.setInt(1, fine);
				ps8.setDate(2, new java.sql.Date(today.getTime()));
				ps8.setDate(3, null);
				ps8.setInt(4, borID);
				ps8.executeUpdate();
				ps8.close();
			}
			
			ps2.setDate(1, new java.sql.Date(today.getTime()));
			ps2.setInt(2, borID);
			ps2.executeUpdate();
			ps2.close();
			
			ps3.setString(1, callNum);
			ResultSet rs3 = ps3.executeQuery();
			if (rs3.next()) {
				PreparedStatement ps6 = conn.prepareStatement("SELECT bName,emailAddress FROM borrower WHERE bid=?");
				ps6.setString(1, rs3.getString(1));
				ResultSet rs6 = ps6.executeQuery();
				
				if (rs6.next()) {
					String name = rs6.getString(1);
					String email = rs6.getString(2);
					
					rs6.close();
					ps6.close();
					
					ps5.setString(1, callNum);
					ps5.setInt(2, copyNum);
					ps5a.setString(1, callNum);
					ps5.executeUpdate();
					ps5a.executeUpdate();	
					ps5.close();
					ps5a.close();
					conn.commit();
					
					ps9.setString(1, callNum);
					ResultSet rs9 = ps9.executeQuery();
					String bookTitle = "";
					if (rs9.next()) {
						bookTitle = rs9.getString(1);
					}
					rs9.close();
					ps9.close();
					
					StringBuilder result = new StringBuilder(SUCCESS_STRING);
					result.append(" ").append(name).append(" notified by email at ").append(email)
						.append(" for ").append(bookTitle).append("(").append(callNum).append(")");
					return result.toString();
				}
			} else {
				ps4.setString(1, callNum);
				ps4.setInt(2, copyNum);
				ps4.executeUpdate();
				ps4.close();
			}
			rs3.close();
			ps3.close();
		} catch (SQLException e) {
			return e.getMessage();
		}
		return SUCCESS_STRING;
	}
    
	// display information of the borrower with this bid
	public static List<List<String[]>> checkAcct(String bid) {
        
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
			PreparedStatement ps2 = conn.prepareStatement("SELECT amount,borrowing.callNumber,title"
                                                          + " FROM fine,borrowing,book"
                                                          + " WHERE fine.borid=borrowing.borid AND borrowing.callNumber=book.callNumber AND bid=?");
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
				String[] borrow = {callNum, title, copyNum, dueDate};
				borrows.add(borrow);
			}
			rs.close();
			ps.close();
			ps2.setString(1, bid);
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				fineAmount = "$" + Integer.toString(rs2.getInt(1)) + ".00";
				callNum = rs2.getString(2);
				title = rs2.getString(3);
				String[] fine = {callNum, title, fineAmount};
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
			e.printStackTrace();
		}
		return result;
	}
    
	// add a new hold request
	public static String holdRequest(String bid, String callNumber) {
        
		Date today = new java.util.Date();
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
			PreparedStatement ps3 = conn.prepareStatement("INSERT INTO holdRequest (hid,bid,callNumber,issuedDate,flag) VALUES (seq_holdRequest.nextval,?,?,?,?)");
			PreparedStatement ps4 = conn.prepareStatement("UPDATE bookCopy SET copyStatus='on-hold' WHERE callNumber=? AND copyNo=?");
			
			ps2.setString(1, callNumber);
			ResultSet rs2 = ps2.executeQuery();
			if (!rs2.next()) {
				rs2.close();
				ps2.close();
				return "Hold request failed. All copies of this item are on-hold.";
			}
			tempCopyNo = rs2.getInt(1);
			rs2.close();
			ps2.close();
			
			ps3.setString(1, bid);
			ps3.setString(2, callNumber);
			ps3.setDate(3, new java.sql.Date(today.getTime()));
			ps3.setString(4, "false");
			ps3.executeUpdate();
			ps3.close();
			
			ps4.setString(1, callNumber);
			ps4.setInt(2, tempCopyNo);
			ps4.executeUpdate();
			ps4.close();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Hold request for item " + callNumber + " was successful.";
	}
    
	// pay a fine that resulted from a particular borrowing instance
	public static String payFines(int borid, int amount) {
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
			e.printStackTrace();
		}
		return "Thank you for your payment. Please pay the remainder of the fine soon.";
	}
	
	// display overdue items
	public static List<String[]> getOverdueItems() {
        
		List<String[]> result = new ArrayList<String[]>();
		String borrowerType, email, callNum, title, borrowerName;
		Date dueDate;
		Date today = new java.util.Date();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT book.callNumber,bName,bType,emailAddress,outDate,title"
                                                         + " FROM borrowing,borrower,book"
                                                         + " WHERE borrowing.bid=borrower.bid AND book.callNumber=borrowing.callNumber AND inDate IS NULL"
                                                         + " ORDER BY bName");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				borrowerType = rs.getString(3);
				dueDate = getDueDate(rs.getDate(5), borrowerType);
				if (today.after(dueDate)) {
					email = rs.getString(4);
					callNum = rs.getString(1);
					title = rs.getString(6);
					borrowerName = rs.getString(2);
					String[] overdue = {borrowerName, title, callNum, email};
					result.add(overdue);
				}
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// add a 
	public static String addBook(String callNumber, String isbn, String title,
                                 String author, String authors, String subjects, String publisher, String publishedYear) {
        try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO book VALUES (?,?,?,?,?,?)");
			
			ps.setString(1, callNumber);
			ps.setString(2, isbn);
			ps.setString(3, title);
			ps.setString(4, author);
			ps.setString(5, publisher);
			ps.setInt(6, Integer.parseInt(publishedYear));
			ps.execute();
		    conn.commit();
			ps.close();
		} catch (SQLException e) {
			try {
				conn.rollback();
				return e.getMessage();
			} catch (SQLException e1) {
				return e1.getMessage();
			}
		}
		
		if (!subjects.isEmpty()) {
			
			String[] subjectsArray = subjects.split(",");
			
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO hasSubject VALUES (?,?)");
				
				for (String subject : subjectsArray) {
					ps.setString(1, callNumber);
					ps.setString(2, subject.trim());
					ps.addBatch();
				}
				ps.executeBatch();
			    conn.commit();
				ps.close();
				
			} catch (SQLException e) {
				try {
					conn.rollback();
					return e.getMessage();
				} catch (SQLException e1) {
					return e1.getMessage();
				}
			}
		}
		
		if (!authors.isEmpty()) {
			String[] authorsArray = authors.split(",");
			
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO hasAuthor VALUES (?,?)");
				
				for (String aauthor : authorsArray) {
					ps.setString(1, callNumber);
					ps.setString(2, aauthor.trim());
					ps.addBatch();
				}
				ps.executeBatch();
			    conn.commit();
				ps.close();
				
			} catch (SQLException e) {
				try {
					conn.rollback();
					return e.getMessage();
				} catch (SQLException e1) {
					return e1.getMessage();
				}
			}
		}
			
			
		
		
		return SUCCESS_STRING + "New book " +  "added.";
		
	}
    
	/**
	 * Generate a report with all the books that have been checked out.
	 *
	 * should return List<String[]>: Call Number, Copy Num, Title, CheckOut Date, Due Date, Overdue Y/N?
	 * should be ordered by Call Number
	 * if Subject field is not empty, generate checkOut books pertaining to the subject
     **/
	
	public static List<String[]> generateBookReport(String subject) {
        
		List<String[]> result = new ArrayList<String[]>();
		Date outDate, dateDue, today = new java.util.Date();
		String callNum, copyNum, title, checkOut, dueDate, overdue = "N", borrowerType;
		PreparedStatement ps;
		try {
			if (subject.isEmpty()) {
                ps = conn.prepareStatement("SELECT book.callNumber,copyNo,title,outDate,bType"
                							+ " FROM book,borrowing,borrower"
                                            + " WHERE borrowing.callNumber=book.callNumber AND borrower.bid=borrowing.bid AND inDate IS NULL"
                                            + " ORDER BY callNumber");
			} else {
				ps = conn.prepareStatement("SELECT book.callNumber,copyNo,title,outDate,bType"
											+ " FROM book,borrowing,borrower,hasSubject"
                                            + " WHERE borrowing.callNumber=book.callNumber AND borrower.bid=borrowing.bid"
                                            + " AND inDate IS NULL AND bookSubject=? AND hasSubject.callNumber=borrowing.callNumber"
                                            + " ORDER BY callNumber");
			    ps.setString(1, subject);
                
			}
			ResultSet rs = ps.executeQuery();
     		while (rs.next()) {
				callNum = rs.getString(1);
				copyNum = Integer.toString(rs.getInt(2));
				title = rs.getString(3);
				outDate = rs.getDate(4);
				checkOut = "" + outDate + "";
				borrowerType = rs.getString(5);
				dateDue = getDueDate(outDate, borrowerType);
				dueDate = "" + dateDue + "";
				if (today.after(dateDue))
					overdue = "Y";
				String[] book = {callNum, copyNum, title, checkOut, dueDate, overdue};
				result.add(book);
				overdue = "N";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
    
	public static List<String[]> listMostPopularItems(String year, int n) {
		String title, author, callNum;
		int count, i = 0;
		List<String[]> result = new ArrayList<String[]>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT title, mainAuthor, borrowing.callNumber, COUNT(*) AS scount"
                                                         + " FROM borrowing, book"
                                                         + " WHERE borrowing.callNumber=book.callNumber AND TO_CHAR(outDate, 'mm/dd/yyyy') LIKE ?"
                                                         + " GROUP BY title, mainAuthor, borrowing.callNumber"
                                                         + " ORDER BY scount");
			ps.setString(1, "%" + year + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next() && i < n) {
				title = rs.getString(1);
				author = rs.getString(2);
				callNum = rs.getString(3);
				count = rs.getInt(4);
				String[] item = {title, author, callNum, Integer.toString(count)};
				i++;
				result.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
    
	private static Date getDueDate(Date borrowDate, String borrowerType) {
		Date dueDate;
		if (borrowerType.equals("Student")) {
			dueDate = new Date(borrowDate.getTime() + 1209600000);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(borrowDate);
			if (borrowerType.equals("Staff")) {
				cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + 6);
				dueDate = cal.getTime();
			} else {
				cal.set(Calendar.WEEK_OF_YEAR, cal.get(Calendar.WEEK_OF_YEAR) + 12);
				dueDate = cal.getTime();
			}
		}
		return dueDate;
	}
}
