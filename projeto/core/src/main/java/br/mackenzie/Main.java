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
import com.badlogic.gdx.Input;

public class Main implements ApplicationListener {
    // --- Constantes do Jogo ---
    private static final float BACKGROUND_CLEAR_RED = 0.2f;
    private static final float BACKGROUND_CLEAR_GREEN = 0.2f;
    private static final float BACKGROUND_CLEAR_BLUE = 0.8f;
    private static final float BACKGROUND_CLEAR_ALPHA = 1.0f;
    private static final String BACKGROUND_IMAGE_PATH = "city1.png";
    private static final float PLAYER_INITIAL_Y = 20f;
    private static final float POLICE_INITIAL_OFFSET_X = 300f;
    private static final float POLICE_HEIGHT_OFFSET = 50f;
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
    private static final Color PAUSE_BG_COLOR = new Color(0f, 0f, 0f, 0.6f); // Fundo semi-transparente
    private boolean isPaused = false;
    private BitmapFont pauseFont;
    private BitmapFont menuFont;
    private GlyphLayout pauseLayout;
    private GlyphLayout resumeLayout;
    private GlyphLayout restartLayout;
    private GlyphLayout exitLayout;

    // --- Home Screen ---
    private Texture startScreenTexture;
    private float startScreenTimer;
    private boolean showStartScreen = true;

    // --- Recursos Gráficos e Entidades ---
    private SpriteBatch spriteBatch;
    private PlayerCharacter player;
    private Police police;
    private Texture backgroundTexture;

    // --- Gerenciamento de Câmera e Plano de Fundo ---
    private float worldCameraX = 0;
    private float scaledBackgroundWidth;
    private float scaledBackgroundHeight;

    // --- Gerenciamento de Pontuação e UI ---
    private ScoreManager scoreManager;
    private BitmapFont scoreFont;
    private BitmapFont gameOverFont;
    private GlyphLayout gameOverLayout;
    private boolean isGameOver;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        // Carrega a tela inicial
        startScreenTexture = new Texture(START_SCREEN_IMAGE_PATH);
        startScreenTimer = 0;
        showStartScreen = true;

        backgroundTexture = new Texture(BACKGROUND_IMAGE_PATH);

        // Calcula a largura do fundo para manter a proporção da imagem original
        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        // Inicializa o jogador e a polícia
        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, PLAYER_INITIAL_Y);
        police = new Police(player.getX() - POLICE_INITIAL_OFFSET_X, PLAYER_INITIAL_Y);
        police.setHeight(player.getHeight() + POLICE_HEIGHT_OFFSET);

        // Inicializa o gerenciador de pontuação e fontes
        scoreManager = new ScoreManager(SCORE_MANAGER_INITIAL_INTERVAL);
        scoreFont = new BitmapFont();
        scoreFont.setColor(FONT_SCORE_COLOR);
        scoreFont.getData().setScale(FONT_SCORE_SCALE);

        gameOverFont = new BitmapFont();
        gameOverFont.setColor(FONT_GAME_OVER_COLOR);
        gameOverFont.getData().setScale(FONT_GAME_OVER_SCALE);
        gameOverLayout = new GlyphLayout(gameOverFont, GAME_OVER_TEXT);

        // Inicializa fontes do pause
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

        isGameOver = false;
    }

    @Override
    public void resize(int width, int height) {
        // Recalcula o tamanho do fundo e reposiciona entidades ao redimensionar a janela
        scaledBackgroundHeight = height;
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        // Ajusta a posição Y de entidades, se existirem
        if (player != null) {
            player.setY(DEFAULT_ENTITY_Y_POSITION);
        }
        if (police != null) {
            police.setY(DEFAULT_ENTITY_Y_POSITION);
            // Garante que a polícia mantenha o tamanho em relação ao jogador
            if (player != null) {
                police.setHeight(player.getHeight() + POLICE_HEIGHT_OFFSET);
            }
        }
    }

    @Override
    public void render() {
        // Limpa a tela com uma cor de fundo
        ScreenUtils.clear(BACKGROUND_CLEAR_RED, BACKGROUND_CLEAR_GREEN, BACKGROUND_CLEAR_BLUE, BACKGROUND_CLEAR_ALPHA);

        float deltaTime = Gdx.graphics.getDeltaTime();

        // === LÓGICA DA TELA INICIAL ===
        if (showStartScreen) {
            startScreenTimer += deltaTime;

            if (startScreenTimer >= START_SCREEN_DISPLAY_TIME || Gdx.input.isTouched()) {
                showStartScreen = false;
            }

            spriteBatch.begin();
            spriteBatch.draw(startScreenTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
            return;
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            isPaused = !isPaused; // Alterna entre pausado e não pausado
        }

        // === SE O JOGO ESTIVER PAUSADO ===
        if (isPaused) {
            drawPauseScreen();

            // Verifica cliques nos botões do menu de pause
            checkPauseMenuInput();
            return; // Sai do render sem atualizar o jogo
        }

        // === SE O JOGO NÃO ESTIVER PAUSADO (SEU CÓDIGO ORIGINAL) ===
        if (!isGameOver) {
            player.update(deltaTime);
            police.update(deltaTime);
            scoreManager.update(deltaTime);

            updateCameraAndPlayerScreenPosition();
            updatePolicePositionRelativePlayer();
            checkCollisions();
        }

        // Renderiza os elementos do jogo
        spriteBatch.begin();
        drawBackground();
        drawGameElements(spriteBatch);
        drawUI(spriteBatch);
        spriteBatch.end();
    }

    /**
     * Desenha a tela de pause com opções de menu
     */
    private void drawPauseScreen() {
        // Primeiro desenha o jogo congelado
        spriteBatch.begin();
        drawBackground();
        drawGameElements(spriteBatch);
        drawUI(spriteBatch);

        // Desenha overlay escuro semi-transparente
        spriteBatch.setColor(PAUSE_BG_COLOR);
        spriteBatch.draw(whitePixelTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.setColor(Color.WHITE);

        // Desenha texto "JOGO PAUSADO"
        float pauseX = (Gdx.graphics.getWidth() - pauseLayout.width) / 2;
        float pauseY = Gdx.graphics.getHeight() * 0.7f;
        pauseFont.draw(spriteBatch, pauseLayout, pauseX, pauseY);

        // Desenha opções do menu
        float resumeX = (Gdx.graphics.getWidth() - resumeLayout.width) / 2;
        float resumeY = Gdx.graphics.getHeight() * 0.55f;  // Ajustei as posições
        menuFont.draw(spriteBatch, resumeLayout, resumeX, resumeY);

        float restartX = (Gdx.graphics.getWidth() - restartLayout.width) / 2;
        float restartY = Gdx.graphics.getHeight() * 0.45f; // Ajustei as posições
        menuFont.draw(spriteBatch, restartLayout, restartX, restartY);

        // NOVA: Opção Sair do Jogo
        float exitX = (Gdx.graphics.getWidth() - exitLayout.width) / 2;
        float exitY = Gdx.graphics.getHeight() * 0.35f;    // Nova posição
        menuFont.draw(spriteBatch, exitLayout, exitX, exitY);

        spriteBatch.end();
    }

    // Cria uma textura branca 1x1 para o overlay
    private Texture whitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void checkPauseMenuInput() {
        // Tecla R - Reiniciar jogo
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.R)) {
            restartGame();
        }

        // Tecla S - Sair do jogo (QUANDO ESTIVER PAUSADO)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.S)) {
            exitGame();
        }

        // Clique do mouse nas opções
        if (Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverte Y

            float resumeY = Gdx.graphics.getHeight() * 0.55f;   // Ajustado
            float restartY = Gdx.graphics.getHeight() * 0.45f;  // Ajustado
            float exitY = Gdx.graphics.getHeight() * 0.35f;     // Ajustado
            float menuHeight = 30; // Altura aproximada do texto

            // Verifica clique em "Continuar"
            if (touchY >= resumeY - menuHeight && touchY <= resumeY + menuHeight) {
                isPaused = false;
            }

            // Verifica clique em "Reiniciar"
            if (touchY >= restartY - menuHeight && touchY <= restartY + menuHeight) {
                restartGame();
            }

            // NOVA: Verifica clique em "Sair do Jogo"
            if (touchY >= exitY - menuHeight && touchY <= exitY + menuHeight) {
                exitGame();
            }
        }
    }

    /**
     * Sai do jogo
     */
    private void exitGame() {
        Gdx.app.exit(); // Fecha a aplicação
    }

    /**
     * Reinicia o jogo para o estado inicial
     */
    private void restartGame() {
        // Reseta variáveis do jogo
        isGameOver = false;
        isPaused = false;
        worldCameraX = 0;

        // Recria os personagens
        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, PLAYER_INITIAL_Y);
        police = new Police(player.getX() - POLICE_INITIAL_OFFSET_X, PLAYER_INITIAL_Y);
        police.setHeight(player.getHeight() + POLICE_HEIGHT_OFFSET);

        // Reseta a pontuação
        scoreManager = new ScoreManager(SCORE_MANAGER_INITIAL_INTERVAL);
    }

    /**
     * Atualiza a posição da câmera do mundo e a posição de tela do jogador
     * para criar um efeito de rolagem da câmera.
     */
    private void updateCameraAndPlayerScreenPosition() {
        float playerScreenXRelativeToCamera = player.getX() - worldCameraX;

        float leftScrollThreshold = Gdx.graphics.getWidth() * CAMERA_SCROLL_THRESHOLD_LEFT_PERCENT;
        float rightScrollThreshold = Gdx.graphics.getWidth() * CAMERA_SCROLL_THRESHOLD_RIGHT_PERCENT - player.getWidth();

        // Lógica de rolagem da câmera baseada na posição do jogador
        if (playerScreenXRelativeToCamera >= rightScrollThreshold) {
            worldCameraX = player.getX() - rightScrollThreshold;
        } else if (playerScreenXRelativeToCamera <= leftScrollThreshold) {
            worldCameraX = player.getX() - leftScrollThreshold;
        }

        // Limita a câmera para não ir além do início do mundo
        if (worldCameraX < 0) {
            worldCameraX = 0;
            if (player.getX() < 0) {
                player.setX(0);
            }
        }

        // Calcula e define a posição de tela do jogador (para renderização)
        float clampedPlayerScreenX = player.getX() - worldCameraX;
        clampedPlayerScreenX = Math.max(0, clampedPlayerScreenX);
        clampedPlayerScreenX = Math.min(Gdx.graphics.getWidth() - player.getWidth(), clampedPlayerScreenX);
        player.setCurrentScreenX(clampedPlayerScreenX);
    }

    /**
     * Ajusta a posição da polícia para que ela não fique muito atrás do jogador.
     */
    private void updatePolicePositionRelativePlayer() {
        float maxDistanceFromPlayer = Gdx.graphics.getWidth() * POLICE_MAX_DISTANCE_FROM_PLAYER_PERCENT;
        if (police.getX() < player.getX() - maxDistanceFromPlayer) {
            police.setX(player.getX() - maxDistanceFromPlayer);
        }
    }

    /**
     * Verifica colisões entre o jogador e a polícia.
     */
    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        Rectangle policeBounds = police.getBounds();

        if (playerBounds.overlaps(policeBounds)) {
            isGameOver = true;
        }
    }

    /**
     * Desenha o plano de fundo com rolagem paralax.
     */
    private void drawBackground() {
        float backgroundDrawOffset = -(worldCameraX % scaledBackgroundWidth);
        if (backgroundDrawOffset > 0) {
            backgroundDrawOffset -= scaledBackgroundWidth;
        }

        spriteBatch.draw(backgroundTexture, backgroundDrawOffset, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth * 2, 0, scaledBackgroundWidth, scaledBackgroundHeight);
    }

    /**
     * Desenha os personagens do jogo.
     * @param spriteBatch O SpriteBatch para desenhar.
     */
    private void drawGameElements(SpriteBatch spriteBatch) {
        if (!isGameOver) {
            player.render(spriteBatch);
            police.render(spriteBatch, -worldCameraX);
        }
    }

    /**
     * Desenha a interface do usuário (pontuação ou tela de Game Over).
     * @param spriteBatch O SpriteBatch para desenhar.
     */
    private void drawUI(SpriteBatch spriteBatch) {
        if (isGameOver) {
            // Centraliza o texto de Game Over
            float x = (Gdx.graphics.getWidth() - gameOverLayout.width) / 2;
            float y = (Gdx.graphics.getHeight() + gameOverLayout.height) / 2;
            gameOverFont.draw(spriteBatch, gameOverLayout, x, y);
        } else {
            // Desenha a pontuação no canto superior esquerdo
            scoreFont.draw(spriteBatch, "Score: " + scoreManager.getScore(), 10, Gdx.graphics.getHeight() - 10);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();

        if (startScreenTexture != null) {
            startScreenTexture.dispose();
        }

        backgroundTexture.dispose();
        player.dispose();
        police.dispose();
        scoreFont.dispose();
        gameOverFont.dispose();

        // Novas fontes do pause
        pauseFont.dispose();
        menuFont.dispose();
    }
}
