package com.m_w_k.gtcefucontent.mixins;

import java.util.ArrayList;
import java.util.List;

import zone.rong.mixinbooter.ILateMixinLoader;

public class GTCEFuCLateMixinLoadingPlugin implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        configs.add("mixins.gtcefucontent.gregtech.json");

        return configs;
    }

    @Override
    public void onMixinConfigQueued(String mixinConfig) {
        ILateMixinLoader.super.onMixinConfigQueued(mixinConfig);
    }
}
