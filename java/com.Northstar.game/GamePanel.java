package com.Northstar.game;

import com.Northstar.game.States.GameStateManager;
import com.Northstar.game.Util.KeyHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public class GamePanel extends JPanel implements Runnable {

    public static int width;
    public static int height;

    public static int frame;
    public boolean running = false;
    private Thread thread;
    private BufferedImage img;
    private Graphics2D g;

    private KeyHandler key;
    private GameStateManager gsm;

    public GamePanel(int width, int height) {
        //Set dimensions of the window
        GamePanel.width = width;
        GamePanel.height = height;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify() {
        //Add thread to run
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this, "GameThread");
            thread.start();
        }
    }

    public void init() throws FileNotFoundException, URISyntaxException {
        //Set flag for while loop
        running = true;
        //Set background for GamePanel
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();
        //Initialise event handlers
        key = new KeyHandler(this);
        gsm = new GameStateManager();
    }


    public void run() {
        try {
            init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //Define check rate of the game
        final double GAME_HERTZ = 60.0;
        final double TBU = 1000000000 / GAME_HERTZ;
        double lastUpdateTime = System.nanoTime();
        double lastRenderTime;

        int frameCount = 0;
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);
        frame = 0;

        final double TARGET_FPS = 60;
        final double TTBR = 1000000000 / TARGET_FPS;


        //While true
        while (running) {

            double now = System.nanoTime();

            //If nano time does not match(latency occur), update immediately
            while (now - lastUpdateTime > TBU) {
                try {
                    update();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    input(key);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                lastUpdateTime += TBU;
                draw();
            }

            //Every time frame increase, the graphics draw, time iterate
            try {
                input(key);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            frameCount++;
            try {
                render();
            } catch (IOException e) {
                e.printStackTrace();
            }
            draw();
            lastRenderTime = now;

            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                if (frameCount != frame) {
                    frame = frameCount;
                }
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while (now - lastRenderTime < TTBR && now - lastUpdateTime < TBU) {
                //If still too soon, wait
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                now = System.nanoTime();
            }
        }
    }

    private void input(KeyHandler key) throws FileNotFoundException, URISyntaxException {
        gsm.input(key);
    }

    private void render() throws IOException {
        //Set background image
        BufferedImage img = ImageIO.read(new File("Resources/Tile/Map.png"));
        //Set image dimensions
        g.drawImage(img, 0, 0, width, height, null);
        gsm.render(g);
    }

    private void update() throws IOException, URISyntaxException {
        //Update game state
        gsm.update();
    }

    private void draw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(img, 0, 0, width, height, null);
        g2.dispose();
    }
}
