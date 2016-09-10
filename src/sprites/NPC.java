package sprites;

import quests.QuestHandler;
import quests.Trigger;

import java.util.List;
import java.util.LinkedList;

public class NPC extends Sprite {
	
	private characters.Character chara;
	private List<Trigger> questActivationTriggers;
	private List<Trigger> questTriggers;
	private Trigger lastActivatedTrigger = null;

	public NPC(int x, int y, characters.Character chara, String[] message) {
		super(x, y);
		this.chara = chara;
		this.message = message;
		questActivationTriggers = new LinkedList<>();
		this.questTriggers = new LinkedList<>();
		initNPC();
	}

	/**
	 * Constructor which includes Activation Triggers (this NPC will be used to activate quests)
     */
	public NPC(int x, int y, characters.Character chara,
			   String[] message, List<Trigger> questActivationTriggers, List<Trigger> questTriggers) {
		this(x, y, chara, message);
		this.questActivationTriggers = questActivationTriggers;
		this.questTriggers = questTriggers;
	}

    private void initNPC() {
    	isObstacle = true;
        setImage(chara.getImage());
    }
 
    public characters.Character getNPC() {
    	return chara;
    }

	public void setCurrentImage(String imageLoc) {
		setImage(imageLoc);
	}

	public void questInteraction() {
		interactNormalQuestTriggers();
		interactActivatingQuest();
	}

	private void interactActivatingQuest() {
		if(questActivationTriggers.size() != 0) {
			if(lastActivatedTrigger == null) {
				if(QuestHandler.isAcceptable(questActivationTriggers.get(0))) {
					lastActivatedTrigger = questActivationTriggers.get(0);
					questActivationTriggers.remove(0);
					QuestHandler.acceptQuest(lastActivatedTrigger);
				} else {
					//nothing for now (dialog of something)
				}
			} else { //lastActivatedTrigger != null
				if(!QuestHandler.isActive(lastActivatedTrigger)) { //previous quest is done
					if(QuestHandler.isAcceptable(questActivationTriggers.get(0))) { //next quest is ready
						lastActivatedTrigger = questActivationTriggers.get(0);
						questActivationTriggers.remove(0);
						QuestHandler.acceptQuest(lastActivatedTrigger);
					}
					//otherwise, do nothing until next quest is ready
				}
				//otherwise, do nothing until last quest is done
			}
		}
	}

	private void interactNormalQuestTriggers() {
		if(questTriggers.size() != 0) { //ensures there are triggers
			//check if any triggers are attached to an active task or not
			questTriggers.forEach(QuestHandler::checkForTrigger);
		}
	}
}
