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

    private float currentEffectiveSpeed = 0f; 
    private float maxSpeed = 350f;
    private float accelerationPerTap = 100f; // Quanto de velocidade é adicionado por cada toque na barra de espaço
    private float decelerationRate = 100f;  // Quanto de velocidade é perdido por segundo (desaceleração natural)

    private float animationTime; 
    private boolean isMoving;  

    private float deltaXThisFrame; 

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
        // Acelera o personagem se a barra de espaço for pressionada (apenas um toque)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentEffectiveSpeed += accelerationPerTap;
            // Limita a velocidade máxima
            if (currentEffectiveSpeed > maxSpeed) {
                currentEffectiveSpeed = maxSpeed;
            }
        }

        // Desacelera o personagem naturalmente ao longo do tempo
        currentEffectiveSpeed -= decelerationRate * deltaTime;
        if (currentEffectiveSpeed < 0) {
            currentEffectiveSpeed = 0;
        }

        isMoving = currentEffectiveSpeed > 0.1f;

        deltaXThisFrame = currentEffectiveSpeed * deltaTime;

        if (isMoving) {
            animationTime += deltaTime * (currentEffectiveSpeed / (maxSpeed / 2f));
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

        spriteBatch.draw(currentFrameTexture, x, y, textureWidth, textureHeight);
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

    public float getDeltaXThisFrame() {
        return deltaXThisFrame;
    }

    public float getSpeed() {
        return currentEffectiveSpeed;
    }
}