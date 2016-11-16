package battle;

import characters.Character;
import characters.Enemy;
import main.GameStage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * battle.BattleHandler is the controller for the Battle System.
 * Handles attacks between characters, and deals with death.
 * All methods are static.
 *
 * @author Sean Zimmerman
 */
public class BattleHandler {

    /**
     * Have the Attacker attack the Defender.
     * @param attacker The attacking character
     * @param defender The defending character
     */
    public static void physicalAttack(Character attacker, Character defender) {
        int damage = BattleHandler.calculateDamage(attacker, defender);
        defender.modifyCurrentHP(-damage);

        if(defender.getCurrentHP() == 0) {
            // DEAD BITCH
            System.out.println(defender.getName() + " WAS BRUTALLY MURDERED");
            if(defender instanceof Enemy) { // enemy died, do shit
                GameStage.gamePane.enemyKilled((Enemy)defender);
            } else { // was the player
                // TODO player death (pop up a game over screen I guess? And have options for main menu or load game or etc)
                System.out.println("Shit son you suck dick");
            }
        }
    }

    /**
     * Calculate the damage to be done to the defender.
     * @param attacker The attacking character
     * @param defender The defending character
     * @return The damage to be dealt
     */
    private static int calculateDamage(Character attacker, Character defender) {
        double modifier = calculateModifier(attacker, defender);
        double a = attacker.getAtk();
        double b = defender.getDef();
        double c = (2.0*attacker.getLvl() + 5.0) / 10.0;
        double e = (1.0*Math.pow(a, 2)) / (0.5 * Math.pow(b, 2));
        double r = (ThreadLocalRandom.current().nextInt(85, 101) * 1.0) / 100.0;

        double damage = 1.5*c*e*modifier*r;

        // casting to an int drops the decimal place, so adding .5 then casting is the same as rounding to the nearest integer
        return (int)(damage + 0.5);
    }

    /**
     * Calculates the modification double which modifies the attack.
     * @param attacker The attacking character
     * @param defender The defending character
     * @return The damage modifier
     */
    private static double calculateModifier(Character attacker, Character defender) {
        double result = 1.0;

        // Diminish Damage Done if the level difference is great enough
        int deltaLevel = defender.getLvl() - attacker.getLvl();
        if(deltaLevel >= 5) { // this means if the player is overleveled they will take less damage
            result *= Math.pow(0.88, deltaLevel); // Exponential drop off, makes it possible to be underleveled, but much more difficult
        }

        return result; // TODO (depending on difficult selected, change this (if enemy is attacking, 0.75 easy, .85 medium, 1.0 hard, 1.2 insanity))
    }
}
