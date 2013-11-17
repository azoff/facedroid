package com.azoffdesign.facedroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.azoffdesign.facedroid.utils.StorageUtil;

/**
 * Created on 11/16/13.
 */
public class GetImageActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getImageFromCamera();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
				startCanvasActivity();
				break;
			default:
				getImageFromCamera();
				break;
		}
	}

	private void startCanvasActivity() {
		Intent intent = new Intent(this, CanvasActivity.class);
		intent.setDataAndType(imageUri, "image/jpeg");
		startActivity(intent);
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri = StorageUtil.getBackgroundImageUri(this));
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

}
