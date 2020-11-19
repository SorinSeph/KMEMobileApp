package com.example.kmemobileapp;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;


public class MyGLSurfaceView extends GLSurfaceView {

    private final MyRenderer mRenderer;
    private Context context3;

    public MyGLSurfaceView(Context context) {
        super(context);

        context3 = context;
        // Create an OpenGL ES 2.0 context.  CHANGED to 3.0  JW.
        setEGLContextClientVersion(3);
        //fix for error No Config chosen, but I don't know what this does.
        //super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyRenderer(context, this);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        Log.d("SurfaceViewThread", Thread.currentThread().getName());
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TOUCH_SCALE_FACTOR2 = 1.5f;
    private float mPreviousX;
    private float mPreviousY;
    private float mPreviousDX;
    private float mPreviousDY;
    float displaceX;
    float displaceY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        Log.d("TouchEventsTag", e.getAction() + " " + e.getActionMasked());

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("LocationX", "Value of x is: " + x);
                mRenderer.checkHUDTouch(x,y);
                break;

            case MotionEvent.ACTION_MOVE:
                displaceX = 0;
                displaceY = 0;

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if (dx != 0) {
                    displaceY = dx;
                }

                if (dy != 0) {
                    displaceX = dy;
                }

                // Working method
                mRenderer.RotateView(displaceX * TOUCH_SCALE_FACTOR, displaceY * TOUCH_SCALE_FACTOR);
                requestRender();
                Log.d("Displacement", "Displace X = " + displaceX + " and Displace Y = " + displaceY);
                break;

            case MotionEvent.ACTION_UP:

                Log.d("Debug3", "Motion event is ACTION.UP");
                mRenderer.stopMovement();
                mRenderer.stopViewRotation();
                mRenderer.setMoveForward(false);
                mRenderer.buttonsReleased();
                requestRender();
                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        mPreviousDX = x - mPreviousX;
        mPreviousDY = x - mPreviousY;

        return true;
    }
}