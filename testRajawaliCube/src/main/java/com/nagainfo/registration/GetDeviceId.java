package com.nagainfo.registration;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class GetDeviceId {

	public static Context context;

	public GetDeviceId(Context context) {
		this.context = context;
	}

	public static String deviceID = null;

	public static String getDeviceid(Context c) {
		WifiManager m_wm = (WifiManager) c
				.getSystemService(Context.WIFI_SERVICE);
		try {
			if (!m_wm.isWifiEnabled())
				m_wm.setWifiEnabled(true);

			deviceID = m_wm.getConnectionInfo().getMacAddress();
		} catch (Exception e) {
			Log.e("getDeviceId error!", e.getMessage());
		}
		return deviceID.replace(":", "");
	}

}
