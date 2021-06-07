package mx.tecnm.tepic.u3_practica1_sqliteconfirestore

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(bd: SQLiteDatabase) {
        bd.execSQL("CREATE TABLE APARTADO(IDAPARTADO INTEGER PRIMARY KEY AUTOINCREMENT,NOMBRECLIENTE VARCHAR(200),PRODUCTO VARCHAR(200),PRECIO FLOAT)")
    }

    override fun onUpgrade(bd: SQLiteDatabase, p1: Int, p2: Int) {
    }
}



