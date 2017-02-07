package com.joshua.a51bike.activity.view;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.joshua.a51bike.R;
import com.joshua.a51bike.activity.control.UserControl;
import com.joshua.a51bike.activity.core.BaseActivity;
import com.joshua.a51bike.zxing.camera.CameraManager;
import com.joshua.a51bike.zxing.decoding.CaptureActivityHandler;
import com.joshua.a51bike.zxing.decoding.InactivityTimer;
import com.joshua.a51bike.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;


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
	private UserControl userControl;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		initTextView();
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		findViewById(R.id.left_back).setOnClickListener(this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private void initTextView() {
		Button btn_title= (Button) findViewById(R.id.btn_title);
		switch (getIntent().getIntExtra("comeFrom",FETCH_PACKAGE_CODE)){
			case FETCH_PACKAGE_CODE:
				btn_title.setText("取件扫码");
				break;
			case DELIVER_PACKAGE_CODE:
				btn_title.setText("派件扫码");
				break;
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
		Log.e(TAG,"----->>>handleDecode result is "+result.getText());
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		resultIntent = new Intent();
		userControl = UserControl.getUserControl();
		if (resultString.equals("")) {
//			setResult(RESULT_CODE_FAIL, resultIntent);
			finish();
		}else {
			resultIntent.putExtra("url",resultString);
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
				default:
						break;
			}
	}
}