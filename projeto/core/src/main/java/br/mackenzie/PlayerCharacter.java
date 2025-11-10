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

    /**
     * Atualiza o estado do personagem a cada frame.
     * @param deltaTime O tempo decorrido desde o último frame.
     */
    public void update(float deltaTime) {
        // Verifica a entrada do usuário para aceleração
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentEffectiveSpeed += ACCELERATION_PER_TAP;
            // Limita a velocidade máxima
            if (currentEffectiveSpeed > MAX_SPEED) {
                currentEffectiveSpeed = MAX_SPEED;
            }
        }

        // Aplica desaceleração constante
        currentEffectiveSpeed -= DECELERATION_RATE * deltaTime;
        // Garante que a velocidade não seja negativa
        if (currentEffectiveSpeed < 0) {
            currentEffectiveSpeed = 0;
        }

        // Define o estado de movimento com base na velocidade
        isMoving = currentEffectiveSpeed > MOVING_THRESHOLD;

        // Calcula o deslocamento do personagem no mundo
        deltaWorldXThisFrame = currentEffectiveSpeed * deltaTime;
        worldX += deltaWorldXThisFrame;

        // Atualiza o tempo de animação apenas se estiver se movendo
        if (isMoving) {
            animationTime += deltaTime * (currentEffectiveSpeed / (MAX_SPEED / 2f));
        } else {
            animationTime = 0;
        }
    }

    /**
     * Desenha o personagem na tela.
     * @param spriteBatch O SpriteBatch usado para desenhar.
     */
    public void render(SpriteBatch spriteBatch) {
        Texture currentFrameTexture;
        if (isMoving) {
            // Calcula o índice do frame da animação de corrida
            int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
            currentFrameTexture = runTextures[currentFrameIndex];
        } else {
            currentFrameTexture = idleTexture; // Usa a textura parada
        }

        // Desenha o frame na posição de tela calculada
        spriteBatch.draw(currentFrameTexture, currentScreenX, screenY, currentFrameTexture.getWidth(), currentFrameTexture.getHeight());
    }

    /**
     * Libera as texturas alocadas.
     */
    public void dispose() {
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    // --- Getters e Setters ---
    public float getX() {
        return worldX;
    }

    public void setX(float worldX) {
        this.worldX = worldX;
    }

    public float getY() {
        return screenY;
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

    /**
     * Retorna a largura atual do sprite do jogador.
     */
    public float getWidth() {
        if (isMoving && runTextures.length > 0 && runTextures[0] != null) {
            return runTextures[0].getWidth();
        }
        return idleTexture.getWidth();
    }

    /**
     * Retorna a altura atual do sprite do jogador.
     */
    public float getHeight() {
        if (isMoving && runTextures.length > 0 && runTextures[0] != null) {
            return runTextures[0].getHeight();
        }
        return idleTexture.getHeight();
    }

    public float getDeltaXThisFrame() {
        return deltaWorldXThisFrame;
    }

    public float getSpeed() {
        return currentEffectiveSpeed;
    }

    /**
     * Retorna a caixa de colisão do jogador em coordenadas de mundo.
     */
    public Rectangle getBounds() {
        return new Rectangle(worldX, screenY, getWidth(), getHeight());
    }
}