# Grimoire

A coremod that ships mixin and loads all necessary things for grimoire mixins.

### What is it?

Grimoire is a coremod that ships and uses [Sponge Mixin](https://github.com/SpongePowered/Mixin) allowing you to create
a patch packages which will contain mixins to be applied.

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
* 3 - Start the server, so the coremod can create a folder alongside the 'mods' and 'config' folders called 'grimoire'.
* 4 - Stop the server.
* 5 - Download any Grimoire-Mixins modules you want from [here](https://github.com/CrucibleMC/Grimoire-Mixins) and put inside the grimoire folder.
* 6 - Start the server again.
