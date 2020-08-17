package com.developer.tapit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EnterAccountDetailsActivity extends Activity implements OnClickListener{
	

	private UserLoginTask mAuthTask = null;

	public Button logBut;
	
	// Values for Cash and User Key(taken from login) at the time of the login attempt.
	private String mCash;
	private String mID;

	// UI references.
	private EditText mCashView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();
	DocumentReference calref = db.collection("Users").document(UserKey.key);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// load previous amount withdrawn
		String prevUsername="";
		String prevPassword="";
		try{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		prevUsername=preferences.getString("u2219aAsgiLPOo","");
		prevPassword=preferences.getString("p09ki8dieik87n","");}catch(Exception e){prevUsername="";prevPassword="";}
		
		setContentView(R.layout.activity_enter_account_details);
		
		//1. Include login data if available
		mCashView = (EditText) findViewById(R.id.email);
		mCashView.setText(prevUsername);


		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(prevPassword);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.password || id == EditorInfo.IME_NULL) {
							saveCredentials();
							return true;
						}
						return false;
					}
				});

		//2. Set listener for send button
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		//when "send" button is clicked, app checks with database if account balance if sufficient
		findViewById(R.id.saveAdButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						calref.get()
								.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
									@Override
									public void onSuccess(DocumentSnapshot documentSnapshot) {
										Bank banks = documentSnapshot.toObject(Bank.class);
										int amount = banks.getAmount();

										if( Integer.parseInt((mCashView).getText().toString()) > amount){
											Context context = getApplicationContext();

											CharSequence text = "You dont have that much money in your bank account";
											int duration = Toast.LENGTH_LONG;

											Toast toast = Toast.makeText(context, text, duration);
											toast.show();
										}
										else{
											saveCredentials();
										}

									}
								});




					}
				});
		
		//3. Listener Logout Button
		logBut= (Button) findViewById(R.id.loggoButton);
		try
		{
		logBut.setOnClickListener(this);
		}
		catch(Exception e)
		{
			Log.d("Tag",e.toString());
		}
		
							
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.enter_account_details, menu);
		return true;
	}


	public void saveCredentials() {
		if (mAuthTask != null) {
			return;
		}

		//1.Resets errors
		mCashView.setError(null);
		mPasswordView.setError(null);

		//2. Values ​​at the time of the login attempt
		mCash = mCashView.getText().toString();
        String key = UserKey.key;
		mID = key;
		//set to accept int only
		boolean cancel = false;
		View focusView = null;



		//4. Check if username is in valid format
		if (TextUtils.isEmpty(mCash)) {
			mCashView.setError(getString(R.string.error_field_required));
			focusView = mCashView;
			cancel = true;
		}



		if (cancel) {
			// 5. Error discovered, no login, focus on faulty box
			focusView.requestFocus();
		} else {
			//6. Show spinner and save the data in the background
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
			
			
			//7. Save for NFC signals
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("u2219aAsgiLPOo",mCash);
			editor.putString("p09ki8dieik87n",mID);
			editor.commit();
			
		}
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	//prep for NFC scan
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// Simulate network access.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
			
			Context context = getApplicationContext();

			CharSequence text = "Place phone on scanner";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	@Override
	public void onClick(View v) {
		
			if(v==logBut)
			{
			Intent menuIntent = new Intent(this,loginPage.class);
			startActivity(menuIntent);	
			}

	}
}
