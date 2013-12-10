package com.proggame.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	
	private static final int ROWS = 10;
	private static final int COLS = 20;
	
	private Color Player_Color 	= Color.GREEN;
	private Color End_Color 	= Color.RED;
	
	private int x_position = 0;
	private int y_position = 0;

	public GamePanel() {
		GridLayout gl = new GridLayout();
		gl.setHgap(5);
		gl.setVgap(5);
		gl.setRows(ROWS);
		gl.setColumns(COLS);
		
		this.setLayout(gl);
		
		GridButton[][] gridButtons = new GridButton[ROWS][COLS];
		
		GridButton gbRef;
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				gbRef = new GridButton();
				gridButtons[i][j] = gbRef;
				this.add(gbRef);
			}
		}
		
		
	}
	
	private static class GridButton extends JButton {
		private static final Dimension GB_DIM = new Dimension(50,30);
		
		private GridButton() {
			this.setPreferredSize(GB_DIM);
			this.setEnabled(false);
		}
	}
}
