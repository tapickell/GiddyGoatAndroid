package me.toddpickell.giddygoat;


import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.*;

import com.dm.zbar.android.scanner.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Scanner extends Activity {

	Button cancel;
	TextView punches;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner_view);
		
		cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// finish();
				int ZBAR_SCANNER_REQUEST = 64;
				Intent intent = new Intent(Scanner.this, ZBarScannerActivity.class);
				intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
				startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
			}
		});
		// display number of punches in text view

		// display scanner window under frame

	}

	@SuppressWarnings("deprecation")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//
		if (resultCode == RESULT_OK) {
			// handle scan result
			Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
	        Toast.makeText(this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
//			String contents = scanResult.getContents();
//			Log.d("SCANNER", contents);
//			if (contents != null) {
//				// success popup
//				AlertDialog alert = new AlertDialog.Builder(Scanner.this).create();
//				alert.setTitle("Scan Successful");
//				alert.setButton("Continue",	new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,	int which) {
//						
//								Log.d("UI_PRESSED", "ContinueButtonPressed");
//
//							}
//						});
//			}
		}
		// else continue with any other code you need in the method
	}
}
