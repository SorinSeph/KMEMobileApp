package com.example.kmemobileapp;

import android.content.Context;

public class Entity2DFactory {
    private final Context mContext;

    public static final int SHAPE_TRIANGLE    = 1;
    public static final int SHAPE_SQUARE      = 2;
    public static final int SHAPE_RECTANGLE   = 3;

    Entity2DFactory(Context context) {
        mContext = context;
    }

    public EntityGame createEntity(int option) {
        switch(option) {
            case SHAPE_SQUARE:
                PrimitiveSquare newSquare = new PrimitiveSquare(mContext, new Vector3D(-1,0,-1), 1.0f);
                return newSquare;
        }

        return null;
    }
}
