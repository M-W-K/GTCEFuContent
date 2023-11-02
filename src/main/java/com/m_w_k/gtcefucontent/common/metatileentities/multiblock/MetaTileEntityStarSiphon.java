package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockAdvancedCasing;

import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.*;
import gregtech.api.metatileentity.IFastRenderMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.interpolate.Eases;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.RenderBufferHelper;
import gregtech.client.utils.RenderUtil;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;

public class MetaTileEntityStarSiphon extends RecipeMapMultiblockController implements IFastRenderMetaTileEntity {

    protected EnergyContainerList inputEnergyContainers;
    protected long heat = 0;
    protected Integer color;

    public MetaTileEntityStarSiphon(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.STAR_SIPHON_RECIPES);
        this.recipeMapWorkable = new StarSiphonRecipeLogic(this);
        this.energyContainer = new EnergyContainerHandler(this, Integer.MAX_VALUE, 0, 0, 0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.BACK, RelativeDirection.UP)
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######3#######",
                        "######3U3######",
                        "#######3#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "#######G#######",
                        "######UNU######",
                        "####GGNNNGG####",
                        "######UNU######",
                        "#######G#######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "######2N2######",
                        "####2223222####",
                        "###GNN3U3NNG###",
                        "####2223222####",
                        "######2N2######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "#######G#######",
                        "###Y#######Y###",
                        "##GCGG###GGCG##",
                        "###Y#######Y###",
                        "#######G#######",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "#######3#######",
                        "######UNU######",
                        "####2223222####",
                        "###E#######E###",
                        "##2#########2##",
                        "##2#########2##",
                        "#U2#########2U#",
                        "3N3#########3N3",
                        "#U2#########2U#",
                        "##2#########2##",
                        "##2#########2##",
                        "###E#######E###",
                        "####2223222####",
                        "######UNU######",
                        "#######3#######")
                .aisle(
                        "######3U3######",
                        "####GGNNNGG####",
                        "###GNN3U3NNG###",
                        "##GCGG###GGCG##",
                        "#GNG#######GNG#",
                        "#GNG#######GNG#",
                        "3N3#########3N3",
                        "UNU#########UNU",
                        "3N3#########3N3",
                        "#GNG#######GNG#",
                        "#GNG#######GNG#",
                        "##GCGG###GGCG##",
                        "###GNN3U3NNG###",
                        "####GGNNNGG####",
                        "######3X3######")
                .aisle(
                        "#######3#######",
                        "######UNU######",
                        "####2223222####",
                        "###E#######E###",
                        "##2#########2##",
                        "##2#########2##",
                        "#U2#########2U#",
                        "3N3#########3N3",
                        "#U2#########2U#",
                        "##2#########2##",
                        "##2#########2##",
                        "###E#######E###",
                        "####2223222####",
                        "######UNU######",
                        "#######3#######")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "##2#########2##",
                        "#GNG#######GNG#",
                        "##2#########2##",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "#######G#######",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "#######G#######",
                        "###Y#######Y###",
                        "##GCGG###GGCG##",
                        "###Y#######Y###",
                        "#######G#######",
                        "#######G#######",
                        "######YCY######",
                        "#######G#######",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "######2N2######",
                        "######2N2######",
                        "####2223222####",
                        "###GNN3U3NNG###",
                        "####2223222####",
                        "######2N2######",
                        "######2N2######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######G#######",
                        "#######G#######",
                        "######UNU######",
                        "####GGNNNGG####",
                        "######UNU######",
                        "#######G#######",
                        "#######G#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .aisle(
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "#######3#######",
                        "######3U3######",
                        "#######3#######",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############",
                        "###############")
                .where('U', stateIndex(0))
                .where('G', stateIndex(1))
                .where('C', stateIndex(4))
                .where('3', stateIndex(3))
                .where('2', stateIndex(2))
                .where('Y', stateIndex(2)
                        .or(metaTileEntities(Arrays.stream(MetaTileEntities.ENERGY_INPUT_HATCH)
                                .filter((mte) -> mte != null && mte.getTier() >= 8)
                                .toArray(MetaTileEntity[]::new)).setMinGlobalLimited(1).setMaxGlobalLimited(16)))
                .where('E', stateIndex(2)
                        .or(autoAbilities(false, false, true, true, true, true, false)))
                .where('X', selfPredicate())
                .where('N', air())
                .where('#', any())
                .build();
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> GTCEFuCMetaBlocks.ADVANCED_CASING
                    .getState(GTCEFuCBlockAdvancedCasing.AdvancedCasingType.NULL_FIELD_CASING);
            case 1 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
            case 4 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
            case 2 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2);
            case 3 -> MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        if (this.recipeMapWorkable.isActive()) {
            return Textures.ACTIVE_FUSION_TEXTURE;
        } else {
            return Textures.FUSION_TEXTURE;
        }
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return Textures.FUSION_REACTOR_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityStarSiphon(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        long energyStored = this.energyContainer.getEnergyStored();
        super.formStructure(context);
        this.initializeAbilities();
        ((EnergyContainerHandler) this.energyContainer).setEnergyStored(energyStored);
    }

    @Override
    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        List<IEnergyContainer> energyInputs = getAbilities(MultiblockAbility.INPUT_ENERGY);
        this.inputEnergyContainers = new EnergyContainerList(energyInputs);
        long euCapacity = calculateEnergyStorageFactor(energyInputs.size());
        // Allow for adaptive max voltage
        this.energyContainer = new EnergyContainerHandler(this, euCapacity, inputEnergyContainers.getInputVoltage(), 0,
                0, 0) {

            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
    }

    private long calculateEnergyStorageFactor(int energyInputAmount) {
        return energyInputAmount * 5000000000L;
    }

    @Override
    protected void updateFormedValid() {
        if (this.inputEnergyContainers.getEnergyStored() > 0) {
            long energyAdded = this.energyContainer.addEnergy(this.inputEnergyContainers.getEnergyStored());
            if (energyAdded > 0) this.inputEnergyContainers.removeEnergy(energyAdded);
        }
        super.updateFormedValid();
        if (recipeMapWorkable.isWorking() && color == null) {
            if (recipeMapWorkable.getPreviousRecipe() != null &&
                    recipeMapWorkable.getPreviousRecipe().getFluidOutputs().size() > 0) {
                int newColor = 0xFF000000 |
                        recipeMapWorkable.getPreviousRecipe().getFluidOutputs().get(0).getFluid().getColor();
                if (!Objects.equals(color, newColor)) {
                    color = newColor;
                    writeCustomData(GregtechDataCodes.UPDATE_COLOR, this::writeColor);
                }
            }
        } else if (!recipeMapWorkable.isWorking() && isStructureFormed() && color != null) {
            color = null;
            writeCustomData(GregtechDataCodes.UPDATE_COLOR, this::writeColor);
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        writeColor(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        readColor(buf);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_COLOR) {
            readColor(buf);
        }
    }

    private void readColor(PacketBuffer buf) {
        color = buf.readBoolean() ? buf.readVarInt() : null;
    }

    private void writeColor(PacketBuffer buf) {
        buf.writeBoolean(color != null);
        if (color != null) {
            buf.writeVarInt(color);
        }
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gregtech.multiblock.fusion_reactor.energy",
                    this.energyContainer.getEnergyStored(), this.energyContainer.getEnergyCapacity()));
            textList.add(new TextComponentTranslation("gregtech.multiblock.fusion_reactor.heat", heat));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.antimatter_compressor.capacity",
                calculateEnergyStorageFactor(16) / 1000000000L));
        tooltip.add(I18n.format("gregtech.machine.fusion_reactor.overclocking"));
    }

    @SuppressWarnings("unused")
    public long getHeat() {
        return heat;
    }

    protected class StarSiphonRecipeLogic extends MultiblockRecipeLogic {

        public StarSiphonRecipeLogic(MetaTileEntityStarSiphon tileEntity) {
            super(tileEntity);
        }

        @Override
        protected double getOverclockingDurationDivisor() {
            return 2.0D;
        }

        @Override
        protected double getOverclockingVoltageMultiplier() {
            return 2.0D;
        }

        @Override
        protected long getMaxVoltage() {
            return super.getMaxVoltage();
        }

        @Override
        public void updateWorkable() {
            super.updateWorkable();
            // Drain heat when the reactor is not active, is paused via soft mallet, or does not have enough energy and
            // has fully wiped recipe progress
            // Don't drain heat when there is not enough energy and there is still some recipe progress, as that makes
            // it doubly hard to complete the recipe
            // (Will have to recover heat and recipe progress)
            if ((!isActive || (hasNotEnoughEnergy && progressTime == 0)) && heat > 0) {
                // heat numbers are so large that exponential decay is wise
                long lossyHeat = (long) (heat * 0.9);
                heat = lossyHeat <= 10000 ? 0 : lossyHeat - 10000;
            }
        }

        @Override
        public boolean checkRecipe(@Nonnull Recipe recipe) {
            if (!super.checkRecipe(recipe))
                return false;

            // if the reactor is not able to hold enough energy for it, do not run the recipe
            if (recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) > energyContainer.getEnergyCapacity())
                return false;

            long heatDiff = recipe.getProperty(FusionEUToStartProperty.getInstance(), 0L) - heat;
            // if the stored heat is >= required energy, recipe is okay to run
            if (heatDiff <= 0)
                return true;

            // if the remaining energy needed is more than stored, do not run
            if (energyContainer.getEnergyStored() < heatDiff)
                return false;

            // remove the energy needed
            energyContainer.removeEnergy(heatDiff);
            // increase the stored heat
            heat += heatDiff;
            return true;
        }

        @Nonnull
        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = super.serializeNBT();
            tag.setLong("Heat", heat);
            return tag;
        }

        @Override
        public void deserializeNBT(@Nonnull NBTTagCompound compound) {
            super.deserializeNBT(compound);
            heat = compound.getLong("Heat");
        }
    }

    @Override
    public void renderMetaTileEntity(double x, double y, double z, float partialTicks) {
        if (color != null && MinecraftForgeClient.getRenderPass() == 0) {
            final int c = color;
            BloomEffectUtil.requestCustomBloom(RENDER_HANDLER, (buffer) -> {
                int color = RenderUtil.colorInterpolator(c, -1).apply(Eases.EaseQuadIn
                        .getInterpolation(Math.abs((Math.abs(getOffsetTimer() % 50) + partialTicks) - 25) / 25));
                float a = (float) (color >> 24 & 255) / 255.0F;
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
                if (entity != null && isActive()) {
                    renderFixedRing(buffer, x, y, z, EnumFacing.Axis.Y, r, g, b, a);
                    renderFixedRing(buffer, x, y, z, EnumFacing.Axis.X, r, g, b, a);
                    renderFixedRing(buffer, x, y, z, EnumFacing.Axis.Z, r, g, b, a);
                }
            });
        }
    }

    private void renderFixedRing(BufferBuilder buffer, double x, double y, double z, EnumFacing.Axis axis, float r,
                                 float g, float b, float a) {
        int xAxisAligned = getFrontFacing().getOpposite().getXOffset();
        int zAxisAligned = getFrontFacing().getOpposite().getZOffset();
        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderBufferHelper.renderRing(buffer,
                x + xAxisAligned * 7 + 0.5,
                y + 0.5,
                z + zAxisAligned * 7 + 0.5,
                6, 0.2, 10, 20,
                r, g, b, a, axis);
        Tessellator.getInstance().draw();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(GTCEFuCUtil.bbHelper(this, 6, 6, -1),
                GTCEFuCUtil.bbHelper(this, -6, -6, -13));
    }

    static BloomEffectUtil.IBloomRenderFast RENDER_HANDLER = new BloomEffectUtil.IBloomRenderFast() {

        @Override
        public int customBloomStyle() {
            return ConfigHolder.client.shader.fusionBloom.useShader ?
                    ConfigHolder.client.shader.fusionBloom.bloomStyle : -1;
        }

        float lastBrightnessX;
        float lastBrightnessY;

        @Override
        @SideOnly(Side.CLIENT)
        public void preDraw(BufferBuilder buffer) {
            BloomEffect.strength = (float) ConfigHolder.client.shader.fusionBloom.strength;
            BloomEffect.baseBrightness = (float) ConfigHolder.client.shader.fusionBloom.baseBrightness;
            BloomEffect.highBrightnessThreshold = (float) ConfigHolder.client.shader.fusionBloom.highBrightnessThreshold;
            BloomEffect.lowBrightnessThreshold = (float) ConfigHolder.client.shader.fusionBloom.lowBrightnessThreshold;
            BloomEffect.step = 1;

            lastBrightnessX = OpenGlHelper.lastBrightnessX;
            lastBrightnessY = OpenGlHelper.lastBrightnessY;
            GlStateManager.color(1, 1, 1, 1);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.disableTexture2D();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void postDraw(BufferBuilder buffer) {
            GlStateManager.enableTexture2D();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
    };
}