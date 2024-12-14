package com.example.memorycollection;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class OrderMove implements View.OnTouchListener {
    private float dX, dY;
    private int lastAction;
    private Context context;

    public OrderMove(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                return true;

            case MotionEvent.ACTION_MOVE:
                v.setX(event.getRawX() + dX);
                v.setY(event.getRawY() + dY);
                lastAction = MotionEvent.ACTION_MOVE;
                return true;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(context, "画像がクリックされました", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return false;
        }
    }
}