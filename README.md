maple_plugin
============
A image selector plugin
------------
It is display on the windows view, especially it will be display above all the views.
If you want to use it, just write the code like MainActivity.


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
