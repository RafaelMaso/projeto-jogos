package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayerCharacter {
    private Texture idleTexture;
    private Texture[] runTextures;
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f;

    private float x;
    private float y;
    private float speed = 200f;

    private float animationTime;
    private boolean isMoving;
    private boolean facingRight = true;

    public PlayerCharacter(float initialX, float initialY) {
        this.x = initialX;
        this.y = initialY;

        idleTexture = new Texture("character.png");
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            runTextures[i] = new Texture("characterRun/run" + (i + 1) + ".png");
        }
    }

    public void update(float deltaTime) {
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= speed * deltaTime;
            isMoving = true;
            facingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += speed * deltaTime;
            isMoving = true;
            facingRight = true;
        }

        if (isMoving) {
            animationTime += deltaTime;
        } else {
            animationTime = 0;
        }
    }

    public void render(SpriteBatch spriteBatch) {
        Texture currentFrameTexture;
        if (isMoving) {
            int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
            currentFrameTexture = runTextures[currentFrameIndex];
        } else {
            currentFrameTexture = idleTexture;
        }

        float textureWidth = currentFrameTexture.getWidth();
        float textureHeight = currentFrameTexture.getHeight();

        if (!facingRight) {
            spriteBatch.draw(currentFrameTexture, x + textureWidth, y, -textureWidth, textureHeight);
        } else {
            spriteBatch.draw(currentFrameTexture, x, y, textureWidth, textureHeight);
        }
    }

    public void dispose() {
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            texture.dispose();
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return (isMoving && runTextures.length > 0) ? runTextures[0].getWidth() : idleTexture.getWidth();
    }

    public float getHeight() {
        return (isMoving && runTextures.length > 0) ? runTextures[0].getHeight() : idleTexture.getHeight();
    }
}