package com.m_w_k.gtcefucontent.api.util;

import java.util.*;
import java.util.stream.Collectors;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

import org.apache.commons.lang3.tuple.MutableTriple;

import gregtech.api.util.RelativeDirection;

/**
 * Contains generalized code to help with rendering rotatable multiblocks
 * <br>
 * <br>
 * Allows you to register vectors to then be rotated based on the multiblock's rotation.
 */
public final class MultiblockRenderRotHelper {

    private final List<Vector3d> registeredVecs;

    private boolean validVecCache = false;
    private final MutableTriple<EnumFacing, EnumFacing, Boolean> matrixCache = new MutableTriple<>();

    private final Matrix3d rotMatrixY = new Matrix3d();
    private final Matrix3d rotMatrixZ = new Matrix3d();
    private final Matrix3d rotMatrixX = new Matrix3d();
    private final Matrix3d rotMatrixVertCorrect = new Matrix3d(0, 0, -1, 0, 1, 0, 1, 0, 0);

    private final HelperUser parent;

    /**
     * Vector registry must occur on helper creation.
     * 
     * @param parent         The instantiator of this helper.
     * @param vecsToRegister The vectors to be rotated by this helper. If these are not <code>final</code>, errors may
     *                       occur.
     */
    public MultiblockRenderRotHelper(HelperUser parent, Vector3d... vecsToRegister) {
        this.parent = parent;
        registeredVecs = Arrays.stream(vecsToRegister).collect(Collectors.toList());
    }

    public void calculateRots(EnumFacing frontFacing, EnumFacing upwardsFacing, boolean isFlipped) {
        // check cache
        if (matrixCache.getLeft() == frontFacing && matrixCache.getMiddle() == upwardsFacing &&
                matrixCache.getRight() == isFlipped)
            return;

        // convert provided EnumFacings to relative EnumFacings
        Vec3i vecUp = RelativeDirection.UP.getRelativeFacing(frontFacing, upwardsFacing, isFlipped).getDirectionVec();
        frontFacing = RelativeDirection.FRONT.getRelativeFacing(frontFacing, upwardsFacing, isFlipped);
        Vec3i vecForward = frontFacing.getDirectionVec();

        Vector3d vectorUp = new Vector3d(vecUp.getX(), vecUp.getY(), vecUp.getZ());
        Vector3d vectorForward = new Vector3d(vecForward.getX(), vecForward.getY(), vecForward.getZ());
        Vector3d horizontal = new Vector3d(0, 0, 1);
        Vector3d vertical = new Vector3d(0, 1, 0);
        Vector3d temp = new Vector3d();
        boolean verticalForward = frontFacing.getYOffset() != 0;

        // first step - rotate around y so that we're aligned with forward in the xy plane
        double rotY = verticalForward ? 0 : frontFacing.getHorizontalAngle();
        if (rotY == 270) rotY = -90;
        rotMatrixY.rotY(-Math.toRadians(rotY));
        // rotate our vector
        rotMatrixY.transform(horizontal);

        // second step - rotate around the NEW z-axis to get aligned with vectorForward
        temp.cross(horizontal, vectorForward); // cross product to determine sign of rotation
        double sum = Math.round(temp.z + temp.x + temp.y);
        double rotZ = vectorForward.angle(horizontal) * (sum != 0 ? sum : 1);
        rotMatrixZ.rotX(rotZ);
        // rotate our vector
        rotMatrixZ.transform(vertical);

        // third step - rotate around the NEW x-axis in order to get aligned with vectorUp
        temp.cross(vectorUp, vertical); // cross product to determine sign of rotation
        sum = Math.round(temp.z + temp.x + temp.y);
        double rotX = vectorUp.angle(vertical) * (sum != 0 ? sum : 1);
        rotMatrixX.rotZ(rotX);

        // update cache & reset vecs
        matrixCache.setLeft(frontFacing);
        matrixCache.setMiddle(upwardsFacing);
        matrixCache.setRight(isFlipped);
        parent.resetVecs();
        validVecCache = false;
    }

    public void rotVecs() {
        // if our vec cache is valid, then we don't need to rotate the vecs
        if (!validVecCache) {
            for (Vector3d vec : registeredVecs) {
                rotMatrixVertCorrect.transform(vec);
                // go backwards because no associative property
                rotMatrixX.transform(vec);
                rotMatrixZ.transform(vec);
                rotMatrixY.transform(vec);
                // fix floating-point error
                vec.set(Math.round(vec.x), Math.round(vec.y), Math.round(vec.z));
            }
            validVecCache = true;
        }
    }

    @FunctionalInterface
    public interface HelperUser {

        void resetVecs();
    }
}
