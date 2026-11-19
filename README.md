## BadOptimizations

Experimental new main branch. Not done right now. And not published to modrinth/curseforge yet either.

The stuff that's published right now is in the "oldsrc" branch folder.

To build this first run `worktree.sh` to setup all the worktrees.  
Then symlink the version you want to "versions/current"
```
ln -sfn 26.1-3 versions/current
```
Then reload your IDE and run gradle :assembleMonoJar. The result goes into the build folder.

In Intellij you will also need to right click `branch` and press reload from disk.