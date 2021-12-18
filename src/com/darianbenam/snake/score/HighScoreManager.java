// File Name:     HighScoreManager.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Friday, May 29, 2020

package com.darianbenam.snake.score;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HighScoreManager
{
    public final String HIGH_SCORE_FILE_PATH = "data\\highscores.dat";
    public final int HIGH_SCORES_COUNT = 5;
    public final int MIN_NAME_LENGTH = 1, MAX_NAME_LENGTH = 30;

    private final String DATA_DELIMITER = "\\|"; // NOTE: The pipe symbol is a metacharacter in regex so we must escape it by using two backwards slashes
    private final String COMMENT_PREFIX = "--";

    private HighScore[] highScoresLeaderboard;

    /**
     * Creates a new instance of a High Score Manager with default values. No high scores are loaded by calling this constructor. If you
     * want to load high scores, use the load high scores method.
     * 
     * @see #loadHighScores()
     */
    public HighScoreManager()
    {
        clearHighScoresLeaderboard();
    }
 
    /**
     * Clears the high score leaderboard by creating a new one initializing each indexes value to have a blank name with a score of zero.
     */
    public void clearHighScoresLeaderboard()
    {
        highScoresLeaderboard = new HighScore[HIGH_SCORES_COUNT];

        for (int i = 0; i < HIGH_SCORES_COUNT; i++)
        {
            highScoresLeaderboard[i] = new HighScore("", 0);
        }
    }

    /**
     * Gets the data delimiter without the escape characters (two backwards slashes) that regex requires for special characters.
     * 
     * @return A string that contains the data delimiter.
     */
    public String getDataDelimiter()
    {
        return DATA_DELIMITER.replace("\\", "");
    }

    /**
     * Gets all the high scores currently stored in the high score manager.
     * 
     * @return An array of type HighScore which is read only.
     */
    public final HighScore[] getHighScoresLeaderboard()
    {
        return highScoresLeaderboard;
    }

    /**
     * Gets the header comment which appears at the top of the high scores file.
     * 
     * @return A String which contains the header comment.
     */
    private String getFileHeaderComment()
    {
        return COMMENT_PREFIX + " This file stores the high scores for the Snake Game by Darian Benam.\n" + COMMENT_PREFIX + " MODIFYING THIS FILE CAN RESULT IN DATA CORRUPTION / UNEXPECTED PROGRAM BEHAVIOUR.\n\n";
    }

    /**
     * Loads the high scores stored in the high score file. The high score file is defined in the global constant variable
     * HIGH_SCORE_FILE_PATH.
     * 
     * @throws Exception
     */
    public void loadHighScores() throws Exception
    {
        File highScoreFile = new File(HIGH_SCORE_FILE_PATH);
        Scanner fileReader = new Scanner(highScoreFile);

        int totalLinesRead = 0;
        String highScoreLine;
        String[] lineTokens;
        
        while (fileReader.hasNextLine() && totalLinesRead < HIGH_SCORES_COUNT)
        {
            highScoreLine = fileReader.nextLine();

            // Skip over empty lines and lines that are comments
            if (highScoreLine.isEmpty() 
                || highScoreLine.length() >= COMMENT_PREFIX.length()
                && highScoreLine.substring(0, COMMENT_PREFIX.length()).equals(COMMENT_PREFIX))
            {
                continue;
            }

            lineTokens = highScoreLine.split(DATA_DELIMITER);

            highScoresLeaderboard[totalLinesRead].name = lineTokens[0];
            highScoresLeaderboard[totalLinesRead].score = Integer.parseInt(lineTokens[1]);

            totalLinesRead++;
        }

        fileReader.close();
    }

    /**
     * Saves the high score leaderboard to the hard drive.
     * 
     * @throws IOException
     */
    public void saveHighScores() throws IOException
    {
        File highScoreFile = new File(HIGH_SCORE_FILE_PATH);
        FileWriter fileWriter = new FileWriter(highScoreFile);

        // First write the header comment
        fileWriter.write(getFileHeaderComment());

        // Now write all the high scores
        for (int i = 0; i < highScoresLeaderboard.length; i++)
        {
            fileWriter.write(highScoresLeaderboard[i].name + getDataDelimiter() + highScoresLeaderboard[i].score + (i == highScoresLeaderboard.length - 1 ? "" : "\n"));
        }

        fileWriter.close();
    }

    /**
     * Gets the rank position for a score. If a score beats a current high score, then that rank position is returned.
     * 
     * @param score The score to be compared to the current high scores.
     * @return Either a number >= 1 if the score beat a high score (where that number represents the rank position of the high score), or
     *         -1 if the score did not beat any high scores on the leaderboard.
     */
    public int getHighScoreRank(int score)
    {
        for (int i = 0; i < highScoresLeaderboard.length; i++)
        {
            if (score >= highScoresLeaderboard[i].score)
            {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * Determines whether a name is valid or not.
     * 
     * A valid name has the following criteria:
     *  - Is not null
     *  - Is not empty
     *  - Is between MIN_NAME_LENGTH and MAX_NAME_LENGTH
     *  - Does not contain the data delimiter character
     * 
     * @return True if the name is valid, if not, false.
     */
    public boolean isValidName(String name)
    {
        return name != null && !name.isEmpty() && name.length() >= MIN_NAME_LENGTH && name.length() <= MAX_NAME_LENGTH && !name.contains(getDataDelimiter());
    }

    /**
     * Updates the high score list by adding/replacing a name at a specified rank.
     * 
     * @param rank The rank that this user will take.
     * @param name The name of the user who got the high score.
     * @param score The high score the user achieved.
     * 
     * @throws RuntimeException This will only be thrown if the rank is out of bounds.
     */
    public void updateHighScore(int rank, String name, int score)
    {
        final int RANK_INDEX = rank - 1;

        if (RANK_INDEX < 0 || RANK_INDEX > HIGH_SCORES_COUNT)
        {
            throw new RuntimeException("Rank out of bounds.");
        }

        // First shift all the high scores down
        for (int i = highScoresLeaderboard.length - 1; i > RANK_INDEX; i--)
        {
            highScoresLeaderboard[i].name = highScoresLeaderboard[i - 1].name;
            highScoresLeaderboard[i].score = highScoresLeaderboard[i - 1].score;
        }

        // Now update the high score
        highScoresLeaderboard[RANK_INDEX].name = name;
        highScoresLeaderboard[RANK_INDEX].score = score;
    }
}
