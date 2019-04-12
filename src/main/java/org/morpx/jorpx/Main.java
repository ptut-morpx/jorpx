package org.morpx.jorpx;

import org.morpx.jorpx.game.GUI;
import org.morpx.jorpx.game.Morpx;

public class Main {
    public static void main(String... args) {
    	Morpx game=new Morpx();
    	GUI gui=new GUI(game);
    	gui.show();
    }
}
