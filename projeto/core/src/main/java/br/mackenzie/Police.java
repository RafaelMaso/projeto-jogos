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
     * @param deltaTime O tempo decorrido desde o último frame.
     */
    public void update(float deltaTime) {
        x += speed * deltaTime;
        animationTime += deltaTime;
    }

    /**
     * Desenha o policial na tela.
     * O backgroundScrollX é usado para traduzir a posição do mundo do policial para a posição correta na tela,
     * fazendo com que ele se mova junto com o scroll do cenário.
     * @param spriteBatch O SpriteBatch para desenhar.
     * @param backgroundScrollX O deslocamento X do background.
     */
    public void render(SpriteBatch spriteBatch, float backgroundScrollX) {
        int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
        Texture currentFrameTexture = runTextures[currentFrameIndex];

        // Usa as dimensões atuais (escaladas) para desenhar
        spriteBatch.draw(currentFrameTexture, x + backgroundScrollX, y, currentWidth, currentHeight);
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

    /**
     * Define a posição Y do policial.
     * @param y A nova coordenada Y.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Retorna a largura atual do policial.
     * @return A largura do policial.
     */
    public float getWidth() {
        return currentWidth;
    }

    /**
     * Retorna a altura atual do policial.
     * @return A altura do policial.
     */
    public float getHeight() {
        return currentHeight;
    }

    /**
     * Define a altura do policial e ajusta a largura proporcionalmente
     * para manter a proporção da imagem original.
     * @param newHeight A nova altura desejada.
     */
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
}