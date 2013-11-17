package com.azoffdesign.facedroid.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created on 11/16/13.
 */
public class StorageUtil {

	public static File getBackgroundImageFile(Context context) {
		File pictureDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		if (pictureDir == null)
			throw new RuntimeException("Unable to open pictures directory");
		if (!pictureDir.exists() && !pictureDir.mkdirs())
			throw new RuntimeException("Unable to create pictures directory");
		return new File(pictureDir.getPath() + File.separator + "background.jpg");
	}

	public static Uri getBackgroundImageUri(Context context) {
		return Uri.fromFile(getBackgroundImageFile(context));
	}

}
