package com.azoffdesign.facedroid.tasks;

import android.graphics.*;
import android.media.FaceDetector;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created on 11/16/13.
 */
public class DetectFaceTask extends AsyncTask<Bitmap, Void, Collection<RectF>> {

	private static final int MAX_FACES = 1;

	private final Callback callback;

	public static interface Callback {
		public void onFaceFound(RectF face);
	}

	public DetectFaceTask(Callback callback) {
		this.callback = callback;
	}

	@Override
	protected void onPostExecute(Collection<RectF> faces) {
		super.onPostExecute(faces);
		for (RectF face : faces)
			callback.onFaceFound(face);
	}

	@Override
	protected void onCancelled(Collection<RectF> faces) {
		super.onCancelled(faces);
		for (RectF face : faces)
			callback.onFaceFound(face);
	}

	@Override
	protected Collection<RectF> doInBackground(Bitmap... bitmaps) {
		Collection<RectF> faces = new ArrayList<RectF>();
		for (Bitmap bitmap : bitmaps)
			faces.addAll(detectFaces(bitmap));
		return faces;
	}

	private Collection<RectF> detectFaces(Bitmap bitmap) {

		FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
		FaceDetector detector = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACES);
		int faceCount = detector.findFaces(bitmap, faces);
		Collection<RectF> rects = new ArrayList<RectF>();
		for (int i=0; i<faceCount; i++) {
			FaceDetector.Face face = faces[i];
			PointF center = new PointF();
			face.getMidPoint(center);
			float radius = face.eyesDistance()*1.5f;
			float left   = center.x - radius;
			float top    = center.y - radius;
			float right  = center.x + radius;
			float bottom = center.y + radius;
			RectF rect = new RectF(left, top, right, bottom);
			rects.add(rect);
		}

		return rects;

	}



}
