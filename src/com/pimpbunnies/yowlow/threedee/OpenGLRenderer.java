package com.pimpbunnies.yowlow.threedee;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;


public class OpenGLRenderer implements Renderer {
	
	/** Cube instance */
	private Cube cube;	
	
	/* Rotation values for all axis */
	private float xrot;				//X Rotation ( NEW )
	private float yrot;				//Y Rotation ( NEW )
	private float zrot;				//Z Rotation ( NEW )
	
	/** The Activity Context ( NEW ) */
	private Context context;
	
	private float mSpeed = 0.0f;
	
	/**
	 * Instance the Cube object and set 
	 * the Activity Context handed over
	 */
	public OpenGLRenderer(Context context, Bitmap[] bitmaps) {
		this.context = context;
		
		cube = new Cube(bitmaps);
	}
	
	public void roll() {
		mSpeed = 12.9f;
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {		
		//Load the texture for the cube once during Surface creation
		cube.loadGLTexture(gl, this.context);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		//Drawing
		gl.glTranslatef(0.0f, 0.0f, -5.0f);		//Move 5 units into the screen
		gl.glScalef(1.1f, 1.1f, 1.1f); 			//Scale the Cube to 80 percent, otherwise it would be too large for the screen
		
		//Rotate around the axis based on the rotation matrix (rotation, x, y, z)
		gl.glRotatef(xrot, 0.0f, 1.0f, 0.0f);	//X
		gl.glRotatef(yrot, 1.0f, 1.0f, 1.0f);	//Y
		gl.glRotatef(zrot, 0.0f, 1.0f, 0.0f);	//Z
				
		cube.draw(gl);							//Draw the Cube	
		
		//Change rotation factors (nice rotation)
		
		mSpeed = mSpeed - 0.1f;
		if (mSpeed >= 0) {
			xrot += mSpeed;
			yrot += mSpeed;
			zrot += mSpeed;
		}
	}

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
}