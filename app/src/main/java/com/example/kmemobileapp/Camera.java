package com.example.kmemobileapp;

import android.opengl.Matrix;

import com.example.kmemobileapp.Vector3D;

public class Camera {

    private Vector3D location;
    private Vector3D rotation;

    public Camera() {
        location = new Vector3D(0,0,0);
        rotation = new Vector3D(0,0,0);
    }

    public void moveLocation(Vector3D offset) {
        if (offset.z != 0) {
            location.x += Math.sin(Math.toRadians(rotation.y)) * -1.0f * offset.z;
            location.z += Math.cos(Math.toRadians(rotation.y)) * offset.z;
        }
        if (offset.x != 0) {
            location.x += Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offset.x;
            location.z += Math.cos(Math.toRadians(rotation.y - 90)) * offset.x;
        }

        location.y += offset.y;
    }
    public void moveRotation(Vector3D offset) {
        rotation.x += offset.x;
        rotation.y += offset.y;
        rotation.z += offset.z;
    }

    public Vector3D getPosition() {
        return location;
    }

    public Vector3D getRotation() {
        return rotation;
    }

    public float[] getViewMatrix() {
        Vector3D cameraPos = getPosition();
        Vector3D cameraRotation = getRotation();

        float[] cameraViewMatrix = new float[16];
        Matrix.setIdentityM(cameraViewMatrix, 0);
        // First do the rotation so camera rotates over its position
        Matrix.setRotateM(cameraViewMatrix, 0, cameraRotation.x,1.f,0.f,0.f);
        Matrix.setRotateM(cameraViewMatrix, 0, cameraRotation.y,0.f,1.f,1.f);

        //viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return cameraViewMatrix;
    }

}
