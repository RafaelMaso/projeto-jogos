package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    PlayerCharacter player;
    Texture backgroundTexture;

    float backgroundScrollX = 0; 

    float scaledBackgroundWidth;
    float scaledBackgroundHeight;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture("city1.png");

        // Calcula as dimensões do background
        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        player = new PlayerCharacter(Gdx.graphics.getWidth() / 4f, 20); // Inicia o player mais para a esquerda
    }

    @Override
    public void resize(int width, int height) {
        // Quando a tela é redimensionada, recalculamos as dimensões do background
        scaledBackgroundHeight = height;
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        if (player != null) {
            player.setY(20); 
            if (player.getX() > width - player.getWidth()) {
                player.setX(width - player.getWidth());
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime();

        player.update(deltaTime); 

        float playerRequestedDeltaX = player.getDeltaXThisFrame();

        float playerScreenMoveX = playerRequestedDeltaX; 
        float backgroundScrollDelta = 0; 

        float screenWidth = Gdx.graphics.getWidth();
        float playerXOnScreen = player.getX();

        float rightScrollThreshold = screenWidth * 0.8f - player.getWidth(); 

        if (playerRequestedDeltaX > 0) { 
            if (playerXOnScreen >= rightScrollThreshold) {
                playerScreenMoveX = 0; 
                backgroundScrollDelta = -playerRequestedDeltaX; 
            } else if (playerXOnScreen + playerRequestedDeltaX > rightScrollThreshold) {
                playerScreenMoveX = rightScrollThreshold - playerXOnScreen;
                backgroundScrollDelta = -(playerRequestedDeltaX - playerScreenMoveX);
            }
        }

        player.setX(playerXOnScreen + playerScreenMoveX);
        backgroundScrollX += backgroundScrollDelta;

        if (player.getX() < 0) {
            player.setX(0);
        }
        if (player.getX() > screenWidth - player.getWidth()) {
            player.setX(screenWidth - player.getWidth());
        }

        while (backgroundScrollX <= -scaledBackgroundWidth) {
            backgroundScrollX += scaledBackgroundWidth;
        }
        while (backgroundScrollX >= scaledBackgroundWidth) {
            backgroundScrollX -= scaledBackgroundWidth;
        }

        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, backgroundScrollX, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundScrollX + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundScrollX - scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);

        player.render(spriteBatch);

        spriteBatch.end();
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
        backgroundTexture.dispose();
        player.dispose();
    }
}