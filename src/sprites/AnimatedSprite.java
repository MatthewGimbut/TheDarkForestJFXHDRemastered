package sprites;

import javafx.scene.canvas.GraphicsContext;

public class AnimatedSprite extends Sprite {

    private String[] locations;
    private int currentIndex;

    public AnimatedSprite(int x, int y) {
        super(x, y);
        currentIndex = 0;
    }

    public AnimatedSprite(int x, int y, String[] image) {
        this(x, y);
        this.locations = image;
    }

    @Override
    public void render(GraphicsContext gc) {
        setImage(locations[++currentIndex%locations.length]);
        gc.drawImage(image, x, y);
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }
}
