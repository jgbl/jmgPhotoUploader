package org.de.jmg.jmgphotouploader;
import java.io.*;

import android.database.sqlite.*;
import android.content.*;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

public class dbpp extends SQLiteOpenHelper
{
	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/org.de.jmg.jmgphotouploader/databases/";

	private static String DB_NAME = "JMGPhotoPrinter.sqlite";

	public SQLiteDatabase DataBase;

	private Context myContext;
	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int P1, int P2)
	{

	}
	public dbpp(Context context)
	{
		super(context, DB_NAME, null, 1);
		File FilesDir = context.getFilesDir();
		DB_PATH = Path.combine(FilesDir.getParent(),"databases");
		this.myContext = context;
		String extPath = Environment.getExternalStorageDirectory().getPath();
		File F = new File(extPath);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			F = context.getExternalFilesDir(null);
			extPath = F.getPath();
		}
		if (F.isDirectory() && F.exists())
		{
			String JMGDataDirectory = Path.combine(extPath, "jmgphotoprinter","database");
			File F1 = new File(JMGDataDirectory);
			if (F1.isDirectory() == false && !F1.exists())
			{
				F1.mkdirs();
				if (checkDataBase() && F1.exists())
				{
					try {
						lib.copyFile(DB_PATH + DB_NAME,Path.combine(JMGDataDirectory,DB_NAME));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						lib.ShowException(context, e);
					}
				}
			}
			if (F1.exists()) DB_PATH = JMGDataDirectory;
			if (DB_PATH.endsWith("/") == false)
			{
				DB_PATH = DB_PATH + "/";
			}
		}
	}

	public final void createDataBase()
	{

		boolean dbExist = checkDataBase();

		if (dbExist)
		{
			//do nothing - database already exist
		}
		else
		{

			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();

			try
			{

				copyDataBase();

			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
				lib.ShowException(myContext,e);
				//throw new RuntimeException("Error copying database");

			}
		}

	}

	/**
  * Check if the database already exist to avoid re-copying the file each time you open the application.
  * @return true if it exists, false if it doesn't
  */
	private boolean checkDataBase()
	{

		SQLiteDatabase checkDB = null;

		try
		{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}
		catch (SQLiteException e)
		{
			System.out.println(e.getMessage());
			//database does't exist yet.

		}

		if (checkDB != null)
		{

			checkDB.close();

		}

		return checkDB != null;
	}

	private void copyDataBase() throws IOException
	{

		//Open your local db as the input stream
		AssetManager A = myContext.getAssets();
		InputStream myInput = A.open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		if ((new java.io.File(DB_PATH)).isDirectory() == false)
		{
			(new java.io.File(DB_PATH)).mkdirs();
		}
		//Open the empty db as the output stream
		
		File file = new File(outFileName);
				
		if (file.exists())
		{
			file.delete();
		}
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		OutputStream myOutput = new FileOutputStream(file);

		//transfer bytes from the inputfile to the outputfile
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte[] buffer = new byte[1024];
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer,0,1024)) > 0)
		{
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public final void openDataBase()
	{

		//Open the database
		String myPath = DB_PATH + DB_NAME;
		DataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		isClosed = false;
	}

	public boolean isClosed;
	@Override
	public void close()
	{

		if (DataBase != null)
		{
			DataBase.close();
			isClosed = true;
		}

		super.close();

	}

	public final android.database.Cursor query(String SQL)
	{
		if (DataBase == null)
		{
			openDataBase();
		}
		return DataBase.rawQuery(SQL, null);

	}

}