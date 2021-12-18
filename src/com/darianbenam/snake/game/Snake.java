// File Name:     Snake.java
// By:            Darian Benam (GitHub: https://github.com/BeardedFish/)
// Date:          Sunday, May 17, 2020

package com.darianbenam.snake.game;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Snake
{
    public static final Color SNAKE_COLOUR = new Color(50, 205, 50);
    public final boolean WALL_COLLISION;
    private final int LEFT_BOUND, TOP_BOUND, BOTTOM_BOUND, RIGHT_BOUND;

    private ArrayList<Point> bodyPartsList;
    private int snakeBodySize, mapHeight, mapWidth;
    private Point tailLastLocation;

    /**
     * Constructor which creates a new instance of the Snake class. The snake will have a head when created and no
     * other body parts.
     *
     * @param startLoc The location where the snake head should start at.
     * @param wallCollision States whether there is wall collision on the map that this snake belongs to or not.
     * @param snakeBodySize The size of a single snake body part (length and width), in pixels.
     * @param mapHeight The height of the map that this snake belongs to.
     * @param mapWidth The width of the map that this snake belongs to.
     */
    public Snake(Point startLoc, boolean wallCollision, int snakeBodySize, int mapHeight, int mapWidth)
    {
        bodyPartsList = new ArrayList<Point>();
        bodyPartsList.add(startLoc);

        this.WALL_COLLISION = wallCollision;
        this.snakeBodySize = snakeBodySize;
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;

        this.LEFT_BOUND = 0;
        this.TOP_BOUND = 0;
        this.BOTTOM_BOUND = mapHeight / snakeBodySize;
        this.RIGHT_BOUND = mapWidth / snakeBodySize;
    }

    public final ArrayList<Point> getBodyPartsList()
    {
        return bodyPartsList;
    }

    public Point getTailLastLocation()
    {
        return tailLastLocation;
    }

    /**
     * Adds a body part to the snake at a specific location.
     *
     * @param loc The x,y location to where the body part will be added.
     */
    public void addBodyPart(Point loc)
    {
        bodyPartsList.add(loc);
    }

    /**
     * Adds a body part to the snake at a specific direction around the tail.
     *
     * @param dir The direction where the body part should be added from the snake tail.
     */
    public void addBodyPart(Direction dir)
    {
        Point tailLocation = getDirectionOffset(bodyPartsList.get(bodyPartsList.size() - 1), dir);
        bodyPartsList.add(tailLocation);

        tailLastLocation = tailLocation;
    }

    /**
     * Moves the snake in a specified direction.
     *
     * @param dir The direction to move the snake.
     */
    public CollisionType move(Direction dir)
    {
        tailLastLocation = bodyPartsList.get(bodyPartsList.size() - 1);
        
        Point previousLocation = bodyPartsList.get(0);
        Point newHeadLoc = getDirectionOffset(previousLocation, dir);

        if (willCollideWithBody(newHeadLoc))
        {
            return CollisionType.Body;
        }

        if (WALL_COLLISION && willGoOutOfBounds(newHeadLoc))
        {
            return CollisionType.Wall;
        }

        // Move the head
        bodyPartsList.set(0, newHeadLoc);

        // Move all the body parts
        for (int i = 1; i < bodyPartsList.size(); i++)
        {
            Point tempPreviousLocation = bodyPartsList.get(i);

            bodyPartsList.set(i, previousLocation);

            previousLocation = tempPreviousLocation;
        }

        return CollisionType.None;
    }

    /**
     * Determines whether the snake head will collide with one of its body parts (excluding its tail).
     *
     * @param headLoc The location of the snake head.
     * @return True if the snake head collided with one of its body parts, if not, false.
     */
    public boolean willCollideWithBody(Point headLoc)
    {
        for (int i = 1; i < bodyPartsList.size() - 1; i++)
        {
            if (headLoc.equals(bodyPartsList.get(i)))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a snake head will go out of bounds. Out of bounds is when the snake head goes past a wall.
     *
     * @param headLoc The location of the snake head.
     * @return True if the snake head will go out of bounds, if not, false.
     */
    public boolean willGoOutOfBounds(Point headLoc)
    {
        return headLoc.y < 0 || headLoc.x < 0  || headLoc.y >= mapHeight / snakeBodySize || headLoc.x >= mapWidth / snakeBodySize;
    }

    /**
     * Gets the offset point based off of a direction. For example, if the initial point is [x: 0, y: 0] and the direction was
     * left then that offset point would be [x: -1, y: 0].
     * 
     * @param initialPoint The point to get the offset of.
     * @param dir The direction to move the initialPoint to.
     * @return The point that the direction lead to.
     */
    private Point getDirectionOffset(Point initialPoint, Direction dir)
    {
        Point offsetPoint = (Point)initialPoint.clone();

        if (dir == Direction.Down)
        {
            offsetPoint.y++;
        }

        if (dir == Direction.Left)
        {
            offsetPoint.x--;
        }

        if (dir == Direction.Right)
        {
            offsetPoint.x++;
        }

        if (dir == Direction.Up)
        {
            offsetPoint.y--;
        }

        handleWallTeleportation(offsetPoint);

        return offsetPoint;
    }

    /**
     * Moves the snake head to the opposite wall if it goes past a wall. This method will only work if wall collision is turned off.
     *
     * @param headLoc The location of the snake head.
     */
    private void handleWallTeleportation(Point headLoc)
    {
        if (!WALL_COLLISION)
        {
            if (headLoc.y < TOP_BOUND)
            {
                headLoc.y = BOTTOM_BOUND - 1;
            }

            if (headLoc.x < LEFT_BOUND)
            {
                headLoc.x = RIGHT_BOUND - 1;
            }

            if (headLoc.y >= BOTTOM_BOUND)
            {
                headLoc.y = TOP_BOUND;
            }

            if (headLoc.x >= RIGHT_BOUND)
            {
                headLoc.x = LEFT_BOUND;
            }
        }
    }
}
