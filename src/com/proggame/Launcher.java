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
import javax.swing.JOptionPane;

import com.proggame.gui.CommandPanel;
import com.proggame.gui.GamePanel;
import com.proggame.utils.CommandListener;
import com.proggame.utils.GameOverListener;

public class Launcher implements CommandListener, GameOverListener {
	
	private final static int MAX_COMMANDS_QUEUE_LENGTH = 20;
	
	private static Launcher instance;
	
	private final JFrame 		frame;
	private final CommandPanel 	cmdPanel;
	private final GamePanel 	gamePanel;
	
	private ThreadPoolExecutor tpe;
	private ThreadPoolExecutor tpe_good;
	
	private static String gameOverGoodMsg 	= "Player wins!";
	private static String gameOverBadMsg 	= "You lose sucka!";
	
	private final Map<String, Runnable> commandMap;
	private final Map<String, Runnable> badCommandMap;
	
	boolean running = false;
	
	private static final Runnable SLEEP = new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(500);
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
		frame = new JFrame("Programming Demo Game");
		
		cmdPanel 	= new CommandPanel(this);
		gamePanel 	= new GamePanel(this);
		
		Box box = Box.createHorizontalBox();
		box.add(cmdPanel);
		box.add(Box.createHorizontalStrut(5));
		box.add(gamePanel);
		
		frame.getContentPane().add(box);
		
//		tpe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES, 
//				new ArrayBlockingQueue<Runnable>(MAX_COMMANDS_QUEUE_LENGTH));
		
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
		
		badCommandMap = new HashMap<String, Runnable>();
		badCommandMap.put("DOWN", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveBadDown();
			}
		});
		badCommandMap.put("RIGHT", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveBadRight();
			}
		});
		badCommandMap.put("LEFT", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveBadLeft();
			}
		});
		badCommandMap.put("UP", new Runnable() {

			@Override
			public void run() {
				gamePanel.moveBadUp();
			}
		});
		
		tpe_good = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES, 
				new ArrayBlockingQueue<Runnable>(MAX_COMMANDS_QUEUE_LENGTH), 
				new ThreadPoolExecutor.DiscardPolicy());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void commandsIssued(String commandStr) {
		tpe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES, 
				new ArrayBlockingQueue<Runnable>(MAX_COMMANDS_QUEUE_LENGTH), 
				new ThreadPoolExecutor.DiscardPolicy());
		
		final String[] commands = commandStr.split(";");
		
		frame.addKeyListener(new KeyListenerNormal());
		frame.requestFocus();
		
		//runCommands(commands);
		Runnable r = new Runnable() {

			@Override
			public void run() {
				runCommands(commands);
			}
		};
		tpe.execute(r);
	}
	
	private void runCommands(final String[] commands) {
		running = true;
		
		Runnable r;
		for (String command : commands) {
			command = command.trim().toUpperCase();
			r = badCommandMap.get(command);
			if (r != null) {
				tpe.execute(SLEEP);
				tpe.execute(r);
			}
		}
		
		//runCommands(commands);
		Runnable rr = new Runnable() {

			@Override
			public void run() {
				runCommands(commands);
			}
		};
		tpe.execute(rr);
	}
	
	@Override
	public void gameOverEventOccured(GameOverEvent event) {
		if (!running)
			return;
		
		running = false;
		
		for (KeyListener kl : frame.getKeyListeners()) {
			frame.removeKeyListener(kl);
		}
		
		tpe_good.getQueue().clear();
		
		String msg = (event.equals(GameOverEvent.GOOD)) ? gameOverGoodMsg : gameOverBadMsg;
		
		JOptionPane.showMessageDialog(frame, msg);
		
		tpe.getQueue().clear();
		tpe.execute(new Runnable() {

			@Override
			public void run() {
				cmdPanel.initialise();
				gamePanel.initialise();
			}
		});
		tpe.shutdown();
	}
	
	public class KeyListenerNormal implements KeyListener {

		@Override
	    public void keyTyped(KeyEvent e) {
	    	//ignore
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {

	        int keyCode = e.getKeyCode();
	    	
	    	if (keyCode == KeyEvent.VK_RIGHT) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("STEP"));
	        }
	    	else if (keyCode == KeyEvent.VK_LEFT) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("BACK"));
	        }
	    	else if (keyCode == KeyEvent.VK_DOWN) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("DROP"));
	    	}
	    	else if (keyCode == KeyEvent.VK_UP) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("CLIMB"));
	    	}
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	        //ignore
	    }
	}
	
	public class KeyListenerAlt implements KeyListener {

		@Override
	    public void keyTyped(KeyEvent e) {
	    	//ignore
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {

	        int keyCode = e.getKeyCode();
	    	
	    	if (keyCode == KeyEvent.VK_RIGHT) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("BACK"));
	        }
	    	else if (keyCode == KeyEvent.VK_LEFT) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("STEP"));
	        }
	    	else if (keyCode == KeyEvent.VK_DOWN) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("CLIMB"));
	    	}
	    	else if (keyCode == KeyEvent.VK_UP) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("DROP"));
	    	}
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	        //ignore
	    }
	}
	
	public class KeyListenerWeird implements KeyListener {

		@Override
	    public void keyTyped(KeyEvent e) {
	    	//ignore
	    }

	    @Override
	    public void keyPressed(KeyEvent e) {

	        int keyCode = e.getKeyCode();
	    	
	    	if (keyCode == KeyEvent.VK_RIGHT) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("DROP"));
	        }
	    	else if (keyCode == KeyEvent.VK_LEFT) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("STEP"));
	        }
	    	else if (keyCode == KeyEvent.VK_DOWN) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("BACK"));
	    	}
	    	else if (keyCode == KeyEvent.VK_UP) {
	    		//tpe.execute(SLEEP);
	    		tpe_good.execute(commandMap.get("CLIMB"));
	    	}
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	        //ignore
	    }
	}
}
