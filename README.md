# Cybercraft 2077

A mod for Minecraft 1.12.2 that adds Cyberpunk 2077 music and sounds to the game, as well as the Kiroshi Optics area indicator.

## Why 1.12?

Simple. I didn't make the mod to be compatible with modern versions of the game, I made it to be compatible with the version of the game I personally play. I've been playing a whitelisted Tekkit 2 survival server with my best friend, and I want this mod to be compatible with that.

I want to thank everyone on the Legacy Modding Discord server for assisting me with reverse-engineering the now-undocumented 1.12.2 Forge API, that is a migraine I do not ever want to have again.

## Mod features

 - Adds Cyberpunk's area indicator, which tells you if the area around you is safe or if monsters are nearby.
 - Adds the Cyberpunk death sound for when you get killed, either by an enemy or natural causes such as fall damage or drowning.
 - Adds the Cyberpunk quest completion sound which plays when escaping a combat encounter, either by physically leaving the area or by killing all enemies.
 - Adds the Cyberpunk alert sound, which plays when a hostile entity targets you. (For example, when a skeleton starts shooting you or a creeper starts to detonate near you.)
 - Handles special cases like Wither and Ender Dragon boss battles, making them trigger combat encounters even though boss entities never target the player.
 - Adds Cyberpunk gang combat music that plays during combat encounters.
 - Ignores hostile enemies and combat encounters when in Creative or Spectator mode.
 - Patches the Minecraft MusicTicker class to allow it to be disabled, preventing normal game music from playing over top of Cyberpunk's during a combat encounter.
 - Runs fully client-side.

## Combat music included

4 combat songs from the Cyberpunk OST are included in this mod.

1. The Heist
2. Scavs Gang combat music
3. Raphens combat music
4. Tigerclaws combat music

In the future I plan on adding:

1. Maelstrom combat music
2. Militech combat music
3. Edgewood Farm mission music

I also plan on adding the following tracks as Creative and In-Game random music, when I learn how to do this:

1. Outsider No More (Aldecaldos theme music)
2. Bells of Laguna Bend (Judy's theme)

## Bugs

1. An exploding Creeper will trigger the encounter win sound if in Combat mode and the creeper is the only nearby enemy.
2. Dying in a Combat area and respawning in a Safe or Hostile area will trigger the encounter win sound, even though you were killed and thus lost.
3. Looking away from all enemies in a Combat encounter or Hostile area can sometimes trigger the Safe area state even though the area isn't actually safe. This is likely caused by entity culling.
4. When walking in the Overworld, the area can sometimes mostakenly register as Hostile even when no enemies are nearby. This usually means that there's a cave nearby and the game is detecting underground enemies.
5. Ender Dragons do not trigger combat encounters even though they should.
6. Switching to Creative or Spectator mode during a combat encounter with cheats enabled will trigger the encounter win sound.
7. Area indicator renders on top of boss health bars.

## Tested hostile entities

So far I've tested with the following entities to make sure they trigger combat mode.

 - Zombie: Triggers when pathfinding toward the player.
 - Skeleton/Wither Skeleton: Triggers when about to shoot the player.
 - Witch: Triggers immediately if close enough.
 - Creeper: Triggers when about to detonate.
 - Silverfish/Endermite: Triggers immediately if close enough,
 - Husk: Same as Zombies
 - Blaze: Triggers immediately if close enough, but is sometimes unreliable
 - Ghast: Same as Blazes
 - Slime/Magma Cube: Triggers immediately when close enough. Combat mode won't end until all descendants of the slime are killed.
 - Enderman: Triggers when the player attacks the entity or if the player looks at the enderman's eyes.
 - Llama: Can sometimes trigger Combat mode if the llama throws its projectile at the player and the projectile stays on-screen for long enough. This is a bug.
 - Wolf: Triggers if attacked by the player. Will not trigger the Hostile state since wolves are classified as animals.
 
## Caution about MusicTicker patching

This mod might break with other mods. In order for combat music to work in an audibly pleasing way, the game needs to be able to disable its normal random music playback. Random music, such as the music heard in the main menu or Creative Mode, is managed by the `MusicTicker` class.

At least in 1.12, there is no built-in way to control or disable the MusicTicker. With that in mind, this mod uses Java reflection to inject a wrapper for MusicTicker into the main Minecraft class. This allows the mod to control when MusicTicker is allowed to function.

If you have other mods that do this as well, they will break and so will Cybercraft 2077. If Cybercraft 2077 is unable to patch the music ticker in the first place, it will act as though it can't disable MusicTicker (so the mod will work fine but you'll hear Cyberpunk music over Minecraft music). But if a mod patches MusicTicker after Cybercraft does,you will crash.
 - 