package com.m_w_k.gtcefucontent.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.jetbrains.annotations.Nullable;

import zone.rong.mixinbooter.IEarlyMixinLoader;

@IFMLLoadingPlugin.Name("GTCEFuCLoadingPlugin")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.TransformerExclusions("gtcefucontent.asm.")
@IFMLLoadingPlugin.SortingIndex(1002)
public class GTCEFuCLoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        configs.add("mixins.gtcefucontent.forge.json");

        return configs;
    }
}
