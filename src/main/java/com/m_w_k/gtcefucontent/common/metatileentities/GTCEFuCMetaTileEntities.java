package com.m_w_k.gtcefucontent.common.metatileentities;

import static com.m_w_k.gtcefucontent.api.capability.IHEUComponent.HEUComponentType.*;
import static com.m_w_k.gtcefucontent.api.util.GTCEFuCUtil.gtcefucId;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.m_w_k.gtcefucontent.api.capability.IHEUComponent;
import com.m_w_k.gtcefucontent.api.render.GTCEFuCTextures;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.*;
import com.m_w_k.gtcefucontent.common.metatileentities.multiblock.multiblockpart.MetaTileEntityHEUComponent;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidConstants;
import gregtech.client.renderer.texture.Textures;

public final class GTCEFuCMetaTileEntities {

    public static MetaTileEntityInfinityExtractor INFINITY_EXTRACTOR;
    public static MetaTileEntityPneumaticInfuser PNEUMATIC_INFUSER;
    public static MetaTileEntitySympatheticCombustor SYMPATHETIC_COMBUSTOR;
    public static MetaTileEntityForgingFurnace FORGING_FURNACE;
    public static MetaTileEntityElectrodeSmelter ELECTRODE_SMELTER;
    public static MetaTileEntityMegaSteamEngine MEGA_STEAM_ENGINE;
    public static MetaTileEntityStarSiphon STAR_SIPHON;
    public static MetaTileEntityAntimatterCompressor ANTIMATTER_COMPRESSOR;
    public static final MetaTileEntityFusionStack[] FUSION_STACK = new MetaTileEntityFusionStack[3];
    public static MetaTileEntityNaqFuelCellPacker NAQ_FUEL_CELL_PACKER;
    public static final MetaTileEntityNaqReactor[] NAQ_REACTOR = new MetaTileEntityNaqReactor[3];
    public static MetaTileEntityLargeNaqReactor LARGE_NAQ_REACTOR;

    public static final MetaTileEntityHeatExchanger[] HEAT_EXCHANGER = new MetaTileEntityHeatExchanger[3];
    public static final MetaTileEntityHeatReclaimer[] HEAT_RECLAIMER = new MetaTileEntityHeatReclaimer[2];
    public static MetaTileEntityHeatDisperser HEAT_DISPERSER;

    public static final Map<IHEUComponent.HEUComponentType, MetaTileEntityHEUComponent> HEU_COMPONENTS =
            new EnumMap<>(IHEUComponent.HEUComponentType.class);
    public static MetaTileEntityHEUComponent[] HEU_ENDPOINTS = new MetaTileEntityHEUComponent[2];
    public static MetaTileEntityHEUComponent[] HEU_HOLDERS = new MetaTileEntityHEUComponent[3];

    private GTCEFuCMetaTileEntities() {}

    public static void init() {
        INFINITY_EXTRACTOR = registerMetaTileEntity(3000,
                new MetaTileEntityInfinityExtractor(gtcefucId("infinity_extractor")));
        PNEUMATIC_INFUSER = registerMetaTileEntity(3001,
                new MetaTileEntityPneumaticInfuser(gtcefucId("pneumatic_infuser")));
        SYMPATHETIC_COMBUSTOR = registerMetaTileEntity(3002,
                new MetaTileEntitySympatheticCombustor(gtcefucId("sympathetic_combustor")));
        FORGING_FURNACE = registerMetaTileEntity(3003,
                new MetaTileEntityForgingFurnace(gtcefucId("forging_furnace")));
        ELECTRODE_SMELTER = registerMetaTileEntity(3004,
                new MetaTileEntityElectrodeSmelter(gtcefucId("electrode_blast_smelter")));
        MEGA_STEAM_ENGINE = registerMetaTileEntity(3005,
                new MetaTileEntityMegaSteamEngine(gtcefucId("mega_steam_engine")));
        FUSION_STACK[0] = registerMetaTileEntity(3006,
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.stack"), GTValues.UHV));
        FUSION_STACK[1] = registerMetaTileEntity(3007,
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.array"), GTValues.UEV));
        FUSION_STACK[2] = registerMetaTileEntity(3008,
                new MetaTileEntityFusionStack(gtcefucId("fusion_stack.complex"), GTValues.UIV));
        STAR_SIPHON = registerMetaTileEntity(3009,
                new MetaTileEntityStarSiphon(gtcefucId("star_siphon")));
        ANTIMATTER_COMPRESSOR = registerMetaTileEntity(3010,
                new MetaTileEntityAntimatterCompressor(gtcefucId("antimatter_compressor")));
        NAQ_FUEL_CELL_PACKER = registerMetaTileEntity(3011,
                new MetaTileEntityNaqFuelCellPacker(gtcefucId("naq_fuel_cell_packer")));
        NAQ_REACTOR[0] = registerMetaTileEntity(3012, new MetaTileEntityNaqReactor(gtcefucId("naq_reactor_1"),
                GTCEFuCTextures.NAQ_REACTOR_OVERLAY, GTValues.ZPM));
        NAQ_REACTOR[1] = registerMetaTileEntity(3013, new MetaTileEntityNaqReactor(gtcefucId("naq_reactor_2"),
                GTCEFuCTextures.NAQ_REACTOR_OVERLAY, GTValues.UV));
        NAQ_REACTOR[2] = registerMetaTileEntity(3014, new MetaTileEntityNaqReactor(gtcefucId("naq_reactor_3"),
                GTCEFuCTextures.NAQ_REACTOR_OVERLAY, GTValues.UHV));
        LARGE_NAQ_REACTOR = registerMetaTileEntity(3015, new MetaTileEntityLargeNaqReactor(gtcefucId("large_naq_reactor")));


        HEAT_EXCHANGER[0] = registerMetaTileEntity(3100,
                new MetaTileEntityHeatExchanger(gtcefucId("heat_exchanger.small"), GTValues.LuV, 3));
        HEAT_EXCHANGER[1] = registerMetaTileEntity(3101,
                new MetaTileEntityHeatExchanger(gtcefucId("heat_exchanger.medium"), GTValues.ZPM, 4));
        HEAT_EXCHANGER[2] = registerMetaTileEntity(3102,
                new MetaTileEntityHeatExchanger(gtcefucId("heat_exchanger.large"), GTValues.UV, 5));

        HEAT_RECLAIMER[0] = registerMetaTileEntity(3110,
                new MetaTileEntityHeatReclaimer(gtcefucId("heat_reclaimer.basic"), false));
        HEAT_RECLAIMER[1] = registerMetaTileEntity(3111,
                new MetaTileEntityHeatReclaimer(gtcefucId("heat_reclaimer.advanced"), true));
        HEAT_DISPERSER = registerMetaTileEntity(3120,
                new MetaTileEntityHeatDisperser(gtcefucId("heat_disperser"),
                        FluidConstants.ROOM_TEMPERATURE, 0.995, 100));

        HEU_COMPONENTS.put(E_STANDARD, registerMetaTileEntity(3200,
                new MetaTileEntityHEUComponent(gtcefucId("heu_endpoint.standard"), E_STANDARD)));
        HEU_COMPONENTS.put(E_RETURNING, registerMetaTileEntity(3201,
                new MetaTileEntityHEUComponent(gtcefucId("heu_endpoint.returning"), E_RETURNING)));
        HEU_COMPONENTS.put(H_STANDARD, registerMetaTileEntity(3210,
                new MetaTileEntityHEUComponent(gtcefucId("heu_pipe_holder.standard"), H_STANDARD)));
        HEU_COMPONENTS.put(H_CONDUCTIVE, registerMetaTileEntity(3211,
                new MetaTileEntityHEUComponent(gtcefucId("heu_pipe_holder.conductive"), H_CONDUCTIVE)));
        HEU_COMPONENTS.put(H_EXPANDED, registerMetaTileEntity(3212,
                new MetaTileEntityHEUComponent(gtcefucId("heu_pipe_holder.expanded"), H_EXPANDED)));

        List<MetaTileEntityHEUComponent> endpoints = new ArrayList<>(2);
        List<MetaTileEntityHEUComponent> holders = new ArrayList<>(3);

        Iterator<Map.Entry<IHEUComponent.HEUComponentType, MetaTileEntityHEUComponent>> iterator = HEU_COMPONENTS
                .entrySet().iterator();
        Map.Entry<IHEUComponent.HEUComponentType, MetaTileEntityHEUComponent> component;
        while (iterator.hasNext()) {
            component = iterator.next();
            if (component.getKey().isEndpoint()) {
                endpoints.add(component.getValue());
            } else {
                holders.add(component.getValue());
            }
        }
        HEU_ENDPOINTS = endpoints.toArray(new MetaTileEntityHEUComponent[2]);
        HEU_HOLDERS = holders.toArray(new MetaTileEntityHEUComponent[3]);
    }
}
