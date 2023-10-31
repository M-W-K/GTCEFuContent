package com.m_w_k.gtcefucontent.common.metatileentities.multiblock.multiblockpart.heu;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.metatileentity.multiblock.GTCEFuCMultiBlockAbilities;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import gregtech.api.capability.impl.ItemHandlerProxy;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.items.materialitem.MetaPrefixItem;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetaTileEntityHEUComponent extends MetaTileEntityMultiblockPart implements IMultiblockAbilityPart<IHEUComponent>, IHEUComponent {

    private final HEUComponentType type;
    private boolean validPiping;

    public MetaTileEntityHEUComponent(ResourceLocation metaTileEntityId, HEUComponentType type) {
        super(metaTileEntityId, 7);
        this.type = type;
        this.importItems = new ItemStackHandler(this.getInventorySize());
        this.itemInventory = new ItemHandlerProxy(importItems, exportItems);
    }

    public HEUComponentType getType() {
        return type;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityHEUComponent(metaTileEntityId, this.type);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            SimpleOverlayRenderer renderer;
            var controller = getController();
            if (controller != null && controller.isActive()) {
                renderer = getActiveOverlay();
            } else {
                renderer = getOverlay();
            }
            if (renderer != null) {
                renderer.renderSided(getFrontFacing(), renderState, translation, pipeline);
                renderer.renderSided(getFrontFacing().getOpposite(), renderState, translation, pipeline);
            }
        }
    }

    public SimpleOverlayRenderer getOverlay() {
        return hasValidPiping() ? GTCEFuCTextures.HEU_COMPONENT_FULL_OVERLAYS.get(this.type.ordinal()) :
                GTCEFuCTextures.HEU_COMPONENT_EMPTY_OVERLAYS.get(this.type.ordinal());
    }

    public SimpleOverlayRenderer getActiveOverlay() {
        // We shouldn't be able to go active unless we're full of pipes.
        return GTCEFuCTextures.HEU_COMPONENT_ACTIVE_OVERLAYS.get(this.type.ordinal());
    }

    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return this.createUITemplate(entityPlayer, getInventorySize()).build(this.getHolder(), entityPlayer);
    }

    private ModularUI.Builder createUITemplate(EntityPlayer player, int gridSize) {
        int backgroundWidth = 176;
        int center = backgroundWidth / 2;

        int gridStartX = center - (gridSize * 9);

        int inventoryStartX = center - 9 - 4 * 18;
        int inventoryStartY = 36 + 12;

        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, backgroundWidth, 36 + 94)
                .label(10, 5, getMetaFullName());

        for (int x = 0; x < gridSize; x++) {
            // WE are the item handler
            builder.widget(new SlotWidget(this, x,
                    gridStartX + x * 18, 18, true, true)
                    .setBackgroundTexture(GuiTextures.SLOT, GuiTextures.PIPE_OVERLAY_1));
        }

        return builder.bindPlayerInventory(player.inventory, GuiTextures.SLOT, inventoryStartX, inventoryStartY);
    }

    @Override
    public MultiblockAbility<IHEUComponent> getAbility() {
        return GTCEFuCMultiBlockAbilities.HEU_COMPONENT;
    }

    public void registerAbilities(List<IHEUComponent> abilityList) {
        abilityList.add(this);
    }

    @Override
    public int getSlots() {
        return this.importItems.getSlots();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.importItems.getStackInSlot(slot);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!this.isItemValid(slot, stack)) return stack;
        ItemStack staack = this.importItems.insertItem(slot, stack, simulate);
        // We have to perform the operation before checking for new validity
        if (!simulate) hasValidPiping();
        return staack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = this.importItems.extractItem(slot, amount, simulate);
        // We have to perform the operation before checking for new validity
        if (!simulate) hasValidPiping();
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.importItems.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.importItems.isItemValid(slot, stack) && this.isValidPipe(stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.importItems.setStackInSlot(slot, stack);
        hasValidPiping();
    }

    @Override
    public boolean hasValidPiping() {
        ItemStack stack;
        for (int i = 0; i < this.getInventorySize(); i++) {
            stack = this.getStackInSlot(i);
            if (!(this.isValidPipe(stack) && stack.getCount() == 64)) return false;
        }
        return true;
    }

    @Override
    public HEUComponentType getComponentType() {
        return this.type;
    }

    @Nullable
    @Override
    public Material getPipeMaterial() {
        if (!this.hasValidPiping()) return null;
        ItemStack stack = this.getStackInSlot(0);
        if (stack.getItem() instanceof MetaPrefixItem prefixItem) {
            return prefixItem.getMaterial(stack);
        }
        return null;
    }

    private boolean isValidPipe(ItemStack stack) {
        OrePrefix prefix = OreDictUnifier.getPrefix(stack);
        if (prefix != null && prefix == OrePrefix.pipeTinyFluid) {
            MaterialStack material = OreDictUnifier.getMaterial(stack);
            return validPipingUpdateCheck(material != null);
        }
        return validPipingUpdateCheck(false);
    }

    private boolean validPipingUpdateCheck(boolean validity) {
        if (validity != this.validPiping) {
            this.scheduleRenderUpdate();
            this.validPiping = validity;
        }
        return validity;
    }

    private int getInventorySize() {
        return this.type.isLargeInventory() ? 2 : 1;
    }
}
