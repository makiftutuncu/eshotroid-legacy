package com.mehmetakiftutuncu.eshotroid.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mehmetakiftutuncu.eshotroid.R;

/**
 * An asynchronous task for getting a page
 * 
 * @author Mehmet Akif Tütüncü
 */
public class GetPageTask extends AsyncTask<String, Integer, String>
{
	private ProgressDialog myDialog;
	private Context myContext;
	
	public GetPageTask(Context context)
	{
		myContext = context;
	}
	
	@Override
	protected void onPreExecute()
	{
		myDialog = ProgressDialog.show(myContext, myContext.getString(R.string.pleaseWait), myContext.getString(R.string.connecting));
	}
	
	@Override
	protected String doInBackground(String... params)
	{
		String result = Connection.getPage(myContext, params[0]);
		return result;
	}
	
	@Override
	protected void onPostExecute(String result)
	{
		myDialog.dismiss();
	}
}