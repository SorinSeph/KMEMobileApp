package com.example.kmemobileapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.renderscript.Matrix4f;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class PrimitiveSquare extends Entity2D {

    private final Context mActivityContext;

    private final String vertexShaderCode =

            // LearnOpenGLES
            "uniform mat4 u_MVPMatrix;  \n" +
                    "uniform mat4 u_MVMatrix;   \n" +
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
                    "gl_Position = u_MVPMatrix * a_Position;\n" +
                    "}";

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

    final int[] textureHandle = new int[1];
    private Vector3D mLocation2;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureCoordsBuffer;
    // private final FloatBuffer mCubeTextureCoordinates;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mTextureHandle;
    private int mTextureUniformHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    float[] squareCoords = new float[12];

    //Add to compendium
    /*
    float squareCoords2[] = {


            - mLength/2, mLength/2, 0,
            - mLength/2, - mLength/2, 0,
            mLength/2, - mLength/2, 0,
            mLength/2, mLength/2, 0 };



            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,    // bottom right
            0.5f,  0.5f, 0.0f };  // top right
            */

    static float squareTextureCoords[] = {

            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f    };

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public PrimitiveSquare(final Context context, Vector3D location, float length) {
        mActivityContext = context;
        mLocation = location;
        mLocation2 = location;
        mLength = length;
        //mWidth = length;

        textureHandle[0] = 2;

        setCoordinates(mLength);

        if (mLocation.x != 0) {
            Matrix.translateM(mModelMatrix, 0, mLocation.x, 0, 0);
        }

        if (mLocation.y != 0) {
            Matrix.translateM(mModelMatrix, 0, 0, mLocation.y, 0);
        }

        if (mLocation.z != 0){
            Matrix.translateM(mModelMatrix, 0,0, 0,  mLocation.z);
        }

        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(squareCoords).position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        textureCoordsBuffer = ByteBuffer.allocateDirect(squareTextureCoords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordsBuffer.put(squareTextureCoords).position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program

        GLES30.glBindAttribLocation(mProgram, 0, "a_Position");
        GLES30.glBindAttribLocation(mProgram, 1, "a_TexCoordinate");

        GLES30.glLinkProgram((mProgram));

        //mTextureHandle = MyRenderer.loadTexture(mActivityContext, R.drawable.grassblock);
        loadTexture(mActivityContext, R.drawable.arrow_up_unpressed);
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

        Log.d("SquareTextureTag", "textureHandle is: " + textureHandle[0]);
    }

    public void updateTexture(final Context context, final int resourceId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);  //Read in the resource
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
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

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "a_Position");

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        mTextureUniformHandle = GLES30.glGetUniformLocation(mProgram, "u_Texture");

        mTextureHandle = GLES30.glGetAttribLocation(mProgram, "a_TexCoordinate");

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

        GLES30.glUniform1i(mTextureUniformHandle, 0);

        textureCoordsBuffer.position(0);
        GLES30.glVertexAttribPointer(mTextureHandle, 2, GLES30.GL_FLOAT, false, 0, textureCoordsBuffer);

        GLES30.glEnableVertexAttribArray(mTextureHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_MVPMatrix");
       // MyRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //MyRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES30.glDrawElements(
                GLES30.GL_TRIANGLE_STRIP, drawOrder.length,
                GLES30.GL_UNSIGNED_SHORT, drawListBuffer);

        //GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    public void draw2(Matrix4f mvpMatrix) {
        // Add program to OpenGL environment
        GLES30.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "a_Position");

        // Enable a handle to the triangle vertices
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        mTextureUniformHandle = GLES30.glGetUniformLocation(mProgram, "u_Texture");

        mTextureHandle = GLES30.glGetAttribLocation(mProgram, "a_TexCoordinate");

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureHandle);

        GLES30.glUniform1i(mTextureUniformHandle, 0);

        textureCoordsBuffer.position(0);
        GLES30.glVertexAttribPointer(mTextureHandle, 2, GLES30.GL_FLOAT, false, 0, textureCoordsBuffer);

        GLES30.glEnableVertexAttribArray(mTextureHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_MVPMatrix");
        // MyRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix.getArray(), 0);
        //MyRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES30.glDrawElements(
                GLES30.GL_TRIANGLE_STRIP, drawOrder.length,
                GLES30.GL_UNSIGNED_SHORT, drawListBuffer);

        //GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);

        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    public int getCoordsPerVertex() {
        return COORDS_PER_VERTEX;
    }

    public void setCoordinates(float length) {
        squareCoords[0] = - mLength/2;     squareCoords[1]  = mLength / 2;     squareCoords[2]  = 0;     // top left
        squareCoords[3] = - mLength/2;     squareCoords[4]  = -mLength/2;      squareCoords[5]  = 0;     // bottom left
        squareCoords[6] = mLength/2;       squareCoords[7]  = -mLength/2;      squareCoords[8]  = 0;     // bottom right
        squareCoords[9] = mLength/2;       squareCoords[10] = mLength/2;       squareCoords[11] = 0;     // top right
    }

    public float[] returnCoords() {
        return squareCoords;
    }

    @Override
    public float[] returnWorldCoords() {

        float[] worldCoords = new float[12];

        // Coordinates on X axis
        worldCoords[0] = squareCoords[0] + mLocation.x;
        worldCoords[3] = squareCoords[3] + mLocation.x;
        worldCoords[6] = squareCoords[6] + mLocation.x;
        worldCoords[9] = squareCoords[9] + mLocation.x;

        // Coordinates on Y axis
        worldCoords[1] = squareCoords[1] + mLocation.y;
        worldCoords[4] = squareCoords[4] + mLocation.y;
        worldCoords[7] = squareCoords[7] + mLocation.y;
        worldCoords[10] = squareCoords[10] + mLocation.y;

        // Coordinates on Z axis
        worldCoords[2] = squareCoords[2] + mLocation.z;
        worldCoords[5] = squareCoords[5] + mLocation.z;
        worldCoords[8] = squareCoords[8] + mLocation.z;
        worldCoords[11] = squareCoords[11] + mLocation.z;

        return worldCoords;
    }
}