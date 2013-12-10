package com.proggame;

import javax.swing.JFrame;

import com.proggame.gui.GamePanel;

public class Launcher {

	public static void main(String[] args) {
		JFrame dummyFrame = new JFrame();
		dummyFrame.setContentPane(new GamePanel());
		
		dummyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dummyFrame.pack();
		dummyFrame.setVisible(true);
	}
}
