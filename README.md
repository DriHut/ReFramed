<img src="icon.png" align="right" width="180px"/>

# Templates 2

[>> Downloads <<](https://modrinth.com/mod/templates-2)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Template blocks can be placed in the world, then right-clicked with a full-size block to set the textures for the template. Template blocks will inherit light and redstone values from the blocks they're given, or they can have light or redstone output added to any given block by right-clicking the template with glowstone dust or a redstone torch, respectively.

While Templates itself adds a handful of common shapes, it's also not too hard for other mods to interface with Templates and add their own templatable blocks.

# quat was here

Todo move this into the main readme section

COOL RIVULET is by mev , this is the most important block in the mod & perhaps the most important block in any mod ever since `incorporeal:clearly`

## Todo

* More templates !!

# For addon developers

## Creating your block

So there are various block interactions in Templates, like adding glowstone to make the block illuminate, adding redstone to make it a power source, etc.

In an ideal world, I'd be able to simply provide the `TemplateBlock` class implementing all those features and ask that you extend it from your block. If your block doesn't already have a superclass I recommend doing this - alas, if it does, you can't also extend `TemplateBlock`, so the implementation of everything has been farmed out to `public static` methods in `TemplateInteractionUtil` in the api package. Simply wire everything up.

Don't forget to feed your `Block.Settings` through `TemplateInteractionUtil.configureSettings` too, and if there's any behaviors you'd like to tweak, try implementing some methods from `TemplateInteractionUtilExt` before resorting to copy-pasting the implementation of `TemplateInteractionUtil` methods. But copy paste away if you must.

The only other requirement is that your block *must* have a block entity, that both returns a `BlockState` object from `RenderAttachmentBlockEntity.getRenderAttachmentData` and implements `ThemeableBlockEntity`. `TemplateEntity` is an implementation of this (and also implements the other half of the features from `TemplateInteractionUtil`); I recommend using it if you can.

## Creating the custom model

(TL;DR look at `assets/templates/blockstates` and the bottom of `TemplatesClient`)

To make a model retexturable, Templates has to know which faces you want to retexture, and which side of the block they correspond to. There are currently three ways to tell Templates your intentions:

### `UnbakedAutoRetexturedModel`

Pass the ID of a JSON model to retexture. All quads that face east will be textured with the east side of the theme, all quads that face up will be textured with the top side of the theme, etc. There is no way to skip a face.

Most of Templates's blocks use this model.

**TODO**: this does not work well with `multipart` models with differently-rotated parts, like the fence model (consisting of 1 fence post and 1 fence-side model that gets rotated around to fill all 4 sides)

### `UnbakedJsonRetexturedModel`

Pass the ID of a JSON model to retexture. All quads textured with `templates:templates_special/east` will be textured with the east side of the theme, all quads textured with `templates:templates_special/up` will be retextured with the top side of the theme, etc. Quads textured with any other texture will be passed through unaltered.

Templates's lever uses this model - `AutoRetexturedModel` was not appropriate because I did not want to retexture the lever arm.

(This works better with multipart models.)

### `UnbakedMeshRetexturedModel`

Pass either a `Supplier<Mesh>` or a `Function<Function<SpriteIdentifier, Sprite>, Mesh>` (i.e. a function with an optional `Function<SpriteIdentifier, Sprite>` argument, and a return type of `Mesh`). All quads `.tag()`ged with `Direction.EAST.ordinal() + 1` will be textured with the east side of the theme, all quads tagged with `Direction.UP.ordinal() + 1` will be retextured with the top side of the theme, etc. Give these faces UV coordinates ranging from 0 to 1.

Quads with the tag `0` will be passed through unaltered (hence the `+ 1` bias), and they expect UV coordinates that already point to an appropriate region of the block atlas (which is where the `Function<SpriteIdentifier, Sprite>` argument comes in - query it for sprites and put their UV coordinates on the model)

(To construct this type, you will also need to pass the identifier of a "base model", which can be a regular JSON model. Miscellaneous `BakedModel` properties like rotations, AO, `isSideLit`, etc will be sourced from it.) 

Templates's slope blocks use this model, because it's otherwise impossible to make triangle-shaped faces in a JSON model.

## Registering your model

1. Decide on an ID for your special model that's *different* from the ID used for the base model.
   * If your base model lives at `yourmod:block/awesome_template`, something like `yourmod:awesome_template_special` would do.
2. Register it using `TemplatesClient.provider.addTemplateModel`.
3. Create a blockstate json for your block, and point it at the ID you decided for your special model in 1).
   * You may use the `x`, `y`, and `uvlock` properties as normal.

Ambient occlusion now defaults to "on" (since 2.1.1). If this looks bad on your model, you can reset it with a `.disableAo()` call on your UnbakedModel. 

You may create a regular item model, or use ours by calling `TemplatesClient.provider.assignItemModel`, passing the ID of the special model & the items you want to assign it to. (The reason you have to do this instead of simply creating a regular item model and setting its `parent`, is that `JsonUnbakedModel`s can't have non-`JsonUnbakedModel`s as their `parent`, and even a trivial item model with only the `parent` field set counts as a `JsonUnbakedModel`. This isn't a problem for block models because blockstates are a layer of indirection before model loading.)