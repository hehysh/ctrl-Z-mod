package com.megacrit.cardcrawl.characters;

public class AbstractPlayer {
    public void preBattlePrep() {
    }

    public void useCard(com.megacrit.cardcrawl.cards.AbstractCard c,
                        com.megacrit.cardcrawl.monsters.AbstractMonster monster,
                        int energyOnUse) {
    }

    public void usePotion(int potionSlot) {
    }

    public void releaseCard() {
    }
}
