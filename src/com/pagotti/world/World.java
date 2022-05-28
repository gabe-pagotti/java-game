package com.pagotti.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.pagotti.entities.*;
import com.pagotti.graficos.Spritesheet;
import com.pagotti.main.Game;

public class World {
	
	public static int WIDTH, HEIGHT;
	public static Tile[] tiles;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			int[] pixels = new int[WIDTH * HEIGHT];
			tiles = new Tile[WIDTH * HEIGHT];
			
			map.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
			
			for(int xx = 0; xx < WIDTH; xx++) {
				for(int yy = 0; yy < HEIGHT; yy++) {
					int pixelAtual = pixels[xx + (yy * WIDTH)];

					// Chão
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					
					if(pixelAtual == 0xFF000000) {
						// Chão
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16, yy * 16, Tile.TILE_FLOOR);
					}
					else if (pixelAtual == 0xFFFFFFFF) {
						// Parede
						tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16, yy * 16, Tile.TILE_WALL);
					}
					else if (pixelAtual == 0xFF0000FF) {
						// Jogador
						Game.player.setX(xx * 16);
						Game.player.setY(yy * 16);
					}
					else if (pixelAtual == 0xFFFF0000) {
						// Inimigo
						Enemy enemy = new Enemy(xx * 16, yy * 16, 16, 16);
						Game.entities.add(enemy);
						Game.enemies.add(enemy);
					}
					else if (pixelAtual == 0xFF00FFFF) {
						// Arma
						Game.entities.add(new Weapon(xx * 16, yy * 16, 16, 16));
					}
					else if (pixelAtual == 0xFF00FF00) {
						// Vida
						Game.entities.add(new Lifepack(xx * 16, yy * 16, 16, 16));
					}
					else if (pixelAtual == 0xFFFFFF00) {
						// Munição
						Game.entities.add(new Bullet(xx * 16, yy * 16, 16, 16));
					}
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFree(int xNext, int yNext) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;
		
		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;
		
		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		
		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		
		return ! (tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
				  tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
				  tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
				  tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile
				 );
	}
	
	public static void restartGame(String level) {
        Game.entities = new ArrayList<Entity>();
        Game.enemies = new ArrayList<Enemy>();
        Game.spritesheet = new Spritesheet("/spritesheet.png");
        Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
        Game.entities.add(Game.player);
        Game.world = new World("/"+level);
	}
	
	public void render(Graphics g) {
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
					continue;
				}
				
				Tile tile = tiles[xx + (yy * WIDTH)];
				tile.render(g);
			}
		}	
	}
}
