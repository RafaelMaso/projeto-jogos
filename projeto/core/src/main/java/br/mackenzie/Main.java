package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

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
    BitmapFont gameOverFont;
    boolean gameOver;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        backgroundTexture = new Texture("city1.png");

        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        player = new PlayerCharacter(Gdx.graphics.getWidth() / 2f, 20); 

        police = new Police(player.getX() - 300f, 20);
        police.setHeight(player.getHeight() + 50); 

        scoreManager = new ScoreManager(10);
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);

        gameOverFont = new BitmapFont();
        gameOverFont.setColor(Color.RED);
        gameOverFont.getData().setScale(4);

        gameOver = false;
    }

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

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime();

        if (!gameOver) {
            player.update(deltaTime); 
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
            if (clampedPlayerScreenX > Gdx.graphics.getWidth() - player.getWidth()) clampedPlayerScreenX = Gdx.graphics.getWidth() - player.getWidth();
            player.setCurrentScreenX(clampedPlayerScreenX);

            float maxDistanceFromPlayer = Gdx.graphics.getWidth() * 0.75f;
            if (police.getX() < player.getX() - maxDistanceFromPlayer) {
                police.setX(player.getX() - maxDistanceFromPlayer);
            }

            Rectangle playerBounds = player.getBounds();
            Rectangle policeBounds = police.getBounds();

            if (playerBounds.overlaps(policeBounds)) {
                gameOver = true;
            }
        }

        float backgroundDrawOffset = -(worldCameraX % scaledBackgroundWidth);
        if (backgroundDrawOffset > 0) { 
            backgroundDrawOffset -= scaledBackgroundWidth;
        }

        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, backgroundDrawOffset, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundDrawOffset + scaledBackgroundWidth * 2, 0, scaledBackgroundWidth, scaledBackgroundHeight); 

        if (gameOver) {
            String gameOverText = "GAME OVER!";
            GlyphLayout layout = new GlyphLayout(gameOverFont, gameOverText);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            float y = (Gdx.graphics.getHeight() + layout.height) / 2;
            gameOverFont.draw(spriteBatch, gameOverText, x, y);
        } else {
            player.render(spriteBatch);
            police.render(spriteBatch, -worldCameraX); 
            font.draw(spriteBatch, "Score: " + scoreManager.getScore(), 10, Gdx.graphics.getHeight() - 10);
        }
        
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
        police.dispose();
        font.dispose();
        gameOverFont.dispose();
    }
}