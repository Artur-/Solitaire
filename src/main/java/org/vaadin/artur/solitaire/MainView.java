package org.vaadin.artur.solitaire;

import org.vaadin.artur.github_corner.GitHubCorner;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@Theme(Lumo.class)
public class MainView extends Div {
    private GameController gameController;

    public MainView() {
        super();
        add(new GitHubCorner("Artur-", "Solitaire"));
        GameLayout gameLayout = new GameLayout();
        gameController = new GameController(gameLayout);

        Button startOver = new Button("Start over", e -> {
            gameController.deal();
        });
        Checkbox drawThree = new Checkbox("Draw three");
        drawThree.setValue(true);
        drawThree.addValueChangeListener(e -> {
            int cardsToDraw = e.getValue() ? 3 : 1;
            gameController.getConfiguration().setCardToDraw(cardsToDraw);
        });

        HorizontalLayout conf = new HorizontalLayout(startOver, drawThree);
        add(conf);
        add(gameLayout);
        gameController.deal();

    }

}
