package au.csiro.cotscontrolcentre_decisionsupporttool_0_0;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Dive;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.DiveList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Manta;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.MantaList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Reef;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Rhis;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Voyage;

public class LoadNewData {
//TODO: Set this up as a headless fragment so loading is not affected by orientation change

    //    extends Fragment  {

//    @Override
//    public void onCreate( Bundle savedInstanceState ) {
//
//        super.onCreate( savedInstanceState );
//
//        //TODO: Double check if this is what we really want
//        setRetainInstance( true );
//
//    }

    public static void loadNewData( Context context, List<Reef> reefList, MantaList mantaList ) {

        //
        // Android now makes it painfully difficult to just read a folder on an SD card, because it
        // wants to enforce filesystem use to maintain security. This is not useful for us, because
        // we are trying to interact between apps that were developed when you were allowed to read
        // the sdCard directly, and we don't have access to the apps that create the files that are
        // stored there. So, we need a workaround.
        //
        // The workaround is clunky, and depends on several assumptions, which is a non-ideal
        // situation. In the medium-term, we should get ThinkSpatial to recode their apps to use
        // a Uri based file store that the CCC DST app can access.
        //
        // In the short term, we search the items in the directory /storage for the folder with a
        // name of the form ####-####, where the # represent numerals.
        //
        // TODO: Coordinate with ThinkSpatial to transition shared files to a Uri-based file store
        //

        File storageDirectory = new File( "/storage" );

        File[] storageDirectoryFolders = storageDirectory.listFiles();

        File sdCardStorageDirectory = new File("" );

//        // Check all the files and folders within the storage parent directory
        for( File storageDirectoryFolder : storageDirectoryFolders ) {

            if ( storageDirectoryFolder.getAbsolutePath().matches( "(.*\\p{XDigit}{4}-\\p{XDigit}{4})" ) ){

                sdCardStorageDirectory = storageDirectoryFolder;

            }

        }

        File cullFile = new File( sdCardStorageDirectory, "/Android/data/au.gov.gbrmpa.cots.capture/files/culldata.db");
        File surveillanceFile = new File( sdCardStorageDirectory, "/Android/data/au.gov.gbrmpa.cots.surveillance/files/surveillance.db");
        File rhisFile = new File( sdCardStorageDirectory, "/Android/data/au.gov.gbrmpa.cots.rhis/files/rhisdata.db");

        String DATABASE_PATH = "/data/data/" + "au.csiro.cotscontrolcentre_decisionsupporttool_0_0" + "/" + "databases/";
        File dbFile = new File( DATABASE_PATH,"cotsData.sqlite");
        SQLiteDatabase db = SQLiteDatabase.openDatabase( dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE );

        DiveList newCullData = loadCullData( cullFile, db );
        MantaList newMantaData = loadSurveillanceData( surveillanceFile, db );
//        List<Rhis> newRhisData = loadRhisData( rhisFile, db );

        for ( Dive dive: newCullData ) {

            if ( dive != null ){

                addCullDataToAppDatabase(dive, db);

            }

        }

        for ( Manta manta: newMantaData ) {

            if ( manta != null ) {

                addMantaDataToAppDatabase(manta, db);

            }

        }

        db.close();

    }

    private static DiveList loadCullData( File file, SQLiteDatabase db ){

        SQLiteDatabase divedb = SQLiteDatabase.openDatabase( file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY );
        Cursor divedbCursor = divedb.rawQuery("SELECT * FROM culldata",null);
        DiveList returnDiveList = new DiveList();

        while ( divedbCursor.moveToNext() ) {

            // Load json
            String diveJSONString = divedbCursor.getString( 1 );

            try {

                JSONObject diveJSONObject = new JSONObject(diveJSONString);

                // Process json
                returnDiveList.add( convertJSONtoDive( diveJSONObject, db ) );

            } catch ( Throwable t ){

                Log.e("My App", "Could not parse malformed JSON: \"" + diveJSONString + "\"");

            }

        }

        divedb.close();

        return returnDiveList;

    }

    private static MantaList loadSurveillanceData( File file, SQLiteDatabase db ){

        SQLiteDatabase surveillancedb = SQLiteDatabase.openDatabase( file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY );
        Cursor surveillancedbCursor = surveillancedb.rawQuery("SELECT * FROM surveillance",null);
        MantaList returnMantaList = new MantaList();

        while ( surveillancedbCursor.moveToNext() ) {

            // Load json
            String surveillanceJSONString = surveillancedbCursor.getString( 1 );

            try {

                JSONObject mantaJSONObject = new JSONObject(surveillanceJSONString);

                // Process json
                returnMantaList.add( convertJSONtoManta( mantaJSONObject, db ) );

            } catch ( Throwable t ){

                Log.e("My App", "Could not parse malformed JSON: \"" + surveillanceJSONString + "\"");

            }

        }

        surveillancedb.close();

        return returnMantaList;

    }

    // TODO: Need to load new Site definitions


    ///////////////    METHODS TO CONVERT THINKSPATIAL JSON INTO COTSCONTROLCENTRE CUSTOM TYPES

    private static Dive convertJSONtoDive( JSONObject jsonObject, SQLiteDatabase db ){

        try {

            int diveId = 0; //Need to generate this as we add it to the SQLTable;
            String diveDate = jsonObject.getString("divedate").substring( 0, 10);;
            double diveAverageDepth = jsonObject.getDouble("depth");
            int diveBottomTime = jsonObject.getInt("bottomTime");
            int diveLessThanFifteenCentimetres = jsonObject.getJSONArray("cohorts").getInt(0);
            int diveFifteenToTwentyFiveCentimetres = jsonObject.getJSONArray("cohorts").getInt(1);
            int diveTwentyFiveToFortyCentimetres = jsonObject.getJSONArray("cohorts").getInt(2);
            int diveGreaterThanFortyCentimetres = jsonObject.getJSONArray("cohorts").getInt(3);
            // We don't really want to use the ThinkSpatial assignment of manta tows to Sites
            // because we don't know how they implemented it, it's assumptions or limitations. To
            // use our own method of assigning manta tows to their nearest Site we need the reef at
            // which the manta tow was collected, and the mean lat and long of the manta tow.
            //
            // We have an issue here because not all manta tows are assigned to cull sites by the
            // ThinkSpatial app, which is fair enough - but if they are not assigned to a cull site
            // the ThinkSpatial app also does not export which reef they are assigned to, with the
            // exception of the info of the internal ThinkSpatial reef id code, which is not fair
            // enough at all, and makes our job almost impossible. The best workarounds I can think
            // of right now are: 1) (fast) given that other mantas will generally be generated at
            // the same reef on the same import, I could maintain a list of the recent internal
            // ThinkSpatial reef_ids and actual reef names from other rows imported during the
            // current import, and use that to figure out the actual reef; or 2) (horrifically slow)
            // write a method that imports all reef polygons and checks which one the manta tow
            // intersects. Sigh.
            //
            //TODO: Compensate for ThinkSpatial's borked manta export when the manta is not assigned to a Site
            //
            // The other thing we should do here is use our
            String siteName = jsonObject.getJSONObject("cullzone").getString("name");
            int siteId = findSiteIdFromSiteName( siteName, db );
            String vesselName = jsonObject.getJSONObject("voyage").getString("vessel");
            int vesselVoyage = jsonObject.getJSONObject("voyage").getInt("title");
            String voyageStartDate = jsonObject.getJSONObject("voyage").getString("start");
            String voyageStopDate = jsonObject.getJSONObject("voyage").getString("end");
            int voyageId = findVoyageIdFromVesselNameAndVoyageNumber( vesselName, vesselVoyage, voyageStartDate, voyageStopDate, db );

            return new Dive( diveId, diveDate, diveAverageDepth, diveBottomTime, diveLessThanFifteenCentimetres, diveFifteenToTwentyFiveCentimetres, diveTwentyFiveToFortyCentimetres, diveGreaterThanFortyCentimetres, siteId, voyageId );

        } catch ( Throwable t ){

            Log.e("CCC_JSON", "Could not parse malformed JSON: \"" + jsonObject + "\"");

        }

        return null;

    }

    private static Manta convertJSONtoManta( JSONObject jsonObject, SQLiteDatabase db ){

        try {

            int mantaId = 0; //Need to generate this as we add it to the SQLTable;
            String mantaDate = jsonObject.getString("towDate").substring( 0, 10);
            JSONArray mantaTowLine = jsonObject.getJSONObject( "towLine" ).getJSONObject( "geometry" ).getJSONArray( "coordinates" );
            double mantaStartLat = ( (JSONArray) mantaTowLine.get(0) ).getDouble(1);
            double mantaStartLong =  ( (JSONArray) mantaTowLine.get(0) ).getDouble(0);
            double mantaStopLat =   ( (JSONArray) mantaTowLine.get(mantaTowLine.length() - 1) ).getDouble(1);
            double mantaStopLong = ( (JSONArray) mantaTowLine.get(mantaTowLine.length() - 1) ).getDouble(0);
            double mantaMeanLat = (mantaStartLat + mantaStopLat) / 2;
            double mantaMeanLong = (mantaStartLong + mantaStopLong) / 2;
            int mantaCots = jsonObject.getInt("cotsSeen");
            String mantaScars = jsonObject.getString("scarsSeen");
            String siteName = ( (JSONObject) jsonObject.getJSONArray("cullzones").get(0) ).getString("name");
            int siteId = findSiteIdFromSiteName( siteName, db );
            String vesselName = jsonObject.getJSONObject("voyage").getString("vessel");
            int vesselVoyage = jsonObject.getJSONObject("voyage").getInt("title");
            String voyageStartDate = jsonObject.getJSONObject("voyage").getString("start").substring( 0, 10);
            String voyageStopDate = jsonObject.getJSONObject("voyage").getString("end").substring( 0, 10);
            int voyageId = findVoyageIdFromVesselNameAndVoyageNumber( vesselName, vesselVoyage, voyageStartDate, voyageStopDate, db );

            return new Manta( mantaId, mantaDate, mantaStartLat, mantaStartLong, mantaStopLat, mantaStopLong, mantaMeanLat, mantaMeanLong, mantaCots, mantaScars, siteId, voyageId );

        } catch ( Throwable t ){

            Log.e("CCC_JSON", "Could not parse malformed JSON: \"" + jsonObject + "\"");

        }

        return null;

    }

    private static Rhis convertJSONtoRhis(JSONObject jsonObject, SQLiteDatabase db ){

        try {

            int rhisId = 0; //Need to generate this as we add it to the SQLTable;
            String rhisDate = jsonObject.getJSONObject("static").getString("surveydate").substring( 0, 10);
            double rhisCoralCover = jsonObject.getJSONObject("Benthos Observations").getInt("Live Coral");
            int rhisCOTSAdult = 0;
            int rhisCOTSJuvenile = 0;
            JSONArray predatorObservations = jsonObject.getJSONArray("Predator Observations");
            for ( int i = 0; i < predatorObservations.length(); i++ ){

                if( ( (JSONObject) predatorObservations.get(i) ).getJSONObject("Observations").equals( "Adults" ) ) {

                    rhisCOTSAdult = ( (JSONObject) predatorObservations.get(i) ).getInt("COTS");

                };

                if( ( (JSONObject) predatorObservations.get(i) ).getJSONObject("Observations").equals( "Juveniles" ) ) {

                    rhisCOTSJuvenile = ( (JSONObject) predatorObservations.get(i) ).getInt("COTS");

                };

            }
            String rhisVisibility = jsonObject.getJSONObject("static").getString("visibility");
            int siteId = 0; //Need to generate this as we add it to the SQLTable;


            return new Rhis( rhisId, rhisDate, rhisCoralCover, rhisCOTSAdult, rhisCOTSJuvenile, rhisVisibility, siteId );

        } catch ( Throwable t ){

            Log.e("CCC_JSON", "Could not parse malformed JSON: \"" + jsonObject + "\"");

        }

        return null;

    }

    ///////////////    METHODS TO LOOK UP CURRENT DATABASE TO CROSS-REFERENCE

    private static int findSiteIdFromSiteName( String siteName, SQLiteDatabase db ) {

       String query =
                "SELECT " +
                        COTSDataContract.SiteEntry.SITE_TABLE_NAME + "." + COTSDataContract.SiteEntry.SITE_TABLE_COLUMN_ID +
                        " FROM " +
                        COTSDataContract.SiteEntry.SITE_TABLE_NAME +
                        " WHERE " +
                        COTSDataContract.SiteEntry.SITE_TABLE_NAME + "." + COTSDataContract.SiteEntry.SITE_TABLE_COLUMN_SITE_NAME + " = '" + siteName + "'";

        Cursor dbCursor = db.rawQuery( query,null);

        int returnSiteId = 0;

        // In theory, a cursor with either 0 rows, if the Site does not already exist, or at most
        // one row, if the Site does already exist, should be returned. If the Site does
        // not already exist, we create it and record the siteId of the new record. If it does,
        // we look up its siteId.
        while ( dbCursor.moveToNext() ) {

            returnSiteId = dbCursor.getInt( dbCursor.getColumnIndex( COTSDataContract.SiteEntry.SITE_TABLE_COLUMN_ID  ) );

        }

        return returnSiteId;

    }

    private static int findVoyageIdFromVesselNameAndVoyageNumber( String vesselName, int voyageNumber, String voyageStartDate, String voyageStopDate, SQLiteDatabase db ) {

        String query =
                "SELECT " +
                        COTSDataContract.VoyageEntry.VOYAGE_TABLE_NAME + "." + COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_ID +
                        " FROM " +
                        COTSDataContract.VoyageEntry.VOYAGE_TABLE_NAME +
                        " LEFT JOIN " +
                        COTSDataContract.VesselEntry.VESSEL_TABLE_NAME +
                        " ON " +
                        COTSDataContract.VesselEntry.VESSEL_TABLE_NAME + "." + COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_ID +
                        " = " +
                        COTSDataContract.VoyageEntry.VOYAGE_TABLE_NAME + "." + COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_ID +
                        " WHERE " +
                        COTSDataContract.VesselEntry.VESSEL_TABLE_NAME + "." + COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_VESSEL_NAME + " = '" + vesselName + "'" +
                        " AND " +
                        COTSDataContract.VoyageEntry.VOYAGE_TABLE_NAME + "." + COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_VOYAGE_NUMBER + " = " + voyageNumber;

        Cursor dbCursor = db.rawQuery( query,null);

        int returnVoyageId = 0;

        // In theory, a cursor with either 0 rows, if the voyage does not already exist, or at most
        // one row, if the voyage does already exist, should be returned. If the voyage does
        // not already exist, we create it and record the voyageId of the new record. If it does,
        // we look up its voyageId.
        if ( dbCursor.moveToNext() == false ) {

            String query2 =
                    "SELECT " +
                        COTSDataContract.VesselEntry.VESSEL_TABLE_NAME + "." + COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_ID +
                        " FROM " +
                        COTSDataContract.VesselEntry.VESSEL_TABLE_NAME +
                        " WHERE " +
                        COTSDataContract.VesselEntry.VESSEL_TABLE_NAME + "." + COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_VESSEL_NAME + " = '" + vesselName +"'";

            Cursor dbCursor2 = db.rawQuery( query2,null);

            int vesselId = 0;

            while ( dbCursor2.moveToNext() ) {

                vesselId = dbCursor2.getInt( dbCursor2.getColumnIndex( COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_ID  ) );

            }

            Voyage newVoyage = new Voyage( 0, voyageNumber, voyageStartDate, voyageStopDate, vesselId );

            returnVoyageId = (int) addVoyageDataToAppDatabase( newVoyage, db );

        } else {

            returnVoyageId = dbCursor.getInt( dbCursor.getColumnIndex( COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_ID  ) );

        }

        // In practice, however, we'd often expect the Voyage to not currently exist in the main
        // SQL database yet, so we have to add it, and get the new row number / id from the new
        // entry to link our Dive and Manta and RHIS records to the new voyage

        return returnVoyageId;

    }

    ///////////////    METHODS TO ADD NEW RECORDS TO SQLITE DATABASE
    //TODO: If manta can't be associated to a Site, don't enter it into the database

    private static long addVoyageDataToAppDatabase( Voyage voyage, SQLiteDatabase db ){

        // Create a new map of values, where column names are the keys
        ContentValues newVoyageEntryValues = new ContentValues();

        // mantaId: Note that the new __id field is created by adding the entry to the VoyageTable;
        newVoyageEntryValues.put( COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_VOYAGE_NUMBER, voyage.getVoyageNumber() );
        newVoyageEntryValues.put( COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_START_DATE, voyage.getStartDate() );
        newVoyageEntryValues.put( COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_STOP_DATE, voyage.getStopDate() );
        newVoyageEntryValues.put( COTSDataContract.VoyageEntry.VOYAGE_TABLE_COLUMN_VESSEL_ID, voyage.getVesselId() );

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert( COTSDataContract.VoyageEntry.VOYAGE_TABLE_NAME, null, newVoyageEntryValues);

        return newRowId;

    }

    private static long addCullDataToAppDatabase( Dive dive, SQLiteDatabase db ){

        // Load db

        // Create a new map of values, where column names are the keys
        ContentValues newDiveEntryValues = new ContentValues();

        // diveId: Note that the new __id field is created by adding the entry to the DiveTable;
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_DATE, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_AVERAGE_DEPTH, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_BOTTOM_TIME, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_LESS_THAN_FIFTEEN_CENTIMETRES, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_FIFTEEN_TO_TWENTY_FIVE_CENTIMETRES, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_TWENTY_FIVE_TO_FORTY_CENTIMETRES, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_GREATER_THAN_FORTY_CENTIMETRES, dive.getDiveDate() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_SITE_ID, dive.getSiteId() );
        newDiveEntryValues.put( COTSDataContract.DiveEntry.DIVE_TABLE_COLUMN_VOYAGE_ID, dive.getVoyageId() );

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert( COTSDataContract.DiveEntry.DIVE_TABLE_NAME, null, newDiveEntryValues);

        return newRowId;

    }

    private static long addMantaDataToAppDatabase( Manta manta, SQLiteDatabase db ){

        // Create a new map of values, where column names are the keys
        ContentValues newMantaEntryValues = new ContentValues();

        // mantaId: Note that the new __id field is created by adding the entry to the MantaTable;
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_DATE, manta.getMantaDate() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_START_LAT, manta.getMantaStartLat() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_START_LONG, manta.getMantaStartLong() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_STOP_LAT, manta.getMantaStopLat() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_STOP_LONG, manta.getMantaStopLong() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_MEAN_LAT, manta.getMantaMeanLat() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_MEAN_LONG, manta.getMantaMeanLong() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_COTS, manta.getMantaCOTS() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_SCARS, manta.getMantaScars() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_SITE_ID, manta.getSiteId() );
        newMantaEntryValues.put( COTSDataContract.MantaEntry.MANTA_TABLE_COLUMN_VOYAGE_ID, manta.getVoyageId() );

        //TODO: Test whether entry is already in database

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert( COTSDataContract.MantaEntry.MANTA_TABLE_NAME, null, newMantaEntryValues);

        return newRowId;


    }

    private static long addRhisDataToAppDatabase( Rhis rhis, SQLiteDatabase db ){

        // Create a new map of values, where column names are the keys
        ContentValues newRhisEntryValues = new ContentValues();

        // rhisId: Note that the new __id field is created by adding the entry to the DiveTable;
        newRhisEntryValues.put( COTSDataContract.RhisEntry.RHIS_TABLE_COLUMN_DATE, rhis.getRhisDate() );
        newRhisEntryValues.put( COTSDataContract.RhisEntry.RHIS_TABLE_COLUMN_AVERAGE_CORAL_COVER, rhis.getRhisCoralCover() );
        newRhisEntryValues.put( COTSDataContract.RhisEntry.RHIS_TABLE_COLUMN_COTS_ADULTS, rhis.getRhisCOTSAdults() );
        newRhisEntryValues.put( COTSDataContract.RhisEntry.RHIS_TABLE_COLUMN_COTS_JUVENILES, rhis.getRhisCOTSJuveniles() );
        newRhisEntryValues.put( COTSDataContract.RhisEntry.RHIS_TABLE_COLUMN_VISIBILITY, rhis.getRhisVisibility() );
        // siteId: Note that the new linked siteId field is created by cross referencing with the SiteTable;

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert( COTSDataContract.RhisEntry.RHIS_TABLE_NAME, null, newRhisEntryValues);

        return newRowId;

    }

}
