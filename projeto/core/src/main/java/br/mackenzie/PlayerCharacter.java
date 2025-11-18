package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.files.FileHandle;

public class PlayerCharacter {
    // --- Constantes do Personagem ---
    private static final String IDLE_TEXTURE_PATH = "character.png";
    private static final String RUN_TEXTURE_BASE_PATH = "characterRun/run";
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f;
    private static final float MAX_SPEED = 350f;
    private static final float ACCELERATION_PER_TAP = 100f;
    private static final float DECELERATION_RATE = 100f;
    private static final float MOVING_THRESHOLD = 0.1f;

    // TAMANHO PADRÃO FIXO PARA TODOS
    private static final float STANDARD_WIDTH = 90f;
    private static final float STANDARD_HEIGHT = 80f;

    // --- Recursos Gráficos ---
    private Texture idleTexture;
    private Texture[] runTextures;

    // --- Estado do Personagem ---
    private float worldX;
    private float screenY;
    private float currentScreenX;
    private float currentEffectiveSpeed = 0f;
    private float animationTime;
    private boolean isMoving;
    private float deltaWorldXThisFrame;

    public PlayerCharacter(float initialWorldX, float initialScreenY) {
        this.worldX = initialWorldX;
        this.screenY = initialScreenY;
        this.currentScreenX = initialWorldX;

        // Carrega as texturas
        idleTexture = new Texture(IDLE_TEXTURE_PATH);
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            FileHandle fileHandle = Gdx.files.internal(RUN_TEXTURE_BASE_PATH + (i + 1) + ".png");
            if (!fileHandle.exists()) {
                Gdx.app.error("PlayerCharacter", "Run texture not found: " + fileHandle.path());
                runTextures[i] = idleTexture;
            } else {
                runTextures[i] = new Texture(fileHandle);
            }
        }
        animationTime = 0;
        isMoving = false;
    }

    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentEffectiveSpeed += ACCELERATION_PER_TAP;
            if (currentEffectiveSpeed > MAX_SPEED) {
                currentEffectiveSpeed = MAX_SPEED;
            }
        }

        currentEffectiveSpeed -= DECELERATION_RATE * deltaTime;
        if (currentEffectiveSpeed < 0) {
            currentEffectiveSpeed = 0;
        }

        isMoving = currentEffectiveSpeed > MOVING_THRESHOLD;

        deltaWorldXThisFrame = currentEffectiveSpeed * deltaTime;
        worldX += deltaWorldXThisFrame;

        if (isMoving) {
            animationTime += deltaTime * (currentEffectiveSpeed / (MAX_SPEED / 2f));
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

        // TAMANHO FIXO - IGNORA TAMANHO ORIGINAL DA TEXTURA
        spriteBatch.draw(currentFrameTexture, currentScreenX, screenY, STANDARD_WIDTH, STANDARD_HEIGHT);
    }

    public void dispose() {
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    public float getX() { return worldX; }
    public void setX(float worldX) { this.worldX = worldX; }
    public float getY() { return screenY; }
    public void setY(float y) { this.screenY = y; }
    public float getCurrentScreenX() { return currentScreenX; }
    public void setCurrentScreenX(float currentScreenX) { this.currentScreenX = currentScreenX; }

    // SEMPRE RETORNA TAMANHO PADRÃO
    public float getWidth() { return STANDARD_WIDTH; }
    public float getHeight() { return STANDARD_HEIGHT; }

    public float getDeltaXThisFrame() { return deltaWorldXThisFrame; }
    public float getSpeed() { return currentEffectiveSpeed; }

    // SEMPRE USA TAMANHO PADRÃO
    public Rectangle getBounds() {
        return new Rectangle(worldX, screenY, STANDARD_WIDTH, STANDARD_HEIGHT);
    }
}
