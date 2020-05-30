// File Name:     HighScoreManager.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Friday, May 29, 2020

package high_score_mngr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HighScoreManager
{
    public final int HIGH_SCORES_COUNT = 5;

    private final String DATA_DELIMITER = "\\|"; // NOTE: The pipe symbol is a metacharacter in regex so we must escape it by using two backwards slashes
    private final String HIGH_SCORE_FILE_PATH = "highscores.dat";

    private HighScore[] highScores;

    /**
     * 
     */
    public HighScoreManager()
    {
        initHighScores();
    }

    /**
     * Gets all the high scores currently stored in the high score manager.
     * 
     * @return An array of type HighScore which is read only.
     */
    public final HighScore[] getHighScores()
    {
        return highScores;
    }

    /**
     * 
     * @throws IOException
     */
    public void loadHighScores() throws IOException
    {
        File highScoreFile = new File(HIGH_SCORE_FILE_PATH);
        Scanner fileReader = new Scanner(highScoreFile);

        int totalLinesRead = 0;
        String highScoreLine;
        String[] lineTokens;
        
        while (fileReader.hasNextLine() && totalLinesRead < HIGH_SCORES_COUNT)
        {
            highScoreLine = fileReader.nextLine();
            lineTokens = highScoreLine.split(DATA_DELIMITER);

            highScores[totalLinesRead].name = lineTokens[0];
            highScores[totalLinesRead].score = Integer.parseInt(lineTokens[1]);

            totalLinesRead++;
        }

        fileReader.close();
    }

    /**
     * 
     * @throws IOException
     */
    public void saveHighScores() throws IOException
    {
        File highScoreFile = new File(HIGH_SCORE_FILE_PATH);
        FileWriter fileWriter = new FileWriter(highScoreFile);

        for (int i = 0; i < highScores.length; i++)
        {
            fileWriter.write(highScores[i].name + DATA_DELIMITER.replace("\\", "") + highScores[i].score + (i == highScores.length - 1 ? "" : "\n"));
        }

        fileWriter.close();
    }

    /**
     * 
     * @param score
     * @return
     */
    public int getHighScoreRank(int score)
    {
        for (int i = 0; i < highScores.length; i++)
        {
            if (score >= highScores[i].score)
            {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * 
     * @param rank
     * @param name
     * @param score
     */
    public void updateHighScore(int rank, String name, int score)
    {
        final int RANK_INDEX = rank - 1;

        if (RANK_INDEX < 0 || RANK_INDEX > HIGH_SCORES_COUNT)
        {
            throw new RuntimeException("Rank out of bounds.");
        }

        // First shift all the high scores down
        for (int i = highScores.length - 1; i > RANK_INDEX; i--)
        {
            highScores[i].name = highScores[i - 1].name;
            highScores[i].score = highScores[i - 1].score;
        }

        // Now update the high score
        highScores[RANK_INDEX].name = name;
        highScores[RANK_INDEX].score = score;
    }

    /**
     * Initalizes the high score array by creating it and by setting each indexes value to have a blank name with a score of zero.
     */
    private void initHighScores()
    {
        highScores = new HighScore[HIGH_SCORES_COUNT];

        for (int i = 0; i < HIGH_SCORES_COUNT; i++)
        {
            highScores[i] = new HighScore("", 0);
        }
    }
}