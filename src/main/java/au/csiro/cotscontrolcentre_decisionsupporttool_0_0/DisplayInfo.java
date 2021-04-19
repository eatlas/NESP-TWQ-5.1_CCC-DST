// COMMENT OUT FOR OFFLINE USE
package au.csiro.cotscontrolcentre_decisionsupporttool_0_0;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.DiveList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.MantaList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Reef;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Site;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SiteList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Voyage;

import static java.lang.Math.max;

/**
 * Created by fle125 on 14/04/2017.
 */

public class DisplayInfo extends Fragment implements
        View.OnClickListener {

    private View infoView;

    private View infoPanelView;
    private View workplanPanelView;

    private TextView reefInfoOverlayReefName;
    private TextView reefInfoOverlayReefMode;
    private TextView reefInfoOverlayNumberOfSites;
    private TextView reefInfoOverlayLastCullDate;
    private TextView reefInfoOverlayLastMantaDate;
    private TextView reefInfoOverlayNumberOfDaysSinceLastManta;
    private TextView reefInfoOverlayMantaDue;
    private TextView reefInfoOverlayTotalCOTSCulledDuringLastCull;
    private TextView reefInfoOverlayTotalCOTSSeenDuringLastManta;
    private TextView reefInfoOverlayAnyMantaScarsSeenDuringLastManta;
    private TextView reefInfoOverlayNumberOfSitesWithMantaCOTSOrScars;

    private TextView reefInfoOverlayWorkPlan;

    private Button generateWorkPlanButton;
    private Button loadSurveillanceFileButton;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState ) {

        // Define the xml file for the fragment
        infoView = inflater.inflate( R.layout.display_info, parent, false );

        return infoView;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated( View view, Bundle savedInstanceState ) {

        // Setup handles to view objects
        workplanPanelView = infoView.findViewById( R.id.overlay_workplan_panel );
        infoPanelView = infoView.findViewById( R.id.overlay_info_panel );

        reefInfoOverlayReefName = infoView.findViewById( R.id.reef_info_overlay_reef_name );
        reefInfoOverlayReefMode = infoView.findViewById( R.id.reef_info_overlay_reef_reef_mode );
        reefInfoOverlayNumberOfSites = infoView.findViewById( R.id.reef_info_overlay_reef_number_of_sites );
        reefInfoOverlayLastCullDate = infoView.findViewById( R.id.reef_info_overlay_last_cull_date );
        reefInfoOverlayLastMantaDate = infoView.findViewById( R.id.reef_info_overlay_last_manta_date );
        reefInfoOverlayNumberOfDaysSinceLastManta = infoView.findViewById( R.id.reef_info_overlay_number_of_days_since_last_manta );
        reefInfoOverlayMantaDue = infoView.findViewById( R.id.reef_info_overlay_manta_due );
        reefInfoOverlayTotalCOTSCulledDuringLastCull = infoView.findViewById( R.id.reef_info_overlay_reef_total_cots_culled_during_last_cull );
        reefInfoOverlayTotalCOTSSeenDuringLastManta = infoView.findViewById( R.id.reef_info_overlay_reef_total_cots_seen_during_last_manta );
        reefInfoOverlayAnyMantaScarsSeenDuringLastManta = infoView.findViewById( R.id.reef_info_overlay_reef_any_manta_scars_seen_during_last_manta );
        reefInfoOverlayNumberOfSitesWithMantaCOTSOrScars = infoView.findViewById( R.id.reef_info_overlay_reef_number_of_sites_with_manta_cots_or_scars );

        reefInfoOverlayWorkPlan = infoView.findViewById(R.id.reef_info_overlay_workplan);

        generateWorkPlanButton = infoView.findViewById(R.id.generateWorkplanButton);
        loadSurveillanceFileButton = infoView.findViewById(R.id.loadSurveillanceFileButton);

        generateWorkPlanButton.setOnClickListener( this );
        loadSurveillanceFileButton.setOnClickListener( this );

        displayInfoFragmentReadyListener.sendInfoFragment( this );

    }

    //    Fragment constructor
    public DisplayInfo() {

        super();

    }

    // In future, we could decide to populate a text info view for when all Voyages are displayed on
    // the map, but we don't use it for now
    public static void DisplayAllVoyageInfo(Context context, final GoogleMap map, List<Voyage> voyageList, boolean firstZoom) {

    }

    // In future, we could decide to populate a text info view for when all Voyages with control
    // data are displayed on the map, but we don't use it for now
    public static void DisplayAllReefsControlled(Context context, final GoogleMap map, List<Reef> reefList, boolean firstZoom) {

    }

    // Once a Reef is selected, we want to display a range of information about it in TextViews.
    // However, this is not as trivial as it might at first seem, because some of the information
    // we want to display as a simple statement of, for instance, number of days since the Reef was
    // last Manta towed, needs to be generated by analysing various underlying data.
    //
    // That means we have to load the underlying data into this method, and process it
    // appropriately. If the calculation relates specifically to one of our custom Types,
    // calculating the total number of COTS culled during a Dive, for instance, then we
    // try to house it as a method of the Type itself. If it relates specifically to a list of one
    // of our Types, for instance finding the most recent Dive at Sites in a SiteList, we try to
    // house it in the custom TypeList. If it is neither of these, we house it as a private method
    // of this class.
    public void DisplayReefInfo( final Context context, final GoogleMap map, final Reef reef, final List<Reef> reefList, final SiteList siteList, final DiveList diveList, final MantaList mantaList, Boolean zoom) {

        MainActivity.loaderTiming.addSplit("DisplayReefInfoStart");

        int totalCOTSCount = 0;
        int totalMantaCount = 0;
        boolean anyMantaScars = false;
        int numberOfSitesWithMantaCOTSOrScars = 0;

        Date mostRecentMantaDate = new Date(0, 01, 01); /* Set latest Date to a Date before the program began  */
        Date mostRecentDiveDate = new Date(0, 01, 01); /* Set latest Date to a Date before the program began */

        Integer diveMostRecentVoyageId = diveList.getMostRecentDiveVoyageId();
        Integer mantaMostRecentVoyageId = mantaList.getMostRecentMantaVoyageId();

        DiveList diveListOnMostRecentDiveVoyage = diveList.getDivesByVoyageId( diveMostRecentVoyageId );
        MantaList mantaListOnMostRecentMantaVoyage = mantaList.getMantasByVoyageId( mantaMostRecentVoyageId );

        // Then, find the date of the most recent Dive and Manta in the Sites at the Reef
        mostRecentDiveDate = diveListOnMostRecentDiveVoyage.get(0).getDiveDateAsDate();
        mostRecentMantaDate = mantaListOnMostRecentMantaVoyage.get(0).getMantaDateAsDate();

        // We know that the function has been passed data about the current Reef, so all the Sites
        // in siteList are at the Reef, and all the Sites at the Reef are in siteList. In future,
        // if this is not the case, we might need to select Sites at the Reef.
        for ( Site site : siteList ) {

            // We total up the number of COTS culled and detected in mantas for each
            totalCOTSCount += diveListOnMostRecentDiveVoyage.getDivesBySiteId( site.getSiteId() ).getTotalCOTS();

            totalMantaCount += mantaListOnMostRecentMantaVoyage.getMantasBySiteId( site.getSiteId() ).getTotalCOTS();

// THIS SEARCHES FOR THE MOST RECENT DIVE AND MANTA TWICE - WHY?
//            totalCOTSCount += diveListOnMostRecentDiveVoyage.getDivesBySiteId( site.getSiteId() ).getDivesOnMostRecentVoyage().getTotalCOTS();
//
//            totalMantaCount += mantaListOnMostRecentMantaVoyage.getMantasBySiteId( site.getSiteId() ).getMantasOnMostRecentVoyage().getTotalCOTS();



        }

        boolean reefModeQ = ( totalMantaCount == 0 ) || ( totalCOTSCount == 0 );

        Calendar todaysDate = Calendar.getInstance();
        long timeSinceLastManta = todaysDate.getTime().getTime() - mostRecentMantaDate.getTime();
        long numberOfDaysSinceLastManta = TimeUnit.MILLISECONDS.toDays(timeSinceLastManta);
        int mantaThreshold;
        if ( reefModeQ ) {
            mantaThreshold = MainActivity.daysToMantaTowIntensiveControlReef;
        } else {
            mantaThreshold = MainActivity.daysToMantaTowMaintenanceReef;
        }
        boolean mantaDue = (numberOfDaysSinceLastManta > mantaThreshold);

        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");

        String reefMode;
        if ( reefModeQ ) {
            reefMode = "Maintenance Mode";
        } else {
            reefMode = "Intensive Mode";
        }

        reefInfoOverlayReefName.setText( reef.getReefName() );
        reefInfoOverlayReefMode.setText( "Reef Mode (calculated): " + reefMode );
        reefInfoOverlayNumberOfSites.setText( "Number of Sites at Reef: " + Integer.toString( siteList.size() ) );
        reefInfoOverlayLastCullDate.setText( "Last cull date: " + simpleDate.format( mostRecentDiveDate ) );
        reefInfoOverlayLastMantaDate.setText( "Last manta tow date: " + simpleDate.format( mostRecentMantaDate ) );
        reefInfoOverlayNumberOfDaysSinceLastManta.setText( "Number of days since last manta tow: " + Long.toString( numberOfDaysSinceLastManta ) );
        reefInfoOverlayMantaDue.setText( "Manta tow due? " + Boolean.toString( mantaDue ) );
        reefInfoOverlayTotalCOTSCulledDuringLastCull.setText( "Total COTS culled during last cull: " + Integer.toString( totalCOTSCount ) );
        reefInfoOverlayTotalCOTSSeenDuringLastManta.setText( "Total COTS seen during last manta: " + Integer.toString( totalMantaCount ) );
        reefInfoOverlayAnyMantaScarsSeenDuringLastManta.setText( "Any scars seen during last manta: " + Boolean.toString( anyMantaScars ) );
        reefInfoOverlayNumberOfSitesWithMantaCOTSOrScars.setText( "Number of Sites with COTS or scars during manta: " + Integer.toString( numberOfSitesWithMantaCOTSOrScars ) );

        ((Activity) context).findViewById( R.id.reef_info_overlay ).setVisibility( View.VISIBLE );

        (((Activity) context).findViewById(R.id.loadingPanel)).setVisibility(View.GONE);

    }

    public void DisplayWorkplanInfo( String workplanText ){

        if ( workplanPanelView.getVisibility() == View.VISIBLE ) {

            workplanPanelView.setVisibility(View.GONE);
            infoPanelView.setVisibility(View.VISIBLE);
            // Ask mapFragment to turn off all the markers
            generateWorkPlanButton.setText("Generate Workplan");

        } else {

            workplanPanelView.setVisibility(View.VISIBLE);
            infoPanelView.setVisibility(View.GONE);
            reefInfoOverlayWorkPlan.setText( workplanText );
            // Ask mapFragment to turn off all the markers
            generateWorkPlanButton.setText("Return to info view");

        }

    }


    private DisplayInfoFragmentLoadDataButtonPressListener displayInfoFragmentLoadDataButtonPressListener;
    private DisplayInfoFragmentGenerateWorkplanButtonPressListener displayInfoFragmentGenerateWorkplanButtonPressListener;
    private DisplayInfoFragmentReadyListener displayInfoFragmentReadyListener;


    private void sendLoadDataButtonPress() {

        if  ( displayInfoFragmentLoadDataButtonPressListener != null ) {

            displayInfoFragmentLoadDataButtonPressListener.sendLoadDataButtonPress();

        }

    }

    private void sendGenerateWorkplanButtonPress() {

        if  ( displayInfoFragmentGenerateWorkplanButtonPressListener != null ) {

            displayInfoFragmentGenerateWorkplanButtonPressListener.sendGenerateWorkplanButtonPress();

        }

    }

    private void sendInfoFragment( DisplayInfo displayInfo ){

        if  ( displayInfoFragmentReadyListener != null ) {

            displayInfoFragmentReadyListener.sendInfoFragment( displayInfo );

        }

    }

    public interface DisplayInfoFragmentLoadDataButtonPressListener {

        public void sendLoadDataButtonPress();

    }

    public interface DisplayInfoFragmentGenerateWorkplanButtonPressListener {

        public void sendGenerateWorkplanButtonPress();

    }

    public interface DisplayInfoFragmentReadyListener {

        public void sendInfoFragment( DisplayInfo displayInfo );

    }


    @Override
    public void onAttach( Context context ) {

        super.onAttach( context );

        try {

            displayInfoFragmentLoadDataButtonPressListener = ( DisplayInfoFragmentLoadDataButtonPressListener ) context;

            displayInfoFragmentGenerateWorkplanButtonPressListener = ( DisplayInfoFragmentGenerateWorkplanButtonPressListener ) context;

            displayInfoFragmentReadyListener = ( DisplayInfoFragmentReadyListener ) context;


        } catch ( ClassCastException e ) {

            throw new ClassCastException( context.toString()+ " must implement displayMapFragmentMarkerTouchListener" );

        }

    }

    @Override
    public void onDetach() {

        displayInfoFragmentLoadDataButtonPressListener = null;

        displayInfoFragmentGenerateWorkplanButtonPressListener = null;

        super.onDetach();

    }


    @Override
    public void onClick( View view ) {

        switch ( view.getId() ) {

            case R.id.loadSurveillanceFileButton:

                sendLoadDataButtonPress();

                break;

            case R.id.generateWorkplanButton:

                sendGenerateWorkplanButtonPress();

                break;

            default:

        }

    }

}
