package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.files.FileHandle;

public class Police {
    // --- Constantes da Polícia ---
    private static final String RUN_TEXTURE_BASE_PATH = "police1Run/run";
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f;
    private static final float INITIAL_SPEED = 50f;
    private static final float MAX_SPEED = 250f;
    private static final float ACCELERATION_RATE = 40f;

    // --- Recursos Gráficos ---
    private Texture[] runTextures;

    // --- Estado da Polícia ---
    private float x;
    private float y;
    private float currentSpeed;
    private float animationTime;
    private float currentWidth;
    private float currentHeight;
    private float originalAspectRatio;

    public Police(float initialWorldX, float initialScreenY) {
        this.x = initialWorldX;
        this.y = initialScreenY;
        this.currentSpeed = INITIAL_SPEED;

        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            FileHandle fileHandle = Gdx.files.internal(RUN_TEXTURE_BASE_PATH + (i + 1) + ".png");
            if (!fileHandle.exists()) {
                Gdx.app.error("Police", "Run texture not found: " + fileHandle.path());
            } else {
                runTextures[i] = new Texture(fileHandle);
            }
        }

        // Inicializa largura, altura e proporção de aspecto com base na primeira textura
        if (runTextures.length > 0 && runTextures[0] != null) {
            float initialTextureWidth = runTextures[0].getWidth();
            float initialTextureHeight = runTextures[0].getHeight();
            this.currentWidth = initialTextureWidth;
            this.currentHeight = initialTextureHeight;
            this.originalAspectRatio = initialTextureWidth / initialTextureHeight;
        } else {
            // Valores padrão seguros caso nenhuma textura seja carregada
            this.currentWidth = 100;
            this.currentHeight = 100;
            this.originalAspectRatio = 1.0f;
            Gdx.app.error("Police", "No police run textures loaded, using default size.");
        }
        animationTime = 0;
    }

    /**
     * Atualiza o estado da polícia a cada frame.
     * @param deltaTime O tempo decorrido desde o último frame.
     */
    public void update(float deltaTime) {
        // Aumenta a velocidade da polícia gradualmente
        currentSpeed += ACCELERATION_RATE * deltaTime;
        // Limita a velocidade máxima
        if (currentSpeed > MAX_SPEED) {
            currentSpeed = MAX_SPEED;
        }

        // Move a polícia com base na velocidade
        x += currentSpeed * deltaTime;
        // Atualiza o tempo de animação
        animationTime += deltaTime;
    }

    /**
     * Desenha a polícia na tela, ajustando pela câmera.
     * @param spriteBatch O SpriteBatch usado para desenhar.
     * @param cameraOffsetWorldX O deslocamento da câmera no mundo X.
     */
    public void render(SpriteBatch spriteBatch, float cameraOffsetWorldX) {
        // Calcula o índice do frame da animação de corrida
        int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
        Texture currentFrameTexture = runTextures[currentFrameIndex];

        if (currentFrameTexture != null) { // Garante que a textura existe antes de tentar desenhar
            // Desenha a textura na posição ajustada pela câmera
            spriteBatch.draw(currentFrameTexture, x + cameraOffsetWorldX, y, currentWidth, currentHeight);
        } else {
             Gdx.app.error("Police", "Attempted to draw null police texture at index " + currentFrameIndex);
        }
    }

    /**
     * Libera as texturas alocadas.
     */
    public void dispose() {
        for (Texture texture : runTextures) {
            if (texture != null) { // Verifica se a textura foi carregada corretamente
                texture.dispose();
            }
        }
    }

    // --- Getters e Setters ---
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
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

    /**
     * Define a altura da polícia e ajusta a largura para manter a proporção de aspecto.
     */
    public void setHeight(float newHeight) {
        if (originalAspectRatio > 0 && newHeight > 0) {
            this.currentHeight = newHeight;
            this.currentWidth = newHeight * originalAspectRatio;
        } else if (newHeight > 0) {
            this.currentHeight = newHeight;
            if (originalAspectRatio == 0) { 
                 this.currentWidth = newHeight;
            }
        }
    }

    public void setSpeed(float speed) {
        this.currentSpeed = speed;
    }

    /**
     * Retorna a caixa de colisão da polícia em coordenadas de mundo.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, currentWidth, currentHeight);
    }
}