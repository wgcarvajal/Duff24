package duff24.com.duff24.basededatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by geovanny on 11/01/16.
 */
public class AdminSQliteOpenHelper extends SQLiteOpenHelper
{
    public static int v = 3;

    public static AdminSQliteOpenHelper crearSQLite(Context context)
    {
        return  new AdminSQliteOpenHelper(context,"admin", null, v);
    }

    public AdminSQliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table pedido(prodid text, prodcantidad integer, prodprecio integer, prodnombreesp text, prodnombreing text, proddescripcioning text, proddescripcionesp text , prodsiningredientes text, prodsiningredientesing text, PRIMARY KEY ( prodid, prodsiningredientes))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.e("se borro BD:", "nueva:"+newVersion);
        Log.e("vieja version", ":"+oldVersion);
        db.execSQL("drop table if exists pedido");
        db.execSQL("create table pedido(prodid text, prodcantidad integer, prodprecio integer, prodnombreesp text, prodnombreing text, proddescripcioning text, proddescripcionesp text , prodsiningredientes text, prodsiningredientesing text, PRIMARY KEY ( prodid, prodsiningredientes))");
    }
}
