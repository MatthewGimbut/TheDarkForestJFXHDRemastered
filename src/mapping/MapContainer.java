package mapping;

import sprites.Exit;
import sprites.LowerLayer;
import sprites.PlayerSprite;
import sprites.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MapContainer {

    private ArrayList<Sprite> mapItems, underLayer, overLayer, collisions;
    private String idName;
    private boolean hostile;
    private JSONMapParser map;
    private JSONMapTemplate template;
    private String location;

    public MapContainer(PlayerSprite player, String location) {
        System.out.println(location);
        this.location = location;
        map = new JSONMapParser(player);
        try {
            loadNewFile(location);
        } catch (IOException e) {
            System.out.println("Error reading file");
        }

        updateLayers();
    }

    public void updateLayers() {
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

    public void randomize(String newFile, String currentFile, Exit prevExit) {
        try {
            map.generateRandomMap(newFile, currentFile, prevExit);
            template = map.parseMap(newFile);
            idName = template.getId();
            location = newFile;
            mapItems = template.getMapItems();
            updateLayers();
        } catch (IOException e) {
            System.out.println("couldn't read random file");
        }
    }

    public void loadNewFile(String loc) throws IOException {
        template = map.parseMap(loc);
        idName = template.getId();
        location = loc;
        mapItems = template.getMapItems();
        updateLayers();
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

    public void addSprite(Sprite toAdd) {
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

    public void removeSprite(Sprite toRemove) {
        try {
            mapItems.remove(toRemove);
            updateLayers();
        } catch (Exception e) {
            System.out.println("Item not found");
        }
    }
}
