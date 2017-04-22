package com.gizwits.opensource.appkit.ControlModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.CommonModule.GosDeploy;
import com.gizwits.opensource.appkit.ConfigModule.GosConfigCountdownActivity;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.utils.HexStrUtils;
import com.gizwits.opensource.appkit.view.HexWatcher;

import static com.baidu.android.pushservice.PushManager.handler;
import static com.gizwits.opensource.appkit.R.id.action_getHardwareInfo;
import static com.gizwits.opensource.appkit.R.id.action_getStatu;

public class GosDeviceControlActivity extends GosControlModuleBaseActivity
		implements OnClickListener, OnEditorActionListener, OnSeekBarChangeListener, View.OnTouchListener {

	/** 设备列表传入的设备变量 */
	private GizWifiDevice mDevice;

	private EditText et_extend_text;
	/**定义按钮*/
	private Button auto_btn;
	private Button strong_btn;
	private Button bow_btn;
	private Button border_btn;
	private Button change_btn;
	private Button left_btn;
	private Button right_btn;
	private Button forword_btn;
	private Button forback_btn;
	private Button play_btn;
	private Button setTime_btn;
	private Button back_btn;
	private Button menu_btn;
	/**定时器*/
	private Timer mTimer;
	/**获取系统时间*/
	SimpleDateFormat sdf;
	Calendar calender;

	private  int status_count = 1;

	/**电量显示*/
	private TextView battery_level;
	private TextView controllMode;
	private byte battery_temp;

	touchSend send1;
	touchSend send2;
	touchSend send3;
	touchSend send4;


	private enum handler_key {

		/** 更新界面 */
		UPDATE_UI,

		DISCONNECT,
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			if (isDeviceCanBeControlled()) {
				progressDialog.cancel();
			} else {
				toastDeviceNoReadyAndExit();
			}
		}

	};

	/** The handler. */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handler_key key = handler_key.values()[msg.what];
			switch (key) {
			case UPDATE_UI:
				updateUI();
				break;
			case DISCONNECT:
				toastDeviceDisconnectAndExit();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);
		initDevice();
		setActionBar(true, true, getDeviceName());
		initView();
		initEvent();
		if(actionBar!=null){
			actionBar.hide();
		}
		sendSystemTime();
	}
	private void initView() {

		et_extend_text = (EditText) findViewById(R.id.et_extend_text);
	}

	private void initEvent() {

		et_extend_text.setOnEditorActionListener(this);
		et_extend_text.addTextChangedListener(new HexWatcher(et_extend_text));
		auto_btn = (Button)findViewById(R.id.auto_btn);
		strong_btn = (Button)findViewById(R.id.strong_btn);
		bow_btn = (Button)findViewById(R.id.bow_btn);
		border_btn = (Button)findViewById(R.id.border_btn);
		change_btn = (Button)findViewById(R.id.change_btn);
		left_btn = (Button)findViewById(R.id.left_btn);
		right_btn = (Button)findViewById(R.id.right_btn);
		forword_btn = (Button)findViewById(R.id.forword_btn);
		forback_btn = (Button)findViewById(R.id.back_btn);
		play_btn = (Button)findViewById(R.id.play_btn);
		setTime_btn = (Button)findViewById(R.id.play_btn);
		back_btn = (Button)findViewById(R.id.title_back_btn);
		menu_btn = (Button)findViewById(R.id.title_select_btn);
		auto_btn.setOnClickListener(this);
		strong_btn.setOnClickListener(this);
		bow_btn.setOnClickListener(this);
		border_btn.setOnClickListener(this);
		change_btn.setOnClickListener(this);
		left_btn.setOnClickListener(this);
		right_btn.setOnClickListener(this);
		forword_btn.setOnClickListener(this);
		forback_btn.setOnClickListener(this);
		play_btn.setOnClickListener(this);
		setTime_btn.setOnClickListener(this);
		back_btn.setOnClickListener(this);
		menu_btn.setOnClickListener(this);
		left_btn.setOnTouchListener(this);
		right_btn.setOnTouchListener(this);
		forword_btn.setOnTouchListener(this);
		forback_btn.setOnTouchListener(this);
		battery_level = (TextView)findViewById(R.id.batteryLevel);
		controllMode = (TextView)findViewById(R.id.controllMode);
		send1 = new touchSend();
		send2 = new touchSend();
		send3 = new touchSend();
		send4 = new touchSend();
//		byte initCMD[] = {(byte) 0xfe,0x55,0x55,0x04,0x03};
//		ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
//		hashMap.put("command", initCMD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
			case 1:
				if(resultCode == RESULT_OK){
					byte[] returnData = data.getByteArrayExtra("extra_data");
					byte cmd = 0x04;
					protocolHandle(returnData,cmd);
				}
		}
	}

	private void initDevice() {
		Intent intent = getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(gizWifiDeviceListener);
	}

	/**定时器处理事件*/
	/*-----------------------------------------------start-----------------------------------------------------*/
	private void setTimerTask(){
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				doActionHandler.sendMessage(message);

			}
		},2000,300000/**设置定时发送时间，以毫秒算*/);
	}
	private Handler doActionHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId){
				case 1:
					byte cmd3 = 0x03;
					byte[] b = {0x00};
					protocolHandle(b, cmd3);

					break;
				default:
					break;
			}
		}
	};
	/*------------------------------------------------end--------------------------------------------------------*/

	/**发送系统时间*/
	/**-------------------------------------------------start---------------------------------------------------------*/
	private void sendSystemTime(){
		new Thread(new Runnable(){

			public void run(){

				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Message msg = new Message();
				msg.arg1 = 1;
				handler.sendMessage(msg); //告诉主线程执行任务

			}

		}).start();
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			calender = Calendar.getInstance();
			sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			//执行动作
			String rt = sdf.format(calender.getTime());
			//String created = calender.get(Calendar.YEAR) + "年";
			byte cmd9 = 0x05;
			byte value[] = rt.getBytes();
			protocolHandle(value,cmd9);
		}
	};
	/**-----------------------------------------------------------end-------------------------------------------------------*/

	private String getDeviceName() {
		if (TextUtils.isEmpty(mDevice.getAlias())) {
			return mDevice.getProductName();
		}
		return mDevice.getAlias();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getStatusOfDevice();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mRunnable);
		// 退出页面，取消设备订阅
		mDevice.setSubscribe(false);
		mDevice.setListener(null);

		//定时器注销
		//mTimer.cancel();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		byte cmd = 0x01;
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			switch (v.getId()){
				case R.id.left_btn:
					byte[] input7 = {0x06};
					if(send1.isRunning()){
						break;
					}
					send1.interrupt();
					send1 = new touchSend();
					send1.setting(input7, cmd,left_btn);
					send1.start();
					break;
				case R.id.right_btn:
					byte[] input8 = {0x07};
					if(send2.isRunning()){
						break;
					}
					send2.interrupt();
					send2 = new touchSend();
					send2.setting(input8, cmd,right_btn);
					send2.start();
					break;
				case R.id.forword_btn:
					byte[] input9 = {0x08};
					if(send3.isRunning()){
						break;
					}
					send3.interrupt();
					send3 = new touchSend();
					send3.setting(input9, cmd,forword_btn);
					send3.start();
					break;
				case R.id.back_btn:
					byte[] input10 = {0x09};
					if(send4.isRunning()){
						break;
					}
					send4.interrupt();
					send4 = new touchSend();
					send4.setting(input10, cmd,forback_btn);
					send4.start();
					break;
			}
		}
		return false;
	}
	/**
	 * 清扫模式开关状态控制
	 * @param button
     */
	private void buttonStatus(int button){
		auto_btn.setSelected(false);
		strong_btn.setSelected(false);
		bow_btn.setSelected(false);
		border_btn.setSelected(false);
		change_btn.setSelected(false);

		auto_btn.setEnabled(true);
		strong_btn.setEnabled(true);
		bow_btn.setEnabled(true);
		border_btn.setEnabled(true);
		change_btn.setEnabled(true);
		switch (button){
			case 1:
				auto_btn.setEnabled(false);
				auto_btn.setSelected(true);
				controllMode.setText(R.string.control_auto);
				break;
			case 2:
				strong_btn.setEnabled(false);
				strong_btn.setSelected(true);
				controllMode.setText(R.string.control_strong);
				break;
			case 3:
				bow_btn.setEnabled(false);
				bow_btn.setSelected(true);
				controllMode.setText(R.string.control_bow);
				break;
			case 4:
				border_btn.setEnabled(false);
				border_btn.setSelected(true);
				controllMode.setText(R.string.control_border);
				break;
			case 5:
				change_btn.setEnabled(false);
				change_btn.setSelected(true);
				controllMode.setText(R.string.control_charge);
				break;
			default:
				break;
		}
	}
	@Override
	public void onClick(View v) {//按钮控制事件
		byte cmd = 0x01;
		byte[] input= {0x22};
		switch (v.getId()) {
			case R.id.auto_btn:
				byte[] imput1 = {0x01};
				protocolHandle(imput1,cmd);
				buttonStatus(1);
				break;

			case R.id.strong_btn:
				byte[] input2 = {0x04};
				protocolHandle(input2,cmd);
				buttonStatus(2);
				break;

            case R.id.bow_btn:
                byte[] input3 = {0x02};
                protocolHandle(input3,cmd);
				buttonStatus(3);
				break;

			case R.id.border_btn:
				byte[] input4 = {0x03};
				protocolHandle(input4,cmd);
				buttonStatus(4);
				break;

			case  R.id.change_btn:
				byte[] input5 = {0x00};
				protocolHandle(input5,cmd);
				buttonStatus(5);
				break;

			case R.id.play_btn:
				byte[] input6 = {0x05};
				if(play_btn.isSelected()){
					play_btn.setSelected(false);
				}else{
					play_btn.setSelected(true);
				}
				protocolHandle(input6,cmd);
				break;
			case R.id.title_back_btn:
				finish();
				break;
			case R.id.title_select_btn:
				openOptionsMenu();
//                PopupMenu popup = new PopupMenu(this, menu_btn);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater()
//                        .inflate(R.menu.menu_test_popul, popup.getMenu());
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        return true;
//                    }
//                });
//                popup.show(); //showing popup menu
				break;
		default:
			break;
		}
	}

	/*
         * ========================================================================
         * EditText 点击键盘“完成”按钮方法
         * ========================================================================
         */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		switch (v.getId()) {
		case R.id.et_extend_text:
//			sendCommand(KEY_TEXT, HexStrUtils.hexStringToBytes(v.getText().toString().replaceAll(" ", "")));
			break;
		default:
			break;
		}
		hideKeyBoard();
		return false;

	}

	/*
	 * ========================================================================
	 * seekbar 回调方法重写
	 * ========================================================================
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		switch (seekBar.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		switch (seekBar.getId()) {
		default:
			break;
		}
	}

	/*
	 * ========================================================================
	 * 菜单栏
	 * ========================================================================
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.device_more, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		byte cmd = 0x00;
		switch (item.getItemId()) {

		case R.id.action_setDeviceInfo://退出
			Activitycollect.finishAll();
			break;

		case action_getHardwareInfo://设置定时清扫
			Intent intent = new Intent(GosDeviceControlActivity.this,TimerSet.class);
			startActivityForResult(intent,1);
			break;

		case action_getStatu://获取设备状态
			cmd = 0x03;
			byte[] data = {0x00};
			protocolHandle(data,cmd);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Description:根据保存的的数据点的值来更新UI
	 */
	protected void updateUI() {
		if(battery_temp != batteryLevel && batteryLevel != 0){
			battery_level.setText( String.valueOf(batteryLevel)+"%");
			battery_temp = batteryLevel;
		}

		switch (buttonStatus){
			case 0: buttonStatus(1);break;
			case 1: buttonStatus(2);break;
			case 2: buttonStatus(3);break;
			case 3: buttonStatus(4);break;
			case 4: buttonStatus(5);break;
			case 5:
				play_btn.setSelected(true);
				controllMode.setText("遥控");
				break;
			case 10:
				play_btn.setSelected(false);
				controllMode.setText("遥控");
				break;
			default: break;
		}
//		setEditText(et_extend_text, HexStrUtils.bytesToHexString(text));
	}

	private void setEditText(EditText et, Object value) {
		et.setText(value.toString());
		et.setSelection(value.toString().length());
		et.clearFocus();
	}

	/**
	 * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
	 */
	private void getStatusOfDevice() {
		// 设备是否可控
		if (isDeviceCanBeControlled()) {
			// 可控则查询当前设备状态
			mDevice.getDeviceStatus();
		} else {
			// 显示等待栏
			progressDialog.show();
			if (mDevice.isLAN()) {
				// 小循环10s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 10000);
			} else {
				// 大循环20s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 20000);
			}
		}
	}


    /**
     * 数据发送函数
     * @param value ：数值byte类型
     * @param cmd   ：命令byte类型
     */
	public void protocolHandle(byte[] value,byte cmd){
		byte[] protocol = new byte[40] ;
		int j = 5;
		protocol[0] = (byte) 0xFE;
		protocol[1] = 0;
		protocol[2] = (byte) ((value.length+3)>>8);
		protocol[3] = (byte)(value.length+3);
		protocol[4] = cmd;
		for(int i = 0;i<value.length;i++){
			protocol[j] = value[i];
			j++;
		}
		protocol[value.length+6] = 1;
		sendCommand("command",protocol);
	}
	/**
	 * 发送指令,下发单个数据点的命令可以用这个方法
	 *
	 * <h3>注意</h3>
	 * <p>
	 * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
	 * </p>
	 *
	 * @param key
	 *            数据点对应的标识名
	 * @param value
	 *            需要改变的值
	 */
	private void sendCommand(String key, Object value) {
		if (value == null) {
			return;
		}
		int sn = 5;
		ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
		hashMap.put(key, value);
		// 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
		// hashMap.put(key2, value2);
		// hashMap.put(key3, value3);
		mDevice.write(hashMap, sn);
		Log.i("liang", "下发命令：" + hashMap.toString());
	}

	private boolean isDeviceCanBeControlled() {
		return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
	}

	private void toastDeviceNoReadyAndExit() {
		Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
		finish();
	}

	private void toastDeviceDisconnectAndExit() {
		Toast.makeText(GosDeviceControlActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
		finish();
	}

	/**
	 * 展示设备硬件信息
	 *
	 * @param hardwareInfo
	 */
	private void showHardwareInfo(String hardwareInfo) {
		String hardwareInfoTitle = "设备硬件信息";
		new AlertDialog.Builder(this).setTitle(hardwareInfoTitle).setMessage(hardwareInfo)
				.setPositiveButton(R.string.besure, null).show();
	}

	/**
	 * Description:设置设备别名与备注
	 */
	private void setDeviceInfo() {

		final Dialog mDialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
		mDialog.show();

		Window window = mDialog.getWindow();
		window.setContentView(R.layout.alert_gos_set_device_info);

		final EditText etAlias;
		final EditText etRemark;
		etAlias = (EditText) window.findViewById(R.id.etAlias);
		etRemark = (EditText) window.findViewById(R.id.etRemark);

		LinearLayout llNo, llSure;
		llNo = (LinearLayout) window.findViewById(R.id.llNo);
		llSure = (LinearLayout) window.findViewById(R.id.llSure);

		if (!TextUtils.isEmpty(mDevice.getAlias())) {
			setEditText(etAlias, mDevice.getAlias());
		}
		if (!TextUtils.isEmpty(mDevice.getRemark())) {
			setEditText(etRemark, mDevice.getRemark());
		}

		llNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		llSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(etRemark.getText().toString())
						&& TextUtils.isEmpty(etAlias.getText().toString())) {
					myToast("请输入设备别名或备注！");
					return;
				}
				mDevice.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
				mDialog.dismiss();
				String loadingText = (String) getText(R.string.loadingtext);
				progressDialog.setMessage(loadingText);
				progressDialog.show();
			}
		});

		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				hideKeyBoard();
			}
		});
	}

	/*
	 * 获取设备硬件信息回调
	 */
	@Override
	protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, String> hardwareInfo) {
		super.didGetHardwareInfo(result, device, hardwareInfo);
		StringBuffer sb = new StringBuffer();
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
			myToast("获取设备硬件信息失败：" + result.name());
		} else {
			sb.append("Wifi Hardware Version:" + hardwareInfo.get(WIFI_HARDVER_KEY) + "\r\n");
			sb.append("Wifi Software Version:" + hardwareInfo.get(WIFI_SOFTVER_KEY) + "\r\n");
			sb.append("MCU Hardware Version:" + hardwareInfo.get(MCU_HARDVER_KEY) + "\r\n");
			sb.append("MCU Software Version:" + hardwareInfo.get(MCU_SOFTVER_KEY) + "\r\n");
			sb.append("Wifi Firmware Id:" + hardwareInfo.get(WIFI_FIRMWAREID_KEY) + "\r\n");
			sb.append("Wifi Firmware Version:" + hardwareInfo.get(WIFI_FIRMWAREVER_KEY) + "\r\n");
			sb.append("Product Key:" + "\r\n" + hardwareInfo.get(PRODUCT_KEY) + "\r\n");

			// 设备属性
			sb.append("Device ID:" + "\r\n" + mDevice.getDid() + "\r\n");
			sb.append("Device IP:" + mDevice.getIPAddress() + "\r\n");
			sb.append("Device MAC:" + mDevice.getMacAddress() + "\r\n");
		}
		showHardwareInfo(sb.toString());
	}

	/*
	 * 设置设备别名和备注回调
	 */
	@Override
	protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
		super.didSetCustomInfo(result, device);
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
			myToast("设置成功");
			progressDialog.cancel();
			finish();
		} else {
			myToast("设置失败：" + result.name());
		}
	}

	/*
	 * 设备状态改变回调，只有设备状态为可控才可以下发控制命令
	 */
	@Override
	protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
		super.didUpdateNetStatus(device, netStatus);
		if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
			mHandler.removeCallbacks(mRunnable);
			progressDialog.cancel();
			if(status_count ==1){
				//定时器初始化
				mTimer = new Timer();
				//开始定时器
				setTimerTask();
			}

		} else {
			mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
		}
	}

	/*
	 * 设备上报数据回调，此回调包括设备主动上报数据、下发控制命令成功后设备返回ACK
	 */
	@Override
	protected void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, Object> dataMap, int sn) {
		super.didReceiveData(result, device, dataMap, sn);
		Log.i("liang", "接收到数据");
		if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
			getDataFromReceiveDataMap(dataMap);
			mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
		}
	}
	//按键长按连发相关延时线程
	class touchSend extends Thread{
		private byte[] buff;
		private  byte cmd_tmp;
		private Button button_btn;
		private boolean is_running;
		private touchSend(){

			is_running = false;
		}

		public void setting(byte[] data,byte cmd,Button button){
			buff = data;
			cmd_tmp = cmd;
			button_btn = button;
		}

		public boolean isRunning(){
			return is_running;
		}

		public void run(){
			is_running = true;
			while(true){
				protocolHandle(buff, cmd_tmp);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				if(!button_btn.isPressed()){
					break;
				}
			}
			is_running = false;
		}
	}

}