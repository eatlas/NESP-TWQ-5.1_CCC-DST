package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Vessel {

    // Private Variables
    // These are the components intrinsic to a Vessel, rather than values that should be derived
    // from the Voyages on which a Vessel was involved.

    private int _vesselId;
    private String _vesselName;
    private String _vesselShortName;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public Vessel(){
    }

    // Constructor based on passing all required values
    public Vessel(int vesselId, String vesselName, String vesselShortName ){

        this._vesselId = vesselId;
        this._vesselName = vesselName;
        this._vesselShortName = vesselShortName;
    }

    // Constructor based on passing a single Cursor to a SiteEntry
    public Vessel(Cursor cursor) {

        this._vesselId = cursor.getInt(cursor.getColumnIndex(COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_ID));
        this._vesselName = cursor.getString(cursor.getColumnIndex(COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_VESSEL_NAME));
        this._vesselShortName = cursor.getString(cursor.getColumnIndex(COTSDataContract.VesselEntry.VESSEL_TABLE_COLUMN_VESSEL_SHORT_NAME));

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each Vessel object.

    // Getting id
    public int getVesselId(){
        return this._vesselId;
    }

    // Getting vesselName
    public String getVesselName(){
        return this._vesselName;
    }

    // Getting vesselShortName
    public String getVesselShortName(){
        return this._vesselShortName;
    }

}
