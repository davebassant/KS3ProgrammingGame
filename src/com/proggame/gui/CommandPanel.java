package com.proggame.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.proggame.utils.CommandListener;

@SuppressWarnings("serial")
public class CommandPanel extends JPanel {
	
	private static final int TEXT_ROWS = 10;
	private static final int TEXT_COLS = 20;
	
	private final CommandListener mlistener;
	
	private final JTextArea txtArea;
	private final JButton btnCommand;

	public CommandPanel(CommandListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener is null");
		}
		mlistener = listener;
		
		this.setLayout(new BorderLayout());
		
		txtArea = new JTextArea(TEXT_ROWS, TEXT_COLS);
		btnCommand = new JButton("Run Commands");
		
		final JScrollPane scrPane = new JScrollPane(txtArea);
		scrPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		btnCommand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mlistener.commandsIssued(txtArea.getText());
				txtArea.setText("");
				txtArea.setEditable(false);
				btnCommand.setEnabled(false);
			}
		});
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(btnCommand);
		box.add(Box.createHorizontalGlue());
		
		this.add(scrPane, BorderLayout.CENTER);
		this.add(box, BorderLayout.SOUTH);
		
		initialise();
	}
	
	public void initialise() {
		txtArea.setText("");
		txtArea.setEditable(true);
		btnCommand.setEnabled(true);
	}
}
