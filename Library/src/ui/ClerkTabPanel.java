package ui;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import sql.LibrarySQLUtil;


@SuppressWarnings("serial")
public class ClerkTabPanel extends UserTabPanel {

	// addBorrower fields
	private static String ADD_BORROWER_ACTION = "ADDBORROWER";
	
	private JTextField nameField;
	private JTextField passwordField;
	private JTextField addressField;
	private JTextField phoneField;
	private JTextField emailField;
	private JTextField sinOrStNoField;
	private JComboBox<String> typeField;
	
	
	@Override
	protected void initializeCards() {
		
		// add borrower function
		createAddBorrowerPanel();

		// check out items function
		JPanel checkOutItemsPanel = new JPanel();

		this.addCard("Check-out Items", checkOutItemsPanel);

		// process return function
		JPanel processReturn = new JPanel();

		this.addCard("Process Return", processReturn);


		// check overdue items function
		JPanel checkOverdueItemsPanel = new JPanel();

		this.addCard("Check Overdue Items", checkOverdueItemsPanel);
		
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
		passwordField = new JTextField();
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
			
			JOptionPane.showMessageDialog(this, "Error. One or more fields empty. All fields must have values.", "Error",
					JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		String result = LibrarySQLUtil.addBorrower(name, password, address, phone, email, sinOrStdNo, type);
		if (LibrarySQLUtil.SUCCESS_STRING.equals(result)) {
			JOptionPane.showMessageDialog(this, "Success. New borrower added.");
		} else {
			JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ADD_BORROWER_ACTION.equals(e.getActionCommand())) {
			addBorrower();
		}
		
	}

}
