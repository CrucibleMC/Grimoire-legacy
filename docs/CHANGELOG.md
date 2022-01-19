# Grimoire Changelog
Global log for all Grimoire changes, starting with release 3.2.0.


## Release v3.2.9

- Fixed omniconfig string lists being restored to their default value after initial loading (#20);
- Fixed *all* subfolders of mods folder being scanned for grimmixes, recursively (#19);
- Ensured proper exception logging when immutable list is used along with @ConfigStringList in annotation config (#15);
- Implemented `Environment#createProxy` method;
- Implemented `GrimoireAPI#loadClass` method.


## Release v3.2.8

- Fixed a bug where `UnsupportedOperationException` was raised when handling omniconfig synchronization packet.


## Release v3.2.7

- Fixed a bug where `NullPointerException` was raised during annotation analysis phase.


## Release v3.2.6

- Fixed a bug where Grimoire would scan jar files in versioned mod folder twice. 


## Release v3.2.5

- Brought back `ForceLoadAsMod` attribute in manifest. Turns out Grimoire was unable to load as mod in production environment without it.

## Release v3.2.4

- Annotation config properties can now be marked as synchronized;
- Default value is no longer added to property comment for string lists.

## Release v3.2.3

- Removed Mixin tweaker specification for `dev` artifact, as it is not used in development environment, and its presence there prevented Grimoire from being properly discovered as mod.

## Release v3.2.2

- As mod, Grimoire no longer demands to be present on both client and server side;
- Removed strict requirements for Minecraft version on coremod/mod;
- Added Minecraft version getter to `GrimoireAPI`;
- Fixed version token replacement when building.

## Release v3.2.1

- `Files#move` is now used to move `defaultconfigs` archive from temporary folder to Minecraft folder, instead of old `File#renameTo` approach. Ensures compatibility with Linux, where folder for temporary files often resides on separate file system (thanks to [@vova7865](https://github.com/vova7865), [#11](https://github.com/CrucibleMC/Grimoire/pull/11)).

## Release v3.2.0

- First official release of glorified Grimoire. Designed accordingly to [#6](https://github.com/CrucibleMC/Grimoire/issues/6).