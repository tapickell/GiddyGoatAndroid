package me.toddpickell.giddygoat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button punch;
	TextView textMarque;
	String specials = "*** Daily Specials ***";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		punch = (Button) findViewById(R.id.button1);
		textMarque = (TextView) findViewById(R.id.mytextview);
		textMarque.setText(specials);
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
		// add method to get twitter status

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		// add credits option to menu
		// #### should we add social integration options to menu for cleaner ui?
		// ####

		return true;
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
