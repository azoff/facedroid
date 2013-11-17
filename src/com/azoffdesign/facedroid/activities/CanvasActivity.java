package com.azoffdesign.facedroid.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import com.azoffdesign.facedroid.R;
import com.azoffdesign.facedroid.tasks.DetectFaceTask;
import com.azoffdesign.facedroid.utils.ErrorUtil;
import com.azoffdesign.facedroid.views.FaceCanvasView;

/**
 * Created on 11/16/13.
 */
public class CanvasActivity extends Activity implements
		FaceCanvasView.Handler, DetectFaceTask.Callback {

	FaceCanvasView faceCanvasView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFaceCanvasView(getIntent().getData());
	}

	private void setFaceCanvasView(Uri backgroundImageUri) {
		if (backgroundImageUri == null) {
			ErrorUtil.showError(this, "Unable to read background image");
			return;
		}
		Bitmap backgroundImageBitmap = BitmapFactory.decodeFile(backgroundImageUri.getPath());
		Bitmap foregroundImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face);
		faceCanvasView = new FaceCanvasView(this, this, backgroundImageBitmap, foregroundImageBitmap);
		setContentView(faceCanvasView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		faceCanvasView.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		faceCanvasView.stop();
	}

	@Override
	public void onFaceCanvasViewError(Throwable error) {
		ErrorUtil.showError(this, error);
	}

	@Override
	public void onFaceCanvasViewResize(FaceCanvasView view) {
		detectFaces();
	}

	public void detectFaces() {
		DetectFaceTask task = new DetectFaceTask(this);
		task.execute(faceCanvasView.getScreenshot());
	}

	@Override
	public void onFaceFound(RectF face) {
		faceCanvasView.setForegroundDestRectF(face);
	}
}
