# ForceGL_AdaptiveRenderScaling(ForceGL_ARS)
- A Minecraft mod for [Fabric](https://fabricmc.net/), [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/), [Quilt](https://quiltmc.org/) & [NeoForge](https://neoforged.net) that includes multiple render functionalities.
- You can download it using [CurseForge](https://www.curseforge.com/minecraft/mc-mods/forcegl2-0-remapped), [Modrinth](https://modrinth.com/mod/forcegl2.0-remapped).

## Features-
- OpenGL version switch.
- AdaptiveChunkScaling - Changes render distance based on your avg. frame rate.

## Fixes-
- Fixes driver crash with old GPUs caused by Java exception while loading shaders using Iris or Oculus. (Including the one's which can run Minecraft without the mod.)
- Fixes game not launching due to GL errors.

## Why?
- Since Minecraft 1.17, Mojang changed the required OpenGL version to 3.2.  
- This means that players with an old graphic card that doesn't support OpenGL 3.2 can no longer play the game.  
- This Minecraft mod makes the game work again for those players by reverting the OpenGL GLFW hints to how it was in Minecraft 1.16.
- Changing render distance dynamically can suit multiple environments where players either need to use high render distances or are limited by performance but would still like to see at a considerate render distance.

## But does it work?
- As far as I tested, there have been no issues. However, you must remember that this mod forces the game to run on an unsupported OpenGL version. There may be unexpected issues with other mods or resource packs.  
- If an issue like the one stated above appears simply turn off the GL override and just use the **ARS** functionality of the mod.

## I found an issue!
- Due to the statement above (that you are running the game on an unsupported OpenGL), I cannot provide extreme support for issues you may experience in this mod. This mod simply changes the OpenGL version and does not apply any other changes to the game's code. I will still provide as much support as I can.

## Compiling the mod
- If you want to compile ForceGL20 by yourself, use the following command:
```
./gradle build  
```
- Use the build from build/libs.

## Orignal mod credits (GL switch logic only.)
- Orignal mod by KabanFriends - [Github](https://github.com/KabanFriends/ForceGL20) [Modrinth](https://modrinth.com/mod/forcegl20)