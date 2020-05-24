// File Name:     SnakeGameEventListener.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Sunday, May 18, 2020

public interface SnakeGameContainerListener
{
    /**
     * Occurs when the snake head touches one of its body parts or if it collides head on with a wall.
     */
    void onGameOver();

    /**
     * Occurs when the user starts the game.
     */
    void onGameStarted();

    /**
     * Occurs when the user starts wins the game.
     */
    void onGameWon();

    /**
     * Occurs when the snake head collides with a food.
     */
    void onScoreUpdated();
}