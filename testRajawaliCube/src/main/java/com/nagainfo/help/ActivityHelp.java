package com.nagainfo.help;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailLoader.OnThumbnailLoadedListener;
import com.google.android.youtube.player.YouTubeThumbnailView.OnInitializedListener;
import com.nagainfo.smartShowroom.R;
//import com.nagainfo.smartShowroom.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ActivityHelp extends Activity implements OnInitializedListener {

	private ImageView iThumbnail;
	private static String KEY_VIDEO_ID = "BzdOPh9mDOc";
	private YouTubeThumbnailView thumbView;
	private YouTubeThumbnailLoader thumbnailLoader;
	public static final String DEVELOPER_KEY = "AIzaSyAXiUzmdfFqUEolECMH60bx1_P0jIdtXKI";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		/*
		 * try { thumbView = new YouTubeThumbnailView(this);
		 * thumbView.initialize(DEVELOPER_KEY, this);
		 * 
		 * } catch (Exception e) {
		 * 
		 * }
		 */

		/*
		 * iThumbnail = (ImageView) findViewById(R.id.thumbnail);
		 * iThumbnail.setTag(KEY_VIDEO_ID); iThumbnail.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub startYoutube(KEY_VIDEO_ID);
		 * 
		 * } });
		 */

	}

	void startYoutube(String VIDEO_ID) {
		Intent intent;
		intent = YouTubeIntents.createPlayVideoIntentWithOptions(this,
				VIDEO_ID, true, false);
		startActivity(intent);
	}

	@Override
	public void onInitializationFailure(YouTubeThumbnailView arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitializationSuccess(YouTubeThumbnailView arg0,
			YouTubeThumbnailLoader thumbnailLoader) {
		this.thumbnailLoader = thumbnailLoader;
		// TODO Auto-generated method stub
		thumbnailLoader
				.setOnThumbnailLoadedListener(new OnThumbnailLoadedListener() {

					@Override
					public void onThumbnailLoaded(YouTubeThumbnailView arg0,
							String arg1) {
						// TODO Auto-generated method stub
						iThumbnail.setImageDrawable(arg0.getDrawable());

					}

					@Override
					public void onThumbnailError(YouTubeThumbnailView arg0,
							ErrorReason arg1) {
						// TODO Auto-generated method stub

					}
				});
		thumbnailLoader.setVideo(KEY_VIDEO_ID);
	}

}
