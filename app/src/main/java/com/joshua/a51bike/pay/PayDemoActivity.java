package com.joshua.a51bike.pay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.joshua.a51bike.R;
import com.joshua.a51bike.pay.util.OrderInfoUtil2_0;

import java.util.Map;

/**
 *  重要说明:
 *  
 *  这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
 *  真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
 *  防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
 */
public class PayDemoActivity extends FragmentActivity {
	
	/** 支付宝支付业务：入参app_id */
	public static final String APPID = "2016073000125976";
	
	/** 支付宝账户登录授权业务：入参pid值 */
	public static final String PID = "";
	/** 支付宝账户登录授权业务：入参target_id值 */
	public static final String TARGET_ID = "";

	/** 商户私钥，pkcs8格式 */
	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
	/** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
	public static final String RSA2_PRIVATE = "";
	public static final String RSA_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCkKVm+MBheoapkNK6khUf5IZ/gcu8HQy+yuxSa525lKxsiBpaKqhVSTp0JK+b7V1fpxxhJyS2Y6JM6UhKiCS5q8hotw1B7zGsp4w4rvpMTvwYmhp2hMoMdOxKoagIc8FavDegsjYud7UbZZSHAnefhzC6uMvkarzql6j14XpMRBx7eEQiCaoxsdu0gnY0GTi2MTEwkhEEYVc5YKz/f09ioaavu4RmQRP4YmxfThumWn5eF7T7wkZU85gTwHJpVDl3mDAle33uMHMl1snBN9Q68NbhmLV4uzzOqgtIot0Re0BZUxTrzvd/JN9Hz3TU+0GZY7XGJAcCZnmQfVj9+ZYpLAgMBAAECggEAMVQPmHvBRuZTWisOc3dtSipVbU98DfWdZpqatXVnkdTjIVVTdTVolMP0oiXkEZCMZT4jSUC/h9wTKYox+SjDHvXC8g9nptulM//7aR+p+FwFUBxRT3frOCRhFeRM4D9D/PQz2Pdrhbgf+wFNpCCO5iXBvSyp99/BwsthT0Mz9ABwTviwhMRqvxebjL/0Tp7Qm+Ti9pDAXV2a7rhLdETpL7GbllyLivacjyvN4apzhnwOC5p6IGFu6kCE46B3Y5Ex7ng4XtQ9qLVA+6sFOcRLnosI3ehR95eTmWbX5wKCBR+MgixLf9PopYfxYSnF11wU2AiKQQOpLHV9c+zc2WJAwQKBgQDsRcJT2Ltp0umT4F8bzF4jBlcFzIdkeqsDUSGQSN7tWNHr8n12n1XxO+OVA2iTaGwQvMeElrEQm5tgOw8l0zs28rDOaAUZ2B0smvLFLDZTWB1pm9spdX4XPu7fgueesiPW/Xo2duI5dvCP9AS4U7/I8uBAwC7NFdBx47lJYpvoKwKBgQCx3j65EFD8XI2+R1xxynO14xSUXG/ymLpMMyEb2wR/7URUBmxSSQ5BXsJxYzDlwuHr5o79HlubXs5nqNQP2epFmErnrPFVHsciOxaZCgpEbyzQ01POeHsmKDwKfaMfk3WW0YsVYrKXScgwPZrAyYBHPHYD4HinafqP0igf2My2YQKBgB2frXrHzM+64xBobGRW3mKz08tlVoBxpOl4jaXCjTjjDwbvU23BEox5ftJbKAx7Zjk/AiEMp8y2Reft37tVXbJJAYQPpLwVzaqfDttL7M/MV3u7T+JF2fZeLKMXjaTnHvlAtMcJZtnfoM/bv4/A6GXwlc9oiTWri4QMEloyI+wbAoGBAKl2fXwEt6Q46jMn+kPQB35749eWWnJgYN7It/q6KQdZH4iOthWP1S0jA07UmnnNu0HuYL14dv8IzM6mY6TWkgXMm1EvzJ60vDU45sbYcf/RjHlJXseQsoQgQVQdb5VeAOK4sjKl3lGV0k1j5FVZpgY47Je0/j9DEZ6FNAVjoWcBAoGAUQom3VjnxyVgbERp4I66SLHyNR0EWXq/AeGgyKp3sBf0cPzE01Q0CGJK9jTKluHILDD7e18skueU+ZWtMFhaEvUs45JvyuwQ5LA6MGyxnqfCluTRrl8SaTiI2RwcS8bb4G27IF4edISEIiqOt3Bcn8ol3UelnJMBwBh5byzUADI=";
	
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
	}
	
	/**
	 * 支付宝支付业务
	 * 
	 * @param v
	 */
	public void payV2(View v) {
		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							//
							finish();
						}
					}).show();
			return;
		}
	
		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * orderInfo的获取必须来自服务端；
		 */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
		final String orderInfo = orderParam + "&" + sign;
		
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(PayDemoActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}


	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
	 * 
	 * @param v
	 */
	public void h5Pay(View v) {
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();
		/**
		 * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
		 * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
		 * 商户可以根据自己的需求来实现
		 */
		String url = "http://m.taobao.com";
		// url可以是一号店或者淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}

}
