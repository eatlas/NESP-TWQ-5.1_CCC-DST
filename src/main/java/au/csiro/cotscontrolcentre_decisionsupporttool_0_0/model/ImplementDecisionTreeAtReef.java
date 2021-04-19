package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.MainActivity;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Dive;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.DiveList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Manta;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.MantaList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Site;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SiteList;

//
// This class contains the logic required to implement the decision tree at a Reef. When a new
// instance is created, it must be passed:
//      1) the name of the Reef;
//      2) the siteList of all Sites at the Reef;
//      3) the diveList of all Dives at the Reef since the last Manta tow
//      4) the most recent Mantas at the Reef
//
// The goal of the class is to ascertain:
// 1) Whether a Reef is in Maintenance or Intensive Control Mode
// 2) Whether the Reef needs to be manta towed
// 3) Whether the Reef needs to be culled
// 4) If so, in what order should the Sites be culled
//
// These decisions are underpinned by the logic contained in the "simple decision tree" in the
// reports Fletcher et al. (2020) and Fletcher (2020).
//
//

public class ImplementDecisionTreeAtReef {

    private String _reefName;
    private SiteList _siteList;
    private DiveList _diveList;
    private MantaList _mantaList;

    // Empty constructor
    public ImplementDecisionTreeAtReef(){
    }

    public ImplementDecisionTreeAtReef( String reefName, SiteList siteList, DiveList diveList, MantaList mantaList ){

        this._reefName = reefName;
        this._siteList = siteList;
        this._diveList = diveList;
        this._mantaList = mantaList;

    }

    public String ImplementDecisionTreeAtReefAndFindControlTasks(){

        // Check whether Reef is in maintenance mode or intensive mode

        String returnString = "";

        if ( maintenanceModeQ() ) {

            // If Maintenance Reef
            returnString = provide_MaintenanceReef_Tasks();

        } else {

            // If Intensive Control Reef
            returnString = provide_IntensiveControlReef_Tasks();

        }

        return returnString;

    }

    public List<Integer> ImplementDecisionTreeAtReefAndFindSiteIdsToBeControlledInOrder(){

        List<Integer> returnSiteIdList = new ArrayList<>();

        if ( maintenanceModeQ() ) {

            // If Maintenance Reef don't return anything

        } else {

            // If Intensive Control Reef, return the SiteIds of Sites to be controlled in order
            returnSiteIdList = provide_IntensiveControlReef_SiteIds( true );

        }

        return returnSiteIdList;

    }

    private String provide_IntensiveControlReef_Tasks() {

        String returnIntensiveControlReefTasks = "";

        List<Integer> intensiveControlReefSiteIds = provide_IntensiveControlReef_SiteIds( true );

        if ( intensiveControlReefSiteIds.isEmpty() ) {

            returnIntensiveControlReefTasks = "All available Sites culled, move to next Reef.";

        } else if ( intensiveControlReefSiteIds.get(0) == -1 ) {

            returnIntensiveControlReefTasks = "Manta tow due. Manta tow Reef before generating Workplan.";

        } else {

            int i = 1;

            returnIntensiveControlReefTasks = "Begin culling sites in the following order: \n\n";

            for ( Integer siteId : intensiveControlReefSiteIds ) {

                Site site = this._siteList.getSiteBySiteId( siteId );

                returnIntensiveControlReefTasks = returnIntensiveControlReefTasks + Integer.toString(i) + ": Site " + site.getSiteName() + " \n\n";

                i++;

            }

        }

        return returnIntensiveControlReefTasks;

    }


    private List<Integer> provide_IntensiveControlReef_SiteIds( boolean forceDespiteManta ) {

        List<Integer> returnIntensiveControlReefSiteIds = new ArrayList<>();

        boolean mantaTowRequired = true;

        // We provide the user the option to force the calculation of Site orders even if a Manta
        // is required.
        if ( forceDespiteManta ) {

            mantaTowRequired = false;

        } else if ( this._mantaList.isEmpty() ) { // Check if this is the first ever Voyage to Reef by checking whether there are any Mantas

            // If so, flag manta tow required
            mantaTowRequired = true;

        } else {

            // Check whether Reef has been manta towed within the manta tow period.
            mantaTowRequired = ( numberOfDaysSinceLastManta() > MainActivity.daysToMantaTowIntensiveControlReef );

        }

        // If manta tow is required, we'll flag that as the first action, after which additional
        // input will be provided, but we don't provide any other advice right now.
        if ( mantaTowRequired ) {

            returnIntensiveControlReefSiteIds.add( -1 );

            // Don't do anything and return a null List

        } else { // If manta tow is not required

            List<Integer> siteIdsOfSitesToBeCulled = new ArrayList<>();
            List<Dive> diveSitesToBeCulledBasedOnLastCull = new ArrayList<>();
            List<Manta> mantaSitesToBeCulledBasedOnLastManta = new ArrayList<>();

            // Iterate through each Site at the Reef
            for ( Site site : this._siteList ) {

                // Get the mantas: 1) from the most recent Voyage during which Mantas were
                // collected; 2) at the current Site. If no Mantas have been conducted at this Reef,
                // or non assigned to this Site, the result will be an empty list.
                // NOTE THAT THE ORDER OF THESE COMMANDS IS LOGICALLY IMPORTANT
                MantaList mostRecentMantasAtSite = this._mantaList.getMantasOnMostRecentVoyage().getMantasBySiteId(site.getSiteId());

                // Get the most recent Dives at this Site from the most recent Voyage during which
                // a Dive was collected at at this Site.
                // NOTE THAT THE ORDER OF THESE COMMANDS IS LOGICALLY IMPORTANT
                DiveList mostRecentDivesAtSite = this._diveList.getDivesBySiteId(site.getSiteId()).getDivesOnMostRecentVoyage();

                // Now, if: 1) there have been Dives at the Site; then 2) find the Date of most recent
                // Dive at the Site, and 3) if there was also a manta at the Site; 3a) find out if
                // the Dive is more recent than the most recent Manta tow, then 3b) if the Dive
                // was conducted more than the site revisitation schedule ago, then 3c) add the
                // Dives to diveSitesToBeCulledBasedOnLastCull; otherwise, if 4) there was never
                // any Manta conducted at the Site (which should never be the case), 4a) if the Dive
                // was conducted more than the site revisitation schedule ago, then 4b) add the
                // Dives to diveSitesToBeCulledBasedOnLastCull
                if (!mostRecentDivesAtSite.isEmpty()) {

                    // Create a composite Dive from however many Dives occurred at the Site during
                    // the most recent Voyage
                    Dive compositeDive = mostRecentDivesAtSite.createCompositeDive();

                    if (!mostRecentMantasAtSite.isEmpty()) { // If there are both Mantas and Dives at the Site

                        // Create composite Manta
                        Manta compositeManta = mostRecentMantasAtSite.createCompositeManta();

                        if (compositeDive.getDiveDateAsDate().after(compositeManta.getMantaDateAsDate())) { // If the Dive is more recent than the Manta

                            if (compositeDive.numberOfDaysSinceDive() > MainActivity.daysToCullSiteAtIntensiveControlReef) { // If the Dive was Dived long enough ago

                                if ( !compositeDive.belowEcologicalThreshold() ) { // Check if the Site is above the Ecological Threshold

                                    diveSitesToBeCulledBasedOnLastCull.add(compositeDive);

                                }

                            } // Else the Site has been Dived too recently, so do nothing

                        } else { // If the Manta is more recent than the Dive

                            if ( !compositeManta.belowEcologicalThreshold() ) { // Check if the Site is above the Ecological Threshold

                                mantaSitesToBeCulledBasedOnLastManta.add(compositeManta); // If so, add it to the list

                            }
                        }

                    } else { // If there are only Dives at the Site (which should never happen, but who knows)


                        if ( compositeDive.numberOfDaysSinceDive() > MainActivity.daysToCullSiteAtIntensiveControlReef ) { // If the Dive was Dived long enough ago

                            if ( !compositeDive.belowEcologicalThreshold() ) { // Check if the Site is above the Ecological Threshold

                                diveSitesToBeCulledBasedOnLastCull.add(compositeDive);

                            }

                        } // Else the Site has been Dived too recently, so do nothing

                    }

                } else { // Else if there are no Dives at the Site

                    if ( !mostRecentMantasAtSite.isEmpty() ) { // If there is a Manta at the Site

                        // Create composite Manta
                        Manta compositeManta = mostRecentMantasAtSite.createCompositeManta();

                        if ( !compositeManta.belowEcologicalThreshold() ) { // Check if the Site is above the Ecological Threshold

                            mantaSitesToBeCulledBasedOnLastManta.add(mostRecentMantasAtSite.createCompositeManta());

                        }
                    }

                }

            }

            // Now, sort the two lists, highest COTS cull numbers and highest Mantas first
            Collections.sort( diveSitesToBeCulledBasedOnLastCull, Dive.getDiveCullNumberComparator() );
            Collections.sort( mantaSitesToBeCulledBasedOnLastManta, Manta.getMantaCOTSNumberComparator() );

            // Then add the siteIds of the Dives first, then the Mantas
            for ( Dive dive : diveSitesToBeCulledBasedOnLastCull ){

                siteIdsOfSitesToBeCulled.add ( dive.getSiteId() );

            }

            for ( Manta manta : mantaSitesToBeCulledBasedOnLastManta ){

                siteIdsOfSitesToBeCulled.add ( manta.getSiteId() );

            }

            if ( siteIdsOfSitesToBeCulled.isEmpty() ) {

                // Don't provide anything

            } else {

                returnIntensiveControlReefSiteIds = siteIdsOfSitesToBeCulled;

            }

        }

        return returnIntensiveControlReefSiteIds;

    }


    private String provide_MaintenanceReef_Tasks() {

        String returnString = "";

        boolean mantaTowRequired = true;

        List<Manta> mantaListCopy = this._mantaList.getMantaListCopy();

        Collections.sort( mantaListCopy, Manta.getMantaDateComparator() );

        Date latestMantaDate = mantaListCopy.get(0).getMantaDateAsDate();

        Calendar todaysDate = Calendar.getInstance();
        long timeSinceLastManta = todaysDate.getTime().getTime() - latestMantaDate.getTime();
        long numberOfDaysSinceLastManta = TimeUnit.MILLISECONDS.toDays( timeSinceLastManta );

        mantaTowRequired = ( numberOfDaysSinceLastManta > MainActivity.daysToMantaTowMaintenanceReef );

        if ( mantaTowRequired ) {

           returnString = "Manta tow out of date, perform comprehensive manta tow.";

        } else {

            returnString = "No manta tow required, move to next Reef.";

        }

        return returnString;

    }


    //
    // This function can be coded more efficiently by returning as soon as the conditions for
    // Maintenance Mode are not achieved, however we leave it like this for now because it's
    // readable. We can work on efficiency later
    //
    private boolean maintenanceModeQ (){

//        HashMap<Integer, Integer> mostRecentDiveIdAtEachSiteId = this._siteList.getMostRecentDiveAtEachSite( this._diveList );
//        HashMap<Integer, List<Integer>> mostRecentMantaIdsAtEachSiteId = this._siteList.getMostRecentMantasAtEachSite( this._mantaList );

        boolean returnMaintenanceModeQ = true;

        // Check if the Reef has no Mantas or Dives
        if ( this._diveList.isEmpty() && this._mantaList.isEmpty() ) {

           // If no, Reef is in Intensive Management Mode
           returnMaintenanceModeQ = true;

        } else { // Reef has at least one manta or cull

           boolean allSitesBelowEcologicalThreshold = true;

           // Check to see whether each Site at the Reef is below the Ecological Threshold by either
           // cull or Manta, depending on which is more recent
            for ( Site site : this._siteList ) {

                // Get the mantas: 1) from the most recent Voyage during which Mantas were
                // collected; 2) at the current Site. If no Mantas have been conducted at this Reef,
                // or non assigned to this Site, the result will be an empty list.
                // NOTE THAT THE ORDER OF THESE COMMANDS IS LOGICALLY IMPORTANT
                MantaList mostRecentMantasAtSite = this._mantaList.getMantasOnMostRecentVoyage().getMantasBySiteId(site.getSiteId());

                // Get the most recent Dives at this Site from the most recent Voyage during which
                // a Dive was collected at at this Site.
                // NOTE THAT THE ORDER OF THESE COMMANDS IS LOGICALLY IMPORTANT
                DiveList mostRecentDivesAtSite = this._diveList.getDivesBySiteId(site.getSiteId()).getDivesOnMostRecentVoyage();

                // Now, if: 1) there have been Dives at the Site; then 2) find the Date of most recent
                // Dive at the Site, and 3) if there was also a manta at the Site; 3a) find out if
                // the Dive is more recent than the most recent Manta tow, then 3b) if the Dive
                // was conducted more than the site revisitation schedule ago, then 3c) add the
                // Dives to diveSitesToBeCulledBasedOnLastCull; otherwise, if 4) there was never
                // any Manta conducted at the Site (which should never be the case), 4a) if the Dive
                // was conducted more than the site revisitation schedule ago, then 4b) add the
                // Dives to diveSitesToBeCulledBasedOnLastCull
                if (!mostRecentDivesAtSite.isEmpty()) {

                    // Create a composite Dive from however many Dives occurred at the Site during
                    // the most recent Voyage
                    Dive compositeDive = mostRecentDivesAtSite.createCompositeDive();

                    if (!mostRecentMantasAtSite.isEmpty()) { // If there are both Mantas and Dives at the Site

                        // Create composite Manta
                        Manta compositeManta = mostRecentMantasAtSite.createCompositeManta();

                        if (compositeDive.getDiveDateAsDate().after(compositeManta.getMantaDateAsDate())) { // If the Dive is more recent than the Manta

                            // Check if the CPUE exceeded the Ecological Threshold
                            allSitesBelowEcologicalThreshold = allSitesBelowEcologicalThreshold && compositeDive.belowEcologicalThreshold();

                        } else { // If the Manta is more recent than the Dive, then add the Site as part of the Manta list

                            // Check if the Manta exceeded the Ecological Threshold
                            allSitesBelowEcologicalThreshold = allSitesBelowEcologicalThreshold && compositeManta.belowEcologicalThreshold();

                        }

                    } else { // If there are only Dives at the Site (which should never happen, but who knows)

                        // Check if the CPUE exceeded the Ecological Threshold
                        allSitesBelowEcologicalThreshold = allSitesBelowEcologicalThreshold && compositeDive.belowEcologicalThreshold();

                    }

                } else { // Else if there are no Dives at the Site

                    // Create composite Manta
                    Manta compositeManta = mostRecentMantasAtSite.createCompositeManta();

                    // Check if the Manta exceeded the Ecological Threshold
                    allSitesBelowEcologicalThreshold = allSitesBelowEcologicalThreshold && compositeManta.belowEcologicalThreshold();

                }

            }

            // Check if all Sites are below the Ecological Threshold

           if ( allSitesBelowEcologicalThreshold ) {

               // If every Site is below the Ecological Threshold, then the Reef qualifies for Maintenance Mode
               returnMaintenanceModeQ = true;

           } else {

               // If not every Site is below the Ecological Threshold, then the Reef qualifies for Intensive Control Mode
               returnMaintenanceModeQ = false;

           }

        }

        // Return result
        return returnMaintenanceModeQ;

    }

    private long numberOfDaysSinceLastManta(){

        // In theory,
        // we should have been passed only the most recent Manta tows at this Reef, but
        // because this is not enforced, we test the mantaList and extract only the most
        // recent mantas from the most recent Voyage at which a Manta was completed.
        Date latestMantaDate = this._mantaList.getMostRecentMantaDate();

        Calendar todaysDate = Calendar.getInstance();
        long timeSinceLastManta = todaysDate.getTime().getTime() - latestMantaDate.getTime();
        long numberOfDaysSinceLastManta = TimeUnit.MILLISECONDS.toDays( timeSinceLastManta );

        return numberOfDaysSinceLastManta;

    }

}
