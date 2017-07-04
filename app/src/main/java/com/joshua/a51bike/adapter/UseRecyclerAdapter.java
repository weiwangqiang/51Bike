package com.joshua.a51bike.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshua.a51bike.Interface.myitemLister;
import com.joshua.a51bike.R;
import com.joshua.a51bike.entity.UserAndUse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wangqiang on 2016/9/28.
 */
public class UseRecyclerAdapter extends  RecyclerView.Adapter<UseRecyclerAdapter.MyViewHolder> {
    private myitemLister lister;
    private MyViewHolder viewHolder;
    private Context context ;
    private int view;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private String TAG = "recyclerAdapter";
    private List<UserAndUse> data ;
    private String from;
    public UseRecyclerAdapter(Context context, int view, List<UserAndUse>data, String from){
        this.context = context;
        this.view = view;
        this.data = data;
        this.from = from;
    }

    @Override
    public UseRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("adapter","adapter viewType is "+viewType);

        viewHolder = new MyViewHolder(LayoutInflater.
                from(context).inflate(view,parent,false));
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

    public void setItemClickListener(myitemLister lister){
        this.lister = lister;
    }
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void onBindViewHolder(UseRecyclerAdapter.MyViewHolder holder, final int position) {
        holder.title.setText(from);
        Date date= new Date(data.get(position).getUseEndTime().getTime());
        holder.time.setText(format.format(date));
        holder.money.setText(data.get(position).getUseMoney()+"元");
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
    public void RefreshDate (List<UserAndUse> data){
        this.data = data;
        notifyDataSetChanged();
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
