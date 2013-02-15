package connectfour.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import connectfour.common.AsyncCallback;
import connectfour.controller.GameController;
import connectfour.model.GameBoard;

public class GameView implements AsyncCallback<Integer> {

	private DisplayedBoard displayedBoard;
	private JButton[] buttonArray;
	private JButton resetButton;
	private JButton undoButton;
	private JButton compMoveButton;
	private JButton cancelButton;
	private JCheckBox deterministic;
	private JRadioButton timeLimitedRadioButton;
	private JRadioButton depthLimitedRadioButton;
	private JTextField timeField;
	private JTextField depthField;
	
	private GameController gc;
	private ExecutorService executor;
	private Runnable compMoveRunnable = new Runnable() {
		@Override
		public void run() {
			gc.makeComputerMove(gv);
		}
	};
	
	private int INIT_DEPTH_LIMIT;
	private long INIT_TIME_LIMIT;
	private GameView gv;
	
	public GameView(final GameController gc, final GameBoard board, boolean INIT_DET_AI, boolean INIT_TIME_LIMITED, long INIT_TIME_LIMIT, int INIT_DEPTH_LIMIT) {
		
		this.gv = this;
		this.gc = gc;
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
				resetButton.setEnabled(false);
				undoButton.setEnabled(false);
				setMoveButtonsEnabled(true);// in case the game had ended
				displayedBoard.repaint();
			}
		});
		resetButton.setEnabled(false);//can't reset when no moves made
		optionButtonsTopRow.add(resetButton);
		
		undoButton = new JButton("UNDO");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gc.undo();
				if (board.getCountersPlaced() == 0) {
					undoButton.setEnabled(false);
					resetButton.setEnabled(false);
				}
				setMoveButtonsEnabled(true);// in case the game had ended
				displayedBoard.repaint();// TODO change to push based method update UI called in model
			}
		});
		undoButton.setEnabled(false);//can't undo when no moves made
		optionButtonsTopRow.add(undoButton);
		
		compMoveButton = new JButton("COMPUTER MOVE");
		compMoveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setNonMoveCancelButtonsEnabled(false);
				setMoveButtonsEnabled(false);
				cancelButton.setEnabled(true);
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
		optionButtonsTopRow.add(deterministic);
		
		JPanel optionButtonsBottomRow = new JPanel();
		int textFieldLength = 6;
		timeField = new JTextField(textFieldLength);
		timeField.setText(String.valueOf(INIT_TIME_LIMIT));
		timeField.setHorizontalAlignment(JTextField.RIGHT);
		timeField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				timeFieldAction();
			}
		});
		depthField = new JTextField(textFieldLength);
		depthField.setText(String.valueOf(INIT_DEPTH_LIMIT));
		depthField.setHorizontalAlignment(JTextField.RIGHT);
		depthField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				depthFieldAction();
			}
		});
		JLabel ms = new JLabel("ms");
		//JLabel ltNine = new JLabel("rec < 9 for non-det");// TODO - change to pop-up
		// TODO - make moves async - i.e. cancellable/leaves an active ui
		
		timeLimitedRadioButton = new JRadioButton("TIME LIMITED");
		timeLimitedRadioButton.setSelected(INIT_TIME_LIMITED);
		timeLimitedRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (timeLimitedRadioButton.isSelected()) {
					timeFieldAction();
					gc.setTimeLimitedAi(true);
				}
			}
		});
		depthLimitedRadioButton = new JRadioButton("DEPTH LIMITED");
		depthLimitedRadioButton.setSelected(!INIT_TIME_LIMITED);
		depthLimitedRadioButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (depthLimitedRadioButton.isSelected()) {
					depthFieldAction();
					gc.setTimeLimitedAi(false);
				}
			}
		});
		
		ButtonGroup aiSearchLimits = new ButtonGroup();
		aiSearchLimits.add(timeLimitedRadioButton);
		aiSearchLimits.add(depthLimitedRadioButton);
		
		JPanel timeLimitedPanel = new JPanel();
		timeLimitedPanel.add(timeLimitedRadioButton);
		timeLimitedPanel.add(timeField);
		timeLimitedPanel.add(ms);
		JPanel depthLimitedPanel = new JPanel();
		depthLimitedPanel.add(depthLimitedRadioButton);
		depthLimitedPanel.add(depthField);
		//depthLimitedPanel.add(ltNine);
		
		optionButtonsBottomRow.add(timeLimitedPanel);
		optionButtonsBottomRow.add(depthLimitedPanel);
		
		JPanel optionButtons = new JPanel();
		optionButtons.setLayout(new BorderLayout());
		optionButtons.add(optionButtonsTopRow, BorderLayout.NORTH);
		optionButtons.add(optionButtonsBottomRow, BorderLayout.CENTER);
 		
		displayedBoard.setPreferredSize(new Dimension(598, 516));//600, 510
		displayedBoard.setBackground(new Color(0,0,102));//DARK BLUE

		displayedBoard.addComponentListener(new ComponentListener () {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				displayedBoard.repaint();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				
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
				setMoveButtonsEnabled(false);
				int result = gc.makeHumanMove(i);
				displayedBoard.repaint();
				if (result == -2) { // invalid move
					setMoveButtonsEnabled(true);
				} else if (result == -1) {
					setNonMoveCancelButtonsEnabled(false);
					cancelButton.setEnabled(true);
					executor.execute(compMoveRunnable);
				} else { // game over
					displayGameOverMessage(result);
				}
			}

		});

		return button;
	}
	
	public void onSuccess(Integer result) {
		displayedBoard.repaint();
		setNonMoveCancelButtonsEnabled(true);
		cancelButton.setEnabled(false);
		if (result == -1) {// game not over
			setMoveButtonsEnabled(true);
		} else {
			setMoveButtonsEnabled(false);
			displayGameOverMessage(result);
		}
	}
	
	public void onFailure(Throwable caught) {
		System.out.println(caught);
	}
	
	private void timeFieldAction() {
		long timeLimit;
		try {
			timeLimit = Long.parseLong(timeField.getText());
			if (timeLimit <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e1) {
			timeLimit = INIT_TIME_LIMIT;
			timeField.setText(String.valueOf(timeLimit));
		}
		gc.setAiTimeLimit(timeLimit);
	}
	
	private void depthFieldAction() {
		int depthLimit;
		try {
			depthLimit = Integer.parseInt(depthField.getText());
			if (depthLimit <= 0) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException e1) {
			depthLimit = INIT_DEPTH_LIMIT;
			depthField.setText(String.valueOf(depthLimit));
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
		timeField.setEnabled(enabled);
		depthLimitedRadioButton.setEnabled(enabled);
		depthField.setEnabled(enabled);
	}
	
}
