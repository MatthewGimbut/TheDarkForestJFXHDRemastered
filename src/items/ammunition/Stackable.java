package items.ammunition;

/**
 * Created by Matthew on 10/8/2016.
 */
public interface Stackable  {
    public abstract void addToStack(int add);
    public abstract void removeFromStack(int remove);
    public abstract void combine(Stackable s);
    public abstract int getCount();
}
