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

    private float worldX; 
    private float screenY;
    private float currentScreenX;
    private float currentEffectiveSpeed = 0f; 
    private float maxSpeed = 350f;
    private float accelerationPerTap = 100f;
    private float decelerationRate = 100f;

    private float animationTime; 
    private boolean isMoving;  

    private float deltaWorldXThisFrame;

    public PlayerCharacter(float initialWorldX, float initialScreenY) {
        this.worldX = initialWorldX;
        this.screenY = initialScreenY;
        this.currentScreenX = initialWorldX;

        idleTexture = new Texture("character.png");
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            runTextures[i] = new Texture(Gdx.files.internal("characterRun/run" + (i + 1) + ".png"));
        }
    }

    /**
     * Atualiza a velocidade e a posição de mundo do jogador.
     * @param deltaTime O tempo decorrido desde o último frame.
     */
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentEffectiveSpeed += accelerationPerTap;
            if (currentEffectiveSpeed > maxSpeed) {
                currentEffectiveSpeed = maxSpeed;
            }
        }

        currentEffectiveSpeed -= decelerationRate * deltaTime;
        if (currentEffectiveSpeed < 0) {
            currentEffectiveSpeed = 0;
        }

        isMoving = currentEffectiveSpeed > 0.1f;

        deltaWorldXThisFrame = currentEffectiveSpeed * deltaTime; 
        worldX += deltaWorldXThisFrame;

        if (isMoving) {
            animationTime += deltaTime * (currentEffectiveSpeed / (maxSpeed / 2f));
        } else {
            animationTime = 0; 
        }
    }

    /**
     * Desenha o jogador na tela. A posição X na tela (currentScreenX)
     * é definida externamente (no Main) para que o scroll da câmera seja gerenciado.
     * @param spriteBatch O SpriteBatch para desenhar.
     */
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

        spriteBatch.draw(currentFrameTexture, currentScreenX, screenY, textureWidth, textureHeight);
    }

    /**
     * Libera os recursos das texturas do jogador.
     */
    public void dispose() {
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            texture.dispose();
        }
    }

    public float getX() {
        return worldX;
    }

    public float getY() {
        return screenY;
    }

    public void setX(float worldX) {
        this.worldX = worldX;
    }

    public void setY(float y) {
        this.screenY = y;
    }

    public float getCurrentScreenX() {
        return currentScreenX;
    }

    public void setCurrentScreenX(float currentScreenX) {
        this.currentScreenX = currentScreenX;
    }

    public float getWidth() {
        return (isMoving && runTextures.length > 0) ? runTextures[0].getWidth() : idleTexture.getWidth();
    }

    public float getHeight() {
        return (isMoving && runTextures.length > 0) ? runTextures[0].getHeight() : idleTexture.getHeight();
    }

    public float getDeltaXThisFrame() {
        return deltaWorldXThisFrame;
    }

    public float getSpeed() {
        return currentEffectiveSpeed;
    }
}