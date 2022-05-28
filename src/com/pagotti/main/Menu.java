package com.pagotti.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Menu {
	
	public String[] options = {"novo jogo", "carregar jogo", "sair"};
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public boolean up, down, enter;
	public boolean pause = false;
	
	public void tick() {
		if (up) {
			up = false;
			currentOption--;
			if(this.currentOption < 0) {
				this.currentOption = this.maxOption;
			}
		}

		if (this.down) {
			down = false;
			currentOption++;
			if(this.currentOption > this.maxOption) {
				this.currentOption = 0;
			}
		}
		
		if(this.enter) {
			enter = false;
			if(options[currentOption] == "novo jogo" || this.options[this.currentOption] == "continuar") {
				Game.gameState = "NORMAL";
				this.pause = false;
			} else if(options[currentOption] == "sair") {
				System.exit(1);
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.yellow);
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString("> ZELDA CLONE <", (Game.WIDTH*Game.SCALE)/2 - 155, (Game.HEIGHT*Game.SCALE)/2 - 160);
		
		// OPÇÕES
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 24));
		if(this.pause == false) {
			g.drawString("Novo jogo", (Game.WIDTH*Game.SCALE)/2 - 65, (Game.HEIGHT*Game.SCALE)/2 - 60);
		} else {
			g.drawString("Continuar", (Game.WIDTH*Game.SCALE)/2 - 60, (Game.HEIGHT*Game.SCALE)/2 - 60);
		}
		
		g.drawString("Carregar jogo", (Game.WIDTH*Game.SCALE)/2 - 85, (Game.HEIGHT*Game.SCALE)/2 - 20);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE)/2 - 30, (Game.HEIGHT*Game.SCALE)/2 + 20);
		
		if(options[currentOption] == "novo jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 - 90, (Game.HEIGHT*Game.SCALE)/2 - 60);
		} else if(options[currentOption] == "carregar jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 - 110, (Game.HEIGHT*Game.SCALE)/2 - 20);
		} else {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 - 55, (Game.HEIGHT*Game.SCALE)/2 + 20);
		}
	}
}
