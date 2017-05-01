package com.android.provider.wfdmcode;

import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.text.TextUtils;

public class WfdmcodeProvider extends ContentProvider {

	public static final String TAG = "WfdmcodeProvider";

	public WfdmcodeDatabaseHelper dbAdapter;

	public static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Wfdmcode.AUTHORITY, "item", Wfdmcode.ITEM);
		uriMatcher.addURI(Wfdmcode.AUTHORITY, "item/#", Wfdmcode.ITEM_ID);
		uriMatcher.addURI(Wfdmcode.AUTHORITY, "qzyj/#", Wfdmcode.QZYJ);
		uriMatcher.addURI(Wfdmcode.AUTHORITY, "force", Wfdmcode.FORCE);
	}

	@Override
	public boolean onCreate() {
		int currentCode = 0;
		try {
			PackageManager pm = getContext().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(
					"com.android.provider.wfdmcode", 0);
			currentCode = pi.versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		dbAdapter = WfdmcodeDatabaseHelper
				.getDBAdapterInstance(getContext(), currentCode);
		try {
			dbAdapter.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbAdapter.openDataBase();
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		int code = uriMatcher.match(uri);
		switch (code) {
		case Wfdmcode.ITEM:
			return Wfdmcode.CONTENT_TYPE;
		case Wfdmcode.ITEM_ID:
			return Wfdmcode.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// SQLiteDatabase db = databasehelper.getWritableDatabase();
		// long rowId;
		// if (uriMatcher.match(uri) != Fixcode.ITEM) {
		// throw new IllegalArgumentException("Unknown URI " + uri);
		// }
		// rowId = db.insert(Fixcode.FrmCode.CODE_TABLE_NAME,
		// Fixcode.FrmCode._ID,
		// values);
		// if (rowId > 0) {
		// Uri noteUri = ContentUris
		// .withAppendedId(Fixcode.CONTENT_URI, rowId);
		// getContext().getContentResolver().notifyChange(noteUri, null);
		// return noteUri;
		// }
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c;
		switch (uriMatcher.match(uri)) {
		case Wfdmcode.ITEM: {
			c = dbAdapter
					.selectRecordsFromDB(Wfdmcode.VioCodeWfdm.TABLE_NAME,
							projection, selection, selectionArgs, null, null,
							sortOrder);
		}
			break;
		case Wfdmcode.ITEM_ID: {
			String wfxwdm = uri.getPathSegments().get(1);
			String w = Wfdmcode.VioCodeWfdm.WFXW
					+ "='"
					+ wfxwdm
					+ "'"
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			c = dbAdapter.selectRecordsFromDB(Wfdmcode.VioCodeWfdm.TABLE_NAME,
					projection, w, selectionArgs, null, null, sortOrder);
		}
			break;
		case Wfdmcode.QZYJ: {
			String wfxwdm = uri.getPathSegments().get(1);
			String w = Wfdmcode.ForceCode.WFDM
					+ "='"
					+ wfxwdm
					+ "'"
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : "");
			c = dbAdapter.selectRecordsFromDB(Wfdmcode.ForceCode.TABLE_NAME,
					projection, w, selectionArgs, null, null, sortOrder);
		}
			break;
		case Wfdmcode.FORCE: {
			c = dbAdapter.selectRecordsFromDB(Wfdmcode.ForceCode.TABLE_NAME,
					projection, selection, selectionArgs, null, null, sortOrder);
		}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
