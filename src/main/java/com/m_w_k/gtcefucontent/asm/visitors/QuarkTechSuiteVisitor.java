package com.m_w_k.gtcefucontent.asm.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import gregtech.asm.util.ObfMapping;

public class QuarkTechSuiteVisitor extends MethodVisitor implements Opcodes {

    public static final String TARGET_CLASS_NAME = "gregtech/common/items/armor/QuarkTechSuite";
    public static final ObfMapping TARGET_METHOD = new ObfMapping(TARGET_CLASS_NAME, "handleUnblockableDamage",
            targetSignature());

    public QuarkTechSuiteVisitor(MethodVisitor mv) {
        super(ASM5, mv);
    }

    @Override
    public void visitCode() {
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitLineNumber(13, label0);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKESTATIC, "com/m_w_k/gtcefucontent/api/damagesources/GTCEFuCDamageSources",
                "isQTUnblockable", "(Lnet/minecraft/util/DamageSource;)Z", false);
        Label label1 = new Label();
        mv.visitJumpInsn(IFEQ, label1);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(label1);
        mv.visitLineNumber(14, label1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitCode();
    }

    // public boolean handleUnblockableDamage(EntityLivingBase entity, @NotNull ItemStack armor, DamageSource source,
    // double damage, EntityEquipmentSlot equipmentSlot)
    private static String targetSignature() {
        return "(" +
                "Lnet/minecraft/entity/EntityLivingBase;" +
                "Lnet/minecraft/item/ItemStack;" +
                "Lnet/minecraft/util/DamageSource;" +
                "DLnet/minecraft/inventory/EntityEquipmentSlot;" +
                ")Z";
    }
}
