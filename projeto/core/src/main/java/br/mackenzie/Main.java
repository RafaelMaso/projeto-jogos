package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    PlayerCharacter player;
    Police police;
    Texture backgroundTexture;

    float worldCameraX = 0;
    float scaledBackgroundWidth;
    float scaledBackgroundHeight;

    ScoreManager scoreManager;
    BitmapFont font;

    /**
     * Chamado quando a aplicação é criada. Inicializa recursos e objetos do jogo.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        backgroundTexture = new Texture("city1.png");

        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        player = new PlayerCharacter(Gdx.graphics.getWidth() / 4f, 20); 

        police = new Police(player.getX() - 200f, 20);
        police.setHeight(player.getHeight() + 50); 
        
        scoreManager = new ScoreManager(10);
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);
    }

    /**
     * Chamado quando a tela é redimensionada.
     * @param width A nova largura da tela.
     * @param height A nova altura da tela.
     */
    @Override
    public void resize(int width, int height) {
        scaledBackgroundHeight = height;
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        if (player != null) {
            player.setY(20); 
        }
        if (police != null) {
            police.setY(20); 
            police.setHeight(player.getHeight() + 50); 
        }
    }

    /**
     * Chamado a cada frame para atualizar a lógica do jogo e renderizar os elementos.
     */
    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime();

        player.update(deltaTime); 
        // Velocidade do policial
        police.setSpeed(210f); 
        police.update(deltaTime);
        scoreManager.update(deltaTime); 

        float playerScreenXRelativeToCamera = player.getX() - worldCameraX; 

        float leftScrollThreshold = Gdx.graphics.getWidth() * 0.2f;
        float rightScrollThreshold = Gdx.graphics.getWidth() * 0.8f - player.getWidth();

        if (playerScreenXRelativeToCamera >= rightScrollThreshold) {
            worldCameraX = player.getX() - rightScrollThreshold;
        } 

        else if (playerScreenXRelativeToCamera <= leftScrollThreshold) {
            worldCameraX = player.getX() - leftScrollThreshold;
        }

        if (worldCameraX < 0) {
            worldCameraX = 0;
            if (player.getX() < 0) {
                player.setX(0);
            }
        }

        float clampedPlayerScreenX = player.getX() - worldCameraX; 
        if (clampedPlayerScreenX < 0) clampedPlayerScreenX = 0; 
        if (clampedPlayerScreenX > Gdx.graphics.getWidth() - player.getWidth()) clampedPlayerScreenX = Gdx.graphics.getWidth() - player.getWidth(); // Evita sair da tela totalmente à direita
        player.setCurrentScreenX(clampedPlayerScreenX);

        float backgroundDrawOffset = -(worldCameraX % scaledBackgroundWidth);
        if (backgroundDrawOffset > 0) { 
            backgroundDrawOffset -= scaledBackgroundWidth;
        }

        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, backgroundDrawOffset, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth * 2, 0, scaledBackgroundWidth, scaledBackgroundHeight); 

        player.render(spriteBatch);
        police.render(spriteBatch, -worldCameraX); 

        font.draw(spriteBatch, "Score: " + scoreManager.getScore(), 10, Gdx.graphics.getHeight() - 10);

        spriteBatch.end();
    }

    /**
     * Chamado quando a aplicação é pausada.
     */
    @Override
    public void pause() {
    }

    /**
     * Chamado quando a aplicação é resumida após uma pausa.
     */
    @Override
    public void resume() {
    }

    /**
     * Chamado quando a aplicação está sendo encerrada. Libera todos os recursos.
     */
    @Override
    public void dispose() {
        spriteBatch.dispose();
        backgroundTexture.dispose();
        player.dispose();
        police.dispose();
        font.dispose();
    }
}