<img src="icon.png" align="right" width="180px"/>

# Templates 2

[>> Downloads <<](https://modrinth.com/mod/templates-2)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

If a Template block is placed in the world and right-clicked with a full-size block, it will take on the appearance of that block. Template blocks will inherit light and redstone values from the blocks they're given. Adding a *redstone torch* will make them emit redstone power, *glowstone dust* will make them emit light, and *popped chorus fruit* will make them intangible.

While Templates itself adds a handful of common shapes, it's not too hard for other mods to interface with Templates and add their own templatable blocks.

# For addon developers

## Creating a block entity

All templates need a block entity to store what block they look like. Templates registers one under `templates:slope`[^1], but as an addon developer you can't add additional blocks to my block entity. (Don't try, please.)

To that end, nothing in Templates relies on the *specific* block entity ID, so just re-register `TemplateEntity` under your own name. You are free to extend `TemplateEntity` as well.

The only hard requirement on the block entity is that it `implements ThemeableBlockEntity`. (This implies it returns a `BlockEntity` from `getRenderAttachmentData`.)

## Creating your block

There are various block interactions in Templates, like adding redstone to make the block emit power. To make your block fit with the other Templates, you'll want those behaviors to apply to your block as well. There's a couple options, depending on the complexity of the block you'd like to "template-ify".

If you simply registered a `Block` or a simple vanilla class like `WallBlock`, use the corresponding class in `io.github.cottonmc.templates.block`.

If you registered something different (like a `MyFancyBlock`), the simplest approach is to leverage `TemplateInteractionUtil`, which is where all of the interaction code lives in a mildly "pluggable" form. Here are the suggested steps:

* Create a `TemplateMyFancyBlock` class that extends your block class.
* Copy-paste the body of `TemplateBlock` into it; adjust the constructor as needed. (It was designed to be copy-pasted in this way; this is how i filled out most of the other classes.)
* Make sure it instantiates your block entity type.
* Check that the methods are implemented the way you'd like.
  * Particularly you might want to look at the redstone emission methods; the default ones don't call `super`.

And if all else fails, just reimplement whatever interactions you'd like, or simply don't bother. None of this is important for the actual *retexturing* part of the mod, apart from returning a suitable block entity.

## Creating the custom model

(TL;DR look at `assets/templates/blockstates` and the bottom of `TemplatesClient`)

Of course Templates leverages custom baked models. All of Templates's baked model implementations find and retexture quads from an upstream source that you will need to provide.

Templates needs three pieces of information to perform retexturing:

* the quad to be retextured
* whether you actually want to retexture it or just pass it through unchanged (see the Lever Template, which doesn't change the lever arm);
* what face of the block it corresponds to (which is sometimes different from "the direction it points" - see the Door Template, the textures "stick" when you open the door)

The last piece of information is important because Templates tries hard to retain the orientation of blocks placed inside of them - you can place specifically an *east-facing* log into a Template, for example, and some faces get the cut wood while other faces get the bark. (Some other mods just use the same texture on all faces of the block, like the particle texture.)

Pick a model implementation that suits your needs:

### `UnbakedAutoRetexturedModel`

* the quad: Sourced from a JSON model.
* whether you want to retexture it: "Yes". All quads will be retextured.
* what face of the block: Automatically determined by facing direction.

To construct, pass the ID of the JSON model you want to source quads from.

There's no way to configure this, so if you want to skip retexturing a face, try the next model implementation instead.

**TODO**: this does not work well with `multipart` models with differently-rotated parts, like the fence model (consisting of 1 fence post and 1 fence-side model that gets rotated around to fill all 4 sides)

### `UnbakedJsonRetexturedModel`

* the quad: Sourced from a JSON model.
* whether you want to retexture it: Determined from the texture applied to the quad.
* what face of the block: Determined via the texture applied to the quad.

To construct, pass the ID of a JSON model to retexture. All quads textured with `templates:templates_special/east` will be textured with the east side of the theme, all quads textured with `templates:templates_special/up` will be retextured with the top side of the theme, etc. Quads textured with any other texture will be passed through unaltered.

<details><summary>Regarding texture variables:</summary>

On the off-chance your blockmodel already has texture variables for `north`, `south`, etc, you can simply apply Templates's special textures to it:

```json
{
	"parent": "mymod:block/my_model",
	"textures": {
		"north": "templates:templates_special/north",
		"east": "templates:templates_special/east",
		"south": "templates:templates_special/south",
		"west": "templates:templates_special/west",
		"up": "templates:templates_special/up",
		"down": "templates:templates_special/down",
	}
}
```

Sadly, many models don't specify *completely* separate textures for all six sides. If you have a setup like an "ends" variable which gets applied to "the top and bottom" or something, please don't use the texture-variables approach. Instead, see if the `UnbakedAutoRetexturedModel` suits your needs, or make a second copy of the json model that does separately fill in all faces.
</details>

(This one works better with multipart models.)

### `UnbakedMeshRetexturedModel`

* the quad: Sourced from a `Mesh`.
* whether you want to retexture it: Quads with a nonzero `tag`.
* what face of the block: Determined from the `tag`.

To construct, pass a `Supplier<Mesh>`. To mark a face "retexture this with the EAST side of the block", call `.tag(Direction.EAST.ordinal() + 1)` on it; same for the other directions. (So, the valid tags are 1, 2, 3, 4, 5, and 6, corresponding to down, up, north, south, west, east.) Give these faces UV coordinates ranging from 0 to 1.

A `.tag` of 0 (the default) will be passed through unchanged. This is a little useless since you still need to provide UV coordinates, so instead of passing a `Supplier<Mesh>` you can also pass a `Function<Function<SpriteIdentifier, Sprite>, Mesh>`; you will be provided with a `Function<SpriteIdentifier, Sprite>` that you can query for sprite information, including their UVs.

(To construct this type, you will also need to pass the identifier of a "base model", which can be a regular JSON model. Miscellaneous `BakedModel` properties like rotations, AO, `isSideLit`, etc will be sourced from it. See Template's `models/block/slope_base`. You may need to set `"gui_light": "front"` to avoid a flat look in the ui.)

### A secret fourth thing

Templates doesn't actually care about the block model you pass in. It won't *work* unless you reimplement the retexturing, but if you have your needs I won't stop you.

All the models are supposed to be extensible (if i left a stray `private` let me know). All the `UnbakedModels` are backed by the same abstract class called `RetexturingBakedModel` which actually does the retexturing; feel free to extend it.

## Registering your model

After you've decided on and constructed your special model, you should tell Templates about it. Pick an ID that's different from the base model. (If your base model is `mymod:block/awesome_template`, a good name might be `mymod:awesome_template_special`). Register your special model under that ID using `TemplatesClient.provider.addTemplateModel`.

To assign the block model, using a vanilla blockstate file, simply point your block at that model ID as normal. (See this mod's `blockstates` folder.) You may also use the `x`, `y`, and `uvlock` properties.

To assign the item model, since items don't have the "blockstate file" level of indirection, call `TemplatesClient.provider.assignItemModel`, passing your special model's ID and the items it should be assigned to. Or if you'd rather use a vanilla json model (that won't be retextured) just make one the vanilla way.

# Most important attribution in the whole wide world

COOL RIVULET is by mev, this is the most important block in the mod & perhaps the most important block in any mod ever since `incorporeal:clearly`

# License

MIT, which is unusual for me (usually i write LGPL) - this is inherited from a [CottonMC project](https://github.com/CottonMC/Templates), which inherited [the ElytraDev template](https://github.com/elytra/Concrete), which might explain if the readme layout looks familiar.

[^1]: Yes, even the blocks other than slopes use `templates:slope`. The slope was the first block added to Templates and I forgot to change the block entity ID, and now I can't change it without breaking worlds. At least it demonstrates how the block entity can be used for more than one template?