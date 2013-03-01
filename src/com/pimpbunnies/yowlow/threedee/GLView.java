package com.pimpbunnies.yowlow.threedee;

import com.pimpbunnies.yowlow.views.ShuffleCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class GLView extends GLSurfaceView {
		
	private Context mContext;
	private OpenGLRenderer mRenderer;
	
	public void shuffle(ShuffleCallback cb) {
		mRenderer.shuffle(cb);
	}
	
	public void setCube(Cube cube) {
		mRenderer.setCube(cube);
	}
		
	public GLView(Context context, Cube cube) {
		super(context);
		mContext = context;
		
		mRenderer = new OpenGLRenderer(mContext, cube);
		
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        this.setRenderer(mRenderer);
        
        setZOrderOnTop(true); 
	}
	
}
