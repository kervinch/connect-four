package connectfour.game;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import connectfour.ai.Computer;
import connectfour.ui.DisplayedBoard;

public class Game {

	private final int SEARCH_DEPTH = 2; //6 = 137257 nodes (for 1st move)[=(7^0)+(7^1)+(7^2)+(7^3)+(7^4)+(7^5)+(7^6)]
									    //crashes with depth 7 (@ 217758 nodes) [before alpha-beta pruning]
									    //with 6 & 5 comp only game red wins (no randomness)
										//with 4 comp only game drawn (no randomness)
										//with 3 & 2 comp only game yellow wins (no randomness)
										//red always starts
										//should be >= 4 for reliable human vs comp play
	private final boolean INITIAL_DETERMINISTIC_AI = true;
	
	private final long TIME_LIMIT = 1500;// time limit for a computer move in milliseconds
	
	private static GameBoard board;
	private static Human human;
	private static Computer computer;

	private static DisplayedBoard displayedBoard;
	private static JButton[] buttonArray;
	private static JButton compMoveButton;
	private static JButton undoButton;
	private static JButton resetButton;

	private Game() {

		board = new GameBoard();
		human = new Human(board);
		computer = new Computer(board, SEARCH_DEPTH, INITIAL_DETERMINISTIC_AI);
		computer.setTimeLimit(TIME_LIMIT);
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
		
		JPanel optionButtons = new JPanel();
		resetButton = new JButton("RESET");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				board.resetGameBoard();
				resetButton.setEnabled(false);
				undoButton.setEnabled(false);
				setButtonsEnabled(true);
				displayedBoard.repaint();
			}
		});
		resetButton.setEnabled(false);//can't reset when no moves made
		optionButtons.add(resetButton);
		
		undoButton = new JButton("UNDO");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				board.undoMove();
				if (board.getCountersPlaced() == 0) {
					undoButton.setEnabled(false);
					resetButton.setEnabled(false);
				}
				setButtonsEnabled(true);
				displayedBoard.repaint();
			}
		});
		undoButton.setEnabled(false);//can't undo when no moves made
		optionButtons.add(undoButton);
		
		compMoveButton = new JButton("COMPUTER MOVE");
		compMoveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setButtonsEnabled(false);
				int lastCounter = board.getLastCounterPlaced();
				makeComputerMove(3 - lastCounter);
			}
		});
		optionButtons.add(compMoveButton);
		
		final JCheckBox deterministic = new JCheckBox("Deterministic", INITIAL_DETERMINISTIC_AI);
		deterministic.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				computer.setDeterministicAI(deterministic.isSelected());
			}
		});
		optionButtons.add(deterministic);
		
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

		/*
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		
//		int[][] b = new int[][] {{0,0,0,0,0,0,0},
//								 {0,0,0,0,0,0,0},
//								 {1,0,1,1,0,0,0},
//								 {2,1,2,2,0,0,0},
//								 {1,2,1,2,0,0,0},
//								 {1,2,2,2,1,0,0}};//hit '2'
//		
//		int[][] b = new int[][] {{0,0,0,0,0,0,0},
//				 				 {0,0,0,0,0,0,0},
//				 				 {0,0,0,0,0,0,0},
//				 				 {0,0,1,0,0,0,0},
//				 				 {0,2,1,0,0,0,0},
//				 				 {0,2,1,0,0,0,0}};//hit '2'
//		
//		int[][] b = new int[][] {{1,2,1,1,0,0,0},
//				 				 {1,1,2,2,0,0,0},
//				 				 {2,2,2,1,1,0,0},
//				 				 {1,2,1,2,2,0,0},
//				 				 {1,2,1,2,2,0,0},
//				 				 {1,1,2,2,1,1,2}};//hit '6'
//
//		int[][] b = new int[][] {{0,0,0,0,0,0,0},
//								 {0,0,0,0,0,0,0},
//								 {0,0,0,0,0,0,0},
//								 {2,0,0,0,0,0,0},
//								 {2,2,1,1,0,0,0},
//								 {2,1,1,1,2,0,0}};//hit comp move
//		board.setBoard(b);
		
	}

	private JButton createButton(final int i) {
		JButton button = new JButton(String.valueOf(i+1));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				setButtonsEnabled(false);
				int lastCounter = board.getLastCounterPlaced();
				int answer = human.move(i, 3 - lastCounter);
				displayedBoard.repaint();
				if (answer == -1) {// game not over
					makeComputerMove(lastCounter);
				} else if (answer == -2) {// did not place counter properly - column was full
					setButtonsEnabled(true);
				} else {// game over
					//buttons already grayed out
					displayGameOverMessage(answer);
				}
			
			}

		});

		return button;
	}

	private void makeComputerMove(int counter) {
		int answer = computer.move(computer.chooseMove(counter), counter);
		displayedBoard.repaint();
		undoButton.setEnabled(true);
		resetButton.setEnabled(true);
		if (answer == -1) {// game isn't over
			setButtonsEnabled(true);
		}
		else {
			setButtonsEnabled(false);
			displayGameOverMessage(answer);
		}
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

	private void setButtonsEnabled(boolean enabled) {
		for (JButton button : buttonArray) {
			button.setEnabled(enabled);
		}
		compMoveButton.setEnabled(enabled);
	}
	
	/*
	 * Rather than directly building the top level frame object in the main
	 * method, we use the invokeLater utility method to ask the Swing framework
	 * to invoke the method 'run' of the Runnable object we pass it, at some
	 * later time that is convenient for it. (The key technical difference is
	 * that this will cause the new object to be created by a different
	 * "thread".)
	 */

	public static void main(String[] args) {
		/*
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Game();
			}
		});
		*/
		/*
		new Thread(new Runnable() {
			public void run() {
				new Game();
			}
		}).start();
		*/
		new Game();
	}

}