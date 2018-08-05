package com.forgestorm.spigotcore.constants;

public enum ProfessionType {
    COOKING("Cooking"),
    FARMING("Farming"),
    FISHING("Fishing"),
    MINING("Mining"),
    SMELTING("Smelting"),
    WOOD_CUTTING("Wood Cutting");

    String professionName;

    ProfessionType(String professionName) {
        this.professionName = professionName;
    }

    public String getProfessionName() {
        return professionName;
    }
}