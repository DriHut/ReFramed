<img src="icon.png" align="right" width="180px"/>

# Templates

[>> Downloads <<](https://github.com/CottonMC/Templates/releases)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Templates is an API for Carpenter's Blocks-like templated blocks. Currently, plain slopes are the only built-in template blocks.

Template blocks can be placed in the world, then right-clicked with a full-size block to set the textures for the template. Template blocks will inherit light and redstone values from the blocks they're given, or they can have light or redstone output added to any given block by right-clicking the template with glowstone dust or a redstone torch, respectively.

# quat was here

## Todo

* Re-generalize the model system (I removed a layer of indirection while rewriting it, so it's just slopes now)
* Upside-down slopes would be nice...
* "Counterrotate" blockstates
  * In the old system, the north/south/east/west faces were constructed individually, so it'd look at the north side of the theme model when computing the north face, the east side of the theme model when computing the east face, etc
  * In the current system, there is only one (south-facing) slope model, and I move its vertices around with a quad transformer to obtain other rotations. (built off the vanilla `AffineTransformation`)
  * But this means... when i'm building the "left" side of the slope, what side of the block should i look at? I have to undo this affine transformation

## Notes for addon developers

To create your block, instantiate or extend `TemplateBlock`. Pass your `Block.Settings` through `TemplateBlock.configureSettings` to wire up the "click for glowstone" feature. Create an `ItemBlock` as normal, and create a `BlockEntityType` by instantiating or extending `TemplateEntity`.

Next, wire up the custom model todo document this, it's easy