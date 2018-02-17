package org.vaadin.artur.solitaire;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.vaadin.artur.gamecard.Card;
import org.vaadin.artur.gamecard.CardPile;
import org.vaadin.artur.gamecard.CardStack;
import org.vaadin.artur.gamecard.Deck;
import org.vaadin.artur.gamecard.data.CardInfo;

import com.vaadin.flow.component.Component;

public class GameController {

    private Configuration configuration = new Configuration();
    private GameLayout gameLayout;
    private Optional<Component> componentWithSelectedCard = Optional.empty();

    public GameController(GameLayout gameLayout) {
        this.gameLayout = gameLayout;
        gameLayout.setController(this);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void deal() {
        Deck deck = gameLayout.deck;
        deck.reset();
        deck.shuffle();

        gameLayout.deckPile.removeAllCards();
        for (CardStack stack : gameLayout.getStacks()) {
            stack.removeAllCards();
        }
        for (CardPile pile : gameLayout.getFinalPiles()) {
            pile.removeAllCards();
        }

        List<CardStack> stacks = gameLayout.getStacks();
        for (int i = 0; i < stacks.size(); i++) {
            CardStack stack = stacks.get(i);
            int cards = i + 1;
            for (int c = 0; c < cards; c++) {
                Card card = new Card(deck.removeTopCard());
                card.setBacksideUp(true);
                // card.setDropHandler(new CardDropHandler(card));
                stack.addCard(card);
            }
            Card lastCard = stack.getCard(cards - 1);
            // lastCardInStack.setDraggable(true);
            lastCard.setBacksideUp(false);
        }

    }

    private void deselectAll() {
        gameLayout.deckPile.setTopCardSelected(false);
        gameLayout.getStacks().forEach(stack -> stack.deselectAll());
        gameLayout.getFinalPiles()
                .forEach(pile -> pile.setTopCardSelected(false));
        componentWithSelectedCard = Optional.empty();
    }

    // private void setSelectedCard(Card c) {
    // selectedCard = c;
    //
    // }

    public void deckClick() {
        deselectAll();
        if (gameLayout.deck.isEmpty()) {
            if (!gameLayout.deckPile.isEmpty()) {
                for (CardInfo c : gameLayout.deckPile.getCards()) {
                    gameLayout.deck.addCard(c);
                }
                gameLayout.deckPile.removeAllCards();
            }
            return;
        }

        for (int i = 0; i < configuration.getCardsToDraw(); i++) {
            CardInfo c = gameLayout.deck.removeTopCard();
            if (c == null) {
                return;
            }

            gameLayout.deckPile.addCard(c);
        }
    }

    public void finalPileClick(CardPile finalPile) {
        CardInfo selectedCard = getFirstSelectedCard();
        if (selectedCard == null) {
            return;
        }

        boolean validMove = !finalPile.isEmpty()
                && finalPile.getTopCard().getRank() == selectedCard.getRank()
                        - 1
                && finalPile.getTopCard().getSuite() == selectedCard.getSuite();
        boolean validMoveEmpty = finalPile.isEmpty()
                && selectedCard.getRank() == 1;

        if (validMove || validMoveEmpty) {
            finalPile.addCard(selectedCard);
            removeCard(componentWithSelectedCard.get(), selectedCard);
            deselectAll();
        }
    }

    public void deckPileClick(CardPile deckPile) {
        if (deckPile.isTopCardSelected()) {
            deckPile.setTopCardSelected(false);
            componentWithSelectedCard = Optional.empty();
        } else {
            deselectAll();
            deckPile.setTopCardSelected(true);
            componentWithSelectedCard = Optional.of(deckPile);
        }

    }

    public void stackClick(CardStack clickedStack, Card clickedCard) {
        List<CardInfo> selectedCards = getSelectedCards();
        CardInfo firstSelected = getFirstSelectedCard();
        // Move the selected card(s) to this stack if they are suitable cards
        boolean validMove = false;
        if (firstSelected != null) {
            if (clickedStack.isEmpty()) {
                if (firstSelected.getRank() == 13) {
                    validMove = true;
                }
            } else if (firstSelected != null) {
                CardInfo lastInStack = clickedStack.getTopCard().getCardInfo();
                if (!clickedStack.containsCard(firstSelected) && firstSelected
                        .isOtherColorAndOneRankLower(lastInStack)) {
                    // Different stack than selection
                    // + matches last card in new stack
                    validMove = true;
                }
            }
        }
        if (validMove) {
            for (CardInfo selectedCard : selectedCards) {
                clickedStack.addCard(new Card(selectedCard));
                removeCard(componentWithSelectedCard.get(), selectedCard);
            }
            deselectAll();
        } else if (!clickedStack.isEmpty()) {
            // Not a valid move: deselect if clicked on the selected card,
            // otherwise select what was clicked on
            deselectAll();
            if (!clickedCard.getCardInfo().equals(firstSelected)) {
                selectCardAndAllOnTop(clickedStack, clickedCard);
            }
        }
    }

    public void deckPileDoubleClick(CardPile deckPile) {
        if (addToAnyFinalPile(deckPile.getTopCard())) {
            removeCard(deckPile, deckPile.getTopCard());
        }
    }

    public void stackDoubleClick(CardStack clickedStack, Card clickedCard) {
        if (clickedStack.getTopCard() == clickedCard) {
            if (addToAnyFinalPile(clickedCard.getCardInfo())) {
                removeCard(clickedStack, clickedCard.getCardInfo());
            }
        }
    }

    private boolean addToAnyFinalPile(CardInfo card) {
        for (CardPile finalPile : gameLayout.getFinalPiles()) {
            if (finalPile.isEmpty()) {
                if (card.getRank() == 1) {
                    finalPile.addCard(card);
                    return true;
                }
            } else {
                if (card.getSuite() == finalPile.getTopCard().getSuite() && card
                        .getRank() == finalPile.getTopCard().getRank() + 1) {
                    finalPile.addCard(card);
                    return true;
                }
            }
        }
        return false;
    }

    private void removeCard(Component component, CardInfo toRemove) {
        if (component == gameLayout.deckPile) {
            if (gameLayout.deckPile.getTopCard().equals(toRemove)) {
                gameLayout.deckPile.removeTopCard();
            }
        } else if (component instanceof CardStack) {
            CardStack stack = (CardStack) component;
            stack.removeCard(toRemove);
            Card topCard = stack.getTopCard();
            if (topCard != null && topCard.isBacksideUp()) {
                topCard.setBacksideUp(false);
            }
        } else {
            throw new IllegalArgumentException("Unsupported component type: "
                    + component.getClass().getName());
        }

    }

    private void selectCardAndAllOnTop(CardStack stack, Card card) {
        card.setSelected(true);
        stack.getCardsOnTopOf(card).forEach(c -> c.setSelected(true));
        componentWithSelectedCard = Optional.of(stack);
    }

    private CardInfo getFirstSelectedCard() {
        List<CardInfo> selected = getSelectedCards();

        if (selected.isEmpty()) {
            return null;
        }
        return selected.get(0);
    }

    private List<CardInfo> getSelectedCards() {
        if (!componentWithSelectedCard.isPresent()) {
            return Collections.emptyList();
        }
        Component component = componentWithSelectedCard.get();
        if (component == gameLayout.deckPile) {
            return Collections.singletonList(gameLayout.deckPile.getTopCard());
        } else if (component instanceof CardStack) {
            return ((CardStack) component).getSelected().stream()
                    .map(Card::getCardInfo).collect(Collectors.toList());
        } else {
            throw new IllegalStateException(
                    "Unknown component with selected card: " + component);
        }
    }
}
