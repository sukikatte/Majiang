package Dice;

import java.util.Random;

// Dingzhuang


public class Dice {
    // Roll of the dice

    private int number;
    // constructor, which generates a new die by default

    public Dice() {
        this.newDice();
    }// Automatically roll once when creating the die

    // Get the points of the dice
   public int getNumber() {
        return this.number;
    }
    // Create a new die, actually roll the die again

    public void newDice() {
        Random random = new Random();
        this.number = random.nextInt(6) + 1; // Generate a random number between 1 and 6
    }
}
