package com.hengtan.nanodegreeapp.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;


public class MusicController extends MediaController {

	private Context mContext;

	public MusicController(Context c){
		super(c);
		this.mContext = c;
	}

	public void hide(){}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			((Activity)mContext).finish();

		return super.dispatchKeyEvent(event);
	}
}
