package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import com.m_w_k.gtcefucontent.GTCEFuContent;
import com.m_w_k.gtcefucontent.api.recipes.GTCEFuCRecipeMaps;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.GTValues;
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
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.recipeproperties.FusionEUToStartProperty;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.util.interpolate.Eases;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.utils.BloomEffectUtil;
import gregtech.client.utils.RenderBufferHelper;
import gregtech.client.utils.RenderUtil;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityFluidHatch;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiFluidHatch;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.m_w_k.gtcefucontent.api.recipes.logic.LimitedPerfectOverclockingLogic.limitedPerfectOverclockingLogic;

public class MetaTileEntityFusionStack extends RecipeMapMultiblockController implements IFastRenderMetaTileEntity {
    protected final int overclock_rating;
    protected EnergyContainerList inputEnergyContainers;
    protected long heat = 0;
    protected Integer color;

    // I had to copy word-for-word so many things from MetaTileEntityFusionReactor because EVERYTHING IS PRIVATE
    // Credit to the creators of GTCEu I guess... though I'm very salty about it.
    // And yes, I tried to use reflection, and I couldn't get it to stop throwing compile-time errors.
    // Plus, use of reflection isn't the most wise thing either.
    // Seriously, why so many private properties? Make them protected plz k thx
    public MetaTileEntityFusionStack(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, GTCEFuCRecipeMaps.FUSION_STACK_RECIPE_MAPS.get(overclock_rating(tier) - 1));
        this.recipeMapWorkable = new FusionStackRecipeLogic(this);
        this.overclock_rating = overclock_rating(tier);
        this.energyContainer = new EnergyContainerHandler(this, Integer.MAX_VALUE, 0, 0, 0, 0) {
            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityFusionStack(metaTileEntityId, tier(overclock_rating));
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return (switch (overclock_rating) {
            default -> FusionStackPatterns.FUSION_STACK;
            case 2 -> FusionStackPatterns.FUSION_ARRAY;
            case 3 -> // since the complex has tile entities, they need to have a direction alignment specified based on the controller's alignment
                    FusionStackPatterns.FUSION_COMPLEX
                            .where('5', FusionStackPatterns.metaTileEntitiesModified(this.getFrontFacing().rotateY(), MetaTileEntities.HPCA_ADVANCED_COMPUTATION_COMPONENT))
                            .where('6', FusionStackPatterns.metaTileEntitiesModified(this.getFrontFacing().rotateY(), MetaTileEntities.HPCA_ACTIVE_COOLER_COMPONENT))
                            .where('7', FusionStackPatterns.metaTileEntitiesModified(this.getFrontFacing().rotateY().getOpposite(), MetaTileEntities.HPCA_ADVANCED_COMPUTATION_COMPONENT))
                            .where('8', FusionStackPatterns.metaTileEntitiesModified(this.getFrontFacing().rotateY().getOpposite(), MetaTileEntities.HPCA_ACTIVE_COOLER_COMPONENT));
        })
                .where('X', selfPredicate())
                .build();
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart instanceof MetaTileEntityFluidHatch
                || sourcePart instanceof MetaTileEntityMultiFluidHatch) return getFluidHatchTexture();
        if (this.recipeMapWorkable.isActive()) {
            return Textures.ACTIVE_FUSION_TEXTURE;
        } else {
            return Textures.FUSION_TEXTURE;
        }
    }

    private ICubeRenderer getFluidHatchTexture() {
        switch (overclock_rating) {
            default -> {
                return Textures.STURDY_HSSE_CASING;
            }
            case 2 -> {
                return Textures.FROST_PROOF_CASING;
            }
            case 3 -> {
                return GCYMTextures.ATOMIC_CASING;
            }
        }
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
        this.energyContainer = new EnergyContainerHandler(this, euCapacity, inputEnergyContainers.getInputVoltage(), 0, 0, 0) {
            @Nonnull
            @Override
            public String getName() {
                return GregtechDataCodes.FUSION_REACTOR_ENERGY_CONTAINER_TRAIT;
            }
        };
    }

    private long calculateEnergyStorageFactor(int energyInputAmount) {
        // each additional tier gets 2x as many inputs already
        return energyInputAmount * 80000000L;
    }

    @Override
    protected void updateFormedValid() {
        if (this.inputEnergyContainers.getEnergyStored() > 0) {
            long energyAdded = this.energyContainer.addEnergy(this.inputEnergyContainers.getEnergyStored());
            if (energyAdded > 0) this.inputEnergyContainers.removeEnergy(energyAdded);
        }
        super.updateFormedValid();
        if (recipeMapWorkable.isWorking() && color == null) {
            if (recipeMapWorkable.getPreviousRecipe() != null && recipeMapWorkable.getPreviousRecipe().getFluidOutputs().size() > 0) {
                int newColor = 0xFF000000 | recipeMapWorkable.getPreviousRecipe().getFluidOutputs().get(0).getFluid().getColor();
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
            textList.add(new TextComponentTranslation("gregtech.multiblock.fusion_reactor.energy", this.energyContainer.getEnergyStored(), this.energyContainer.getEnergyCapacity()));
            textList.add(new TextComponentTranslation("gregtech.multiblock.fusion_reactor.heat", heat));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.fusion_reactor.capacity", calculateEnergyStorageFactor((int) (Math.pow(2, 3 + overclock_rating))) / 1000000L));
        tooltip.add(I18n.format("gregtech.machine.fusion_reactor.overclocking"));
        tooltip.add(TooltipHelper.RAINBOW_SLOW + I18n.format("gtcefucontent.machine.fusion_stack.perfect", overclock_rating));
        if (overclock_rating == 3) {
            tooltip.add(TooltipHelper.BLINKING_CYAN + I18n.format("gtcefucontent.machine.fusion_stack.coolant"));
        }
    }

    @Nonnull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.FUSION_REACTOR_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    public long getHeat() {
        return heat;
    }

    protected class FusionStackRecipeLogic extends MultiblockRecipeLogic {

        public FusionStackRecipeLogic(MetaTileEntityFusionStack tileEntity) {
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

        @Nonnull
        protected int[] runOverclockingLogic(@Nonnull IRecipePropertyStorage propertyStorage, int recipeEUt, long maxVoltage, int duration, int amountOC) {
            return limitedPerfectOverclockingLogic(
                    Math.abs(recipeEUt),
                    maxVoltage,
                    duration,
                    amountOC,
                    getOverclockingDurationDivisor(),
                    getOverclockingVoltageMultiplier(),
                    overclock_rating
            );
        }

        @Override
        protected long getMaxVoltage() {
            // Increase the tier-lock voltage twice as fast as normal fusion
            // UEV, UXV, MAX
            return Math.min(GTValues.V[tier(overclock_rating * 2)], super.getMaxVoltage());
        }

        @Override
        public void updateWorkable() {
            super.updateWorkable();
            // Drain heat when the reactor is not active, is paused via soft mallet, or does not have enough energy and has fully wiped recipe progress
            // Don't drain heat when there is not enough energy and there is still some recipe progress, as that makes it doubly hard to complete the recipe
            // (Will have to recover heat and recipe progress)
            if ((!(isActive || workingEnabled) || (hasNotEnoughEnergy && progressTime == 0)) && heat > 0) {
                heat = heat <= 40000 ? 0 : (heat - 40000);
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
                int color = RenderUtil.colorInterpolator(c, -1).apply(Eases.EaseQuadIn.getInterpolation(Math.abs((Math.abs(getOffsetTimer() % 50) + partialTicks) - 25) / 25));
                float a = (float) (color >> 24 & 255) / 255.0F;
                float r = (float) (color >> 16 & 255) / 255.0F;
                float g = (float) (color >> 8 & 255) / 255.0F;
                float b = (float) (color & 255) / 255.0F;
                Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
                if (entity != null && isActive()) {
                    int xAxisAligned = getFrontFacing().getOpposite().getXOffset();
                    int zAxisAligned = getFrontFacing().getOpposite().getZOffset();
                    if (overclock_rating == 1) {
                        double xOffset = xAxisAligned * 7 + 0.5;
                        double zOffset = zAxisAligned * 7 + 0.5;
                        renderFusionRing(buffer,
                                x + xOffset,
                                y + 2.5,
                                z + zOffset,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset,
                                y - 1.5,
                                z + zOffset,
                                r, g, b, a);
                    } else if (overclock_rating == 2) {
                        double xOffset = xAxisAligned * 4 + 0.5;
                        double zOffset = zAxisAligned * 4 + 0.5;
                        double xMod = zAxisAligned * 10;
                        double zMod = xAxisAligned * 10;
                        renderFusionRing(buffer,
                                x + xOffset + xMod,
                                y - 1.5,
                                z + zOffset + zMod,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset + xMod,
                                y - 5.5,
                                z + zOffset + zMod,
                                r, g, b, a);

                        renderFusionRing(buffer,
                                x + xOffset - xMod,
                                y - 1.5,
                                z + zOffset - zMod,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset - xMod,
                                y - 5.5,
                                z + zOffset - zMod,
                                r, g, b, a);
                    } else {
                        double xOffset = xAxisAligned + 0.5;
                        double zOffset = zAxisAligned + 0.5;
                        double xMod = zAxisAligned * 10;
                        double zMod = xAxisAligned * 10;
                        double xShift = xAxisAligned * 10;
                        double zShift = zAxisAligned * 10;
                        renderFusionRing(buffer,
                                x + xOffset + xMod + xShift,
                                y - 4.5,
                                z + zOffset + zMod + zShift,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset + xMod + xShift,
                                y - 8.5,
                                z + zOffset + zMod + zShift,
                                r, g, b, a);

                        renderFusionRing(buffer,
                                x + xOffset - xMod + xShift,
                                y - 4.5,
                                z + zOffset - zMod + zShift,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset - xMod + xShift,
                                y - 8.5,
                                z + zOffset - zMod + zShift,
                                r, g, b, a);


                        renderFusionRing(buffer,
                                x + xOffset + xMod - xShift,
                                y - 4.5,
                                z + zOffset + zMod - zShift,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset + xMod - xShift,
                                y - 8.5,
                                z + zOffset + zMod - zShift,
                                r, g, b, a);

                        renderFusionRing(buffer,
                                x + xOffset - xMod - xShift,
                                y - 4.5,
                                z + zOffset - zMod - zShift,
                                r, g, b, a);
                        renderFusionRing(buffer,
                                x + xOffset - xMod - xShift,
                                y - 8.5,
                                z + zOffset - zMod - zShift,
                                r, g, b, a);
                    }
                }
            });
        }
    }

    // very useful if you need to render a large number of rings with a single block
    protected void renderFusionRing(BufferBuilder buffer, double x, double y, double z, float r, float g, float b, float a) {
        buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderBufferHelper.renderRing(buffer,
                x, y, z, 6, 0.2, 10, 20,
                r, g, b, a, EnumFacing.Axis.Y);
        Tessellator.getInstance().draw();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (overclock_rating == 1) {
            return new AxisAlignedBB(bbHelper(6, 2, -1),
                    bbHelper(-6, -2, -13));
        } else if (overclock_rating == 2) {
            return new AxisAlignedBB(bbHelper(16, -2, 2),
                    bbHelper(-16, -6, -10));
        } else {
            return new AxisAlignedBB(bbHelper(16, -4, 15),
                    bbHelper(-16, -8, -17));
        }
    }

    /**
     * Returns a BlockPos offset from the controller by the given values
     * @param x Positive is controller left, Negative is controller right
     * @param y Positive is controller up, Negative is controller down
     * @param z Positive is controller front, Negative is controller back
     * @return Offset BlockPos
     */
    protected BlockPos bbHelper(int x, int y, int z) {
        return this.getPos()
                .offset(getFrontFacing(), z)
                .offset(getFrontFacing().getOpposite().rotateY(), x)
                .offset(EnumFacing.UP, y);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }

    @Override
    public boolean isGlobalRenderer() {
        return true;
    }

    static BloomEffectUtil.IBloomRenderFast RENDER_HANDLER = new BloomEffectUtil.IBloomRenderFast() {
        @Override
        public int customBloomStyle() {
            return ConfigHolder.client.shader.fusionBloom.useShader ? ConfigHolder.client.shader.fusionBloom.bloomStyle : -1;
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

    protected static int tier(int overclock_rating) {
        return overclock_rating + GTValues.UV;
    }

    protected static int overclock_rating(int tier) {
        return tier - GTValues.UV;
    }

    public static void init() {
        FusionEUToStartProperty.registerFusionTier(9, "(Stack)");
        FusionEUToStartProperty.registerFusionTier(10, "(Array)");
        FusionEUToStartProperty.registerFusionTier(11, "(Complex)");
        FusionStackPatterns.init();
    }
}