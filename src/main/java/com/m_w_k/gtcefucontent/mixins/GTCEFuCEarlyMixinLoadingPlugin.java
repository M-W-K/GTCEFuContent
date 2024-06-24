package com.m_w_k.gtcefucontent.mixins;

import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class GTCEFuCEarlyMixinLoadingPlugin implements IEarlyMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        configs.add("mixins.gregtech.forge.json");

        return configs;
    }
}
