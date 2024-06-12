package com.m_w_k.gtcefucontent.api.gui;

import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import gregtech.api.gui.resources.TextureArea;

public final class GTCEFuCGuiTextures {

    public static final TextureArea FUSION_REACTOR_STACK_TITLE =
            fullImage("textures/gui/widget/fusion_reactor_stack_title.png");
    public static final TextureArea FUSION_REACTOR_ARRAY_TITLE =
            fullImage("textures/gui/widget/fusion_reactor_array_title.png");
    public static final TextureArea FUSION_REACTOR_COMPLEX_TITLE =
            fullImage("textures/gui/widget/fusion_reactor_complex_title.png");

    public static final TextureArea PROGRESS_BAR_HEU_HEAT =
            fullImage("textures/gui/progress_bar/progress_bar_heu_heat.png");

    public static final TextureArea ROBOT_ARM_OVERLAY =
            fullImage("textures/gui/overlay/robot_arm_overlay.png");

    private static TextureArea fullImage(String imageLocation) {
        return new TextureArea(GTCEFuCUtil.gtcefucId(imageLocation), 0.0, 0.0, 1.0, 1.0);
    }
}
