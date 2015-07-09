package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.MediaController;


public class MusicController extends MediaController {

	private Context mContext;
	private boolean mTwoPane;

	public MusicController(Context c, boolean twoPane){
		super(c);
		this.mContext = c;
		this.mTwoPane = twoPane;
	}

	public void hide(){}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			((Activity) mContext).finish();

			if(!mTwoPane && PlayerUtil.isLastActivity(PlayerActivity.class.getName()))
			{
				Intent intent = new Intent(mContext, MainActivity.class);
				mContext.startActivity(intent);
			}
		}

		return super.dispatchKeyEvent(event);
	}
}
