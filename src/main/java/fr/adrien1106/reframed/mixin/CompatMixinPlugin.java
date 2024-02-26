package fr.adrien1106.reframed.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CompatMixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, Supplier<Boolean>> CONDITIONS = Map.of(
        "fr.adrien1106.reframed.mixin.compat.AthenaBakedModelMixin", () -> FabricLoader.getInstance().isModLoaded("athena"),
        "fr.adrien1106.reframed.mixin.compat.AthenaWrappedGetterMixin", () -> FabricLoader.getInstance().isModLoaded("athena")
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
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
