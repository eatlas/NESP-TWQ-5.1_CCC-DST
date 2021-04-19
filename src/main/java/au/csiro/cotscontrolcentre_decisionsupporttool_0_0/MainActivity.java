package au.csiro.cotscontrolcentre_decisionsupporttool_0_0;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;

import android.util.TimingLogger;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.ReefEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.ReefPolygonsEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.SitePolygonsEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.SiteEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.VesselEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.VoyageEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.DiveEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.MantaEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.RhisEntry;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.model.ImplementDecisionTreeAtReef;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Reef;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.ReefPolygon;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.ReefPolygonPoint;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Site;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SiteList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.SitePolygon;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SitePolygonList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.SitePolygonPoint;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SitePolygonPointList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Vessel;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Voyage;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Dive;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.DiveList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Manta;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.MantaList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Rhis;

;

// PURPOSE
//
// This activity:
//
//      1. lets the user select which Reef on the GBR to analyse based on the ecologically-
// informed decision tree outlined in "An ecologically-based operational strategy for COTS Control:
// Integrated decision making from the site to the regional scale" [1].
//
//      2. displays for the user the latest data available for the selected Reef, including:
//          a) the latest manta tow data
//          b) the latest site density estimate
//          c) the current status of each site (above or below ecological threshold)
//          d) a list of the recommended order in which to visit each site
//
// The user selects the reef to analyse based on:
//      1. pinch-and-zoom on Google map
//      2. GPS selected by pin button overlaid on map
//      3. text based reef name search in floating search bar
//
// The app allows the user to review the most up-to-date data for the current Reef, notifies the
// Decision Maker when it is time to manta tow the Reef, highlights which Sites the Decision
// Support Tool recommends diving on next, and allows the Decision Maker to manually override
// the Decision Support Tool for actions at a specific Site.
//
// Behind the scenes, the activity also updates the latest data from the GBRMPA data collection
// apps. A separate service checks for nearby COTS Control Tablets to automatically sync the data
// from those tablets to this tablet.
//
// PHILOSOPHY
//
// The app can be viewed as three key components:
//
//     1. A component that reads stored Control Program data from the app's sqlite database file
//
//     2. A component that runs the relevant subset of that data through the COTS Decision Tree
//        and provides recommendations on when to manta tow or which Sites to cull next
//
//     3. A component that display the data and recommendations using a map interface
//
// The COTS Control Program data is central to each of these components, and how it is stored and
// interacted with is a core part of the tool development.
//
// Database Tables
//
// The data is stored in an sqlite database within the app. The database is structured based on the
// physical structure of the system, and contains components related to geographic features and
// management actions. The Control Program operates at the intersection of these components, and
// so the way they are structured is important to the operation of the Decision Support Tool.
//
// Geographic Features: The database contains Tables for storing two types of geographic features:
// Reefs and Sites. Although Sites are defined by the Control Program, once they are defined they
// are fixed. As such, the Site table should also remain static and not contain any information
// related to management, other than the addition of new Sites.
//
// Management Actions: The database contains Tables for storing five types of management-related
// information: Vessels, Voyages, Dives (cull dives), Mantas (manta tows), and RHIS (Reef Health
// Information Surveys). These characteristics reflect management actions: Dives report how many
// COTS were culled, Voyages report dates that they occurred etc. As more control actions continue,
// more items get added to these Tables.
//
// Database Table Relationships
//
// Vessels are the basic unit of management.
//
// Voyages refer to a specific Vessel, and take place over a certain date period. They are not
// associated with a given Reef directly, rather the Dives or Mantas that take place during the
// Voyage are assigned to a Site or Reef, as below.
//
// Dives take place on a Voyage and at a Site and take place on a certain Date.
//
// Mantas take place on a Voyage and at a "Nearest Site", and take place on a certain Date.
// Technically, Mantas take place at a Reef, and we may want to change this structure to reflect
// that because, ultimately, this is a derived relationship rather than a fundamental one, because
// Mantas are not strictly contained within Sites. This will become more important if we want to
// introduce a maximum distance from a Site that a Manta can be
// assigned to it, but for now this works.
//
// RHIS take place on a Voyage and at a Site and take place on a certain Date.
//
// Data Types
//
// The data from the database Tables is loaded into custom types within the app. These types are
// based on similar principles to the Tables.
//
// In addition to the data types themselves, we define several types of Lists of data types. This
// is purely to enable sensible list-based search options - e.g. a function for returning the
// most recently visited Site from a list of Sites, based on the latest Dive, RHIS and/or nearby
// Manta tows.
//
//
// IMPLEMENTATION
//
//
// REFERENCES
//
// [1]	Fletcher C. S., Bonin M. C, Westcott D. A.. (2020) An ecologically-based operational
// strategy for COTS Control: Integrated decision making from the site to the regional scale.
// Reef and Rainforest Research Centre Limited, Cairns (65pp.).
//
// TODO: SEQUENCE DATA LOADING TO IMPROVE UI EXPERIENCE
//
// TODO: INVESTIGATE USING ONE SUPER-LOADER TO LOAD EVERYTHING NEEDED AT ONCE

// Note that AppCompatActivity extends FragmentActivity
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DisplayMap.DisplayMapFragmentMarkerTouchListener,
        DisplayMap.DisplayMapFragmentMapTouchListener,
        DisplayMap.DisplayMapFragmentReadyListener,
        DisplayInfo.DisplayInfoFragmentLoadDataButtonPressListener,
        DisplayInfo.DisplayInfoFragmentGenerateWorkplanButtonPressListener,
        DisplayInfo.DisplayInfoFragmentReadyListener {

    // Define loader IDs
    private static final int ID_LOAD_REEF_TABLE = 100;
    private static final int ID_LOAD_REEFPOLYGONS_TABLE = 150;
    private static final int ID_LOAD_SITE_TABLE = 200;
//    private static final int ID_LOAD_SITEID_FROM_SITENAME = 201;
    private static final int ID_LOAD_SITEPOLYGONS_TABLE = 250;
    private static final int ID_LOAD_VESSEL_TABLE = 300;
    private static final int ID_LOAD_VOYAGE_TABLE = 400;
    private static final int ID_LOAD_DIVE_TABLE = 500;
    private static final int ID_LOAD_MANTA_TABLE = 600;
    private static final int ID_LOAD_RHIS_TABLE = 700;

    private Hashtable<Integer,Integer> cursorsStillLoadingData = new Hashtable<Integer,Integer>();

    private static int mapDisplayType;
    private static final int MAP_DISPLAY_ALL_REEFS_WITH_VOYAGES = 100;
    private static final int MAP_DISPLAY_REEF_WITH_SITES = 200;

// Define ArrayLists of the fundamental data types associated with the Control Program data
    public static List<Reef> reefList = new ArrayList<Reef>();
    public static List<ReefPolygonPoint> reefPolygonPointsList = new ArrayList<ReefPolygonPoint>();
    public static ReefPolygon reefPolygon;
    public static SiteList siteList = new SiteList();
    public static SitePolygonPointList sitePolygonPointsList = new SitePolygonPointList();
    public static SitePolygonList sitePolygonsList = new SitePolygonList();
    public static List<Vessel> vesselList = new ArrayList<Vessel>();
    public static List<Voyage> voyageList = new ArrayList<Voyage>();
    public static DiveList diveList = new DiveList();
    public static MantaList mantaList = new MantaList();
    public static List<Rhis> rhisList = new ArrayList<Rhis>();

    public static Reef selectedReef;

    public Context context;

    private DisplayMap displayMapFragment;
    private DisplayInfo displayInfoFragment;

    public static GoogleMap mainMap;
    private boolean firstMap = true;

    private String siteName;

//  These are working variables that are just used during the coding and debugging of the
//  application. In the long-run they will be replaced with dynamically created variables

    // Latitude and longitude of Cairns
    private static double CAIRNS_LATITUDE = -16.9186;
    private static double CAIRNS_LONGITUDE = 145.7781;
    private static LatLng cairnsLatLng = new LatLng( CAIRNS_LATITUDE, CAIRNS_LONGITUDE );

    public static double ecologicalThresholdCPUE = 0.1;
    public static int ecologicalThresholdMantaCOTS = 0;
    public static String ecologicalThresholdMantaScars = "a";

    public static int daysToMantaTowMaintenanceReef = 183 /* default 183 - six months */;
    public static int daysToMantaTowIntensiveControlReef = 42 /* default 42 - every fourth 10 - 12 day voyage */;
    public static int daysToCullSiteAtIntensiveControlReef = 10; /* default 10 - every 10 - 12 voyage */

    public static TimingLogger loaderTiming = new TimingLogger( "CCC_LOADER", "loadingTimingStart");

//
//
// MAIN onCreate function
//
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the theme to the app theme to replace the splash screen theme
        setTheme( R.style.AppTheme );

        // First, call the super.onCreate method
        super.onCreate( savedInstanceState );

        context = getApplicationContext();

        checkPermission();

        // Set the content view
        setContentView( R.layout.activity_main );

        // Initialize a series of loaders. If a loader doesn't already exist, one is created and started.
        // and (if the activity/fragment is currently started) starts. We use restartLoader rather
        // than initLoader (which would reuse the old loader if it already existed) because there
        // seems to be a bug in the SupportLoaderManager class that prevents the cursor data
        // persisting with an orientation change.

        mapDisplayType = MAP_DISPLAY_ALL_REEFS_WITH_VOYAGES;

        //TODO: Consider removing this and the ReefInfo fragment as dynamically loaded fragments
        // and just show and hide them as necessary
        //
        // Set up the display_map fragment
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add( R.id.display_map_fragment_container_view, new DisplayMap(), null )
                .add( R.id.display_info_fragment_container_view, new DisplayInfo(), null )
                .commit();

        // Set up the display_info fragment
//        getSupportFragmentManager().beginTransaction()
//                .setReorderingAllowed(true)
//                .add( R.id.display_info_fragment_container_view, new DisplayInfo(), null )
//                .commit();

        getSupportLoaderManager().initLoader(ID_LOAD_REEF_TABLE, null, this);


    }

//
//
// onCreateLoader function
//
//
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch ( loaderId ) {

            case ID_LOAD_REEF_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_REEF_TABLE, 1 );

                return new CursorLoader(this, ReefEntry.CONTENT_URI.buildUpon().appendPath("controlled").build(), null, null, null, null);

            case ID_LOAD_REEFPOLYGONS_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_REEFPOLYGONS_TABLE, 1 );

                return new CursorLoader(this, ReefPolygonsEntry.CONTENT_URI, null, ReefPolygonsEntry.REEF_POLYGONS_TABLE_COLUMN_REEF_ID + "=" + Integer.toString( selectedReef.getReefId() ) , null, null);

            case ID_LOAD_SITE_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_SITE_TABLE, 1 );

                return new CursorLoader(this, SiteEntry.CONTENT_URI, null, SiteEntry.SITE_TABLE_COLUMN_REEF_ID + "=" + Integer.toString( selectedReef.getReefId() ) , null, null);

//            case ID_LOAD_SITEID_FROM_SITENAME:
//
//                cursorsStillLoadingData.put( ID_LOAD_SITEID_FROM_SITENAME, 1 );
//
//                return new CursorLoader(this, SiteEntry.CONTENT_URI.buildUpon().appendPath("where/sitename").build(), null, SiteEntry.SITE_TABLE_COLUMN_SITE_NAME + "=" + siteName , null, null);

            case ID_LOAD_SITEPOLYGONS_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_SITEPOLYGONS_TABLE, 1 );

                return new CursorLoader(this, SitePolygonsEntry.CONTENT_URI, null, SiteEntry.SITE_TABLE_COLUMN_REEF_ID + "=" + Integer.toString( selectedReef.getReefId() ) , null, null);

            case ID_LOAD_VESSEL_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_VESSEL_TABLE, 1 );

                return new CursorLoader(this, VesselEntry.CONTENT_URI, null, null, null, null);

            case ID_LOAD_VOYAGE_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_VOYAGE_TABLE, 1 );

                return new CursorLoader(this, VoyageEntry.CONTENT_URI, null, SiteEntry.SITE_TABLE_COLUMN_REEF_ID + "=" + Integer.toString( selectedReef.getReefId() ), null, null);

            case ID_LOAD_DIVE_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_DIVE_TABLE, 1 );

                return new CursorLoader(this, DiveEntry.CONTENT_URI.buildUpon().appendPath("where").appendPath("reef").build(), null, SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_REEF_ID + "=" + Integer.toString( selectedReef.getReefId() ), null, null);

            case ID_LOAD_MANTA_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_MANTA_TABLE, 1 );

                return new CursorLoader(this, MantaEntry.CONTENT_URI.buildUpon().appendPath("where").appendPath("reef").build(), null, SiteEntry.SITE_TABLE_NAME + "." + SiteEntry.SITE_TABLE_COLUMN_REEF_ID + "=" + Integer.toString( selectedReef.getReefId() ), null, null);

            case ID_LOAD_RHIS_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_RHIS_TABLE, 1 );

                return new CursorLoader(this, RhisEntry.CONTENT_URI, null, null, null, null);

            default:

                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

    }

//
//
// onLoadFinished function
//
//
    @Override
    public void onLoadFinished( Loader<Cursor> loader, Cursor data ) {

        int loaderId = loader.getId();

        switch (loaderId) {

            case ID_LOAD_REEF_TABLE:

                // Clear the old list data
                reefList.clear();

                // Load data from the database voyageTable
                while (data.moveToNext()) {
                    reefList.add( new Reef( data ) );
                }

                break;

            case ID_LOAD_REEFPOLYGONS_TABLE:

                // Here, we know that the cursor is returning only ReefPolygonPoints from this
                // particular Reef, so we can just load them all into the appropriate
                // ReefPolygon

                // Clear the old list data
                reefPolygonPointsList.clear();

                while (data.moveToNext()) {
                    reefPolygonPointsList.add( new ReefPolygonPoint( data ) );
                }

                if ( !reefPolygonPointsList.isEmpty() ) {

                    reefPolygon = new ReefPolygon( reefPolygonPointsList );

                }

                // Display Reef Polygon
                displayMapFragment.DisplayReefOutline( selectedReef, reefPolygon );

                // Next, we load the details of the Voyages that have visited this Reef
                getSupportLoaderManager().restartLoader(ID_LOAD_VOYAGE_TABLE, null, this);

                break;

            case ID_LOAD_SITE_TABLE:

                // Clear the old list data
                siteList.clear();

                // Load data from the database voyageTable
                while (data.moveToNext()) {
                    siteList.add( new Site( data ) );
                }

                getSupportLoaderManager().restartLoader(ID_LOAD_MANTA_TABLE, null, this);

                break;

            case ID_LOAD_SITEPOLYGONS_TABLE:

                // Here, we know that the query will have returned polygon points from every
                // Site at the Reef, and that the polygon points will be in contiguous chunks for
                // each Site, and in their correct order. Therefore, we can cycle through the list,
                // adding SitePolygonPoints to a list until the __siteId changes, then save the list
                // to a new SitePolygon as necessary

                // Clear the old lists data
                sitePolygonPointsList.clear();
                sitePolygonsList.clear();

                SitePolygonPointList sitePolygonPointList = new SitePolygonPointList();

                int currentSiteId = -1;

                while ( data.moveToNext() ) {

                    if ( currentSiteId == data.getInt(data.getColumnIndex( SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_SITE_ID ) ) ){

                        sitePolygonPointList.add( new SitePolygonPoint( data ) );

                    } else {

                        if ( currentSiteId != -1 ) {

                                sitePolygonsList.add( new SitePolygon( sitePolygonPointList ) );
                        }

                        sitePolygonPointList = new SitePolygonPointList();

                        currentSiteId = data.getInt(data.getColumnIndex( SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_SITE_ID ) );

                    }

                }

                displayMapFragment.addDivesToMap( siteList, diveList, sitePolygonsList );

                break;

            case ID_LOAD_VESSEL_TABLE:

                // Clear the old list data
                vesselList.clear();

                // Load data from the database voyageTable
                while (data.moveToNext()) {
                    vesselList.add( new Vessel( data ) );
                }

                break;

            case ID_LOAD_VOYAGE_TABLE:

                // Clear the old list data
                voyageList.clear();

                // Load data from the database voyageTable
                while (data.moveToNext()) {

                    voyageList.add( new Voyage( data ) );

                }

                getSupportLoaderManager().restartLoader(ID_LOAD_SITE_TABLE, null, this);

                break;

            case ID_LOAD_DIVE_TABLE:

                cursorsStillLoadingData.put( ID_LOAD_DIVE_TABLE, 1 );

                // Clear the old list data
                diveList.clear();

                while (data.moveToNext()) {

                    diveList.add( new Dive( data ) );

                }

                displayInfoFragment.DisplayReefInfo( this, mainMap, selectedReef, reefList, siteList, diveList, mantaList, true );

                getSupportLoaderManager().restartLoader(ID_LOAD_SITEPOLYGONS_TABLE, null, this);

                break;

            case ID_LOAD_MANTA_TABLE:

                // Clear the old list data
                mantaList.clear();

                // Load data from the database voyageTable
                while (data.moveToNext()) {

                    mantaList.add( new Manta( data ) );

                }

                displayMapFragment.addMantasToMap( mantaList );

                getSupportLoaderManager().restartLoader(ID_LOAD_DIVE_TABLE, null, this);

                break;

            case ID_LOAD_RHIS_TABLE:

                // Clear the old list data
                rhisList.clear();

                // Load data from the database voyageTable
                while (data.moveToNext()) {
                    rhisList.add( new Rhis( data ) );
                }

                break;

            default:

                throw new RuntimeException("Loader Not Implemented: " + loaderId);

        }

        displayMap();

    }

    private void displayMap(){

        switch ( mapDisplayType ) {

            case MAP_DISPLAY_ALL_REEFS_WITH_VOYAGES:

                displayMapFragment.DisplayAllReefsControlled( reefList, true);

                break;

            case MAP_DISPLAY_REEF_WITH_SITES:

                findViewById( R.id.display_info_fragment_container_view ).setVisibility( View.VISIBLE );

                displayMapFragment.DisplayReef( selectedReef, reefPolygon );

                break;

            default:

                throw new UnsupportedOperationException( "That mapDisplayType not implemented yet." );

        }

    }

    public void LoadNewData() {

        LoadNewData.loadNewData( this, reefList, mantaList );

        getSupportLoaderManager().restartLoader(ID_LOAD_SITE_TABLE, null, this);
        getSupportLoaderManager().restartLoader(ID_LOAD_DIVE_TABLE, null, this);
        getSupportLoaderManager().restartLoader(ID_LOAD_MANTA_TABLE, null, this);
        getSupportLoaderManager().restartLoader(ID_LOAD_VOYAGE_TABLE, null, this);

        // Ask map to turn off previous Mantas and Culls

        // Ask map to display new Mantas

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case 123: {


                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)     {
                    //Peform your task here if any
                } else {

                    checkPermission();
                }
                return;
            }
        }
    }

    @Override
    public void sendTouchedReefId(int reefId) {

        selectedReef = reefList.get( 0 );

        for ( Reef reef : reefList ){

            if ( reef.getReefId() == reefId ){

                selectedReef = reef;

            }

        }

        mapDisplayType = MAP_DISPLAY_REEF_WITH_SITES;

        // First, we load the reefPolygon, and then display it
        getSupportLoaderManager().restartLoader(ID_LOAD_REEFPOLYGONS_TABLE, null, this);

    }

    @Override
    public void sendMapTouched() {

        findViewById( R.id.display_info_fragment_container_view ).setVisibility( View.GONE );

    }

    @Override
    public void sendGenerateWorkplanButtonPress() {

        ImplementDecisionTreeAtReef implementDecisionTreeAtReef = new ImplementDecisionTreeAtReef( selectedReef.getReefName(), siteList, diveList, mantaList );
        String workplanText = implementDecisionTreeAtReef.ImplementDecisionTreeAtReefAndFindControlTasks();

        displayInfoFragment.DisplayWorkplanInfo( workplanText );

        List<Integer> siteIdsInControlOrder = implementDecisionTreeAtReef.ImplementDecisionTreeAtReefAndFindSiteIdsToBeControlledInOrder();

        if ( !siteIdsInControlOrder.isEmpty() ) {

            displayMapFragment.DisplayWorkplanMarkers(this, mainMap, selectedReef, reefPolygon, reefList, siteList, diveList, sitePolygonsList, mantaList, true, siteIdsInControlOrder);

        }

    }



    @Override
    public void sendLoadDataButtonPress() {

        LoadNewData();

    }

    @Override
    public void sendMapFragment( DisplayMap displayMap ) {

        displayMapFragment = displayMap;

    }

    @Override
    public void sendInfoFragment( DisplayInfo displayInfo ) {

        displayInfoFragment = displayInfo;

    }

}
