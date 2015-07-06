package com.nagainfo.smartShowroom;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

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

public class RoomRenderer2 extends RajawaliRenderer implements
		OnObjectPickedListener {
	float a, b, height, wallC, wallD;
	Camera cam;
	float scale;
	Context context;
	private Object3D mObjectGroup, mObjectGroup2;
	private ObjectColorPicker mPicker;
	private ObjectColorPicker mObjectPicker;
	private Object3D mSelectedObject;
	private Object3D picked3DObject;
	private Bitmap lastScreenshot;
	private boolean screenshot;
	private Object3D group = new Object3D();

	public RoomRenderer2(Context context, int a, int b, int height, int c, int d) {
		super(context);
		this.context = context;
		scale = 1000;
		cam = getCurrentCamera();
		cam.setZ(1);
		setFrameRate(30);
		cam.setFarPlane(100000000);
		cam.setNearPlane(10);
		cam.setFieldOfView(90);

		/*
		 * this.length=length; this.height=height; this.width=width;
		 */
		/*
		 * this.a = a; this.height = height; this.b = b;
		 */

		this.a = 1;
		this.height = 1;
		this.b = 1;

		float r1 = (float) c / (float) a;
		float r2 = (float) d / (float) b;

		this.wallC = r1;
		this.wallD = r2;
		// int

	}

	/*
	 * @Override public void
	 * onDrawFrame(javax.microedition.khronos.opengles.GL10 glUnused) {
	 * 
	 * }
	 */
	public void takeScreenshot() {
		screenshot = true;
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
			MediaScannerConnection.scanFile(context, new String[] { file.getPath() }, new String[] { "image/png" }, null);

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
				saveScreenShot(lastScreenshot);

				screenshot = false;
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

		}
	}

	Object3D get3DObject() {
		return group;
	}

	double calculateAngle() {
		float adj = a - wallC;
		float opp = b - wallD;

		double angleDeg = Math.toDegrees(Math.atan2(opp, adj));
		return angleDeg;

	}

	double calculateLength() {
		float opp = b - wallD;
		double angleRad = Math.toRadians(calculateAngle());
		double sinAng = Math.sin(angleRad);
		double length = opp / sinAng;
		Log.i("length", length + "");

		return length;
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

				material.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}

			float frontLength = wallC;
			// front
			Plane plane = addWall(material, frontLength * scale, height * 1.0f
					* scale, Axis.Z);
			// plane.setScale(scale);
			plane.setX(((1 - wallC) / 2) * scale);
			plane.setZ(-0.5 * scale);
			plane.setName("front");
			plane.setRotY(180);
			group.addChild(plane);

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

			plane = addWall(material1, a * 1.0f * scale, height * scale, Axis.Z);
			// plane.setScale(scale);

			// plane.setRotY(180);
			plane.setZ(+0.5 * scale);
			plane.setName("back");
			group.addChild(plane);
			if (new File(filePath).exists()) {
				// bmp.recycle();
			}

			// frontleft
			filePath = path + "frontleft" + ".png";

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
				// material.setColor(Color.BLUE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material2.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			float leftWidth = (float) calculateLength();
			plane = addWall(material2, leftWidth * scale, height * scale,
					Axis.X);
			// plane.setScale(scale);
			// plane.setScaleZ(360);
			// plane.setZ(0.5*scale);
			plane.setRotY(180);

			plane.setRotX(270);

			plane.setX(-0.5 * scale);
			plane.setName("frontleft");
			plane.setZ(-(wallD / 2) * scale);
			// plane.setZ(-(wallD/4) * scale);
			float opp = b - wallD;
			opp /= 2;
			float adj = a - wallC;
			adj /= 2;
			plane.setX(plane.getX() + (opp * scale));
			// plane.setZ(plane.getZ()-(adj* scale));

			plane.setRotZ(-calculateAngle());
			// plane.rotateAround(new Vector3(0, 0, 1), 45, true);

			group.addChild(plane);

			// left

			filePath = path + "left" + ".png";

			if (new File(filePath).exists()) {
				bmp = BitmapFactory.decodeFile(filePath);
			}
			Material material6 = new Material();
			try {
				if (new File(filePath).exists()) {
					material6.addTexture(new Texture("checkerboard", bmp));
				} else {
					material6.addTexture(new Texture("checkerboard",
							R.drawable.wall));
				}
				// material.setDiffuseMethod(new DiffuseMethod.Lambert());
				// material.setColor(Color.rgb(255, 0, 255));
				// material.setColor(Color.WHITE);
				// material.useVertexColors(true);
				// material.setVertexColors(Color.BLACK);
				material6.setColorInfluence(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			float frontleftWidth = (float) wallD;
			plane = addWall(material6, frontleftWidth * scale, height * scale,
					Axis.X);
			// plane.setScale(scale);
			// plane.setScaleZ(360);
			plane.setRotY(180);
			plane.setRotX(270);
			plane.setZ((1 - wallD) / 2 * scale);
			plane.setX(-0.5 * scale);
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

			plane = addWall(material3, b * scale, height * scale, Axis.X);
			// plane.setScale(scale);
			// plane.setRotZ(90);
			plane.setRotX(90);
			// plane.setRotY(180);

			plane.setX(0.5 * scale);
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

			plane = addWall(material4, a * scale, b * scale, Axis.Y);
			// plane.setScale(scale);
			plane.setRotY(180);
			plane.setRotX(180);
			plane.setY(0.5 * scale);
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

			plane = addWall(material5, a * scale, b * scale, Axis.Y);
			// plane.setScale(scale);
			// plane.setRotX(180);
			plane.setRotZ(180);
			plane.setRotX(180);
			plane.setY(-0.5 * scale);
			plane.setName("bottom");
			// plane.setZ(0.5*scale);
			group.addChild(plane);
			// group.setScale(scale);
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
		 * } catch (ParsingException ex) { ex.printStackTrace(); }
		 */

	}

	Plane addWall(Material material, Float width, Float height, Axis axis) {
		Plane plane = new Plane(width, height, 1, 1, axis);
		mPicker.registerObject(plane);
		plane.setMaterial(material);
		plane.setColor(Color.WHITE);
		plane.setBackSided(true);
		plane.setDoubleSided(true);
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

	public void resetSelectedObject() {
		mSelectedObject = null;
	}

	public String getWallName() {
		Plane p = (Plane) mSelectedObject;
		// Log.i("Wall name ", p.getName());
		return p.getName();
	}

	public void setWallTexture(String filepath) {
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
			//myBitmap = Bitmap.createScaledBitmap(myBitmap, 512, 512, false);
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
