Versions before 2.1.2 have been backfilled; I gotta be more on top of changelogs.

# next version (unreleased)

* Remove some unused stuff from the jar
* Code cleanups, hopefully without breaking ABI compat (i don't have an ABI checker in the pipeline tho)

road map:

* want to fix texture orientation on vertical slabs/doors
* really want to fix the way vertical slabs place lmao (it's so buggy)
* clean up `StairShapeMaker`

# 2.1.1 (Aug 2, 2023)

Enable ambient-occlusion ("smooth lighting") on all Templates except for the slopes, which are still bugged

# 2.1.0 (Jul 31, 2023)

* Add a vertical slab template
* Add a "tiny slope" template
* Change the block entity NBT format to be much smaller
* Reduce memory footprint of the block entity
* Respect `doTileDrops`
* Improve creative ctrl-pick behavior on glowing Templates
* Adding a Barrier block to a Template makes it remove its model (not unbreakable)

# 2.0.4 (Jul 25, 2023)

* Apply more block tags
* Apply item tags

# 2.0.3 (Jul 23, 2023)

* add Door and Iron Door templates
* cool rivulet

# 2.0.2 (Jul 20, 2023)

* Add an Iron Trapdoor template
* Add some more mod metadata (change name to "Templates 2", add authors, fix sources link)

# 2.0.1 (Jul 11, 2023)

Fix a duplication glitch with the Stair Template, which was retaining its block entity after being broken.

# 2.0.0 (Jul 11, 2023)

Initial release