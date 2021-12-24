package id.shimo.mindfulness.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

import id.shimo.mindfulness.DetailActivity;
import id.shimo.mindfulness.Home;
import id.shimo.mindfulness.model.User;

public class DBHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "db_jornal";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tb_journal";
    private static final String ID_COLUMN = "id";
    private static final String BEST_THING_COLUMN = "bestThing";
    private static final String WORST_THING_COLUMN = "worstThing";
    private static final String RATE_COLUMN = "rate";
    private static final String WELLNESS_COLUMN = "wellness";
    private static final String DID_COLUMN = "did";
    private static final String DATETIME_COLUMN = "datetime";

    public static final String table_name1 = "tb_user";
    public static final String id_user = "id_user";
    public static final String row_name = "name";
    public static final String row_email = "email";
    public static final String row_username = "username";
    public static final String row_password = "password";
    public static final String row_age = "age";
    public static final String row_gender = "gender";

    private SQLiteDatabase database;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                +TABLE_NAME+"("
                +ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +BEST_THING_COLUMN+" TEXT, "
                +WORST_THING_COLUMN+" TEXT, "
                +RATE_COLUMN+" TEXT, "
                +WELLNESS_COLUMN+" TEXT, "
                +DID_COLUMN+" TEXT, "
                +DATETIME_COLUMN+" TEXT, "
                +id_user+" INT )"
        );

        String queryUser = "CREATE TABLE " + table_name1 + "(" + id_user + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + row_name + " TEXT," + row_email + " TEXT," + row_username + " TEXT,"
                + row_password + " TEXT," + row_age + " INTEGER," + row_gender + " TEXT)";

        db.execSQL(queryUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertDataUser(ContentValues values) {
        database.insert(table_name1, null, values);
    }

//    public boolean checkUser(String username, String password) {
//        String[] columns = {id_user};
//        SQLiteDatabase db = getReadableDatabase();
//        String selection = row_username + "=?" + " and " + row_password + "=?";
//        String[] selectionArgs = {username, password};
//        Cursor cursor = db.query(table_name1, columns, selection, selectionArgs, null, null, null);
//        int count = cursor.getCount();
//        cursor.close();
//        database.close();
//
//        if (count > 0)
//            return true;
//        else
//            return false;
//    }

    public User checkUser(String username, String password) {
        User loggedUser=new User();
        String[] columns = {id_user,row_name,row_email,row_age,row_password,row_username,row_gender};
        SQLiteDatabase db = getReadableDatabase();
        String selection = row_username + "=?" + " and " + row_password + "=?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(table_name1, columns, selection, selectionArgs, null, null, null);
        if(cursor.moveToFirst()){
            loggedUser.setId_user(cursor.getInt(cursor.getColumnIndexOrThrow(id_user)));
            loggedUser.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(row_age)));
            loggedUser.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(row_email)));
            loggedUser.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(row_username)));
            loggedUser.setGender(cursor.getString(cursor.getColumnIndexOrThrow(row_gender)));
            loggedUser.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(row_password)));
            loggedUser.setName(cursor.getString(cursor.getColumnIndexOrThrow(row_name)));
        }
        cursor.close();
        return loggedUser;
    }

    public void insertJournal (String bestThing, String worstThing, String rate, String wellness, String did, String datetime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BEST_THING_COLUMN, bestThing);
        contentValues.put(WORST_THING_COLUMN, worstThing);
        contentValues.put(RATE_COLUMN, rate);
        contentValues.put(WELLNESS_COLUMN, wellness);
        contentValues.put(DID_COLUMN, did);
        contentValues.put(DATETIME_COLUMN, datetime);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor readJournals() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = ("SELECT*FROM tb_journal ORDER BY id DESC");
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void deleteJournal(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db.delete(TABLE_NAME, ID_COLUMN + "=" + id, null) > 0){
            Toast.makeText(context, "Delete Success", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateJournal (Integer id, String bestThing, String worstThing, String rate, String wellness, String did, String datetime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BEST_THING_COLUMN, bestThing);
        contentValues.put(WORST_THING_COLUMN, worstThing);
        contentValues.put(RATE_COLUMN, rate);
        contentValues.put(WELLNESS_COLUMN, wellness);
        contentValues.put(DID_COLUMN, did);
        contentValues.put(DATETIME_COLUMN, datetime);
        if(db.update(TABLE_NAME, contentValues, ID_COLUMN + "=" + id, null) > 0){
            Toast.makeText(context, "Update Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, Home.class);
            context.startActivity(intent);
        }else {
            Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
