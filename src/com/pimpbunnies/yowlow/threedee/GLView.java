package com.pimpbunnies.yowlow.threedee;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class GLView extends GLSurfaceView {
		
	private Context mContext;
	private OpenGLRenderer mRenderer;
	
	public void roll() {
		mRenderer.roll();
	}
	
	public GLView(Context context, Bitmap[] bitmaps) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		
		mRenderer = new OpenGLRenderer(mContext, bitmaps);
		
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setRenderer(mRenderer);
        
        setZOrderOnTop(true); 
	}
	
}
