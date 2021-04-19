package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.VoyageEntry;

import static java.lang.Math.random;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Voyage {

    // Private Variables
    // These are the components intrinsic to a Voyage, rather than simply a collection of Dives
    // For instance - the Voyage startDate and stopDate may be different than the first or last
    // date of Dives if weather was bad, etc.
    // On the other hand, it is not possible for the number of CoTS removed on a Voyage to be
    // different to the sum removed during the constituent Dives. We therefore do not want to
    // record the number of CoTS removed during a Voyage in the object itself, but we do want to
    // provide a method that returns the number of CoTS removed during the voyage by summing up
    // the number removed in each dive.

    private int _voyageId;
    private int _voyageVoyageNumber;
    private String _voyageStartDate;
    private String _voyageStopDate;
    private int _vesselId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public Voyage(){

    }

    // Constructor
    public Voyage(int voyageId, int voyageVoyageNumber, String voyageStartDate, String voyageStopDate, int vesselId){
        this._voyageId = voyageId;
        this._voyageVoyageNumber = voyageVoyageNumber;
        this._voyageStartDate = voyageStartDate;
        this._voyageStopDate = voyageStopDate;
        this._vesselId = vesselId;
    }

    // Constructor
    public Voyage(Cursor cursor) {

        this._voyageId = cursor.getInt(cursor.getColumnIndex(VoyageEntry.VOYAGE_TABLE_COLUMN_ID));
        this._voyageVoyageNumber = cursor.getInt(cursor.getColumnIndex(VoyageEntry.VOYAGE_TABLE_COLUMN_VOYAGE_NUMBER));
        this._voyageStartDate = cursor.getString(cursor.getColumnIndex(VoyageEntry.VOYAGE_TABLE_COLUMN_START_DATE));
        this._voyageStopDate = cursor.getString(cursor.getColumnIndex(VoyageEntry.VOYAGE_TABLE_COLUMN_STOP_DATE));
        this._vesselId = cursor.getInt(cursor.getColumnIndex(VoyageEntry.VOYAGE_TABLE_COLUMN_VESSEL_ID));

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each Voyage object.

    // Getting vesselVoyage
    public int getVoyageId(){ return this._voyageId; }

    // Getting vesselVoyage
    public int getVoyageNumber(){ return this._voyageVoyageNumber; }

    // Getting startDate
    public String getStartDate(){
        return this._voyageStartDate;
    }

    // Getting stopDate
    public String getStopDate(){
        return this._voyageStopDate;
    }

    // Getting vessel
    public int getVesselId(){
        return this._vesselId;
    }

    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort

    public double meanLatitude() {

            return -16.9186 + random(); /* Cairns Latitude */

    }

    public double meanLongitude() {

            return 145.7781 + random(); /* Cairns Longitude */

    }

}
