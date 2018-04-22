package com.bisoft.dictionary.helper;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bisoft.dictionary.model.WordObject;

/**
 * Created by burakisik on 04.03.2018.
 */

public class WordsDBHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    public static final String DATABASE_NAME = "allWords.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_ALLWORDS = "words";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_DEFINATION = "definition";
    public static final String COLUMN_PHONESPELLING = "phoneSpelling";
    public static final String COLUMN_FAVOURITEFLAG = "favouriteFlag";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ALLWORDS
            + "( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WORD + " text not null,"
            + COLUMN_DEFINATION + " text not null,"
            + COLUMN_PHONESPELLING + " text,"
            + COLUMN_FAVOURITEFLAG + " BOOLEAN"+
            ");";

    public WordsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALLWORDS);
        onCreate(db);
    }

    public boolean insertWord(WordObject word){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word.getWord());
        values.put(COLUMN_DEFINATION, word.getDefinition());
        values.put(COLUMN_PHONESPELLING, word.getPhoneSpelling());

        // Inserting Row
        db.insert(TABLE_ALLWORDS, null, values);
        db.close(); // Closing connection

        return true;
    }

    public Cursor getWord(String word) {
        db = this.getReadableDatabase();
        Cursor data = db.rawQuery("select * from words where word=" + word + "", null);
        return data;
    }

    public int numberOfRows() {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_ALLWORDS);
        return numRows;
    }

    public ArrayList<WordObject> getAllWords() {
        ArrayList<WordObject> allWordList = new ArrayList<>();

        db = this.getWritableDatabase();
        // Select All WORDS
        String selectQuery = "SELECT  * FROM " + TABLE_ALLWORDS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WordObject word = new WordObject();
                word.setWord(cursor.getString(1)); //if you start columnIndex=0 ,is returned id we dont need it!!!
                word.setDefinition(cursor.getString(2));
                word.setPhoneSpelling(cursor.getString(3));
                allWordList.add(word);
            } while (cursor.moveToNext());
        }
        return allWordList;
    }

    public ArrayList<WordObject> getFavouriteWords(){
        ArrayList<WordObject> favouriteWordList = new ArrayList<>();
        db = this.getWritableDatabase();

        Cursor cursor = db.query(true, TABLE_ALLWORDS, null, COLUMN_FAVOURITEFLAG + "= ?", new String[] { "1" }, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                boolean isFavourite = cursor.getInt(4) >0; //control COLUMN_FAVOURITEFLAG is true or not
                if(isFavourite) {//if favouriteFlag is true, word is favourite
                    WordObject word = new WordObject();
                    word.setWord(cursor.getString(1)); // if you start columnIndex=0 ,is returned id we dont need it!!!
                    word.setDefinition(cursor.getString(2));
                    word.setPhoneSpelling(cursor.getString(3));
                    favouriteWordList.add(word);
                }
            } while (cursor.moveToNext());
        }
        return favouriteWordList;
    }
    public boolean search(String word) {
        db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_ALLWORDS, null, COLUMN_WORD + "= ?", new String[] { word }, null, null, null, null);

        if (mCursor.getCount()>0) {
            Log.i("kelime","kayıtlı");
            return true;
        }
        else {
            Log.i("kelime","kayıtlı değil");
            return false;
        }
    }

    public void deleteWord(String name)
    {
        db = this.getWritableDatabase();
        String[] whereArgs = new String[] { String.valueOf(name) };
        db.delete(TABLE_ALLWORDS, COLUMN_WORD+"=?", whereArgs);
    }

    public boolean isFavouriteWord(String word) {
        db = this.getReadableDatabase();
        String [] settingsProjection = {
                COLUMN_ID,
                COLUMN_WORD,
                COLUMN_DEFINATION,
                COLUMN_PHONESPELLING,
                COLUMN_FAVOURITEFLAG
        };

        String whereClause =COLUMN_WORD+"=?";
        String [] whereArgs = {word};
        Cursor cursor = db.query(
                TABLE_ALLWORDS,
                settingsProjection,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            //control COLUMN_FAVOURITEFLAG is true or not
            boolean isFavourite = cursor.getInt(4) >0;
            if (isFavourite){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void makeFavouriteFlagTrue(String word){
        db = this.getReadableDatabase();
        ContentValues con = new ContentValues();
        con.put(COLUMN_FAVOURITEFLAG,true);
        int id = db.update(TABLE_ALLWORDS, con, COLUMN_WORD + " = ?",new String[]{word});
    }
    public void makeFavouriteFlagFalse(String word){
        db = this.getReadableDatabase();
        ContentValues con = new ContentValues();
        con.put(COLUMN_FAVOURITEFLAG,false);
        int id = db.update(TABLE_ALLWORDS, con, COLUMN_WORD + " = ?",new String[]{word});
    }
}

