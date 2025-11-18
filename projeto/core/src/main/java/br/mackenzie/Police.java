package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.files.FileHandle;

public class Police {
    // --- Constantes ---
    private static final String RUN_TEXTURE_BASE_PATH = "police1Run/run";
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f;
    private static final float INITIAL_SPEED = 50f;
    private static final float MAX_SPEED = 250f;
    private static final float ACCELERATION_RATE = 40f;
    private static final float BASE_HEIGHT = 125f;
    private static final float BASE_WIDTH = 115f;

    // --- Recursos Gráficos ---
    private Texture[] runTextures;

    // --- Estado ---
    private float x;
    private float y;
    private float currentSpeed;
    private float animationTime;
    private float currentWidth;
    private float currentHeight;
    private float originalAspectRatio;
    private float speedMultiplier = 1.0f;

    public Police(float initialWorldX, float initialScreenY) {
        this.x = initialWorldX;
        this.y = initialScreenY;
        this.currentSpeed = INITIAL_SPEED;

        loadTextures();
        initializeSize();
        animationTime = 0;
    }

    private void loadTextures() {
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            FileHandle fileHandle = Gdx.files.internal(RUN_TEXTURE_BASE_PATH + (i + 1) + ".png");
            if (fileHandle.exists()) {
                runTextures[i] = new Texture(fileHandle);
            } else {
                Gdx.app.error("Police", "Run texture not found: " + fileHandle.path());
            }
        }
    }

    private void initializeSize() {
        this.currentHeight = BASE_HEIGHT;
        this.currentWidth = BASE_WIDTH;

        if (runTextures.length > 0 && runTextures[0] != null) {
            float textureWidth = runTextures[0].getWidth();
            float textureHeight = runTextures[0].getHeight();
            this.originalAspectRatio = textureWidth / textureHeight;
        } else {
            this.originalAspectRatio = BASE_WIDTH / BASE_HEIGHT;
        }
    }

    public void update(float deltaTime) {
        currentSpeed += ACCELERATION_RATE * deltaTime;
        if (currentSpeed > MAX_SPEED) {
            currentSpeed = MAX_SPEED;
        }

        x += (currentSpeed * speedMultiplier) * deltaTime;
        animationTime += deltaTime;
    }

    public void render(SpriteBatch spriteBatch, float cameraOffsetWorldX) {
        int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
        Texture currentFrameTexture = runTextures[currentFrameIndex];

        if (currentFrameTexture != null) {
            spriteBatch.draw(currentFrameTexture, x + cameraOffsetWorldX, y, currentWidth, currentHeight);
        }
    }

    public void dispose() {
        for (Texture texture : runTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    // --- Getters e Setters ---
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return currentWidth; }
    public float getHeight() { return currentHeight; }

    public void setHeight(float newHeight) {
        if (newHeight > 0) {
            this.currentHeight = newHeight;
            if (originalAspectRatio > 0) {
                this.currentWidth = newHeight * originalAspectRatio;
            } else {
                this.currentWidth = BASE_WIDTH;
            }
        }
    }

    public void setSpeed(float speed) { this.currentSpeed = speed; }
    public void setSpeedMultiplier(float multiplier) { this.speedMultiplier = multiplier; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, currentWidth, currentHeight);
    }
}
