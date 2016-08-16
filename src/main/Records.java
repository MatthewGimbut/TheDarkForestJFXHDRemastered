package main;

import java.io.Serializable;

public class Records implements Serializable {

	private int enemiesKilled;
	private int totalXP;
	private int totalGoldCollected;
	private int areasExplored;
	
	public Records() {
		enemiesKilled = 0;
		totalXP = 0;
		totalGoldCollected = 0;
		areasExplored = 0;
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public void incrementEnemiesKilled() {
		this.enemiesKilled++;
	}

	public int getTotalXP() {
		return totalXP;
	}

	public void setTotalXP(int totalXP) {
		this.totalXP = totalXP;
	}
	
	public void increaseTotalXP(int xp) {
		this.totalXP += xp;
	}

	public int getTotalGoldCollected() {
		return totalGoldCollected;
	}

	public void setTotalGoldCollected(int totalGoldCollected) {
		this.totalGoldCollected = totalGoldCollected;
	}
	
	public void increaseTotalGold(int gold) {
		this.totalGoldCollected += gold;
	}

	public int getAreasExplored() {
		return areasExplored;
	}

	public void incrementAreasExplored() {
		this.areasExplored++;
	}
	
}
