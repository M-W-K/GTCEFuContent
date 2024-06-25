package com.m_w_k.gtcefucontent.asmm.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import gregtech.asm.util.ObfMapping;

public class UniversalBucketVisitor extends MethodVisitor implements Opcodes {

    public static final String TARGET_CLASS_NAME = "net/minecraftforge/fluids/UniversalBucket";
    public static final ObfMapping TARGET_METHOD = new ObfMapping(TARGET_CLASS_NAME, "func_150895_a",
            targetSignature());

    private static final String OWNER = "com/m_w_k/gtcefucontent/asmm/hooks/UniversalBucketHooks";
    private static final String SIGNATURE = signature();
    private static final String METHOD_NAME = "generateEutecticBuckets";

    public UniversalBucketVisitor(MethodVisitor mv) {
        super(ASM5, mv);
    }

    @Override
    public void visitCode() {
        mv.visitVarInsn(ALOAD, 0); // Item
        mv.visitVarInsn(ALOAD, 1); // Creative Tabs
        mv.visitVarInsn(ALOAD, 2); // NonNullList
        mv.visitMethodInsn(INVOKESTATIC, OWNER, METHOD_NAME, SIGNATURE, false);
        mv.visitLabel(new Label());
        mv.visitCode();
    }

    // public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems)
    private static String targetSignature() {
        return "(" +
                "Lnet/minecraft/creativetab/CreativeTabs;" + // CreativeTabs
                "Lnet/minecraft/util/NonNullList;" + // NonNullList
                ")V"; // return void
    }

    // public static void generateEutecticBuckets(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
    private static String signature() {
        return "(" +
                "Lnet/minecraft/item/Item;" + // Item
                "Lnet/minecraft/creativetab/CreativeTabs;" + // CreativeTabs
                "Lnet/minecraft/util/NonNullList;" + // NonNullList
                ")V"; // return void
    }
}
