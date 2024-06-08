package com.m_w_k.gtcefucontent.asm;

import com.m_w_k.gtcefucontent.asm.visitors.FluidTooltipUtilVisitor;
import gregtech.asm.util.TargetClassVisitor;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class GTCEFuCTransformer implements IClassTransformer, Opcodes {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        String internalName = transformedName.replace('.', '/');
        if (internalName.equals(FluidTooltipUtilVisitor.TARGET_CLASS_NAME)) {
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new TargetClassVisitor(classWriter, FluidTooltipUtilVisitor.TARGET_METHOD, FluidTooltipUtilVisitor::new), 0);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
