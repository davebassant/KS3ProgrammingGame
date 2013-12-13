package com.proggame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	private static final String DEFAULT_GOOD_MSG 	= "PLAYER WINS!";
	private static final String DEFAULT_BAD_MSG 	= "PROGRAMMER WINS!";
	
	//private static final String KEYBOARD_NORM 	= "NORM";
	private static final String KEYBOARD_FLIP 	= "FLIP";
	private static final String KEYBOARD_ALT 	= "ALT";
	
	private final static String KEYS_STR 		= "KEYS";
	private final static String MSG_STR 		= "MSG";
	private final static String MOVE_STR 		= "MOVE";
	private final static String RAND_STR 		= "RAND";
	private final static String LOOP_STR 		= "LOOP";
	private final static String LOOP_END_STR 	= "END";
	
	private static Launcher instance;
	
	private final JFrame 		frame;
	private final CommandPanel 	cmdPanel;
	private final GamePanel 	gamePanel;
	
	private ThreadPoolExecutor tpe;
	private ThreadPoolExecutor tpe_good;
	
	private static String gameOverGoodMsg;
	private static String gameOverBadMsg;
	
	private static final String CHEAT_MSG = " ** Programmer Cheated!! **";
	
	private final Map<String, Runnable> commandMap;
	private final Map<String, Runnable> badCommandMap;
	
	boolean running = false;
	
	private static final Runnable SLEEP = new Runnable() {

		@Override
		public void run() {
			try {
				Thread.sleep(250);
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
		badCommandMap.put("CHEAT", new Runnable() {

			@Override
			public void run() {
				gamePanel.cheat();
			}
		});
		
		tpe_good = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES, 
				new ArrayBlockingQueue<Runnable>(MAX_COMMANDS_QUEUE_LENGTH), 
				new ThreadPoolExecutor.DiscardPolicy());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	private int handleLoop(String[] commands, List<String> lstCmds, int loopStartIndex) {
		String command = commands[loopStartIndex].trim();
		String[] loop_args = command.split(" ");
		if (loop_args.length != 2)
			throw new IllegalArgumentException("loop declaration invalid; command must be 'loop N;'");
		
		//String quantStr = loop_args[1].substring(0, loop_args[1].length() - 1);
		String quantStr = loop_args[1];
		int loopCount;
		try {
			loopCount = Integer.parseInt(quantStr);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("loop declaration invalid; NaN");
		}
		if (loopCount < 1)
			throw new IllegalArgumentException("loop declaration invalid; negative or zero loop count");
		
		int end_index = ++loopStartIndex;
		command = commands[end_index].trim().toUpperCase();
		while (command.compareTo(LOOP_END_STR) != 0) {
			if (command.startsWith(LOOP_STR)) 
				throw new IllegalArgumentException("embedded loops aren't supported :(");
			
			++end_index;
			if (end_index == commands.length)
				throw new IllegalArgumentException("loop not ended");
			
			command = commands[end_index].trim().toUpperCase();
		}
		
		int j;
		for (int i = 0; i < loopCount; ++i) {
			j = loopStartIndex;
			while (j < end_index) {
				lstCmds.add(commands[j++]);
			}
		}
		
		return end_index;
	}
	
	private String[] expandLoops(String[] commands) {
		List<String> lstCmds = new ArrayList<String>();
		
		String command;
		for (int index = 0; index < commands.length; ++index) {
			command = commands[index].trim().toUpperCase();
			if (command.startsWith(LOOP_STR)) {
				try {
					index = handleLoop(commands, lstCmds, index);
				} catch (Exception e) {
					throw new IllegalArgumentException(e.getMessage());
				}
			} else {
				lstCmds.add(command);
			}
		}
		
		String[] result = new String[lstCmds.size()];
		return lstCmds.toArray(result);
	}

	@Override
	public boolean commandsIssued(String commandStr) {
		tpe = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.MINUTES, 
				new ArrayBlockingQueue<Runnable>(MAX_COMMANDS_QUEUE_LENGTH), 
				new ThreadPoolExecutor.DiscardPolicy());
		
		gameOverGoodMsg = DEFAULT_GOOD_MSG;
		gameOverBadMsg 	= DEFAULT_BAD_MSG;
		
		String[] commands = commandStr.split(";");
		
		try {
			frame.addKeyListener(new KeyListenerNormal());
			frame.requestFocus();
			
			final String[] commandsNoLoops = expandLoops(commands);
			
			Runnable r = new Runnable() {
	
				@Override
				public void run() {
					runCommands(commandsNoLoops);
				}
			};
			tpe.execute(r);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private Runnable handleMove(final String command) {
		String[] commandParts = command.split(" ");
		if (commandParts.length != 3) {
			throw new IllegalArgumentException("Invalid Move; must be 'move N M;'");
		}
		
		try {
			final int x = Integer.parseInt(commandParts[1]);
			final int y = Integer.parseInt(commandParts[2]);
			
			return new Runnable() {

				@Override
				public void run() {
					gamePanel.moveBad(x, y);
				}
			};
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Invalid Move; NaN");
		}
	}
	
	private Runnable handleRand() {
		return new Runnable() {

			@Override
			public void run() {
				gamePanel.moveBadRand();
			}
		};
	}
	
	private Runnable handleMsg(String command) {
		final String[] cmdParts = command.split(" ");
		
		if (cmdParts.length < 2) {
			return new Runnable() {

				@Override
				public void run() {
					//do nothing
				}
			};
		}
			
		return new Runnable() {

			@Override
			public void run() {
				gameOverBadMsg = cmdParts[1];
			}
		};
	}
	
	private Runnable handleKeys(String command) {
		final String[] cmdParts = command.split(" ");
		
		if (cmdParts.length < 2)
			return new Runnable() {

			@Override
			public void run() {
				//do nothing
			}
		};
		
		final KeyListener kl;
		String keys_id = cmdParts[1];
		//if (keys_id.equals(KEYBOARD_NORM)) {
			//kl = new KeyListenerNormal();
		//} else 
		if (keys_id.equals(KEYBOARD_FLIP)) {
			kl = new KeyListenerFlip();
		} else if (keys_id.equals(KEYBOARD_ALT)) {
			kl = new KeyListenerAlt();
		} else {
			return new Runnable() {

				@Override
				public void run() {
					//do nothing
				}
			};
		}
			
		return new Runnable() {

			@Override
			public void run() {
				resetKeyListeners(kl);
			}
		};
	}
	
	private void clearKeyListeners() {
		for (KeyListener kl : frame.getKeyListeners()) {
			frame.removeKeyListener(kl);
		}
	}
	
	private void resetKeyListeners(KeyListener new_kl) {
		if (new_kl == null) 
			return;
		
		clearKeyListeners();
		
		frame.addKeyListener(new_kl);
	}
	
	private void runCommands(final String[] commands) {
		running = true;
		
		int key_change_count = 0;
		
		Runnable r;
		for (String command : commands) {
			command = command.trim().toUpperCase();
			if (command.startsWith(MOVE_STR)) {
				r = handleMove(command);
			} else if (command.startsWith(RAND_STR)) {
				r = handleRand();
			} else if (command.startsWith(MSG_STR)) {
				r = handleMsg(command);
			} else if (command.startsWith(KEYS_STR)) {
				++key_change_count;
				if (key_change_count <= 1) {
					r = handleKeys(command);
				} else {
					r = null;
				}
			} else {
				r = badCommandMap.get(command);
			}
			
			if (r != null) {
				tpe.execute(SLEEP);
				tpe.execute(r);
			}
		}
		
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
		
		clearKeyListeners();
		//resetKeyListeners(null);
		
		tpe_good.getQueue().clear();
		
		String msg = (event.equals(GameOverEvent.GOOD)) ? gameOverGoodMsg : gameOverBadMsg;
		if (event.equals(GameOverEvent.CHEAT))
			msg += CHEAT_MSG;
		
		JOptionPane.showMessageDialog(frame, msg);
		
		tpe.getQueue().clear();
		tpe.execute(new Runnable() {

			@Override
			public void run() {
				cmdPanel.initialise();
				gamePanel.initialise();
				resetKeyListeners(null);
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
	    		//tpe.execute(SMALL_SLEEP);
	    		tpe_good.execute(commandMap.get("STEP"));
	        }
	    	else if (keyCode == KeyEvent.VK_LEFT) {
	    		//tpe.execute(SMALL_SLEEP);
	    		tpe_good.execute(commandMap.get("BACK"));
	        }
	    	else if (keyCode == KeyEvent.VK_DOWN) {
	    		//tpe.execute(SMALL_SLEEP);
	    		tpe_good.execute(commandMap.get("DROP"));
	    	}
	    	else if (keyCode == KeyEvent.VK_UP) {
	    		//tpe.execute(SMALL_SLEEP);
	    		tpe_good.execute(commandMap.get("CLIMB"));
	    	}
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {
	        //ignore
	    }
	}
	
	public class KeyListenerFlip implements KeyListener {

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
