package com.qpet.inicio;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecoration extends RecyclerView.ItemDecoration{
    private Drawable divider;
    private int spaceHeight;

    public MarginItemDecoration(Drawable divider, int spaceHeight) {
        this.divider = divider;
        this.spaceHeight = spaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = spaceHeight;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int childCount = parent.getChildCount();
        int lastVisiblePosition = -1;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child != null) { // Verifica si la vista child no es nula
                lastVisiblePosition = parent.getChildAdapterPosition(child);

                int top = child.getBottom();
                int bottom = top + spaceHeight;

                // Dibuja el espacio con el color de fondo deseado
                divider.setBounds(parent.getLeft(), top, parent.getRight(), bottom);
                divider.draw(c);
            }
        }

        // Dibuja el espacio para el último elemento visible si es necesario
        if (lastVisiblePosition == parent.getAdapter().getItemCount() - 1) {
            View lastChild = parent.getChildAt(childCount - 1);
            if (lastChild != null) { // Verifica si la vista lastChild no es nula
                int top = lastChild.getBottom();
                int bottom = top + spaceHeight;

                divider.setBounds(parent.getLeft(), top, parent.getRight(), bottom);
                divider.draw(c);
            }
        }
    }
}
