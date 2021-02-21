package core.pdf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "PDF_DATABASE";

    private static final String TABLE_PDFDOC = "PDFDoc";

    private static final String COLUMN_PDFDOC_ID = "PDFDoc_Id";
    private static final String COLUMN_PDFDOC_PATH = "PDFDoc_Path";
    private static final String COLUMN_PDFDOC_NAME = "PDFDoc_Name";
    private static final String COLUMN_PDFDOC_SIZE = "PDFDoc_Size";
    private static final String COLUMN_PDFDOC_FAVOURITE = "PDFDoc_Favourite";
    private static final String COLUMN_PDFDOC_OPENED_TIME = "PDFDoc_Opened_Time";
    private static final String COLUMN_PDFDOC_LAST_MODIFIED = "PDFDoc_Last_Modified";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String scriptPDFDoc = "CREATE TABLE " + TABLE_PDFDOC + "("
                + COLUMN_PDFDOC_ID + " TEXT PRIMARY KEY,"
                + COLUMN_PDFDOC_PATH + " TEXT,"
                + COLUMN_PDFDOC_NAME + " TEXT,"
                + COLUMN_PDFDOC_SIZE + " TEXT,"
                + COLUMN_PDFDOC_FAVOURITE + " INTEGER,"
                + COLUMN_PDFDOC_OPENED_TIME + " INTEGER,"
                + COLUMN_PDFDOC_LAST_MODIFIED + " INTEGER"
                + ")";

        sqLiteDatabase.execSQL(scriptPDFDoc);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PDFDOC);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<PDFDoc> getAllPDFDoc() {

        ArrayList<PDFDoc> pdfDocList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PDFDOC;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PDFDoc pdfDoc = new PDFDoc();
                pdfDoc.setId(cursor.getString(0));
                pdfDoc.setPath(cursor.getString(1));
                pdfDoc.setName(cursor.getString(2));
                pdfDoc.setSize(cursor.getString(3));
                pdfDoc.setFavourite(cursor.getInt(4)==1);
                pdfDoc.setOpenedTime(cursor.isNull(5)?null:new Date(cursor.getLong(5)));
                pdfDoc.setLastModified(new Date(cursor.getLong(6)));
                // Adding to list
                pdfDocList.add(pdfDoc);
            } while (cursor.moveToNext());
        }

        return pdfDocList;
    }

    public ArrayList<PDFDoc> getAllPDFDocFavourite() {

        ArrayList<PDFDoc> pdfDocList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PDFDOC + " WHERE " + COLUMN_PDFDOC_FAVOURITE + " = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PDFDoc pdfDoc = new PDFDoc();
                pdfDoc.setId(cursor.getString(0));
                pdfDoc.setPath(cursor.getString(1));
                pdfDoc.setName(cursor.getString(2));
                pdfDoc.setSize(cursor.getString(3));
                pdfDoc.setFavourite(cursor.getInt(4)==1);
                pdfDoc.setOpenedTime(cursor.isNull(5)?null:new Date(cursor.getLong(5)));
                pdfDoc.setLastModified(new Date(cursor.getLong(6)));
                // Adding to list
                pdfDocList.add(pdfDoc);
            } while (cursor.moveToNext());
        }

        return pdfDocList;
    }

    public ArrayList<PDFDoc> getAllPDFDocRecent() {

        ArrayList<PDFDoc> pdfDocList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PDFDOC + " WHERE " + COLUMN_PDFDOC_OPENED_TIME + " IS NOT NULL";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PDFDoc pdfDoc = new PDFDoc();
                pdfDoc.setId(cursor.getString(0));
                pdfDoc.setPath(cursor.getString(1));
                pdfDoc.setName(cursor.getString(2));
                pdfDoc.setSize(cursor.getString(3));
                pdfDoc.setFavourite(cursor.getInt(4)==1);
                pdfDoc.setOpenedTime(new Date(cursor.getLong(5)));
                pdfDoc.setLastModified(new Date(cursor.getLong(6)));
                // Adding to list
                pdfDocList.add(pdfDoc);
            } while (cursor.moveToNext());
        }

        return pdfDocList;
    }

    public void addPDFDoc(PDFDoc pdfDoc) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PDFDOC_ID, pdfDoc.getId());
        values.put(COLUMN_PDFDOC_PATH, pdfDoc.getPath());
        values.put(COLUMN_PDFDOC_NAME, pdfDoc.getName());
        values.put(COLUMN_PDFDOC_SIZE, pdfDoc.getSize());
        values.put(COLUMN_PDFDOC_FAVOURITE, pdfDoc.isFavourite() ? 1 : 0);
        values.put(COLUMN_PDFDOC_LAST_MODIFIED,pdfDoc.getLastModified().getTime());
        if(pdfDoc.getOpenedTime()!=null){
            values.put(COLUMN_PDFDOC_OPENED_TIME,pdfDoc.getOpenedTime().getTime());
        }

        sqLiteDatabase.insert(TABLE_PDFDOC, null, values);

        sqLiteDatabase.close();
    }

    public int updatePDFDoc(PDFDoc pdfDoc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PDFDOC_NAME, pdfDoc.getName());
        values.put(COLUMN_PDFDOC_SIZE, pdfDoc.getSize());
        values.put(COLUMN_PDFDOC_FAVOURITE, pdfDoc.isFavourite() ? 1 : 0);
        if(pdfDoc.getOpenedTime()!=null){
            values.put(COLUMN_PDFDOC_OPENED_TIME,pdfDoc.getOpenedTime().getTime());
        }else {
            values.putNull(COLUMN_PDFDOC_OPENED_TIME);
        }
        values.put(COLUMN_PDFDOC_LAST_MODIFIED,pdfDoc.getLastModified().getTime());

        // updating row
        return db.update(TABLE_PDFDOC, values, COLUMN_PDFDOC_ID + " = ?",
                new String[]{String.valueOf(pdfDoc.getId())});
    }

    public void deletePDFDoc(PDFDoc pdfDoc) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PDFDOC, COLUMN_PDFDOC_ID + " = ?",
                new String[]{String.valueOf(pdfDoc.getId())});
        db.close();
    }

    public void loadAllPDFDoc(List<PDFDoc> pdfDocListLoaded) {
        List<PDFDoc> pdfDocDBs = getAllPDFDoc();

        deleteAllPDFDoc();

        for (PDFDoc pdfDoc : pdfDocListLoaded) {

            for (PDFDoc pdfDocDB : pdfDocDBs) {
                if (pdfDoc.getPath().equals(pdfDocDB.getPath())) {
                    pdfDoc.setId(pdfDocDB.getId());
                    pdfDoc.setFavourite(pdfDocDB.isFavourite());
                    pdfDoc.setOpenedTime(pdfDocDB.getOpenedTime());
                    pdfDoc.setLastModified(pdfDocDB.getLastModified());
                    break;
                }
            }

            addPDFDoc(pdfDoc);
        }
    }


    public void deleteAllPDFDoc() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PDFDOC);
    }

    public int clearPDFDocFavourite() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PDFDOC_FAVOURITE, 0);
        // updating row
        return db.update(TABLE_PDFDOC, values, null,null);
    }

    public int clearPDFDocRecent() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.putNull(COLUMN_PDFDOC_OPENED_TIME);
        // updating row
        return db.update(TABLE_PDFDOC, values, null,null);
    }
}

