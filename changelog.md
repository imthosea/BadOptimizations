It's been a while.

Version 3.0.0:
- Replaced Architectury with a new setup (debugging is a lot easier now)
- Replaced Forgix with some fancy Gradle tasks
  - Now on versions below 26.1, only classes that have Minecraft code, like mixins, are remapped, whereas classes that don't will only be stored once
  - That cuts down the jar size by around half
  - Versions 26.1+ now have the fabric/neo variant combined, and there, no classes are remapped or duplicated, because the game is unbofuscated now
- Fixed Minecraft version metadata in some versions
- Relicense to ISC, it's functionally identical to MIT but I prefer its phrasing
- New Fabric Loader version requirements:
  - 1.20.1 and 1.20.2-1.20.4 now require Fabric 0.14.20 or higher (released August 2023)
  - 1.20.6-1.21.8 and 1.21.10/1.21.11 now require Fabric 0.15.0 or higher (released November 2023)
  - 26.1-26.3 now require Fabric 0.18.4 or higher (released December 2025)
  - Ideally however you should be using the LATEST fabric loader regardless of Minecraft version
- Dropped support for Minecraft 1.20.5, upgrade to the hotfix 1.20.6 instead
- Combined Minecraft version jars:
  - Versions 1.20.6, 1.21.1, 1.21.2-5 and 1.21.6-8 now share one jar
  - Versions 1.21.10 and 1.21.11 now share one jar
  - Versions 26.1, 26.2 and 26.3 now share one jar
- Sky angle caching is now implemented with a better mixin between versions 1.20.6 and 1.21.10
- FOV caching is now implemented with a better mixin on all versions
- Replaced most mixin accessors with access wideners
- Refactor the locations of mixins to match the option they're for
- Added opcodes to FIELD mixins
- Fix a ton of compiler warnings
- Fix a bug where on 1.21.10 and up, the mannequin renderer could be returned in place of the player renderer
  - This fixes compat with radar icons on xaero's minimap
- Now 8 corners will be checked rather than 2 to see if the sky color needs updating
  - This fixes the sky color jumping really weirdly if you entered a new biome at a specific angle
  - The difference is night and day (Pun intended)
- Fix a bug where, if you deleted your config file after booting the game, then went into the mod list menu and opened the config file, it would be regenerated with your prior deleted values rather than the default values
- Rewrote the config loader
  - It's a lot neater now
  - If your config has errors, rather than crashing the game, the default values will now be loaded, and a message will be shown in the chat

Woah! That was a lot. 
I started making this around early October, but I don't have a lot of time to work on modding, so I chipped away at this for a while.
For the long wait, sorry...

Something that annoyed me with the prior setup was every file was copied for each version branch, even though stuff like most of the buildscript, assets, and the config code stayed the same between them. Unfortuanutely git lacks any sort of way to "pin" files across branches, so instead I came up with a somewhat strange but definitely functional solution with worktrees.  
Now everything that stays the same across versions is in a new main branch. Stuff that actually changes across versions, like mixin code or the version catalogue is in its own branch. Every version branch is a worktree in your root folder, and you change a symlink to change what you're working on. And because each version is still a branch, you can still cherry-pick changes between them. See more [here](https://github.com/imthosea/BadOptimizations/blob/main/how2build.md).

Combining a ton of Minecraft versions into one jar required some kinda hacky reflection...but I think it was worth it, because you know what's better than maintaining eleven jars? Maintaining only FIVE. Anything that's called in gameplay uses Java 9's `MethodHandle`s so that the JIT will inline it. To test I made some gradle tasks that'd launch several PrismLauncher instances with the same jar, to make sure that the jar works on the versions it supports. See more [here](https://github.com/imthosea/BadOptimizations/blob/main/how2test.md).

I may drop some Minecraft versions in the future. I didn't want to for this release, but...how many people still play like, 1.20.2, or 1.21.10? ...I don't actually know. I'll have to ask around.

I predict something will break, so I'm marking this as a beta. If there are no new issues for a week I will mark it as stable.

That's it. Now go enjoy your thanksgiving dinner.