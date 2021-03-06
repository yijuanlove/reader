package com.yn.reader.snap;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.yn.reader.R;
import com.yn.reader.model.booklist.Navigation;

import java.util.ArrayList;

public class SnapAdapter extends RecyclerView.Adapter<SnapAdapter.ViewHolder> implements GravitySnapHelper.SnapListener {
    public void setOnSnapNavigationSelectionChanged(OnSnapNavigationSelectionChanged onSnapNavigationSelectionChanged) {
        mOnSnapNavigationSelectionChanged = onSnapNavigationSelectionChanged;
    }

    public interface OnSnapNavigationSelectionChanged{
        void selected(Snap snap,Navigation beforeSelection, Navigation afterSelection);
    }
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private ArrayList<Snap> mSnaps;
    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };
    private OnSnapNavigationSelectionChanged mOnSnapNavigationSelectionChanged;

    public SnapAdapter() {
        mSnaps = new ArrayList<>();
    }

    public void addSnap(Snap snap) {
        mSnaps.add(snap);
    }

    public Navigation getSelectionNavigationBySnapTag(int tag){
        Navigation navigation = null;
        try {
            for (Snap bean:mSnaps) {
                if (bean.getTag()==tag){
                    for (Navigation item:bean.getApps()) {
                        if (item.isSelected()){
                            navigation = item;
                            if (bean.getApps().indexOf(item)==0)navigation.setId(null);
                            break;
                        }
                    }
                    break;
                }
            }
        }catch (Exception ex){}
        return navigation;
    }
    public Snap getSnapByTag(int tag){
        Snap snap = null;
        try {
            for (Snap bean:mSnaps) {
                if (bean.getTag()==tag){
                    snap = bean;
                }
            }
        }catch (Exception ex){}
        return snap;
    }

    @Override
    public int getItemViewType(int position) {
        Snap snap = mSnaps.get(position);
        switch (snap.getGravity()) {
            case Gravity.CENTER_VERTICAL:
                return VERTICAL;
            case Gravity.CENTER_HORIZONTAL:
                return HORIZONTAL;
            case Gravity.START:
                return HORIZONTAL;
            case Gravity.TOP:
                return VERTICAL;
            case Gravity.END:
                return HORIZONTAL;
            case Gravity.BOTTOM:
                return VERTICAL;
        }
        return HORIZONTAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = viewType == VERTICAL ? LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_snap_vertical, parent, false)
                : LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_snap, parent, false);

        if (viewType == VERTICAL) {
            view.findViewById(R.id.recyclerView).setOnTouchListener(mTouchListener);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Snap snap = mSnaps.get(position);

        if (snap.getGravity() == Gravity.START || snap.getGravity() == Gravity.END) {

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));

            new GravitySnapHelper(snap.getGravity(), false, this).attachToRecyclerView(holder.recyclerView);

        } else if (snap.getGravity() == Gravity.CENTER_HORIZONTAL ||
                snap.getGravity() == Gravity.CENTER_VERTICAL) {

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), snap.getGravity() == Gravity.CENTER_HORIZONTAL ?
                    LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));

            new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);

        } else if (snap.getGravity() == Gravity.CENTER) { // Pager snap

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));

            new GravityPagerSnapHelper(Gravity.START).attachToRecyclerView(holder.recyclerView);

        } else { // Top / Bottom

            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext()));

            new GravitySnapHelper(snap.getGravity()).attachToRecyclerView(holder.recyclerView);
        }


        holder.assign(snap);
    }

    @Override
    public int getItemCount() {
        return mSnaps.size();
    }

    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public RecyclerView recyclerView;
        private Adapter mAdapter;


        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
        public void assign(final Snap snap){
            mAdapter = new Adapter(snap.getGravity() == Gravity.START
                    || snap.getGravity() == Gravity.END
                    || snap.getGravity() == Gravity.CENTER_HORIZONTAL,
                    snap.getGravity() == Gravity.CENTER, snap.getApps());

            mAdapter.setOnNavigationSelectionChanged(new Adapter.OnNavigationSelectionChanged() {
                @Override
                public void selected(Navigation beforeSelection, Navigation afterSelection) {
                    if (mOnSnapNavigationSelectionChanged!=null)mOnSnapNavigationSelectionChanged.selected(snap,beforeSelection,afterSelection);
                }
            });
            recyclerView.setAdapter(mAdapter);
        }
    }
}

