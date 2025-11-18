package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Pixmap;

public class Main implements ApplicationListener {
    // --- Constantes do Jogo ---
    private static final float BACKGROUND_CLEAR_RED = 0.2f;
    private static final float BACKGROUND_CLEAR_GREEN = 0.2f;
    private static final float BACKGROUND_CLEAR_BLUE = 0.8f;
    private static final float BACKGROUND_CLEAR_ALPHA = 1.0f;
    private static final float PLAYER_INITIAL_Y = 20f;
    private static final float POLICE_INITIAL_OFFSET_X = 300f;
    private static final int SCORE_MANAGER_INITIAL_INTERVAL = 10;
    private static final float FONT_SCORE_SCALE = 2f;
    private static final float FONT_GAME_OVER_SCALE = 4f;
    private static final Color FONT_SCORE_COLOR = Color.WHITE;
    private static final Color FONT_GAME_OVER_COLOR = Color.RED;
    private static final float CAMERA_SCROLL_THRESHOLD_LEFT_PERCENT = 0.2f;
    private static final float CAMERA_SCROLL_THRESHOLD_RIGHT_PERCENT = 0.8f;
    private static final float POLICE_MAX_DISTANCE_FROM_PLAYER_PERCENT = 0.75f;
    private static final String GAME_OVER_TEXT = "GAME OVER!";
    private static final float DEFAULT_ENTITY_Y_POSITION = 20f;
    private static final String START_SCREEN_IMAGE_PATH = "home-screen.png";
    private static final float START_SCREEN_DISPLAY_TIME = 5f;
    private static final String PAUSE_TEXT = "JOGO PAUSADO";
    private static final String RESUME_TEXT = "Continuar (ESC)";
    private static final String RESTART_TEXT = "Reiniciar (R)";
    private static final String EXIT_TEXT = "Sair (S)";
    private static final Color PAUSE_BG_COLOR = new Color(0f, 0f, 0f, 0.6f);

    // --- Sistema de Fases ---
    private static final String[] BACKGROUND_PATHS = {
        "city1.png",     // Fase 1
        "city2.png",     // Fase 2
        "city3.png"      // Fase 3
    };
    private static final float[] PHASE_SPEED_MULTIPLIERS = {1.0f, 1.2f, 1.4f};
    private static final int[] PHASE_SCORE_THRESHOLDS = {100, 300, 600};

    // --- Variáveis do Jogo ---
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean showStartScreen = true;
    private int currentPhase = 0;
    private float gameSpeedMultiplier = 1.0f;
    private float worldCameraX = 0;
    private float scaledBackgroundWidth;
    private float scaledBackgroundHeight;
    private float startScreenTimer;

    // --- Recursos Gráficos ---
    private SpriteBatch spriteBatch;
    private Texture startScreenTexture;
    private Texture backgroundTexture;
    private PlayerCharacter player;
    private Police police;

    // --- UI ---
    private ScoreManager scoreManager;
    private BitmapFont scoreFont;
    private BitmapFont gameOverFont;
    private BitmapFont pauseFont;
    private BitmapFont menuFont;
    private GlyphLayout gameOverLayout;
    private GlyphLayout pauseLayout;
    private GlyphLayout resumeLayout;
    private GlyphLayout restartLayout;
    private GlyphLayout exitLayout;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        // Carrega telas
        startScreenTexture = new Texture(START_SCREEN_IMAGE_PATH);
        backgroundTexture = new Texture(BACKGROUND_PATHS[currentPhase]);
        gameSpeedMultiplier = PHASE_SPEED_MULTIPLIERS[currentPhase];

        // Calcula tamanho do background
        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        // Inicializa personagens
        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, PLAYER_INITIAL_Y);
        police = new Police(player.getX() - POLICE_INITIAL_OFFSET_X, PLAYER_INITIAL_Y);

        // Inicializa sistemas
        scoreManager = new ScoreManager(SCORE_MANAGER_INITIAL_INTERVAL);
        initializeFonts();

        isGameOver = false;
        startScreenTimer = 0;
    }

    private void initializeFonts() {
        scoreFont = new BitmapFont();
        scoreFont.setColor(FONT_SCORE_COLOR);
        scoreFont.getData().setScale(FONT_SCORE_SCALE);

        gameOverFont = new BitmapFont();
        gameOverFont.setColor(FONT_GAME_OVER_COLOR);
        gameOverFont.getData().setScale(FONT_GAME_OVER_SCALE);
        gameOverLayout = new GlyphLayout(gameOverFont, GAME_OVER_TEXT);

        pauseFont = new BitmapFont();
        pauseFont.setColor(Color.YELLOW);
        pauseFont.getData().setScale(4f);

        menuFont = new BitmapFont();
        menuFont.setColor(Color.WHITE);
        menuFont.getData().setScale(2f);

        pauseLayout = new GlyphLayout(pauseFont, PAUSE_TEXT);
        resumeLayout = new GlyphLayout(menuFont, RESUME_TEXT);
        restartLayout = new GlyphLayout(menuFont, RESTART_TEXT);
        exitLayout = new GlyphLayout(menuFont, EXIT_TEXT);
    }

    @Override
    public void resize(int width, int height) {
        scaledBackgroundHeight = height;
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        if (player != null) player.setY(DEFAULT_ENTITY_Y_POSITION);
        if (police != null) police.setY(DEFAULT_ENTITY_Y_POSITION);
    }

    @Override
    public void render() {
        ScreenUtils.clear(BACKGROUND_CLEAR_RED, BACKGROUND_CLEAR_GREEN, BACKGROUND_CLEAR_BLUE, BACKGROUND_CLEAR_ALPHA);

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Tela inicial
        if (showStartScreen) {
            handleStartScreen();
            return;
        }

        // Pause
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (isPaused) {
            drawPauseScreen();
            checkPauseMenuInput();
            return;
        }

        // Jogo principal
        if (!isGameOver) {
            updateGame(deltaTime);
        }

        renderGame();
    }

    private void handleStartScreen() {
        startScreenTimer += Gdx.graphics.getDeltaTime();
        if (startScreenTimer >= START_SCREEN_DISPLAY_TIME || Gdx.input.isTouched()) {
            showStartScreen = false;
        }

        spriteBatch.begin();
        spriteBatch.draw(startScreenTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
    }

    private void updateGame(float deltaTime) {
        player.update(deltaTime);
        police.update(deltaTime);
        scoreManager.update(deltaTime);

        updateCameraAndPlayerScreenPosition();
        updatePolicePositionRelativePlayer();
        checkCollisions();
        checkPhaseTransition();
    }

    private void renderGame() {
        spriteBatch.begin();
        drawBackground();
        drawGameElements(spriteBatch);
        drawUI(spriteBatch);
        spriteBatch.end();
    }

    private void checkPhaseTransition() {
        if (currentPhase < PHASE_SCORE_THRESHOLDS.length - 1 &&
            scoreManager.getScore() >= PHASE_SCORE_THRESHOLDS[currentPhase]) {
            currentPhase++;
            changePhase(currentPhase);
        }
    }

    private void changePhase(int newPhase) {
        currentPhase = newPhase;

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        backgroundTexture = new Texture(BACKGROUND_PATHS[currentPhase]);
        gameSpeedMultiplier = PHASE_SPEED_MULTIPLIERS[currentPhase];

        // Recalcula tamanho do background
        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        worldCameraX = 0;
        player.setX(Gdx.graphics.getWidth() / 2f);
        police.setX(player.getX() - POLICE_INITIAL_OFFSET_X);
    }

    private void drawPauseScreen() {
        spriteBatch.begin();
        drawBackground();
        drawGameElements(spriteBatch);
        drawUI(spriteBatch);

        // Overlay de pause
        spriteBatch.setColor(PAUSE_BG_COLOR);
        spriteBatch.draw(whitePixelTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.setColor(Color.WHITE);

        // Textos do menu de pause
        float pauseX = (Gdx.graphics.getWidth() - pauseLayout.width) / 2;
        float pauseY = Gdx.graphics.getHeight() * 0.7f;
        pauseFont.draw(spriteBatch, pauseLayout, pauseX, pauseY);

        float resumeX = (Gdx.graphics.getWidth() - resumeLayout.width) / 2;
        float resumeY = Gdx.graphics.getHeight() * 0.55f;
        menuFont.draw(spriteBatch, resumeLayout, resumeX, resumeY);

        float restartX = (Gdx.graphics.getWidth() - restartLayout.width) / 2;
        float restartY = Gdx.graphics.getHeight() * 0.45f;
        menuFont.draw(spriteBatch, restartLayout, restartX, restartY);

        float exitX = (Gdx.graphics.getWidth() - exitLayout.width) / 2;
        float exitY = Gdx.graphics.getHeight() * 0.35f;
        menuFont.draw(spriteBatch, exitLayout, exitX, exitY);

        spriteBatch.end();
    }

    private Texture whitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void checkPauseMenuInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            restartGame();
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            exitGame();
        }

        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            float menuHeight = 30;

            float resumeY = Gdx.graphics.getHeight() * 0.55f;
            float restartY = Gdx.graphics.getHeight() * 0.45f;
            float exitY = Gdx.graphics.getHeight() * 0.35f;

            if (touchY >= resumeY - menuHeight && touchY <= resumeY + menuHeight) {
                isPaused = false;
            } else if (touchY >= restartY - menuHeight && touchY <= restartY + menuHeight) {
                restartGame();
            } else if (touchY >= exitY - menuHeight && touchY <= exitY + menuHeight) {
                exitGame();
            }
        }
    }

    private void exitGame() {
        Gdx.app.exit();
    }

    private void restartGame() {
        isGameOver = false;
        isPaused = false;
        worldCameraX = 0;
        currentPhase = 0;
        gameSpeedMultiplier = PHASE_SPEED_MULTIPLIERS[0];

        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, PLAYER_INITIAL_Y);
        police = new Police(player.getX() - POLICE_INITIAL_OFFSET_X, PLAYER_INITIAL_Y);

        scoreManager = new ScoreManager(SCORE_MANAGER_INITIAL_INTERVAL);

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        backgroundTexture = new Texture(BACKGROUND_PATHS[0]);
    }

    private void updateCameraAndPlayerScreenPosition() {
        float playerScreenXRelativeToCamera = player.getX() - worldCameraX;

        float leftScrollThreshold = Gdx.graphics.getWidth() * CAMERA_SCROLL_THRESHOLD_LEFT_PERCENT;
        float rightScrollThreshold = Gdx.graphics.getWidth() * CAMERA_SCROLL_THRESHOLD_RIGHT_PERCENT - player.getWidth();

        if (playerScreenXRelativeToCamera >= rightScrollThreshold) {
            worldCameraX = player.getX() - rightScrollThreshold;
        } else if (playerScreenXRelativeToCamera <= leftScrollThreshold) {
            worldCameraX = player.getX() - leftScrollThreshold;
        }

        if (worldCameraX < 0) {
            worldCameraX = 0;
            if (player.getX() < 0) {
                player.setX(0);
            }
        }

        float clampedPlayerScreenX = player.getX() - worldCameraX;
        clampedPlayerScreenX = Math.max(0, clampedPlayerScreenX);
        clampedPlayerScreenX = Math.min(Gdx.graphics.getWidth() - player.getWidth(), clampedPlayerScreenX);
        player.setCurrentScreenX(clampedPlayerScreenX);
    }

    private void updatePolicePositionRelativePlayer() {
        float maxDistanceFromPlayer = Gdx.graphics.getWidth() * POLICE_MAX_DISTANCE_FROM_PLAYER_PERCENT;
        float phaseDifficulty = 1.0f - (currentPhase * 0.2f);
        float adjustedMaxDistance = maxDistanceFromPlayer * Math.max(0.4f, phaseDifficulty);

        if (police.getX() < player.getX() - adjustedMaxDistance) {
            police.setX(player.getX() - adjustedMaxDistance);
        }

        police.setSpeedMultiplier(gameSpeedMultiplier);
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        Rectangle policeBounds = police.getBounds();

        if (playerBounds.overlaps(policeBounds)) {
            isGameOver = true;
        }
    }

    private void drawBackground() {
        float backgroundDrawOffset = -(worldCameraX % scaledBackgroundWidth);
        if (backgroundDrawOffset > 0) {
            backgroundDrawOffset -= scaledBackgroundWidth;
        }

        spriteBatch.draw(backgroundTexture, backgroundDrawOffset, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth * 2, 0, scaledBackgroundWidth, scaledBackgroundHeight);
    }

    private void drawGameElements(SpriteBatch spriteBatch) {
        if (!isGameOver) {
            player.render(spriteBatch);
            police.render(spriteBatch, -worldCameraX);
        }
    }

    private void drawUI(SpriteBatch spriteBatch) {
        if (isGameOver) {
            float x = (Gdx.graphics.getWidth() - gameOverLayout.width) / 2;
            float y = (Gdx.graphics.getHeight() + gameOverLayout.height) / 2;
            gameOverFont.draw(spriteBatch, gameOverLayout, x, y);
        } else {
            scoreFont.draw(spriteBatch, "Score: " + scoreManager.getScore(), 10, Gdx.graphics.getHeight() - 10);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        spriteBatch.dispose();
        if (startScreenTexture != null) startScreenTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (player != null) player.dispose();
        if (police != null) police.dispose();
        scoreFont.dispose();
        gameOverFont.dispose();
        pauseFont.dispose();
        menuFont.dispose();
    }
}
