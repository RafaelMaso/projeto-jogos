package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    PlayerCharacter player;
    Texture backgroundTexture;

    float backgroundScrollX = 0; // Posição de rolagem horizontal do background

    // Variáveis para manter a proporção do background
    float scaledBackgroundWidth;
    float scaledBackgroundHeight;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture("city1.png");

        // Calcula as dimensões escaladas do background
        scaledBackgroundHeight = Gdx.graphics.getHeight();
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        player = new PlayerCharacter(Gdx.graphics.getWidth() / 4f, 20); // Inicia o player mais para a esquerda
    }

    @Override
    public void resize(int width, int height) {
        // Quando a tela é redimensionada, recalculamos as dimensões escaladas do background
        scaledBackgroundHeight = height;
        float aspectRatio = (float) backgroundTexture.getWidth() / backgroundTexture.getHeight();
        scaledBackgroundWidth = scaledBackgroundHeight * aspectRatio;

        if (player != null) {
            player.setY(20); // Mantém o player na parte inferior da tela
            // Se o player está no limite da tela, ajuste a posição para a nova largura da tela
            if (player.getX() > width - player.getWidth()) {
                player.setX(width - player.getWidth());
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime();

        player.update(deltaTime); // Atualiza o estado da animação e calcula deltaXThisFrame

        float playerRequestedDeltaX = player.getDeltaXThisFrame(); // Movimento que o player 'quer' fazer

        float playerScreenMoveX = playerRequestedDeltaX; // Movimento que o player realmente fará na tela
        float backgroundScrollDelta = 0; // Deslocamento que o background fará

        float screenWidth = Gdx.graphics.getWidth();
        float playerXOnScreen = player.getX(); // Posição X atual do player na tela

        // Define uma "zona ativa" na tela onde o jogador se move livremente.
        // Se ele tentar sair dessa zona, o background começa a rolar.
        float leftScrollThreshold = screenWidth * 0.2f; // Ex: 20% da tela a partir da esquerda
        float rightScrollThreshold = screenWidth * 0.8f - player.getWidth(); // Ex: 80% da tela a partir da esquerda, ajustado pela largura do player

        // Lógica para movimento para a direita (agora o player só se move para a direita)
        if (playerRequestedDeltaX > 0) { // O jogador está tentando mover para a direita
            if (playerXOnScreen >= rightScrollThreshold) {
                // O jogador já está no limite direito da zona de rolagem, então o background rola
                playerScreenMoveX = 0; // O player para de se mover na tela
                backgroundScrollDelta = -playerRequestedDeltaX; // O background rola para a esquerda
            } else if (playerXOnScreen + playerRequestedDeltaX > rightScrollThreshold) {
                // O jogador vai entrar na zona de rolagem neste frame
                // Move o jogador até o limite e o restante do movimento rola o background
                playerScreenMoveX = rightScrollThreshold - playerXOnScreen;
                backgroundScrollDelta = -(playerRequestedDeltaX - playerScreenMoveX);
            }
        }
        // A lógica para movimento para a esquerda foi removida do PlayerCharacter,
        // então este bloco não será mais ativado.
        // Se quiséssemos um limite para a esquerda sem o jogador ir "para trás", poderíamos
        // ter uma lógica para que backgroundScrollX não se torne muito positivo.
        // Por enquanto, o player só se move para a direita ou fica parado.

        // Aplica os movimentos calculados
        player.setX(playerXOnScreen + playerScreenMoveX);
        backgroundScrollX += backgroundScrollDelta;

        // Garante que o jogador não saia da tela
        if (player.getX() < 0) {
            player.setX(0);
        }
        if (player.getX() > screenWidth - player.getWidth()) {
            player.setX(screenWidth - player.getWidth());
        }

        // Lógica de loop para o background
        while (backgroundScrollX <= -scaledBackgroundWidth) {
            backgroundScrollX += scaledBackgroundWidth;
        }
        while (backgroundScrollX >= scaledBackgroundWidth) {
            backgroundScrollX -= scaledBackgroundWidth;
        }

        spriteBatch.begin();

        // Desenha o background usando as dimensões SCALED
        spriteBatch.draw(backgroundTexture, backgroundScrollX, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundScrollX + scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);
        spriteBatch.draw(backgroundTexture, backgroundScrollX - scaledBackgroundWidth, 0, scaledBackgroundWidth, scaledBackgroundHeight);


        player.render(spriteBatch);

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
        backgroundTexture.dispose();
        player.dispose();
    }
}