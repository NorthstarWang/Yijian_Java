package com.Northstar.game;

import com.Northstar.game.GamePanel;

import javax.swing.JFrame;

public class Window extends JFrame{

    public Window(){
        setTitle("YiJian");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new GamePanel(1280,1280));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
