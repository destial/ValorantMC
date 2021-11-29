package xyz.destiall.mc.valorant.api.match;

public class Economy {
    private Integer balance;
    public Economy() {
        balance = 0;
    }
    public Economy(Integer startingBalance) {
        balance = startingBalance;
    }

    public Integer getBalance() {
        return balance;
    }

    public Economy remove(Integer amount) {
        balance -= amount;
        return this;
    }

    public Economy add(Integer amount) {
        balance += amount;
        return this;
    }
}
