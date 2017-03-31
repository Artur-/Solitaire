package org.vaadin.artur.solitaire;

import java.io.Serializable;

public class Configuration implements Serializable {

    private int cardToDraw = 3;

    public int getCardsToDraw() {
        return cardToDraw;
    }

    public void setCardToDraw(int cardToDraw) {
        this.cardToDraw = cardToDraw;
    }

}
