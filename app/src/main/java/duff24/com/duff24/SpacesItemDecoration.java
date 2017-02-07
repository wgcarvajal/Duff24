package duff24.com.duff24;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by geovanny on 3/09/16.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int left_right;
    private int bottom;

    public SpacesItemDecoration(int left_right, int bottom) {
        this.left_right =left_right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = left_right;
        outRect.right = left_right;
        outRect.bottom = bottom;
    }
}
