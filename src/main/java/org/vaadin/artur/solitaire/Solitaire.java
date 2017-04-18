// package org.vaadin.artur.solitaire;
//
// import java.io.Serializable;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
//
// import org.vaadin.artur.playingcards.Card;
// import org.vaadin.artur.playingcards.CardContainer;
// import org.vaadin.artur.playingcards.CardInfo;
// import org.vaadin.artur.playingcards.CardPile;
// import org.vaadin.artur.playingcards.CardStack;
// import org.vaadin.artur.playingcards.Deck;
// import org.vaadin.artur.playingcards.criteria.AcceptCardWithColor;
// import org.vaadin.artur.playingcards.criteria.AcceptCardWithRank;
// import org.vaadin.artur.playingcards.shared.Suite.Color;
//
// import com.vaadin.event.LayoutEvents.LayoutClickEvent;
// import com.vaadin.event.LayoutEvents.LayoutClickListener;
// import com.vaadin.event.MouseEvents.ClickEvent;
// import com.vaadin.event.MouseEvents.ClickListener;
// import com.vaadin.event.Transferable;
// import com.vaadin.event.dd.DragAndDropEvent;
// import com.vaadin.event.dd.DropHandler;
// import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
// import com.vaadin.event.dd.acceptcriteria.And;
// import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
// import com.vaadin.event.dnd.DropEvent;
// import com.vaadin.event.dnd.DropTargetExtension;
// import com.vaadin.server.VaadinRequest;
// import com.vaadin.ui.AbsoluteLayout;
// import com.vaadin.ui.Component;
// import com.vaadin.ui.MenuBar;
//
// public class Solitaire {
//
// private Deck deck;
// private CardPile deckPile;
//
// private CardStack stack[] = new CardStack[7];
//
// private CardPile finalPile[] = new CardPile[4];
//
// private Card selectedCard;
// private MenuBar menu;
//
// private Configuration configuration = new Configuration();
//
// private static int xOffset = 30;
// private static int yOffset = 70;
//
// protected void init(VaadinRequest request) {
// AbsoluteLayout mainLayout = new AbsoluteLayout();
// mainLayout.setSizeFull();
// setContent(createMainLayout(mainLayout));
//
// deck = new Deck();
// deck.addClickListener(new ClickListener() {
// @Override
// public void click(ClickEvent event) {
// if (event.isDoubleClick()) {
// return;
// }
//
// deckClick();
// }
// });
//
// deckPile = new CardPile();
// // deckPile.setDraggable(true);
// deckPile.addClickListener(new ClickListener() {
// @Override
// public void click(ClickEvent event) {
// if (event.isDoubleClick()) {
// deckPileDoubleClick();
// } else {
// deckPileClick();
// }
// }
// });
// for (int i = 0; i < stack.length; i++) {
// stack[i] = new CardStack();
// // stack[i].setDropHandler(new DropHandler() {
// //
// // @Override
// // public void drop(DragAndDropEvent dropEvent) {
// // doDrop(dropEvent);
// // }
// //
// // @Override
// // public AcceptCriterion getAcceptCriterion() {
// // // Drop handler is used only when dropping on layout so we
// // // accept only kings
// // return new AcceptCardWithRank(13);
// // }
// // });
// stack[i].addLayoutClickListener(new LayoutClickListener() {
//
// @Override
// public void layoutClick(LayoutClickEvent event) {
// CardStack stack = ((CardStack) event.getComponent()
// .getParent());
// if (event.getChildComponent() == null && !stack.isEmpty()) {
// return;
// }
//
// Card card = (Card) event.getChildComponent();
// if (event.isDoubleClick()) {
// stackDoubleClick(stack, card);
// } else {
// stackClick(stack, card);
//
// }
// }
// });
// }
// for (int i = 0; i < finalPile.length; i++) {
// final CardPile pile = new CardPile();
// pile.addClickListener(event -> finalPileClick(pile));
// DropTargetExtension<CardPile> dropHandler = new DropTargetExtension<>(
// pile);
// dropHandler.setDragOverCriteria(criteriaScript);
// dropHandler.addDropListener((DropEvent<CardPile> e) -> {
// Optional<Object> dragData = e.getDragData();
// if (!e.getDragData().isPresent()
// || !(dragData.get() instanceof CardInfo)) {
// return;
// }
//
// CardInfo topCard = pile.getTopCard();
// if (topCard == null) {
// return e.getTransferData();
// }
// doDrop(e);
// });
// // dropHandler.setDropCriteria();
// // new DropHandler() {
// //
// // @Override
// // public AcceptCriterion getAcceptCriterion() {
// // Card topCard = pile.getTopCard();
// //
// // if (topCard == null) {
// // return new AcceptCardWithRank(1);
// // } else {
// // AcceptCardWithRank rank = new AcceptCardWithRank(
// // topCard.getRank() + 1);
// // AcceptCardWithSuite suite = new AcceptCardWithSuite(
// // topCard.getSuite());
// // return new And(rank, suite);
// // }
// // }
// //
// // });
// // pile.setDraggable(true);
//
// finalPile[i] = pile;
// }
//
// // Layout
//
// mainLayout.addComponent(deck, createPosition(0, 0));
// mainLayout.addComponent(deckPile,
// createPosition(0, Card.WIDTH + xOffset));
// for (int i = 0; i < finalPile.length; i++) {
// mainLayout.addComponent(finalPile[i],
// createPosition(0, (i + 3) * (Card.WIDTH + xOffset)));
// }
//
// int stackY = Card.HEIGHT + yOffset;
// for (int i = 0; i < stack.length; i++) {
// mainLayout.addComponent(stack[i],
// createPosition(stackY, i * (Card.WIDTH + xOffset)));
// }
//
// deal();
// }
//
// private String createPosition(int top, int left) {
// return "top: " + top + "px;left:" + left + "px;";
// }
//
// protected void stackDoubleClick(CardStack stack, Card card) {
// if (stack.getTopCard() != card) {
// return;
// }
//
// moveToFinalPileIfPossible(stack, card);
// }
//
// protected void deckPileDoubleClick() {
// CardInfo card = deckPile.getTopCard();
// if (card == null) {
// return;
// }
//
// moveToFinalPileIfPossible(deckPile, card);
// }
//
// /**
// * Tries to move the card to a final pile.
// *
// * @param source
// * @param card
// */
// protected void moveToFinalPileIfPossible(CardContainer source, Card card) {
// // Find a suitable target among the final piles
// for (int i = 0; i < finalPile.length; i++) {
// CardPile target = finalPile[i];
// if (moveToFinalPile(source, target, card)) {
// return;
// }
//
// }
// }
//
// private boolean moveToFinalPile(CardContainer source, CardPile target,
// Card card) {
// CardInfo topCard = target.getTopCard();
//
// int allowedRank = 1;
// if (topCard != null) {
// allowedRank = topCard.getRank() + 1;
// }
//
// if (card.getRank() != allowedRank) {
// return false;
// }
//
// if (topCard != null && topCard.getSuite() != card.getSuite()) {
// return false;
// }
//
// return moveCard(source, target, card);
// }
//
// protected void finalPileClick(CardPile pile) {
// if (getSelectedCard() != null) {
// // Try to move card to pile
// CardInfo topCard = pile.getTopCard();
// int expectedRank = 1;
// if (topCard != null) {
// expectedRank = topCard.getRank() + 1;
// }
//
// if (getSelectedCard().getRank() != expectedRank) {
// return;
// }
//
// if (topCard != null
// && topCard.getSuite() != getSelectedCard().getSuite()) {
// // same suite must go on top of each other
// return;
// }
//
// // Ok to move, remove from source, add to stack
// CardContainer source = getSelectedCard().getCardContainer();
// moveCard(source, pile, getSelectedCard());
// return;
// }
//
// }
//
// private boolean moveCard(Object source, Object target, Card card) {
// if (source instanceof CardStack) {
// // Move the selected cards and all above it
// CardStack sourceStack = (CardStack) source;
//
// List<Card> cards = new ArrayList<>();
// cards.add(card);
// cards.addAll(sourceStack.getCardsAbove(card));
//
// if (!(target instanceof CardStack) && cards.size() > 1) {
// // Only CardStack accepts multiple cards
// return false;
// }
//
// for (Card c : cards) {
// sourceStack.removeCard(c);
// // Has been removed from source
// target.addCard(c);
// }
//
// deselectAll();
// if (!source.isEmpty()) {
// sourceStack.getTopCard().setBacksideUp(false);
// }
// return true;
// } else {
// if (source.removeCard(card)) {
// // Has been removed from source
// target.addCard(card);
// deselectAll();
// return true;
//
// }
// }
//
// return false;
// }
//
// /**
// * Called when one of the card stacks have been clicked. The card can be
// * null if the stack is empty.
// *
// * @param cardStack
// * @param card
// */
// protected void stackClick(CardStack cardStack, Card card) {
// if (getSelectedCard() != null
// && getSelectedCard().getCardContainer() != cardStack) {
// moveToStack(getSelectedCard(), cardStack);
//
// return;
// }
//
// if (card == null) {
// return;
// }
//
// if (card.isBacksideUp()) {
// // cannot interact with flipped cards
// return;
// }
//
// boolean wasSelected = card.isSelected();
// deselectAll();
// if (!wasSelected) {
// // Select the card and the cards below it
// card.setSelected(true);
// for (int i = cardStack.getCardPosition(card); i < cardStack
// .getNumberOfCards(); i++) {
// cardStack.getCard(i).setSelected(true);
// }
// setSelectedCard(card);
// }
// }
//
// private boolean moveToStack(final Card card, CardStack cardStack) {
// // Try to move card to stack
// Card topCard = cardStack.getTopCard();
// int expectedRank = 13;
// if (topCard != null) {
// expectedRank = topCard.getRank() - 1;
// }
//
// if (card.getRank() != expectedRank) {
// return false;
// }
//
// if (topCard != null && topCard.getSuite().getColor() == card.getSuite()
// .getColor()) {
// // same suite cannot go on top of each other
// return false;
// }
//
// // Ok to move, remove from source, add to stack
// CardContainer source = card.getCardContainer();
// moveCard(source, cardStack, card);
//
// // Make sure cards in stacks are all draggable
// // card.setDraggable(true);
// card.setDropHandler(new CardDropHandler(card));
//
// return true;
// }
//
// private class CardDropHandler implements DropHandler, Serializable {
//
// private Card card;
//
// private CardDropHandler(Card card) {
// this.card = card;
// }
//
// @Override
// public void drop(DragAndDropEvent dropEvent) {
// doDrop(dropEvent);
// }
//
// @Override
// public AcceptCriterion getAcceptCriterion() {
// ClientSideCriterion rank = new AcceptCardWithRank(
// card.getRank() - 1);
// ClientSideCriterion color = new AcceptCardWithColor(Color.BLACK);
// if (card.getSuite().getColor() == Color.BLACK) {
// color = new AcceptCardWithColor(Color.RED);
// }
//
// return new And(rank, color);
// }
// }
//
// private Card getSelectedCard() {
// return selectedCard;
// }
//
// protected void deckPileClick() {
// boolean selected = deckPile.isTopCardSelected();
// deselectAll();
//
// if (!selected) {
// deckPile.setTopCardSelected(true);
// setSelectedCard(deckPile.getTopCard());
// }
//
// }
//
//
// private boolean doDrop(DragAndDropEvent dropEvent) {
// Transferable t = dropEvent.getTransferable();
// Component source = t.getSourceComponent();
// Component target = dropEvent.getTargetDetails().getTarget();
//
// return doDrop(source, target);
// }
//
// private boolean doDrop(Component source, Component target) {
// System.out.println("doDrop: " + source + " dropped on " + target);
//
// if (source instanceof CardPile) {
// CardPile sourcePile = (CardPile) source;
// source = sourcePile.getTopCard();
// }
// if (!(source instanceof Card)) {
// return false;
// }
//
// Card sourceCard = (Card) source;
//
// if (target instanceof CardPile) {
// // Move to a final pile as deck does not support drop
// return moveToFinalPile(sourceCard.getCardContainer(),
// (CardPile) target, sourceCard);
// }
// if (target instanceof CardStack) {
// // Move to a card stack. Only accept if stack is empty.
// return moveToStack(sourceCard, (CardStack) target);
// }
// CardContainer targetContainer = null;
// if (target instanceof Card) {
// Card targetCard = (Card) target;
// targetContainer = targetCard.getCardContainer();
// }
//
// if (targetContainer != null && targetContainer instanceof CardStack) {
// // Card to stack
// CardStack targetStack = (CardStack) targetContainer;
// return moveToStack(sourceCard, targetStack);
//
// }
//
// return false;
// }
//
// }
