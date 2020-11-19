package com.example.kmemobileapp;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PrimitiveLine extends EntityGame {
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
//    static float triangleCoords[] = {
//            // in counterclockwise order:
//            -0.5f,  0.8f, 0.0f,
//            0.5f, 0.8f, 0.0f,
//
//    };
    private final int vertexCount = 2;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    float color[] = { 1.f, 0.0f, 0.0f, 0.0f };
    private float mLocationStart;
    private float mLocationEnd;
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public PrimitiveLine(final Vector3D locationStart, final Vector3D locationEnd) {
        mProgram = GLES30.glCreateProgram();
        float lineCoords[] = {
                // in counterclockwise order:
                locationStart.x, locationStart.y, locationStart.z,
                locationEnd.x, locationEnd.y, locationEnd.z,
        };

        //Matrix.setIdentityM(mModelMatrix,0);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                lineCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(lineCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        // prepare shaders and OpenGL program
        int vertexShader = MyRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void setTaceLocation(Vector3D start, Vector3D end) {

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);
//        MyRenderer.checkGlError2("glUseProgram");
        int[] linked = new int[1];
        //MyRenderer.checkLinking(mProgram, GLES30.GL_LINK_STATUS, linked);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, vertexStride, vertexBuffer);
        // get handle to fragment shader's vColor member
        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);
        // get handle to shape's transformation matrix

   //     MyRenderer.checkGlError("glGetUniformLocation");
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
       // MyRenderer.checkGlError("glGetUniformLocation");
        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
       // MyRenderer.checkGlError("glUniformMatrix4fv");
        // Draw the triangle
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, vertexCount);
        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}
