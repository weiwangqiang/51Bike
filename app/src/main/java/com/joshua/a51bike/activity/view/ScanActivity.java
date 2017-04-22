package com.joshua.a51bike.activity.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.zxing.camera.CameraManager;
import com.joshua.a51bike.zxing.decoding.CaptureActivityHandler;
import com.joshua.a51bike.zxing.decoding.InactivityTimer;
import com.joshua.a51bike.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * class description here
 *
 *	扫码界面
 *
 *  @version 1.0.0
 * @outher wangqiang
 * @project 51Bike
 * @since 2017-01-10
 */
public class ScanActivity extends BaseActivity implements Callback {
	private String TAG = "ScanActivity";
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;//是否加载surface
	private boolean playBeep;//是否在振动

	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private final static int FETCH_PACKAGE_CODE = 1;
	private final static int DELIVER_PACKAGE_CODE = 2;
	private final static int RESULT_CODE_OK = 3;
	private final static int RESULT_CODE_FAIL = 4;
	private Intent resultIntent;
	private static final int REQUEST_CODE_CAMERA = 0x0002;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		initGranted();
//		initTextView();
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		findViewById(R.id.left_back).setOnClickListener(this);
		findViewById(R.id.scan_title).setOnClickListener(this);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	/**
	 * 初始化权限
	 */
	private void initGranted() {
		int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
		if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
			return;
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_CAMERA:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// Permission Granted
					Toast.makeText(ScanActivity.this, "Location Granted", Toast.LENGTH_SHORT)
							.show();
				} else {
					// Permission Denied
					Toast.makeText(ScanActivity.this, "Location Denied", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	

	public void handleDecode(Result result, Bitmap barcode) {
		Log.e(TAG,"----->>>handleDecode result is  \n  "+result.getText());
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		resultIntent = new Intent();
		if (resultString.equals("")) {
			uiUtils.showToast("请重新再试");
//			setResult(RESULT_CODE_FAIL, resultIntent);
//			finish();
		}else {
			resultIntent.putExtra("bike_mac",resultString);
//			setResult(RESULT_CODE_OK, resultIntent);
			userControl.toBikeMes(this,resultString);
		}
//		finish();
	}

	/**
	 * 初始化camera
	 * @param surfaceHolder
     */
	private void initCamera(SurfaceHolder surfaceHolder) {
		Log.e(TAG,"----->>>.initCamera");

		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		Log.e(TAG,"----->>>.surfaceChanged");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG,"----->>>.surfaceCreated");

		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
		Log.e(TAG,"----->>>.surfaceDestroyed");

	}

	public ViewfinderView getViewfinderView() {
		Log.e(TAG,"----->>>.getViewfinderView");

		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 初始化振动
	 */
	private void initBeepSound() {
		Log.e(TAG,"----->>>.initBeepSound");

		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	/**
	 * 开始振动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
			switch (v.getId()){
				case R.id.left_back:
					finish();
					break;
				case R.id.scan_title:
					userControl.toBikeMes(this,"  ");
					break;
				default:
						break;
			}
	}
}