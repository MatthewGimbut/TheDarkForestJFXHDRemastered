package quests.task;

import quests.trigger.Trigger;

import java.io.Serializable;

/**
 * quests.task.Task represents an individual Task apart of a Quest.
 * Abstract class with children quests.task.KillTask, quests.quests.task.Task.GatherTask, quests.ExploreTask,
 * quests.quests.task.Task.InteractTask.
 * @author Sean Zimmerman
 */
public abstract class Task implements Serializable {

    private static final long serialVersionUID = -254610042760779131L;
    private boolean isComplete = false;
    protected String description;
    protected Trigger trigger;

    /**
     * Checks if the task is complete, if so switch isComplete and return true.
     * @return if the Task is complete
     */
    public abstract boolean checkTaskComplete();

    public boolean isComplete() { return this.isComplete; }

    public void setComplete(boolean isComplete) { this.isComplete = isComplete; }

    public String toString() { return this.description; }

    public Trigger getTrigger() { return this.trigger; }
}
