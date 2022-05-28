package com.pagotti.entities;

import java.awt.image.BufferedImage;

import com.pagotti.main.Game;

public class Weapon extends Entity {
	
	final static BufferedImage SPRITE = Game.spritesheet.getSprite(7*16, 0, 16, 16);
	
	public Weapon(int x, int y, int width, int height) {
		super(x, y, width, height, SPRITE);
	}

}
