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

public class AllWordsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "allWords.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ALLWORDS = "words";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_DEFINATION = "definition";
    public static final String COLUMN_PHONESPELLING = "phoneSpelling";


    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ALLWORDS
            + "( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WORD + " text not null,"
            + COLUMN_DEFINATION + " text not null,"
            + COLUMN_PHONESPELLING + " text" +
            ");";


    public AllWordsDBHelper(Context context) {
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word.getWord());
        values.put(COLUMN_DEFINATION, word.getDefinition());
        values.put(COLUMN_PHONESPELLING, word.getPhoneSpelling());

        // Inserting Row
        db.insert(TABLE_ALLWORDS, null, values);
        db.close(); // Closing connection

        return true;
    }

    public Cursor getData(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("select * from words where word=" + word + "", null);
        return data;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_ALLWORDS);
        return numRows;
    }

    public ArrayList<WordObject> getAllWords() {
        ArrayList<WordObject> wordList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Log.i("MyFavChanDbHelper","getAllItems");

        // Select All WORDS
        String selectQuery = "SELECT  * FROM " + TABLE_ALLWORDS;
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                WordObject word = new WordObject();
                word.setWord(cursor.getString(1)); //if you start columnIndex=0 ,is returned id we dont need it!!!
                word.setDefinition(cursor.getString(2));
                word.setPhoneSpelling(cursor.getString(3));
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        return wordList;
    }

    public boolean search(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
       // String query = "SELECT * FROM "+TABLE_ALLWORDS+ " where "+COLUMN_WORD+" LIKE "+word;
        Cursor mCursor = db.query(true, TABLE_ALLWORDS, null, COLUMN_WORD + "= ?", new String[] { word }, null, null, null, null);
        //Cursor mCursor = db.rawQuery(query, null);

        if (mCursor.getCount()>0) {
            Log.i("kelime","kayıtlı");
            return true;
        }
        else {
            Log.i("kelime","kayıtlı değil");
            return false;
        }
    }
}

