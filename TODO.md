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

# Attacks
- [x] Finish developing magic attacks
- [ ] Put in special attacks?

# Character Info Screen
- [ ] Continue to organize and format the screen

# Inventory Screen
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
- [ ] Test Serializing for Quests

# Crafting System
- [ ] Add crafting system
- [ ] Add miscellaneous junk items to be used by the crafting system

# Quest System
- [ ] Implement location triggers
- [ ] Explore Tasks
- [ ] Have quests be read from .txt files instead of current setup(?)
- [ ] Have dialog be associated with tasks

# GUI
- [ ] Have a map system for the player to look at (things to discuss, how detailed should it be?)
- [ ] Come up with a way to allow the player to move over/behind different parts of sprites to give illusion of depth?
- [ ] Allow the player to access settings from the main menu
- [ ] Create basic character creation screen

# Known bugs
- [ ] Messages get cut off sometimes in the MessagePane class
