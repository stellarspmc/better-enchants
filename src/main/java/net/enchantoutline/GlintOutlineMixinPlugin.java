package net.enchantoutline;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GlintOutlineMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("Avaritia")) {
            return LoadingModList.get().getModFileById("avaritia") != null;
        }
        return true;
    }

    // Leave the rest of the lifecycle methods empty
    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String target, ClassNode classNode, String mixin, IMixinInfo mixinInfo) {}
    @Override public void postApply(String target, ClassNode classNode, String mixin, IMixinInfo mixinInfo) {}
}