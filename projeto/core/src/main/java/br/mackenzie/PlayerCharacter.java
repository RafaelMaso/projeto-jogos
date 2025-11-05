package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PlayerCharacter {
    private Texture idleTexture;
    private Texture[] runTextures;
    private static final int NUM_RUN_FRAMES = 8;
    private static final float FRAME_DURATION = 0.1f; // Duração de cada frame da animação de corrida

    private float x; // Posição X na tela
    private float y;

    // --- NOVAS VARIÁVEIS para o novo sistema de velocidade ---
    private float currentEffectiveSpeed = 0f; // Velocidade atual do personagem
    private float maxSpeed = 350f;          // Velocidade máxima que o personagem pode atingir
    private float accelerationPerTap = 80f; // Quanto de velocidade é adicionado por cada toque na barra de espaço
    private float decelerationRate = 150f;  // Quanto de velocidade é perdido por segundo (desaceleração natural)
    // --------------------------------------------------------

    private float animationTime; // Tempo acumulado para a animação
    private boolean isMoving;    // Flag para saber se o personagem está se movendo
    private boolean facingRight = true; // O personagem agora sempre se move para a direita, então sempre está 'facingRight'

    private float deltaXThisFrame; // Armazena o movimento X calculado para o frame atual

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
        // 1. Acelera o personagem se a barra de espaço for pressionada (apenas um toque)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentEffectiveSpeed += accelerationPerTap;
            // Limita a velocidade máxima
            if (currentEffectiveSpeed > maxSpeed) {
                currentEffectiveSpeed = maxSpeed;
            }
        }

        // 2. Desacelera o personagem naturalmente ao longo do tempo
        currentEffectiveSpeed -= decelerationRate * deltaTime;
        // Garante que a velocidade não seja negativa (personagem não volta)
        if (currentEffectiveSpeed < 0) {
            currentEffectiveSpeed = 0;
        }

        // 3. Define se o personagem está se movendo com base na velocidade atual
        // Um pequeno valor é usado para evitar trepidação quando a velocidade é quase zero
        isMoving = currentEffectiveSpeed > 0.1f;

        // 4. Calcula o movimento horizontal para este frame
        // A direção é sempre para a direita, então deltaXThisFrame é sempre positivo ou zero.
        deltaXThisFrame = currentEffectiveSpeed * deltaTime;

        // 5. Atualiza o tempo de animação se estiver se movendo
        if (isMoving) {
            // Avança o tempo de animação mais rápido quanto maior a velocidade,
            // para que a animação de corrida seja proporcional ao movimento.
            // maxSpeed / 2f é um valor de referência para a velocidade "normal" da animação.
            animationTime += deltaTime * (currentEffectiveSpeed / (maxSpeed / 2f));
        } else {
            animationTime = 0; // Se não estiver se movendo, reseta o tempo para mostrar o frame de idle
        }
    }

    public void render(SpriteBatch spriteBatch) {
        Texture currentFrameTexture;
        if (isMoving) {
            // Calcula o índice do frame atual da animação de corrida
            int currentFrameIndex = (int)(animationTime / FRAME_DURATION) % NUM_RUN_FRAMES;
            currentFrameTexture = runTextures[currentFrameIndex];
        } else {
            currentFrameTexture = idleTexture; // Mostra o frame de idle se não estiver se movendo
        }

        float textureWidth = currentFrameTexture.getWidth();
        float textureHeight = currentFrameTexture.getHeight();

        // O personagem sempre está virado para a direita, então o draw é simples.
        spriteBatch.draw(currentFrameTexture, x, y, textureWidth, textureHeight);
    }

    public void dispose() {
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            texture.dispose();
        }
    }

    // --- Getters e Setters (mantidos do código anterior) ---
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

    // Retorna a velocidade efetiva atual do personagem
    public float getSpeed() {
        return currentEffectiveSpeed;
    }
}