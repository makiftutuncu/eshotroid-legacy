package com.mehmetakiftutuncu.eshotroid.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mehmetakiftutuncu.eshotroid.BuildConfig;
import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.activity.Main;
import com.mehmetakiftutuncu.eshotroid.fragment.KentKartBalanceFragment;
import com.mehmetakiftutuncu.eshotroid.model.KentKart;
import com.mehmetakiftutuncu.eshotroid.model.KentKartBalanceQueryResult;
import com.mehmetakiftutuncu.eshotroid.utility.Connection;
import com.mehmetakiftutuncu.eshotroid.utility.Constants;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;
import com.mehmetakiftutuncu.eshotroid.utility.Parser;

/** An asynchronous task for querying Kent Kart balance
 * 
 * @author mehmetakiftutuncu */
public class QueryKentKartBalanceTask extends AsyncTask<Void, Void, Void>
{
	/** Kent Kart to query*/
	private KentKart myKentKart;
	
	/** {@link KentKartBalanceFragment} object to update UI with the results */
	private KentKartBalanceFragment myFragment;
	
	/** URL to send requests for querying Kent Kart balance */
	private final static String URL = "http://m.kentkart.com/kws.php";
	/** User agent value that will be added to the request headers */
	private final static String[] USER_AGENTS = new String[]
	{
		"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36",
		"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36",
		"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:24.0) Gecko/20100101 Firefox/24.0 Waterfox/24.0",
		"Mozilla/5.0 (Linux; U; Android 4.0.3; de-ch; HTC Sensation Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
	};
	
	/** Tag for debugging */
	private static final String LOG_TAG = "Eshotroid_QueryKentKartBalanceTask";
	
	/** Constructor for querying and updating UI afterwards
	 *
	 * @param fragment {@link QueryKentKartBalanceTask#myFragment}
	 * @param kentKart {@link QueryKentKartBalanceTask#myKentKart} */
	public QueryKentKartBalanceTask(KentKartBalanceFragment fragment, KentKart kentKart)
	{
		myFragment = fragment;
		myKentKart = kentKart;
	}
	
	@Override
	protected Void doInBackground(Void... params)
	{
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "Querying Kent Kart balance for " +
				myKentKart + "...");
		
		// If there is no internet connection
		if(!Connection.isNetworkAvailable(myFragment.getActivity()))
		{
			Log.e(LOG_TAG, "No network connection!");
			
			Messages.getInstance().showNegative(myFragment.getActivity(),
					myFragment.getString(R.string.error_noConnection));
			
			return null;
		}
		
		try
		{
			// Post a message to the UI thread to notify user that refreshing started
			new Handler(myFragment.getActivity().getMainLooper()).post(new Runnable()
			{
				@Override
				public void run()
				{
					// Show the progress in Kent Kart page and disable query button
					((Main) myFragment.getActivity()).toggleMode(true, Constants.PAGE_ID_KENT_KART_BALANCE);
					myFragment.setQuerying(true);
				}
			});
			
			Messages.getInstance().showNeutral(myFragment.getActivity(),
					myFragment.getString(R.string.info_kentKart_refreshing,
							myKentKart.toString()));
			
			// Get resulting page with balance information
			String resultingPage = sendRequest();
			
			// Parse the resulting page to generate balance information
			final KentKartBalanceQueryResult result = Parser.parseKentKartBalance(resultingPage);
			
			// Show result
			new Handler(Looper.getMainLooper()).post(new Runnable()
			{
				@Override
				public void run()
				{
					if(result != null)
					{
						Messages.getInstance().showPositive(myFragment.getActivity(),
								myFragment.getString(R.string.info_kentKart_successful));
						
						myFragment.showQueryResult(result);
					}
					else
					{
						Messages.getInstance().showNegative(myFragment.getActivity(),
								myFragment.getString(R.string.info_kentKart_invalid));
					}
				}
			});
		}
		catch(ClientProtocolException e)
		{
			Log.e(LOG_TAG, "Error occurred while querying Kent Kart balance!", e);
			
			Messages.getInstance().showNegative(myFragment.getActivity(),
					myFragment.getString(R.string.error_noConnection));
		}
		catch(IOException e)
		{
			Log.e(LOG_TAG, "Error occurred while querying Kent Kart balance!", e);
			
			Messages.getInstance().showNegative(myFragment.getActivity(),
					myFragment.getString(R.string.error_parse));
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error occurred while querying Kent Kart balance!", e);
			
			Messages.getInstance().showNegative(myFragment.getActivity(),
					myFragment.getString(R.string.error_unknown));
		}
		finally
		{
			// Post a message to the UI thread to notify user that refreshing started
			new Handler(myFragment.getActivity().getMainLooper()).post(new Runnable()
			{
				@Override
				public void run()
				{
					// Hide the progress in Kent Kart page and enable query button
					((Main) myFragment.getActivity()).toggleMode(false, Constants.PAGE_ID_KENT_KART_BALANCE);
					myFragment.setQuerying(false);
				}
			});
		}
		
		return null;
	}
	
	/** Sends a POST request to get balance information page of the Kent Kart
	 * whose alias number is given
	 * 
	 * @return Balance information page of the Kent Kart whose alias number is
	 * given, or null if an error occurs
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String sendRequest() throws ClientProtocolException, IOException
	{
		// Create a client, a post request and parameters array list
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		ArrayList<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		
		// Set request information
		httpPost.setHeader("User-Agent", USER_AGENTS[new Random().nextInt(USER_AGENTS.length)]);
		parameters.add(new BasicNameValuePair("func", "bs"));
		parameters.add(new BasicNameValuePair("myregion", "006")); // 006 is for İzmir
		parameters.add(new BasicNameValuePair("myregiontitle", "IZMIR"));
		parameters.add(new BasicNameValuePair("val", myKentKart.getAliasNo1() + myKentKart.getAliasNo2()));
		httpPost.setEntity(new UrlEncodedFormEntity(parameters));
		
		// Execute the request
		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		// If response is OK
		if(httpResponse.getStatusLine().getStatusCode() == 200)
		{
			// Get response content
			HttpEntity httpEntity = httpResponse.getEntity();
			
			// Convert the entity (the resulting page) to string and return it
			return EntityUtils.toString(httpEntity, "UTF-8");
		}
		
		return null;
	}
}