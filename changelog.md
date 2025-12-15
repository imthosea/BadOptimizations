Version 2.4.1:
 - Migrate to Mojang Mappings
 - Change some comments in the config file
 - Update to 1.21.11
   - Sky color is now data driven making sky color caching infeasible so it's been removed
   - Lightmap caching remains but no longer directly uses time as a factor, so lightmap_time_change_needed_for_update no longer has any effect
   - These changes don't apply to 1.21.10-