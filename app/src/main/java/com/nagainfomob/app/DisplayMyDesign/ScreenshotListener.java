package com.nagainfomob.app.DisplayMyDesign;

public interface ScreenshotListener {
	public static int VIEW_FRONT = 1;
	public static int VIEW_BACK = 2;
	public static int VIEW_LEFT = 3;
	public static int VIEW_RIGHT = 4;

	public void onFrontshotTaken();

	public void onBackshotTaken();

	public void onLeftshotTaken();

	public void onRightshotTaken();

}
