package com.joshua.a51bike.activity.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RouteSearch;
import com.joshua.a51bike.R;
import com.joshua.a51bike.adapter.RideSegmentListAdapter;
import com.joshua.a51bike.util.AMapUtil;


public class RideRouteDetailActivity extends Activity implements View.OnClickListener {
	private RidePath mRidePath;
	private TextView mTitle,mTitleWalkRoute,navigation;
	private ListView mRideSegmentList;
	private RideSegmentListAdapter mRideSegmentListAdapter;
	private RouteSearch.FromAndTo fromAndTo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		getIntentData();
//		navigation = (TextView) findViewById(R.id.navigation);
		navigation.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText("骑行路线详情");
		mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
		String dur = AMapUtil.getFriendlyTime((int) mRidePath.getDuration());
		String dis = AMapUtil
				.getFriendlyLength((int) mRidePath.getDistance());
		mTitleWalkRoute.setText(dur + "(" + dis + ")");
		mRideSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mRideSegmentListAdapter = new RideSegmentListAdapter(
				this.getApplicationContext(), mRidePath.getSteps());
		mRideSegmentList.setAdapter(mRideSegmentListAdapter);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}
		mRidePath = intent.getParcelableExtra("ride_path");
		fromAndTo = intent.getParcelableExtra("fromAndTo");
	}

	public void onBackClick(View view) {
		this.finish();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
//			case R.id.navigation:
//				Intent intent = new Intent(this,navi.class);
//				intent.putExtra("ridePath",mRidePath);
//				intent.putExtra("fromAndTo",fromAndTo);
//				startActivity(intent);
//				break;
		}
	}
}
