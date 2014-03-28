package ui;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LibrarianTabPanel extends UserTabPanel {

	@Override
	protected void initializeCards() {
		
		createAddBookPanel();
		createGenerateBookReportPanel();
		createListMostPopularItemsPanel();
		
	}
	
	
	private void createAddBookPanel() {
		
		JPanel createAddBookPanel = new JPanel();

		//TODO
		
		this.addCard("Search Books", createAddBookPanel);
	}


	private void createGenerateBookReportPanel() {
		
		JPanel createGenerateBookReportPanel = new JPanel();

		//TODO
		
		this.addCard("Generate Book Report", createGenerateBookReportPanel);
	}


	private void createListMostPopularItemsPanel() {
		
		JPanel createListMostPopularItemsPanel = new JPanel();

		//TODO
		
		this.addCard("List Most Popular Items", createListMostPopularItemsPanel);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
