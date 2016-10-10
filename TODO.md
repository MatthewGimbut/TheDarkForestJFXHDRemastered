# General
- [x] Restructure files in the GUI package to be more organized

# Battle System
- [ ] Attacking
   - [ ] 'J' Physical Attack
      - [x] Spawn Insivible Hitbox of specific range (field in weapon)
      - [x] Projectile do the same but that moves with a specified speed, only spawn a projectile weapon if it has ammo
      - [x] Put rate of fire cap on (weapon speed?)
      - [x] If hitbox collides with an active enemy, do damage (battle handler)
         - [x] Normal weapons
         - [x] Projectiles
   - [x] 'K' Magic Attack
- [ ] Develop enemy AI (move randomly with heavy bias towards the player, attack randomly only when facing the player and within range)
- [ ] On enemy kill drop items where the enemy died (randomly ofc)
- [ ] Battle Handler Class + functionality
- [ ] Extend Sprite class to have a combat Sprite, which contains information about the weapon/projectile when being placed on the screen

# Weapons
- [ ] Put range field in for weapons
- [x] Create projectile weapons (and ammo for them), with a specified projectile speed (attached to ammo)
- [ ] When a new weapon is equipped if it is not a projectile weapon then call GamePane.despawnPlayerProjectiles() (this will keep the player from shooting a projectile then switching weapons and it staying on screen. Add this in after projectile weapons are added to the game
- [x] Max rate of fire (attack) field.
- [x] Add Spell Tome as a weapon type (but have it take up left hand slot, alternative to shield?)
- [x] Add different elemental spells depending on equipped spell tome

# Attacks
- [x] Finish developing magic attacks
- [ ] Put in special attacks?

# Character Info Screen
- [x] Add gold count
- [x] Fix XP not showing
- [ ] Continue to organize and format the screen

# Inventory Screen
- [x] Add "all" tab
- [x] Add favorites
- [ ] Ability to sort items
- [ ] Add way to drop items

# Map Builder/System
- [ ] Add more areas in general
- [ ] Add quest restricted areas

# Settings Menu
- [ ] Add ability to change color scheme
- [ ] Add ability to change font

# Save System
- [ ] Make temporary files to store map changes, overwrite old map files on save
- [x] Update SerialVersionUIDs
- [ ] Test Serializing for Quests

# Crafting System
- [ ] Add crafting system
- [ ] Add miscellaneous junk items to be used by the crafting system

# Quest System
- [x] Add quest system
- [x] Serializing (I hope it works at least, haven't been able to test it.)
- [ ] Implement location triggers
- [ ] Explore Tasks
- [ ] Have quests be read from .txt files instead of current setup(?)
- [ ] Have dialog be associated with tasks

# GUI
- [x] Create priority quest overlay which displays the current task of the current quest
- [x] If a quest menu tries to launch while another is open it is pushed to a queue and loaded after the blocking menu is closed. 
- [x] Add quest screen (seperate tabs for active or completed). If the user clicks on an active quest set it to priority quest (run QuestHandler.setPriority(q))
- [ ] Have a map system for the player to look at (things to discuss, how detailed should it be?)
- [ ] Have enemy health bar drawn above their sprite for active enemies (transparent like the priority quest display)
- [x] Have player health and mana bars displayed on screen
- [x] Edit StatsPane for more fitting names
- [x] Create basic art for bows, arrows
- [x] Directional arrows/bolts images
- [x] Have each projectile have a modifier to change base speed depending on type?

# Known bugs
- [x] Map loads twice? Syso statements load twice, investigate (Fixed, doesn't actually load twice, the fillQuests() method is just called twice so it prints it out twice)
- [x] XP doesn't update in character stat screen
- [ ] Messages get cut off sometimes in the MessagePane class
