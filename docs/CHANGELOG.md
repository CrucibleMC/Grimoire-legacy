# Grimoire Changelog
Global log for all Grimoire changes, starting with release 3.2.0.

## Release v3.2.0

- First official release of glorified Grimoire. Designed accordingly to [#6](https://github.com/CrucibleMC/Grimoire/issues/6).

## Release v3.2.1

- `Files#move` is now used to move `defaultconfigs` archive from temporary folder to Minecraft folder, instead of old `File#renameTo` approach. Ensures compatibility with Linux, where folder for temporary files often resides on separate file system (thanks to [@vova7865](https://github.com/vova7865), [#11](https://github.com/CrucibleMC/Grimoire/pull/11)).##

## Release v3.2.2

- As mod, Grimoire no longer demands to be present on both client and server side;
- Removed strict requirements for Minecraft version on coremod/mod;
- Added Minecraft version getter to `GrimoireAPI`;
- Fixed version token replacement when building.