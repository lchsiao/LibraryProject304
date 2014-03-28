package ui;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BorrowerTabPanel extends UserTabPanel {

	@Override
	protected void initializeCards() {

		createSearchBooksPanel();
		createCheckAccountPanel();
		createRequestHoldPanel();
		createPayFinesPanel();
		
	}

	
	private void createSearchBooksPanel() {
		
		JPanel createSearchBooksPanel = new JPanel();

		//TODO
		
		this.addCard("Search Books", createSearchBooksPanel);
	}


	private void createCheckAccountPanel() {
		
		JPanel createCheckAccountPanel = new JPanel();

		//TODO
		
		this.addCard("Check Account", createCheckAccountPanel);
	}


	private void createRequestHoldPanel() {
		
		JPanel createRequestHoldPanel = new JPanel();

		//TODO
		
		this.addCard("Request Hold", createRequestHoldPanel);
	}


	private void createPayFinesPanel() {
		
		JPanel createPayFinesPanel = new JPanel();

		//TODO
		
		this.addCard("Pay Fines", createPayFinesPanel);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
