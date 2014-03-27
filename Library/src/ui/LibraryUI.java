package ui;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class LibraryUI {
	
	public void initialize()
	{
		JFrame mainFrame = new JFrame("Library App");

		// anonymous inner class for closing the window
		mainFrame.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{ 
				System.exit(0); 
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel clerkPanel = new ClerkTabPanel();
		tabbedPane.addTab("Clerk", clerkPanel);
		
		JPanel borrowerPanel = new JPanel();
		tabbedPane.addTab("Borrower", borrowerPanel);
		
		JPanel librarianPanel = new JPanel();
		tabbedPane.addTab("Librarian", librarianPanel);
		
		mainFrame.add(tabbedPane);

		// size the window to obtain a best fit for the components
		mainFrame.pack();

		// center the frame
		Dimension d = mainFrame.getToolkit().getScreenSize();
		Rectangle r = mainFrame.getBounds();
		mainFrame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		mainFrame.setVisible(true);
	}
	
	
	public static void main(String args[])
	{
		new LibraryUI().initialize();
	}
}
