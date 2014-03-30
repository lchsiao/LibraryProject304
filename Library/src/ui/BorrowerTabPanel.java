package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import sql.LibrarySQLUtil;

@SuppressWarnings("serial")
public class BorrowerTabPanel extends UserTabPanel {

	// createSearchBooks fields
	private static final String SEARCH_BOOKS_ACTION = "SEARCHBOOKS";
	
	private JFrame booksFrame;
	private String[] booksTableHeader;
	private JTextField titleField;
	private JTextField authorField;
	private JTextField subjectField;
	
	// createCheckAccount fields
	private static final String CHECK_ACCT_ACTION = "CHECKACCT";
		
	private JTextField checkAcctBidField;
	
	// createRequestHold fields
	private static final String HOLD_REQUEST_ACTION = "HOLDREQUEST";
	
	private JTextField requestHoldBidField;
	
	private JTextField callNumberField;
	
	// createPayFines fields
	private static final String PAY_FINES_ACTION = "PAYFINES";
	
	private JTextField boridField;
	private JTextField amountField;
	
	
	@Override
	protected void initializeCards() {

		createSearchBooksPanel();
		createCheckAccountPanel();
		createRequestHoldPanel();
		createPayFinesPanel();
		
	}

	/**
	* Search for books using keyword search on titles, authors and subjects. 
	* 
	* The result is a list of books that match the search together 
	* with the number of copies that are in and out.
	**/
	
	private void createSearchBooksPanel() {
		
		JPanel createSearchBooksPanel = new JPanel(new BorderLayout());
		createSearchBooksPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createSearchBooksPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createSearchBooksPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel titleLabel = new JLabel("Title:");
		titleField = new JTextField();
		createSearchBooksPanelTop.add(titleLabel);
		createSearchBooksPanelTop.add(titleField);

		JLabel authorLabel = new JLabel("Author:");
		authorField = new JTextField();
		createSearchBooksPanelTop.add(authorLabel);
		createSearchBooksPanelTop.add(authorField);
		
		JLabel subjectLabel = new JLabel("Subject:");
		subjectField = new JTextField();
		createSearchBooksPanelTop.add(subjectLabel);
		createSearchBooksPanelTop.add(subjectField);
		
		createSearchBooksPanel.add(createSearchBooksPanelTop, BorderLayout.PAGE_START);

		JButton searchBooksSubmit = new JButton("Search Books");
		createSearchBooksPanel.add(searchBooksSubmit, BorderLayout.CENTER);
		searchBooksSubmit.addActionListener(this);
		searchBooksSubmit.setActionCommand(SEARCH_BOOKS_ACTION);
		
		booksFrame = new JFrame();
		booksTableHeader = new String[] {"Title", "Call No.", "In", "On", "On Hold"};
		
		this.addCard("Search Books", createSearchBooksPanel);
	}

	/**
	 * Check his/her account. 
	 * 
	 * The system will display the items the borrower has currently borrowed 
	 * and not yet returned, any outstanding fines and 
	 * the hold requests that have been placed by the borrower.
	 */
	
	private void createCheckAccountPanel() {
		
		JPanel createCheckAccountPanel = new JPanel(new BorderLayout());
		createCheckAccountPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createCheckAccountPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createCheckAccountPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel bidLabel = new JLabel("Card Number:");
		checkAcctBidField = new JTextField();
		createCheckAccountPanelTop.add(bidLabel);
		createCheckAccountPanelTop.add(checkAcctBidField);

		createCheckAccountPanel.add(createCheckAccountPanelTop, BorderLayout.PAGE_START);

		JButton checkAccountSubmit = new JButton("Check Account");
		createCheckAccountPanel.add(checkAccountSubmit, BorderLayout.CENTER);
		checkAccountSubmit.addActionListener(this);
		checkAccountSubmit.setActionCommand(CHECK_ACCT_ACTION);

		this.addCard("Check Account", createCheckAccountPanel);
	}


	/**
	 * Place a hold request for a book that is out. 
	 * 
	 * When the item is returned, the system sends an email to the borrower and 
	 * informs the library clerk to keep the book out of the shelves.
	 */
	
	private void createRequestHoldPanel() {
		
		JPanel createRequestHoldPanel = new JPanel(new BorderLayout());
		createRequestHoldPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createRequestHoldPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createRequestHoldPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel bidLabel = new JLabel("Card Number:");
		requestHoldBidField = new JTextField();
		createRequestHoldPanelTop.add(bidLabel);
		createRequestHoldPanelTop.add(requestHoldBidField);
		
		JLabel callNumberLabel = new JLabel("Call Number:");
		callNumberField = new JTextField();
		createRequestHoldPanelTop.add(callNumberLabel);
		createRequestHoldPanelTop.add(callNumberField);

		createRequestHoldPanel.add(createRequestHoldPanelTop, BorderLayout.PAGE_START);

		JButton placeHoldRequestSubmit = new JButton("Place Hold Request");
		createRequestHoldPanel.add(placeHoldRequestSubmit, BorderLayout.CENTER);
		placeHoldRequestSubmit.addActionListener(this);
		placeHoldRequestSubmit.setActionCommand(HOLD_REQUEST_ACTION);

		this.addCard("Request Hold", createRequestHoldPanel);
	}

	/**
	 * Pay a fine.
	 */
	
	private void createPayFinesPanel() {
		
		JPanel createPayFinesPanel = new JPanel(new BorderLayout());
		createPayFinesPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createPayFinesPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createPayFinesPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel boridLabel = new JLabel("Borrow Reference:");
		boridField = new JTextField();
		createPayFinesPanelTop.add(boridLabel);
		createPayFinesPanelTop.add(boridField);
		
		JLabel amountLabel = new JLabel("Amount:");
		amountField = new JTextField();
		createPayFinesPanelTop.add(amountLabel);
		createPayFinesPanelTop.add(amountField);

		createPayFinesPanel.add(createPayFinesPanelTop, BorderLayout.PAGE_START);

		JButton payFineSubmit = new JButton("Pay Fine");
		createPayFinesPanel.add(payFineSubmit, BorderLayout.CENTER);
		payFineSubmit.addActionListener(this);
		payFineSubmit.setActionCommand(PAY_FINES_ACTION);

		
		this.addCard("Pay Fines", createPayFinesPanel);
	}

	private void searchBooks() {
		
		String title = titleField.getText();
		String author = authorField.getText();
		String subject = subjectField.getText();
		
		if (title.isEmpty() || author.isEmpty() || subject.isEmpty()) {
			showDefaultError();
			return;
		}
		
		List<String[]> result = LibrarySQLUtil.searchBooks(title, author, subject);
		String[][] data = result.toArray(new String[result.size()][]);
		if (data.length != 0) {
			
			JTable booksTable = new JTable(data, booksTableHeader);
			booksTable.setPreferredScrollableViewportSize(new Dimension(600, booksTable.getPreferredSize().height));
			booksTable.setFillsViewportHeight(true);
			JScrollPane scroll = new JScrollPane(booksTable);
			
			booksFrame.add(scroll);
			booksFrame.pack();
			
			// center the frame
			Dimension d = booksFrame.getToolkit().getScreenSize();
			Rectangle r = booksFrame.getBounds();
			booksFrame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );
			
			booksFrame.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "No results found");
		}
		
		
		
	}

	private void checkAcct() {
		
		String bid = checkAcctBidField.getText();
		
		if (bid.isEmpty()) {
			showDefaultError();
			return;
		}
		
		String result = LibrarySQLUtil.checkAcct(bid);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			// Display the account details in a new JPanel
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	private void holdRequest() {

		String bid = requestHoldBidField.getText();
		String callNumber = callNumberField.getText();
		
		if (bid.isEmpty() || callNumber.isEmpty()) {
			showDefaultError();
			return;
		}
		
		String result = LibrarySQLUtil.holdRequest(bid, callNumber);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			// Display the account details in a new JPanel
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	private void payFines() {
		
		String borid = boridField.getText();
		String amount = amountField.getText();
			
		
		if (borid.isEmpty()) {
			showDefaultError();
			return;
		}
		
		String result = LibrarySQLUtil.payFines(borid, amount);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			// Display the account details in a new JPanel
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		switch (e.getActionCommand()) {
		
			case SEARCH_BOOKS_ACTION:
				searchBooks();
				break;
			case CHECK_ACCT_ACTION:
				checkAcct();
				break;
			case HOLD_REQUEST_ACTION:
				holdRequest();
				break;
			case PAY_FINES_ACTION:
				payFines();
				break;
				
		}
	
	}
	
}
