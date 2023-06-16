<img src="icon.png" align="right" width="180px"/>

# Templates

[>> Downloads <<](https://github.com/CottonMC/Templates/releases)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Templates is an API for Carpenter's Blocks-like templated blocks. Currently, plain slopes are the only built-in template blocks.

Template blocks can be placed in the world, then right-clicked with a full-size block to set the textures for the template. Template blocks will inherit light and redstone values from the blocks they're given, or they can have light or redstone output added to any given block by right-clicking the template with glowstone dust or a redstone torch, respectively.

# quat was here - TODO

* Re-generalize the model system (I removed a layer of indirection while rewriting it, so it's just slopes now)
* See what I can do about using the vanilla rotation system (`ModelBakeSettings.getRotation`) instead of manually rotating the `Mesh`
  * A simplification of the mesh system would *definitely* reduce the friction of adding new meshes
  * Upside-down slopes would be nice...
* (if i may): Packages-ish "retexturing" of json blockmodels