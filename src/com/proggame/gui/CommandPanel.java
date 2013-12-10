package com.proggame.gui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class CommandPanel extends JPanel {
	
	private static final int TEXT_ROWS = 10;
	private static final int TEXT_COLS = 20;

	public CommandPanel() {
		this.setLayout(new BorderLayout());
		
		JTextArea txtArea = new JTextArea(TEXT_ROWS, TEXT_COLS);
		JButton btnCommand = new JButton("Run Command");
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(btnCommand);
		box.add(Box.createHorizontalGlue());
		
		this.add(txtArea, BorderLayout.CENTER);
		this.add(box, BorderLayout.SOUTH);
	}
}
