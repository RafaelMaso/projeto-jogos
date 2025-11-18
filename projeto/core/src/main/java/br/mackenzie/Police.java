package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.files.FileHandle;

public class Police {
    // --- Constantes ---
    private static final String[] PHASE_TEXTURE_PATHS = {
        "police1Run/run",  // Fase 1
        "police2Run/run",  // Fase 2
        "police3Run/run"   // Fase 3
    };
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f;
    private static final float INITIAL_SPEED = 50f;
    private static final float MAX_SPEED = 250f;
    private static final float ACCELERATION_RATE = 40f;

    // MESMO TAMANHO DO PLAYER
    private static final float STANDARD_WIDTH = 110f;
    private static final float STANDARD_HEIGHT = 115f;

    // --- Recursos Gráficos ---
    private Texture[][] phaseRunTextures;
    private Texture[] currentRunTextures;

    // --- Estado ---
    private float x;
    private float y;
    private float currentSpeed;
    private float animationTime;
    private float speedMultiplier = 1.0f;
    private int currentPhase = 0;

    public Police(float initialWorldX, float initialScreenY) {
        this.x = initialWorldX;
        this.y = initialScreenY;
        this.currentSpeed = INITIAL_SPEED;

        loadAllPhaseTextures();
        setPhase(0);
        animationTime = 0;
    }

    private void loadAllPhaseTextures() {
        phaseRunTextures = new Texture[PHASE_TEXTURE_PATHS.length][NUM_RUN_FRAMES];

        for (int phase = 0; phase < PHASE_TEXTURE_PATHS.length; phase++) {
            for (int frame = 0; frame < NUM_RUN_FRAMES; frame++) {
                String frameNumber = String.format("%02d", frame + 1);
                FileHandle fileHandle = Gdx.files.internal(PHASE_TEXTURE_PATHS[phase] + frameNumber + ".png");

                if (fileHandle.exists()) {
                    phaseRunTextures[phase][frame] = new Texture(fileHandle);
                } else {
                    fileHandle = Gdx.files.internal(PHASE_TEXTURE_PATHS[phase] + (frame + 1) + ".png");
                    if (fileHandle.exists()) {
                        phaseRunTextures[phase][frame] = new Texture(fileHandle);
                    } else {
                        Gdx.app.error("Police", "Texture not found for phase " + phase + ", frame " + (frame + 1) + ": " + fileHandle.path());
                        phaseRunTextures[phase][frame] = createFallbackTexture();
                    }
                }
            }
        }
    }

    private Texture createFallbackTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(100, 100, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 0, 0, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void setPhase(int phase) {
        if (phase >= 0 && phase < phaseRunTextures.length) {
            this.currentPhase = phase;
            this.currentRunTextures = phaseRunTextures[phase];
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
        if (currentRunTextures == null || currentRunTextures.length == 0) return;

        int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
        Texture currentFrameTexture = currentRunTextures[currentFrameIndex];

        if (currentFrameTexture != null) {
            // MESMO TAMANHO DO PLAYER
            spriteBatch.draw(currentFrameTexture, x + cameraOffsetWorldX, y, STANDARD_WIDTH, STANDARD_HEIGHT);
        }
    }

    public void dispose() {
        if (phaseRunTextures != null) {
            for (int phase = 0; phase < phaseRunTextures.length; phase++) {
                if (phaseRunTextures[phase] != null) {
                    for (int frame = 0; frame < phaseRunTextures[phase].length; frame++) {
                        if (phaseRunTextures[phase][frame] != null) {
                            phaseRunTextures[phase][frame].dispose();
                        }
                    }
                }
            }
        }
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return STANDARD_WIDTH; }
    public float getHeight() { return STANDARD_HEIGHT; }
    public void setSpeed(float speed) { this.currentSpeed = speed; }
    public void setSpeedMultiplier(float multiplier) { this.speedMultiplier = multiplier; }
    public int getCurrentPhase() { return currentPhase; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, STANDARD_WIDTH, STANDARD_HEIGHT);
    }
}
