package ui;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class PositionAwareButton extends JButton {
	
		private int position;
	
		public PositionAwareButton(String text) {
			super(text);
		}
		
		public int getPosition() {
			return position;
		}
		
		public void setPosition(int pos) {
			position = pos;
		}
}
