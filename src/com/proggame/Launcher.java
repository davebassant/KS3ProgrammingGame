package com.proggame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.JFrame;

import com.proggame.gui.CommandPanel;
import com.proggame.gui.GamePanel;
import com.proggame.utils.CommandListener;
import com.proggame.utils.GameEventListener;

public class Launcher implements CommandListener, GameEventListener {
	
	private final static int MAX_COMMANDS_QUEUE_LENGTH = 20;
	
	private static Launcher instance;
	
	private final KeyListenerFrame frame;
	private final CommandPanel 	cmdPanel;
	private final GamePanel 	gamePanel;
	
	private ThreadPoolExecutor tpe;
	
	private final Map<String, Runnable> commandMap;
	
	private static final Runnable SLEEP = new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	};

	public static void main(String[] args) {
		if (instance == null) {
			new Launcher();
		}
	}
	
	public Launcher() {
		frame = new KeyListenerFrame();
		
		cmdPanel 	= new CommandPanel(this);
		gamePanel 	= new GamePanel();
		
		Box box = Box.createHorizontalBox();
		box.add(cmdPanel);
		box.add(Box.createHorizontalStrut(5));
		box.add(gamePanel);
		
		frame.getContentPane().add(box);
		
		tpe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES, 
				new ArrayBlockingQueue<Runnable>(MAX_COMMANDS_QUEUE_LENGTH));
		
		commandMap = new HashMap<String, Runnable>();
		commandMap.put("DROP", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveDown();
			}
		});
		commandMap.put("STEP", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveRight();
			}
		});
		commandMap.put("BACK", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveLeft();
			}
		});
		commandMap.put("CLIMB", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveUp();
			}
		});
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		//frame.requestFocus();
		
		//frame.addKeyListener(new Keyboard());
	}

	@Override
	public void eventOccured(GameEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commandsIssued(String commandStr) {
//		String[] commands = commandStr.split(";");
//		
//		Runnable r;
//		for (String command : commands) {
//			command = command.trim().toUpperCase();
//			r = commandMap.get(command);
//			if (r != null) {
//				tpe.execute(SLEEP);
//				tpe.execute(r);
//			}
//		}
		frame.requestFocus();
	}
	
	@SuppressWarnings("serial")
	public class KeyListenerFrame extends JFrame implements KeyListener {

	    public KeyListenerFrame() {
	        super();
	        
	        addKeyListener(this);
	    }

	    @Override
	    public void keyTyped(KeyEvent e) {
	    	//ignore
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {

	        int keyCode = e.getKeyCode();
	    	
	    	if (keyCode == KeyEvent.VK_RIGHT) {
	    		//tpe.execute(SLEEP);
				tpe.execute(commandMap.get("STEP"));
	        }
	    	else if (keyCode == KeyEvent.VK_LEFT) {
	    		//tpe.execute(SLEEP);
				tpe.execute(commandMap.get("BACK"));
	        }
	    	else if (keyCode == KeyEvent.VK_DOWN) {
	    		//tpe.execute(SLEEP);
				tpe.execute(commandMap.get("DROP"));
	    	}
	    	else if (keyCode == KeyEvent.VK_UP) {
	    		//tpe.execute(SLEEP);
				tpe.execute(commandMap.get("CLIMB"));
	    	}
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	        //ignore
	    }
	}
}
