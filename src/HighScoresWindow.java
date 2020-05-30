// File Name:     HighScoresWindow.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Thursday, May 28, 2020

import high_score_mngr.HighScore;
import high_score_mngr.HighScoreManager;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class HighScoresWindow extends JDialog
{
    public static final String WINDOW_TITLE = "High Scores";

    private final int WINDOW_HEIGHT = 500, WINDOW_WIDTH = 500;

    private JButton okBtn;
    private JLabel rankTitleLbl, nameTitleLbl, scoreTitleLbl;
    private JPanel highScoresPnl, buttonsPnl;

    private HighScoreRow[] highScoreRows;

    private class HighScoreRow
    {
        public JLabel rankLbl, nameLbl, scoreLbl;

        public HighScoreRow(int rank, String name, int score)
        {
            rankLbl = new JLabel(Integer.toString(rank));
            nameLbl = new JLabel(name);
            scoreLbl = new JLabel(Integer.toString(score));
        }
    }

    private HighScoreManager highScoreMngr;

    public HighScoresWindow(JFrame parentFrame, HighScoreManager highScoreMngr)
    {
        super(parentFrame, WINDOW_TITLE, true);

        this.highScoreMngr = highScoreMngr;

        setupWindow();
    }

    public void setupWindow()
    {
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setResizable(false);

        setupHighScorePnl(); 
        setupButtonsPnl();

        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void setupHighScorePnl()
    {
        highScoresPnl = new JPanel();
        highScoresPnl.setLayout(new GridLayout(highScoreMngr.HIGH_SCORES_COUNT + 2, 4, 100, 10));
        highScoresPnl.setBorder(new EmptyBorder(25, 25, 0, 25));

        rankTitleLbl = new JLabel("Rank");
        nameTitleLbl = new JLabel("Name");
        scoreTitleLbl = new JLabel("Score");

        highScoresPnl.add(rankTitleLbl);
        highScoresPnl.add(nameTitleLbl);
        highScoresPnl.add(scoreTitleLbl);

        HighScore[] highScores = highScoreMngr.getHighScores();

        highScoreRows = new HighScoreRow[highScoreMngr.HIGH_SCORES_COUNT];
        for (int i = 0; i < highScoreRows.length; i++)
        {
            highScoreRows[i] = new HighScoreRow(i + 1, highScores[i].name, highScores[i].score);

            highScoresPnl.add(highScoreRows[i].rankLbl);
            highScoresPnl.add(highScoreRows[i].nameLbl);
            highScoresPnl.add(highScoreRows[i].scoreLbl);
        }

        this.add(highScoresPnl);
    }

    private void setupButtonsPnl()
    {
        buttonsPnl = new JPanel();
        buttonsPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonsPnl.setBorder(new EmptyBorder(25, 25, 25, 25));

        
        okBtn = new JButton("Ok");

        buttonsPnl.add(okBtn);

        this.add(buttonsPnl, BorderLayout.SOUTH);
    }
}