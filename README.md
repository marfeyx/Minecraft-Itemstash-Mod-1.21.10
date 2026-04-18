# Itemstash

Itemstash is a Minecraft Java Fabric mod for Minecraft 1.21.10 by Marfeyx.

When an item would be picked up but the player's inventory has no free slot or matching stack space, the item is saved into a virtual stash instead of being lost on the ground. The stash is opened with a configurable keybind, shown in Minecraft's keybinds menu as **Open Itemstash**.

The stash screen lists every stored item with its count and offers:

- **Take Stack**: move one stack into the inventory when space is available.
- **Fill Inventory**: move as much of that item as fits.
- **Drop All**: drop all stored items of that type into the world.

## Support

For support and other Marfeyx links, visit [marfeyx.ch/linktree](https://marfeyx.ch/linktree).

## Credits

Created by Marfeyx with programming help from Codex.

## Build

This project uses Fabric Loom. Build with:

```powershell
.\gradlew build
```

If you do not have the Gradle wrapper yet, generate it with a local Gradle installation:

```powershell
gradle wrapper --gradle-version 8.14.3
```
