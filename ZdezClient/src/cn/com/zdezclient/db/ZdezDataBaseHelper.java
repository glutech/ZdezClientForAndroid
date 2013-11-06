package cn.com.zdezclient.db;

import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.preference.ZdezPreferences;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ZdezDataBaseHelper extends SQLiteOpenHelper {

	// 对于不同的用户id，创建不同的数据库，
	private final static String DATABASE_NAME = "cn_com_zdez_"
			+ ZdezPreferences.getUserId(ZdezApplication.getInstance()
					.getPrefs()) + ".db";
	private final static int DATABASE_VERSION = 3;
	private volatile static ZdezDataBaseHelper zdezDBInstance;
	private final static String TAG = ZdezDataBaseHelper.class.getSimpleName();
	private final static boolean DEBUG = ZdezPreferences.getDebug();

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

	/**
	 * 随着程序功能的升级，数据库的结构也需要调整（数据库文件不是代码，不随程序升级而改变，除非在这里明确指明） 本来可以使用newVersion >
	 * oldVersion,这一个条件来进行所有的更新操作，但为了明确记录每次升级的改变，故明确指明每个新旧版本号，虽然代码冗余，但思路清晰
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG)
			Log.d("DBHelper", "On upgrade db, the oldVersion is :" + oldVersion
					+ " and the newVersion is : " + newVersion);
		if (oldVersion == 1 && newVersion == 2) {
			if (DEBUG)
				Log.d("DBHelper", "add the remarks column");
			db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN schoolMsgRemarks TEXT");
		}

		if (oldVersion == 2 && newVersion == 3) {
			if (DEBUG)
				Log.d("DBHelper", "Update db structure from ver2 to ver3");
			// db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN userId TEXT");
			// db.execSQL("ALTER TABLE News ADD COLUMN userId TEXT");
			// db.execSQL("ALTER TABLE ZdezMsg ADD COLUMN userId TEXT");
			db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN schoolMsgTop INTEGER");
			db.execSQL("ALTER TABLE News ADD COLUMN newsTop INTEGER");
			db.execSQL("ALTER TABLE ZdezMsg ADD COLUMN zdezTop INTEGER");
		}

		if (oldVersion == 1 && newVersion == 3) {
			if (DEBUG)
				Log.d("DBHelper", "Update db structure from ver1 to ver3");
			db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN schoolMsgRemarks TEXT");
			// db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN userId TEXT");
			// db.execSQL("ALTER TABLE News ADD COLUMN userId TEXT");
			// db.execSQL("ALTER TABLE ZdezMsg ADD COLUMN userId TEXT");
			db.execSQL("ALTER TABLE SchoolMsg ADD COLUMN schoolMsgTop INTEGER");
			db.execSQL("ALTER TABLE News ADD COLUMN newsTop INTEGER");
			db.execSQL("ALTER TABLE ZdezMsg ADD COLUMN zdezTop INTEGER");
		}

		if (DEBUG)
			Log.d("DBHelper",
					"Finish the upgrage, the version of DB is "
							+ db.getVersion());

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
				+ "schoolMsgTop INTEGER, " + "schoolMsgReadStatus INTEGER,"
				+ "schoolMsgCover TEXT)";
		db.execSQL(sql);
	}

	private void createNewsTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS News ("
				+ "newsId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "newsTitle TEXT," + "newsContent TEXT,"
				+ "newsDate timestamp," + "newsReadStatus INTEGER, "
				+ "newsTop INTEGER, " + "newsCover TEXT)";
		db.execSQL(sql);
	}

	private void createZdezTable(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS ZdezMsg ("
				+ "zdezId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "zdezTitle TEXT," + "zdezContent TEXT,"
				+ "zdezDate timestamp," + "zdezReadStatus INTEGER,"
				+ "zdezTop INTEGER, " + "zdezCover TEXT)";
		db.execSQL(sql);
	}
}
