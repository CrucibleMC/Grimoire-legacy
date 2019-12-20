# Grimoire

A core mod that Grimoire-Mixins Modules use to be loaded up.

#### What is it?

Grimoire is a coremod that implements [Sponge Mixin](https://github.com/SpongePowered/Mixin) and allows to 'organize' the order of mixins modules that will me implemented.
These module are internally called 'Grim-Patchs'

###### What is Sponge Mixin?

Basically it's a program that allows editing other programs at run time.

For example, if there is a mod that causes tons o lag and the author does not give you the source code for you to edit it and recompile, you can use Sponge Mixin to edit that mod at runtime and fix the problem by yourself.

### Where are the Grimoire Modules/Paches ?

You can find them over here: [Grimoire-Mixins](https://github.com/CrucibleMC/Grimoire-Mixins)

### How to Install?

* 0 - Notice that grimoire will work in both server and client side. But, almost all of our Grim-Patchs are for ServerSide Only!
* 1 - Download [Grimoire](https://github.com/CrucibleMC/Grimoire/releases).
* 2 - Put it inside the mods folder.
* 3 - Start the server, so the coremod can create a folder alongside the 'mods' and 'config' folders called 'grimoire'.
* 4 - Stop the server.
* 5 - Download any Grimoire-Mixins modules you want from [here](https://github.com/CrucibleMC/Grimoire-Mixins) and put inside the grimoire folder.
* 6 - Start the server again.