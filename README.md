# Grimoire: Legacy 1.7.10


### Note 1: Personal Suggestion, Use UniMixins as grimoire will not be compatible with other projects.

The idea of grimoire is to allow the fixing of multiple mods! Right now, the biggest FixingProject on 1.7.10 is [GTNewHorizons](https://github.com/GTNewHorizons) and they use [UniMixins](https://github.com/LegacyModdingMC/UniMixins)!

**So, i recommend you to use [UniMixins](https://github.com/LegacyModdingMC/UniMixins) to keep yourself compatible with them.**

### Note 2: As of 20.01.2022 this project is continued and developed independently by Aizistral. Check out his repository if you want to get updated Grimoire (3.x.x or later): https://github.com/Aizistral-Studios/Grimoire

A coremod that ships mixin and loads all necessary things for grimoire mixins.

### What is it?

Grimoire is a coremod that ships and uses [Sponge Mixin](https://github.com/SpongePowered/Mixin) allowing you to create
a patch module which will contain mixins to be applied.

These modules are internally called 'Grim-Patches'.

#### Why I need it?

As server owner sometimes you need to patch some buggy or laggy mod, this coremod helps you to organize patches.

#### Isn't patching mods a bad practice?

Yes! it is, and you should not be doing it at all. Due to classloading issues mixins can't be applied to mods without
hacks but sometimes following best practices isn't an option when your only option is fixing a mod with your own hands.  

### Where are the Grimoire Modules/Patches ?

You can find them over here: [Grimoire-Mixins](https://github.com/CrucibleMC/Grimoire-Mixins)

### How to Install?

* 0 - Notice that grimoire will work in both server and client side. But, almost all of our Grim-Patches are for ServerSide Only!
* 1 - Download [Grimoire](https://github.com/CrucibleMC/Grimoire/releases).
* 2 - Put it inside the mods folder.
* 5 - Download any Grimoire-Mixins modules you want from [here](https://github.com/CrucibleMC/Grimoire-Mixins) and put inside the mods folder.
* 5a - If the patch does not work (an legacy patch), create a folder named grimoire alongside the mods folder and put it there.  
* 6 - Start the server again.
