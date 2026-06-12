package com.example.electricitybillcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricity_bill.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "bills";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_KWH = "kwh";
    private static final String COLUMN_TOTAL_CHARGES = "total_charges";
    private static final String COLUMN_REBATE_PERCENT = "rebate_percent";
    private static final String COLUMN_FINAL_COST = "final_cost";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MONTH + " TEXT NOT NULL, " +
                    COLUMN_KWH + " INTEGER NOT NULL, " +
                    COLUMN_TOTAL_CHARGES + " REAL NOT NULL, " +
                    COLUMN_REBATE_PERCENT + " INTEGER NOT NULL, " +
                    COLUMN_FINAL_COST + " REAL NOT NULL" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertBill(String month, int kwh, double totalCharges, int rebatePercent, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_KWH, kwh);
        values.put(COLUMN_TOTAL_CHARGES, totalCharges);
        values.put(COLUMN_REBATE_PERCENT, rebatePercent);
        values.put(COLUMN_FINAL_COST, finalCost);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1;
    }

    public List<BillRecord> getAllBills() {
        List<BillRecord> billList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BillRecord bill = new BillRecord();
                bill.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                bill.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH)));
                bill.setKWh(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KWH)));
                bill.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CHARGES)));
                bill.setRebatePercent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REBATE_PERCENT)));
                bill.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_COST)));
                billList.add(bill);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return billList;
    }

    public BillRecord getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        BillRecord bill = null;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            bill = new BillRecord();
            bill.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            bill.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH)));
            bill.setKWh(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KWH)));
            bill.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CHARGES)));
            bill.setRebatePercent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REBATE_PERCENT)));
            bill.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_COST)));
        }
        cursor.close();
        db.close();

        return bill;
    }

    public boolean updateBill(int id, String month, int kwh, double totalCharges, int rebatePercent, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MONTH, month);
        values.put(COLUMN_KWH, kwh);
        values.put(COLUMN_TOTAL_CHARGES, totalCharges);
        values.put(COLUMN_REBATE_PERCENT, rebatePercent);
        values.put(COLUMN_FINAL_COST, finalCost);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }

    public boolean deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        return result > 0;
    }
}
