// COMMENT OUT FOR OFFLINE USE
package au.csiro.cotscontrolcentre_decisionsupporttool_0_0;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Manta;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.MantaList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Reef;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.DiveList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.ReefPolygon;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Site;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SiteList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.SitePolygon;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SitePolygonList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Voyage;

import static java.lang.Math.max;

// The minimum information required to create a new DisplayMap fragment object is nothing - the map
// simply displays at the default Google location. After that, various simple helper methods are
// provided to move the map to a certain location, to zoom the map etc.
//
// Custom methods are then provided to interact specifically with COTS Program data, as follows:
//
// 1) DisplayReefs( ReefList reefList ): Zoom to the extent of Reefs in the ReefList and add a marker at the LatLng of each Reef
// 2) DisplayReef( Reef reef ): Zoom to the Lat Lng of the Reef with Zoom level 7.0f
// 3) DisplayReef( Reef reef, SiteList siteList ): Add markers at the LatLng of each Site at the Reef
// 4) DisplayMantas( MantaList mantaList ): Add Polylines to the current map for the mantaList
// 5) DisplayDives( DiveList diveList, SitePolygonList sitePolygonList ): Add Site Polygons
// is provided

public class DisplayMap extends Fragment implements
        OnMapReadyCallback,
        OnMarkerClickListener,
        OnMapClickListener,
        View.OnClickListener {

    private ClusterManager<MyItem> clusterManager;

    private List<Marker> mapMarkerList = new ArrayList<>();
    private List<Polygon> mapPolygonList = new ArrayList<>();
    private List<Polyline> mapPolylineList = new ArrayList<>();
    private List<Marker> workplanMarkerList = new ArrayList<>();

    private Button mantaButton;
    private Button cullButton;

    private boolean mantaButtonClicked = false;
    private boolean cullButtonClicked = false;

    private int mapDisplayType;
    private final int MAP_DISPLAY_ALL_REEFS_WITH_VOYAGES = 100;
    private final int MAP_DISPLAY_REEF_WITH_SITES = 200;

    private GoogleMap mainMap;
    private View mapView;
    private View mapFrameView;

    private int mapWidth = 0;
    private int mapHeight = 0;

    // Define ArrayLists of the fundamental data types associated with the Control Program data
    public List<Reef> dmReefList = new ArrayList<Reef>();

    private boolean mapDataReady = false;

    private Context dmContext;

    // Latitude and longitude of Cairns
    private double CAIRNS_LATITUDE = -16.9186;
    private double CAIRNS_LONGITUDE = 145.7781;
    private LatLng cairnsLatLng = new LatLng( CAIRNS_LATITUDE, CAIRNS_LONGITUDE );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        // Define the xml file for the fragment
        mapView = inflater.inflate(R.layout.display_map, parent, false);

        return mapView;

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated( final View view, Bundle savedInstanceState ) {

        // Define the view for the actual map fragment
        mapFrameView = mapView.findViewById( R.id.map_frame );

        // Set up the map fragment
        ( ( SupportMapFragment ) getChildFragmentManager().findFragmentById( R.id.map_main ) ).getMapAsync( this );

        // Because the view gets resized dynamically as the fragment and other fragments are added
        // and removed from the screen, we need to add a listener to update the current width and
        // height available to draw the map.
        view.getViewTreeObserver().addOnGlobalLayoutListener(

                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

//                        mapFrameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mapWidth = mapFrameView.getHeight();
                        mapHeight = mapFrameView.getHeight();

                    }

                });

        // Set up handles
        mantaButton = mapView.findViewById(R.id.mantaButton);
        cullButton = mapView.findViewById(R.id.cullButton);

        mantaButton.setOnClickListener( this );
        cullButton.setOnClickListener( this );

        displayMapFragmentReadyListener.sendMapFragment( this );

    }

    //    Fragment constructor
    public DisplayMap() {

        super();

    }

    private void DisplayCairnsRegion() {

        mainMap.clear();

        mainMap.moveCamera( CameraUpdateFactory.newLatLngZoom( cairnsLatLng, 7.0f) );

    }

    public void DisplayAllVoyages(Context context, final GoogleMap map, List<Voyage> voyageList, boolean firstZoom ) {

        dmContext = context;

        mainMap.clear();
        if ( clusterManager != null ) {

            clusterManager.clearItems();

        } else {

            clusterManager = new ClusterManager<MyItem>( context, map );

        }

        mainMap.setOnCameraIdleListener(clusterManager);

        Builder builder = new Builder();

        for (Voyage voyage : voyageList) {

            LatLng latLng = new LatLng(voyage.meanLatitude(), voyage.meanLongitude());

            builder.include(latLng);

            clusterManager.addItem(new MyItem(latLng.latitude, latLng.longitude, "Barry", String.valueOf(voyage.getVoyageId())));

        }

        if (firstZoom) {

            mainMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), mapWidth, mapHeight, 200));

        } else {

            mainMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), mapWidth, mapHeight, 200));

        }

    }

    public void DisplayAllReefsControlled( List<Reef> reefList, boolean firstZoom ) {

        mainMap.clear();
        mapMarkerList.clear();
        mapPolygonList.clear();
        mapPolylineList.clear();

        // If we're displaying all reefs, we don't want to display the display button bar
        mapView.findViewById(R.id.mantaButton).setVisibility(View.GONE);
        mapView.findViewById(R.id.cullButton).setVisibility(View.GONE);

        // Update DisplayMap's dmReefList
        dmReefList = reefList;
        mapDataReady = true;

        // Set up a new cluster manager if necessary, or clear the old one
        // Also set up the required OnCameraIdle listener so that the ClusterManager can do it's thing
        // when we stop scrolling
        if ( clusterManager != null ) {

            clusterManager.clearItems();

        } else {

            clusterManager = new ClusterManager<MyItem>( this.getContext(), mainMap );

        }

        mainMap.setOnCameraIdleListener( clusterManager );

        // Set up a builder to store the lats and longs of all the reef polygon points, then
        // cycle through add all the points for the Reef Polygon to the builder
        Builder builder = new Builder();

        for ( Reef reef : reefList ) {

            LatLng latLng = new LatLng(reef.getReefLatitude(), reef.getReefLongitude());

            builder.include(latLng);

            clusterManager.addItem(new MyItem(latLng.latitude, latLng.longitude, String.valueOf(reef.getReefName()), String.valueOf(reef.getReefId())));

        }

        // Zoom the map to the appropriate location. If it's the first time the map is set up, we
        // just move the camera straight to the location - otherwise we animate the move
        if ( firstZoom ) {

            mainMap.moveCamera( CameraUpdateFactory.newLatLngBounds( builder.build(), mapWidth, mapHeight, 200 ) );

        } else {

            mainMap.animateCamera( CameraUpdateFactory.newLatLngBounds( builder.build(), mapWidth, mapHeight, 200 ) );

        }

    }

    public void DisplayReefOutline( Reef reef, ReefPolygon reefPolygon ) {

        // Clear the map and clear all the clusters
        mainMap.clear();
        mapMarkerList.clear();
        mapPolygonList.clear();
        mapPolylineList.clear();

        if ( clusterManager != null ) {

            clusterManager.clearItems();

        }

        // Set up a builder to store the lats and longs of all the reef polygon points, then
        // cycle through add all the points for the Reef Polygon to the builder. At a minimum
        // add the lat lng recorded for the Reef, in case there are no polygon points
        Builder builder = new Builder();

        builder.include( new LatLng( reef.getReefLatitude(), reef.getReefLongitude() ) );

        if ( !( reefPolygon == null ) ) {

            mainMap.addPolygon(new PolygonOptions().addAll(reefPolygon.getReefPolygonPoints()).strokeWidth(5).strokeColor(Color.GRAY));

            for (LatLng reefPolygonPoint : reefPolygon.getReefPolygonPoints()) {

                builder.include(reefPolygonPoint);

            }
        }

        // Zoom camera early to provide quick UI feedback
        mainMap.animateCamera( CameraUpdateFactory.newLatLngBounds( builder.build(), mapWidth, mapHeight, 200 ) );

    }


    public void DisplayReef( final Reef reef, ReefPolygon reefPolygon ) {

        // Set up a builder to store the lats and longs of all the reef polygon points, then
        // cycle through add all the points for the Reef Polygon to the builder. At a minimum
        // add the lat lng recorded for the Reef, in case there are no polygon points
        Builder builder = new Builder();

        builder.include( new LatLng( reef.getReefLatitude(), reef.getReefLongitude() ) );

        if ( !( reefPolygon == null ) ) {

            for ( LatLng reefPolygonPoint : reefPolygon.getReefPolygonPoints() ) {

                builder.include(reefPolygonPoint);

            }

        }

        // Zoom camera early to provide quick UI feedback
        mainMap.animateCamera( CameraUpdateFactory.newLatLngBounds( builder.build(), mapWidth, mapHeight, 40 ) );

        mapView.findViewById( R.id.loadingPanel).setVisibility(View.GONE);

    }

    public void addDivesToMap( final SiteList siteList, final DiveList diveList, final SitePolygonList sitePolygonsList ){

        // First, find the most recent Dive in the Sites at the Reef
        Integer diveMostRecentVoyageId = diveList.getMostRecentDiveVoyageId();

        DiveList diveListOnMostRecentDiveVoyage = diveList.getDivesByVoyageId( diveMostRecentVoyageId );

        // We know that the function has been passed data about the current Reef, so all the Sites
        // in siteList are at the Reef, and all the Sites at the Reef are in siteList. In future,
        // if this is not the case, we might need to select Sites at the Reef.
        // for ( Site site : siteList.getSitesWithReefId( reefId ) ) {
        for ( Site site : siteList ) {

            SitePolygon siteSitePolygon = sitePolygonsList.getSitePolygonBySiteId(site.getSiteId());

            if ( siteSitePolygon != null ) {

                PolygonOptions cullPolygonOptions;

                Integer totalCOTSCulledOnDivesAtSiteOnMostRecentVoyageToReef = diveListOnMostRecentDiveVoyage.getDivesBySiteId(site.getSiteId()).getTotalCOTS();

                // THIS SEARCHES FOR THE MOST RECENT DIVE AND MANTA TWICE - WHY?
//                Integer totalCOTSCulledOnDivesAtSiteOnMostRecentVoyageToReef = diveListOnMostRecentDiveVoyage.getDivesBySiteId(site.getSiteId()).getDivesOnMostRecentVoyage().getTotalCOTS();

                // Add the polygon to the map, coloured by the number of COTS removed during the last cull there - if there were no COTS culled there, don't provide any fill
                cullPolygonOptions = new PolygonOptions().addAll(siteSitePolygon.getSitePolygonPoints()).strokeWidth(1).fillColor(colorFunctionCull(totalCOTSCulledOnDivesAtSiteOnMostRecentVoyageToReef, 64));

                mapPolygonList.add( mainMap.addPolygon( cullPolygonOptions ) );

            }

        }

        cullButtonClicked = false;

        cullButton.setVisibility(View.VISIBLE);

    }

    public void addMantasToMap( final MantaList mantaList ){

        // Find the most recent Mantas at the Reef
        Integer mantaMostRecentVoyageId = mantaList.getMostRecentMantaVoyageId();

        MantaList mantaListOnMostRecentMantaVoyage = mantaList.getMantasByVoyageId( mantaMostRecentVoyageId );

        // Add all the mantas appropriated coloured
        for (Manta manta : mantaListOnMostRecentMantaVoyage) {

            PolylineOptions mantaPolylineOptions = new PolylineOptions().add(new LatLng(manta.getMantaStartLat(), manta.getMantaStartLong()), new LatLng(manta.getMantaStopLat(), manta.getMantaStopLong())).width(3).color(colorFunctionManta(mantaCOTSSeverity(manta), 255));

            mapPolylineList.add(mainMap.addPolyline(mantaPolylineOptions));

        }

        mantaButtonClicked = false;

        mantaButton.setVisibility(View.VISIBLE);



    }

    private int mantaCOTSSeverity( Manta manta ) {

        int mantaCOTSseverity = 0;

        if ( manta.getMantaScars().equals( "c" ) || ( manta.getMantaScars().equals( "p" ) && ( manta.getMantaCOTS() > 1 ) ) ) {

            mantaCOTSseverity = max( 2, mantaCOTSseverity );

        } else if ( manta.getMantaScars().equals( "p" ) || ( manta.getMantaScars().equals( "a" ) && ( manta.getMantaCOTS() > 0 ) ) ) {

            mantaCOTSseverity = 1;

        } else {

            mantaCOTSseverity = 0;

        }

        return mantaCOTSseverity;

    }

    private int colorFunctionCull(Integer cotsCount, int opacity) {

        int outputColor;

        if (cotsCount == null ){
            outputColor = ColorUtils.setAlphaComponent(Color.WHITE, 00);
        } else if (cotsCount < 1) {
            outputColor = ColorUtils.setAlphaComponent(Color.GREEN, opacity);
        } else if (cotsCount < 4) {
            outputColor = ColorUtils.setAlphaComponent(Color.YELLOW, opacity);
        } else {
            outputColor = ColorUtils.setAlphaComponent(Color.RED, opacity);
        }

        return outputColor;

    }

    private int colorFunctionManta(int cotsCount, int opacity) {

        int outputColor;

        if (cotsCount < 1) {
            outputColor = ColorUtils.setAlphaComponent(Color.GREEN, opacity);
        } else if (cotsCount < 2) {
            outputColor = ColorUtils.setAlphaComponent(Color.YELLOW, opacity);
        } else {
            outputColor = ColorUtils.setAlphaComponent(Color.RED, opacity);
        }

        return outputColor;

    }

    public void DisplayWorkplanMarkers( Context context, final GoogleMap map, final Reef reef, ReefPolygon reefPolygon, final List<Reef> reefList, final SiteList siteList, final DiveList diveList, final SitePolygonList sitePolygonsList, final MantaList mantaList, Boolean zoom, List<Integer> siteIdsInControlOrder ){

        int i = 1;

        for (Integer siteId : siteIdsInControlOrder) {

            Site site = siteList.getSiteBySiteId(siteId);

            LinearLayout tv = ((LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.site_marker_info, null, false));
            TextView site_marker_info_text = tv.findViewById(R.id.site_marker_info_text);

            //            site_marker_info_text.setText( Integer.toString( i ) + ", " + site.getSiteName() + ", COTS: " + numberOfCOTSCulledAtSite +", Manta: " + numberOfMantaCOTSAtSite );
            site_marker_info_text.setText(Integer.toString(i));

            tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

            tv.setDrawingCacheEnabled(true);
            tv.buildDrawingCache();
            Bitmap bm = tv.getDrawingCache();

            workplanMarkerList.add(

                    mainMap.addMarker(

                            new MarkerOptions()
                                    .position(new LatLng(site.getSiteLatitude(), site.getSiteLongitude()))
                                    .title(Integer.toString(i))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bm))

                    )

            );

            i++;

        }


    }


    @Override
    public boolean onMarkerClick( final Marker marker ) {

        mapView.findViewById( R.id.loadingPanel ).setVisibility( View.VISIBLE );

        if ( marker.getSnippet() != null ) {

            int id = Integer.parseInt(marker.getSnippet());

            sendTouchedReefId( id );

            //TODO: Stagger loading to make UI responsive - first search and load dives and mantas
            // just from the most recent Voyage and display quickly, then move to background loading
            // all the dives and mantas at the relevant Reef so that if the user requires it, the
            // data is ready to go

        }

        return true;
    }


    @Override
    public void onMapClick( final LatLng latlng ) {

        if ( mapDataReady ) {

            mapDisplayType = MAP_DISPLAY_ALL_REEFS_WITH_VOYAGES;

            DisplayAllReefsControlled( dmReefList, false );

            sendMapTouched();

        } else {

            // Do nothing

        }


    }

    @Override
    public void onMapReady( final GoogleMap map ) {

        mainMap = map;

        mainMap.setOnMarkerClickListener( this );

        mainMap.setOnMapClickListener( this );

        DisplayCairnsRegion();

    }

    private DisplayMapFragmentMarkerTouchListener displayMapFragmentMarkerTouchListener;
    private DisplayMapFragmentMapTouchListener displayMapFragmentMapTouchListener;
    private DisplayMapFragmentReadyListener displayMapFragmentReadyListener;

    private void sendTouchedReefId( int reefId ) {

        if  ( displayMapFragmentMarkerTouchListener != null ) {

            displayMapFragmentMarkerTouchListener.sendTouchedReefId( reefId );

        }

    }

    private void sendMapTouched() {

        if  ( displayMapFragmentMapTouchListener != null ) {

            displayMapFragmentMapTouchListener.sendMapTouched();

        }

    }

    private void sendMapFragment( DisplayMap displayMap ){

        if  ( displayMapFragmentReadyListener != null ) {

            displayMapFragmentReadyListener.sendMapFragment( displayMap );

        }

    }

    public interface DisplayMapFragmentMarkerTouchListener {

        public void sendTouchedReefId( int reefId );

    }

    public interface DisplayMapFragmentMapTouchListener {

        public void sendMapTouched();

    }

    public interface DisplayMapFragmentReadyListener {

        public void sendMapFragment( DisplayMap displayMap );

    }


    @Override
    public void onAttach( Context context ) {

        super.onAttach( context );

        try {

            displayMapFragmentMarkerTouchListener = ( DisplayMapFragmentMarkerTouchListener ) context;

            displayMapFragmentMapTouchListener = ( DisplayMapFragmentMapTouchListener ) context;

            displayMapFragmentReadyListener = ( DisplayMapFragmentReadyListener ) context;

        } catch ( ClassCastException e ) {

            throw new ClassCastException( context.toString()+ " must implement displayMapFragmentMarkerTouchListener" );

        }

    }

    @Override
    public void onDetach() {

        displayMapFragmentMarkerTouchListener = null;

        displayMapFragmentMapTouchListener = null;

        super.onDetach();

    }

    private void toggleMantas(){

        mantaButtonClicked = !mantaButtonClicked;

        for ( Polyline mantaPolyline : mapPolylineList ) {

            mantaPolyline.setVisible( !mantaButtonClicked );

        }

    }

    private void toggleDives(){

        cullButtonClicked = !cullButtonClicked;

        int alpha;

        if (cullButtonClicked) {
            alpha = 00;
        } else {
            alpha = 64;
        }

        for (Polygon cullPolygon : mapPolygonList) {

            cullPolygon.setFillColor(ColorUtils.setAlphaComponent(cullPolygon.getFillColor(), alpha));

        }

    }

    @Override
    public void onClick( View view ) {

        switch ( view.getId() ) {

            case R.id.mantaButton:

                toggleMantas();

                break;

            case R.id.cullButton:

                toggleDives();

                break;

            default:

        }

    }


}
