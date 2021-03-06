package com.marverenic.music.instances.section;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marverenic.music.R;
import com.marverenic.heterogeneousadapter.EnhancedViewHolder;
import com.marverenic.heterogeneousadapter.HeterogeneousAdapter;

public class SpacerSingleton extends HeterogeneousAdapter.SingletonSection<Void> {

    private int mHeight;

    public SpacerSingleton(int height) {
        super(null);
        mHeight = height;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    @Override
    public boolean showSection(HeterogeneousAdapter adapter) {
        int thisIndex = adapter.getSectionIndex(this);
        return adapter.getSection(thisIndex - 1).getItemCount(adapter) > 0;
    }

    @Override
    public EnhancedViewHolder<Void> createViewHolder(HeterogeneousAdapter adapter,
                                                                  ViewGroup parent) {
        View itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.instance_blank, parent, false);


        return new ViewHolder(itemView, (RecyclerView) parent);
    }

    public class ViewHolder extends EnhancedViewHolder<Void> {

        RecyclerView parent;

        public ViewHolder(View itemView, RecyclerView parent) {
            super(itemView);
            this.parent = parent;
        }

        @Override
        public void onUpdate(Void item, int sectionPosition) {
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();

            int height = SpacerSingleton.this.mHeight;
            if (height >= 0) {
                params.height = height;
            } else {
                params.height = parent.getHeight() + height;
            }
            itemView.setLayoutParams(params);
        }
    }
}
