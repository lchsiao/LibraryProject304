package ui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import sql.LibrarySQLUtil;


@SuppressWarnings("serial")
public class ClerkTabPanel extends UserTabPanel {

	// addBorrower fields
	private static final String ADD_BORROWER_ACTION = "ADDBORROWER";

	private JTextField nameField;
	private JTextField passwordField;
	private JTextField addressField;
	private JTextField phoneField;
	private JTextField emailField;
	private JTextField sinOrStNoField;
	private JComboBox<String> typeField;

	// checkoutItem fields
	private static final String CHECK_OUT_ACTION = "CHECKOUT";
	private static final String ADD_ITEM_ACTION = "ADDITEM";
	private static final String REMOVE_ITEM_ACTION = "REMOVEITEM";

	private JPanel checkOutItemsPanelTop;
	private JTextField bidField;
	private List<JTextField> itemsField;
	
	// processReturn fields
	private static final String PROCESS_RETURN_ACTION = "PROCESSRETURN";

	private JTextField returnIDField;
	private JTextField copyNumberField;
	
	// checkOverdueItems fields
	private static final String SEND_NOTIFICATION_ACTION = "SENDNOTICIATION";
	private static final String[] HEADER_OVERDUE_ITEMS = new String[] {"Borrower Name", "Title", "Call Number", "Email", "Send Email"};
	
	private JPanel checkOverdueItemsPanel;
	private JTable overdueItemsTable;

	@Override
	protected void initializeCards() {

		createAddBorrowerPanel();
		createCheckOutPanel();
		createProcessReturnPanel();
		createOverdueItemsPanel();
		
	}
	

	private void createAddBorrowerPanel() {

		JPanel addBorrowerPanel = new JPanel(new BorderLayout());
		addBorrowerPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel addBorrowerPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		addBorrowerPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel nameLabel = new JLabel("Name:");
		nameField = new JTextField();
		addBorrowerPanelTop.add(nameLabel);
		addBorrowerPanelTop.add(nameField);
		nameField.setToolTipText("Required: Enter your full name to setup your icanhazbookz library account (max 30 characters).");

		JLabel passwordLabel = new JLabel("Password:");
		passwordField = new JPasswordField();
		addBorrowerPanelTop.add(passwordLabel);
		addBorrowerPanelTop.add(passwordField);
		passwordField.setToolTipText("Required: Enter your password (max 20 characters).");

		JLabel addressLabel = new JLabel("Address:");
		addressField = new JTextField();
		addBorrowerPanelTop.add(addressLabel);
		addBorrowerPanelTop.add(addressField);
		addressField.setToolTipText("Required: Enter your address (max 30 characters).");

		JLabel phoneLabel = new JLabel("Phone:");
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits(10);
		format.setMaximumIntegerDigits(10);
		format.setGroupingUsed(false);
		phoneField = new JFormattedTextField(format);
		addBorrowerPanelTop.add(phoneLabel);
		addBorrowerPanelTop.add(phoneField);
		phoneField.setToolTipText("Required: Enter your phone number.");

		JLabel emailLabel = new JLabel("Email Address:");
		emailField = new JTextField();
		addBorrowerPanelTop.add(emailLabel);
		addBorrowerPanelTop.add(emailField);
		emailField.setToolTipText("Required: Enter your email address (max 30 chars). This will be the primary mode of contact from icanhazbookz library.");

		JLabel sinOrStNoLabel = new JLabel("Sin or Student Number:");
		sinOrStNoField = new JTextField();
		addBorrowerPanelTop.add(sinOrStNoLabel);
		addBorrowerPanelTop.add(sinOrStNoField);
		sinOrStNoField.setToolTipText("Required: Enter your Student Number if you are a university student, otherwise supply your SIN.");

		JLabel typeLabel = new JLabel("Type:");
		String[] types = {"Student", "Faculty", "Staff"};
		typeField = new JComboBox<String>(types);
		addBorrowerPanelTop.add(typeLabel);
		addBorrowerPanelTop.add(typeField);
		typeField.setToolTipText("Select appropriate Borrower Type.");

		addBorrowerPanel.add(addBorrowerPanelTop, BorderLayout.PAGE_START);

		JButton addBorrowerSubmit = new JButton("Add Borrower");
		addBorrowerPanel.add(addBorrowerSubmit, BorderLayout.CENTER);
		addBorrowerSubmit.addActionListener(this);
		addBorrowerSubmit.setActionCommand(ADD_BORROWER_ACTION);

		this.addCard("Add Borrower", addBorrowerPanel);

	}

	
	private void createCheckOutPanel() {
		
		JPanel checkOutItemsPanel = new JPanel(new BorderLayout());
		checkOutItemsPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		checkOutItemsPanelTop = new JPanel(new GridLayout(0, 3, 10, 10));
		checkOutItemsPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel bidLabel = new JLabel("Card Number:");
		bidField = new JFormattedTextField(intFormat);
		checkOutItemsPanelTop.add(bidLabel);
		checkOutItemsPanelTop.add(bidField);
		checkOutItemsPanelTop.add(Box.createHorizontalGlue());
		bidField.setToolTipText("Required: Enter the borrower's card number.");

		JButton itemsButton = new JButton("Add Item");
		itemsButton.addActionListener(this);
		itemsButton.setActionCommand(ADD_ITEM_ACTION);
		itemsField = new LinkedList<JTextField>();
		checkOutItemsPanelTop.add(itemsButton);
		JTextField firstItem = new JTextField();
		firstItem.setToolTipText("Required: Enter the call number of the book to check out.");
		itemsField.add(firstItem);
		checkOutItemsPanelTop.add(firstItem);
		checkOutItemsPanelTop.add(Box.createHorizontalGlue());

		checkOutItemsPanel.add(checkOutItemsPanelTop, BorderLayout.PAGE_START);

		JButton checkOutItemsSubmit = new JButton("Check-out Items");
		checkOutItemsPanel.add(checkOutItemsSubmit, BorderLayout.CENTER);
		checkOutItemsSubmit.addActionListener(this);
		checkOutItemsSubmit.setActionCommand(CHECK_OUT_ACTION);

		this.addCard("Check-out Items", checkOutItemsPanel);

	}


	private void createProcessReturnPanel() {

		JPanel processReturnPanel = new JPanel(new BorderLayout());
		processReturnPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
		JPanel processReturnPanelTop = new JPanel(new GridLayout(0, 2, 10, 10));
		processReturnPanelTop.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel returnIDLabel = new JLabel("Item Call Number:");
		returnIDField = new JTextField();
		processReturnPanelTop.add(returnIDLabel);
		processReturnPanelTop.add(returnIDField);
		returnIDField.setToolTipText("Required: Enter the call number of the book that is being returned.");
		
		JLabel copyLabel = new JLabel("Copy Number:");
		copyNumberField = new JFormattedTextField(intFormat);
		processReturnPanelTop.add(copyLabel);
		processReturnPanelTop.add(copyNumberField);
		copyNumberField.setToolTipText("Required: Enter the book's copy number that is being returned.");

		processReturnPanel.add(processReturnPanelTop, BorderLayout.PAGE_START);

		JButton processReturnSubmit = new JButton("Process Return");
		processReturnPanel.add(processReturnSubmit, BorderLayout.CENTER);
		processReturnSubmit.addActionListener(this);
		processReturnSubmit.setActionCommand(PROCESS_RETURN_ACTION);
		
		this.addCard("Process Return", processReturnPanel);
	}
	
	
	private void createOverdueItemsPanel() {
		
		checkOverdueItemsPanel = new JPanel(new BorderLayout());
		checkOverdueItemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (((String) e.getItem()).equals("Check Overdue Items"))
					updateOverdueItems();
			}
		});
		
		updateOverdueItems();
		
		this.addCard("Check Overdue Items", checkOverdueItemsPanel);
	}
	
	
	private boolean addBorrower() {

		String name = nameField.getText();
		String password = passwordField.getText();
		String address = addressField.getText();
		String phone = phoneField.getText();
		String email = emailField.getText();
		String sinOrStdNo = sinOrStNoField.getText();
		String type = typeField.getItemAt(typeField.getSelectedIndex());

		if (name.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty() ||
				sinOrStdNo.isEmpty() || type.isEmpty()) {

			showDefaultError();

			return false;
		}

		String result = LibrarySQLUtil.addBorrower(name, password, address, phone, email, sinOrStdNo, type);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			
			nameField.setText("");
			passwordField.setText("");
			addressField.setText("");
			phoneField.setText("");
			emailField.setText("");
			sinOrStNoField.setText("");
			
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return true;
	}
	
	
	private void addItem() {
		
		Component parent = checkOutItemsPanelTop.getParent();
		if (checkOutItemsPanelTop.getPreferredSize().height >= parent.getHeight() - 30) {
			return;
		}
		
		int position = (checkOutItemsPanelTop.getComponentCount() - 1) + 3;
		
		checkOutItemsPanelTop.add(Box.createHorizontalGlue());
		JTextField newField = new JTextField();
		checkOutItemsPanelTop.add(newField);
		
		PositionAwareButton remove = new PositionAwareButton("X");
		remove.setPosition(position);
		remove.setActionCommand(REMOVE_ITEM_ACTION);
		remove.addActionListener(this);
		
		checkOutItemsPanelTop.add(remove);
		itemsField.add(newField);
		
		checkOutItemsPanelTop.getParent().validate();
	}
	
	
	private void removeItem(int buttonPosition) {
		
		int rowStartPosition = buttonPosition - 2;
		int listPosition = buttonPosition/3 - 1;
		
		itemsField.remove(listPosition);
		
		checkOutItemsPanelTop.remove(rowStartPosition);	
		checkOutItemsPanelTop.remove(rowStartPosition);
		checkOutItemsPanelTop.remove(rowStartPosition);
		
		for (Component c : checkOutItemsPanelTop.getComponents()) {
			
			if (c instanceof PositionAwareButton) {
				final PositionAwareButton b = (PositionAwareButton) c;
				final int oldPosition = b.getPosition();
				
				if (oldPosition > buttonPosition) {
					b.setPosition(oldPosition-3);
				}
			}
		}
		
		checkOutItemsPanelTop.getParent().validate();
	}
	
	private boolean checkOutItems() {
		
		String bid = bidField.getText();
		List<String> items = new ArrayList<>();
		boolean itemIsEmpty = false;
		
		for (JTextField field : itemsField) {
			String text = field.getText();
			
			if (field.getText().isEmpty()) {
				itemIsEmpty = true;
				break;
			}
			items.add(text);
		}

		if (bid.isEmpty() || itemIsEmpty || items.isEmpty()) {

			showDefaultError();
			
			return false;
		}

		String result = LibrarySQLUtil.checkOutItems(bid, items);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return true;
	}
	
	
	private boolean processReturn() {
		
		String returnID = returnIDField.getText();
		String copyNumber = copyNumberField.getText();
		
		if (returnID.isEmpty() || copyNumber.isEmpty()) {

			showDefaultError();

			return false;
		}

		String result = LibrarySQLUtil.processReturn(returnID, Integer.parseInt(copyNumber));
		if (result.equals(LibrarySQLUtil.SUCCESS_STRING)) {
			JOptionPane.showMessageDialog(this, result);
		}
		else if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			
			String bookString = result.substring(result.indexOf(" for ") + 5);
			String[] words = result.split(" ");
			String name = words[1];
			String email = result.substring(result.indexOf(" notified by email at ") + " notified by email at ".length(), result.indexOf(" for "));
			
			StringBuilder msgBuilder = new StringBuilder("Hello ").append(name).append(",\n\n");
			msgBuilder.append("Your held item: ").append(bookString).append(" is now ready for pick-up.");
			sendEmail("Held Item Ready for Pick-up", email, msgBuilder.toString());
			
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result);
		}

		return true;
		
	}
	
	
	private void updateOverdueItems() {
		
		checkOverdueItemsPanel.removeAll();
		
		List<String[]> items = LibrarySQLUtil.getOverdueItems();
		Object[][] itemsData = new Object[items.size()][];
		
		// add check boxes
		for (int i = 0; i < items.size(); i++) {
			
			String[] row = items.get(i);
			Object[] rowWithCheckBoxesStrings = new Object[row.length + 1];
			
			int j;
			for (j = 0; j < row.length; j++) {
				rowWithCheckBoxesStrings[j] = row[j];
			}
			
			rowWithCheckBoxesStrings[j] = new Boolean(false);
			
			itemsData[i] = rowWithCheckBoxesStrings;
		}
		
		overdueItemsTable = new JTable(itemsData, HEADER_OVERDUE_ITEMS) {
			
			@Override
			public java.lang.Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}
	 
	        public boolean isCellEditable(int row, int col) {
	            if (col == HEADER_OVERDUE_ITEMS.length-1) {
	                return true;
	            }
	            return false;
	        }
		};
		
		overdueItemsTable.setPreferredScrollableViewportSize(new Dimension(600, 200));
		overdueItemsTable.setFillsViewportHeight(true);

		JScrollPane scroll = new JScrollPane(overdueItemsTable);
		
		JButton sendNotificationButton = new JButton("Send Notification");
		sendNotificationButton.setActionCommand(SEND_NOTIFICATION_ACTION);
		sendNotificationButton.addActionListener(this);
		
		checkOverdueItemsPanel.add(scroll, BorderLayout.PAGE_START);
		checkOverdueItemsPanel.add(sendNotificationButton, BorderLayout.CENTER);
		
		checkOverdueItemsPanel.validate();
	}
	
	
	private void sendOverdueEmail() {
		
		int checkBoxIndex = HEADER_OVERDUE_ITEMS.length-1;
		int emailIndex = HEADER_OVERDUE_ITEMS.length-2;
		int nameIndex = 0;
		int callNoIndex = 2;
		int titleIndex = 1; 
		
		Map<String, List<String>> emailToBookMap = new HashMap<String, List<String>>();
		Map<String, List<String>> emailToNameMap = new HashMap<String, List<String>>();
		
		for (int i = 0; i < overdueItemsTable.getRowCount(); i++) {
			
			if ((Boolean) overdueItemsTable.getValueAt(i, checkBoxIndex)) {
				
				String email = (String) overdueItemsTable.getValueAt(i, emailIndex);
				String name = (String) overdueItemsTable.getValueAt(i, nameIndex);
				String callNoString = (String) overdueItemsTable.getValueAt(i, callNoIndex);
				String titleString = (String) overdueItemsTable.getValueAt(i, titleIndex);
				String bookString = new StringBuilder(titleString).append("(").append(callNoString).append(")").toString();
				
				if (!emailToBookMap.containsKey(email)) {
					emailToBookMap.put(email, new ArrayList<String>());
				}
				emailToBookMap.get(email).add(bookString);
				
				if (!emailToNameMap.containsKey(email)) {
					emailToNameMap.put(email, new ArrayList<String>());
				}
				emailToNameMap.get(email).add(name);
			}
		}
		
		
		for (String emailString : emailToBookMap.keySet()) {
			
			StringBuilder msgBuilder = new StringBuilder("Hello ");
			
			List<String> nameList = emailToNameMap.get(emailString);
			HashSet<String> hs = new HashSet<String>();
			hs.addAll(nameList);
			nameList.clear();
			nameList.addAll(hs);
			
			if (nameList.size() > 2) {
				for (int i = 0; i < nameList.size()-1; i++) {
					msgBuilder.append(nameList.get(i).trim()).append(", ");
				}
				msgBuilder.append(" and ").append(nameList.get(nameList.size()-1).trim()).append(",");
			}
			
			if (nameList.size() == 1) {
				msgBuilder.append(nameList.get(0).trim()).append(", ");
			}	
			
			if (nameList.size() == 2)  { 
				msgBuilder.append(nameList.get(0).trim()).append(" and ").append(nameList.get(1).trim()).append(",");
			}
			
			msgBuilder.append("\n\n");
			msgBuilder.append("You have the following overdue items: ");

			for (String bookString : emailToBookMap.get(emailString)) {
				msgBuilder.append(bookString).append(", ");
			} 
			msgBuilder.setLength(msgBuilder.length()-2);
			msgBuilder.append("\n\n");
			msgBuilder.append("Please return them immediately.");
			
			sendEmail("Overdue Library Items", emailString, msgBuilder.toString());
		}
		
		JOptionPane.showMessageDialog(this, "Emails sent to " + emailToBookMap);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch (e.getActionCommand()) {
		
			case ADD_BORROWER_ACTION:
				addBorrower();
				break;
			case CHECK_OUT_ACTION:
				checkOutItems();
				break;
			case ADD_ITEM_ACTION:
				addItem();
				break;
			case REMOVE_ITEM_ACTION:
				int buttonPosition = ((PositionAwareButton)e.getSource()).getPosition();
				removeItem(buttonPosition);
				break;
			case PROCESS_RETURN_ACTION:
				processReturn();
				break;
			case SEND_NOTIFICATION_ACTION:
				sendOverdueEmail();
				break;
				
		}

	}

}
