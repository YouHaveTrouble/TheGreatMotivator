package me.youhavetrouble.thegreatmotivator;

public class TGMPlayer {

    private double balance;

    TGMPlayer(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void add(double toAdd) {
        this.balance += toAdd;
    }

    public void subtract(double toSubtract) {
        this.balance -= toSubtract;
    }

    public boolean has(double amount) {
        return this.balance >= amount;
    }
}
