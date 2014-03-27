package ui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class UserTabPanel extends JPanel implements ActionListener {
	
	protected static Dimension DEFAULT_TEXT_SIZE = new Dimension(30, 10);
	
    private JPanel cards;
    private JComboBox<String> comboBox;
     
    public UserTabPanel() {
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

    protected void addCard(String name, Component c) {
    	comboBox.addItem(name);
    	cards.add(name, c);
    }

}