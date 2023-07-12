// File Name:     SnakeGameWindow.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Sunday, May 17, 2020

package com.darianbenam.snake.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;

public class SnakeGameContainer extends JPanel
{
    private final Color BG_COLOUR = new Color(30, 30, 30);
    private final Color FOOD_COLOUR = new Color(255, 44, 88);
    private final Color TEXT_COLOUR = new Color(255, 255, 255);
    private final Direction INITIAL_SNAKE_DIR = Direction.Right;
    private final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 24);
    private final boolean WALL_COLLISION = true;
    private final int FOOD_POINTS_WORTH = 15;
    private final int GAME_LOOP_SLEEP_MS = 75;
    private final int SNAKE_DIMENSIONS = 10;
    private final int SNAKE_START_X = 3, SNAKE_START_Y = 1;
    private final int CONTAINER_HEIGHT = SNAKE_DIMENSIONS * 50, CONTAINER_WIDTH = SNAKE_DIMENSIONS * 75;

    private ArrayList<SnakeGameContainerListener> eventListenersList = new ArrayList<SnakeGameContainerListener>();
    private Direction nextSnakeDirection = INITIAL_SNAKE_DIR, snakeDirection = nextSnakeDirection;
    private Point foodLocation;
    private Snake snake;
    private boolean gamePaused = false, gameStarted = false, gameOver = false, gameWon = false, killLoopThread = false;
    private int score = 0;

    /**
     * Constructor which creates the snake game container panel.
     */
    public SnakeGameContainer()
    {
        super(true); // Enable double buffering

        this.setBackground(BG_COLOUR);

        setupSnakeAndFood();
    }

    public int getScore()
    {
        return score;
    }

    /**
     * Gets the size of the snake game container. The size of the container is constant.
     *
     * @return A Dimension which represents the snake game containers size.
     */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(CONTAINER_WIDTH, CONTAINER_HEIGHT);
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public boolean isGamePaused()
    {
        return gamePaused;
    }

    public boolean isGameStarted()
    {
        return gameStarted;
    }

    public void setPauseState(boolean pause)
    {
        gamePaused = pause;
    }

    /**
     * Changes the snake travel direction. This method wont change the snakes travel direction if the direction is the opposite 
     * direction of where the snake is heading or if the game is paused.
     *
     * @param dir The direction the snake should travel.
     */
    public void setSnakeDirection(Direction dir)
    {
        if (getOppositeDirection(dir).equals(snakeDirection) || gamePaused)
        {
            return;
        }

        nextSnakeDirection = dir;
    }

    /**
     * Adds an event listener to the list of event listeners for this snake game container.
     *
     * @param scoreListener The listener instance to be added to the list.
     */
    public void addEventListener(SnakeGameContainerListener scoreListener)
    {
        eventListenersList.add(scoreListener);
    }

    /**
     * Starts the game by resetting variables and running the game loop on a new thread. This method notifies
     * all event listeners that the game started.
     */
    public void startGame()
    {
        if (gameOver)
        {
            setupSnakeAndFood();
        }

        resetScore();
        resetVariables();

        gameStarted = true;

        // Start the game loop on a new thread via lambda expression
        new Thread(() ->
        {
            gameLoop();
        }).start();

        for (SnakeGameContainerListener listener : eventListenersList)
        {
            listener.onGameStarted();
        }
    }

    /**
     * Starts a new game by resetting everything to its inital state.
     */
    public void startNewGame()
    {
        killLoopThread = true;

        setupSnakeAndFood();
        resetScore();
        resetVariables();

        this.repaint();
    }

    /**
     * Resets some global scope variables to their default values.
     */
    private void resetVariables()
    {
        nextSnakeDirection = INITIAL_SNAKE_DIR;
        snakeDirection = nextSnakeDirection;
        gameOver = false;
        gamePaused = false;
        gameStarted = false;
        gameWon = false;
        killLoopThread = false;
    }

    /**
     * Sets up the snake game by creating the snake, generating a food at a random location, and starting
     * the main game loop.
     */
    private void setupSnakeAndFood()
    {
        // Create a new snake with a length of three
        snake = new Snake(new Point(SNAKE_START_X, SNAKE_START_Y), WALL_COLLISION, SNAKE_DIMENSIONS, CONTAINER_HEIGHT, CONTAINER_WIDTH);
        snake.addBodyPart(Direction.Left);
        snake.addBodyPart(Direction.Left);

        generateFood();
    }

    /**
     * The game loop which will run forever until the game is over or if the player won the game.
     */
    private void gameLoop()
    {
        while (!killLoopThread && !gameOver && !gameWon)
        {
            if (!gamePaused)
            {
                try
                {
                    CollisionType collisionTypeAfterMoving = snake.move(snakeDirection);
                    if (collisionTypeAfterMoving != CollisionType.None) // Either collided with a wall (if there are no walls) or one of its body parts
                    {
                        gameOver();

                        break;
                    }

                    handleFoodCollision();

                    snakeDirection = nextSnakeDirection;

                    Thread.sleep(GAME_LOOP_SLEEP_MS);
                }
                catch (InterruptedException ex)
                {
                    System.out.println("Exception thrown in game loop: " + ex.toString());
                }
            }

            this.repaint();
        }
    }

    /**
     * Handles when the snake head collidies with the food on the map. When the snake head collidies with the food, the score is added
     * some points and the snake grows by one unit.
     */
    private void handleFoodCollision()
    {
        if (snake.getBodyPartsList().get(0).equals(foodLocation))
        {
            addPointsToScore();

            snake.addBodyPart(snake.getTailLastLocation());

            generateFood();
        }
    }

    /**
     * Generates a food on the map at a random location where the snake body is not present.
     */
    private void generateFood()
    {
        ArrayList<Point> map = getEmptyMapPoints();

        if (map.size() == 0) // Snake has filled up the entire map
        {
            winGame();
        }
        else // Snake has not filled up the entire map
        {
            int randIndex = (int)(Math.random() * map.size());
            foodLocation = map.get(randIndex);
        }
    }

    /**
     * Gets all the map points where the snake is not present.
     *
     * @return An ArrayList of type Point which contains all the empty coordinate locations of the map.
     */
    private ArrayList<Point> getEmptyMapPoints()
    {
        ArrayList<Point> result = new ArrayList<Point>();

        Point mapPoint;
        for (int row = 0; row < CONTAINER_HEIGHT / SNAKE_DIMENSIONS; row++) // Rows
        {
            for (int col = 0; col < CONTAINER_WIDTH/ SNAKE_DIMENSIONS; col++)
            {
                mapPoint = new Point(col, row);

                if (!snake.getBodyPartsList().contains(mapPoint))
                {
                    result.add(mapPoint);
                }
            }
        }

        return result;
    }

    /**
     * Gets the opposite direction of a direction.
     *
     * @param dir The direction that you want the opposite of.
     * @return The direction that is opposite of the 'dir' parameter.
     */
    private Direction getOppositeDirection(Direction dir)
    {
        Direction oppDir;

        if (dir == Direction.Down)
        {
            oppDir = Direction.Up;
        }
        else if (dir == Direction.Right)
        {
            oppDir = Direction.Left;
        }
        else if (dir == Direction.Left)
        {
            oppDir = Direction.Right;
        }
        else // dir equals Up
        {
            oppDir = Direction.Down;
        }

        return oppDir;
    }

    /**
     * Sets variables that tell the snake game container that the game was won which will overall stop the game loop.
     * This also will notify all listeners that the game was won.
     */
    private void winGame()
    {
        gameStarted = false;
        gameOver = true;
        gameWon = true;

        for (SnakeGameContainerListener listener : eventListenersList)
        {
            listener.onGameWon();
        }
    }

    /**
     * Sets variables that tell the snake game container that the game is over due to either the snake colliding with
     * itself or with a wall. This method will overall stop the game loop and will also will notify all listeners that
     * the game is over.
     */
    private void gameOver()
    {
        gameOver = true;
        gameStarted = false;

        for (SnakeGameContainerListener listener : eventListenersList)
        {
            listener.onGameOver();
        }
    }

    /**
     * Adds FOOD_POINTS_WORTH to the score and notifies all listeners that the score was updated.
     */
    private void addPointsToScore()
    {
        score += FOOD_POINTS_WORTH;

        for (SnakeGameContainerListener listener : eventListenersList)
        {
            listener.onScoreUpdated();
        }
    }

    /**
     * Resets the score to zero and notifies all listeners that the score was updated.
     */
    private void resetScore()
    {
        score = 0;

        for (SnakeGameContainerListener listener : eventListenersList)
        {
            listener.onScoreUpdated();
        }
    }

    /**
     * Draw a String centered in the middle of a Rectangle object. The code for this method was borrowed from
     * https://stackoverflow.com/a/27740330/11760346/.
     *
     * @param g The Graphics object used to draw.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font)
    {
        FontMetrics metrics = g.getFontMetrics(font);

        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setFont(font);
        g.drawString(text, x, y);
    }

    /**
     * Paints the snake game container panel. Everything visual that appears on the snake game container panel is
     * handled here.
     *
     * @param g The Graphics object used to paint on the panel.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the food
        g2d.setColor(FOOD_COLOUR);
        g2d.fillRect(foodLocation.x * SNAKE_DIMENSIONS, foodLocation.y * SNAKE_DIMENSIONS, SNAKE_DIMENSIONS, SNAKE_DIMENSIONS);

        // Draw the snake
        g2d.setColor(Snake.SNAKE_COLOUR);
        for (int i = 0; i < snake.getBodyPartsList().size(); i++)
        {
            Point bodyPartLoc = snake.getBodyPartsList().get(i);

            g2d.fillRect(bodyPartLoc.x * SNAKE_DIMENSIONS, bodyPartLoc.y * SNAKE_DIMENSIONS, SNAKE_DIMENSIONS, SNAKE_DIMENSIONS);
        }

        g2d.setColor(TEXT_COLOUR);

        if (gameWon)
        {
            drawCenteredString(g2d, "You win!", this.getBounds(), TEXT_FONT);
        }
        else if (gameOver && !gameStarted)
        {
            drawCenteredString(g2d, "Game over! Press the Spacebar to start a new game!", this.getBounds(), TEXT_FONT);
        }
        else if (!gameStarted)
        {
            drawCenteredString(g2d, "Press the Spacebar to start the game!", this.getBounds(), TEXT_FONT);
        }
        else if (gamePaused)
        {
            drawCenteredString(g2d, "Game paused. Press P to unpause.", this.getBounds(), TEXT_FONT);
        }
    }
}
