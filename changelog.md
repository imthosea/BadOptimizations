Version 2.4.0:
- Other mods can now add cache hooks that tell BadOptimizations when to update the lightmap/skycolor. See https://github.com/imthosea/BadOptimizations/wiki/Adding-lightmap-skycolor-caching-hooks
  - Add new config option "ignore_mod_cache_hooks" to ignore loading these hooks (not recommended)
- Fix incorrect Minecraft version requirements on some releases
- (1.21.10+) Use proper API for adding debug HUD entries