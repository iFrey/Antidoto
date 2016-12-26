package buy.fair.antidoto;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ifrey on 7/11/16.
 */


public class AntidotoDBHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String PACKAGE = "buy.fair.antidoto";
    private static String DB_PATH = "/data/data/"+PACKAGE+"/databases/";
    private static String DB_NAME = "antidoto.sql";
    private SQLiteDatabase adDataBase;
    private final Context adContext;
    private static final String TAG = "Database";




    public AntidotoDBHelper(Context context) {
        super(context, DB_NAME, null,1);
        this.adContext = context;
    }



    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = adContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        adDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(adDataBase != null)
            adDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createDataBase();
        }catch (IOException ex){
            Log.e(TAG, "IOException creating database: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.



    /** Returns a JobDetailCursor for the specified jobId
     * @param jobId The _id of the job
     */
    public checkRightCursor checkRight(long jobId) {
        String sql = checkRightCursor.QUERY + jobId;
        SQLiteDatabase d = getReadableDatabase();
        checkRightCursor c = (checkRightCursor) d.rawQueryWithFactory(
                new checkRightCursor.Factory(),
                sql,
                null,
                null);
        c.moveToFirst();
        return c;
    }

    /**
     * Provides self-contained query-specific cursor for Job Detail.
     * The query and all Accessor methods are in the class.
     */
    public static class checkRightCursor extends SQLiteCursor {
        /** The query for this cursor */
        private static final String QUERY =
                "SELECT jobs._id, employers._id, employers.website, title," +
                        " description, start_time, end_time, employer_name, " +
                        "contact_name, rating, street, city, state, zip, phone, " +
                        "email, latitude, longitude, status FROM jobs, employers "+
                        "WHERE jobs.employer_id = employers._id "+
                        "AND jobs._id = ";
        /** Cursor constructor */
        private checkRightCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
                                String editTable, SQLiteQuery query) {
            super(db, driver, editTable, query);
        }
        /** Private factory class necessary for rawQueryWithFactory() call */
        private static class Factory implements SQLiteDatabase.CursorFactory{
            @Override
            public Cursor newCursor(SQLiteDatabase db,
                                    SQLiteCursorDriver driver, String editTable,
                                    SQLiteQuery query) {
                return new checkRightCursor(db, driver, editTable, query);
            }
        }
        /* Accessor functions -- one per database column */
        public long getColJobsId() {
            return getLong(getColumnIndexOrThrow("jobs._id"));
        }
        public long getColEmployersId() {
            return getLong(getColumnIndexOrThrow("employers._id"));
        }
        public String getColWebsite() {
            return getString(getColumnIndexOrThrow("employers.website"));
        }
        public String getColTitle() {
            return getString(getColumnIndexOrThrow("title"));
        }
        public String getColDescription() {
            return getString(getColumnIndexOrThrow("description"));
        }
        public long getColStartTime() {
            return getLong(getColumnIndexOrThrow("start_time"));
        }
        public long getColEndTime() {
            return getLong(getColumnIndexOrThrow("end_time"));
        }
        public String getColEmployerName() {
            return getString(getColumnIndexOrThrow("employer_name"));
        }
        public String getColContactName() {
            return getString(getColumnIndexOrThrow("contact_name"));
        }
        public long getColRating() {
            return getLong(getColumnIndexOrThrow("rating"));
        }
        public String getColStreet() {
            return getString(getColumnIndexOrThrow("street"));
        }
        public String getColCity() {
            return getString(getColumnIndexOrThrow("city"));
        }
        public String getColState() {
            return getString(getColumnIndexOrThrow("state"));
        }
        public String getColZip(){
            return getString(getColumnIndexOrThrow("zip"));
        }
        public String getColPhone() {
            return getString(getColumnIndexOrThrow("phone"));
        }
        public String getColEmail(){
            return getString(getColumnIndexOrThrow("email"));
        }
        public long getColLatitude() {
            return getLong(getColumnIndexOrThrow("latitude"));
        }
        public long getColLongitude() {
            return getLong(getColumnIndexOrThrow("longitude"));
        }
        public long getColStatus() {
            return getLong(getColumnIndexOrThrow("status"));
        }
    }
}
