package de.lgs.prin;

public class Field {
    private Fieldtype type;
    private int amount;
    public void executeAction(Player player) {
        player.addMoney(amount);
        System.out.printf("Execute %d on player %s\n", this.amount, player.getName());
    }
    public Field(Fieldtype type, int amount) {this.type = type; this.amount = amount;}
}
