package com.ford.openxc.camera;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import com.ford.openxc.cameraioio.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

public class ServoControlActivity extends IOIOActivity {
	private final int PAN_PIN = 5;
	private final int TILT_PIN = 6;

	private final int PWM_FREQ = 100;
	private static final String TAG = "turet is moving!!!!!!!!!";
	private SeekBar mPanSeekBar;
	private SeekBar mTiltSeekBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPanSeekBar = (SeekBar) findViewById(R.id.panSeekBar);
		mTiltSeekBar = (SeekBar) findViewById(R.id.tiltSeekBar);

		enableUi(false);
	}

	class Looper extends BaseIOIOLooper {
		private PwmOutput panPwmOutput;
		private PwmOutput tiltPwmOutput;

		// Opening the pins 3-6 in open drain mode with chosen PWM frequency
		public void setup() throws ConnectionLostException {

			try {
				panPwmOutput = ioio_.openPwmOutput(PAN_PIN, PWM_FREQ);
				tiltPwmOutput = ioio_.openPwmOutput(TILT_PIN, PWM_FREQ);

				enableUi(true);
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}

		public void loop() throws ConnectionLostException {
			try {
				panPwmOutput.setPulseWidth(500 + mPanSeekBar.getProgress() * 2);

				tiltPwmOutput
						.setPulseWidth(500 + mTiltSeekBar.getProgress() * 2);
				Thread.sleep(20);
			} catch (InterruptedException e) {
				ioio_.disconnect();
			} catch (ConnectionLostException e) {
				enableUi(false);
				throw e;
			}
		}
	}

	// creates a loop instance
	@Override
	protected IOIOLooper createIOIOLooper() {

		return new Looper();

	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPanSeekBar.setEnabled(enable);
				mTiltSeekBar.setEnabled(enable);
			}
		});
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/**
		 * the following switch statement will execute based on chosen optio and
		 * will trigger the appropriate intents
		 */

		case R.id.action_bluetooth:
			startActivityForResult(new Intent(
					android.provider.Settings.ACTION_BLUETOOTH_SETTINGS), 0);
			Toast.makeText(this, "Manage bluetooth connections",
					Toast.LENGTH_SHORT).show();

			break;
		default:

			break;
		}
		return true;
	}
}