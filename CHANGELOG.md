# Changelog

## Itemstash 1.0.0

Initial release of Itemstash for Minecraft 1.21.10.

### Added

- Automatic item stashing when the player inventory is full.
- Virtual per-player stash storage.
- Configurable **Open Itemstash** keybind.
- Stash menu showing stored items and item counts.
- **Take Stack** action to move one stack into the inventory.
- **Fill Inventory** action to fill available inventory space with the selected item.
- **Drop All** action to drop all stored items of a selected type in front of the player.
- Persistent stash saving across game sessions and server restarts.
- Stash contents persist after player death and respawn.
- 15-second delay before player-dropped items can be picked up by the stash.
- Mod icon and metadata for Itemstash by Marfeyx.

### Fixed

- Fixed stash contents being lost when the player dies.
- Fixed the stash screen being invisible on some clients due to transparent text colors.
- Fixed Lunar Client crash caused by applying screen blur twice.
- Fixed items not entering the stash when the main inventory was full.
- Fixed **Drop All** removing items from the stash without physically dropping them.
- Fixed dropped stash items being immediately pulled back into the stash.

### Requirements

- Minecraft 1.21.10
- Fabric Loader
- Fabric API
