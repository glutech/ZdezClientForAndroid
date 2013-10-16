package cn.com.zdezclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ZdezDataBaseHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "cn_com_zdez.db";
	private final static int DATABASE_VERSION = 2;
	private volatile static ZdezDataBaseHelper zdezDBInstance;

	/**
	 * 返回这个类的单例
	 * 
	 * @param context
	 * @return
	 */
	public static ZdezDataBaseHelper getInstance(Context context) {
		if (zdezDBInstance == null) {
			synchronized (ZdezDataBaseHelper.class) {
				if (zdezDBInstance == null) {
					zdezDBInstance = new ZdezDataBaseHelper(context);
				}
			}
		}
		return zdezDBInstance;
	}

	public ZdezDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createSchoolMsgTable(db);
		createNewsTable(db);
		createZdezTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("DBHelper", "On upgrade db, the oldVersion is :" + oldVersion
				+ " and the newVersion is : " + newVersion);
		if (oldVersion == 1 && newVersion == 2) {
			Log.d("DBHelper", "add the remarks column");
			db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN schoolMsgRemarks TEXT");
		}

		Log.d("DBHelper",
				"Finish the upgrage, the version of DB is " + db.getVersion());

	}

	public static void deleteAllTableWhileLogout(Context context) {
		SQLiteDatabase db = getInstance(context).getWritableDatabase();
		db.delete("SchoolMsg", null, null);
		db.delete("News", null, null);
		db.delete("ZdezMsg", null, null);
	}

	/**
	 * 创建学校信息表
	 * 
	 * @param db
	 */
	private void createSchoolMsgTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS SchoolMsg ("
				+ "schoolMsgId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "schoolMsgTitle TEXT," + "schoolMsgContent TEXT,"
				+ "schoolMsgDate timestamp," + "schoolMsgSchoolName TEXT,"
				+ "schoolMsgSenderName TEXT," + "schoolMsgRemarks TEXT, "
				+ "schoolMsgReadStatus INTEGER," + "schoolMsgCover TEXT)";
		db.execSQL(sql);
	}

	private void createNewsTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS News ("
				+ "newsId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "newsTitle TEXT," + "newsContent TEXT,"
				+ "newsDate timestamp," + "newsReadStatus INTEGER,"
				+ "newsCover TEXT)";
		db.execSQL(sql);
	}

	private void createZdezTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS ZdezMsg ("
				+ "zdezId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "zdezTitle TEXT," + "zdezContent TEXT,"
				+ "zdezDate timestamp," + "zdezReadStatus INTEGER,"
				+ "zdezCover TEXT)";
		db.execSQL(sql);
	}
}
