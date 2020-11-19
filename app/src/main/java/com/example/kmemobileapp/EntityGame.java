package com.example.kmemobileapp;

import android.opengl.Matrix;
import android.renderscript.Matrix4f;

public class EntityGame {
    public float[] mModelMatrix;
    public Vector3D mLocation;

    EntityGame() {
        mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        mLocation = new Vector3D(0,0,0);
    }

    public void draw(float[] mvpMatrix) {}

    public void draw(float[] mvMatrix, float[] pMatrix) {}

    public void setViewMatrix(float[] inViewMatrix) {}

    public void setProjectionMatrix(float[] inMatrix) {}

    public void setModelMatrix(float[] inModelMatrix) { mModelMatrix = inModelMatrix; }

    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public Vector3D getLocation() { return mLocation; }

    public void setLocation( Vector3D NewLocation) {
        if (NewLocation.x != 0) {
            Matrix.translateM(mModelMatrix, 0, NewLocation.x, 0, 0);
        }
        if (NewLocation.y != 0) {
            Matrix.translateM(mModelMatrix, 0, 0, NewLocation.y, 0);
        }
        if (NewLocation.z != 0){
            Matrix.translateM(mModelMatrix, 0,0, 0,  NewLocation.z);
        }
    }

    //public void translateEntity();

    public float[] returnCoords() { return new float[0]; }

    public float[] returnWorldCoords() { return new float[0]; }
}
