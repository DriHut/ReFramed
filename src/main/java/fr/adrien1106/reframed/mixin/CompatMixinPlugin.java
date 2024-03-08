package fr.adrien1106.reframed.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CompatMixinPlugin implements IMixinConfigPlugin {

    private static final FabricLoader LOADER = FabricLoader.getInstance();
    private static final Logger LOGGER = LoggerFactory.getLogger("ReFramed MIXIN");
    private static final List<String> COMPAT_MOD = List.of("athena", "indium", "sodium");
    private static final Map<String, Supplier<Boolean>> CONDITIONS = Map.of(
        "fr.adrien1106.reframed.mixin.compat.AthenaBakedModelMixin", () -> LOADER.isModLoaded(COMPAT_MOD.get(0)),
        "fr.adrien1106.reframed.mixin.compat.AthenaWrappedGetterMixin", () -> LOADER.isModLoaded(COMPAT_MOD.get(0)),
        "fr.adrien1106.reframed.mixin.render.TerrainRenderContextMixin", () -> !LOADER.isModLoaded(COMPAT_MOD.get(1)),
        "fr.adrien1106.reframed.mixin.render.BlockRenderInfoMixin", () -> !LOADER.isModLoaded(COMPAT_MOD.get(1)),
        "fr.adrien1106.reframed.mixin.render.AbstractBlockRenderContextMixin", () -> !LOADER.isModLoaded(COMPAT_MOD.get(1)),
        "fr.adrien1106.reframed.mixin.compat.IndiumTerrainRenderContextMixin", () -> LOADER.isModLoaded(COMPAT_MOD.get(1)),
        "fr.adrien1106.reframed.mixin.compat.IndiumTerrainBlockRenderInfoMixin", () -> LOADER.isModLoaded(COMPAT_MOD.get(1)),
        "fr.adrien1106.reframed.mixin.compat.IndiumAbstractBlockRenderContextMixin", () -> LOADER.isModLoaded(COMPAT_MOD.get(1)),
        "fr.adrien1106.reframed.mixin.compat.SodiumBlockOcclusionCacheMixin", () -> LOADER.isModLoaded(COMPAT_MOD.get(2))
    );


    @Override
    public void onLoad(String mixin_package) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String target_class, String mixin_class) {
        return CONDITIONS.getOrDefault(mixin_class, () -> true).get();
    }

    @Override
    public void acceptTargets(Set<String> mine, Set<String> others) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String target_class_name, ClassNode target_class, String mixin_class_name, IMixinInfo mixin_info) {

    }

    @Override
    public void postApply(String target_class, ClassNode target, String mixin_class, IMixinInfo mixin_info) {
        String mixin_class_name = mixin_class.substring(mixin_class.lastIndexOf('.') + 1);
        COMPAT_MOD.forEach(mod -> {
            if (mixin_class_name.toLowerCase().startsWith(mod))
                LOGGER.info("Loaded compatibility mixin class for mod \"" + mod + "\" (class: " + target_class + ")");
        });
    }
}
