package com.omarica.iptv;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> {
    private final PublishSubject<String> onClickSubject = PublishSubject.create();
    private List<Stream> mItem;
    private Context mContext;
    private LayoutInflater mInflater;
    private int row_index = 0;

    public PlaylistAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
    }

    public void selectRow(int index) {
        row_index = index;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View sView = mInflater.inflate(R.layout.item_playlist, parent, false);
        return new ItemHolder(sView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final Stream item = mItem.get(position);
        if (item != null) {
            holder.update(item);
        }
        holder.number.setText((position + 1) + "");
        if (position == row_index) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, VideoActivity.class);
                intent.putExtra("index", position);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {

        if (mItem != null)
            return mItem.size();
        else return 0;
    }

    public void update(List<Stream> _list) {
        this.mItem = _list;
        notifyDataSetChanged();
    }

    public Observable<String> getPositionClicks() {
        return onClickSubject.asObservable();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {


        TextView name;
        TextView number;
        //    TextView url;

        public ItemHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            number = (TextView) view.findViewById(R.id.item_number);
            // url = (TextView) view.findViewById(R.id.item_url);

        }

        public void update(final Stream item) {
            name.setText(item.getName());
            //  url.setText(item.getUrl());
        }
    }
}
