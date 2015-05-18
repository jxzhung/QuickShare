package com.jzhung.quickshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jzhung.quickshare.R;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	private RelativeLayout relativeLayout;
	private ImageView face;
	private ImageView facebg;
	private ImageButton start;
	private Animation facebgAnim;
	private Animation faceAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		relativeLayout = (RelativeLayout) findViewById(R.id.relayout_main);
		face = (ImageView) findViewById(R.id.face);
		facebg = (ImageView) findViewById(R.id.facebg);
		start = (ImageButton) findViewById(R.id.start);

		//笑脸背景动画
		facebg.setBackgroundColor(Color.argb(255, 29, 189, 91));
		facebgAnim = AnimationUtils.loadAnimation(this, R.anim.face_bg);
		facebgAnim.setAnimationListener(new FacebgAnimListener());
		facebg.startAnimation(facebgAnim);

		//笑脸动画
		faceAnim =  AnimationUtils.loadAnimation(this, R.anim.face_move);
		faceAnim.setAnimationListener(new FaceAnimListener());

		start.setOnClickListener(new StartOnClickListener());
	}

	/**
	 * 开始按钮被点击
	 */
	class StartOnClickListener implements View.OnClickListener{

		@Override
		public void onClick(View view) {
			Intent intent = new Intent(MainActivity.this, FileChooseActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 笑脸动画
	 */
	class FaceAnimListener implements Animation.AnimationListener{
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			relativeLayout.removeView(face);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}

	/**
	 * 笑脸背景动画
	 */
	class FacebgAnimListener implements Animation.AnimationListener{
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			relativeLayout.removeView(facebg);
			face.startAnimation(faceAnim);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}
}
