package com.nagainfomob.smartShowroom;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.RajawaliActivity;

import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.primitives.ScreenQuad;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.GLU;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;

public class RoomRenderer extends RajawaliRenderer implements
		OnObjectPickedListener {
	float length, width, height;
	Camera cam;
	float scale;
	Context context;
	private Object3D mObjectGroup, mObjectGroup2;
	private ObjectColorPicker mPicker;
	private ObjectColorPicker mObjectPicker;
	private Object3D mSelectedObject;
	private Object3D picked3DObject;
	private boolean screenshot, freezeshot;
	private Bitmap lastScreenshot;
	Object3D group = new Object3D();
	String freezeshotpath, freezeshotpath1;
	private Context context1;

	public RoomRenderer(Context context, float length, float width, float height) {
		super(context);
		this.context = context;
		scale = 1000;
		// scale = 10;

		cam = getCurrentCamera();
		cam.setZ(1);
		setFrameRate(30);
		cam.setFarPlane(100000000);
		// cam.setFarPlane(1000);

		cam.setNearPlane(1);
		cam.setFieldOfView(90);

		// this.length=length; this.height=height; this.width=width;

		this.length = length/height;
		this.height = 1;
		this.width = width/height;
		// int

	}

	ArrayList<Vector3> getVertices() {
		ArrayList<Vector3> vertices = new ArrayList<Vector3>();
		for (int i = 0; i < group.getNumChildren(); i++) {
			vertices.add(group.getChildAt(i).getPosition());
		}
		return vertices;
	}

	public void takeScreenshot() {
		screenshot = true;
	}

	ScreenshotListener screnShotListener;
	int screnshotPosition = 0;

	public void takefreezeshot(String path, String path1, Context context,
			int view, ScreenshotListener listener) {
		screnshotPosition = view;
		// getSurfaceView().getDrawingCache();
		Log.i("render", "start take");
		screnShotListener = listener;
		freezeshot = true;
		freezeshotpath = path;
		freezeshotpath1 = path1;
		this.context1 = context;
	}

	private void savefreezeshot(Bitmap bmp) {
		File directoryPath = new File(freezeshotpath1);
		if (!directoryPath.exists()) {
			directoryPath.mkdirs();
		}

		String filePath1 = freezeshotpath + ".png";
		try {
			File file = new File(filePath1);
			FileOutputStream fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
			// Toast.makeText(context1, "SAVED", 1000).show();
			MediaScannerConnection.scanFile(context,
					new String[] { file.getPath() },
					new String[] { "image/png" }, null);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(null, "Save file error!");
		}
		Log.i("render", "complete take");
		if (screnShotListener != null) {
		
			switch (screnshotPosition) {
			case 1:
				screnShotListener.onFrontshotTaken();
				break;
			case 2:
				screnShotListener.onBackshotTaken();
				break;
			case 3:
				screnShotListener.onLeftshotTaken();
				break;
			case 4:
				screnShotListener.onRightshotTaken();
				break;
			default:
				break;
			}

		}
	}

	private String saveScreenShot(Bitmap bmp) {
		String path;
		String currentProject = GlobalVariables.getProjectName();

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/ScreenShots/" + currentProject + "/";
		File directoryPath = new File(path);
		if (!directoryPath.exists()) {
			directoryPath.mkdirs();
		}

		String fileName = currentProject + "_" + System.currentTimeMillis();
		String filePath = path + fileName + ".png";
		try {
			File file = new File(filePath);
			FileOutputStream fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
			MediaScannerConnection.scanFile(context,
					new String[] { file.getPath() },
					new String[] { "image/png" }, null);

		} catch (Exception e) {
			e.printStackTrace();
			Log.i(null, "Save file error!");
		}
		// Toast.makeText(getApplicationContext(), "asd",
		// Toast.LENGTH_SHORT).show();

		return filePath;
	}

	/*
	 * @Override public void
	 * onDrawFrame(javax.microedition.khronos.opengles.GL10 glUnused) {
	 * 
	 * }
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		try {
			super.onDrawFrame(gl);
			if (screenshot) {
				int screenshotSize = mViewportWidth * mViewportHeight;
				ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
				bb.order(ByteOrder.nativeOrder());
				gl.glReadPixels(0, 0, mViewportWidth, mViewportHeight,
						GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
				int pixelsBuffer[] = new int[screenshotSize];
				bb.asIntBuffer().get(pixelsBuffer);
				bb = null;
				Bitmap bitmap = Bitmap.createBitmap(mViewportWidth,
						mViewportHeight, Bitmap.Config.RGB_565);
				bitmap.setPixels(pixelsBuffer, screenshotSize - mViewportWidth,
						-mViewportWidth, 0, 0, mViewportWidth, mViewportHeight);
				pixelsBuffer = null;

				short sBuffer[] = new short[screenshotSize];
				ShortBuffer sb = ShortBuffer.wrap(sBuffer);
				bitmap.copyPixelsToBuffer(sb);

				// Making created bitmap (from OpenGL points) compatible with
				// Android bitmap
				for (int i = 0; i < screenshotSize; ++i) {
					short v = sBuffer[i];
					sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
				}
				sb.rewind();
				bitmap.copyPixelsFromBuffer(sb);
				lastScreenshot = bitmap;
				// saveScreenShot(lastScreenshot);
				saveScreenShot(lastScreenshot);
				screenshot = false;
			}
			if (freezeshot) {
				int screenshotSize = mViewportWidth * mViewportHeight;
				ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
				bb.order(ByteOrder.nativeOrder());
				gl.glReadPixels(0, 0, mViewportWidth, mViewportHeight,
						GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
				int pixelsBuffer[] = new int[screenshotSize];
				bb.asIntBuffer().get(pixelsBuffer);
				bb = null;
				Bitmap bitmap = Bitmap.createBitmap(mViewportWidth,
						mViewportHeight, Bitmap.Config.RGB_565);
				bitmap.setPixels(pixelsBuffer, screenshotSize - mViewportWidth,
						-mViewportWidth, 0, 0, mViewportWidth, mViewportHeight);
				pixelsBuffer = null;

				short sBuffer[] = new short[screenshotSize];
				ShortBuffer sb = ShortBuffer.wrap(sBuffer);
				bitmap.copyPixelsToBuffer(sb);

				// Making created bitmap (from OpenGL points) compatible with
				// Android bitmap
				for (int i = 0; i < screenshotSize; ++i) {
					short v = sBuffer[i];
					sBuffer[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
				}
				sb.rewind();
				bitmap.copyPixelsFromBuffer(sb);
				lastScreenshot = bitmap;
				// saveScreenShot(lastScreenshot);
				savefreezeshot(lastScreenshot);
				freezeshot = false;
			}
		} catch (Exception e) {
			/*
			 * activity.runOnUiThread(new Runnable() { public void run() {
			 * Toast.makeText(activity, "Download completed!",
			 * Toast.LENGTH_SHORT).show();
			 * 
			 * } });
			 */
			// Toast.makeText(context,
			// "Insufficient Memory.Please reload the project!",
			// Toast.LENGTH_LONG).show();
			// ((RajawaliActivity)(getContext().getApplicationContext())).finish();
			e.printStackTrace();
		}
	}

	public void initScene() {
		super.initScene();
		DirectionalLight spotLight = new DirectionalLight(0f * scale,
				0f * scale, 1f * scale);
		spotLight.setPower(2 * scale);
		mPicker = new ObjectColorPicker(this);
		mObjectPicker = new ObjectColorPicker(this);
		mObjectPicker.setOnObjectPickedListener(new OnObjectPickedListener() {

			@Override
			public void onObjectPicked(Object3D object) {
				// TODO Auto-generated method stub
				picked3DObject = object;
				Log.i("Picked Object", object.getName());
			}
		});
		mPicker.setOnObjectPickedListener(this);

		getCurrentScene().addLight(spotLight);

		String path;
		String currentProject = GlobalVariables.getProjectName();

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/" + currentProject + "/";

		String filePath;// = path + fileName + ".png";

		// getCurrentScene().setUsesCoverageAa(true);
		{
			// front
			filePath = path + "front" + ".png";
			Bitmap bmp = null;
			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material = new Material();
			try {
				if (new File(filePath).exists()) {
					material.addTexture(new Texture("checkerboard", bmp));
				} else {
					material.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// front
			Plane plane = addWall(material, length * scale, height * scale,
					Axis.Z);
			// plane.setScale(scale);
			plane.setZ(-0.5 * scale * width);
			plane.setName("front");
			plane.setRotY(180);
			group.addChild(plane);
			Log.i("vertex:" + plane.getName(), plane.getWorldPosition()
					.toString());
			if (new File(filePath).exists()) {
				// bmp.recycle();
			}

			// back

			filePath = path + "back" + ".png";
			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material1 = new Material();
			try {
				if (new File(filePath).exists()) {
					material1.addTexture(new Texture("checkerboard", bmp));
				} else {
					material1.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material1.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			plane = addWall(material1, length * scale, height * scale, Axis.Z);
			// plane.setScale(scale);

			// plane.setRotY(180);
			plane.setZ(+0.5 * scale * width);
			plane.setName("back");
			group.addChild(plane);
			if (new File(filePath).exists()) {
				// bmp.recycle();
			}

			// left

			filePath = path + "left" + ".png";

			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material2 = new Material();
			try {
				if (new File(filePath).exists()) {
					material2.addTexture(new Texture("checkerboard", bmp));
				} else {
					material2.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material2.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			plane = addWall(material2, width * scale, height * scale, Axis.X);
			// plane.setScale(scale);
			// plane.setScaleZ(360);
			plane.setRotY(180);
			plane.setRotX(270);
			plane.setX(-0.5 * scale * length);
			plane.setName("left");
			// plane.setZ(0.5*scale);
			group.addChild(plane);

			// right

			filePath = path + "right" + ".png";

			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material3 = new Material();
			try {
				if (new File(filePath).exists()) {
					material3.addTexture(new Texture("checkerboard", bmp));
				} else {
					material3.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material3.setColorInfluence(0);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			plane = addWall(material3, width * scale, height * scale, Axis.X);
			// plane.setScale(scale);
			// plane.setRotZ(90);
			plane.setRotX(90);
			// plane.setRotY(180);
//			plane.setBackSided(false);
//			plane.setBlendingEnabled(true);
//			plane.setTransparent(true);
//			plane.setVisible(false);
			plane.setX(0.5 * scale * length);
			plane.setName("right");

			// plane.setZ(0.5*scale);
			group.addChild(plane);

			// Top

			filePath = path + "top" + ".png";

			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material4 = new Material();
			try {
				if (new File(filePath).exists()) {
					material4.addTexture(new Texture("checkerboard", bmp));
				} else {
					material4.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				
				material4.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			plane = addWall(material4, length * scale, width * scale, Axis.Y);
			// plane.setScale(scale);
			plane.setRotY(180);
			plane.setRotX(180);
			plane.setY(0.5 * scale * height);
			plane.setName("top");
			// plane.setZ(0.5*scale);
			group.addChild(plane);

			// Bottom

			filePath = path + "bottom" + ".png";

			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material5 = new Material();
			try {
				if (new File(filePath).exists()) {
					material5.addTexture(new Texture("checkerboard", bmp));
				} else {
					material5.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material5.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			plane = addWall(material5, length * scale, width * scale, Axis.Y);
			// plane.setScale(scale);
			// plane.setRotX(180);
			plane.setRotZ(180);
			plane.setRotX(180);
			plane.setY(-0.5 * scale * height);
			plane.setName("bottom");
			// plane.setZ(0.5*scale);
			group.addChild(plane);
			// group.setScreenCoordinates(0, 0, mViewportWidth, mViewportHeight,
			// 2);
			addChild(group);
			
		}
		
		/*
		 * LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(),
		 * mTextureManager, R.raw.clothes_shop_mirror); try { objParser.parse();
		 * mObjectGroup = objParser.getParsedObject();
		 * mObjectGroup.setName("Mirror");
		 * mObjectPicker.registerObject(mObjectGroup);
		 * mObjectGroup.setScale(45); //mObjectGroup.setLight(spotLight);
		 * addChild(mObjectGroup); mObjectGroup.setColor(Color.BLACK);
		 * mObjectGroup.setPosition(0, -0.5*scale, -0.25*scale);
		 * 
		 * objParser = new LoaderOBJ(mContext.getResources(), mTextureManager,
		 * R.raw.light_switch); objParser.parse(); mObjectGroup2 =
		 * objParser.getParsedObject(); mObjectGroup2.setScale(100);
		 * mObjectGroup2.setColor(Color.BLUE); mObjectGroup2.setRotY(90);
		 * mObjectGroup2.setPosition(0.5*scale,0, 0); addChild(mObjectGroup2);
		 * 
		 * objParser = new LoaderOBJ(mContext.getResources(), mTextureManager,
		 * R.raw.faucet_obj); objParser.parse(); mObjectGroup2 =
		 * objParser.getParsedObject(); mObjectGroup2.setScale(30);
		 * mObjectGroup2.setColor(Color.BLUE); mObjectGroup2.setRotY(90);
		 * mObjectGroup2.setPosition(0,0, 0); addChild(mObjectGroup2);
		 * 
		 * } catch (ParsingException ex) { ex.printStackTrace(); }
		 */
		
		
	}
	
	boolean collision()
	{
		IBoundingVolume vol1=cam.getTransformedBoundingVolume();
		IBoundingVolume vol2=group.getGeometry().getBoundingBox();
//		vol1.transform(cam.getRotationMatrix());
//		vol2.transform(group.getModelMatrix());
//		boolean dash=vol1.intersectsWith(vol1);
		return true;
	}

	Plane addWall(Material material, Float width, Float height, Axis axis) {
		Plane plane = new Plane(width, height, 1, 1, axis);
		mPicker.registerObject(plane);
		plane.setMaterial(material);
		plane.setColor(Color.WHITE);
		plane.setBackSided(true);
//		plane.setBlendingEnabled(false);
		plane.setDoubleSided(true);
//		plane.setTransparent(true);
		plane.setColor(0xff0000ff);
		
		

		return plane;
	}

	public void get3DObjectAt(float x, float y) {
		mObjectPicker.getObjectAt(x, y);
		// mPicker.getObjectAt(x, y);
	}

	public void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	public void onObjectPicked(Object3D object) {
		mSelectedObject = object;

		// mSelectedObject.setColor(0x00000000);
	}

	public Object3D getPicked3D() {
		return picked3DObject;
	}

	public Plane getPickedObject() {
		return (Plane) mSelectedObject;
	}

	public String getWallName() {
		Plane p = (Plane) mSelectedObject;
		// Log.i("Wall name ", p.getName());
		return p.getName();
	}

	public void resetSelectedObject() {
		mSelectedObject = null;
	}

	public void setWallTexture(String filepath) {
		try {
			File imgFile = new File(filepath);
			if (imgFile.exists()) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
						.getAbsolutePath());// storage/emulated/0/SmartShowRoom/hdhs/front.png
				// Bitmap myBitmap =
				// BitmapFactory.decodeFile("/storage/emulated/0/SmartShowRoom/hdhs/front.png");
				int imgW = myBitmap.getWidth();
				int imgH = myBitmap.getHeight();

				Log.i("Width", imgW + "");
				Log.i("Height", imgH + "");
				// myBitmap = Bitmap.createScaledBitmap(myBitmap, 512, 512,
				// false);
				// Drawable d = new BitmapDrawable(getResources(), myBitmap);
				Texture customTexture = new Texture("customWall", myBitmap);
				Material material = new Material();
				try {
					material.addTexture(customTexture);
					material.setColorInfluence(0);
				} catch (TextureException e) {
					e.printStackTrace();
				}
				Plane p = (Plane) mSelectedObject;

				p.setMaterial(material);
				mSelectedObject = null;
			}
		} catch (Exception ex) {

		}

	}

	public void stopMovingSelectedObject() {

		Material material = new Material();
		try {
			material.addTexture(new Texture("checkerboard", R.drawable.wall));
			material.setColorInfluence(0);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		Plane p = (Plane) mSelectedObject;

		p.setMaterial(material);
		stopRendering();
		// mSelectedObject = null;
	}

}
