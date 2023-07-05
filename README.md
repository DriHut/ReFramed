<img src="icon.png" align="right" width="180px"/>

# Templates

[>> Downloads <<](https://github.com/CottonMC/Templates/releases)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Templates is an API for Carpenter's Blocks-like templated blocks. Currently, plain slopes are the only built-in template blocks.

Template blocks can be placed in the world, then right-clicked with a full-size block to set the textures for the template. Template blocks will inherit light and redstone values from the blocks they're given, or they can have light or redstone output added to any given block by right-clicking the template with glowstone dust or a redstone torch, respectively.

# quat was here

## Todo

* `templates:block/slope_base` needs a suspicious amount of custom rotations. Maybe the model is pointing the wrong way.
* `uvlock` in a blockstate will not work for `RetexturedMeshTemplateUnbakedModel`s. Can it be fixed?
* Upside-down slopes would be nice...
* More templates !!

# For addon developers

## Creating your block

So there are various block interactions in Templates, like adding glowstone to make the block illuminate, adding redstone to make it a power source, etc. In an ideal world, I'd be able to simply provide the `TemplateBlock` class implementing all those features and ask that you extend it from your block. And if your block doesn't already have a superclass I recommend doing this - alas, if it does you can't extend `TemplateBlock`, so the implementation of everything has been farmed out to `public static` methods in `TemplateInteractionUtil` in the api package. Simply wire everything up. Don't forget to feed your `Block.Settings` through `TemplateInteractionUtil.configureSettings` too.

The only other requirement is that your block *must* have a block entity that both returns a `BlockState` object from `RenderAttachmentBlockEntity.getRenderAttachmentData` and implements `ThemeableBlockEntity`. `TemplateEntity` is an implementation of this (and also implements the other half of the features from `TemplateInteractionUtil`); I recommend using it if you can.

## Creating the custom model

TL;DR: Look at `assets/templates/blockstates`, look at `assets/templates/models/block`, and look at the bottom of `TemplatesClient`.

### Using a JSON model

We will construct a `RetexturedJsonModelUnbakedModel`. You need the ID of the json model to retexture.

The base model can have a `parent` of anything you want. To make a surface retexturable, give it the texture `templates:templates_special/down`, `templates:templates_special/north`, `templates:templates_special/east` (etc). These textures will be *replaced* at runtime by the corresponding region of the corresponding texture of the template's theme block. (For example, a surface textured using `templates:templates_special/north` will look like the north side of the template's theme.)

If possible, I don't recommend making a wholly *new* json model. If you have an existing model that defines `north`, `south`, `east` etc texture variables, just use this:

```json
{
	"parent": "yourmod:your/cool/json/model",
	"textures": {
		"down": "templates:templates_special/down",
		"up": "templates:templates_special/up",
		"north": "templates:templates_special/north",
		"south": "templates:templates_special/south",
		"west": "templates:templates_special/west",
		"east": "templates:templates_special/east"
	}
}
```

(If your model has texture variables that look more like `up/down/side` instead of `up/down/north/south/east/west`, you *can* cheat and assign something like `templates:templates_special/east` to the `side` texture slot - but imagine a player retexturing your template to look like a sideways log, which is not rotationally symmetric. It won't work as expected.)

Finally, if your base model is a multipart model, *and* you plan on using it as an item model, I need a representative blockstate in order to select the correct configuration of the base model. Pick one and pass it as the second parameter to `RetexturedJsonModelUnbakedModel`. If the base model is not a multipart model, this is not required.

That's all you need to construct a `RetexturedJsonModelUnbakedModel`, so to finish things off, [register it.](#registering-the-custom-model)

### Using a custom `Mesh`-based model

If you have a shape in mind that can't be represented with a json model (like a perfect 45 degree wedge), this is a good option.

We will construct a `RetexturedMeshUnbakedModel`. You need two things - the ID of a parent model, and a `Supplier<>` of a `Mesh` to retexture.

Ideally, the parent model should have a parent of `block/block`, or at least define *some* non-default rotations (smokey the bear voice *Only You Can Prevent Weirdly Rotated First-Person Models*) and set `"gui_light": "front"` (lest the item model look weird). You should use a json model for this, but keep in mind that no quads will be read from the json model - it's only used to source all the miscellaneous `BakedModel` options.

When building the `Mesh`, if you want a face to be dynamically retextured, `.tag()` it with the `.ordinal() + 1` of the `Direction` it corresponds to, and give the face UV coordinates ranging from 0 to 1. (For example, if you tag a face with `3` (`Direction.NORTH.ordinal() + 1`), it will be retextured to the north side of the template's theme.)

(TODO: implement a system for baking unchanging `Sprite`s onto the mesh, potentially by "registering" more tags; the problem is you don't have access to sprite uvs at mesh building time. Or just provide a way to get sprite UVs at mesh building time...?)

That's all you need in order to construct a `RetexturedMeshUnbakedModel`, so to finish things off, [register it.](#registering-the-custom-model)

## Registering the custom model

1. Decide on an ID for your special model that's *different* from the ID used for the base model.
   * If your base model lives at `yourmod:block/awesome_template`, something like `yourmod:awesome_template_special` would do.
2. Register it using `TemplatesClient.provider.addTemplateModel`.
3. Create a blockstate json for your block, and point it at the ID you decided for your special model in 1).
   * You may rotate the blockmodel with the `x` and `y` properties.

You may create a regular item model, or use ours by calling `TemplatesClient.provider.assignItemModel`, passing the ID of the special model & the items you want to assign it to. (The reason you have to do this instead of simply creating a regular item model and setting its `parent`, is that `JsonUnbakedModel`s can't have non-`JsonUnbakedModel`s as their `parent`, and even a trivial item model with only the `parent` field set counts as a `JsonUnbakedModel`. This isn't a problem for block models because blockstates are a layer of indirection before model loading.)