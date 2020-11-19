package com.example.kmemobileapp;

public class Vector3D {

    public float x;
    public float y;
    public float z;

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3D(float X, float Y, float Z) {
        this.x = X;
        this.y = Y;
        this.z = Z;
    }

    public static void add (Vector3D vector, float scalar) {
        vector.x += scalar;
        vector.y += scalar;
        vector.z += scalar;
    }

    public static void subtract (Vector3D vector, float scalar) {
        vector.x -= scalar;
        vector.y -= scalar;
        vector.z -= scalar;
    }

    public static void multiply (Vector3D vector, float scalar) {
        vector.x *= scalar;
        vector.y *= scalar;
        vector.z *= scalar;
    }

    public static void normalize(Vector3D vector) {
        float length = (float) Math.sqrt ( (vector.x * vector.x) + (vector.y * vector.y) + (vector.z * vector.z) );
        vector.x = vector.x / length;
        vector.y = vector.y / length;
        vector.z = vector.z / length;
    }

}
