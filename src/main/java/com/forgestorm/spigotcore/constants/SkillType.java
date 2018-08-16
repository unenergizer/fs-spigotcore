package com.forgestorm.spigotcore.constants;

public enum SkillType {
    COOKING("Cooking"),
    FARMING("Farming"),
    FISHING("Fishing"),
    MINING("Mining"),
    SMELTING("Smelting"),
    WOOD_CUTTING("Wood Cutting");

    final String skillName;

    SkillType(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillName() {
        return skillName;
    }
}