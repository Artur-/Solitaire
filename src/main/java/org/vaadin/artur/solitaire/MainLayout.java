package org.vaadin.artur.solitaire;

import com.vaadin.ui.MenuBar.MenuItem;

public class MainLayout extends MainLayoutDesign {
    private GameController gameController;

    public MainLayout() {
        super();

        gameController = new GameController(gameLayout);
        MenuItem startOver = menu.getItems().get(0).getChildren().get(0);
        startOver.setCommand(e -> {
            gameController.deal();
        });

        MenuItem drawThree = menu.getItems().get(1).getChildren().get(0);
        drawThree.setCheckable(true);
        drawThree.setChecked(true);
        drawThree.setCommand(e -> {
            if (e.isChecked()) {
                gameController.getConfiguration().setCardToDraw(3);
            } else {
                gameController.getConfiguration().setCardToDraw(1);
            }
        });

        gameController.deal();

    }

}
