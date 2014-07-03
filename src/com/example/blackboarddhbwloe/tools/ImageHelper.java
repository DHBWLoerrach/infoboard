package com.example.blackboarddhbwloe.tools;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.blackboarddhbwloe.MainActivity;
import com.example.blackboarddhbwloe.R;
import com.example.blackboarddhbwloe.userarea.AnzeigeSchalten;
import com.example.blackboarddhbwloe.userarea.MeinBenutzerProfil;

public class ImageHelper {

	public String tag = "ImageHelper";

	Activity AnlegenActivity;

	public static final int PICK_FROM_CAMERA = 1;
	public static final int PICK_FROM_FILE = 2;

	public static Bitmap imageList;
	public static Bitmap imageDetail;
	public static String pathList;
	public static String pathDetail;

	public Uri mImageCaptureUri;
	public String sourcePath;

	public Bitmap previewImage;

	public ImageHelper(AnzeigeSchalten activity) {
		AnlegenActivity = activity;
	}

	//Zweiter construktor fuer Profilbilder einfuegen
	public ImageHelper(MeinBenutzerProfil benutzerProfil) {
		AnlegenActivity = benutzerProfil;
	}

	public void openImagePicker(View v) {

		final String[] items = new String[] { "von Kamera", "von Speicher" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(),
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

		builder.setTitle(R.string.wahle_bildquelle);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					// von Kamera
					File imageDirectory = new File(
							Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
									+ "/BulletinBoard DHBW");
					imageDirectory.mkdirs();

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file = new File(
							imageDirectory.getAbsolutePath(),
							MainActivity.USERNAME
									+ "_"
									+ String.valueOf(System.currentTimeMillis())
									+ ".jpg");
					mImageCaptureUri = Uri.fromFile(file);

					try {
						intent.putExtra(
								android.provider.MediaStore.EXTRA_OUTPUT,
								mImageCaptureUri);
						intent.putExtra("return-data", true);

						AnlegenActivity.startActivityForResult(intent,
								PICK_FROM_CAMERA);
					} catch (Exception e) {
						e.printStackTrace();
					}

					dialog.cancel();
				} else {
					// von File Picker
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					AnlegenActivity.startActivityForResult(Intent
							.createChooser(intent, "Complete action using"),
							PICK_FROM_FILE);
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(tag, "onActivityResult started");
		if (resultCode != Activity.RESULT_OK) {
			System.out.println(tag + "  No Image taken");
			return;
		}

		if (requestCode == PICK_FROM_FILE) {
			mImageCaptureUri = data.getData();
			sourcePath = getRealPathFromURI(mImageCaptureUri); // from Gallery

			if (sourcePath == null)
				sourcePath = mImageCaptureUri.getPath(); // from File Manager

		} else {
			sourcePath = mImageCaptureUri.getPath();
		}

		System.out.println(tag + "  onACtivityResult finished");

	}

	public String getRealPathFromURI(Uri contentUri) {

		System.out.println(tag + "  getRealPathFromUri started");

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = AnlegenActivity.getContentResolver().query(contentUri,
				proj, null, null, null);

		if (cursor == null)
			return null;

		int column_index_path = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		System.out.println(tag + "   getRealPathFromUri finished");

		return cursor.getString(column_index_path);
	}

	public void imageScaling() {

		System.out.println(tag + "  imageScaling started");

		final int imageSizeList = 60;
		final int imageSizeDetail_height;
		final int imageSizeDetail_width;

		final BitmapFactory.Options optOutput = new BitmapFactory.Options();
		final BitmapFactory.Options optList = new BitmapFactory.Options();
		final BitmapFactory.Options optDetail = new BitmapFactory.Options();

		optOutput.inJustDecodeBounds = true;
		
		optList.inJustDecodeBounds = false;
		optDetail.inJustDecodeBounds = false;

		try {

			Log.d(tag, "SourcePath= " + sourcePath);
			FileInputStream in = new FileInputStream(sourcePath);

			BitmapFactory.decodeStream(in, null, optOutput);

			in.close();
			
			Log.d("outHeight", optOutput.outHeight + "");
			Log.d("outWidth", optOutput.outWidth + "");

			if (optOutput.outHeight < optOutput.outWidth) {
				imageSizeDetail_height = 600;
				imageSizeDetail_width = 800;
			} else {
				imageSizeDetail_height = 800;
				imageSizeDetail_width = 600;
			}

			int height_scaleList = optOutput.outHeight / imageSizeList;
			int width_scaleList = optOutput.outWidth / imageSizeList;

			Log.d("HeightScale", height_scaleList + "");
			Log.d("WidthScale", width_scaleList + "");

			int height_scaleDetail = optOutput.outHeight / imageSizeDetail_height;
			int width_scaleDetail = optOutput.outWidth / imageSizeDetail_width;
			
			Log.d("HeightScale", height_scaleDetail + "");
			Log.d("WidthScale", width_scaleDetail + "");

			if (height_scaleList > width_scaleList) {

				optList.inSampleSize = width_scaleList;

			} else {
				optList.inSampleSize = height_scaleList;
			}

			Log.d("inSampleSize", optList.inSampleSize + "");
			
			FileInputStream inList = new FileInputStream(sourcePath);
			imageList = BitmapFactory.decodeStream(inList, null, optList);
			inList.close();
			
			if (height_scaleDetail > width_scaleDetail) {

				optDetail.inSampleSize = width_scaleDetail;

			} else {
				optDetail.inSampleSize = height_scaleDetail;
			}
			
			Log.d(tag, "DetailScale: "+ optDetail.inSampleSize);
					
			FileInputStream inDetail = new FileInputStream(sourcePath);		
			imageDetail = BitmapFactory.decodeStream(inDetail, null, optDetail);
			inDetail.close();
			
			previewImage = imageDetail;
			
			in.close();

		} catch (Exception e) {
			Log.d(tag, "Image Convert failed: " + e);
		}

	}

}
