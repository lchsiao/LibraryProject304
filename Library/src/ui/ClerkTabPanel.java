package ui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

		JLabel passwordLabel = new JLabel("Password:");
		passwordField = new JPasswordField();
		addBorrowerPanelTop.add(passwordLabel);
		addBorrowerPanelTop.add(passwordField);

		JLabel addressLabel = new JLabel("Address:");
		addressField = new JTextField();
		addBorrowerPanelTop.add(addressLabel);
		addBorrowerPanelTop.add(addressField);

		JLabel phoneLabel = new JLabel("Phone:");
		phoneField = new JTextField();
		addBorrowerPanelTop.add(phoneLabel);
		addBorrowerPanelTop.add(phoneField);

		JLabel emailLabel = new JLabel("Email Address:");
		emailField = new JTextField();
		addBorrowerPanelTop.add(emailLabel);
		addBorrowerPanelTop.add(emailField);

		JLabel sinOrStNoLabel = new JLabel("Sin or Student Number:");
		sinOrStNoField = new JTextField();
		addBorrowerPanelTop.add(sinOrStNoLabel);
		addBorrowerPanelTop.add(sinOrStNoField);

		JLabel typeLabel = new JLabel("Type:");
		String[] types = {"Student", "Faculty", "Staff", "General Public"};
		typeField = new JComboBox<String>(types);
		addBorrowerPanelTop.add(typeLabel);
		addBorrowerPanelTop.add(typeField);

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
		bidField = new JTextField();
		checkOutItemsPanelTop.add(bidLabel);
		checkOutItemsPanelTop.add(bidField);
		checkOutItemsPanelTop.add(Box.createHorizontalGlue());

		JButton itemsButton = new JButton("Add Item");
		itemsButton.addActionListener(this);
		itemsButton.setActionCommand(ADD_ITEM_ACTION);
		itemsField = new LinkedList<JTextField>();
		checkOutItemsPanelTop.add(itemsButton);
		JTextField firstItem = new JTextField();
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
	

	private void createOverdueItemsPanel() {
		
		JPanel checkOverdueItemsPanel = new JPanel();

		//TODO
		
		this.addCard("Check Overdue Items", checkOverdueItemsPanel);
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

		processReturnPanel.add(processReturnPanelTop, BorderLayout.PAGE_START);

		JButton processReturnSubmit = new JButton("Process Return");
		processReturnPanel.add(processReturnSubmit, BorderLayout.CENTER);
		processReturnSubmit.addActionListener(this);
		processReturnSubmit.setActionCommand(PROCESS_RETURN_ACTION);
		
		this.addCard("Process Return", processReturnPanel);
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
	
	
	private void removeItem(PositionAwareButton source) {
		
		int buttonPosition = source.getPosition();
		int rowStartPosition = buttonPosition - 2;
		int listPosition = buttonPosition/3 - 2;
		
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

		if (returnID.isEmpty()) {

			showDefaultError();

			return false;
		}

		String result = LibrarySQLUtil.processReturn(returnID);
		if (result.contains(LibrarySQLUtil.SUCCESS_STRING)) {
			JOptionPane.showMessageDialog(this, result);
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return true;
		
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
				removeItem((PositionAwareButton)e.getSource());
				break;
			case PROCESS_RETURN_ACTION:
				processReturn();
				
		}

	}

}
