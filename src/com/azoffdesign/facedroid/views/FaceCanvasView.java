package com.azoffdesign.facedroid.views;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import com.azoffdesign.facedroid.utils.MatrixUtil;

/**
 * Created on 11/16/13.
 */
public class FaceCanvasView extends SurfaceView implements
		SurfaceHolder.Callback, Runnable, View.OnTouchListener {

	Thread thread;
	SurfaceHolder surfaceHolder;
	Bitmap backgroundImage;
	Bitmap foregroundImage;
	RectF canvasRectF;
	RectF backgroundSourceRectF;
	RectF foregroundSourceRectF;
	RectF foregroundDestRectF;
	Handler handler;

	volatile boolean dirty = true;
	volatile boolean resized = false;
	volatile boolean running = false;

	public static interface Handler {
		public void onFaceCanvasViewError(Throwable error);
		public void onFaceCanvasViewResize(FaceCanvasView view);
	}

	public FaceCanvasView(Context context, Handler handler) {
		super(context);
		setDrawingCacheEnabled(true);
		setHandler(handler);
		setOnTouchListener(this);
		setMatchParentLayout();
		setCallback();
	}

	public FaceCanvasView(Context context, Handler handler, Bitmap backgroundImage, Bitmap foregroundImage) {
		this(context, handler);
		setBackgroundImage(backgroundImage);
		setForegroundImage(foregroundImage);
	}

	public void setMatchParentLayout() {
		setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
		));
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setBackgroundImage(Bitmap backgroundImage) {
		this.backgroundImage = backgroundImage;
		this.calcBackgroundSourceRectF();
		dirty = true;
	}

	private void calcBackgroundSourceRectF() {
		this.backgroundSourceRectF = new RectF(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
		dirty = true;
	}

	public void setForegroundImage(Bitmap foregroundImage) {
		this.foregroundImage = foregroundImage;
		this.foregroundSourceRectF = new RectF(0, 0, foregroundImage.getWidth(), foregroundImage.getHeight());
		dirty = true;
	}

	public void setForegroundDestRectF(RectF foregroundDestRectF) {
		this.foregroundDestRectF = foregroundDestRectF;
		dirty = true;
	}

	private void setCanvasSize(float width, float height) {
		canvasRectF = new RectF(0, 0, width, height);
		resized = true;
		dirty = true;
	}

	private void setCallback() {
		surfaceHolder = getHolder();
		if (surfaceHolder == null) {
			handler.onFaceCanvasViewError(new RuntimeException("Unable to get surface holder"));
			return;
		}
		surfaceHolder.addCallback(this);
	}

	private boolean translateForeground(float x, float y) {
		if (foregroundDestRectF == null)
			return false;
		float width  = foregroundDestRectF.width();
		float height = foregroundDestRectF.height();
		float left   = x - (width/2);
		float top    = y - (height/2);
		foregroundDestRectF = new RectF(left, top, left + width, top + height);
		dirty = true;
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return translateForeground(event.getX(), event.getY());
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		dirty = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setCanvasSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public Bitmap getScreenshot() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawBitmaps(canvas);
		return bitmap;
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop(){
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			handler.onFaceCanvasViewError(e);
			running = true;
			stop();
		}
	}

	@Override
	public void run() {

		while (running) {

			if (!dirty)
				continue;

			if (!surfaceHolder.getSurface().isValid())
				continue;

			Canvas canvas = surfaceHolder.lockCanvas();
			if (canvas == null)
				continue;

			drawBitmaps(canvas);

			surfaceHolder.unlockCanvasAndPost(canvas);

			dirty = false;

			if (resized) {
				handler.onFaceCanvasViewResize(this);
				resized = false;
			}

		}

	}

	public void drawBitmaps(Canvas canvas) {

		if (canvasRectF == null) return;
		canvas.drawColor(Color.BLACK);

		Paint ditherPaint = new Paint();
		ditherPaint.setDither(true);

		Matrix scaleBG = MatrixUtil.newScaleMatrix(backgroundSourceRectF, canvasRectF);
		canvas.drawBitmap(backgroundImage, scaleBG, ditherPaint);

		if (foregroundDestRectF != null) {
			Matrix scaleFG = MatrixUtil.newScaleMatrix(foregroundSourceRectF, foregroundDestRectF);
			canvas.drawBitmap(foregroundImage, scaleFG, ditherPaint);
		}

	}

}
