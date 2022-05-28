package com.pagotti.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.pagotti.graficos.Spritesheet;
import com.pagotti.main.Game;
import com.pagotti.world.Camera;
import com.pagotti.world.World;

public class Player extends Entity {
	
	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1, dir = right_dir;
	public double speed = 1.6;
	public double life = 100, maxLife = 100;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamage;
	public int ammo = 0;

	public boolean isDamaged = false, hasGun = false;
	private int damageFrames = 0;
	
	public boolean mouseShoot = false, jumping = false;
	public int mx, my, jumpingMaxHeigth = 50, jumpingSpeed = 2;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		this.rightPlayer = new BufferedImage[4];
		this.leftPlayer = new BufferedImage[4];
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			this.rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
			this.leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}
	}
	
	
	public void tick() {
		this.moved = false;
		if(this.right && (World.isFree((int) (x + speed), this.getY()) || this.z > 0)) {
			this.moved = true;
			this.dir = this.right_dir;
			this.x += this.speed;
		}
		else if(this.left && (World.isFree((int) (x - speed), this.getY()) || this.z > 0)) {
			this.moved = true;
			this.dir = this.left_dir;
			this.x -= this.speed;
		}
		
		if(this.up && (World.isFree(this.getX(), (int) (y - speed)) || this.z > 0)) {
			this.moved = true;
			this.y -= this.speed;
		}
		else if(this.down && (World.isFree(this.getX(), (int) (y + speed)) || this.z > 0)) {
			this.moved = true;
			this.y += this.speed;
		}
		
		if(this.moved) {
			this.frames++;
			if(this.frames == this.maxFrames) {
				this.frames = 0;
				this.index++;
				if(this.index > this.maxIndex) {
					this.index = 0;
				}
			}
		}

		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionGun();
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 30) {
				this.damageFrames = 0;
				this.isDamaged = false;
			}
		}
		
		if(mouseShoot) {			
			if(hasGun && ammo > 0) {	
				ammo--;
				
				int px = 0, py = 8;
				double angle = 0;
				if(dir == right_dir) {
					px = 18;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + 8 - Camera.x));
				} else {
					px = -8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + 8 - Camera.x));
				}

				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		if(life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}

		if(this.jumping && this.z < this.jumpingMaxHeigth) {
			this.z += this.jumpingSpeed;
		} else {
			if(this.z > 0) {
				this.z -= this.jumpingSpeed;
			}
	
			this.jumping = false;
		}
		
		updateCamera();
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	public void checkCollisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) {
				if(Entity.isColliding(this, e)) {
					hasGun = true;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}	
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Bullet) {
				if(Entity.isColliding(this, e)) {
					ammo += 10;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}	
	
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Lifepack) {
				if(Entity.isColliding(this, e)) {
					life += 10;
					if(life >= 100) {
						life = 100;
					}
					
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(isDamaged) {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - this.z, null);
			g.setColor(new Color(0, 0, 0, 100));
			g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, this.z/2, this.z/2);
			if(hasGun) {
				if(dir == left_dir) {
					g.drawImage(Entity.GUN_DAMAGE_LEFT, this.getX() - Camera.x - 8, this.getY() - Camera.y - this.z, null);
				} else {
					g.drawImage(Entity.GUN_DAMAGE_RIGHT, this.getX() - Camera.x + 8, this.getY() - Camera.y - this.z, null);
				}
			}
			return;
		}
		
		if(this.dir == this.right_dir) {
			g.drawImage(rightPlayer[this.index], this.getX() - Camera.x, this.getY() - Camera.y - this.z, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 8, this.getY() - Camera.y - this.z, null);
			}
		}
		else if(this.dir == this.left_dir) {
			g.drawImage(leftPlayer[this.index], this.getX() - Camera.x, this.getY() - Camera.y - this.z, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 8, this.getY() - Camera.y - this.z, null);
			}
		}
		
		if(this.z > 0) {
			g.setColor(new Color(0, 0, 0, 100));
			g.fillOval(this.getX() - Camera.x + 4, this.getY() - Camera.y + 14 - this.z/2, this.z/2, this.z/2);
		}
	}
}
