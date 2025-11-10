package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Police {
    private Texture[] runTextures;
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f;

    private float x;
    private float y;
    private float speed = 150f;
    
    private float animationTime;
    private float currentWidth;
    private float currentHeight;
    private float originalAspectRatio;

    public Police(float initialWorldX, float initialScreenY) {
        this.x = initialWorldX;
        this.y = initialScreenY;
        
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            runTextures[i] = new Texture(Gdx.files.internal("police1Run/run" + (i + 1) + ".png"));
        }
        
        if (runTextures.length > 0) {
            float initialTextureWidth = runTextures[0].getWidth();
            float initialTextureHeight = runTextures[0].getHeight();
            this.currentWidth = initialTextureWidth;
            this.currentHeight = initialTextureHeight;
            this.originalAspectRatio = initialTextureWidth / initialTextureHeight;
        } else {
            this.currentWidth = 0;
            this.currentHeight = 0;
            this.originalAspectRatio = 1.0f; 
        }
        animationTime = 0;
    }

    /**
     * Atualiza a posição e a animação do policial.
     */
    public void update(float deltaTime) {
        x += speed * deltaTime; 
        animationTime += deltaTime;
    }

    /**
     * Desenha o policial na tela.
     */
    public void render(SpriteBatch spriteBatch, float cameraOffsetWorldX) {
        int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
        Texture currentFrameTexture = runTextures[currentFrameIndex];

        spriteBatch.draw(currentFrameTexture, x + cameraOffsetWorldX, y, currentWidth, currentHeight);
    }

    /**
     * Libera os recursos das texturas do policial.
     */
    public void dispose() {
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

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return currentWidth;
    }

    public float getHeight() {
        return currentHeight;
    }

    public void setHeight(float newHeight) {
        if (originalAspectRatio > 0 && newHeight > 0) { 
            this.currentHeight = newHeight;
            this.currentWidth = newHeight * originalAspectRatio;
        } else {
            this.currentHeight = newHeight;
            if (originalAspectRatio == 0) { 
                 this.currentWidth = newHeight; 
            }
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}