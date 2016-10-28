package org.de.jmg.jmgphotouploader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	public JMPPPApplication app;
	public android.database.Cursor Cursor;
	public static int requestCode = 9999;
	private boolean _changed = false;
	public SettingsActivity() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		try
		{
			setContentView(R.layout.settingsactivity);
			app = (JMPPPApplication) getApplication();
			
			if (app.dbpp != null)
			{
				lib.dbpp = app.dbpp;
				lib.dbpp = new dbpp(this);
				lib.dbpp.createDataBase();
				app.dbpp = lib.dbpp;
			}
			else
			{
				lib.dbpp = new dbpp(this);
				lib.dbpp.createDataBase();
				app.dbpp = lib.dbpp;
			}
			if (app.ppa.ServiceCursor == null)
			{
				do {
		        	Cursor = lib.dbpp.query("Select * FROM Services");
					app.ppa.ServiceCursor = Cursor;
					if (Cursor.getCount() == 3){
						try{
							lib.dbpp.DataBase.execSQL ("ALTER TABLE Services ADD COLUMN 'package' VARCHAR");
						}
						catch(Exception ex){
							System.out.print(ex.getMessage());
						}
						lib.dbpp.DataBase.execSQL("INSERT INTO Services ('Name','URL','package') VALUES('Pinterest','pinterest.com','com.pinterest')");
					}
				} while (Cursor.getCount() < 4);
			}
			else
			{
				Cursor = lib.dbpp.query("Select * FROM Services");
				app.ppa.ServiceCursor = Cursor;
			}
			if (Cursor.getCount() > 0)
			{
				TableLayout layout = (TableLayout)this.findViewById(R.id.settings); // (context);  
				layout.setBackgroundColor(0xFF000000);
				if (layout.getChildCount() == 0)
				{
					boolean first = true;
					lib.setgstatus("GetChildview enumerate Services");
					TableRow tr = new TableRow(this);
			        tr.setLayoutParams(new ViewGroup.LayoutParams(
			                ViewGroup.LayoutParams.FILL_PARENT,
			                ViewGroup.LayoutParams.WRAP_CONTENT));
					for(int i = 1; i < Cursor.getColumnCount(); i++) 
					{
						
				 
				        /** Creating a TextView to add to the row **/
				        TextView Column = new TextView(this);
				        Column.setText(Cursor.getColumnName(i));
						if (Cursor.getColumnName(i).equalsIgnoreCase("package"))
						{
							Column.setText(app.MainContext.getString(R.string.packagename));
						}
				        Column.setTextColor(Color.GRAY);
				        Column.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
						Column.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
				        //Column.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				        Column.setPadding(5, 5, 5, 0);
				        int index = i - 1;
				        if (index == 3) {index = 0; Column.setText(" ");}
				        tr.addView(Column, index);  // Adding textView to tablerow.
				    }
					// Add the TableRow to the TableLayout
			        layout.addView(tr, new TableLayout.LayoutParams(
			                LayoutParams.FILL_PARENT,
			                LayoutParams.WRAP_CONTENT));
			 
					while ((first) ? (Cursor.moveToFirst()) : (Cursor.moveToNext()))
					{
						tr = new TableRow(this);
				        for(int i = 1; i < Cursor.getColumnCount(); i++) 
				        {
				        	first = false;
							
							
					        if (Cursor.getString(i) == null || (Cursor.getString(i).equalsIgnoreCase("true") == false && Cursor.getString(i).equalsIgnoreCase("false") == false))
							{
					        	EditText Column = new EditText(this);
					        	Column.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL 
					        			| android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
								Column.setText(Cursor.getString(i));
								Column.setTextColor(Color.WHITE);
								Column.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
						        Column.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
						        //Column.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
						        Column.setPadding(5, 5, 5, 0);
						        if (i == 0) Column.setEnabled(false);
						        Column.setTag(Cursor.getInt(0));
						        tr.addView(Column);
							}
							else
							{
								CheckBox Column = new CheckBox(this);
								//Column.setText(Cursor.getString(i));
								Column.setChecked((Cursor.getString(i).equalsIgnoreCase("true"))); 
								Column.setTextColor(Color.WHITE);
						        Column.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
						        //Column.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
						        Column.setTag(Cursor.getInt(0));
						        Column.setPadding(5, 5, 5, 0);
						        int index = i - 1;
						        if (index == 3) {index = 0;}
						        tr.addView(Column, index);
							}
	
						}
				     // Add the TableRow to the TableLayout
				        layout.addView(tr, new TableLayout.LayoutParams(
				                LayoutParams.FILL_PARENT,
				                LayoutParams.WRAP_CONTENT));
					}
				 
				}
				//layout.setShrinkAllColumns(true);
				HorizontalScrollView sv = (HorizontalScrollView)this.findViewById(R.id.scrollviewsettings);
				sv.setHorizontalScrollBarEnabled(true);
				//sv.computeScroll();
				sv.requestLayout();
				//layout.setStretchAllColumns(true);
			}

		}
		catch (Exception ex)
		{
			lib.ShowException(this, ex);
		}
	}
	
	public void addService (View v)
	{
		try
		{
			TableLayout layout = (TableLayout)this.findViewById(R.id.settings); // (context); 
			TableRow tr = new TableRow(this);
			String sql = "INSERT INTO Services ('Name','URL','package') VALUES('new','new','new')";
			lib.dbpp.openDataBase();
			lib.dbpp.DataBase.execSQL(sql);
			_changed = true;
			Cursor = lib.dbpp.query("Select * FROM Services");
			Cursor.moveToPosition(Cursor.getCount()-1);
	        for(int i = 1; i < Cursor.getColumnCount(); i++) 
	        {
	        	if (Cursor.getString(i) == null || (Cursor.getString(i).equalsIgnoreCase("true") == false && Cursor.getString(i).equalsIgnoreCase("false") == false))
				{
		        	EditText Column = new EditText(this);
		        	Column.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_NORMAL 
		        			| android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
					Column.setText(Cursor.getString(i));
					Column.setTextColor(Color.WHITE);
					Column.setTextSize(TypedValue.COMPLEX_UNIT_PX,lib.convertFromDp(this, Column.getTextSize()));
			        Column.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			        //Column.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			        Column.setPadding(5, 5, 5, 0);
			        if (i == 0) Column.setEnabled(false);
			        Column.setTag(Cursor.getInt(0));
			        tr.addView(Column);
				}
				else
				{
					CheckBox Column = new CheckBox(this);
					//Column.setText(Cursor.getString(i));
					Column.setChecked((Cursor.getString(i).equalsIgnoreCase("true"))); 
					Column.setTextColor(Color.WHITE);
			        Column.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
			        //Column.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			        Column.setTag(Cursor.getInt(0));
			        Column.setPadding(5, 5, 5, 0);
			        int index = i - 1;
			        if (index == 3) {index = 0;}
			        tr.addView(Column, index);
				}
	
			}
	     // Add the TableRow to the TableLayout
	        layout.addView(tr, new TableLayout.LayoutParams(
	                LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT));
	        HorizontalScrollView sv = (HorizontalScrollView)this.findViewById(R.id.scrollviewsettings);
			//sv.computeScroll();
			sv.requestLayout();
		}
		catch (Exception ex)
		{
			lib.ShowException(this, ex);
		}
	}

	
	
	@Override
	protected void onStart()
	{
		super.onStart();
			
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle)
	{
		super.onSaveInstanceState(bundle);
		
	}
	private boolean isInFocus = false;
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    isInFocus = hasFocus;
	    if (!hasFocus) 
	    {
	    	SaveSettings();
	    	Intent i = new Intent();
            i.putExtra("changed", _changed);
            setResult(Activity.RESULT_OK, i);
            finishActivity(requestCode);
	    }
	}
	@Override
	public void onBackPressed() {
	    if (true) 
	    {
	    	SaveSettings();
	    	Intent i = new Intent();
            i.putExtra("changed", _changed);
            setResult(Activity.RESULT_OK, i);
            finishActivity(requestCode);
	    }
	    super.onBackPressed();
	    
	}
	@Override
    protected void onPause() 
    {
		super.onPause();
	}
	
	private void SaveSettings()
	{
		try
		{
			if (true) // (lib.ShowMessageYesNo(this, "Do you want to save all changes?"))
			{
				TableLayout layout = (TableLayout)this.findViewById(R.id.settings);
				if (app.dbpp != null)
				{
					lib.dbpp = app.dbpp;
					lib.dbpp = new dbpp(this);
					lib.dbpp.createDataBase();
					app.dbpp = lib.dbpp;
				}
				else
				{
					lib.dbpp = new dbpp(this);
					lib.dbpp.createDataBase();
					app.dbpp = lib.dbpp;
				}
				Cursor = lib.dbpp.query("Select * FROM Services");
				app.ppa.ServiceCursor = Cursor;
				for (int i = 1; i < layout.getChildCount(); i++)
				{
					TableRow  r = (TableRow)layout.getChildAt(i);
					for (int ii = 0; ii < r.getChildCount(); ii++)
					{
						View v = r.getChildAt(ii);
						Cursor.moveToPosition((Integer)v.getTag());
						if (v.getClass() == EditText.class)
						{
							EditText e = (EditText)v;
							String EdText = e.getText().toString();
							String CursorText = Cursor.getString(ii);
							if (CursorText == null && EdText == "") EdText = CursorText;
							if (EdText.equals(CursorText) == false)
							{
								_changed = true;
								String sql = "UPDATE Services SET " 
									+ Cursor.getColumnName(ii) 
									+ " = \"" + e.getText() + "\" WHERE _id = " + e.getTag();
							
								lib.dbpp.DataBase.execSQL(sql);
							}
						}
						else if(v.getClass() == CheckBox.class)
						{
							CheckBox cb = (CheckBox)v;
							int index = ii + 1;
							if (index == 1) index = 4; else index = ii;
							if (("" + (cb.isChecked())).equals(Cursor.getString(index)) == false)
							{
								_changed = true;
								String sql = "UPDATE Services SET " 
									+ Cursor.getColumnName(index) 
									+ " = \"" + cb.isChecked() + "\" WHERE _id = " + cb.getTag();
							
								lib.dbpp.DataBase.execSQL(sql);
							}
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	@Override
    protected void onDestroy() 
    {
    	super.onDestroy();
    	// if (!isInFocus) launchMainActivity();
    }
	private void launchMainActivity() {
        startActivity(new Intent(getApplicationContext(), _MainActivity.class));
    }

}
