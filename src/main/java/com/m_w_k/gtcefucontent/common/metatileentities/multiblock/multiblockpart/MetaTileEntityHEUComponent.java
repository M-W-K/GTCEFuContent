package com.m_w_k.gtcefucontent.common.metatileentities.multiblock.multiblockpart;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.metatileentity.multiblock.GTCEFuCMultiBlockAbility;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.client.renderer.texture.cube.AxisAlignedCubeRenderer;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.MetaTileEntityHeatExchanger;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.GregtechDataCodes;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;

public class MetaTileEntityHEUComponent extends MetaTileEntityMultiblockPart
                                        implements IMultiblockAbilityPart<IHEUComponent>, IHEUComponent {

    private static final Map<Item, Boolean> validItemCache = new HashMap<>();
    private final HEUComponentType type;
    private boolean validPiping = false;
    private boolean stackIsDirty = true;

    public MetaTileEntityHEUComponent(ResourceLocation metaTileEntityId, HEUComponentType type) {
        super(metaTileEntityId, 7);
        this.type = type;
        initializeInventory();
    }

    @SuppressWarnings("unused")
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
            AxisAlignedCubeRenderer renderer;
            var controller = getController();
            if (controller != null && controller.isActive()) {
                renderer = getActiveOverlay();
            } else {
                renderer = getOverlay();
            }
            if (renderer != null) {
                EnumFacing facing = getFrontFacing();
                // rotate ourselves if we're being controlled by a heat exchanger
                if (controller instanceof MetaTileEntityHeatExchanger) {
                    facing = controller.getFrontFacing();
                }
                // unfortunately our renderOriented overrides the passed, so we need to generate a new one
                facing = EnumFacing.getFacingFromAxis(facing.getAxisDirection(), facing.getAxis());
                renderer.renderOriented(renderState, translation, pipeline, facing);
            }
        }
    }

    public AxisAlignedCubeRenderer getOverlay() {
        return hasValidPiping() ? GTCEFuCTextures.HEU_COMPONENT_FULL_OVERLAYS.get(this.type.ordinal()) :
                GTCEFuCTextures.HEU_COMPONENT_EMPTY_OVERLAYS.get(this.type.ordinal());
    }

    public AxisAlignedCubeRenderer getActiveOverlay() {
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
        return GTCEFuCMultiBlockAbility.HEU_COMPONENT;
    }

    public void registerAbilities(List<IHEUComponent> abilityList) {
        abilityList.add(this);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        syncWrite(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        syncRead(buf);
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.UPDATE_ITEM_COUNT) {
            syncRead(buf);
        }
    }

    private void syncWrite(PacketBuffer buf) {
        for (int i = 0; i < this.getInventorySize(); i++) {
            buf.writeItemStack(this.getStackInSlot(i));
        }
    }

    private void syncRead(PacketBuffer buf) {
        for (int i = 0; i < getInventorySize(); i++) {
            try {
                this.setStackInSlot(i, buf.readItemStack());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public IItemHandler getItemInventory() {
        return this;
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        if (this.type == null) return super.createImportItemHandler();
        return new ItemStackHandler(this.getInventorySize());
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
        if (!simulate) {
            this.setStackDirty();
            this.hasValidPiping();
        }
        return staack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack stack = this.importItems.extractItem(slot, amount, simulate);
        if (!simulate) {
            this.setStackDirty();
            this.hasValidPiping();
        }
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.importItems.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.importItems.isItemValid(slot, stack) && isValidPipe(stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.importItems.setStackInSlot(slot, stack);
        this.setStackDirty();
        this.hasValidPiping();
    }

    @Override
    public boolean hasValidPiping() {
        if (stackIsDirty) {
            stackIsDirty = false;
            ItemStack stack;
            for (int i = 0; i < this.getInventorySize(); i++) {
                stack = this.getStackInSlot(i);
                if (!(isValidPipe(stack) && stack.getCount() == 64)) return validPipingUpdateCheck(false);
            }
            return validPipingUpdateCheck(true);
        } else return this.validPiping;
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
        MaterialStack materialStack = OreDictUnifier.getMaterial(stack);
        if (materialStack != null) {
            return materialStack.material;
        }
        return null;
    }

    public static boolean isValidPipe(ItemStack stack) {
        // check against the cache
        Boolean cacheResult = validItemCache.get(stack.getItem());
        if (cacheResult != null) return cacheResult;

        // do fresh validity calculations
        OrePrefix prefix = OreDictUnifier.getPrefix(stack);
        MaterialStack material = null;
        if (prefix != null && prefix == OrePrefix.pipeTinyFluid) {
            material = OreDictUnifier.getMaterial(stack);
        }
        validItemCache.put(stack.getItem(), material != null);
        return material != null;
    }

    private boolean validPipingUpdateCheck(boolean validity) {
        if (validity != this.validPiping) {
            if (getWorld() != null && !getWorld().isRemote) {
                writeCustomData(GregtechDataCodes.UPDATE_ITEM_COUNT, this::syncWrite);
            } else {
                this.scheduleRenderUpdate();
            }
            this.validPiping = validity;
        }
        return validity;
    }

    private int getInventorySize() {
        return this.type.isLargeInventory() ? 2 : 1;
    }

    private void setStackDirty() {
        this.stackIsDirty = true;
    }
}
