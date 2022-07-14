package com.example.giphy;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int spase;
    public SpaceItemDecoration(int space){
        this.spase = space;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = spase;
        outRect.right = spase;
        outRect.bottom = spase;
        // Добавить верхнее поле только для первого элемента, чтобы избежать двойных пробелов между элементами
        if(parent.getChildLayoutPosition(view)==0){
            outRect.top = spase;
        }else {
            outRect.top=0;
        }

    }
}
