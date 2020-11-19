package com.example.kmemobileapp;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.Looper;
import android.os.SystemClock;
import android.renderscript.Matrix4f;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.lang.Math;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyRenderer implements GLSurfaceView.Renderer {

    private final Context mContext;
    private final MyGLSurfaceView mySurfaceView;
    private static final String TAG = "MyGLRenderer";
    private PrimitiveTriangle mTriangle;
    private PrimitiveSquare mHUDSquare;
    private PrimitiveSquare mHUDSquareRight;
    private ArrayList<EntityGame> mEntityList = new ArrayList<EntityGame>();
    private ArrayList<EntityGame> mHudList = new ArrayList<EntityGame>();
    private PrimitiveCube mTestCube;

    private boolean bUIInput;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mMVPMatrix2 = new float[16];

    private Matrix4f mMVPMatrix3 = new Matrix4f();

    private Vector3D cameraPos;
    private Vector3D cameraFront;
    private Vector3D cameraUp;

    private final float[] tempModelViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mOrthoMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    /* Second matrix for rendering the HUD */
    private float[] mViewMatrix2 = new float[16];
    private float[] mHUDViewMatrix = new float[16];

    private final float[] mRotationMatrix = new float[16];
    private Entity3DFactory entityFactory;
    private Entity2DFactory entity2DFactory;

    private float Yaw;
    private float Pitch;
    private float Roll;

    private int g_EntityIterator;

    private int mWidth;
    private int mHeight;

    private float mAngle;
    private float mXRotation;
    private float mYRotation;
    private float mCurrentX;
    private float mCurrentY;
    private float mAngleX;
    private float mAngleY;
    private float mPreviousX;
    private float mPreviousY;
    private float mRotationAngleX;
    private float mRotationAngleY;
    private Vector3D mDirection;
    private long lastFrameTime;
    private boolean bCanMove;
    private boolean bMoveForward;
    private boolean bMoveRight;
    public boolean bAddNewEntity;
    private Camera mCamera = new Camera();

    float lookX;
    float lookY;
    float lookZ;
    float eyeX;
    float eyeY;
    float eyeZ;
    float upX;
    float upY;
    float upZ;

    private Vector3D mLocation;

    /**
     * Temporary variables
     * */

    float tempDispX = 0;
    float tempDispY = 0;

    public MyRenderer(final Context context, final MyGLSurfaceView myGLSurfaceview) {
        mContext = context;
        mySurfaceView = myGLSurfaceview;
        mLocation = new Vector3D(0, 0, 3);
        entity2DFactory = new Entity2DFactory(mContext);
        bUIInput = false;
        bAddNewEntity = true;
        g_EntityIterator = 0;
        mWidth = 0;
        mHeight = 0;
        mDirection = new Vector3D(0,0,0);
        mAngleX = 0;
        mAngleY = 0;
        cameraPos = new Vector3D(0,0,3);
        cameraFront = new Vector3D (0,0,-1);
        Yaw = 0;
        Pitch = -90;
        Log.d("RendererLog", "Thread in renderer: " + Thread.currentThread().getName());
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        entityFactory = new Entity3DFactory();

        Matrix.setIdentityM(mViewMatrix, 0);

        //mTriangle = new PrimitiveTriangle();
        mHUDSquare = new PrimitiveSquare(mContext, new Vector3D(-1, 0, 1), 0.5f);
        mHUDSquareRight = new PrimitiveSquare(mContext, new Vector3D(-0.35f, -0.3f, 1), 0.5f);

        float distance = -2.f;
        for (int i = 0; i < 3; i++) {
            mEntityList.add(new PrimitiveCube(mContext, mMVPMatrix, 1.0f, new Vector3D(distance, -1f, -5f)));
            distance += 2.f;
        }

        mTestCube = new PrimitiveCube(mContext, mMVPMatrix, 1.0f, new Vector3D(0f, -1f, -5f));

        mHudList.add(mHUDSquare);
        mHudList.add(mHUDSquareRight);

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0.f, 0.f, 0.f, 0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mHUDViewMatrix, 0, 0,0,10, 0f,0f,0f, 0f,1.0f,0.0f);

        Pitch = 0;
        Yaw   = 90;
        Roll  = -90;
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        //long time = SystemClock.uptimeMillis() % 10000L;

        // World rendering

        Vector3D cameraPos = mCamera.getPosition();
        Vector3D cameraRot = mCamera.getRotation();

        Matrix.setIdentityM(mViewMatrix,0);

        Matrix.rotateM(mViewMatrix, 0, cameraRot.x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mViewMatrix, 0, cameraRot.y, 0.0f, 1.0f, 0.0f);

        Matrix.translateM(mViewMatrix, 0, cameraPos.x, cameraPos.y, cameraPos.z);

        for (EntityGame entityIterator : mEntityList) {
            float[] testModelMatrix = entityIterator.getModelMatrix();
            float[] currentView = mViewMatrix;
            Matrix.multiplyMM(tempModelViewMatrix, 0, currentView, 0, testModelMatrix, 0);
            entityIterator.draw(tempModelViewMatrix, mProjectionMatrix);
        }

        if (bCanMove) {
            float sensitivity = 0.01f;
            if (bMoveForward) {
                //cameraPos.z += 0.05f;
                cameraPos.x += (float)Math.sin(Math.toRadians(cameraRot.y)) * -1.0f * 0.05f;
                cameraPos.z += (float)Math.cos(Math.toRadians(cameraRot.y)) * 0.05f;
                Log.d("MoveForwardTag", "bMoveForward = true");
            }
            // actually move back
            if (bMoveRight) {
                cameraPos.x += (float)Math.sin(Math.toRadians(cameraRot.y)) * 1.0f * 0.05f;
                cameraPos.z += (float)Math.cos(Math.toRadians(cameraRot.y)) * -0.05f;
                Log.d("MoveRightTag", "bMoveRight = true");
            }
        }

        // HUD rendering

        // Disable depth test for drawing on top
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        for (EntityGame hudIterator : mHudList) {
            Matrix.multiplyMM(mMVPMatrix2, 0, mHUDViewMatrix, 0, hudIterator.getModelMatrix(), 0);
            Matrix.multiplyMM(mMVPMatrix2, 0, mOrthoMatrix, 0, mMVPMatrix2, 0);

            hudIterator.draw(mMVPMatrix2);

            float[] modelScreenCoords = new float[3];
            int[] convertedViewMatrix = new int[16];

            for (int i = 0; i < mHUDViewMatrix.length; i++) {
                convertedViewMatrix[i] = (int) mHUDViewMatrix[i];
            }
        }

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES30.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;

        Log.d("SurfaceTag", "width = " + width + " and height = " + height);

        float ratio = (float) mWidth / mHeight;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 10);
        Matrix.orthoM(mOrthoMatrix, 0, -ratio, ratio, -1, 1, 1, 15);


    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);
        int[] compiled = new int[1];

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e("ESShader", GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

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
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();

        return textureHandle[0];
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     * <p>
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public static void checkLinking(int inProgram, int[] inParams) {
        GLES30.glGetProgramiv(inProgram, GLES30.GL_LINK_STATUS, inParams, 0);
        if (inParams[0] == 0) {
            //Log.e(TAG, "GL Linking Error " + GLES30.glGetError());
            Log.e("Program Info Log:", GLES30.glGetProgramInfoLog(inProgram));
            throw new RuntimeException();
        }
    }

    public void RotateView(float rotX, float rotY) {
        mXRotation = rotX;
        mYRotation = rotY;

        Vector3D displaceVec = new Vector3D (rotX, rotY, 0);

        mCamera.moveRotation(displaceVec);
    }

    public void stopViewRotation() {
        mXRotation = 0;
        mYRotation = 0;
    }

    public void stopMovement() {
        bCanMove = false;
        bMoveForward = false;
        bMoveRight = false;
    }

    public void singleLineTrace(final Vector3D traceStart, final Vector3D traceEnd) {
        //Thread t = Thread.currentThread();

        mySurfaceView.queueEvent(new Runnable() {

            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                float[] coords = new float[4];
                float[] tempCoords = new float[4];
                int[] viewport = {0, 0, mWidth, mHeight};

                float[] tempIdentityMatrix = new float[16];
                Matrix.setIdentityM(tempIdentityMatrix,0);

                int result = GLU.gluUnProject(traceStart.x, traceStart.y, 0, mViewMatrix, 0, mProjectionMatrix, 0, viewport, 0, coords, 0);

                if(result != 0) {
                    traceStart.x = coords[0];
                    traceStart.y = coords[1];
                    traceStart.z = coords[2];

                    PrimitiveLine lineTrace = new PrimitiveLine(traceStart, traceEnd);

                    Log.d("LineTraceTag", "Value of x is: " + traceStart.x + " and value of y is: " + traceStart.y);

                    mHudList.add(lineTrace);
                }
            }
        });
    }

    public void addCube() {
        mySurfaceView.queueEvent(new Runnable() {

            @Override
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                bAddNewEntity = false;

                PrimitiveCube tempCube = new PrimitiveCube(mContext, mMVPMatrix, 1.0f, new Vector3D(0, 0, -5));
                bAddNewEntity = true;
            }
        });
    }

    public void checkHUDTouch(float inX, float inY) {

        int[] viewport = {0, 0, mWidth, mHeight};
        float[] coords = new float[4];
        ArrayList<Float> hudCoordsLIst = new ArrayList<Float>();

        int hudNumber = mHudList.size();
        int coordsPerSquare = 12;
        float[] hudCoords = new float[coordsPerSquare * hudNumber];

        int result = GLU.gluUnProject(inX, inY, 0, mHUDViewMatrix, 0, mOrthoMatrix, 0, viewport, 0, coords, 0);

        if (result == 1) {
            Log.d("UnprojectedXTag", "Unprojected value of X is: " + coords[0]);
            Log.d("UnprojectedYTag", "Unprojected value of Y is: " + coords[1]);
            coords[1] = -coords[1];
        }

        for (EntityGame hudIterator : mHudList) {
            float[] tempCoords = hudIterator.returnWorldCoords();
            for(int j = 0; j < tempCoords.length; j++) {
                hudCoordsLIst.add(tempCoords[j]);
            }
        }

        Log.d("ExplicitButtonCoord", "From button2, coordinates 12, 21, 13, 16 is: " + hudCoordsLIst.get(12) + hudCoordsLIst.get(21)+ hudCoordsLIst.get(13)+ hudCoordsLIst.get(16));

        for (int k = 0; k < hudNumber; k++) {
            // elements: 0, 9 for x, 1, 4 for y
            if ( coords[0] > hudCoordsLIst.get(0 + (12 * k)) && coords[0] < hudCoordsLIst.get(9 + (12 * k)) &&
                    coords[1] > hudCoordsLIst.get(4 + (12 * k)) && coords[1] < hudCoordsLIst.get(1 + (12 * k)) ) {
                Log.d("ActivatedButton", "Activated button #" + k);
                if (k == 0) {
                    mySurfaceView.queueEvent(new Runnable() {

                        @Override
                        public void run() {
                            mHUDSquare.updateTexture(mContext, R.drawable.arrow_up_pressed);
                        }
                    });
                    bCanMove = true;
                    bMoveForward = true;
                }
                else if (k == 1) {
                    bCanMove = true;
                    bMoveRight = true;
                }
                else {
                    bCanMove = false;
                    bMoveForward = false;
                    bMoveRight = false;
                }
            }
        }

        for (int i = 0; i < hudCoordsLIst.size(); i++) {
            Log.d("TouchTest", "HUD coord #" + i + " is: " + hudCoordsLIst.get(i));
        }
    }

    public void setMoveForward(boolean InBool) {
        bCanMove = InBool;
    }

    public void buttonsReleased() {

        mySurfaceView.queueEvent(new Runnable() {

            @Override
            public void run() {
                mHUDSquare.updateTexture(mContext, R.drawable.arrow_up_unpressed);
            }
        });
    }
}