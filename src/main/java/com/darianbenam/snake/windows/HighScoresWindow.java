// File Name:     HighScoresWindow.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Thursday, May 28, 2020

package com.darianbenam.snake.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.darianbenam.snake.score.HighScore;
import com.darianbenam.snake.score.HighScoreManager;

public class HighScoresWindow extends JDialog
{
    private static final String WINDOW_TITLE = "High Scores";
    private final String EMPTY_NAME_VALUE = "-";

    private HighScoreManager highScoreMngr;
    private ButtonListener btnListener;

    private JButton okBtn, clearHighScoresBtn;
    private JLabel windowTitleLbl, rankTitleLbl, nameTitleLbl, scoreTitleLbl;
    private HighScoreRow[] highScoreRows;
    private JPanel highScoresPnl, buttonsPnl;
    
    /**
     * Inner class for handling JButton clicks on this window.
     */
    private class ButtonListener implements ActionListener
    {
        /**
         * Invoked every time a JButton is clicked.
         *
         * @param e The ActionEvent that occurred when the JButton was clicked.
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == okBtn)
            {
                dispose(); // Dispose the HighScoresWindow, which will overall close the window
            }

            if (e.getSource() == clearHighScoresBtn)
            {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the high scores? This action cannot be undone.", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
                if (result ==  JOptionPane.YES_OPTION)
                {
                    highScoreMngr.clearHighScoresLeaderboard();
                    updateHighScoreRows();

                    try
                    {
                        highScoreMngr.saveHighScores();
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
     * Inner class for storing a row of a high score.
     */
    private class HighScoreRow
    {
        public JLabel rankLbl, nameLbl, scoreLbl;

        public HighScoreRow(Font parentFont, int rank, String name, int score)
        {
            final Font lblFont = new Font(parentFont.getFontName(), Font.PLAIN, parentFont.getSize());

            rankLbl = new JLabel(Integer.toString(rank));
            nameLbl = new JLabel(name);
            scoreLbl = new JLabel(Integer.toString(score));

            rankLbl.setFont(lblFont);
            nameLbl.setFont(lblFont);
            scoreLbl.setFont(lblFont);

            rankLbl.setHorizontalAlignment(JLabel.CENTER);
            nameLbl.setHorizontalAlignment(JLabel.CENTER);
            scoreLbl.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    /**
     * Creates a window that shows the top N high scores in the snake game.
     * 
     * @param parentFrame The parent frame of the snake game.
     * @param highScoreMngr The high score manager instance that contains the loaded high score leaderboard.
     */
    public HighScoresWindow(JFrame parentFrame, HighScoreManager highScoreMngr)
    {
        super(parentFrame, WINDOW_TITLE, true);

        this.highScoreMngr = highScoreMngr;

        setupWindow();
    }

    /**
     * Sets up the high score window. By calling this method, the window is not shown.
     */
    public void setupWindow()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        setupWindowTitle();
        setupHighScorePnl(); 
        setupButtonsPnl();

        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void setupWindowTitle()
    {
        final Font titleFont = new Font(this.getFont().getFontName(), Font.BOLD, 24);

        windowTitleLbl = new JLabel("Top " + highScoreMngr.HIGH_SCORES_COUNT + " High Scores");
        windowTitleLbl.setFont(titleFont);
        windowTitleLbl.setBorder(new EmptyBorder(15, 15, 0, 15));

        this.add(windowTitleLbl, BorderLayout.NORTH);
    }

    /**
     * Sets up the high score panel which consists as a visual representation of what data is in the high score mananger.
     */
    private void setupHighScorePnl()
    {
        highScoresPnl = new JPanel();
        highScoresPnl.setLayout(new GridLayout(highScoreMngr.HIGH_SCORES_COUNT + 2, 4, 100, 10));
        highScoresPnl.setBorder(new EmptyBorder(15, 100, -15, 100));

        rankTitleLbl = new JLabel("Rank");
        nameTitleLbl = new JLabel("Name");
        scoreTitleLbl = new JLabel("Score");

        rankTitleLbl.setHorizontalAlignment(JLabel.CENTER);
        nameTitleLbl.setHorizontalAlignment(JLabel.CENTER);
        scoreTitleLbl.setHorizontalAlignment(JLabel.CENTER);

        highScoresPnl.add(rankTitleLbl);
        highScoresPnl.add(nameTitleLbl);
        highScoresPnl.add(scoreTitleLbl);

        updateHighScoreRows();

        this.add(highScoresPnl);
    }

    /**
     * Sets up the button panel which serves as a container for buttons that do certain tasks (ex: Ok button closes the window).
     */
    private void setupButtonsPnl()
    {
        btnListener = new ButtonListener();

        buttonsPnl = new JPanel();
        buttonsPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonsPnl.setBorder(new EmptyBorder(0, 15, 15, 15));

        okBtn = new JButton("Ok");
        clearHighScoresBtn = new JButton("Clear High Scores");

        okBtn.addActionListener(btnListener);
        clearHighScoresBtn.addActionListener(btnListener);

        buttonsPnl.add(okBtn);
        buttonsPnl.add(clearHighScoresBtn);

        this.add(buttonsPnl, BorderLayout.SOUTH);
    }

    /**
     * Updates the high score rows on the GUI of the window.
     */
    private void updateHighScoreRows()
    {
        HighScore[] highScores = highScoreMngr.getHighScoresLeaderboard();

        if (highScoreRows == null)
        {
            highScoreRows = new HighScoreRow[highScoreMngr.HIGH_SCORES_COUNT];
        }

        for (int i = 0; i < highScoreRows.length; i++)
        {
            final String PLAYER_NAME = highScores[i].name.isEmpty() ? EMPTY_NAME_VALUE : highScores[i].name;

            if (highScoreRows[i] == null) // Initialize the high score rows
            {
                highScoreRows[i] = new HighScoreRow(this.getFont(),
                                                    i + 1,
                                                    PLAYER_NAME,
                                                    highScores[i].score);

                highScoresPnl.add(highScoreRows[i].rankLbl);
                highScoresPnl.add(highScoreRows[i].nameLbl);
                highScoresPnl.add(highScoreRows[i].scoreLbl);
            }
            else // High score rows are already initialized
            {
                highScoreRows[i].nameLbl.setText(PLAYER_NAME);
                highScoreRows[i].scoreLbl.setText(Integer.toString(highScores[i].score));
            }
        }
    }
}
