package com.pagotti.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.pagotti.main.Game;
import com.pagotti.main.Sound;
import com.pagotti.world.Camera;
import com.pagotti.world.World;

public class Enemy extends Entity {

	private double speed = 1;
	private int maskX = 8, maskY = 8, maskW = 10, maskH = 10;
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	private int life = 2;
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;

	final static BufferedImage DAMAGE_SPRITE = Game.spritesheet.getSprite(9*16, 16, 16, 16);
	
	public Enemy(int x, int y, int width, int height) {
		super(x, y, width, height);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112+16, 16, 16, 16);
	}
	
	public void tick() {		
		int nextX = (int) (x+speed);
		int previousX =  (int) (x-speed);
		int nextY = (int) (y+speed);
		int previousY =  (int) (y-speed);
			
		if(this.isCollidingWithPlayer()) {
			if(Game.rand.nextInt(100) < 10) {
				Sound.hurtEffect.play();
				Game.player.life -= Game.rand.nextInt(5);
				Game.player.isDamaged = true;
			}
						
			return;
		}

		if((int) x < Game.player.getX() &&
			World.isFree(nextX, this.getY()) &&
			! this.isColliding(nextX, this.getY())) {
			x = nextX;
		}
		else if((int) x > Game.player.getX() &&
				World.isFree(previousX, this.getY()) &&
				! this.isColliding(previousX, this.getY())) {
			x = previousX;
		}

		if((int) y < Game.player.getY() &&
			World.isFree(this.getX(), nextY) &&
			! this.isColliding(this.getX(), nextY)) {
			y = nextY;
		}
		else if((int) y > Game.player.getY() &&
				World.isFree(this.getX(), previousY) &&
				! this.isColliding(this.getX(), previousY)) {
			y = previousY;
		}
		
		this.frames++;
		if(this.frames == this.maxFrames) {
			this.frames = 0;
			this.index++;
			if(this.index > this.maxIndex) {
				this.index = 0;
			}
		}
		
		collidingBullet();
		
		if(life <= 0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				if(Entity.isColliding(this, e)) {
					isDamaged = true;
					Game.bullets.remove(e);
					life--;
					return;
				}
			}
		}
		
		
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemy = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskW, maskH);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		if(enemy.intersects(player) && Game.player.z == 0) {
			return true;
		}
		
		return false;
	}
	
	public boolean isColliding(int xNext, int yNext) {
		Rectangle currentEnemy = new Rectangle(xNext + maskX, yNext + maskY, maskW, maskH);
		
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy enemy = Game.enemies.get(i);
			if(enemy == this) {
				continue;
			}
			
			Rectangle targetEnemy = new Rectangle(enemy.getX() + maskX, enemy.getY() + maskY, maskW, maskH);
			if(currentEnemy.intersects(targetEnemy)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			return;
		}
		
		
		g.drawImage(DAMAGE_SPRITE, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

}
