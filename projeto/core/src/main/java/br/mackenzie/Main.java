package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;

    Texture idleTexture;
    Texture[] runTextures;
    int NUM_RUN_FRAMES = 8;

    float characterX;
    float characterY;
    float speed = 200f;

    float animationTime;
    float frameDuration = 0.1f;
    int currentFrameIndex;

    boolean isMoving;
    boolean facingRight = true;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        // Carrega a textura do personagem parado (assumindo que está diretamente em assets/)
        idleTexture = new Texture("character.png");

        // Carrega todas as texturas da animação de corrida
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            // Caminho modificado para incluir a subpasta 'characterRun'
            runTextures[i] = new Texture("characterRun/run" + (i + 1) + ".png");
        }

        // Inicializa a posição do personagem
        characterX = 0;
        characterY = (Gdx.graphics.getHeight() - idleTexture.getHeight()) / 2f;

        // Inicializa o estado da animação
        animationTime = 0;
        currentFrameIndex = 0;
        isMoving = false;
    }

    @Override
    public void resize(int width, int height) {
        characterY = (height - idleTexture.getHeight()) / 2f;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime();

        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterX -= speed * deltaTime;
            isMoving = true;
            facingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterX += speed * deltaTime;
            isMoving = true;
            facingRight = true;
        }

        Texture currentFrameTexture;
        if (isMoving) {
            animationTime += deltaTime;
            currentFrameIndex = (int)(animationTime / frameDuration) % NUM_RUN_FRAMES;
            currentFrameTexture = runTextures[currentFrameIndex];
        } else {
            animationTime = 0;
            currentFrameTexture = idleTexture;
        }

        if (characterX < 0) {
            characterX = 0;
        }
        float textureWidth = currentFrameTexture.getWidth();
        if (characterX > Gdx.graphics.getWidth() - textureWidth) {
            characterX = Gdx.graphics.getWidth() - textureWidth;
        }

        spriteBatch.begin();

        float textureHeight = currentFrameTexture.getHeight();

        if (!facingRight) {
            spriteBatch.draw(currentFrameTexture, characterX + textureWidth, characterY, -textureWidth, textureHeight);
        } else {
            spriteBatch.draw(currentFrameTexture, characterX, characterY, textureWidth, textureHeight);
        }

        spriteBatch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            texture.dispose();
        }
    }
}