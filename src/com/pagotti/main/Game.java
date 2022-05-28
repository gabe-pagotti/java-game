package com.pagotti.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.pagotti.entities.BulletShoot;
import com.pagotti.entities.Enemy;
import com.pagotti.entities.Entity;
import com.pagotti.entities.Player;
import com.pagotti.graficos.Spritesheet;
import com.pagotti.graficos.UI;
import com.pagotti.world.Camera;
import com.pagotti.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Thread thread;
    private boolean isRunning;
    public static final int SCALE = 3;
    private int CUR_LEVEL = 1, MAX_LEVEL = 2;
    private BufferedImage image;

	public static JFrame frame;
    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static List<Entity> entities;
    public static List<Enemy> enemies;
    public static List<BulletShoot> bullets;
    public static Spritesheet spritesheet; 
    public static World world;
    public static Player player;
    
    public static Random rand;
    public UI ui;
    
    public static String gameState = "MENU";
    private boolean showMessageGameOver = true;
    private boolean restartGame = false;
    private int framesGameOver = 0;
    
    public Menu menu;
    
    public Game()
    {
    	Sound.musicBackGround.loop();
    	rand = new Random();
    	addKeyListener(this);
    	addMouseListener(this);
        this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        this.initFrame();
        // Inicializando objetos
        this.ui = new UI();
        this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        entities = new ArrayList<Entity>();
        enemies = new ArrayList<Enemy>();
        bullets = new ArrayList<BulletShoot>();
        	
        spritesheet = new Spritesheet("/spritesheet.png");
        player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
        entities.add(player);
        world = new World("/level1.png");

        menu = new Menu();
    }

    public void initFrame()
    {
        frame = new JFrame("Game #1");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start()
    {
        this.thread = new Thread(this);
        this.isRunning = true;
        thread.start();
    }

    public synchronized void stop()
    {
        this.isRunning = false;

        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        Game game = new Game();
        game.start();
    }

    public void tick()
    {
    	if (gameState == "NORMAL") {
    		this.restartGame = false;
    		
	    	for(int i = 0; i < entities.size(); i++) {
	    		Entity e = entities.get(i);
	    		e.tick();
	    	}
	    	
	    	for(int i = 0; i < bullets.size(); i++) {
	    		bullets.get(i).tick();
	    	}
	    	
	    	if(enemies.size() == 0) {
	    		CUR_LEVEL++;
	    		if(CUR_LEVEL > MAX_LEVEL) {
	    			CUR_LEVEL = 1;
	    		}
	    		
	    		String newWorld = "level"+CUR_LEVEL+".png";
	    		World.restartGame(newWorld);
	    	}
    	} else if(gameState == "GAME_OVER") {
    		this.framesGameOver++;
    		if(this.framesGameOver == 30) {
    			this.framesGameOver = 0;
    			if(this.showMessageGameOver) {
    				this.showMessageGameOver = false;
    			} else {
    				this.showMessageGameOver = true;
    			}
    		}
    		
    		if(restartGame) {
    			this.restartGame = false;
    			gameState = "NORMAL";
    			CUR_LEVEL = 1;
	    		String newWorld = "level"+CUR_LEVEL+".png";
	    		World.restartGame(newWorld);
    		}
    	} else if(gameState == "MENU") {
    		menu.tick();
    	}
    	
    }

    public void render()
    {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //Graphics2D g2 = (Graphics2D) g;
        world.render(g);
    	for(int i = 0; i < entities.size(); i++) {
    		Entity e = entities.get(i);
    		e.render(g);
    	}
    	
    	for(int i = 0; i < bullets.size(); i++) {
    		bullets.get(i).render(g);
    	}
    	
    	ui.render(g);
    	
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(this.image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
        g.setFont(new Font("arial", Font.BOLD, 20));
        g.setColor(Color.white);
        g.drawString("Munição: " + player.ammo, 600, 20);

    	if(gameState == "GAME_OVER") {
    		Graphics2D g2 = (Graphics2D) g;
    		g2.setColor(new Color(0, 0, 0, 200));
    		g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
            g2.setFont(new Font("arial", Font.BOLD, 40));
            g2.setColor(Color.white);
            g2.drawString("Game Over", WIDTH+15, HEIGHT+80);
            if(showMessageGameOver) {
            	g2.drawString("> Pressione enter para reiniciar <", WIDTH-175, HEIGHT+120);
            }
    	} else if (gameState == "MENU") {
    		menu.render(g);
    	}
    	
        bs.show();
    }

    public void run()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        double timer = System.currentTimeMillis();
        int frames = 0;
        
        requestFocus();
        
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                this.tick();
                this.render();
                delta--;
                frames++;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }

        this.stop();
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			if(gameState == "MENU") {
				menu.up = true;
			}
			
			player.up = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			if(gameState == "MENU") {
				menu.down = true;
			}
			
			player.down = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "MENU") {
				menu.enter = true;
				return;
			}
			
			if(gameState == "GAME_OVER") {
				this.restartGame = true;
				return;
			}
			
			player.jumping = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(gameState == "GAME_OVER") {
				this.restartGame = true;
			}
			
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(gameState == "NORMAL") {
				Game.gameState = "MENU";
				menu.pause = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = e.getX()/3;
		player.my = e.getY()/3;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		player.mouseShoot = false;
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
