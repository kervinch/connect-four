package connectfour.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import connectfour.controller.Controller;
import connectfour.model.Model;

public class GameView implements View {

	private DisplayedBoard displayedBoard;
	private JButton[] buttonArray;
	private JButton resetButton;
	private JButton undoButton;
	private JButton compMoveButton;
	private JButton cancelButton;
	private JCheckBox deterministic;
	private JRadioButton timeLimitedRadioButton;
	private JRadioButton depthLimitedRadioButton;
	private JTextField timeLimitField;
	private JTextField depthLimitField;
	
	private Controller gc;
	private ExecutorService executor;
	private Runnable compMoveRunnable = new Runnable() {
		@Override
		public void run() {
			gc.makeComputerMove();
		}
	};
	
	private int INIT_DEPTH_LIMIT;
	private long INIT_TIME_LIMIT;
	private Model board;
	
	public GameView(final Controller gc, Model board, boolean INIT_DET_AI, boolean INIT_TIME_LIMITED, long INIT_TIME_LIMIT, int INIT_DEPTH_LIMIT) {
		
		this.gc = gc;
		this.board = board;
		this.INIT_TIME_LIMIT = INIT_TIME_LIMIT;
		this.INIT_DEPTH_LIMIT = INIT_DEPTH_LIMIT;
		executor = Executors.newSingleThreadExecutor();
	
		displayedBoard = new DisplayedBoard(board);
		buttonArray = new JButton[7];
		
		// Top-level frame
		final JFrame frame = new JFrame("Connect Four");
		frame.setLocation(310, 130);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel numberButtons = new JPanel();
		numberButtons.setLayout(new GridLayout(1, 7, 4, 4));// 4,4?
		for (int i = 0; i < 7; i++) {
			JButton button = createButton(i);
			buttonArray[i] = button;
			numberButtons.add(button);
		}
		numberButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel optionButtonsTopRow = new JPanel();
		resetButton = new JButton("RESET");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gc.reset();
			}
		});
		resetButton.setEnabled(false);//can't reset when no moves made
		optionButtonsTopRow.add(resetButton);
		
		undoButton = new JButton("UNDO");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gc.undo();
			}
		});
		undoButton.setEnabled(false);//can't undo when no moves made
		optionButtonsTopRow.add(undoButton);
		
		compMoveButton = new JButton("COMPUTER MOVE");
		compMoveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				executor.execute(compMoveRunnable);
			}
		});
		optionButtonsTopRow.add(compMoveButton);
		
		ImageIcon cancelIcon = new ImageIcon(getClass().getResource("/img/no_mini.png"));
		cancelButton = new JButton(cancelIcon);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gc.cancelAiMove();
			}
		});
		cancelButton.setEnabled(false);
		optionButtonsTopRow.add(cancelButton);
		
		deterministic = new JCheckBox("Deterministic", INIT_DET_AI);
		deterministic.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				gc.setDeterministicAi(deterministic.isSelected());
			}
		});
		deterministic.setToolTipText("If unchecked the computer chooses randomly from among equally good moves");
		optionButtonsTopRow.add(deterministic);
		
		String limitFieldToolTip = "Hit enter to register a change";
		JPanel optionButtonsBottomRow = new JPanel();
		int textFieldLength = 6;
		timeLimitField = new JTextField(textFieldLength);
		timeLimitField.setText(String.valueOf(INIT_TIME_LIMIT));
		timeLimitField.setHorizontalAlignment(JTextField.RIGHT);
		timeLimitField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				timeLimitFieldAction();
			}
		});
		timeLimitField.setToolTipText(limitFieldToolTip);
		JLabel ms = new JLabel("ms");
		depthLimitField = new JTextField(textFieldLength);
		depthLimitField.setText(String.valueOf(INIT_DEPTH_LIMIT));
		depthLimitField.setHorizontalAlignment(JTextField.RIGHT);
		depthLimitField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				depthLimitFieldAction();
			}
		});
		depthLimitField.setToolTipText(limitFieldToolTip);
		
		timeLimitedRadioButton = new JRadioButton("TIME LIMITED");
		timeLimitedRadioButton.setSelected(INIT_TIME_LIMITED);
		timeLimitedRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.SELECTED == e.getStateChange()) {
					timeLimitFieldAction();
					gc.setTimeLimitedAi(true);
				}
			}
		});
		timeLimitedRadioButton.setToolTipText("The computer chooses a move within the time specified");
		depthLimitedRadioButton = new JRadioButton("DEPTH LIMITED");
		depthLimitedRadioButton.setSelected(!INIT_TIME_LIMITED);
		depthLimitedRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (ItemEvent.SELECTED == e.getStateChange()) {
					depthLimitFieldAction();
					gc.setTimeLimitedAi(false);
				}
			}
		});
		depthLimitedRadioButton.setToolTipText("The computer looks a maximum of the specified moves ahead");
		
		ButtonGroup aiSearchLimits = new ButtonGroup();
		aiSearchLimits.add(timeLimitedRadioButton);
		aiSearchLimits.add(depthLimitedRadioButton);
		
		JPanel timeLimitedPanel = new JPanel();
		timeLimitedPanel.add(timeLimitedRadioButton);
		timeLimitedPanel.add(timeLimitField);
		timeLimitedPanel.add(ms);
		JPanel depthLimitedPanel = new JPanel();
		depthLimitedPanel.add(depthLimitedRadioButton);
		depthLimitedPanel.add(depthLimitField);
		
		optionButtonsBottomRow.add(timeLimitedPanel);
		optionButtonsBottomRow.add(depthLimitedPanel);
		
		JPanel optionButtons = new JPanel();
		optionButtons.setLayout(new BorderLayout());
		optionButtons.add(optionButtonsTopRow, BorderLayout.NORTH);
		optionButtons.add(optionButtonsBottomRow, BorderLayout.CENTER);
 		
		displayedBoard.setPreferredSize(new Dimension(598, 516));//600, 510
		displayedBoard.setBackground(new Color(0,0,102));//DARK BLUE

		displayedBoard.addComponentListener(new ComponentAdapter () {
			@Override
			public void componentResized(ComponentEvent arg0) {
				displayedBoard.repaint();
			}
		});
		
		// create panel to hold all of above
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		// add objects to panel
		mainPanel.add(numberButtons, BorderLayout.NORTH);
		mainPanel.add(displayedBoard, BorderLayout.CENTER);
		mainPanel.add(optionButtons, BorderLayout.SOUTH);
		
		frame.add(mainPanel, BorderLayout.CENTER);

		// Put the frame on the screen
		frame.pack();
		frame.setVisible(true);

	}
	
	private JButton createButton(final int i) {
		JButton button = new JButton(String.valueOf(i+1));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gc.makeHumanMove(i);
				if (gc.getLastMoveResult() == -1) { // human chosen move was not invalid/game ending
					executor.execute(compMoveRunnable);
				}
			}

		});
		return button;
	}
		
	private void timeLimitFieldAction() {
		long timeLimit;
		try {
			timeLimit = Long.parseLong(timeLimitField.getText());
			if (timeLimit <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e1) {
			timeLimit = INIT_TIME_LIMIT;
			timeLimitField.setText(String.valueOf(timeLimit));
		}
		gc.setAiTimeLimit(timeLimit);
	}
	
	private void depthLimitFieldAction() {
		int depthLimit;
		try {
			depthLimit = Integer.parseInt(depthLimitField.getText());
			if (depthLimit <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e1) {
			depthLimit = INIT_DEPTH_LIMIT;
			depthLimitField.setText(String.valueOf(depthLimit));
		}
		gc.setAiDepthLimit(depthLimit);
	}

	private void displayGameOverMessage(int answer) {
		String title = "Game Over";
		if (answer == 0) {
			JOptionPane.showMessageDialog (null, "DRAW", title, JOptionPane.INFORMATION_MESSAGE);
		}
		else if (answer == 1) {
			JOptionPane.showMessageDialog (null, "RED WINS!", title, JOptionPane.INFORMATION_MESSAGE);
		}
		else if (answer == 2) {
			JOptionPane.showMessageDialog (null, "YELLOW WINS!", title, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void setMoveButtonsEnabled(boolean enabled) {
		for (JButton button : buttonArray) {
			button.setEnabled(enabled);
		}
		compMoveButton.setEnabled(enabled);
	}
	
	private void setNonMoveCancelButtonsEnabled(boolean enabled) {
		resetButton.setEnabled(enabled);
		undoButton.setEnabled(enabled);
		deterministic.setEnabled(enabled);
		timeLimitedRadioButton.setEnabled(enabled);
		timeLimitField.setEnabled(enabled);
		depthLimitedRadioButton.setEnabled(enabled);
		depthLimitField.setEnabled(enabled);
	}

	private void executeSyncOnEdt(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Runnable counterPlacedRunnable = new Runnable() {
		@Override
		public void run() {
			displayedBoard.repaint();
		}
	};
	
	@Override
	public void onCounterPlaced() {
		executeSyncOnEdt(counterPlacedRunnable);
	}
	
	private Runnable resetRunnable = new Runnable() {
		public void run() {
			resetButton.setEnabled(false);
			undoButton.setEnabled(false);
			setMoveButtonsEnabled(true);// in case the game had ended
			displayedBoard.repaint();
		}
	};
	
	@Override
	public void onReset() {
		executeSyncOnEdt(resetRunnable);
	}

	private Runnable undoRunnable = new Runnable() {
		public void run() {
			if (board.getNumCountersPlaced() == 0) {
				undoButton.setEnabled(false);
				resetButton.setEnabled(false);
			}
			setMoveButtonsEnabled(true);// in case the game had ended
			displayedBoard.repaint();
		}
	};
	
	@Override
	public void onUndo() {
		executeSyncOnEdt(undoRunnable);
	}
	
	private Runnable computerStartMoveRunnable = new Runnable() {
		public void run() {
			setNonMoveCancelButtonsEnabled(false);
			setMoveButtonsEnabled(false);
			cancelButton.setEnabled(true);
		}
	};
	
	@Override
	public void onComputerStartMove() {
		executeSyncOnEdt(computerStartMoveRunnable);
	}
	
	private Runnable computerEndMoveRunnable = new Runnable() {
		public void run() {
			int result = gc.getLastMoveResult();
			setNonMoveCancelButtonsEnabled(true);
			cancelButton.setEnabled(false);
			if (result == -1) {// game not over
				setMoveButtonsEnabled(true);
			} else {
				displayGameOverMessage(result);
			}
		}
	};
	
	@Override
	public void onComputerEndMove() {
		executeSyncOnEdt(computerEndMoveRunnable);
	}
	
	private Runnable humanStartMoveRunnable = new Runnable() {
		public void run() {
			setMoveButtonsEnabled(false);
		}
	};
	
	@Override
	public void onHumanStartMove() {
		executeSyncOnEdt(humanStartMoveRunnable);
	}
	
	private Runnable humanEndMoveRunnable = new Runnable() {
		public void run() {
			int result = gc.getLastMoveResult();
			if (result == -2 || result == -1) { // invalid move or game not over
				setMoveButtonsEnabled(true);
			} else { // game over
				displayGameOverMessage(result);
			}
		}
	};
	
	@Override
	public void onHumanEndMove() {
		executeSyncOnEdt(humanEndMoveRunnable);
	}
	
}
