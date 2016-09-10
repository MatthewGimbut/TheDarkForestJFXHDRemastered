package quests;

/**
 * quests.Task represents an individual Task apart of a Quest.
 * Abstract class with children quests.KillTask, quests.GatherTask, quests.ExploreTask,
 * quests.InteractTask.
 * @author Sean Zimmerman
 */
public abstract class Task {

    private boolean isComplete = false;
    protected String description;
    protected Trigger trigger;

    //check if the task is complete, is so switch isComplete and return true
    public abstract boolean checkTaskComplete();

    public boolean isComplete() { return this.isComplete; }

    public void setComplete(boolean isComplete) { this.isComplete = isComplete; }

    public String toString() { return this.description; }

    public Trigger getTrigger() { return this.trigger; }
}
