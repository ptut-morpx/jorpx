package org.morpx.jorpx;

import java.io.File;
import java.io.IOException;

import org.morpx.jorpx.ai.MinmaxAI;
import org.morpx.jorpx.ai.RandomAI;
import org.morpx.jorpx.config.Config;
import org.morpx.jorpx.game.GUI;
import org.morpx.jorpx.game.Morpx;

public class Main {
	public static void main(String... args) throws IOException {
		Config conf=Config.readConfig(new File("jorpx.ini"));
		conf.setIfDefault("p1", "kind", "human");
		conf.setIfDefault("p-1", "kind", "random");
		conf.writeConfig(new File("jorpx.ini"));
		
		Morpx game=new Morpx();
		GUI gui=new GUI(game);
		
		for(int i:new int[] {1, -1}) {
			switch(conf.get("p"+i, "kind")) {
				case "human":
					gui.addPlayer(i);
					break;
				
				case "random":
					RandomAI.INSTANCE.install(game, i);
					break;
					
				case "minmax": {
					String path=conf.get("p"+i, "path");
					int depth=Integer.parseInt(conf.get("p"+i, "depth"));
					int[] c=new int[4];
					for(int j=0; j<4; j++) c[j]=Integer.parseInt(conf.get("p"+i, "c"+j));
					MinmaxAI ai=new MinmaxAI(path, depth, c[0], c[1], c[2], c[3]);
					ai.install(game, i);
					break;
				}
			}
		}
		
		game.start();
		gui.show();
	}
}
