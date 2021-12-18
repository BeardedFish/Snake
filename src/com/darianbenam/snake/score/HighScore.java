// File Name:     HighScore.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Friday, May 29, 2020

package com.darianbenam.snake.score;

public class HighScore
{
    public String name;
    public int score;

    /**
     * Creates a new HighScore class instance which holds two values.
     * 
     * @param name The name of the person who achieved the high score.
     * @param score The high score that the person achieved.
     */
    public HighScore(String name, int score)
    {
        this.name = name;
        this.score = score;
    }
}
