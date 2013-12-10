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
	private Color Default_Color = Color.WHITE;
	
	private int x_position = 0;
	private int y_position = 0;
	
	private final GridButton[][] gridButtons;

	public GamePanel() {
		GridLayout gl = new GridLayout();
		gl.setHgap(5);
		gl.setVgap(5);
		gl.setRows(ROWS);
		gl.setColumns(COLS);
		
		this.setLayout(gl);
		
		gridButtons = new GridButton[ROWS][COLS];
		
		GridButton gbRef;
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				gbRef = new GridButton();
				gridButtons[i][j] = gbRef;
				this.add(gbRef);
			}
		}
		
		setPositionColor(Player_Color);
		gridButtons[ROWS-1][COLS-1].setBackground(End_Color);
	}
	
	public void moveRight() {
		if (x_position >= (COLS - 1)) {
			return;
		}
		
		setPositionColor(Default_Color);
		++x_position;
		setPositionColor(Player_Color);
	}
	
	public void moveDown() {
		if (y_position >= (ROWS - 1)) {
			return;
		}
		
		setPositionColor(Default_Color);
		++y_position;
		setPositionColor(Player_Color);
	}
	
	public void moveLeft() {
		if (x_position == 0) {
			return;
		}
		
		setPositionColor(Default_Color);
		--x_position;
		setPositionColor(Player_Color);
	}
	
	public void moveUp() {
		if (y_position == 0) {
			return;
		}
		
		setPositionColor(Default_Color);
		--y_position;
		setPositionColor(Player_Color);
	}
	
	private void setPositionColor(Color color) {
		gridButtons[y_position][x_position].setBackground(color);
	}
	
	private class GridButton extends JButton {
		
		private final Dimension GB_DIM = new Dimension(50,30);
		
		private GridButton() {
			this.setPreferredSize(GB_DIM);
			this.setEnabled(false);
			this.setBackground(GamePanel.this.Default_Color);
		}
	}
}
