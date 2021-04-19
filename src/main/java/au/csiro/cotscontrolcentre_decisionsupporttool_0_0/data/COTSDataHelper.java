package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fle125 on 27/03/2017.
 */

public class COTSDataHelper extends SQLiteOpenHelper {

    // We need to do some jiggery-pokery here to load the pre-existing cotsData.sqlite database into the app.
    // I'm not sure why - maybe the system needs to be register it as a data source or something - but I
    // tried just copying it to the correct directory of the Android file system itself and it didn't work.
    // All the advice online suggests this is the best way to do it, and it has the benefit that I can update
    // the database locally before uploading the APK with it compiled in.

    // The instance, in order to make the COTSDataHelper a singleton and avoid unclosed databases and memory leaks
//    private static COTSDataHelper sInstance;

    // The database path
    private static String DATABASE_PATH;

    // The database name
    private static final String DATABASE_NAME = "cotsData.sqlite";

    // If you change the database schema, you must increment the database version so that the onUpdate
    // function is called on next load.
    private static final int DATABASE_VERSION = 1;

    // Some private variables for what follows:

    private SQLiteDatabase mDatabase;

    private final Context mContext;

//
//    The following three functions are the compulsory functions that need to be overridden
//

//    public static synchronized COTSDataHelper getInstance(Context context) {
//
//        // Use the application context, which will ensure that you
//        // don't accidentally leak an Activity's context.
//        // See this article for more information: http://bit.ly/6LRzfx
//        if (sInstance == null) {
//            sInstance = new COTSDataHelper(context.getApplicationContext());
//        }
//        return sInstance;
//    }


    // Make constructor private to calculate the DATABASE_PATH in the public version below before calling super

    public COTSDataHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        this.mContext = context;
        boolean dbexist = checkDatabase();
        if(dbexist)
        {
            openDatabase();
        }
        else
        {
            createDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDatabase() {

        boolean dbExist = checkDatabase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDatabase();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

    }

//
//  The following functions are informed by knowledgeable internet sources about loading pre-existing
//  data into an app
//

//  Check if the database already exist to avoid re-copying the file each time you open the application.
//  @return true if it exists, false if it doesn't
    private boolean checkDatabase(){

        boolean checkdb = false;
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            checkdb = dbfile.exists();

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

//        return checkDB != null ? true : false;
        return checkdb;
    }

//  Copies your database from your local assets-folder to the just created empty database in the
//  system folder, from where it can be accessed and handled. This is done by transferring byte
//  streams.
    private void copyDatabase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DATABASE_PATH + DATABASE_NAME;

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

    public SQLiteDatabase openDatabase() throws android.database.sqlite.SQLiteException{

        //Open the database
        String myPath = DATABASE_PATH + DATABASE_NAME;
        mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        return mDatabase;

    }

    @Override
    public synchronized void close() {

        if(mDatabase != null) {
            mDatabase.close();
        }

        super.close();

    }

}
