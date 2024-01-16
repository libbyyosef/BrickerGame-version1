package src;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.*;
import src.brick_strategies.CollisionStrategy;

import java.awt.event.KeyEvent;
import java.util.Random;

public class BrickerGameManager extends GameManager {
    private static final float BALL_SPEED = 270;
    private static final int PADDLE_WIDTH = 110;
    private static final String PADDLE_PATH = "assets/paddle.png";
    private static final int FRAME_TARGET = 80;
    private static final String BALL_PATH = "assets/ball.png";
    private static final String BALL_SOUND_PATH = "assets/blop.wav";
    private static final float BALL_WIDTH = 20;
    private static final float BALL_HEIGHT = 20;
    private static final float PADDLE_HEIGHT = 15;
    private static final int MIN_DIS_FROM_EDGE = 10;
    private static final int PADDLE_PADDING = 30;
    private static final String HEART_PATH = "assets/heart.png";
    private static final float LIFE_WIDTH = 20;
    private static final float LIFE_HEIGHT = 20;
    private static final float INITIAL_LIFE_X = 10;
    private static final int ROWS_BRICKS = 8;
    private static final int COLS_BRICKS = 7;
    private static final String BRICKS_PATH = "assets/brick.png";
    private static final int BRICK_PADDING_X = 10;
    private static final int BRICK_PADDING_Y = 5;
    private static final float BRICK_HEIGHT = 20;
    private static final float INIT_BRICK_Y_PADDING = 40;
    private static final float INIT_BRICK_X_PADDING = 20;
    private static final float INIT_NUMERIC_WIDTH = 100;
    private static final String BG_PATH = "assets/bg.jpeg";
    private static final String WINNING_MASSAGE = "You win!";
    private static final String LOSING_MASSAGE = "You Lose!";
    private static final String WINDOW_NAME = "GAME";
    private static final float WINDOW_WIDTH = 700;
    private static final float WINDOW_HEIGHT = 500;
    private final Counter livesCounter1;
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;

    private Brick[][] bricks;
    private final Counter bricksCounter;
    private UserInputListener inputListener1;

    /**
     * constructor of the game board and logic
     *
     * @param windowTitle      the name on the window
     * @param windowDimensions sizes of the window
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        livesCounter1 = new Counter();
        bricksCounter = new Counter();
    }

    /**
     * initilize all the game object to their starting point
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        /*initialization*/
        this.bricksCounter.reset();

        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(FRAME_TARGET);
        inputListener1 = inputListener;

        /*create ball*/
        createBall(imageReader, soundReader, windowController);

        /*create paddles*/
        Renderable paddleImage = imageReader.readImage(PADDLE_PATH, false);

        /*create paddle*/
        createPaddle(paddleImage, inputListener, windowDimensions);

        /*create hearts*/
        createHearts(imageReader);

        /*create bricks*/
        createBricks(imageReader, new CollisionStrategy(super.gameObjects()),
                windowDimensions);

        /*creates borders*/
        createBorders(new Vector2(1, windowDimensions.y()));

        /*create numericLifeCounter*/
        createNumericLifeCounter();

        /*create background*/
        createBackground(imageReader);
    }

    /**
     * creates the ball
     *
     * @param imageReader      image of ball
     * @param soundReader      sound when the ball hits an object
     * @param windowController information of the window game
     */
    private void createBall(ImageReader imageReader
            , SoundReader soundReader, WindowController windowController) {
        Renderable ballImage = imageReader.readImage(BALL_PATH, true);
        Sound collisionSound = soundReader.readSound(BALL_SOUND_PATH);
        ball = new Ball(Vector2.ZERO, new Vector2(BALL_WIDTH, BALL_HEIGHT), ballImage, collisionSound);
        windowDimensions = windowController.getWindowDimensions();
        relocateBall();
    }

    /**
     * relocate the ball at the start of the game and when the user loses
     */
    private void relocateBall() {
        ball.setCenter(windowDimensions.mult(0.5f));
        this.gameObjects().addGameObject(ball);
        float ballVelX = BALL_SPEED;
        float ballVelY = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()) {
            ballVelX *= -1;
        }
        if (rand.nextBoolean()) {
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
    }

    /**
     * creates the paddle
     *
     * @param paddleImage      image of the paddle
     * @param inputListener    input from the user if to move right or left
     * @param windowDimensions sizes of paddle
     */
    private void createPaddle(Renderable paddleImage, UserInputListener inputListener, Vector2 windowDimensions) {
        GameObject Paddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDLE_HEIGHT), paddleImage,
                inputListener, windowDimensions, MIN_DIS_FROM_EDGE);
        Paddle.setCenter(new Vector2(windowDimensions.x() / 2, (int) windowDimensions.y() - PADDLE_PADDING));
        gameObjects().addGameObject(Paddle);
    }


    /**
     * creates the graphic lives
     *
     * @param imageReader image of the lives - a heart
     */
    private void createHearts(ImageReader imageReader) {
        Renderable heartImage = imageReader.readImage(HEART_PATH, true);
        int numOfLives = 3;
        Vector2 initialVec = Vector2.LEFT.add(new Vector2(INITIAL_LIFE_X,
                windowDimensions.y() - LIFE_HEIGHT - 1));
        GraphicLifeCounter graphicLifeCounter = new GraphicLifeCounter(initialVec, new Vector2(LIFE_WIDTH, LIFE_HEIGHT),
                this.livesCounter1, heartImage, super.gameObjects(), numOfLives);
        this.gameObjects().addGameObject(graphicLifeCounter, Layer.FOREGROUND);
    }


    /**creates 56 bricks
     * @param imageReader image of a brick
     * @param strategy strategy to collide the brick
     * @param windowDimensions sizes of a brick
     */
    private void createBricks(ImageReader imageReader, CollisionStrategy strategy, Vector2 windowDimensions) {
        bricks = new Brick[ROWS_BRICKS][COLS_BRICKS];
        Renderable brickImage = imageReader.readImage(BRICKS_PATH, false);
        float widthOfBrick = (windowDimensions.x()) / ROWS_BRICKS;
        float heightOfBrick = BRICK_HEIGHT;
        Vector2 initialVec = Vector2.LEFT.add(new Vector2(INIT_BRICK_X_PADDING, INIT_BRICK_Y_PADDING));
        for (int i = 0; i < ROWS_BRICKS; i++) {
            for (int j = 0; j < COLS_BRICKS; j++) {
                bricks[i][j] =
                        new Brick(initialVec.add(new Vector2(((int) widthOfBrick + BRICK_PADDING_X) * j,
                        ((int) heightOfBrick + BRICK_PADDING_Y) * i)),
                        new Vector2(widthOfBrick, heightOfBrick), brickImage,
                        strategy,
                        bricksCounter);
                gameObjects().addGameObject(bricks[i][j]);
                bricksCounter.increment();
            }
        }
    }


    /**creates left and right borders and a ceiling
     * @param vec size of sides
     */
    private void createBorders(Vector2 vec) {
        GameObject leftWall = new GameObject(Vector2.ZERO, vec, null);
        GameObject rightWall = new GameObject(new Vector2(windowDimensions.x(), 0), vec, null);
        GameObject ceiling = new GameObject(Vector2.ZERO, new Vector2(windowDimensions.x(), 3), null);
        gameObjects().addGameObject(leftWall);
        gameObjects().addGameObject(rightWall);
        gameObjects().addGameObject(ceiling);
    }

    /**
     * creates the text of number of lives
     */
    private void createNumericLifeCounter() {
        Vector2 initialVec = Vector2.LEFT.add(new Vector2(INIT_NUMERIC_WIDTH,
                windowDimensions.y() - LIFE_HEIGHT-1));
        NumericLifeCounter numericLifeCounter = new NumericLifeCounter(livesCounter1, initialVec,
                new Vector2(LIFE_WIDTH, LIFE_HEIGHT),
                super.gameObjects());
        gameObjects().addGameObject(numericLifeCounter, Layer.FOREGROUND);
    }

    /**creates background
     * @param imageReader background image
     */
    private void createBackground(ImageReader imageReader) {
        Renderable backgroundImage = imageReader.readImage(BG_PATH, false);
        GameObject background = new GameObject(Vector2.ZERO, new Vector2(windowDimensions.x(), windowDimensions
                .y()), backgroundImage);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }


    /**check if the game ends - if it is a win or a loose and update the board according to it
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkForGameEnd();
    }


    /**
     * check if the game ends - if it is a win or a loose, let the user decides if he wants to play again
     * ot not, print a win or loose message to screen, there is a win if there are no more bricks or it the
     * user pressed 'w', if there are no more lives there is a loose
     */
    private void checkForGameEnd() {
        boolean isWin = false;
        double ballHeight = ball.getCenter().y();
        String prompt = "";
        if (bricksCounter.value() == 0 || inputListener1.isKeyPressed(KeyEvent.VK_W)) {
            /*user wins*/
            prompt = WINNING_MASSAGE;
            isWin = true;
        }

        if (ballHeight > windowDimensions.y()) {
            /*user lost*/
            prompt = LOSING_MASSAGE;
            livesCounter1.decrement();
            ball.setCenter(windowDimensions.mult(0.5f));
        }
        if (!prompt.isEmpty()) {
            prompt += " Play again?";
            if (livesCounter1.value() == 0 || isWin) {
                if (windowController.openYesNoDialog(prompt)) {
                    windowController.resetGame();
                } else {
                    windowController.closeWindow();
                }
            }
        }
    }

    public static void main(String[] args) {
        new BrickerGameManager(WINDOW_NAME, new Vector2(WINDOW_WIDTH, WINDOW_HEIGHT)).run();
    }
}