package com.mehmetakiftutuncu.eshotroid.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mehmetakiftutuncu.eshotroid.R;
import com.mehmetakiftutuncu.eshotroid.database.KentKartDatabase;
import com.mehmetakiftutuncu.eshotroid.model.KentKart;
import com.mehmetakiftutuncu.eshotroid.model.KentKartBalanceQueryResult;
import com.mehmetakiftutuncu.eshotroid.task.QueryKentKartBalanceTask;
import com.mehmetakiftutuncu.eshotroid.utility.Messages;

/** Fragment of the Kent Kart balance page
 * 
 * @author mehmetakiftutuncu */
public class KentKartBalanceFragment extends Fragment
{
	private TextView info;
	
	private LinearLayout existingQueryLayout;
	private Spinner selectedKentKart;
	private ImageButton deleteKentKart;
	private TextView deleteKentKartInfo;
	private Button newQuery;
	
	private LinearLayout newQueryLayout;
	private EditText aliasNo1;
	private EditText aliasNo2;
	private EditText aliasNo3;
	private CheckBox saveStatus;
	private EditText saveName;
	private Button existingQuery;
	
	private Button query;
	
	private LinearLayout resultLayout;
	private TextView balance;
	private TextView lastLoad;
	private TextView lastUse;
	private Button goBack;
	
	private ArrayList<KentKart> kentKarts;
	
	private boolean isExistingQueryMode;
	
	private boolean isQuerying = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_kent_kart_balance, container, false);
		
		initialize(view);
		
		return view;
	}
	
	public void showQueryResult(KentKartBalanceQueryResult result)
	{
		// Clear focus of alias input to hide keyboard
		aliasNo1.clearFocus();
		
		// Show result and go back button
		showOrHideView(resultLayout, true);
		showOrHideView(goBack, true);
		
		// Hide existing and new query layouts
		showOrHideView(existingQueryLayout, false);
		showOrHideView(newQueryLayout, false);
		
		// Hide info and query button
		showOrHideView(info, false);
		showOrHideView(query, false);
		
		// Set result texts
		if(result.getBalance() != null)
			balance.setText(getString(R.string.kentKartBalance_balance, result.getBalance()));
		else
			balance.setText(getString(R.string.kentKartBalance_unknown));
		
		if(result.getLastLoadTime() != null)
		{
			if(result.getLastLoadAmount() != null)
				lastLoad.setText(getString(R.string.kentKartBalance_lastLoad, result.getLastLoadTime(), result.getLastLoadAmount()));
			else
				lastLoad.setText(getString(R.string.kentKartBalance_lastLoad_onlyTime, result.getLastLoadTime()));
		}
		else
		{
			if(result.getLastLoadAmount() != null)
				lastLoad.setText(getString(R.string.kentKartBalance_lastLoad_onlyAmount, result.getLastLoadAmount()));
			else
				lastLoad.setText(getString(R.string.kentKartBalance_unknown));
		}
		
		if(result.getLastUseTime() != null)
		{
			if(result.getLastUseAmount() != null)
				lastUse.setText(getString(R.string.kentKartBalance_lastUse, result.getLastUseTime(), result.getLastUseAmount()));
			else
				lastUse.setText(getString(R.string.kentKartBalance_lastUse_onlyTime, result.getLastUseTime()));
		}
		else
		{
			if(result.getLastUseAmount() != null)
				lastUse.setText(getString(R.string.kentKartBalance_lastUse_onlyAmount, result.getLastUseAmount()));
			else
				lastUse.setText(getString(R.string.kentKartBalance_unknown));
		}
	}
	
	public void setQuerying(boolean isQuerying)
	{
		this.isQuerying = isQuerying;
		if(query != null)
		{
			query.setEnabled(!isQuerying);
		}
		if(newQuery != null)
		{
			newQuery.setEnabled(!isQuerying);
		}
	}
	
	public boolean isQuerying()
	{
		return isQuerying;
	}
	
	/** Initializes UI components */
	private void initialize(View view)
	{
		info = (TextView) view.findViewById(R.id.textView_kentKartBalance_info);
		
		existingQueryLayout = (LinearLayout) view.findViewById(R.id.linearLayout_kentKartBalance_existingQuery);
		selectedKentKart = (Spinner) view.findViewById(R.id.spinner_kentKartBalance_selectedKentKart);
		deleteKentKart = (ImageButton) view.findViewById(R.id.imageButton_kentKartBalance_deleteKentKart);
		deleteKentKartInfo = (TextView) view.findViewById(R.id.textView_kentKartBalance_deleteKentKartInfo);
		newQuery = (Button) view.findViewById(R.id.button_kentKartBalance_newQuery);
		
		newQueryLayout = (LinearLayout) view.findViewById(R.id.linearLayout_kentKartBalance_newQuery);
		aliasNo1 = (EditText) view.findViewById(R.id.editText_kentKartBalance_aliasNo1);
		aliasNo2 = (EditText) view.findViewById(R.id.editText_kentKartBalance_aliasNo2);
		aliasNo3 = (EditText) view.findViewById(R.id.editText_kentKartBalance_aliasNo3);
		saveStatus = (CheckBox) view.findViewById(R.id.checkBox_kentKartBalance_saveKentKart_saveStatus);
		saveName = (EditText) view.findViewById(R.id.editText_kentKartBalance_saveKentKart_saveName);
		existingQuery = (Button) view.findViewById(R.id.button_kentKartBalance_existingQuery);
		
		query = (Button) view.findViewById(R.id.button_kentKartBalance_query);
		
		resultLayout = (LinearLayout) view.findViewById(R.id.linearLayout_kentKartBalance_result);
		balance = (TextView) view.findViewById(R.id.textView_kentKartBalance_balance);
		lastLoad = (TextView) view.findViewById(R.id.textView_kentKartBalance_lastLoad);
		lastUse = (TextView) view.findViewById(R.id.textView_kentKartBalance_lastUse);
		goBack = (Button) view.findViewById(R.id.button_kentKartBalance_goBack);
		
		// Click listener for new query button
		newQuery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Set mode to new query
				setExistingQueryMode(false);
				
				// Hide info text
				showOrHideView(info, false);
			}
		});
		
		// Text change listener for aliasNo1 to select next field when filled
		aliasNo1.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				if(s.length() == 5)
				{
					aliasNo2.requestFocus();
				}
				
				checkFields();
			}
		});
		
		// Text change listener for aliasNo2 to select next field when filled
		aliasNo2.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				if(s.length() == 5)
				{
					aliasNo3.requestFocus();
				}
				
				checkFields();
			}
		});
		
		// Text change listener for aliasNo3 to enable query button when filled
		aliasNo3.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				checkFields();
			}
		});
		
		// Checked change listener for save status
		saveStatus.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// Show or hide save name
				showOrHideView(saveName, isChecked);
				
				checkFields();
			}
		});
		
		// Text change listener for save name to enable query button when filled
		saveName.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				checkFields();
			}
		});
		
		// Click listener for new query button
		existingQuery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Load Kent Kart list again
				initializeKentKarts();
				
				// Set mode to existing query
				setExistingQueryMode(true);
				
				// Show info text
				showOrHideView(info, true);
			}
		});
		
		// Click listener for query button
		query.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				KentKart kentKart = null;
				
				if(isExistingQueryMode)
				{
					// Get selected Kent Kart
					kentKart = getSelectedKentKart();
				}
				else
				{
					if(saveStatus.isChecked())
					{
						// Save Kent Kart
						kentKart = saveKentKart();
					}
					else
					{
						// Just create a Kent Kart
						kentKart = createKentKart();
					}
				}
				
				// Query
				new QueryKentKartBalanceTask(KentKartBalanceFragment.this, kentKart).execute();
			}
		});
		
		// Click listener for delete Kent Kart button
		deleteKentKart.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				deleteKentKart();
				
				// Load Kent Kart list again
				initializeKentKarts();
			}
		});
		
		// Click listener for go back button
		goBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Reload Kent Karts
				initializeKentKarts();
			}
		});
		
		initializeKentKarts();
	}
	
	private void initializeKentKarts()
	{
		// Show existing and new query layouts (One of them will be hidden later)
		showOrHideView(existingQuery, true);
		showOrHideView(newQuery, true);
		
		// Show info and query button
		showOrHideView(info, true);
		showOrHideView(query, true);
		
		// Hide result layout
		showOrHideView(resultLayout, false);
		// Show or hide save name
		showOrHideView(saveName, false);
		
		// Get saved Kent Karts
		loadKentKarts();
		
		// Set adapter
		selectedKentKart.setAdapter(new ArrayAdapter<KentKart>(getActivity(), android.R.layout.simple_spinner_dropdown_item, kentKarts));
		
		// If there are saved Kent Karts
		if(kentKarts != null && kentKarts.size() > 0)
		{
			// Set mode to existing query
			setExistingQueryMode(true);
			
			// Show delete Kent Kart button and info
			showOrHideView(deleteKentKart, true);
			showOrHideView(deleteKentKartInfo, true);
			
			// Show new query button
			showOrHideView(newQuery, true);
			
			// Set info
			info.setText(getString(R.string.kentKartBalance_info_selectKentKart));
		}
		else
		{
			// Set mode to new query
			setExistingQueryMode(false);
			
			// Hide existing query button
			showOrHideView(existingQuery, false);
			
			// Set info
			info.setText(getString(R.string.kentKartBalance_info_noSavedKentKart));
		}
	}
	
	/** Checks input fields and enables or disables query button accordingly */
	private void checkFields()
	{
		// If all fields are filled
		if(aliasNo1.getText().toString().length() == 5 &&
			aliasNo2.getText().toString().length() == 5 &&
			aliasNo3.getText().toString().length() == 1 &&
			(saveStatus.isChecked() ? 
				saveName.getText().toString().length() > 0 :
				true))
		{
			// Enable query button
			query.setEnabled(true);
		}
		else
		{
			// Disable query button
			query.setEnabled(false);
		}
	}
	
	/** Sets query mode to existing or new
	 * 
	 * @param isExistingQuery If true, mode will be existing query mode */
	private void setExistingQueryMode(boolean isExistingQuery)
	{
		// Set flag
		isExistingQueryMode = isExistingQuery;
		
		// Show or hide new query layout
		showOrHideView(newQueryLayout, !isExistingQuery);
		
		// Show or hide existing query layout
		showOrHideView(existingQueryLayout, isExistingQuery);
		
		if(isExistingQuery)
		{
			// Set query button enabled state
			query.setEnabled(selectedKentKart.getSelectedItemPosition() != Spinner.INVALID_POSITION);
		}
		else
		{
			// Clear alias numbers
			aliasNo1.setText("");
			aliasNo2.setText("");
			aliasNo3.setText("");
			
			// Reset save status and name
			saveStatus.setChecked(false);
			saveName.setText("");
			
			query.setEnabled(false);
		}
	}
	
	private KentKart createKentKart()
	{
		return new KentKart(saveName.getText().toString(),
				aliasNo1.getText().toString(), aliasNo2.getText().toString(),
				aliasNo3.getText().toString());
	}
	
	private KentKart getSelectedKentKart()
	{
		KentKart kentKart = null;
		KentKartDatabase db = KentKartDatabase.getDatabase(getActivity());
		ArrayList<KentKart> kentKarts = db.get();
		db.closeDatabase();
		
		if(kentKarts != null && kentKarts.size() > 0)
		{
			kentKart = kentKarts.get(selectedKentKart.getSelectedItemPosition());
		}
		
		return kentKart;
	}
	
	private void loadKentKarts()
	{
		KentKartDatabase db = KentKartDatabase.getDatabase(getActivity());
		kentKarts = db.get();
		db.closeDatabase();
	}
	
	private KentKart saveKentKart()
	{
		KentKart kentKart = createKentKart();
		
		KentKartDatabase db = KentKartDatabase.getDatabase(getActivity());
		db.addOrUpdate(kentKart);
		db.closeDatabase();
		
		Messages.getInstance().showPositive(getActivity(),
				getString(R.string.info_kentKart_saved, kentKart.toString()));
		
		return kentKart;
	}
	
	private void deleteKentKart()
	{
		// Get selected Kent Kart
		KentKart kentKart = getSelectedKentKart();
		
		// Delete selected Kent Kart
		KentKartDatabase db = KentKartDatabase.getDatabase(getActivity());
		db.delete(kentKart.getId());
		db.closeDatabase();
		
		Messages.getInstance().showPositive(getActivity(),
				getString(R.string.info_kentKart_deleted, kentKart.toString()));
	}
	
	/** Sets the visibility of the given view
	 * 
	 * @param view View whose visibility will be changed
	 * @param show If true, view will be visible. If false, view will be gone */
	private void showOrHideView(View view, boolean show)
	{
		if(show)
		{
			view.setVisibility(View.VISIBLE);
		}
		else
		{
			view.setVisibility(View.GONE);
		}
	}
}