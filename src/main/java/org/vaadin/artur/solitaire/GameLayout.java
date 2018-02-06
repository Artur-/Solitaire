package org.vaadin.artur.solitaire;

import java.util.Arrays;
import java.util.List;

import org.vaadin.artur.gamecard.Card;
import org.vaadin.artur.gamecard.CardPile;
import org.vaadin.artur.gamecard.CardStack;
import org.vaadin.artur.gamecard.Deck;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class GameLayout extends VerticalLayout {
    protected Deck deck = new Deck();
    protected CardPile deckPile = new CardPile();

    protected CardPile[] finalPiles = new CardPile[4];
    protected CardStack[] stacks = new CardStack[7];

    public GameLayout() {
        setSpacing(true);
        HorizontalLayout topLayout = new HorizontalLayout();
        HorizontalLayout bottomLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        bottomLayout.setSpacing(true);

        for (int i = 0; i < finalPiles.length; i++) {
            finalPiles[i] = new CardPile();
        }
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new CardStack();
        }
        Div spacer = new Div();
        spacer.setWidth(Card.WIDTH + "px");

        deck.getElement().getStyle().set("width", Card.WIDTH + "px");
        deck.getElement().getStyle().set("height", Card.HEIGHT + "px");

        topLayout.add(deck, deckPile, spacer);
        topLayout.add(finalPiles);
        bottomLayout.add(stacks);
        add(topLayout, bottomLayout);
    }

    public void setController(GameController gameController) {
        deck.addClickListener(e -> {
            gameController.deckClick();
        });
        deckPile.addClickListener(e -> {
            gameController.deckPileClick(deckPile);
        });
        deckPile.addDoubleClickListener(e -> {
            gameController.deckPileDoubleClick(deckPile);
        });

        for (int i = 0; i < finalPiles.length; i++) {
            CardPile finalPile = finalPiles[i];
            finalPile.addClickListener(e -> {
                gameController.finalPileClick(finalPile);
            });
        }
        for (int i = 0; i < stacks.length; i++) {
            stacks[i].addClickListener(e -> {
                if (e.getTarget() == null || !e.getTarget().isBacksideUp()) {
                    gameController.stackClick(e.getSource(), e.getTarget());
                }
            });
            stacks[i].addDoubleClickListener(e -> {
                if (e.getTarget() == null || !e.getTarget().isBacksideUp()) {
                    gameController.stackDoubleClick(e.getSource(),
                            e.getTarget());
                }
            });
        }
    }

    public List<CardPile> getFinalPiles() {
        return Arrays.asList(finalPiles);
    }

    public List<CardStack> getStacks() {
        return Arrays.asList(stacks);
    }

}
