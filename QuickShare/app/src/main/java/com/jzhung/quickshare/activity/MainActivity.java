package com.jzhung.quickshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jzhung.quickshare.R;
import com.jzhung.quickshare.model.httpserver.HttpServer;
import com.jzhung.quickshare.model.httpserver.ServerConfig;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
            final String localIp = getLocalHostIp();
            final int port = 8889;
            //启动http服务
            Toast.makeText(MainActivity.this, "服务已启动，请访问http://" + localIp + ":" + port, Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerConfig serverConfig = new ServerConfig(localIp, port, "/mnt/sdcard2/");
                    HttpServer httpServer = HttpServer.getInstance(serverConfig);
                    httpServer.start();
                }
            }).start();

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

    /**
     * 得到本机ip地址
     * @return
     */
    public String getLocalHostIp(){
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()){
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())){
                        ipaddress = ip.getHostAddress();
                    }
                }
            }
        }catch (SocketException e){
            Log.e(TAG, "获取本地ip地址失败");
            e.printStackTrace();
        }
        Log.i(TAG, "本机IP:"+ ipaddress);
        return ipaddress;

    }
}
