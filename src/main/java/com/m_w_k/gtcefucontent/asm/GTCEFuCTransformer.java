package com.m_w_k.gtcefucontent.asm;

import com.m_w_k.gtcefucontent.asm.visitors.QuarkTechSuiteVisitor;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.m_w_k.gtcefucontent.asm.visitors.FluidTooltipUtilVisitor;
import com.m_w_k.gtcefucontent.asm.visitors.UniversalBucketVisitor;

import gregtech.asm.util.TargetClassVisitor;

public class GTCEFuCTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        String internalName = transformedName.replace('.', '/');
        switch (internalName) {
            case FluidTooltipUtilVisitor.TARGET_CLASS_NAME -> {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(0);
                classReader.accept(new TargetClassVisitor(classWriter, FluidTooltipUtilVisitor.TARGET_METHOD,
                        FluidTooltipUtilVisitor::new), 0);
                return classWriter.toByteArray();
            }
            case UniversalBucketVisitor.TARGET_CLASS_NAME -> {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(0);
                classReader.accept(new TargetClassVisitor(classWriter, UniversalBucketVisitor.TARGET_METHOD,
                        UniversalBucketVisitor::new), 0);
                return classWriter.toByteArray();
            }
            case QuarkTechSuiteVisitor.TARGET_CLASS_NAME -> {
                ClassReader classReader = new ClassReader(basicClass);
                ClassWriter classWriter = new ClassWriter(0);
                classReader.accept(new TargetClassVisitor(classWriter, QuarkTechSuiteVisitor.TARGET_METHOD,
                        QuarkTechSuiteVisitor::new), 0);
                return classWriter.toByteArray();
            }
        }
        return basicClass;
    }
}
