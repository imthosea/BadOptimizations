6/16/26:
- Port to 26.2

3/25/2026:
- Port to 26.1
- Fabric and NeoForge variants are no longer combined into one jar
  - This may revert in the future

Version 2.4.1:
 - Migrate to Mojang Mappings
 - Change some comments in the config file
 - Update to 1.21.11
   - Sky color is now data driven making sky color caching infeasible so it's been removed
   - Lightmap caching remains but no longer directly uses time as a factor, so lightmap_time_change_needed_for_update no longer has any effect
   - These changes don't apply to 1.21.10-