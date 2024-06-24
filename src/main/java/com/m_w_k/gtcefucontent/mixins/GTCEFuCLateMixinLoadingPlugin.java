package com.m_w_k.gtcefucontent.mixins;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class GTCEFuCLateMixinLoadingPlugin implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();

        configs.add("mixins.gtcefucontent.gregtech.json");

        return configs;
    }
}
