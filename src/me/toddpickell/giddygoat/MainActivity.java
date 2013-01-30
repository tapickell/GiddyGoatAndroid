package me.toddpickell.giddygoat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.zbar.Symbol;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button punch;
	private TextView textMarque;
	private TextView punchCount;
	public static final String CARD_FILENAME = "punch_card";
	private OnSharedPreferenceChangeListener listener; 
	private String specials = "* * *  Daily Specials  * * *";
	private SharedPreferences punchCard;
	private ShareActionProvider mShareActionProvider;
	private String code = "2a73e02a88ee9bcb965cc0f22c0cabbf68d5e823992884b4514bc242b0146ff16d5cf349c374cf7c";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		punch = (Button) findViewById(R.id.button1);
		textMarque = (TextView) findViewById(R.id.mytextview);
		punchCount = (TextView) findViewById(R.id.textView1);
		textMarque.setText(specials);
		
		punchCard = getSharedPreferences(CARD_FILENAME, MODE_PRIVATE);
		Log.d("PUNCH_CARD", Integer.toString(punchCard.getInt("punches", 69)));
		if (punchCard.contains("punches")) {
			//card file already in place
			//can I just set text view registered for changes??
			//or do I update Int for text view then register??
			//Android documentation is clear as mud :(
			Log.d("PUNCH_CARD", "punch card has punches field");
			Log.d("PUNCH_CARD", Integer.toString(punchCard.getInt("punches", 69)));
			punchCount.setText(Integer.toString(punchCard.getInt("punches", 50)));
			
		} else {
			//card file doesnt contain punches need to start as new card
			//not sure if this wiil run on first run or not????
			//documentation not very specific as to how to create a shared pref file.
			Log.d("PUNCH_CARD", "added new punches field to shared pref file");
			SharedPreferences.Editor editor = punchCard.edit();
			editor.putInt("punches", 0);
			editor.commit();
			// #### maybe throw this onto other thread for performance ####
		}
		Log.d("PUNCH_CARD", Integer.toString(punchCard.getInt("punches", 69)));
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				// TODO Auto-generated method stub
				Log.d("SHARED_PREF", key.toString());
				punchCount.setText(Integer.toString(punchCard.getInt("punches", 50)));
			}
		};
		
		punchCard.registerOnSharedPreferenceChangeListener(listener);
		
		String status = null;

		// grab async thread to download twitter feed from ### could have method
		// to store and check dates on data for freshness like ios app ###
		new tweetFeed().execute(status);
		
		//check string
		// when finished kick back to main thread to update string for ui

		punch.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// display scan view for punch
				AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
						.create();
				alert.setTitle("Hand Phone to Barista");
				alert.setMessage("Please hand your phone to your Giddy Goat Barista. They will add a punch to your card for you.");
				alert.setButton("Continue",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// goto scan view for scanning qr code for punch
								Log.d("UI_PRESSED", "ContinueButtonPressed");
								int ZBAR_SCANNER_REQUEST = 64;
								Intent intent = new Intent(MainActivity.this, ZBarScannerActivity.class);
								intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
								startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

							
							}
						});
				alert.setButton2("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing here!

							}
						});
				alert.show();
			}
		});

	}
	


	@SuppressWarnings("deprecation")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		if (resultCode == RESULT_OK) {
			// handle scan result
			String contents = data.getStringExtra(ZBarConstants.SCAN_RESULT);
			Toast.makeText(this, "Scan Result = " + contents, Toast.LENGTH_SHORT).show();
	        
			Log.d("SCANNER", contents);
			if (contents.equals(code)) {
				Log.d("SCANNER", "Scan == code");
				Integer temp = punchCard.getInt("punches", 50);
				if (temp >= 0 && temp < 10) {
					temp++;
				} else {
					temp = 0;
				}
				SharedPreferences.Editor editor = punchCard.edit();
				editor.putInt("punches", temp);
				editor.commit();
				
			} else {
				Log.d("SCANNER", "Scan != code");
			}
		}
		// else continue with any other code you need in the method
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		// #### should we add social integration options to menu for cleaner ui?
		// ####
		MenuItem item = menu.findItem(R.id.menu_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	
	public class tweetFeed extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.d("ASYNCTASK", "doInBackground");
			return getTweet();
		}
		
		protected void onPostExecute(String result) {
			if (result != null) {
				Log.d("ASYNCTASK", "PostExecute: " + result);
				String post = specials + "      ..." + result;
				textMarque.setText(post);
				
			}
		}
		
		private String getTweet() {
			Log.d("ASYNCTASK", "startGetTweet");
			String status = null;
			try {
				// http request to get status ####
				// https://api.twitter.com/1/statuses/user_timeline.json?screen_name=giddygoatupdate&count=1
				URL url = new URL("https://api.twitter.com/1/statuses/user_timeline.json?screen_name=giddygoatupdate&count=1");
				Reader tweetReader = new InputStreamReader(url.openStream());
				
				// json parsing to get just status ####
				// "text":"Testing 1,2,3....come on app blackboard!", ####
				JsonReader tweetJsonReader = new JsonReader(tweetReader);
				tweetJsonReader.beginArray();//crash!!!
				tweetJsonReader.beginObject();
				
				while (tweetJsonReader.hasNext()) {
					String name = tweetJsonReader.nextName();
					if (name.equals("text")) {
						status = tweetJsonReader.nextString();
					} else {
						tweetJsonReader.skipValue();
					}
				}
				tweetJsonReader.close();
				
			} catch (MalformedURLException e) {
				Log.d("TWITTER_FEED", e.toString());
			} catch (IOException e) {
				Log.d("TWITTER_FEED", e.toString());
			}

			// return string
			return status;
		}
	}
}
