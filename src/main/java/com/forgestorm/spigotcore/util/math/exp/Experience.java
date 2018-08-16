package com.forgestorm.spigotcore.util.math.exp;

import java.text.DecimalFormat;

@SuppressWarnings("WeakerAccess")
public abstract class Experience {
	protected int expOffSet;
	protected int minLevel = 1;
	int var1;
	double var2;
	double var3;

	public int getExpOffSet() {
        return getExperience(1, false);
    }

	/**
	 * This will return the experience needed for a given level.
	 * @param level The level we want to get an experience amount for.
	 * @return The experience needed to obtain this level.
	 */
	public int getExperience(int level) {
		return getExperience(level, false);
	}
	
	/**
	 * This will return the experience needed for a given level.
	 * @param level The level we want to get an experience amount for.
	 * @param getOffSet Includes the experience offset in this calculation.
	 * @return The experience needed to obtain this level.
	 */
	public int getExperience(int level, boolean getOffSet) {
		int points = 0;
		int output;

		int maxLevel = 100;
		for (int lvl = 1; lvl <= maxLevel; lvl++) {
			points += Math.floor(var1 * Math.pow(2, lvl / var2));
			
			output = (int) Math.floor(points / var3);
			
			if (lvl == level) {
				return getOffSet ? output : output - expOffSet;
			}
		}
		return 0;
	}
	
	/**
	 * This will return the players level based off their experience.
	 * @param exp The players current experience.
	 * @return Returns a level based on the exp parameter.
	 */
	public int getLevel(long exp) {
		for (int i = 1; i <= 100; i++) {
			
			if (exp >= getExperience(i) && exp < getExperience(i + 1)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Gets the percentage required to get to the next level.
	 * @param experience The players current experience.
	 * @return Returns the players percentage left to level.
	 */
	public double getPercentToNextLevel(long experience) {
		int currentLVL = getLevel(experience);
		int nextLVLxp = getExperience(currentLVL + 1);
		double difference = nextLVLxp - experience;
		return (difference / (nextLVLxp - getExperience(currentLVL, true))) * 100;
	}
	
	/**
	 * This will calculate a percentage for the players experience bar.
	 * @param experience The players current experience.
	 * @return Returns a number between 1 and 100.
	 */
	public double getPercentToLevel(long experience) {
		int currentLVL = getLevel(experience);
		int nextLVLxp = getExperience(currentLVL + 1);
		float difference = nextLVLxp - experience;
		float percent = difference / (nextLVLxp - getExperience(currentLVL, true));
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.parseDouble(df.format((1 - percent) * 100));
	}
	
	/**
	 * This will calculate a percentage for the players experience bar.
	 * @param experience The players current experience.
	 * @return Returns a number between 0 and 1.
	 */
	public float getBarPercent(long experience) {
		int currentLVL = getLevel(experience);
		int nextLVLxp = getExperience(currentLVL + 1);
		float difference = nextLVLxp - experience;
		float percent = difference / (nextLVLxp - getExperience(currentLVL, true));
		return 1 - percent;
	}
}
