package com.dandbazaar.back.Items;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dandbazaar.back.games.Game;

import org.junit.jupiter.api.Test;

public class ItemConversionTest {

    @Test
    public void testConversionBetweenGames_exampleFromIssue() {
        // Juego 1: espada = 20 -> ratio = 20 / 20 = 1.0
        Game game1 = new Game();
        game1.setName("Game1");
        game1.setSwordPatternRatio(1.0);

        // Juego 2: espada = 299.99 -> ratio = 20 / 299.99 ≈ 0.0667
        Game game2 = new Game();
        game2.setName("Game2");
        game2.setSwordPatternRatio(20.0 / 299.99);

        Item item = new Item();
        item.setInGamePrice(119.95);
        item.setGame(game1);

    Double converted = item.toTargetCurrency(game2);

    // Calculamos el valor esperado usando la misma fórmula documentada:
    // precioDestino = precioOrigen * (ratioOrigen / ratioDestino)
    Double expected = item.getInGamePrice() * (game1.getSwordPatternRatio() / game2.getSwordPatternRatio());

    assertEquals(expected, converted, 0.0001, "La conversión debe seguir la fórmula esperada");
    }
}
