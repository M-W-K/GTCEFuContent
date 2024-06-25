package com.m_w_k.gtcefucontent.asmm.visitors;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import gregtech.asm.util.ObfMapping;

public class FluidTooltipUtilVisitor extends MethodVisitor implements Opcodes {

    public static final String TARGET_CLASS_NAME = "gregtech/api/util/FluidTooltipUtil";
    public static final ObfMapping TARGET_METHOD = new ObfMapping(TARGET_CLASS_NAME, "getFluidTooltip",
            targetSignature());

    private static final String OWNER = "com/m_w_k/gtcefucontent/asmm/hooks/FluidTooltipUtilHooks";
    private static final String SIGNATURE = signature();
    private static final String METHOD_NAME = "fluidTooltipOverride";

    public FluidTooltipUtilVisitor(MethodVisitor mv) {
        super(ASM5, mv);
    }

    @Override
    public void visitCode() {
        mv.visitVarInsn(ALOAD, 0); // FluidStack
        mv.visitMethodInsn(INVOKESTATIC, OWNER, METHOD_NAME, SIGNATURE, false);
        mv.visitInsn(ARETURN);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitCode();
    }

    // public boolean apply(@Nullable ItemStack input)
    private static String targetSignature() {
        return "(" +
                "Lnet/minecraftforge/fluids/FluidStack;" + // FluidStack
                ")Ljava/util/List;"; // return list
    }

    // public static boolean extendedApply(@Nullable ItemStack input, @NotNull OreIngredient ingredient)
    private static String signature() {
        return "(" +
                "Lnet/minecraftforge/fluids/FluidStack;" + // FluidStack
                ")Ljava/util/List;"; // return list
    }
}
