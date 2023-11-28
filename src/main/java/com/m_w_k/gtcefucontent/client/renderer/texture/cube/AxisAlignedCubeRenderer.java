package com.m_w_k.gtcefucontent.client.renderer.texture.cube;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.ArrayUtils;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import gregtech.api.GTValues;
import gregtech.api.gui.resources.ResourceHelper;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.cclop.LightMapOperation;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.RenderUtil;
import gregtech.common.ConfigHolder;

public class AxisAlignedCubeRenderer implements ICubeRenderer {

    private static final Rotation DEF_ROT = new Rotation(0, 0, 0, 0);

    public enum RenderSide {

        AXIS,
        SIDE;

        public static final RenderSide[] VALUES = values();

        public static RenderSide byAxis(EnumFacing.Axis axis, EnumFacing side) {
            if (side.getAxis() == axis) {
                return AXIS;
            } else return SIDE;
        }
    }

    protected final String basePath;

    @SideOnly(Side.CLIENT)
    protected Map<RenderSide, TextureAtlasSprite> sprites;

    @SideOnly(Side.CLIENT)
    protected Map<RenderSide, TextureAtlasSprite> spritesEmissive;

    public AxisAlignedCubeRenderer(String basePath) {
        this.basePath = basePath;
        Textures.CUBE_RENDERER_REGISTRY.put(basePath, this);
        Textures.iconRegisters.add(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(TextureMap textureMap) {
        String modID = GTValues.MODID;
        String basePath = this.basePath;
        String[] split = this.basePath.split(":");
        if (split.length == 2) {
            modID = split[0];
            basePath = split[1];
        }
        this.sprites = new EnumMap<>(RenderSide.class);
        this.spritesEmissive = new EnumMap<>(RenderSide.class);
        for (RenderSide overlayFace : RenderSide.VALUES) {
            String faceName = overlayFace.name().toLowerCase();
            ResourceLocation resourceLocation = new ResourceLocation(modID,
                    String.format("blocks/%s/%s", basePath, faceName));
            sprites.put(overlayFace, textureMap.registerSprite(resourceLocation));

            String emissive = String.format("blocks/%s/%s_emissive", basePath, faceName);
            if (ResourceHelper.doResourcepacksHaveTexture(modID, emissive, true)) {
                spritesEmissive.put(overlayFace, textureMap.registerSprite(new ResourceLocation(modID, emissive)));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getSpriteOnSide(RenderSide renderSide) {
        return sprites.get(renderSide);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getParticleSprite() {
        return getSpriteOnSide(RenderSide.AXIS);
    }

    // borrows heavily from OrientedOverlayRenderer
    @Override
    @SideOnly(Side.CLIENT)
    public void renderOrientedState(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline,
                                    Cuboid6 bounds, EnumFacing frontFacing, boolean isActive,
                                    boolean isWorkingEnabled) {
        for (EnumFacing renderSide : EnumFacing.VALUES) {
            RenderSide overlayFace = RenderSide.byAxis(frontFacing.getAxis(), renderSide);
            TextureAtlasSprite renderSprite = sprites.get(overlayFace);

            // preserve the original translation when not rotating the top and bottom
            Matrix4 renderTranslation = translation.copy();

            // Rotate faces to match front facing
            Rotation rotation = getRotation(renderTranslation, renderSide, frontFacing);
            renderTranslation = RenderUtil.adjustTrans(renderTranslation, renderSide, 1);
            renderTranslation.apply(rotation);

            Textures.renderFace(renderState, renderTranslation, ArrayUtils.addAll(pipeline, rotation), renderSide,
                    bounds, renderSprite, BlockRenderLayer.CUTOUT_MIPPED);

            TextureAtlasSprite emissiveSprite = spritesEmissive.get(overlayFace);
            if (emissiveSprite != null) {
                if (ConfigHolder.client.machinesEmissiveTextures) {
                    IVertexOperation[] lightPipeline = ArrayUtils.addAll(pipeline, new LightMapOperation(240, 240),
                            rotation);
                    Textures.renderFace(renderState, renderTranslation, lightPipeline, renderSide, bounds,
                            emissiveSprite, BloomEffectUtil.getEffectiveBloomLayer());
                } else {
                    Textures.renderFace(renderState, renderTranslation, ArrayUtils.addAll(pipeline, rotation),
                            renderSide, bounds, emissiveSprite, BlockRenderLayer.CUTOUT_MIPPED);
                }
            }
        }
    }

    public Rotation getRotation(Matrix4 transformation, EnumFacing renderSide, EnumFacing frontFacing) {
        switch (renderSide) {
            case UP -> {
                if (frontFacing == EnumFacing.SOUTH) {
                    transformation.translate(1, 0, 1);
                    return new Rotation(Math.PI, 0, 1, 0);
                }
                if (frontFacing == EnumFacing.WEST) {
                    transformation.translate(0, 0, 1);
                    return new Rotation(Math.PI / 2, 0, 1, 0);
                }
                if (frontFacing == EnumFacing.EAST) {
                    transformation.translate(1, 0, 0);
                    return new Rotation(-Math.PI / 2, 0, 1, 0);
                }
            }
            case DOWN -> {
                if (frontFacing == EnumFacing.NORTH) {
                    transformation.translate(1, 0, 1);
                    return new Rotation(Math.PI, 0, 1, 0);
                }
                if (frontFacing == EnumFacing.EAST) {
                    transformation.translate(0, 0, 1);
                    return new Rotation(Math.PI / 2, 0, 1, 0);
                }
                if (frontFacing == EnumFacing.WEST) {
                    transformation.translate(1, 0, 0);
                    return new Rotation(-Math.PI / 2, 0, 1, 0);
                }
            }
            case EAST -> {
                if (frontFacing == EnumFacing.DOWN) {
                    transformation.translate(0, 1, 1);
                    return new Rotation(Math.PI, 1, 0, 0);
                }
                if (frontFacing == EnumFacing.NORTH) {
                    transformation.translate(0, 0, 1);
                    return new Rotation(-Math.PI / 2, 1, 0, 0);
                }
                if (frontFacing == EnumFacing.SOUTH) {
                    transformation.translate(0, 1, 0);
                    return new Rotation(Math.PI / 2, 1, 0, 0);
                }
            }
            case WEST -> {
                if (frontFacing == EnumFacing.DOWN) {
                    transformation.translate(0, 1, 1);
                    return new Rotation(Math.PI, 1, 0, 0);
                }
                if (frontFacing == EnumFacing.SOUTH) {
                    transformation.translate(0, 1, 0);
                    return new Rotation(Math.PI / 2, 1, 0, 0);
                }
                if (frontFacing == EnumFacing.NORTH) {
                    transformation.translate(0, 0, 1);
                    return new Rotation(-Math.PI / 2, 1, 0, 0);
                }
            }
            case NORTH -> {
                if (frontFacing == EnumFacing.DOWN) {
                    transformation.translate(1, 1, 0);
                    return new Rotation(Math.PI, 0, 0, 1);
                }
                if (frontFacing == EnumFacing.WEST) {
                    transformation.translate(1, 0, 0);
                    return new Rotation(Math.PI / 2, 0, 0, 1);
                }
                if (frontFacing == EnumFacing.EAST) {
                    transformation.translate(0, 1, 0);
                    return new Rotation(-Math.PI / 2, 0, 0, 1);
                }
            }
            case SOUTH -> {
                if (frontFacing == EnumFacing.DOWN) {
                    transformation.translate(1, 1, 0);
                    return new Rotation(Math.PI, 0, 0, 1);
                }
                if (frontFacing == EnumFacing.EAST) {
                    transformation.translate(0, 1, 0);
                    return new Rotation(-Math.PI / 2, 0, 0, 1);
                }
                if (frontFacing == EnumFacing.WEST) {
                    transformation.translate(1, 0, 0);
                    return new Rotation(Math.PI / 2, 0, 0, 1);
                }
            }
        }
        return DEF_ROT;
    }
}
