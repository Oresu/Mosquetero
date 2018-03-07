package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//Titulo de la ventana del juego
		config.title="El Magnifico Mosqueperro";
		//Ancho de la ventana
		config.width=800;
		//Alto de la ventana
		config.height=480;
		//Lanza el juego version escritorio
		new LwjglApplication(new MyGdxGame(), config);
	}
}
