<img src="icon.png" align="right" width="180px"/>

# Templates

[>> Downloads <<](https://github.com/CottonMC/Templates/releases)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Templates is an API for Carpenter's Blocks-like templated blocks. Currently, plain slopes are the only built-in template blocks.

Template blocks can be placed in the world, then right-clicked with a full-size block to set the textures for the template. Template blocks will inherit light and redstone values from the blocks they're given, or they can have light or redstone output added to any given block by right-clicking the template with glowstone dust or a redstone torch, respectively.

# quat was here

## Todo

* Upside-down slopes would be nice...
* More templates !!

## Notes for addon developers

To create your block, instantiate or extend `TemplateBlock`. Pass your `Block.Settings` through `TemplateBlock.configureSettings` to wire up the "click for glowstone" feature. Create an `ItemBlock` as normal, and create a `BlockEntityType` by instantiating or extending `TemplateEntity`.

Next, wire up the custom model todo document this, it's easy