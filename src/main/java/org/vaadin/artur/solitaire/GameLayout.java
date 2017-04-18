package org.vaadin.artur.solitaire;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.vaadin.artur.playingcards.Card;
import org.vaadin.artur.playingcards.CardPile;
import org.vaadin.artur.playingcards.CardStack;

public class GameLayout extends GameLayoutDesign {

    public GameLayout() {
    }

    public void setController(GameController gameController) {
        deck.addClickListener(e -> {
            gameController.deckClick();
        });
        deckPile.addClickListener(e -> {
            if (e.isDoubleClick()) {
                gameController.deckPileDoubleClick(deckPile);
            } else {
                gameController.deckPileClick(deckPile);
            }
        });
        getFinalPiles().forEach(pile -> {
            pile.addClickListener(e -> {
                gameController.finalPileClick((CardPile) e.getComponent());
            });
        });
        getStacks().forEach(stack -> {
            stack.addLayoutClickListener(e -> {
                if (e.isDoubleClick()) {
                    gameController.stackDoubleClick(
                            (CardStack) e.getComponent().getParent(),
                            (Card) e.getChildComponent());
                } else {
                    gameController.stackClick(
                            (CardStack) e.getComponent().getParent(),
                            (Card) e.getChildComponent());
                }
            });
        });
    }

    public List<CardStack> getStacks() {
        return Stream.of(stack1, stack2, stack3, stack4, stack5, stack6, stack7)
                .collect(Collectors.toList());
    }

    public List<CardPile> getFinalPiles() {
        return Stream.of(pile1, pile2, pile3, pile4)
                .collect(Collectors.toList());
    }

}
