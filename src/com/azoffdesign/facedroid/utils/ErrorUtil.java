package com.azoffdesign.facedroid.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created on 11/16/13.
 */
public class ErrorUtil {

	public static void showError(Context context, String message) {
		showError(context, new RuntimeException(message));
	}

	public static void showError(Context context, Throwable t) {
		Log.e(context.getClass().getName(), t.getMessage(), t);
		Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
	}

}
