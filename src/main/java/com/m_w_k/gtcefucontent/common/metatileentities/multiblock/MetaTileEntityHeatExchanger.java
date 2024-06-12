package com.m_w_k.gtcefucontent.common.metatileentities.multiblock;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.m_w_k.gtcefucontent.api.capability.impl.HEUGridHandler;
import com.m_w_k.gtcefucontent.api.metatileentity.IHeatExchanger;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.common.block.GTCEFuCMetaBlocks;
import com.m_w_k.gtcefucontent.common.block.blocks.GTCEFuCBlockStandardCasing;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.multiblock.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.gui.GTCEFuCGuiTextures;
import com.m_w_k.gtcefucontent.api.metatileentity.multiblock.GTCEFuCMultiBlockAbility;
import com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil;
import com.m_w_k.gtcefucontent.common.metatileentities.GTCEFuCMetaTileEntities;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.metatileentity.IDataInfoProvider;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockCleanroomCasing;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;

public class MetaTileEntityHeatExchanger extends MultiblockWithDisplayBase
                                         implements IDataInfoProvider, IProgressBarMultiblock, IHeatExchanger {

    protected IItemHandlerModifiable inputInventory;
    protected IItemHandlerModifiable outputInventory;
    protected IMultipleTankHandler inputFluidInventory;
    protected IMultipleTankHandler outputFluidInventory;

    protected final int tier;
    protected final int width;
    protected final int height;
    protected final double speedBonus;
    protected final int hEUCount;
    protected final List<IHEUComponent> notifiedHEUComponentList = new ArrayList<>();
    private @NotNull IItemHandlerModifiable componentsInv = new ItemStackHandler(0);
    protected final HEUGridHandler heuHandler;

    protected int maxPipeVolModifier = -1;

    public MetaTileEntityHeatExchanger(ResourceLocation metaTileEntityId, int tier, int sideLength) {
        this(metaTileEntityId, tier, sideLength, sideLength);
    }

    public MetaTileEntityHeatExchanger(ResourceLocation metaTileEntityId, int tier, int width, int height) {
        super(metaTileEntityId);
        this.tier = tier;
        this.width = width;
        this.height = height;
        this.hEUCount = width * height;
        this.speedBonus = GTCEFuCUtil.geometricMean(width, height);
        this.heuHandler = new HEUGridHandler(this);
    }

    @Override
    protected void updateFormedValid() {
        if (this.heuHandler.isWorkingEnabled()) {
            this.heuHandler.tick();
            // piping I/O
            if (this.componentsInv.getSlots() != 0) {
                // piping fill processing
                if (this.inputInventory.getSlots() != 0) {
                    GTTransferUtils.moveInventoryItems(this.inputInventory, this.componentsInv);
                }

                // piping empty processing
                if (this.outputInventory.getSlots() != 0) {
                    GTTransferUtils.moveInventoryItems(this.componentsInv, this.outputInventory);
                }

                // recheck piping validity
                if (!this.getNotifiedHEUComponentList().isEmpty()) {
                    this.notifiedHEUComponentList.clear();
                    heuHandler.updateComponentPipingStatus();
                }
            }
        } else {
            // clear our stored thermal energy when stopped
            heuHandler.setThermalEnergy(0);
        }
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.initializeAbilities();
        this.heuHandler.onStructureForm(getAbilities(GTCEFuCMultiBlockAbility.HEU_COMPONENT));
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.resetTileAbilities();
        this.heuHandler.onStructureInvalidate();
    }

    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
        this.componentsInv = new ItemHandlerList(getAbilities(GTCEFuCMultiBlockAbility.HEU_COMPONENT));
    }

    private void resetTileAbilities() {
        this.inputInventory = new ItemStackHandler(0);
        this.inputFluidInventory = new FluidTankList(false);
        this.outputInventory = new ItemStackHandler(0);
        this.outputFluidInventory = new FluidTankList(false);
        this.componentsInv = new ItemStackHandler(0);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHeatExchanger(this.metaTileEntityId, this.tier, this.width, this.height);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return wherify(FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.UP, RelativeDirection.FRONT)
                .aisle(generateAisle(AisleType.START))
                .aisle(generateAisle(AisleType.ENDPOINT))
                .aisle(generateAisle(AisleType.HOLDER)).setRepeatable(4, 16)
                .aisle(generateAisle(AisleType.ENDPOINT))
                .aisle(generateAisle(AisleType.END)), this.tier).build();
    }

    protected String[] generateAisle(AisleType aisleType) {
        String[] aisle = new String[this.height + 2];
        aisle[0] = switch (aisleType) {
            case START -> {
                int firstCount = this.width / 2;
                int secondCount = this.width - 1 - firstCount;
                yield 'C' + StringUtils.repeat('I', firstCount) + 'X' + StringUtils.repeat('I', secondCount) + 'C';
            }
            default -> StringUtils.repeat('C', this.width + 2);
            case END -> 'C' + StringUtils.repeat('I', this.width) + 'C';
        };
        int lastIndex = this.height + 1;
        for (int i = 1; i < lastIndex; i++) {
            aisle[i] = switch (aisleType) {
                case START, END -> '#' + StringUtils.repeat('I', this.width) + '#';
                case ENDPOINT -> 'G' + StringUtils.repeat('E', this.width) + 'G';
                case HOLDER -> 'G' + StringUtils.repeat('P', this.width) + 'G';
            };
        }
        aisle[lastIndex] = switch (aisleType) {
            case START, END -> 'C' + StringUtils.repeat('I', this.width) + 'C';
            default -> StringUtils.repeat('C', this.width + 2);
        };
        return aisle;
    }

    protected FactoryBlockPattern wherify(FactoryBlockPattern pattern, int tier) {
        return pattern
                .where('I', stateIndex(0).setMinGlobalLimited((tier - 3) * tier).or(autoAbilities(true, false))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setPreviewCount(2))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setPreviewCount(2))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1, 1))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1, 1)))
                .where('C', stateIndex(1))
                .where('G', stateIndex(2))
                .where('E', metaTileEntities(GTCEFuCMetaTileEntities.HEU_ENDPOINTS))
                .where('P', metaTileEntities(GTCEFuCMetaTileEntities.HEU_HOLDERS))
                .where('X', selfPredicate())
                .where('#', any());
    }

    @Nonnull
    protected static TraceabilityPredicate stateIndex(int id) {
        return states(switch (id) {
            default -> MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.INVAR_HEATPROOF);
            case 1 -> GTCEFuCMetaBlocks.STANDARD_CASING
                    .getState(GTCEFuCBlockStandardCasing.CasingType.THERMOSTABLE_CERAMIC);
            case 2 -> MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
        });
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.HEAT_PROOF_CASING;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return GTCEFuCTextures.HEAT_EXCHANGER_OVERLAY;
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        this.getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                this.isActive() || !this.heuHandler.isWorkingEnabled(), this.heuHandler.isWorkingEnabled());
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementMaxPipeVolMod)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gtcefucontent.multiblock.heat_exchanger.max_pipe_vol_mod_decrement"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementMaxPipeVolMod)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gtcefucontent.multiblock.heat_exchanger.max_pipe_vol_mod_increment"));
        return group;
    }

    private void incrementMaxPipeVolMod(Widget.ClickData clickData) {
        if (this.maxPipeVolModifier == -1) return;
        this.maxPipeVolModifier++;
        if (this.heuHandler.getCurrentPipeVolModifier() < this.maxPipeVolModifier) {
            this.maxPipeVolModifier = -1;
        }
    }

    private void decrementMaxPipeVolMod(Widget.ClickData clickData) {
        if (this.maxPipeVolModifier == -1) {
            this.maxPipeVolModifier = this.heuHandler.getCurrentPipeVolModifier();
        } else if (this.maxPipeVolModifier > 0) this.maxPipeVolModifier--;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, @Nonnull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtcefucontent.machine.heat_exchanger.info",
                this.hEUCount));
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.COOLING;
    }

    @Nonnull
    @Override
    public List<ITextComponent> getDataInfo() {
        List<ITextComponent> list = new ArrayList<>();
        if (ConfigHolder.machines.enableMaintenance && hasMaintenanceMechanics()) {
            list.add(new TextComponentTranslation("behavior.tricorder.multiblock_maintenance",
                    new TextComponentTranslation(TextFormattingUtil.formatNumbers(getNumMaintenanceProblems()))
                            .setStyle(new Style().setColor(TextFormatting.RED))));
        }
        return list;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            heuHandler.addInfo(textList);
        }
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(heuHandler.isWorkingEnabled(), heuHandler.isActive())
                .addWorkingStatusLine();
    }

    @Override
    protected void addWarningText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (isStructureFormed()) {
            heuHandler.addWarnings(textList);
        }
    }

    @Override
    protected void addErrorText(List<ITextComponent> textList) {
        super.addErrorText(textList);
        if (isStructureFormed()) {
            heuHandler.addErrors(textList);
        }
    }

    @Override
    public boolean isActive() {
        return this.isStructureFormed() && this.heuHandler.isActive() && this.heuHandler.isWorkingEnabled();
    }

    public void addNotifiedHeuComponent(IHEUComponent component) {
        if (!notifiedHEUComponentList.contains(component)) {
            this.notifiedHEUComponentList.add(component);
        }
    }

    public List<IHEUComponent> getNotifiedHEUComponentList() {
        return notifiedHEUComponentList;
    }

    @Override
    public double getFillPercentage(int index) {
        return (double) this.heuHandler.getThermalEnergy() / this.heuHandler.getMaxHeat();
    }

    @Override
    public TextureArea getProgressBarTexture(int index) {
        return GTCEFuCGuiTextures.PROGRESS_BAR_HEU_HEAT;
    }

    @Override
    public void addBarHoverText(List<ITextComponent> hoverList, int index) {
        ITextComponent heatInfo = TextComponentUtil.translationWithColor(
                TextFormatting.DARK_RED,
                "gtcefucontent.machine.heat_exchanger.heat1",
                TextFormattingUtil.formatLongToCompactString(this.heuHandler.getThermalEnergy()),
                TextFormattingUtil.formatLongToCompactString(this.heuHandler.getMaxHeat()));
        hoverList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "gtcefucontent.machine.heat_exchanger.heat2",
                heatInfo));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("Limit", this.maxPipeVolModifier);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        this.maxPipeVolModifier = data.getInteger("Limit");
        super.readFromNBT(data);
    }

    @Override
    public IMultipleTankHandler getInputFluidInventory() {
        return this.inputFluidInventory;
    }

    @Override
    public IMultipleTankHandler getOutputFluidInventory() {
        return this.outputFluidInventory;
    }

    @Override
    public int getHEUCount() {
        return this.hEUCount;
    }

    @Override
    public double getSpeedBonus() {
        return this.speedBonus;
    }

    @Override
    public int getMaxPipeVolMultiplier() {
        return this.maxPipeVolModifier;
    }

    protected enum AisleType {
        START, ENDPOINT, HOLDER, END;
    }

}
