package main;

import sprites.PlayerSprite;

import java.io.*;
import java.util.ArrayList;

public interface SaveManager {

	public final static boolean DO_NOT_APPEND = false;
	
	public static void serialize(String currentMapLocation, PlayerSprite currentPlayer, String backgroundId) {
		currentPlayer.getPlayer().prepareSerializeQuests(); //serializes the quests
		try(FileOutputStream fs = new FileOutputStream("Saves\\save01.ser", DO_NOT_APPEND);
			ObjectOutputStream os = new ObjectOutputStream(fs)) {
			ArrayList<Object> items = new ArrayList<Object>();
			items.add(0, currentPlayer);
			items.add(1, currentMapLocation);
			items.add(2, backgroundId);
			System.out.println(currentMapLocation);
			os.writeObject(items);
		} catch (IOException e) {
			GameStage.logger.error(e);
		} catch (Exception e) {
			GameStage.logger.error(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> deserialize() {
		try(FileInputStream fs = new FileInputStream("Saves\\save01.ser");
			ObjectInputStream os = new ObjectInputStream(fs)) {
			ArrayList<Object> mapstuff = (ArrayList<Object>) os.readObject();

			((PlayerSprite) mapstuff.get(0)).getPlayer().deserializeQuests(); //deserialize the quests


			return mapstuff;
		} catch (IOException e) {
			GameStage.logger.error(e);
		} catch (Exception e) {
			GameStage.logger.error(e);
		}
		return null;
	}
	
}
