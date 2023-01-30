package com.rentas.ppob.libs;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffLeft;
    private int mItemOffTop;
    private int mItemOffRight;
    private int mItemOffBottom;

    public ItemOffsetDecoration(int offLeft, int offTop, int offRight, int offBottom ) {
        mItemOffLeft = offLeft;
        mItemOffTop = offTop;
        mItemOffRight = offRight;
        mItemOffBottom = offBottom;
    }

    public ItemOffsetDecoration(@NonNull Context context, int itemOffsetId) {
        this(Utility.getPixelValue(context,itemOffsetId));
    }

    public ItemOffsetDecoration(int pixelValue) {
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffLeft, mItemOffTop, mItemOffRight, mItemOffBottom);
    }
}
