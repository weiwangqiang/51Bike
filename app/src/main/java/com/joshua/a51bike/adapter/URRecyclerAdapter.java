package com.joshua.a51bike.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshua.a51bike.Interface.myitemLister;
import com.joshua.a51bike.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *  我的行程列表adapter
 *
 * Created by wangqiang on 2016/9/28.
 */
public class URRecyclerAdapter extends  RecyclerView.Adapter<URRecyclerAdapter.MyViewHolder> {
    private myitemLister lister;
    private MyViewHolder viewHolder;
    private Context context ;
    private int view;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<HashMap<String,String>> data ;
    public URRecyclerAdapter(Context context, int view, List<HashMap<String,String>> data){
        this.context = context;
        this.view = view;
        this.data = data;
    }

    @Override
    public URRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewHolder = new MyViewHolder(LayoutInflater.
                from(context).inflate(view,parent,false),context);
        return viewHolder;
    }

    public void setLister(myitemLister lister){
        this.lister = lister;
    }
    @Override
    public void onBindViewHolder(URRecyclerAdapter.MyViewHolder holder,final int position) {
        try{
            holder.time.setText(data.get(position).get("time").toString());
            holder.spend.setText(data.get(position).get("spend").toString());
            holder.bikeId.setText(data.get(position).get("carId").toString());
        }catch(Exception e){
            e.printStackTrace();
        }
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
    public void RefreshDate (ArrayList<HashMap<String,String>> data){
        this.data = data;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView time,spend,bikeId;
        private CardView cardView;

        public MyViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            if(context!=null){
                getshow();

            }
        }
        public void getshow(){
            time = (TextView) itemView.findViewById(R.id.route_item_time);
            spend = (TextView)itemView.findViewById(R.id.route_item_spend);
            bikeId = (TextView)itemView.findViewById(R.id.route_item_carId);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

        }

        @Override
        public void onClick(View v) {
            if(lister != null){
                lister.onItemClicked(getAdapterPosition());
            }
        }
    }
}
