package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.ReefEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.ReefPolygonsEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.SiteEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.SitePolygonsEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.VesselEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.VoyageEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.DiveEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.MantaEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.RhisEntry;

//
// The COTSDataProvider class is where the main app cotsData.sqlite database is queried to load
// data into the custom Types of the app. It is important because much of the selection of the
// data that is displayed and processed occurs here, prior to analysis within the main class
// methods.
//
// The COTSDataProvider was established at a point where it was expected the COTS Control Centre
// would leverage extensive communication between multiple apps. Since then, better
// compartmentalisation between user tasks has reduced this requirement. As a result, although the
// framework for the COTSDataProvider is established, it is not currently leveraged efficiently,
// instead relying on multiple custom queries targeted at the immediate needs of the main COTS-DST
// app. In future, this will be restructures to provide a consistent approach to querying across
// all data types, with appropriate query builders etc.
//
// TODO: Restructure to have a consistent approach to querying, in terms of: 1) which arguments are
//  hardcoded into loaders; 2) which are accessed via "selection"; and 3) which are accessed via a
//  combination of "selection" and "selectionArgs"
//

public class COTSDataProvider extends ContentProvider {

    //
    // DEFINE DATAPROVIDER IDS
    // These are just human-readable integer variables that can be used in the switch statements
    // throughout this class. The only goal is to provide a unique integer for each type of
    // query we want to consider, so we space them out to give ourselves room to add additional
    // options if necessary as development continues
    //

    // REEFS - starting at 100
    public static final int CODE_REEFS_ALL = 100;
    public static final int CODE_REEFS_CONTROLLED = 101;

    // REEFPOLYGONS - starting at 150
    public static final int CODE_REEFPOLYGONS = 150;

    // SITES - starting at 200
    public static final int CODE_SITES_ALL = 200;
    public static final int CODE_SITES_WITH_SITENAME = 201;

    // SITEPOLYGONS - starting at 250
    public static final int CODE_SITEPOLYGONS = 250;

    // VESSELS - starting at 300
    public static final int CODE_VESSELS_ALL = 300;

    // VOYAGES - starting at 400
    public static final int CODE_VOYAGES_ALL = 400;

    // DIVES - starting at 500
    public static final int CODE_DIVES_ALL = 500;
    public static final int CODE_DIVES_AT_REEF = 501;
    public static final int CODE_DIVES_AT_SITE = 502;

    // MANTAS - starting at 600
    public static final int CODE_MANTAS_ALL = 600;
    public static final int CODE_MANTAS_AT_REEF = 601;

    //RHIS - starting at 700
    public static final int CODE_RHIS_ALL = 700;

    //
    // DEFINE PRIVATE CLASS VARIABLES
    //
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    // This method provides a "UriMatcher" to determine which query to run based on the Uri used
    // to call the COTSDataProvider. That is, it converts a Uri like
    // "au.csiro.cotscontrolcentre_decisionsupporttool_0_0/reefs/" into one of the human-readable
    // DATAPROVIDER ID integers above. In practice, this isn't heavily leveraged yet, but it's a
    // necessary component of a DataProvider.
    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = COTSDataContract.CONTENT_AUTHORITY;

//        Create a code for each type of URI.

        //
        // REEFS
        //

        // All Reefs: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/reefs/
        matcher.addURI(
                authority,
                ReefEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_REEFS_ALL
        );

        // Controlled Reefs: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/reefs/controlled/
        matcher.addURI(
                authority,
                ReefEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath() + "/controlled/",
                CODE_REEFS_CONTROLLED
        );


        //
        // REEF POLYGONS
        //

        // Reef Polygons from selected Reef: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/reefpolygons/
        matcher.addURI(
                authority,
                ReefPolygonsEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_REEFPOLYGONS
        );

        //
        // SITES
        //

        // All sites: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/sites/
        matcher.addURI(
                authority,
                SiteEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_SITES_ALL
        );

        // All sites: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/sites/where/sitename/*siteName*
        matcher.addURI(
                authority,
                SiteEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath()
                        + "/where/sitename/",
                CODE_SITES_WITH_SITENAME
        );

        //
        // SITEPOLYGONS
        //

        // SITE Polygons from selected sITE: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/sitepolygons/
        matcher.addURI(
                authority,
                SitePolygonsEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_SITEPOLYGONS
        );

        //
        // VESSELS
        //

        // All sites: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/vessels/
        matcher.addURI(
                authority,
                VesselEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_VESSELS_ALL
        );

        //
        // VOYAGES
        //

        // All voyages: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/voyages/
        matcher.addURI(
                authority,
                VoyageEntry.CONTENT_URI
                .buildUpon()
                .build()
                .getPath(),
                CODE_VOYAGES_ALL
        );

        //
        // DIVES
        //

        // All dives: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/dives/
        matcher.addURI(
                authority,
                DiveEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_DIVES_ALL
        );

        // Dives at Reef: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/dives/where/reef/*reefId*
        matcher.addURI(
                authority,
                DiveEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath()
                        + "/where/reef/",
                CODE_DIVES_AT_REEF
        );

        // Dives at Site: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/dives/where/sitename/*siteName*
        matcher.addURI(
                authority,
                DiveEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath()
                        + "/where/site/",
                CODE_DIVES_AT_SITE
        );


        //
        // MANTAS
        //

        // All Manta tows: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/mantas/
        matcher.addURI(
                authority,
                MantaEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_MANTAS_ALL
        );

        // Mantas at Reef: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/mantas/where/reef/*reefId*
        matcher.addURI(
                authority,
                MantaEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath()
                        + "/where/reef/",
                CODE_MANTAS_AT_REEF
        );

        //
        // RHIS
        //

        // All RHIS: au.csiro.cotscontrolcentre_decisionsupporttool_0_0/rhis/
        matcher.addURI(
                authority,
                RhisEntry.CONTENT_URI
                        .buildUpon()
                        .build()
                        .getPath(),
                CODE_RHIS_ALL
        );

        return matcher;
    }


    @Override
    public boolean onCreate() {
//      Note that onCreate is run on the main thread, so performing any lengthy operations will
//      cause app lag.

        return true;

    }


//    The query function is where we extract records from the database to populate the various
//    parts of our app. We open the database in read only mode when making a query. We use a
//    rawQuery rather than a query because some of our queries are too complicated for the Android
//    query function, and because I read that it is supposed to prevent SQL injection risks,
//    although I haven't verified that claim. In my opinion it's also easier for someone who knows
//    SQL to read and maintain.
//
//    In future this may be rewritten to make use of the modular nature of the Uri design so that
//    an explicit query does not need to be written out for every case, but for now we just do
//    what needs to be done
//
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        COTSDataHelper COTSDataHelper = new COTSDataHelper(getContext());

        Cursor cursor;

        switch ( sUriMatcher.match(uri) ) {

            //
            // REEFS
            //

            case CODE_REEFS_ALL: {

                String query =
                        // We select all rows from REEFS_TABLE_NAME
                        "SELECT * FROM " + ReefEntry.REEF_TABLE_NAME;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            case CODE_REEFS_CONTROLLED: {

                String query =
                        // We select all rows from REEFS_TABLE_NAME at which there are Dive and
                        // Manta records, through the use of the INNER JOIN function.
                        // TODO: Change this query to select Reefs with Manta OR Dive, not Manta
                        //  AND Dive
                        "SELECT DISTINCT " +
                         ReefEntry.REEF_TABLE_NAME + ".* " +
                        "FROM " +
                         SiteEntry.SITE_TABLE_NAME +
                         " INNER JOIN " + DiveEntry.DIVE_TABLE_NAME + " ON " + DiveEntry.DIVE_TABLE_NAME + "." + DiveEntry.DIVE_TABLE_COLUMN_SITE_ID + "=" + SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_ID +
                         " INNER JOIN " + ReefEntry.REEF_TABLE_NAME + " ON " + ReefEntry.REEF_TABLE_NAME + "." + ReefEntry.REEF_TABLE_COLUMN_ID + "=" + SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_REEF_ID;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // REEFPOLYGONS
            //
            case CODE_REEFPOLYGONS: {

                String query;

                if ( selection.isEmpty() ) {

                    cursor = null;
                    break; /* We don't want to return a cursor for all Reefs, it would contain too many items to be useful */

                }
                {

                    query = "SELECT * FROM " + ReefPolygonsEntry.REEF_POLYGONS_TABLE_NAME + " WHERE " + selection;

                }

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // SITES
            //

            case CODE_SITES_ALL: {

                String query;

                String basequery =
                        "SELECT DISTINCT " +
                        SiteEntry.SITE_TABLE_NAME + ".*" +
                        " FROM " + SiteEntry.SITE_TABLE_NAME +
                        " LEFT JOIN " + DiveEntry.DIVE_TABLE_NAME + " ON " + DiveEntry.DIVE_TABLE_NAME + "." + DiveEntry.DIVE_TABLE_COLUMN_SITE_ID + " = " + SiteEntry.SITE_TABLE_NAME +"." + SiteEntry.SITE_TABLE_COLUMN_ID +
                        " LEFT JOIN " + MantaEntry.MANTA_TABLE_NAME + " ON " + MantaEntry.MANTA_TABLE_NAME + "." + MantaEntry.MANTA_TABLE_COLUMN_SITE_ID + " = " + SiteEntry.SITE_TABLE_NAME +"." + SiteEntry.SITE_TABLE_COLUMN_ID;

                String groupby = " GROUP BY " + SiteEntry.SITE_TABLE_NAME +"." + SiteEntry.SITE_TABLE_COLUMN_ID;

                if ( selection.isEmpty() ) {

                    query = basequery + groupby;

                } else {

                    query = basequery + " WHERE " + selection + groupby;

                }

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            case CODE_SITES_WITH_SITENAME: {

                String query;

                String basequery = "SELECT " +
                        SiteEntry.SITE_TABLE_NAME +
                        " FROM " + SiteEntry.SITE_TABLE_NAME;

                if ( selection.isEmpty() ) {

                    query = basequery;

                } else {

                    query = basequery + " WHERE " + selection;

                }

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // VESSELS
            //

            case CODE_VESSELS_ALL: {

                String query =
                        // We select all rows from SITES_TABLE_NAME
                        "SELECT * FROM " + VesselEntry.VESSEL_TABLE_NAME;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;
            }

            //
            // SITEPOLYGONS
            //

            case CODE_SITEPOLYGONS: {

                String query;

                if ( selection.isEmpty() ) {

                    cursor = null;
                    break; /* We don't want to return a cursor for all Sites, it would contain too many items to be useful */

                }
                {
                    query =
                            "SELECT DISTINCT " +
                            SitePolygonsEntry.SITE_POLYGONS_TABLE_NAME + ".* " +
                            "FROM " +
                            SitePolygonsEntry.SITE_POLYGONS_TABLE_NAME +
                            " LEFT JOIN " + SiteEntry.SITE_TABLE_NAME + " ON " + SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_ID + "=" + SitePolygonsEntry.SITE_POLYGONS_TABLE_NAME + "." + SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_SITE_ID +
                            " WHERE " + selection +
                            " ORDER BY " + SitePolygonsEntry.SITE_POLYGONS_TABLE_NAME + "." + SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_SITE_ID + ", " + SitePolygonsEntry.SITE_POLYGONS_TABLE_NAME + "." + SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_POINT_ORDER;
                }

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // VOYAGES SITES
            //

            case CODE_VOYAGES_ALL: {

                String query;

                String basequery = "SELECT DISTINCT " +
                        VoyageEntry.VOYAGE_TABLE_NAME + ".*" +
                        " FROM " + VoyageEntry.VOYAGE_TABLE_NAME +
                        " LEFT JOIN " + DiveEntry.DIVE_TABLE_NAME + " ON " + DiveEntry.DIVE_TABLE_NAME + "." + DiveEntry.DIVE_TABLE_COLUMN_VOYAGE_ID + " = " + VoyageEntry.VOYAGE_TABLE_NAME +"." + VoyageEntry.VOYAGE_TABLE_COLUMN_ID +
                        " LEFT JOIN " + MantaEntry.MANTA_TABLE_NAME + " ON " + MantaEntry.MANTA_TABLE_NAME + "." + MantaEntry.MANTA_TABLE_COLUMN_VOYAGE_ID + " = " + VoyageEntry.VOYAGE_TABLE_NAME +"." + VoyageEntry.VOYAGE_TABLE_COLUMN_ID +
                        " LEFT JOIN " + SiteEntry.SITE_TABLE_NAME + " ON " + SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_ID + " = " + DiveEntry.DIVE_TABLE_NAME + "." + DiveEntry.DIVE_TABLE_COLUMN_SITE_ID;

                String groupby = " GROUP BY " + VoyageEntry.VOYAGE_TABLE_NAME +"." + VoyageEntry.VOYAGE_TABLE_COLUMN_ID;

                if ( selection == null ) {

                    query = basequery + groupby;

                } else {

                    query = basequery + " WHERE " + selection + groupby;

                }

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // DIVES
            //

            case CODE_DIVES_ALL: {

                String query;

                if ( selection.isEmpty() ) {
                    query = "SELECT * FROM " + DiveEntry.DIVE_TABLE_NAME;
                }
                {
                    query = "SELECT * FROM " + DiveEntry.DIVE_TABLE_NAME + " WHERE " + selection;
                }

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            case CODE_DIVES_AT_REEF: {

                //TODO: Change this query to get all Dives after the most recent Voyage with Manta tow
                String query =
                        "SELECT DISTINCT " +
                        DiveEntry.DIVE_TABLE_NAME + ".* " +
                        "FROM " +
                        DiveEntry.DIVE_TABLE_NAME +
                        " LEFT JOIN " + SiteEntry.SITE_TABLE_NAME + " ON " + SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_ID + "=" + DiveEntry.DIVE_TABLE_NAME + "." + DiveEntry.DIVE_TABLE_COLUMN_SITE_ID +
                        " WHERE " + selection;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            case CODE_DIVES_AT_SITE: {

                String query =
                        "SELECT " + DiveEntry.DIVE_TABLE_NAME + ".* FROM " +
                                DiveEntry.DIVE_TABLE_NAME +
                                " WHERE " + selection;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // MANTAS
            //

            case CODE_MANTAS_ALL: {

                String query =
                        // We select all rows from MANTAS_TABLE_NAME
                        "SELECT * FROM " + MantaEntry.MANTA_TABLE_NAME;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;
            }

            case CODE_MANTAS_AT_REEF: {

                String query =
                        "SELECT DISTINCT " + MantaEntry.MANTA_TABLE_NAME + ".* FROM " +
                                MantaEntry.MANTA_TABLE_NAME +
                                " INNER JOIN " + SiteEntry.SITE_TABLE_NAME + " ON " + SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_ID + "=" + MantaEntry.MANTA_TABLE_NAME + "." + MantaEntry.MANTA_TABLE_COLUMN_SITE_ID +
                                " WHERE " + selection;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query,null);

                break;

            }

            //
            // RHIS
            //

            case CODE_RHIS_ALL: {

                String query =
                        "SELECT * FROM " + RhisEntry.RHIS_TABLE_NAME;

                cursor = COTSDataHelper.getReadableDatabase().rawQuery(query, null);

                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    //
    // COMPULSORY METHODS
    //

    // These are not yet used in the current implementation, but may be developed further as the
    // COTSContentProvider is leveraged more completely.

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException(
                "Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "Not yet implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("Not yet implemented");
    }

//     This is an internal method to assist the testing framework in running smoothly, described here:
//     http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        COTSDataHelper COTSDataHelper = new COTSDataHelper(getContext());
        COTSDataHelper.close();
        super.shutdown();
    }

}