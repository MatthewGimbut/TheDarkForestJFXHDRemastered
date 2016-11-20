package main;

import sprites.PlayerSprite;

import java.io.*;
import java.util.ArrayList;

public interface SaveManager {

	public final static boolean DO_NOT_APPEND = false;
	
	public static void serialize(String currentMapLocation, PlayerSprite currentPlayer, String backgroundId, String file) {
		currentPlayer.getPlayer().prepareSerializeQuests(); //serializes the quests
		try(FileOutputStream fs = new FileOutputStream(file, DO_NOT_APPEND);
			ObjectOutputStream os = new ObjectOutputStream(fs)) {
			ArrayList<Object> items = new ArrayList<Object>();
			items.add(0, currentPlayer);
			items.add(1, currentMapLocation);
			items.add(2, backgroundId);
			os.writeObject(items);
		} catch (IOException e) {
			GameStage.logger.error(e);
			GameStage.logger.error("IOException while serializing save.");
			e.printStackTrace();
		} catch (Exception e) {
			GameStage.logger.error(e);
			GameStage.logger.error("General while serializing save.");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> deserialize(String file) {
		try(FileInputStream fs = new FileInputStream(file);
			ObjectInputStream os = new ObjectInputStream(fs)) {
			ArrayList<Object> mapstuff = (ArrayList<Object>) os.readObject();

			return mapstuff;
		} catch (IOException e) {
			GameStage.logger.error(e);
			GameStage.logger.error("IOException while deserializing save.");
		} catch (Exception e) {
			GameStage.logger.error(e);
			GameStage.logger.error("General while deserializing save.");
		}
		return null;
	}
	
}
