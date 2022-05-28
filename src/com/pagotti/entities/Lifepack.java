package com.pagotti.entities;

import java.awt.image.BufferedImage;

import com.pagotti.main.Game;

public class Lifepack extends Entity {

	final static BufferedImage SPRITE = Game.spritesheet.getSprite(6*16, 0, 16, 16);

	public Lifepack(int x, int y, int width, int height) {
		super(x, y, width, height, SPRITE);
	}

}
