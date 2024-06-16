package com.m_w_k.gtcefucontent.api.unification.properties;

import net.minecraftforge.fluids.Fluid;

import com.m_w_k.gtcefucontent.api.fluids.EutecticFluid;
import com.m_w_k.gtcefucontent.api.fluids.GTCEFuCFluidStorageKeys;

import gregtech.api.fluids.FluidConstants;
import gregtech.api.fluids.store.FluidStorage;
import gregtech.api.unification.material.properties.IMaterialProperty;
import gregtech.api.unification.material.properties.MaterialProperties;
import gregtech.api.unification.material.properties.PropertyKey;

public class GTCEFuCEutecticMaterialProperty implements IMaterialProperty {

    private final int minTemp;
    private int defaultTemp;
    private final int maxTemp;
    private final int thermalCapacity1mb;

    public GTCEFuCEutecticMaterialProperty(int minTemp, int maxTemp, int thermalCapacity1mb) {
        this(minTemp, FluidConstants.ROOM_TEMPERATURE, maxTemp, thermalCapacity1mb);
    }

    public GTCEFuCEutecticMaterialProperty(int minTemp, int defaultTemp, int maxTemp, int thermalCapacity1mb) {
        this.minTemp = minTemp;
        this.defaultTemp = defaultTemp;
        this.maxTemp = maxTemp;
        this.thermalCapacity1mb = thermalCapacity1mb;
    }

    public int getThermalCapacity1mb() {
        return thermalCapacity1mb;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.FLUID);
        FluidStorage storage = properties.getProperty(PropertyKey.FLUID).getStorage();
        Fluid fluid = storage.get(GTCEFuCFluidStorageKeys.EUTECTIC);
        if (fluid == null) {
            fluid = new EutecticFluid.Builder()
                    .defaultTemp(this.defaultTemp)
                    .minTemp(this.minTemp)
                    .maxTemp(this.maxTemp)
                    .build(properties.getMaterial().getModid(), properties.getMaterial(),
                            GTCEFuCFluidStorageKeys.EUTECTIC);
            storage.store(GTCEFuCFluidStorageKeys.EUTECTIC, fluid);

            properties.getProperty(PropertyKey.FLUID).setPrimaryKey(GTCEFuCFluidStorageKeys.EUTECTIC);
        }

        this.defaultTemp = fluid.getTemperature();
    }
}
