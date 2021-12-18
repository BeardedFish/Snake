// File Name:     SnakeGameWindow.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Sunday, May 17, 2020

package com.darianbenam.snake.windows;

import com.darianbenam.snake.adapter.MenuAdapter;
import com.darianbenam.snake.game.Direction;
import com.darianbenam.snake.game.SnakeGameContainer;
import com.darianbenam.snake.game.SnakeGameContainerListener;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import com.darianbenam.snake.score.HighScoreManager;

public class SnakeGameWindow extends JFrame implements SnakeGameContainerListener
{
    private static final String WINDOW_TITLE = "Snake";
    private final Color BG_COLOUR = new Color(0, 0, 0);
    private final int WINDOW_HEIGHT = 500, WINDOW_WIDTH = 500;

    private WindowKeyListener keyListener;
    private MainMenuListener menuListener;

    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu;
    private JMenuItem newGameMenuItem, highScoresMenuItem, closeMenuItem, aboutMenuItem;

    private SnakeGameContainer snakeGame;
    private HighScoreManager highScoreMngr;

    /**
     * Inner class for handling clicks on the JMenu of this window.
     */
    private class MainMenuListener extends MenuAdapter implements ActionListener
    {
        /**
         * Invoked every time a JMenuItem is clicked.
         *
         * @param e The ActionEvent that occurred when the JMenuItem was clicked.
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == newGameMenuItem)
            {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (dialogResult == JOptionPane.YES_OPTION)
                {
                    snakeGame.startNewGame();
                }
            }

            if (e.getSource() == highScoresMenuItem)
            {
                showHighScoreWindow();
            }

            if (e.getSource() == closeMenuItem)
            {
                System.exit(0);
            }

            if (e.getSource() == aboutMenuItem)
            {
                JOptionPane.showMessageDialog(null, "Snake\nBy: Darian Benam\nVersion: 1.1", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        /**
         * Invoked every time a JMenu is selected.
         *
         * @param e The MenuEvent that occurred when the JMenu was selected.
         */
        @Override
        public void menuSelected(MenuEvent e)
        {
            if (snakeGame.isGameStarted())
            {
                snakeGame.setPauseState(true);
            }
        }
    }

    /**
     * Inner class for handling keyboard input on this window.
     */
    private class WindowKeyListener extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    snakeGame.setSnakeDirection(Direction.Up);
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    snakeGame.setSnakeDirection(Direction.Down);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    snakeGame.setSnakeDirection(Direction.Left);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    snakeGame.setSnakeDirection(Direction.Right);
                    break;
                case KeyEvent.VK_P:
                    if (snakeGame.isGameStarted())
                    {
                        snakeGame.setPauseState(!snakeGame.isGamePaused());
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!snakeGame.isGameStarted() || snakeGame.isGameOver())
                    {
                        snakeGame.startGame();
                    }
                    break;
            }
        }
    }

    /**
     * Creates a window that contains the snake game so that the user can play.
     */
    public SnakeGameWindow()
    {
        super();

        setupWindow();
    }

    /**
     * Sets up the window so that the snake game can be played. By calling this method, the window will be shown automatically.
     */
    private void setupWindow()
    {
        this.setBackground(BG_COLOUR);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);

        initListeners();
        initHighScoreManager();
        setupMenuBar();
        setupSnakeGameContainer();
        updateTitleWithScore();

        this.addKeyListener(keyListener);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Initializes the inner class listeners that this window requires.
     */
    private void initListeners()
    {
        keyListener = new WindowKeyListener();
        menuListener = new MainMenuListener();
    }
 
    /**
     * Initializes the high score manager by loading up the high scores saved on the hard drive into the program. If the high scores fail
     * to load, then the high scores will just be initialized with default values (empty names and scores of zero).
     */
    private void initHighScoreManager()
    {
        highScoreMngr = new HighScoreManager();

        try
        {
            highScoreMngr.loadHighScores();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "An error occured while trying to load the high score file (" + highScoreMngr.HIGH_SCORE_FILE_PATH + ").\n\nError Message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sets up the menu bar that will appear at the top of the window.
     */
    private void setupMenuBar()
    {
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        newGameMenuItem = new JMenuItem("New Game");
        highScoresMenuItem = new JMenuItem("High Scores");
        closeMenuItem = new JMenuItem("Close");
        fileMenu.add(newGameMenuItem);
        fileMenu.add(highScoresMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(closeMenuItem);

        helpMenu = new JMenu("Help");
        aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        fileMenu.addMenuListener(menuListener);
        helpMenu.addMenuListener(menuListener);

        newGameMenuItem.addActionListener(menuListener);
        highScoresMenuItem.addActionListener(menuListener);
        closeMenuItem.addActionListener(menuListener);
        aboutMenuItem.addActionListener(menuListener);

        this.setJMenuBar(menuBar);
    }

    /**
     * Sets up the snake game container.
     */
    private void setupSnakeGameContainer()
    {
        JPanel gameContainerPanel = new JPanel();
        gameContainerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gameContainerPanel.setBackground(BG_COLOUR);

        snakeGame = new SnakeGameContainer();
        gameContainerPanel.add(snakeGame);

        snakeGame.addEventListener(this);

        this.add(gameContainerPanel);
    }

    /**
     * Handles the occurrence of if a new high score is achieved from the player. If a high score is not achieved, this method will not do
     * anything.
     */
    private void handleNewHighScore()
    {
        if (snakeGame.getScore() > 0)
        {
            int rank = highScoreMngr.getHighScoreRank(snakeGame.getScore());

            if (rank != -1)
            {
                String name;
                boolean cancelled = false;
                boolean invalidName = false;

                while (true) // Loop forever until a valid name is entered
                {
                    name = JOptionPane.showInputDialog(null, "You achieved a high score! Enter your name to be displayed on the high score board:", "Congratulations", JOptionPane.INFORMATION_MESSAGE);

                    if (name == null) // User pressed cancel
                    {
                        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel? Your high score will not be saved.", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    
                        if (result == JOptionPane.YES_OPTION)
                        {
                            cancelled = true;

                            break;
                        }
                    }
                    else // User entered data in the text field
                    {
                        if (highScoreMngr.isValidName(name))
                        {
                            break;
                        }
                        else
                        {
                            invalidName = true;
                        }
                    }

                    if (invalidName)
                    {
                        JOptionPane.showMessageDialog(null, "The name you entered is invalid. A valid name cannot contain \"" + highScoreMngr.getDataDelimiter() + "\" and also it must be between " + highScoreMngr.MIN_NAME_LENGTH + " and " + highScoreMngr.MAX_NAME_LENGTH + " characters in length.", "Error", JOptionPane.ERROR_MESSAGE);

                        invalidName = false; // Reset this for the next iteration of the while loop
                    }
                } // End while

                if (!cancelled)
                {
                    highScoreMngr.updateHighScore(rank, name, snakeGame.getScore());

                    try
                    {
                        highScoreMngr.saveHighScores();

                        showHighScoreWindow();
                    }
                    catch (IOException ex)
                    {
                        JOptionPane.showMessageDialog(null, "An error occured while trying to save the high score file (" + highScoreMngr.HIGH_SCORE_FILE_PATH + ").\n\nError Message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    /**
     * Creates a new instance of the high score window and shows it.
     */
    private void showHighScoreWindow()
    {
        HighScoresWindow hsWindow = new HighScoresWindow(this, highScoreMngr);
        hsWindow.setVisible(true);
    }

    /**
     * Updates the JFrame window title in a specific format with the current score the player achieved in the
     * snake game game.
     */
    private void updateTitleWithScore()
    {
        this.setTitle(WINDOW_TITLE + " | Score: " + snakeGame.getScore());
    }

    @Override
    public void onGameStarted()
    {
        updateTitleWithScore();
    }

    @Override
    public void onGameOver()
    {
        this.setTitle(WINDOW_TITLE + " | Game Over! Final Score: " + snakeGame.getScore());

        handleNewHighScore();
    }

    @Override
    public void onGameWon()
    {
        this.setTitle(WINDOW_TITLE + " | You win! Final Score: " + snakeGame.getScore());
    }

    @Override
    public void onScoreUpdated()
    {
        updateTitleWithScore();
    }
}
