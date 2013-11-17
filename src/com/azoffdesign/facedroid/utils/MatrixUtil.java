package com.azoffdesign.facedroid.utils;

import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created on 11/16/13.
 */
public class MatrixUtil {

	public static Matrix newScaleMatrix(RectF src, RectF dst) {
		Matrix scale = new Matrix();
		scale.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
		return scale;
	}

}
