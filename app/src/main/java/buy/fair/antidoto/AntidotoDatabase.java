package buy.fair.antidoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ifrey on 7/11/16.
 */


public class AntidotoDatabase extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String PACKAGE = "buy.fair.antidoto";
    private static String DB_PATH = "/data/data/"+PACKAGE+"/databases/";
    private static String DB_NAME = "antidoto.sql";
    private final Context adContext;
    private static final String TAG = "Database";
    private static final int DATABASE_VERSION = 1;
    private static final String SP_KEY_DB_VER = "db_ver";


    public AntidotoDatabase(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.adContext = context;
        initialize();
    }

    /**
     * Initializes database. Creates database if doesn't exist.
     */
    private void initialize() {
        if (databaseExists()) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(adContext);
            int dbVersion = prefs.getInt(SP_KEY_DB_VER, 1);
            if (DATABASE_VERSION != dbVersion) {
                File dbFile = adContext.getDatabasePath(DB_NAME);
                if (!dbFile.delete()) {
                    Log.w(TAG, "Unable to update database");
                }
            }
        }
        if (!databaseExists()) {
            createDatabase();
        }
    }


    /**
     * Returns true if database file exists, false otherwise.
     * @return
     */
    private boolean databaseExists() {
        File dbFile = adContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    /**
     * Creates database by copying it from assets directory.
     */
    private void createDatabase() {
        String parentPath = adContext.getDatabasePath(DB_NAME).getParent();
        String path = adContext.getDatabasePath(DB_NAME).getPath();

        File file = new File(parentPath);
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.w(TAG, "Unable to create database directory");
                return;
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = adContext.getAssets().open(DB_NAME);
            os = new FileOutputStream(path);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(adContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(SP_KEY_DB_VER, DATABASE_VERSION);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    /****************************************************************************************************************
     ************************************* CURSOR BUSQUEDA DE BARCODES **********************************************
     ***************************************************************************************************************/

    /** Returns a JobDetailCursor for the specified jobId
     * @param barcode The barcode to check
     */
    public checkBarcodeCursor checkBarcodeCursor(long barcode) {
        String sql = checkBarcodeCursor.QUERY + barcode + ", e.barcode ) > 0";
        SQLiteDatabase d = getReadableDatabase();
        checkBarcodeCursor c = (checkBarcodeCursor) d.rawQueryWithFactory(
                new checkBarcodeCursor.Factory(),
                sql,
                null,
                null);
        c.moveToFirst();
        return c;
    }

    /**
     * Provides self-contained query-specific cursor for product/company and if its not fair.
     * The query and all Accessor methods are in the class.
     */
    public static class checkBarcodeCursor extends SQLiteCursor {
        /** The query for this cursor */
        private static final String QUERY =
                "SELECT r.name as reason_name, r.description as reason_description, r.url as reason_url, "+
                        "e.name as element_name, e.description as element_description, e.barcode as element_barcode, " +
                        "c.name as company_name " +
                        "FROM camps as ca, elements as e, reasons as r, companies as c "+
                        "WHERE  e._id= ca.element_id "+
                        "AND ca.reason_id = r._id " +
                        "AND c._id = e.company " +
                        "AND instr( ";
        /** Cursor constructor */
        private checkBarcodeCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }
        /** Private factory class necessary for rawQueryWithFactory() call */
        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                                    SQLiteCursorDriver driver, String editTable,
                                    SQLiteQuery query) {
                return new checkBarcodeCursor(db, driver, editTable, query);
            }
        }
        /* Accessor functions -- one per database column */
        //public long getColReasonsId() { return getLong(getColumnIndexOrThrow("reasons._id")); }
        public String getColReasonsName() { return getString(getColumnIndexOrThrow("reason_name")); }
        public String getColReasonsDescription() { return getString(getColumnIndexOrThrow("reason_description")); }
        public String getColReasonsUrl() { return getString(getColumnIndexOrThrow("reason_url")); }
        public String getColElementsName() { return getString(getColumnIndexOrThrow("element_name")); }
        public String getColElementsDescription() { return getString(getColumnIndexOrThrow("element_description")); }
        public int getColElementsBarcode() { return getInt(getColumnIndexOrThrow("element_barcode")); }
        public String getColCompanyName() { return  getString(getColumnIndexOrThrow("company_name")); }
    }


    /****************************************************************************************************************
     ************************************* CURSOR BUSQUEDA DE CADENAS ***********************************************
     ***************************************************************************************************************/

    /************************************************
     * EN ESTAS BUSQUEDAS AL BUSCARSE TAMBIEN POR EMPRESA, Y ALGUNAS ESTARAN EN EL BOICOT POR SI MISMAS SE NECESITA
     *  CREAR UN PRODUCTO QUE SE LLAME TODOS PARA AÑADIR LA EMPRESA A LOS BOICOTTS
     */

    /** Returns a JobDetailCursor for the specified jobId
     * @param searchString The String to search
     */
    public checkSearchStringCursor checkSearchStringCursor(String searchString) {
        String sql = checkSearchStringCursor.QUERY + searchString + "%\') " +
                "OR lower(e.description) like lower(\'%" + searchString + "%\') " +
                "OR ( " +
                    "lower(c.name) like lower(\'%" + searchString + "%\') " +
                    "AND e.company = c._id ) )" ;
        SQLiteDatabase d = getReadableDatabase();
        checkSearchStringCursor c = (checkSearchStringCursor) d.rawQueryWithFactory(
                new checkSearchStringCursor.Factory(),
                sql,
                null,
                null);
        c.moveToFirst();
        return c;
    }


    /**
     * Provides self-contained query-specific cursor for product/company and if its not fair.
     * The query and all Accessor methods are in the class.
     */
    public static class checkSearchStringCursor extends SQLiteCursor {
        /** The query for this cursor */
        private static final String QUERY =
                "SELECT r.name as reason_name, r.description as reason_description, r.url as reason_url, "+
                        "e.name as element_name, e.description as element_description, e.barcode as element_barcode, " +
                        "c.name as company_name " +
                        "FROM camps as ca, elements as e, reasons as r, companies as c "+
                        "WHERE  e._id= ca.element_id "+
                        "AND ca.reason_id = r._id " +
                        "AND ( lower(e.name) like lower(\'%";
        /** Cursor constructor */
        private checkSearchStringCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                                   String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }
        /** Private factory class necessary for rawQueryWithFactory() call */
        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                                    SQLiteCursorDriver driver, String editTable,
                                    SQLiteQuery query) {
                return new checkSearchStringCursor(db, driver, editTable, query);
            }
        }
        /* Accessor functions -- one per database column */
        //public long getColReasonsId() { return getLong(getColumnIndexOrThrow("reasons._id")); }
        public String getColReasonsName() { return getString(getColumnIndexOrThrow("reason_name")); }
        public String getColReasonsDescription() { return getString(getColumnIndexOrThrow("reason_description")); }
        public String getColReasonsUrl() { return getString(getColumnIndexOrThrow("reason_url")); }
        public String getColElementsName() { return getString(getColumnIndexOrThrow("element_name")); }
        public String getColElementsDescription() { return getString(getColumnIndexOrThrow("element_description")); }
        public int getColElementsBarcode() { return getInt(getColumnIndexOrThrow("element_barcode")); }
        public String getColCompanyName() { return  getString(getColumnIndexOrThrow("company_name")); }

    }
}
