package com.mts.athanasiosmoutsioulis.edaattendancesystem;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
        import android.graphics.Canvas;
        import android.graphics.Rect;
        import android.graphics.drawable.Drawable;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.AttributeSet;
        import android.view.View;

/***
 * By C. Efstratiou
 *
 * Based on zokipirlo implementation: https://gist.github.com/zokipirlo/82336d89249e05bba5aa
 *      which is based on lapastillaroja implementation: https://gist.github.com/lapastillaroja/858caf1a82791b6c1a36
 *      which is based on original code by fatfingers : https://gist.github.com/fatfingers/233abbae200b5e87297b
 *
 * Includes fix for animated decoration as described by Lorne Laliberte : http://stackoverflow.com/questions/29061450/how-to-disable-recyclerview-item-decoration-drawing-for-the-duration-of-item-ani
 *
 * For examples on how to use check the readme at: https://gist.github.com/zokipirlo/82336d89249e05bba5aa#file-readme-md
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration{
    private Drawable mDivider;

    public DividerItemDecoration(Resources resources) {
        mDivider = resources.getDrawable(R.drawable.line_divider);
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}