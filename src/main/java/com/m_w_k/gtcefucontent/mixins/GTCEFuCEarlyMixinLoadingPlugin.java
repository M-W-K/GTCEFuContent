package com.m_w_k.gtcefucontent.mixins;

import java.util.ArrayList;
import java.util.List;

import zone.rong.mixinbooter.IEarlyMixinLoader;

public class GTCEFuCEarlyMixinLoadingPlugin implements IEarlyMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        configs.add("mixins.gregtech.forge.json");

        return configs;
    }
}
