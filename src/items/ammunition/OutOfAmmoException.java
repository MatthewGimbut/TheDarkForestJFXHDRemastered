package items.ammunition;

/**
 * Created by Matthew on 10/8/2016.
 */
public class OutOfAmmoException extends Exception {

    public OutOfAmmoException(String message) {
        super(message);
    }
}
