package ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

@SuppressWarnings("serial")
public abstract class UserTabPanel extends JPanel implements ActionListener {
	
	protected static Dimension DEFAULT_TEXT_SIZE = new Dimension(30, 10);
	
    private JPanel cards;
    protected JComboBox<String> comboBox;
    
    protected NumberFormat intFormat = NumberFormat.getIntegerInstance();
    protected NumberFormat currencyFormat = new DecimalFormat("0.00");
    protected NumberFormat yearFormat = NumberFormat.getIntegerInstance();
     
    public UserTabPanel() {
    	
    	intFormat.setGroupingUsed(false);
    	yearFormat.setMaximumIntegerDigits(4);
    	yearFormat.setMinimumIntegerDigits(4);
    	yearFormat.setGroupingUsed(false);
    	
    	this.setLayout(new BorderLayout());
    	
    	cards = new JPanel(new CardLayout());
    	
    	JPanel topPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    	
    	JLabel tip = new JLabel("Select an action:");
    	topPane.add(tip);
        comboBox = new JComboBox<String>();
        comboBox.setEditable(false);
        
        comboBox.addItemListener(new ItemListener() {
        	
        	@Override
            public void itemStateChanged(ItemEvent e) {
                CardLayout cl = (CardLayout) cards.getLayout();
                cl.show(cards, (String) e.getItem());
            }
        });
        
        topPane.add(comboBox);
        
        this.add(topPane, BorderLayout.PAGE_START);
        this.add(cards, BorderLayout.CENTER);
        
        initializeCards();
    }
    
    protected abstract void initializeCards();
    
	protected void createAndDisplayPopupTable(JFrame frame, String[][] data, String[] header) {
		
		if (data.length != 0) {
			Dimension d = this.getToolkit().getScreenSize();
			
			frame.getContentPane().removeAll();
			
			JTable table = new JTable(data, header) {
				
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			
			table.setPreferredScrollableViewportSize(new Dimension(600, Math.min(table.getPreferredSize().height, d.height)));
			table.setFillsViewportHeight(true);

			JScrollPane scroll = new JScrollPane(table);
			
			frame.add(scroll);
			frame.pack();

			// center the frame
			Rectangle r = frame.getBounds();
			frame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

			frame.validate();
			frame.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "No results found for: " + frame.getTitle());
		}
	}

    protected void addCard(String name, Component c) {
    	comboBox.addItem(name);
    	cards.add(name, c);
    }
    
	protected void sendEmail(String subject, String recipient, String msg, String footerImage) {
		
		try {
			SimpleEmail email = new SimpleEmail();
			
			msg = msg + "\n\nYour Librarian,\nMelon Melon\nicanhazbookz365@gmail.com\n";
			
			email.setHostName("smtp.gmail.com");
			email.setStartTLSRequired(true);
			email.setSSLOnConnect(true);
			email.setSmtpPort(465);
			email.setSubject(subject);
			email.setAuthentication("icanhazbookz365@gmail.com", "iliekmudkipz");
			email.setDebug(true);
			email.setFrom("icanhazbookz365@gmail.com", "Your Library");
			email.addTo(recipient);
			
			MimeMultipart multipart = new MimeMultipart();
			
			MimeBodyPart msgPart = new MimeBodyPart();
			msgPart.setText(msg);
			multipart.addBodyPart(msgPart);
			
			MimeBodyPart imagePart = new MimeBodyPart();
			imagePart.setContent("<img src=\"" + footerImage + "\" height=\"190\" width=\"190\">", "text/html");

			multipart.addBodyPart(imagePart);
			
			email.setContent(multipart);
			
			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
    
    protected void showDefaultError() {
    	JOptionPane.showMessageDialog(this, "Error. One or more required fields empty.", "Error",
				JOptionPane.ERROR_MESSAGE);
    }

}