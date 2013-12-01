package com.mehmetakiftutuncu.eshotroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.utility.Connection;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;

/** Welcome activity of the application which is shown on the first launch
 * 
 * @author mehmetakiftutuncu */
public class Welcome extends ActionBarActivity
{
	private Button next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().hide();
		
		setContentView(R.layout.activity_welcome);
		
		next = (Button) findViewById(R.id.button_activityWelcome_start);
		
		next.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!Connection.isNetworkAvailable(Welcome.this))
				{
					Messages.getInstance().showNegative(Welcome.this, getString(R.string.error_setupWizard_noBusses));
					
					return;
				}
				
				finish();
				startActivity(new Intent(Welcome.this, SetupWizard.class));
			}
		});
	}
}