package com.m_w_k.gtcefucontent.api.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.m_w_k.gtcefucontent.GTCEFuContent;

import gregtech.api.GTValues;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.api.recipes.logic.OverclockingLogic;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlag;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.BlastProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.loaders.recipe.CraftingComponent;

public class ForgingFurnaceRecipeProducer {

    public static final ForgingFurnaceRecipeProducer DEFAULT_PRODUCER = new ForgingFurnaceRecipeProducer();

    private static final Map<MaterialFlag, OrePrefix> FORGEABLE_MATERIAL_FLAGS = new HashMap<>(15);

    private static final List<OrePrefix> FORGEABLE_PIPES = new ArrayList<>(0);

    // awkward, but it's the only way to avoid a fatal error for some reason.
    private void populateReferences() {
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_DENSE, OrePrefix.plateDense);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_FRAME, OrePrefix.frameGt);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_PLATE, OrePrefix.plate);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_DOUBLE_PLATE, OrePrefix.plateDouble);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_FOIL, OrePrefix.foil);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_BOLT_SCREW, OrePrefix.bolt);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_GEAR, OrePrefix.gear);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_SMALL_GEAR, OrePrefix.gearSmall);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_LONG_ROD, OrePrefix.stickLong);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_ROD, OrePrefix.stick);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_RING, OrePrefix.ring);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_ROTOR, OrePrefix.rotor);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_ROUND, OrePrefix.round);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_SPRING, OrePrefix.spring);
        FORGEABLE_MATERIAL_FLAGS.put(MaterialFlags.GENERATE_SPRING_SMALL, OrePrefix.springSmall);

        FORGEABLE_PIPES.add(OrePrefix.pipeHugeItem);
        FORGEABLE_PIPES.add(OrePrefix.pipeLargeItem);
        FORGEABLE_PIPES.add(OrePrefix.pipeNormalItem);
        FORGEABLE_PIPES.add(OrePrefix.pipeSmallItem);
        FORGEABLE_PIPES.add(OrePrefix.pipeHugeFluid);
        FORGEABLE_PIPES.add(OrePrefix.pipeLargeFluid);
        FORGEABLE_PIPES.add(OrePrefix.pipeNormalFluid);
        // skip quadruple and nonuple; they can be straightforwardly crafted.
        FORGEABLE_PIPES.add(OrePrefix.pipeSmallFluid);
        FORGEABLE_PIPES.add(OrePrefix.pipeTinyFluid);
    }

    public void produce(List<Material> blastMaterials) {
        populateReferences();

        for (Material material : blastMaterials) {
            BlastProperty property = material.getProperty(PropertyKey.BLAST);
            RecipeBuilder<BlastRecipeBuilder> builder = createBuilder(property, material);

            // Cooled ingot
            buildRecipes(property, OrePrefix.ingot, material, 0, builder);
            int i = 0;
            // Forgeable components
            for (Map.Entry<MaterialFlag, OrePrefix> entry : FORGEABLE_MATERIAL_FLAGS.entrySet()) {
                i++;
                if (material.hasFlag(entry.getKey())) {
                    buildRecipes(property, entry.getValue(), material, i, builder);
                }
            }

            // Pipes
            if (material.hasProperty(PropertyKey.ITEM_PIPE)) {
                for (OrePrefix prefix : FORGEABLE_PIPES.subList(0, 3)) {
                    i++;
                    buildRecipes(property, prefix, material, i, builder);
                }
            } else if (material.hasProperty(PropertyKey.FLUID_PIPE)) {
                for (OrePrefix prefix : FORGEABLE_PIPES.subList(4, 8)) {
                    i++;
                    buildRecipes(property, prefix, material, i, builder);
                }

            }

            // One day, maybe, I'll get texture sets for hot parts - and then cooling will no longer be inline!
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    protected @NotNull BlastRecipeBuilder createBuilder(BlastProperty property, Material material) {
        BlastRecipeBuilder builder = GTCEFuCRecipeMaps.FORGING_FURNACE_RECIPES.recipeBuilder();
        // apply the duration override
        int duration = property.getDurationOverride();
        if (duration < 0) duration = Math.max(1, (int) (material.getMass() * property.getBlastTemperature() / 50L));

        // 5% penalty for forging
        builder.duration((int) (duration * 1.05));

        // apply the EUt override
        int EUt = property.getEUtOverride();
        if (EUt < 0) EUt = GTValues.VA[GTValues.MV];
        builder.EUt(EUt);

        // 100K penalty for forging
        return builder.blastFurnaceTemp(property.getBlastTemperature() + 100);
    }

    protected void buildRecipes(BlastProperty property, OrePrefix prefix, Material material, int circuitMeta,
                                RecipeBuilder<BlastRecipeBuilder> builder) {
        // early copy in order to keep us separate.
        builder = builder.copy();

        // no forgeable materials have special behavior with getMaterialAmount
        long ratio = prefix.getMaterialAmount(null);

        // these should always return proper whole numbers
        // only weird ratios like 3/2 will break
        int inputAmount = (int) Math.max(ratio / GTValues.M, 1);
        int outputAmount = (int) Math.max(GTValues.M / ratio, 1);

        builder.input(OrePrefix.dust, material, inputAmount);
        builder.output(prefix, material, outputAmount);

        int duration = inputAmount * builder.getDuration();

        // calculate overclock level of inline cooling
        // log base x of y = log(y)/log(x)
        int coolingOverclock = MathHelper.ceil(
                Math.log(builder.getEUt() / 128.0) / Math.log(OverclockingLogic.STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER));

        int coolerDuration = (int) (inputAmount
        // Freezer formula, I think. It's hard to find, ok? Unobfuscated greg might as well still be obfuscated.
                * material.getMass() * 3
                // inefficiency - square root of temp/1000
                // aka, increasing temp by x4 increases cooling time by x2; perfect efficiency at 1000K
                * Math.max(1, Math.pow(material.getBlastTemperature() / 1000.0, 1 / 2.0))
                // overclocking; do not allow underclocks.
                / Math.pow(OverclockingLogic.STANDARD_OVERCLOCK_DURATION_DIVISOR, Math.max(0, coolingOverclock)));

        // add liquid helium if necessary
        if (material.getBlastTemperature() >= 5000) {
            builder = builder
                    .fluidInputs(Materials.LiquidHelium.getFluid(500 * inputAmount))
                    .fluidOutputs(Materials.Helium.getFluid(250 * inputAmount));
        }
        // remove cooling time if no hot ingot exists
        else if (!OrePrefix.ingot.doGenerateItem(material)) {
            coolerDuration = 0;
        }

        if (duration <= 0) {
            GTCEFuContent.log(
                    "A Forging Furnace recipe with an invalid duration attempted to sneak through! It was cancelled for safety.",
                    GTCEFuContent.LogType.WARN);
            GTCEFuContent.log("Recipe was " + material + " being transformed into " + prefix,
                    GTCEFuContent.LogType.WARN);
            return;
        }

        // build the gas recipe if it exists
        if (property.getGasTier() != null) {
            RecipeBuilder<BlastRecipeBuilder> builderGas = builder.copy();
            FluidStack gas = CraftingComponent.EBF_GASES.get(property.getGasTier());
            builderGas.circuitMeta(circuitMeta)
                    .fluidInputs(new FluidStack(gas, gas.amount * inputAmount))
                    .duration((int) (duration * 0.67) + coolerDuration)
                    .buildAndRegister();
        }

        // build the non-gas recipe
        // use a tiny amount of graphite to prevent recipe conflict
        builder.circuitMeta(circuitMeta)
                .input(OrePrefix.dustTiny, Materials.Graphite)
                .duration(duration + coolerDuration)
                .buildAndRegister();
    }
}
