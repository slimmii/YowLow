package com.pimpbunnies.yowlow.threedee;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;
import javax.microedition.khronos.opengles.GL11;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.pimpbunnies.yowlow.views.ShuffleCallback;

public class Cube {

	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	/** The buffer holding the texture coordinates */
	private FloatBuffer textureBuffer;
	/** The buffer holding the indices */
	private ByteBuffer[] indexBuffer = new ByteBuffer[6];

	private ShuffleCallback mCb;

	/** Our texture pointer */
	private int[] textures = new int[6];

	/**
	 * The initial vertex definition
	 * 
	 * Note that each face is defined, even if indices are available, because of
	 * the texturing we want to achieve
	 */
	private float vertices[] = {
			// Vertices according to faces
			-1.0f,
			-1.0f,
			1.0f, // Vertex 0
			1.0f,
			-1.0f,
			1.0f, // v1
			-1.0f,
			1.0f,
			1.0f, // v2
			1.0f,
			1.0f,
			1.0f, // v3

			1.0f,
			-1.0f,
			1.0f, // ...
			1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,

			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f,

			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, 1.0f,

			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f,

			-1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, };

	/** The initial texture coordinates (u, v) */
	private float texture[] = {
			// Mapping coordinates for the vertices
			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

			0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,

	};

	private Matrix normal;

	/** The initial indices definition */
	private byte indices[][] = {
			// Faces definition
			{ 0, 1, 3, 0, 3, 2 }, // Face front
			{ 4, 5, 7, 4, 7, 6 }, { 8, 9, 11, 8, 11, 10 },
			{ 12, 13, 15, 12, 15, 14 }, { 16, 17, 19, 16, 19, 18 },
			{ 20, 21, 23, 20, 23, 22 } };
	private Bitmap[] mBitmaps;

	/**
	 * The Cube constructor.
	 * 
	 * Initiate the buffers.
	 */
	public Cube(Bitmap[] bitmaps) {
		mBitmaps = bitmaps;

		normal = new Matrix();

		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		//
		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);

		for (int i = 0; i < 6; i++) {
			indexBuffer[i] = ByteBuffer.allocateDirect(indices[i].length);
			indexBuffer[i].put(indices[i]);
			indexBuffer[i].position(0);
		}

		zRot = 0;
		
//		Matrix.setIdentityM(matrix, 0);
//		Matrix.rotateM(matrix, 0, 0, 1.0f, 0.0f, 0.0f);
//		Matrix.rotateM(matrix, 0, 0, 0.0f, 1.0f, 0.0f);
//		Matrix.rotateM(matrix, 0, zRot, 0.0f, 0.0f, 1.0f);	
//		
//		Matrix.multiplyMV(result, 0, matrix, 0, normals[0], 0);
//		normals[0] = new float[] {result[0], result[1], result[2],0.0f};
//		Matrix.multiplyMV(result, 0, matrix, 0, normals[1], 0);		
//		normals[1] = new float[] {result[0], result[1], result[2],0.0f};		
//		Matrix.multiplyMV(result, 0, matrix, 0, normals[2], 0);		
//		normals[2] = new float[] {result[0], result[1], result[2],0.0f};
	}

	public void setBitmaps(Bitmap[] bitmaps) {
		mBitmaps = bitmaps;
	}

	public boolean shuffling = false;
	private int ignores = 0;
	public boolean direction;

	public void shuffle(ShuffleCallback cb) {
		if (shuffling) {
			shuffling = false;
		} else {
			xRot = 0;
			yRot = 0;
			zRot = 0;			
			shuffling = true;
			ignores = 4 + new Random().nextInt(5);			
		}
		mCb = cb;
		direction = new Random().nextBoolean();
		ignoreForAWhile = true;
		System.out.println("RANDOOOOM: " + new Random().nextInt(10));
	}

	int xRot = 0;
	int yRot = 0;
	int zRot = 0;
	int j = 0;
	boolean ignoreForAWhile = false;
	boolean test = false;

	private static final float[] matrix = new float[16];
	private float[][] normals = new float[][] {
			{1.0f,0.0f,0.0f,0.0f},
			{0.0f,1.0f,0.0f,0.0f},
			{0.0f,0.0f,1.0f,0.0f}
	};

	private static final float[] xaxis = new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
	private static final float[] yaxis = new float[] { 0.0f, 1.0f, 0.0f, 0.0f };
	private static final float[] zaxis = new float[] { 0.0f, 0.0f, 1.0f, 0.0f };
	private float[] result = new float[4];

	public float dotProduct(float[] a, float[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	/**
	 * The object own drawing function. Called from the renderer to redraw this
	 * instance with possible changes in values.
	 * 
	 * @param gl
	 *            - The GL Context
	 */
	public synchronized void draw(GL10 gl) {
		synchronized (matrix) {
			gl.glRotatef(10, 1.0f, 0.0f, 1.0f);
			gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
			gl.glRotatef(yRot, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
			gl.glRotatef(-10, 1.0f, 0.0f, 1.0f);
			Matrix.setIdentityM(matrix, 0);
			Matrix.rotateM(matrix, 0, xRot, 1.0f, 0.0f, 0.0f);
			Matrix.rotateM(matrix, 0, yRot, 0.0f, 1.0f, 0.0f);
			Matrix.rotateM(matrix, 0, zRot, 0.0f, 0.0f, 1.0f);

			boolean rotate = true;
			boolean landed = true;
			if (shuffling) {
			for (int i = 0; i < 3; i++) {
				Matrix.multiplyMV(result, 0, matrix, 0, normals[i], 0);
				float xdot = Math.abs(dotProduct(xaxis, result));
				float ydot = Math.abs(dotProduct(yaxis, result));
				float zdot = Math.abs(dotProduct(zaxis, result));
				if (shuffling) {
					if ((xdot > 0.99 || xdot < 0.01)
							&& (ydot > 0.99 || ydot < 0.0001)
							&& (zdot > 0.99 || zdot < 0.01)) {					
					} else {				
						landed = false;
					}
				}
				System.out.println("XDOT[" + i + "]=" + xdot);
				System.out.println("YDOT[" + i + "]=" + ydot);
				System.out.println("ZDOT[" + i + "]=" + zdot);				
			}
			}

			if (shuffling) {
				if (landed && !ignoreForAWhile) {
					// We should stop, we've landed!
					System.out.println("Schtop!;");
					System.out.println(ignores);
					if (ignores == 0) {
						shuffling = false;
						rotate = false;
						mCb.shuffled();
						mCb = null;
					} else {
						direction = new Random().nextBoolean();
						ignores--;
					}
				}
				if (rotate) {
					ignoreForAWhile = false;
					if (direction) {
						xRot = xRot + 15;
					} else {
						yRot = yRot + 15;
					}
//					zRot = zRot + 10;
				}			
			}
			// Het vectorproduct moet hier 0 zijn!!!

			int[] m = new int[16];
			gl.glGetIntegerv(gl.GL_MODELVIEW, m, 0);

			// Point to our buffers
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Set the face rotation
			gl.glFrontFace(GL10.GL_CCW);

			// Enable the vertex and texture state
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

			for (int i = 0; i < 6; i++) {
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
				gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE,
						indexBuffer[i]);
			}


			
			// Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);			
		}
	}

	/**
	 * Load the textures
	 * 
	 * @param gl
	 *            - The GL Context
	 * @param context
	 *            - The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context) {

		// Generate one texture pointer...
		gl.glGenTextures(6, textures, 0);
		// ...and bind it to our array

		for (int i = 0; i < 6; i++) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);

			// Create Nearest Filtered Texture
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);

			// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_REPEAT);

			// Use the Android GLUtils to specify a two-dimensional texture
			// image
			// from our bitmap
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmaps[i], 0);

			mBitmaps[i].recycle();
		}

	}
}
