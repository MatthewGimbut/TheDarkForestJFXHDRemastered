package characters;


import main.GameStage;

import java.lang.*;

/**
 * characters.Enemy subclass for all enemies to be implemented
 * @author Matthew Gimbut
 *
 */
public class Enemy extends Character {

	private String customMusic;
	private boolean isActive = true; //for testing
	private boolean isSelected;
	private EnemyTypes type = EnemyTypes.TestEnemy; //placeholder for now
	//TODO have all constructors take an EnemyTypes

	public Enemy(String name, int lvl, int currentHP, int maxHP, int currentMana, int maxMana, int atk, int magic, int def, int speed) {
		super(name, lvl, currentHP, maxHP, currentMana, maxMana, atk, magic, def, speed, 0, 0);
		this.isSelected = false;
	}

	public Enemy(String name, int lvl, int currentHP, int maxHP, int currentMana, int maxMana, int atk, int magic, int def, int speed,
				 String north, String south, String east, String west) {
		super(name, lvl, currentHP, maxHP, currentMana, maxMana, atk, magic, def, speed, 0, 0);
		this.west = west;
		this.north = north;
		this.east = east;
		this.south = south;
		this.setImage(west);
		this.isSelected = false;
	}

	public Enemy(String name, int lvl, int currentHP, int maxHP, int currentMana, int maxMana, int atk, int magic, int def, int speed,
				 String north, String south, String east, String west, String customMusic) {
		super(name, lvl, currentHP, maxHP, currentMana, maxMana, atk, magic, def, speed, 0, 0);
		this.west = west;
		this.north = north;
		this.east = east;
		this.south = south;
		this.setImage(west);
		this.isSelected = false;
		this.customMusic = customMusic;
	}

	public Enemy(String name) {
		super(name , 1, 100, 100, 100, 100, 4, 4, 4, GameStage.getRandom(50), 0, 10);
		this.isSelected = false;
		this.setImage("Images\\Enemies\\Enemy0.png");
		this.customMusic = "";
	}
	
	public Enemy(String name, String image) {
		this(name);
		this.setImage(image);
		this.customMusic = "";
	}
	
	public Enemy(String name, String image, String customMusic) {
		this(name, image);
		this.customMusic = customMusic;
	}

	public Enemy(String name, int lvl, int hp, int mana, int atk, int magic, int def, int speed, String north, String south, String east,
				 String west, String customMusic) {
		super(name , lvl, hp, hp, mana, mana, atk, magic, def, speed, 0, 100);
		this.isSelected = false;
		this.west = west;
		this.north = north;
		this.east = east;
		this.south = south;
		this.setImage(west);
		this.customMusic = customMusic;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean getSelected() {
		return isSelected;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public String getCustomMusic() {
		return customMusic;
	}

	public void setCustomMusic(String customMusic) {
		this.customMusic = customMusic;
	}

	public EnemyTypes enemyType() { return this.type; }
		
}
