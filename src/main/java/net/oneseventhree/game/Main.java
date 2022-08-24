package net.oneseventhree.game;

public class Main
{
    public static void main(String[] args)
    {
        new Thread(new OneSevenThree(), "OneSevenThree-Main").start();
    }
}
