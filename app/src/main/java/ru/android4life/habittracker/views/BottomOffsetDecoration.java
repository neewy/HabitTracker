package ru.android4life.habittracker.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class for adding some free space at the end
 * of any RecyclerView list.
 * <p>
 * Created by neewy on 29.10.16.
 */

public class BottomOffsetDecoration extends RecyclerView.ItemDecoration {
    private int mBottomOffset;

    public BottomOffsetDecoration(int bottomOffset) {
        mBottomOffset = bottomOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int dataSize = state.getItemCount();
        int position = parent.getChildPosition(view);
        if (dataSize > 0 && position == dataSize - 1) {
            outRect.set(0, 0, 0, mBottomOffset);
        } else {
            outRect.set(0, 0, 0, 0);
        }

    }
}
