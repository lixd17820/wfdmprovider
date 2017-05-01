package com.android.provider.wfdmcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WfdmcodeDatabaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "";
	// private static final String DB_NAME = "flashcode.db";
	private SQLiteDatabase myDataBase;

	private final Context myContext;
	private int version;

	private String TAG = "WfdmcodeDatabaseHelper";

	private static WfdmcodeDatabaseHelper mDBConnection;

	/**
	 * 构造方法,由上下文和版本号两个参数
	 * 
	 * @param context
	 * @param version
	 */
	private WfdmcodeDatabaseHelper(Context context, int version) {
		super(context, Wfdmcode.DATABASE_NAME, null, version);
		this.myContext = context;
		this.version = version;
		DB_PATH = "/data/data/"
				+ context.getApplicationContext().getPackageName()
				+ "/databases/";
		File f = new File(DB_PATH);
		if (!f.exists())
			f.mkdirs();
	}

	/**
	 * 单例方法
	 * 
	 * @param context
	 * @return DBAdapter
	 */
	public static synchronized WfdmcodeDatabaseHelper getDBAdapterInstance(
			Context context, int version) {
		if (mDBConnection == null) {
			mDBConnection = new WfdmcodeDatabaseHelper(context, version);
		}
		return mDBConnection;
	}

	/**
	 * 创建数据库,如果数据库存在,则什么也不做 如果不存在则拷贝文件
	 * 
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {
		String myPath = DB_PATH + Wfdmcode.DATABASE_NAME;
		boolean isNewDb = checkDataBase();
		if (!isNewDb) {
			try {
				copyDataBase();
				SQLiteDatabase db = SQLiteDatabase.openDatabase(myPath, null,
						SQLiteDatabase.OPEN_READWRITE);
				if (db != null && db.getVersion() < version) {
					Log.e(TAG, "wfdw code version set to: " + version);
					db.setVersion(version);
					db.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 验证文件是否存在,并且版本和目前版本的关系,如果不存在或版本低于目前版本则返回真
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		boolean result = false;
		int oldversion = 0;
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH + Wfdmcode.DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		if (checkDB != null) {
			oldversion = checkDB.getVersion();
			Log.e(TAG, "old database's version is " + oldversion);
			checkDB.close();
			if (oldversion >= version) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets()
				.open(Wfdmcode.DATABASE_NAME);
		String outFileName = DB_PATH + Wfdmcode.DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * Open the database
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + Wfdmcode.DATABASE_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables
	 * again - upgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log
				.i("onUpgrade", "db old " + oldVersion + " newVersion "
						+ newVersion);

	}

	// ----------------------- CRUD Functions ------------------------------

	/**
	 * This function used to select the records from DB.
	 * 
	 * @param tableName
	 * @param tableColumns
	 * @param whereClase
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return A Cursor object, which is positioned before the first entry.
	 */
	public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
			String whereClase, String whereArgs[], String groupBy,
			String having, String orderBy) {
		return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
				groupBy, having, orderBy);
	}

	/**
	 * This function used to update the Record in DB.
	 * 
	 * @param tableName
	 * @param initialValues
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are updated
	 */
	public int updateRecordsInDB(String tableName, ContentValues initialValues,
			String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs);
	}

	/**
	 * This function used to delete the Record in DB.
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return 0 in case of failure otherwise return no of row(s) are deleted.
	 */
	public int deleteRecordInDB(String tableName, String whereClause,
			String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	// --------------------- Select Raw Query Functions ---------------------

	/**
	 * apply raw Query
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return Cursor
	 */
	public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);
	}

}

// 以上是FixcodeDatabaseHelper类,用于创建和更新数据库
