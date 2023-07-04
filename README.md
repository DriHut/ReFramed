<img src="icon.png" align="right" width="180px"/>

# Templates

[>> Downloads <<](https://github.com/CottonMC/Templates/releases)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Templates is an API for Carpenter's Blocks-like templated blocks. Currently, plain slopes are the only built-in template blocks.

Template blocks can be placed in the world, then right-clicked with a full-size block to set the textures for the template. Template blocks will inherit light and redstone values from the blocks they're given, or they can have light or redstone output added to any given block by right-clicking the template with glowstone dust or a redstone torch, respectively.

# quat was here

## Todo

* Evaluate whether I need to keep the `Supplier` in TemplatesModelProvider, or whether I can reuse my `UnbakedModel`s indefinitely
* `templates:block/slope_base` needs a suspicious amount of custom rotations. Maybe the model is pointing the wrong way.
* `uvlock` in a blockstate will not work for `RetexturedMeshTemplateUnbakedModel`s. Can it be fixed?
* Upside-down slopes would be nice...
* More templates !!

## For addon developers

You may create your block any way you like, just make sure it has a block entity that returns a `BlockState` object from `RenderAttachmentBlockEntity.getRenderAttachmentData` and implements `ThemeableBlockEntity`. If you don't already have a block, the stock implementations in `TemplateBlock` and `TemplateEntity` are considered public API - they also implement the light- and redstone-emission features, just remember to feed the `AbstractBlock.Settings` through `TemplateBlock.configureSettings`. 

(Really the important part is implementing `RenderAttachmentBlockEntity` with a `BlockState`; that's the only thing Templates's bakedmodels assume. `ThemeableBlockEntity` opts-in to a couple more things such as overriding the break/sprint/fall particles.)

## `Mesh`-based models

We will construct a `RetexturedMeshUnbakedModel`. You need two things - the ID of a parent model, and a `Supplier<Mesh>` to retexture.

Fill in the parent model field with the ID of any model. Ideally, this model should have a parent of `block/block`, or at least define *some* non-default rotations (smokey the bear voice *Only You Can Prevent Weirdly Rotated First-Person Models*), set `"gui_light": "front"` (lest the item model look weird), and define a particle texture.

When building the `Mesh`, if you want a face to be dynamically retextured, `.tag()` it with the `.ordinal() + 1` of the `Direction` it corresponds to and give the face U/V coordinates ranging from 0 to 1. (For example, if you tag a face with `3` (`Direction.NORTH.ordinal() + 1`), it will be retextured to the north side of the template's theme.)

(TODO: implement a system for baking unchanging `Sprite`s onto the mesh, potentially by "registering" more tags; the problem is you don't have access to sprite uvs at mesh building time. Or just provide a way to get sprite UVs at mesh building time...?)

That's all you need in order to construct a `RetexturedMeshUnbakedModel`, so to finish things off:

* Come up with an ID for it
* Register it using `TemplatesClient.provider.addTemplateModel` (a thin wrapper around Fabric's `ModelResourceProvider`)
* Create a blockstate for your block, point it at your model's ID
  * You may rotate the blockmodel with the `x` and `y` properties.

You may create a regular item model (JSON or otherwise), or use ours by calling `TemplatesClient.provider.assignItemModel` (a thin wrapper around Fabric's `ModelVariantProvider`) passing the same model ID the block used. (This is a bit of a kludge. The reason you have to do this, instead of simply creating a regular item model and setting its `parent`, is that `JsonUnbakedModel`s can't have non-`JsonUnbakedModel`s as their `parent`, and even a trivial item model with only the `parent` field set counts as a `JsonUnbakedModel`. Blockstates are a layer of indirection before model loading, so it's not a problem for blocks.)

## JSON models

Soon:tm: