package mapping;

import sprites.Exit;
import sprites.LowerLayer;
import sprites.PlayerSprite;
import sprites.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * Container class for the Sprites on the map.
 * Sorts all of the layers on its own, offloading that work from the GamePane class.
 *
 * @author Matthew Gimbut
 *
 */

public class MapContainer {

    private ArrayList<Sprite> mapItems, underLayer, overLayer, collisions;
    private String idName;
    private boolean hostile;
    private JSONMapParser map;
    private JSONMapTemplate template;
    private String location;

    /**
     * Constructor for the MapContainer class that loads a new file on creation.
     * @param player The main player object.
     * @param location The file location of the new file to be loaded in to the game.
     */
    public MapContainer(PlayerSprite player, String location) {
        System.out.println(location);
        this.location = location;
        map = new JSONMapParser(player);
        loadNewFile(location);
        updateLayers();
    }

    /**
     * Sorts important Sprite objects into separate collections to make it easier to check for collisions.
     * Makes a call to the garbage collector afterwards because this method is frequently used when
     * loading new maps, which causes a lot of objects to be useless.
     */
    private void updateLayers() {
        underLayer = (ArrayList<Sprite>) mapItems.stream()
                .filter(sprite -> sprite instanceof LowerLayer)
                .filter(Sprite::isVisible)
                .collect(Collectors.toList());

        overLayer = (ArrayList<Sprite>) mapItems.stream()
                .filter(sprite -> !(sprite instanceof LowerLayer))
                .filter(Sprite::isVisible)
                .collect(Collectors.toList());

        collisions = (ArrayList<Sprite>) mapItems.stream()
                .filter(Sprite::isObstacle)
                .collect(Collectors.toList());

        Runtime.getRuntime().gc();
    }

    /**
     * Loads a new file using the JSONMapParser to create a randomized map.
     * @param newFile The new file to be written to.
     * @param currentFile The current map file that the player is in.
     * @param prevExit The Exit object that the player came from.
     */
    public void randomize(String newFile, String currentFile, Exit prevExit) {
        try {
            map.generateRandomMap(newFile, currentFile, prevExit);
            template = map.parseMap(newFile);
            idName = template.getId();
            location = newFile;
            mapItems = template.getMapItems();
            updateLayers();
        } catch (IOException e) {
            System.out.println("Error in creating random file!");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads a new map file.
     * @param loc The location of the new map file.
     */
    public void loadNewFile(String loc)  {
        template = map.parseMap(loc);
        idName = template.getId();
        location = loc;
        mapItems = template.getMapItems();
        updateLayers();
    }

    /**
     * Adds a sprite to the current map iff it doesn't intersect anything and updates layers.
     * @param toAdd The Sprite object to add to the game.
     */
    public void add(Sprite toAdd) {
        boolean found = false;
        for(Sprite sprite : mapItems) {
            if(sprite.getBounds().intersects(toAdd.getBounds())) {
                found = true;
            }
        }
        if(!found) {
            mapItems.add(toAdd);
            updateLayers();
        }
    }

    /**
     * Removes a Sprite from the map.
     * @param toRemove The Sprite object to remove from the map.
     */
    public void removeSprite(Sprite toRemove) {
        try {
            mapItems.remove(toRemove);
            updateLayers();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error in removing item.");
        }
    }

    public ArrayList<Sprite> getMapItems() {
        return mapItems;
    }

    public void setMapItems(ArrayList<Sprite> mapItems) {
        this.mapItems = mapItems;
        updateLayers();
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    public ArrayList<Sprite> getUnderLayer() {
        return underLayer;
    }

    public void setUnderLayer(ArrayList<Sprite> underLayer) {
        this.underLayer = underLayer;
    }

    public ArrayList<Sprite> getOverLayer() {
        return overLayer;
    }

    public void setOverLayer(ArrayList<Sprite> overLayer) {
        this.overLayer = overLayer;
    }

    public ArrayList<Sprite> getCollisions() {
        return collisions;
    }

    public void setCollisions(ArrayList<Sprite> collisions) {
        this.collisions = collisions;
    }

    public String getLocation() {
        return this.location;
    }

}
