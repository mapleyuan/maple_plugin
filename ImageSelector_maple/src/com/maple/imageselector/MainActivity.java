package com.maple.imageselector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.maple.imageselector.ImagePickCustom.ImagePickSelectListener;

/**
 * @author yuanweinan
 * 
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		View view = findViewById(R.id.main_button);

		final ImageView image = (ImageView) findViewById(R.id.main_image);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ImagePickCustom.show(getApplicationContext(),
						new ImagePickSelectListener() {

							@Override
							public void selectedImage(String path) {
								// TODO Auto-generated method stub
								Log.i("maple", path);
								if (path != null) {

									Bitmap bitmap = BitmapFactory
											.decodeFile(path);
									image.setImageBitmap(bitmap);
								}
							}
						});
			}
		});
	}
}
