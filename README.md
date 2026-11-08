## BadOptimizations

Experimental new main branch. Not done right now. And not published to modrinth/curseforge yet either.

The stuff that's published right now is in the "oldsrc" branch folder.

To build this first run `worktree.sh` to setup all the worktrees.  
Then symlink the version you want to "branch"
```
ln -sfn versions/26.3 branch
```
And run `gradle clean` so everything doesn't break.
Then reload your IDE and run gradle :assembleMonoJar. The result goes into the build folder.