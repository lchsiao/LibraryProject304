package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import sql.LibrarySQLUtil;

@SuppressWarnings("serial")
public class LibrarianTabPanel extends UserTabPanel {

	//addBook fields
	private static final String ADD_BOOK_ACTION = "ADDBOOK";
	
	private JTextField callNumberField;
	private JTextField isbnField;
	private JTextField titleField;
	private JTextField authorField;
	private JTextField authorsField;
	private JTextField subjectsField;
	private JTextField publisherField;
	private JTextField publishedYearField;	
	
	
	//generateBookReport fields
	private static final String GENERATE_BOOK_REPORT_ACTION = "GENBOOKREPORT";
	
	private static final String[] HEADER_BOOK_REPORT =  new String[] {"Call Number", "Copy No", "Title",
																		"CheckOut Date", "Due Date", "Overdue Y/N?"};
	
	private JTextField subjectField;
	private JFrame bookReportFrame;
	
	//listMostPopularItems fields
	private static final String LIST_MOST_POPULAR_ITEMS_ACTION = "LISTPOPITEMS";

	private static final String[] HEADER_MOST_POPULAR_ITEMS = new String[] {"Title", "Author", "Call Number", 
																				"Number of times borrowed"};
	
	private JTextField yearField;
	private JTextField nField;
	private JFrame mostPopularFrame;
	
	
	@Override
	protected void initializeCards() {
		
		createAddBookPanel();
		createGenerateBookReportPanel();
		createListMostPopularItemsPanel();
		
	}
	
	
	/**
	 * Adds a new book or new copy of an existing book to the library. 
	 * The librarian provides the information for the new book, and the system adds it to the library.
	 */
	
	private void createAddBookPanel() {
		
		
		JPanel createAddBookPanel = new JPanel(new BorderLayout());
		createAddBookPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createAddBookPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createAddBookPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel callNumberLabel = new JLabel("Call Number:");
		callNumberField = new JTextField();
		createAddBookPanelTop.add(callNumberLabel);
		createAddBookPanelTop.add(callNumberField);
		callNumberField.setToolTipText("Required: Enter the unique call number for this book (max 20 characters). Last 4 digits must be the book's published year.");

		
		JLabel isbnLabel = new JLabel("ISBN:");
		isbnField = new JTextField();
		createAddBookPanelTop.add(isbnLabel);
		createAddBookPanelTop.add(isbnField);
		isbnField.setToolTipText("Required: Enter the unique isbn for this book (max 20 characters).");

		JLabel titleLabel = new JLabel("Book Title:");
		titleField = new JTextField();
		createAddBookPanelTop.add(titleLabel);
		createAddBookPanelTop.add(titleField);
		titleField.setToolTipText("Required: Enter the book's title (max 50 characters).");
		
		JLabel authorLabel = new JLabel("Main Author:");
		authorField = new JTextField();
		createAddBookPanelTop.add(authorLabel);
		createAddBookPanelTop.add(authorField);
		authorField.setToolTipText("Required: Enter the book's first author (max 30 characters).");
		
		JLabel authorsLabel = new JLabel("Additional Authors:");
		authorsField = new JTextField();
		createAddBookPanelTop.add(authorsLabel);
		createAddBookPanelTop.add(authorsField);
		authorsField.setToolTipText("Optional: For additional author names, separate by commas.");
		
		JLabel subjectsLabel = new JLabel("Subjects:");
		subjectsField = new JTextField();
		createAddBookPanelTop.add(subjectsLabel);
		createAddBookPanelTop.add(subjectsField);
		subjectsField.setToolTipText("Optional: For more than one subject, separate by commas.");
		
		JLabel publisherLabel = new JLabel("Publisher:");
		publisherField = new JTextField();
		createAddBookPanelTop.add(publisherLabel);
		createAddBookPanelTop.add(publisherField);
		publisherField.setToolTipText("Required: Enter the publisher for this book (max 30 characters).");
		
		JLabel publishedYearLabel = new JLabel("Published Year:");
		publishedYearField = new JFormattedTextField(yearFormat);
		createAddBookPanelTop.add(publishedYearLabel);
		createAddBookPanelTop.add(publishedYearField);
		publishedYearField.setToolTipText("Required: Enter the book's year of publication. This should be used as the last 4 digits for call number.");
		
		createAddBookPanel.add(createAddBookPanelTop, BorderLayout.PAGE_START);

		JButton addBookSubmit = new JButton("Add Book");
		createAddBookPanel.add(addBookSubmit, BorderLayout.CENTER);
		addBookSubmit.addActionListener(this);
		addBookSubmit.setActionCommand(ADD_BOOK_ACTION);

		this.addCard("Add Book", createAddBookPanel);
	}


	/**
	 * Generate a report with all the books that have been checked out. 
	 * 
	 * For each book the report shows the date it was checked out and the due date. 
	 * The system flags the items that are overdue. 
	 * The items are ordered by the book call number. 
	 * If a subject is provided the report lists only books related to that subject, 
	 * otherwise all the books that are out are listed by the report.
	 */
	
	private void createGenerateBookReportPanel() {
		
		JPanel createGenerateBookReportPanel = new JPanel(new BorderLayout());
		createGenerateBookReportPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createGenerateBookReportPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createGenerateBookReportPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JLabel subjectLabel = new JLabel("Optional Subject:");
		subjectField = new JTextField();
		createGenerateBookReportPanelTop.add(subjectLabel);
		createGenerateBookReportPanelTop.add(subjectField);
		subjectField.setToolTipText("Enter a subject name for constraining book report results pertaining only to those books related to the subject field.");
		
		createGenerateBookReportPanel.add(createGenerateBookReportPanelTop, BorderLayout.PAGE_START);

		JButton generateBookReportSubmit = new JButton("Generate Book Report");
		createGenerateBookReportPanel.add(generateBookReportSubmit, BorderLayout.CENTER);
		generateBookReportSubmit.addActionListener(this);
		generateBookReportSubmit.setActionCommand(GENERATE_BOOK_REPORT_ACTION);
		
		bookReportFrame = new JFrame("Book Report");
		
		this.addCard("Generate Book Report", createGenerateBookReportPanel);
	}


	/**
	 * Generate a report with the most popular items in a given year. 
	 * 
	 * The librarian provides a year and a number n. 
	 * The system lists out the top n books that where borrowed the most times during that year. 
	 * The books are ordered by the number of times they were borrowed.
	 */
	
	private void createListMostPopularItemsPanel() {
		
		JPanel createListMostPopularItemsPanel = new JPanel(new BorderLayout());
		createListMostPopularItemsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel createListMostPopularItemsPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		createListMostPopularItemsPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JLabel yearLabel = new JLabel("Year:");
		yearField = new JFormattedTextField(yearFormat);
		createListMostPopularItemsPanelTop.add(yearLabel);
		createListMostPopularItemsPanelTop.add(yearField);
		yearField.setToolTipText("Required: Enter the year in which you are interested in finding the most popular books.");
		
		JLabel nLabel = new JLabel("Number of top books to be displayed:");
		nField = new JFormattedTextField(intFormat);
		createListMostPopularItemsPanelTop.add(nLabel);
		createListMostPopularItemsPanelTop.add(nField);
		nField.setToolTipText("Required: Enter the number of top popular books you want to view.");
		
		createListMostPopularItemsPanel.add(createListMostPopularItemsPanelTop, BorderLayout.PAGE_START);

		JButton listMostPopularItemsSubmit = new JButton("List Most Popular Items");
		createListMostPopularItemsPanel.add(listMostPopularItemsSubmit, BorderLayout.CENTER);
		listMostPopularItemsSubmit.addActionListener(this);
		listMostPopularItemsSubmit.setActionCommand(LIST_MOST_POPULAR_ITEMS_ACTION);
		
		mostPopularFrame = new JFrame("Most Popular Books");
		
		this.addCard("List Most Popular Items", createListMostPopularItemsPanel);
	}


	private boolean addBook() {
		
		String callNumber = callNumberField.getText();
		String isbn = isbnField.getText();
		String title = titleField.getText();
		String author = authorField.getText();
		String authors = authorsField.getText();
		String subjects = subjectsField.getText();
		String publisher = publisherField.getText();
		String publishedYear = publishedYearField.getText();
		
		if (callNumber.isEmpty() || isbn.isEmpty() || title.isEmpty() || 
				author.isEmpty() || publisher.isEmpty() || publishedYear.isEmpty()) {
			
			showDefaultError();
			return false;
		}
	
		String result = LibrarySQLUtil.addBook(callNumber, isbn, title, author, authors, subjects, publisher, publishedYear);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			JOptionPane.showMessageDialog(this, result);
			
			callNumberField.setText("");
			isbnField.setText("");
			titleField.setText("");
			authorField.setText("");
			authorsField.setText("");
			subjectsField.setText("");
			publisherField.setText("");
			publishedYearField.setText("");
			
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return true;
		
	}


	private void generateBookReport() {
		
		String subject = subjectField.getText();
		
		List<String[]> result = LibrarySQLUtil.generateBookReport(subject);
		String[][] bookReportData = result.toArray(new String[result.size()][]);
		
		createAndDisplayPopupTable(bookReportFrame, bookReportData, HEADER_BOOK_REPORT);
	
	}


	private void listMostPopularItems() {
		
		String year = yearField.getText();
		String n = nField.getText();
		
		if (year.isEmpty() || n.isEmpty()) {
			showDefaultError();
			return;
		}
		
		List<String[]> result = LibrarySQLUtil.listMostPopularItems(year, Integer.parseInt(n));
		String [][] mostPopularData = result.toArray(new String[result.size()][]);
		
		createAndDisplayPopupTable(mostPopularFrame, mostPopularData, HEADER_MOST_POPULAR_ITEMS);
	}


	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch (e.getActionCommand()) {
		
		case ADD_BOOK_ACTION:
			addBook();
			break;
		case GENERATE_BOOK_REPORT_ACTION:
			generateBookReport();
			break;
		case LIST_MOST_POPULAR_ITEMS_ACTION:
			listMostPopularItems();
			break;
		}			
	}
}
