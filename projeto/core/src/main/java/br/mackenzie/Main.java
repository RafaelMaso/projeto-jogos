package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {

    // === CONSTANTES DO JOGO ===

    // Cores e Visual
    private static final float BACKGROUND_CLEAR_RED = 0.2f;
    private static final float BACKGROUND_CLEAR_GREEN = 0.2f;
    private static final float BACKGROUND_CLEAR_BLUE = 0.8f;
    private static final float BACKGROUND_CLEAR_ALPHA = 1.0f;
    private static final Color FONT_SCORE_COLOR = Color.WHITE;
    private static final Color FONT_GAME_OVER_COLOR = Color.RED;
    private static final Color VICTORY_COLOR = Color.GREEN;
    private static final Color PAUSE_BG_COLOR = new Color(0f, 0f, 0f, 0.6f);
    private static final float ENTITY_STANDARD_WIDTH = 115f;
    private static final float ENTITY_STANDARD_HEIGHT = 125f;

    // Textos
    private static final String GAME_OVER_TEXT = "GAME OVER!";
    private static final String VICTORY_TEXT = "VOCÊ ESCAPOU!";
    private static final String PAUSE_TEXT = "JOGO PAUSADO";
    private static final String RESUME_TEXT = "Continuar (ESC)";
    private static final String RESTART_TEXT = "Reiniciar (R)";
    private static final String EXIT_TEXT = "Sair (S)";

    // Caminhos de Imagens
    private static final String START_SCREEN_IMAGE_PATH = "home-screen.png";
    private static final String[] BACKGROUND_PATHS = {
        "city1.png", "city2.png", "city3.png"
    };

    // Configurações do Jogo
    private static final float PLAYER_INITIAL_Y = 20f;
    private static final float POLICE_INITIAL_OFFSET_X = 300f;
    private static final float DEFAULT_ENTITY_Y_POSITION = 20f;
    private static final int SCORE_MANAGER_INITIAL_INTERVAL = 10;
    private static final int VICTORY_SCORE = 300;
    private static final float START_SCREEN_DISPLAY_TIME = 5f;

    // Configurações de Câmera
    private static final float CAMERA_SCROLL_THRESHOLD_LEFT_PERCENT = 0.2f;
    private static final float CAMERA_SCROLL_THRESHOLD_RIGHT_PERCENT = 0.8f;
    private static final float POLICE_MAX_DISTANCE_FROM_PLAYER_PERCENT = 0.75f;

    // Configurações de Fonte
    private static final float FONT_SCORE_SCALE = 2f;
    private static final float FONT_GAME_OVER_SCALE = 4f;

    // Sistema de Fases
    private static final float[] PHASE_SPEED_MULTIPLIERS = {1.0f, 1.2f, 1.38f};
    private static final int[] PHASE_SCORE_THRESHOLDS = {100, 250, 300};


    // === VARIÁVEIS DO JOGO ===

    // Estados do Jogo
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private boolean isVictory = false;
    private boolean showStartScreen = true;

    // Progressão
    private int currentPhase = 0;
    private float gameSpeedMultiplier = 1.0f;
    private float worldCameraX = 0;
    private float startScreenTimer;

    // Gráficos e Câmera
    private float scaledBackgroundWidth;
    private float scaledBackgroundHeight;
    private SpriteBatch spriteBatch;
    private Texture startScreenTexture;
    private Texture backgroundTexture;

    // Entidades
    private PlayerCharacter player;
    private Police police;

    // UI
    private ScoreManager scoreManager;
    private BitmapFont scoreFont, gameOverFont, victoryFont, pauseFont, menuFont;
    private GlyphLayout gameOverLayout, victoryLayout, pauseLayout, resumeLayout, restartLayout, exitLayout;


    // === MÉTODOS PRINCIPAIS ===

    @Override
    public void create() {
        initializeGraphics();
        initializeEntities();
        initializeUI();
        resetGameState();
    }

    @Override
    public void render() {
        ScreenUtils.clear(BACKGROUND_CLEAR_RED, BACKGROUND_CLEAR_GREEN, BACKGROUND_CLEAR_BLUE, BACKGROUND_CLEAR_ALPHA);
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (showStartScreen) {
            handleStartScreen();
            return;
        }

        if (handlePause()) return;

        if (!isGameOver) {
            updateGame(deltaTime);
        }

        renderGame();
    }

    @Override
    public void resize(int width, int height) {
        updateBackgroundSize();
        repositionEntities();
    }

    @Override
    public void dispose() {
        disposeResources();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}


    // === INICIALIZAÇÃO ===

    private void initializeGraphics() {
        spriteBatch = new SpriteBatch();
        startScreenTexture = new Texture(START_SCREEN_IMAGE_PATH);
        backgroundTexture = new Texture(BACKGROUND_PATHS[currentPhase]);
        gameSpeedMultiplier = PHASE_SPEED_MULTIPLIERS[currentPhase];
        updateBackgroundSize();
    }

    private void initializeEntities() {
        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, PLAYER_INITIAL_Y);
        police = new Police(player.getX() - POLICE_INITIAL_OFFSET_X, PLAYER_INITIAL_Y);
    }

    private void initializeUI() {
        scoreManager = new ScoreManager(SCORE_MANAGER_INITIAL_INTERVAL);
        initializeFonts();
    }

    private void initializeFonts() {
        // Score Font
        scoreFont = new BitmapFont();
        scoreFont.setColor(FONT_SCORE_COLOR);
        scoreFont.getData().setScale(FONT_SCORE_SCALE);

        // Game Over Font
        gameOverFont = new BitmapFont();
        gameOverFont.setColor(FONT_GAME_OVER_COLOR);
        gameOverFont.getData().setScale(FONT_GAME_OVER_SCALE);
        gameOverLayout = new GlyphLayout(gameOverFont, GAME_OVER_TEXT);

        // Victory Font
        victoryFont = new BitmapFont();
        victoryFont.setColor(VICTORY_COLOR);
        victoryFont.getData().setScale(4f);
        victoryLayout = new GlyphLayout(victoryFont, VICTORY_TEXT);

        // Pause Fonts
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

    private void resetGameState() {
        isGameOver = false;
        isVictory = false;
        isPaused = false;
        startScreenTimer = 0;
        worldCameraX = 0;
    }


    // === LÓGICA DO JOGO ===

    private void handleStartScreen() {
        startScreenTimer += Gdx.graphics.getDeltaTime();
        if (startScreenTimer >= START_SCREEN_DISPLAY_TIME || Gdx.input.isTouched()) {
            showStartScreen = false;
        }

        spriteBatch.begin();
        spriteBatch.draw(startScreenTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
    }

    private boolean handlePause() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (isPaused) {
            drawPauseScreen();
            checkPauseMenuInput();
            return true;
        }
        return false;
    }

    private void updateGame(float deltaTime) {
        if (isVictory) return;

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


    // === SISTEMA DE FASES ===

    private void checkPhaseTransition() {
        if (currentPhase < PHASE_SCORE_THRESHOLDS.length - 1 &&
            scoreManager.getScore() >= PHASE_SCORE_THRESHOLDS[currentPhase]) {
            currentPhase++;
            changePhase(currentPhase);
        }

        if (scoreManager.getScore() >= VICTORY_SCORE && !isVictory) {
            isVictory = true;
        }
    }

    private void changePhase(int newPhase) {
        currentPhase = newPhase;

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        backgroundTexture = new Texture(BACKGROUND_PATHS[currentPhase]);
        gameSpeedMultiplier = PHASE_SPEED_MULTIPLIERS[currentPhase];

        police.setPhase(currentPhase);

        updateBackgroundSize();

        worldCameraX = 0;
        player.setX(Gdx.graphics.getWidth() / 2f);
        police.setX(player.getX() - POLICE_INITIAL_OFFSET_X);
    }


    // === CÂMERA E MOVIMENTO ===

    private void updateCameraAndPlayerScreenPosition() {
        float playerScreenX = player.getX() - worldCameraX;
        float leftThreshold = Gdx.graphics.getWidth() * CAMERA_SCROLL_THRESHOLD_LEFT_PERCENT;
        float rightThreshold = Gdx.graphics.getWidth() * CAMERA_SCROLL_THRESHOLD_RIGHT_PERCENT - ENTITY_STANDARD_WIDTH; // Usa largura padrão

        if (playerScreenX >= rightThreshold) {
            worldCameraX = player.getX() - rightThreshold;
        } else if (playerScreenX <= leftThreshold) {
            worldCameraX = player.getX() - leftThreshold;
        }

        if (worldCameraX < 0) {
            worldCameraX = 0;
            if (player.getX() < 0) player.setX(0);
        }

        float clampedScreenX = player.getX() - worldCameraX;
        clampedScreenX = Math.max(0, Math.min(Gdx.graphics.getWidth() - ENTITY_STANDARD_WIDTH, clampedScreenX)); // Usa largura padrão
        player.setCurrentScreenX(clampedScreenX);
    }

    private void updatePolicePositionRelativePlayer() {
        float maxDistance = Gdx.graphics.getWidth() * POLICE_MAX_DISTANCE_FROM_PLAYER_PERCENT;
        float phaseDifficulty = 1.0f - (currentPhase * 0.2f);
        float adjustedDistance = maxDistance * Math.max(0.4f, phaseDifficulty);

        if (police.getX() < player.getX() - adjustedDistance) {
            police.setX(player.getX() - adjustedDistance);
        }

        police.setSpeedMultiplier(gameSpeedMultiplier);
    }

    private void updateBackgroundSize() {
        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;
    }

    private void repositionEntities() {
        if (player != null) player.setY(DEFAULT_ENTITY_Y_POSITION);
        if (police != null) police.setY(DEFAULT_ENTITY_Y_POSITION);
    }


    // === COLISÕES ===

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        Rectangle policeBounds = police.getBounds();

        if (playerBounds.overlaps(policeBounds)) {
            isGameOver = true;
        }
    }


    // === RENDERIZAÇÃO ===

    private void drawBackground() {
        float backgroundOffset = -(worldCameraX % scaledBackgroundWidth);
        if (backgroundOffset > 0) backgroundOffset -= scaledBackgroundWidth;

        spriteBatch.draw(backgroundTexture, backgroundOffset, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundOffset + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundOffset + scaledBackgroundWidth * 2, 0, scaledBackgroundWidth, scaledBackgroundHeight);
    }

    private void drawGameElements(SpriteBatch spriteBatch) {
        if (!isGameOver && !isVictory) {
            player.render(spriteBatch);
            police.render(spriteBatch, -worldCameraX);
        }
    }

    private void drawUI(SpriteBatch spriteBatch) {
        if (isVictory) {
            drawVictoryScreen();
        } else if (isGameOver) {
            drawGameOverScreen();
        } else {
            drawGameUI();
        }
    }

    private void drawVictoryScreen() {
        float victoryX = (Gdx.graphics.getWidth() - victoryLayout.width) / 2;
        float victoryY = (Gdx.graphics.getHeight() + victoryLayout.height) / 2;
        victoryFont.draw(spriteBatch, victoryLayout, victoryX, victoryY);
    }

    private void drawGameOverScreen() {
        float x = (Gdx.graphics.getWidth() - gameOverLayout.width) / 2;
        float y = (Gdx.graphics.getHeight() + gameOverLayout.height) / 2;
        gameOverFont.draw(spriteBatch, gameOverLayout, x, y);
    }

    private void drawGameUI() {
        scoreFont.draw(spriteBatch, "Score: " + scoreManager.getScore(), 10, Gdx.graphics.getHeight() - 10);
        scoreFont.draw(spriteBatch, "Fase: " + (currentPhase + 1), 10, Gdx.graphics.getHeight() - 50);
    }


    // === PAUSE MENU ===

    private void drawPauseScreen() {
        spriteBatch.begin();
        drawBackground();
        drawGameElements(spriteBatch);
        drawUI(spriteBatch);

        spriteBatch.setColor(PAUSE_BG_COLOR);
        spriteBatch.draw(createWhitePixelTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.setColor(Color.WHITE);

        drawPauseMenuText();
        spriteBatch.end();
    }

    private void drawPauseMenuText() {
        float pauseX = (Gdx.graphics.getWidth() - pauseLayout.width) / 2;
        float pauseY = Gdx.graphics.getHeight() * 0.7f;
        pauseFont.draw(spriteBatch, pauseLayout, pauseX, pauseY);

        drawCenteredText(resumeLayout, 0.55f);
        drawCenteredText(restartLayout, 0.45f);
        drawCenteredText(exitLayout, 0.35f);
    }

    private void drawCenteredText(GlyphLayout layout, float heightPercent) {
        float x = (Gdx.graphics.getWidth() - layout.width) / 2;
        float y = Gdx.graphics.getHeight() * heightPercent;
        menuFont.draw(spriteBatch, layout, x, y);
    }

    private void checkPauseMenuInput() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            restartGame();
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            exitGame();
        }

        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            handlePauseMenuClick();
        }
    }

    private void handlePauseMenuClick() {
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float menuHeight = 30;

        if (isClickInArea(touchY, 0.55f, menuHeight)) {
            isPaused = false;
        } else if (isClickInArea(touchY, 0.45f, menuHeight)) {
            restartGame();
        } else if (isClickInArea(touchY, 0.35f, menuHeight)) {
            exitGame();
        }
    }

    private boolean isClickInArea(float touchY, float areaPercent, float tolerance) {
        float areaY = Gdx.graphics.getHeight() * areaPercent;
        return touchY >= areaY - tolerance && touchY <= areaY + tolerance;
    }


    // === CONTROLES DO JOGO ===

    private void restartGame() {
        resetGameState();
        currentPhase = 0;
        gameSpeedMultiplier = PHASE_SPEED_MULTIPLIERS[0];

        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, PLAYER_INITIAL_Y);
        police = new Police(player.getX() - POLICE_INITIAL_OFFSET_X, PLAYER_INITIAL_Y);
        scoreManager = new ScoreManager(SCORE_MANAGER_INITIAL_INTERVAL);

        if (backgroundTexture != null) backgroundTexture.dispose();
        backgroundTexture = new Texture(BACKGROUND_PATHS[0]);
        updateBackgroundSize();
    }

    private void exitGame() {
        Gdx.app.exit();
    }


    // === UTILITÁRIOS ===

    private Texture createWhitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void disposeResources() {
        spriteBatch.dispose();
        if (startScreenTexture != null) startScreenTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (player != null) player.dispose();
        if (police != null) police.dispose();
        scoreFont.dispose();
        gameOverFont.dispose();
        victoryFont.dispose();
        pauseFont.dispose();
        menuFont.dispose();
    }
}
