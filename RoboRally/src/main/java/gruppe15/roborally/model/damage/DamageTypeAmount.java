package gruppe15.roborally.model.damage;

public class DamageTypeAmount {
    private int amount;
    public final DamageTypes type;
    public DamageTypeAmount(int amount, DamageTypes type) {
        this.amount = amount;
        this.type = type;
    }

    public void setAmount(int newAmount) {
        this.amount = newAmount;
    }
    public int getAmount() {
        return this.amount;
    }
    //public abstract void applyDamage(Player player); // Logic for handling damage to {player}
}