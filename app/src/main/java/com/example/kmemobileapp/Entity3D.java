package com.example.kmemobileapp;

public class Entity3D extends EntityGame {

    public float mLength;
    public float mWidth;

    Entity3D() {}

    public void draw(float[] mvpMatrix){}

    public void setViewMatrix(float[] inViewMatrix) {
    }

    public void setProjectionMatrix(float[] inMatrix) {
    }

    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public float getLength() { return mLength; }
    public float getWidth() { return mWidth; }
}
