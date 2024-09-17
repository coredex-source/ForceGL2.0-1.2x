# ForceGL2.0
A Minecraft mod for [Fabric](https://fabricmc.net/), [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/), [Quilt](https://quiltmc.org/) & [NeoForge](https://neoforged.net) that forces the game to use OpenGL 2.0.
You can download it using [CurseForge](https://www.curseforge.com/minecraft/mc-mods/forcegl2-0-remapped), [Modrinth](https://modrinth.com/mod/forcegl2.0-remapped).

## Mod does not load up on forge or neoforge?
The loaders take early control of the loading screen, to prevent that from happening-
- Launch the game once and let it crash.
- Navigate to .minecraft/config folder, edit the fml.toml file and change **earlyWindowControl = true** to **earlyWindowControl = false**.
- Load the game again with the mod in the mods folder and it should load up normally.

## Fixes-
Fixes driver crash with old GPUs caused by Java exception while loading shaders using Iris or Oculus. (Including the one's which can run Minecraft without the mod.)
Fixes game not launching due to GL errors.

## Why?
Since Minecraft 1.17, Mojang changed the required OpenGL version to 3.2.  
This means that players with an old graphic card that doesn't support OpenGL 3.2 can no longer play the game.  
This Minecraft mod makes the game work again for those players by reverting the OpenGL GLFW hints to how it was in Minecraft 1.16.

## But does it work?
As far as I tested, there have been no issues. However, you must remember that this mod forces the game to run on an unsupported OpenGL version. There may be unexpected issues with other mods or resource packs.  
**While it is fine to use this mod to run newer versions of Minecraft, please do consider upgrading to a newer graphic card as soon as possible.**

## I found an issue!
Due to the statement above (that you are running the game on an unsupported OpenGL), I cannot provide extreme support for issues you may experience in this mod. This mod simply changes the OpenGL version and does not apply any other changes to the game's code. I will still provide as much support as I can.

## Compiling the mod
If you want to compile ForceGL20 by yourself, use the following command:
```
./gradle build  
```
Use the build from {LoaderType}/build/libs.

## Orignal mod
Orignal mod by KabanFriends - [Github](https://github.com/KabanFriends/ForceGL20) [Modrinth](https://modrinth.com/mod/forcegl20)
