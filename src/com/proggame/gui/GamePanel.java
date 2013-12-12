package com.proggame.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.proggame.utils.GameOverListener;
import com.proggame.utils.GameOverListener.GameOverEvent;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	
	private static final int ROWS = 10;
	private static final int COLS = 20;
	
	private Color Player_Color 		= Color.GREEN;
	private Color Bad_Player_Color 	= Color.BLACK;
	private Color End_Color 		= Color.RED;
	private Color Default_Color 	= Color.WHITE;
	
	private int x_position;
	private int y_position;
	
	private int bad_x_position;
	private int bad_y_position;
	
	private final GridButton[][] gridButtons;
	
	private final GameOverListener gameOverListener;
	
	private final Random randomGen = new Random();

	public GamePanel(GameOverListener goListener) {
		GridLayout gl = new GridLayout();
		gl.setHgap(5);
		gl.setVgap(5);
		gl.setRows(ROWS);
		gl.setColumns(COLS);
		
		this.setLayout(gl);
		
		gameOverListener = goListener;
		
		gridButtons = new GridButton[ROWS][COLS];
		
		GridButton gbRef;
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				gbRef = new GridButton();
				gridButtons[i][j] = gbRef;
				this.add(gbRef);
			}
		}
		
		initialise();
	}
	
	public void initialise() {
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				gridButtons[i][j].setBackground(Default_Color);
			}
		}
		
		x_position = 0;
		y_position = 0;
		bad_x_position = COLS - 1;
		bad_y_position = 0;
		
		setPositionColor(Player_Color, false);
		gridButtons[ROWS-1][COLS-1].setBackground(End_Color);
		setBadPositionColor(Bad_Player_Color, false);
	}
	
	public void moveRight() {
		if (x_position >= (COLS - 1)) {
			return;
		}
		
		setPositionColor(Default_Color, false);
		++x_position;
		setPositionColor(Player_Color, true);
	}
	
	public void moveDown() {
		if (y_position >= (ROWS - 1)) {
			return;
		}
		
		setPositionColor(Default_Color, false);
		++y_position;
		setPositionColor(Player_Color, true);
	}
	
	public void moveLeft() {
		if (x_position == 0) {
			return;
		}
		
		setPositionColor(Default_Color, false);
		--x_position;
		setPositionColor(Player_Color, true);
	}
	
	public void moveUp() {
		if (y_position == 0) {
			return;
		}
		
		setPositionColor(Default_Color, false);
		--y_position;
		setPositionColor(Player_Color, true);
	}
	
	public void moveBadRight() {
		setBadPositionColor(Default_Color, false);
		
		if (bad_x_position == (COLS - 1))
			bad_x_position = 0;
		else 
			++bad_x_position;
		
		if (bad_x_position == 0 && bad_y_position == 0)
			bad_x_position = 1;
		
		if (bad_x_position == (COLS - 1) && bad_y_position == (ROWS - 1))
			bad_x_position = 0;
		
		setBadPositionColor(Bad_Player_Color, true);
	}
	
	public void moveBadLeft() {
		setBadPositionColor(Default_Color, false);
		
		if (bad_x_position == 0)
			bad_x_position = (COLS - 1);
		else 
			--bad_x_position;
		
		if (bad_x_position == 0 && bad_y_position == 0)
			bad_x_position = (COLS - 1);
		
		if (bad_x_position == (COLS - 1) && bad_y_position == (ROWS - 1))
			--bad_x_position;
		
		setBadPositionColor(Bad_Player_Color, true);
	}
	
	public void moveBadDown() {
		setBadPositionColor(Default_Color, false);
		
		if (bad_y_position == (ROWS - 1))
			bad_y_position = 0;
		else 
			++bad_y_position;
		
		if (bad_x_position == 0 && bad_y_position == 0)
			bad_y_position = 1;
		
		if (bad_x_position == (COLS - 1) && bad_y_position == (ROWS - 1))
			bad_y_position = 0;
		
		setBadPositionColor(Bad_Player_Color, true);
	}
	
	public void moveBadUp() {
		setBadPositionColor(Default_Color, false);
		
		if (bad_y_position == 0)
			bad_y_position = (ROWS - 1);
		else 
			--bad_y_position;
		
		if (bad_x_position == 0 && bad_y_position == 0)
			bad_y_position = (ROWS - 1);
		
		if (bad_x_position == (COLS - 1) && bad_y_position == (ROWS - 1))
			--bad_y_position;
		
		setBadPositionColor(Bad_Player_Color, true);
	}
	
	public void moveBad(int x, int y) {
		if (x == 0 && y == 0) 
			return;
		
		if (x < 0 || y < 0) 
			return;
		
		if (x == (COLS - 1) && y == (ROWS - 1))
			return;
		
		if (x > (COLS - 1) || y > (ROWS - 1))
			return;
		
		setBadPositionColor(Default_Color, false);
		bad_x_position = x;
		bad_y_position = y;
		setBadPositionColor(Bad_Player_Color, true);
	}
	
	public void moveBadRand() {
		int x = randomGen.nextInt(COLS);
		int y = randomGen.nextInt(ROWS);
		
		moveBad(x, y);
	}
	
	private void setPositionColor(Color color, boolean check) {
		gridButtons[y_position][x_position].setBackground(color);
		if (check)
			checkPositions();
	}
	
	private void setBadPositionColor(Color color, boolean check) {
		gridButtons[bad_y_position][bad_x_position].setBackground(color);
		if (check)
			checkPositions();
	}
	
	private void checkPositions() {
		if (y_position == ROWS - 1 && x_position == COLS - 1) {
			gameOverListener.gameOverEventOccured(GameOverEvent.GOOD);
		} else if (y_position == bad_y_position && x_position == bad_x_position) {
			gameOverListener.gameOverEventOccured(GameOverEvent.BAD);
		}
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
