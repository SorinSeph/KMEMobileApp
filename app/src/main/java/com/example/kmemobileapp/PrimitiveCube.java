package com.example.kmemobileapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.renderscript.Matrix4f;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class PrimitiveCube extends Entity3D {

    private final Context mActivityContext;

    private final String vertexShaderCode =

            "uniform mat4 u_MVPMatrix;  \n" +
                    "uniform mat4 u_MVMatrix;   \n" +
                    "uniform mat4 u_PMatrix;   \n" +
                    "attribute vec4 a_Position;\n"    +
                    //"attribute vec4 a_Color; \n" +
                    //"attribute vec3 a_Normal;\n" +
                    "attribute vec2 a_TexCoordinate;\n" +
                    //"varying vec3 v_Position;\n" +
                    //"varying vec4 v_Color;\n" +
                    //"varying vec3 v_Normal;\n" +
                    "varying vec2 v_TexCoordinate;\n" +
                    "void main()\n" +
                    "{\n" +
                    //"v_Position = vec3(u_MVMatrix * a_Position);\n" +
                    //"v_Color = a_Color; \n" +
                    "v_TexCoordinate = a_TexCoordinate; \n" +
                    //"v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0)); \n" +
                    "gl_Position = u_PMatrix * u_MVMatrix * a_Position;\n" +
                    "}";

    // Modified KmE, not working
    /*
                    "#version 300 es \n" +
                    "uniform mat4 u_MVPMatrix;  \n" +
                    "uniform mat4 u_MVMatrix;   \n" +
                    "in vec4 a_Position; \n" +
                    //"attribute vec4 a_Color; \n" +
                    //"attribute vec3 a_Normal;\n" +
                    "in vec2 a_TexCoordinate;\n" +
                    //"varying vec3 v_Position;\n" +
                    //"varying vec4 v_Color;\n" +
                    //"varying vec3 v_Normal;\n" +
                    "out vec2 v_TexCoordinate;\n" +
                    "void main()\n" +
                    "{\n" +
                    //"v_Position = vec3(u_MVMatrix * a_Position);\n" +
                    //"v_Color = a_Color; \n" +
                    "v_TexCoordinate = a_TexCoordinate; \n" +
                    //"v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0)); \n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "}\n";
                    */


            /*
            //Original
            "uniform mat4 u_MVPMatrix;  \n" +
                    "uniform mat4 u_MVMatrix;   \n" +
                    "attribute vec4 a_Position;\n"    +
                    "attribute vec2 a_TexCoordinate;\n" +
                    "varying vec2 v_TexCoordinate;\n" +
                    "void main()\n" +
                    "{\n" +
                    "v_TexCoordinate = a_TexCoordinate; \n" +
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "}";
                    */

    private final String fragmentShaderCode =

            "precision mediump float;  \n" +
                    //"uniform vec3 u_LightPos;\n" +
                    "uniform sampler2D u_Texture;\n" +
                    //"varying vec3 v_Position;\n" +
                    //"varying vec4 v_Color;\n" +
                    //"varying vec3 v_Normal;\n" +
                    "varying vec2 v_TexCoordinate;\n" +
                    "void main()\n" +
                    "{\n" +
                    //"float distance = length(u_LightPos - v_Position);\n" +
                    //"vec3 lightVector = normalize(u_LightPos - v_Position);\n" +
                    //"float diffuse = max(dot(v_Normal, lightVector), 0.0);\n" +
                    //"diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));\n" +
                    //"diffuse = diffuse + 0.3;\n" +

                    //with color
                    //"gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate)); \n"+

                    //without color
                    "gl_FragColor = (texture2D(u_Texture, v_TexCoordinate)); \n"+
                    "}";

    // version 300 modified, not working
            /*
                    "#version 300 es \n" +
                    "precision mediump float; \n" +
                    //"uniform vec3 u_LightPos;\n" +
                    "uniform sampler2D u_Texture;\n" +
                    //"varying vec3 v_Position;\n" +
                    //"varying vec4 v_Color;\n" +
                    //"varying vec3 v_Normal;\n" +
                    "out vec4 FragColor; \n" +
                    "out vec2 v_TexCoordinate; \n" +
                    "void main() \n" +
                    "{ \n" +
                    //"float distance = length(u_LightPos - v_Position);\n" +
                    //"vec3 lightVector = normalize(u_LightPos - v_Position);\n" +
                    //"float diffuse = max(dot(v_Normal, lightVector), 0.0);\n" +
                    //"diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));\n" +
                    //"diffuse = diffuse + 0.3;\n" +

                    //with color
                    //"gl_FragColor = (v_Color * texture2D(u_Texture, v_TexCoordinate)); \n"+

                    //without color
                    "FragColor = (texture(u_Texture, v_TexCoordinate)); \n"+
                    "}";
                    */


    /*
    // Original
    "precision mediump float;  \n" +
            "uniform sampler2D u_Texture;\n" +
            "varying vec2 v_TexCoordinate;\n" +
            "void main()\n" +
            "{\n" +
            "gl_FragColor = (texture2D(u_Texture, v_TexCoordinate)); \n"+
            "}";
            */

    static final int BYTES_PER_FLOAT = 4;

    private int mVBOId[] = new int[3];
    private int mVAOId[] = new int[1];

    private final float mLength;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureCoordsBuffer;
    private final IntBuffer indicesBuffer;
    // private final FloatBuffer mCubeTextureCoordinates;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mPMatrixHandle;
    private int mTextureHandle;
    private int mTextureUniformHandle;
    private Vector3D mOriginalLocation;

    final int[] textureHandle = new int[1];

    public float[] mViewMatrix = new float[16];
    public float[] mProjectionMatrix = new float[16];


    // private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    // This original code draws 2 triangles with a shared edge to form a square.
    // The top left vertex is omitted so as not to be drawn twice, using a draw list order

    static float cubeCoords[] = {

            // Front face
            -1.0f, 1.0f, 1.0f,  // top left
            -1.0f, -1.0f, 1.0f, // bottom left
            1.0f, 1.0f, 1.0f,   // top right
            -1.0f, -1.0f, 1.0f, // bottom left
            1.0f, -1.0f, 1.0f,  // bottom right
            1.0f, 1.0f, 1.0f,   // top right

            // Right face
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,

            // Back face
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            // Left face
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,

            // Top face
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,

            // Bottom face
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
    };

    static float squareCoords2[] = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,    // bottom right
            0.5f,  0.5f, 0.0f };  // top right

    // Draws a square on the UV unwrap plane.

    static float squareTextureCoords[] = {

            // Front face
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            /*
            // Front face, backup
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
             */

            // Right face
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            // Back face
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            // Left face
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            // Top face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            // Bottom face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    final float[] squareColor =
            {
                    // Front face (red)
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f
            };

    final int[] indices = {
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7,};

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public PrimitiveCube(final Context context, float[] MVPMatrix, float length, Vector3D location) {
        mActivityContext = context;
        mLength = length;
        mOriginalLocation = location;
        //Matrix.setIdentityM(super.mModelMatrix, 0);
        // Matrix.setIdentityM(mModelMatrix, 0);

        if (mOriginalLocation.x != 0) {
            Matrix.translateM(mModelMatrix, 0, mOriginalLocation.x, 0, 0);
        }
        if (mOriginalLocation.y != 0) {
            Matrix.translateM(mModelMatrix, 0, 0, mOriginalLocation.y, 0);
        }
        if (mOriginalLocation.z != 0){
            Matrix.translateM(mModelMatrix, 0,0, 0,  mOriginalLocation.z);
        }

        // Generate the VAO ID

        GLES30.glGenVertexArrays(1, mVAOId,0);
        GLES30.glBindVertexArray(mVAOId[0]);

        // Generate the VBOs, bind them then load them with data

        GLES30.glGenBuffers(3, mVBOId,0);

        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(cubeCoords).position(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOId[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT, vertexBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glEnableVertexAttribArray ( 0 ); // Index 0 is the position of the VBO's first bound array, the vertices coordinates
        GLES30.glVertexAttribPointer(
                0,                           // index 0 for coordinates VBO
                3,                           // size 3, for x, y and z
                GLES30.GL_FLOAT,
                false,
                0,
                0 );

        ByteBuffer dlb = ByteBuffer.allocateDirect(
                 // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        //colorBuffer = ByteBuffer.allocateDirect(squareColor.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        //colorBuffer.put(squareColor).position(0);

        textureCoordsBuffer = ByteBuffer.allocateDirect(squareTextureCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordsBuffer.put(squareTextureCoords).position(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOId[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, textureCoordsBuffer.capacity() * BYTES_PER_FLOAT, textureCoordsBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glEnableVertexAttribArray ( 1 ); // Index 1 is the position of the VBO's second bound array, the texture coordinates
        GLES30.glVertexAttribPointer (
                1,                                 // index 1 for texture VBO
                2,
                GLES30.GL_FLOAT,
                false,
                0,
                0);

        indicesBuffer = ByteBuffer.allocateDirect(squareTextureCoords.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        indicesBuffer.put(indices).position(0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBOId[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_FLOAT, indicesBuffer, GLES30.GL_STATIC_DRAW);

        //GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        //GLES30.glBindBuffer (GLES30.GL_ARRAY_BUFFER, mVBOId[0]);
       // GLES30.glBindBuffer (GLES30.GL_ELEMENT_ARRAY_BUFFER, mVBOId[1]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Reset to the default VAO
        GLES30.glBindVertexArray (0);

        // prepare shaders and OpenGL program
        int vertexShader = MyRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        Log.e("Program Info Log:", GLES30.glGetProgramInfoLog(mProgram));
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program

        int[] shaderParams = new int[1];
        GLES30.glGetShaderiv(vertexShader, GLES30.GL_COMPILE_STATUS, shaderParams, 0);

        GLES30.glBindAttribLocation(mProgram, 0, "a_Position");
        GLES30.glBindAttribLocation(mProgram, 1, "a_TexCoordinate");

        GLES30.glLinkProgram(mProgram);

        GLES30.glValidateProgram(mProgram);

        // MyRenderer.checkLinking(mProgram, programLink);

        loadTexture(mActivityContext, R.drawable.grassblock);
    }

    public void loadTexture(final Context context, final int resourceId) {
        //final int[] textureHandle = new int[1];

        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error generating texture name.");
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;    // No pre-scaling

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        // Bind to the texture in OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();

        //mTextureHandle = textureHandle[0];

        Log.d("CubeTextureTag", "textureHandle is: " + textureHandle[0]);
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     //* @param mvpMatrix - The Model View Project matrix in which to draw
     //* this shape.
     */
    public void draw(float[] mvMatrix, float[] pMatrix) {
        // Add program to OpenGL environment
        //mMVPMatrix = mvpMatrix;

        int[] programLink = new int[1];

        GLES30.glGetProgramiv(mProgram, GLES30.GL_LINK_STATUS, programLink, 0);
        MyRenderer.checkLinking(mProgram, programLink);
        Log.d("ProgramTag", "Program is: " + mProgram);

        GLES30.glUseProgram(mProgram);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "a_Position");
        mTextureUniformHandle = GLES30.glGetUniformLocation(mProgram, "u_Texture");
        mTextureHandle = GLES30.glGetAttribLocation(mProgram, "a_TexCoordinate");

        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

        GLES30.glUniform1i(mTextureUniformHandle, 0);

        textureCoordsBuffer.position(0);
        GLES30.glVertexAttribPointer(mTextureHandle, 2, GLES30.GL_FLOAT, false, 0, textureCoordsBuffer);

        GLES30.glEnableVertexAttribArray(mTextureHandle);

        // get handle to fragment shader's vColor member
        //mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES30.glUniform4fv(mColorHandle, 1, color, 0);

        //get handle to shape's transformation matrix
        //mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_MVMatrix");
        //MyRenderer.checkGlError("glGetUniformLocation");

        GLES30.glBindVertexArray(mVAOId[0]);

        mMVMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_MVMatrix");
        //MyRenderer.checkGlError("glGetUniformLocation");
        GLES30.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

        mPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_PMatrix");
        //MyRenderer.checkGlError("glGetUniformLocation");
        GLES30.glUniformMatrix4fv(mPMatrixHandle, 1, false, pMatrix, 0);

        // Apply the projection and view transformation
        //GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //MyRenderer.checkGlError("glUniformMatrix4fv");

        //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, super.mModelMatrix, 0);

        //GLES30.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvpMatrix, 0);

        //Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mvpMatrix, 0);

        //GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the square
        //GLES30.glDrawElements(
        //        GLES30.GL_TRIANGLE_STRIP, drawOrder.length,
        //        GLES30.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_SHORT, 0 );

        GLES30.glBindVertexArray (0);
        GLES30.glDeleteVertexArrays(1, mVAOId, 0);

        // Disable vertex array
        //GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    public void translate(Vector3D location) {
        Matrix.translateM(super.mModelMatrix, 0, location.x, location.y, location.z);
    }

    public void rotate(float angleX, float angleY, float angleZ) {

    }

    public float[] getModelMatrix() {
        return super.mModelMatrix;
    }

    public void setModelMatrix(float[] matrix) {
        mModelMatrix = matrix;
    }

    public void setViewMatrix(float[] inViewMatrix) {
        mViewMatrix = inViewMatrix;
    }

    public void setProjectionMatrix(float[] inMatrix) {
        mProjectionMatrix = inMatrix;
    }

    public Vector3D getLocation() {
        return mOriginalLocation;
    }
}