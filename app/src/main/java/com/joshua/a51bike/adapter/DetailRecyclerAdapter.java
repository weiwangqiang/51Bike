package com.joshua.a51bike.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.joshua.a51bike.Interface.RVItemListener;
import com.joshua.a51bike.R;

import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2016/9/28.
 */
public class DetailRecyclerAdapter extends  RecyclerView.Adapter<DetailRecyclerAdapter.MyViewHolder> {
    private RVItemListener lister;
    private MyViewHolder viewHolder;
    private Context context ;
    private int view;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private String TAG = "recyclerAdapter";
    private List<Map<String,String>> data ;
    private String[] from;
    public DetailRecyclerAdapter(Context context, int view,List<Map<String,String>>data,String[] from){
        this.context = context;
        this.view = view;
        this.data = data;
        this.from = from;
    }

    @Override
    public DetailRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("adapter","adapter viewType is "+viewType);

        viewHolder = new MyViewHolder(LayoutInflater.
                from(context).inflate(view,parent,false));

//        if (viewType == TYPE_ITEM) {
//            return viewHolder;
//        }
//        // type == TYPE_FOOTER 返回footerView
//        else if (viewType == TYPE_FOOTER) {
//            Log.e("adapter","adapter viewType is "+viewType+" has get View footer");
//            View view = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.footer, null);
//            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
//                    RecyclerView.LayoutParams.WRAP_CONTENT));
//            return new MyViewHolder(view,null);
//        }
        return viewHolder;
    }
//    @Override
//    public int getItemViewType(int position) {
//        // 最后一个item设置为footerView
//        Log.e("adapter","return position is "+position);
//        return TYPE_ITEM;
////        if (position + 1 == getItemCount())
////        {
////            return TYPE_FOOTER;
////        } else if(position!=0){
////            return TYPE_ITEM;
////        }
////        return -1;
//    }

    public void setItemClickListener(RVItemListener lister){
        this.lister = lister;
    }
    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link #} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link #()} which will
     * have the updated juhe.jiangdajiuye.adapter position.
     * <p/>
     * Override {@link #(, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the juhe.jiangdajiuye.adapter's data set.
     */
    @Override
    public void onBindViewHolder(DetailRecyclerAdapter.MyViewHolder holder, final int position) {
        holder.title.setText(data.get(position).get(from[0]));
        holder.time.setText(data.get(position).get(from[1]));
        holder.money.setText(data.get(position).get(from[2]));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lister != null){
                    lister.onItemClicked(position);
                }
            }
        });
    }

    /**
     * Returns the total number of items in the data set hold by the juhe.jiangdajiuye.adapter.
     *
     * @return The total number of items in this juhe.jiangdajiuye.adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }
    public void RefreshDate (Map<String,String> map){
        data.add(0,map);
        notifyItemInserted(0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,time,money;
        private View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.item_title);
            time = (TextView) itemView.findViewById(R.id.item_time);
            money = (TextView) itemView.findViewById(R.id.item_money);

        }

    }
}
