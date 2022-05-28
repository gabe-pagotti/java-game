package com.pagotti.entities;

import java.awt.image.BufferedImage;

import com.pagotti.main.Game;

public class Bullet extends Entity {

	final static BufferedImage SPRITE = Game.spritesheet.getSprite(6*16, 16, 16, 16);
	
	public Bullet(int x, int y, int width, int height) {
		super(x, y, width, height, SPRITE);
	}

}
