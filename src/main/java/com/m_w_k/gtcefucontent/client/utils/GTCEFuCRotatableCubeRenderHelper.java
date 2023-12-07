package com.m_w_k.gtcefucontent.client.utils;

import com.m_w_k.gtcefucontent.api.util.MultiblockRenderRotHelper;
import gregtech.client.utils.RenderBufferHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

public class GTCEFuCRotatableCubeRenderHelper {

    protected final Vector3d empty = new Vector3d();

    protected final Vector3d xVecMirror = new Vector3d();
    protected final Vector3d yVecMirror = new Vector3d();
    protected final Vector3d zVecMirror = new Vector3d();

    protected final Vector3d xVecD = new Vector3d();
    protected final Vector3d yVecD = new Vector3d();
    protected final Vector3d zVecD = new Vector3d();

    protected final Vector3d offsetVec = new Vector3d();

    protected final Vector3d tempVec1 = new Vector3d();
    protected final Vector3d tempVec2 = new Vector3d();
    protected final Matrix3d tempMatrix = new Matrix3d();

    public GTCEFuCRotatableCubeRenderHelper() {}

    public GTCEFuCRotatableCubeRenderHelper setSize(double x, double y, double z) {
        clearSize();
        this.xVecD.setX(x);
        this.yVecD.setY(y);
        this.zVecD.setZ(z);
        return resetRotation();
    }

    public GTCEFuCRotatableCubeRenderHelper setSize(Vector3d size) {
        return setSize(size.getX(), size.getY(), size.getZ());
    }

    public GTCEFuCRotatableCubeRenderHelper clearSize() {
        this.xVecD.set(empty);
        this.yVecD.set(empty);
        this.zVecD.set(empty);
        return resetRotation();
    }

    public GTCEFuCRotatableCubeRenderHelper resetRotation() {
        this.xVecMirror.set(xVecD);
        this.yVecMirror.set(yVecD);
        this.zVecMirror.set(zVecD);
        return this;
    }

    public GTCEFuCRotatableCubeRenderHelper setDisplacement(double x, double y, double z) {
        this.offsetVec.set(x, y, z);
        return this;
    }

    public GTCEFuCRotatableCubeRenderHelper setDisplacement(Vector3d displacement) {
        return setDisplacement(displacement.getX(), displacement.getY(), displacement.getZ());
    }

    public GTCEFuCRotatableCubeRenderHelper clearDisplacement() {
        this.offsetVec.set(empty);
        return this;
    }

    public GTCEFuCRotatableCubeRenderHelper rotate(Matrix3d rotationMatrix) {
        rotationMatrix.transform(this.xVecMirror);
        rotationMatrix.transform(this.yVecMirror);
        rotationMatrix.transform(this.zVecMirror);
        return this;
    }

    public GTCEFuCRotatableCubeRenderHelper rotateY(double radians) {
        tempMatrix.rotY(radians);
        return rotate(tempMatrix);
    }

    public GTCEFuCRotatableCubeRenderHelper rotateX(double radians) {
        tempMatrix.rotX(radians);
        return rotate(tempMatrix);
    }

    public GTCEFuCRotatableCubeRenderHelper rotateZ(double radians) {
        tempMatrix.rotZ(radians);
        return rotate(tempMatrix);
    }

    public void renderCubeFrame(BufferBuilder buffer, float r, float g, float b, float a, int w) {
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        // thicken/narrow the line based on distance
        GL11.glLineWidth((float) (w / offsetVec.length()));
        // UFR
        goToCorner(buffer, false, false, false).color(r, g, b, a).endVertex();
        // DFR
        goToCorner(buffer, false, true, false).color(r, g, b, a).endVertex();
        // DBR
        goToCorner(buffer, false, true, true).color(r, g, b, a).endVertex();
        // DFR
        goToCorner(buffer, false, true, false).color(r, g, b, a).endVertex();
        // DFL
        goToCorner(buffer, true, true, false).color(r, g, b, a).endVertex();
        // DBL
        goToCorner(buffer, true, true, true).color(r, g, b, a).endVertex();
        // DFL
        goToCorner(buffer, true, true, false).color(r, g, b, a).endVertex();
        // UFL
        goToCorner(buffer, true, false, false).color(r, g, b, a).endVertex();
        // UBL
        goToCorner(buffer, true, false, true).color(r, g, b, a).endVertex();
        // UFL
        goToCorner(buffer, true, false, false).color(r, g, b, a).endVertex();
        // UFR
        goToCorner(buffer, false, false, false).color(r, g, b, a).endVertex();
        // UBR
        goToCorner(buffer, false, false, true).color(r, g, b, a).endVertex();
        // DBR
        goToCorner(buffer, false, true, true).color(r, g, b, a).endVertex();
        // DBL
        goToCorner(buffer, true, true, true).color(r, g, b, a).endVertex();
        // UBL
        goToCorner(buffer, true, false, true).color(r, g, b, a).endVertex();
        // UBR
        goToCorner(buffer, false, false, true).color(r, g, b, a).endVertex();

        Tessellator.getInstance().draw();
        GL11.glLineWidth(1);
    }

    private BufferBuilder goToCorner(BufferBuilder buffer, boolean xMirrored, boolean yMirrored, boolean zMirrored) {
        if (xMirrored) tempVec1.negate(xVecMirror);
        else tempVec1.set(xVecMirror);

        if (yMirrored) tempVec2.negate(yVecMirror);
        else tempVec2.set(yVecMirror);
        tempVec1.add(tempVec2);

        if (zMirrored) tempVec2.negate(zVecMirror);
        else tempVec2.set(zVecMirror);
        tempVec1.add(tempVec2);

        tempVec1.add(offsetVec);

        return buffer.pos(tempVec1.getX(), tempVec1.getY(), tempVec1.getZ());

    }
}
