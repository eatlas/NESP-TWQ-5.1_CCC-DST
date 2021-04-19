package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.RhisEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Rhis {

    // Private Variables
    // These are the components intrinsic to a Rhis, rather than a nearby Dive or Site or Reef where
    // the Rhis took place

    private int _rhisId;
    private String _rhisDate;
    private double _rhisCoralCover;
    private int _rhisCOTSAdult;
    private int _rhisCOTSJuvenile;
    private String _rhisVisibility;
    private int _siteId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public Rhis(){

    }

    // Constructor
    public Rhis(int rhisId, String rhisDate, double rhisCoralCover, int rhisCOTSAdult, int rhisCOTSJuvenile, String rhisVisibility, int siteId ){

        this._rhisId = rhisId;
        this._rhisDate = rhisDate;
        this._rhisCoralCover = rhisCoralCover;
        this._rhisCOTSAdult = rhisCOTSAdult;
        this._rhisCOTSJuvenile = rhisCOTSJuvenile;
        this._rhisVisibility = rhisVisibility;
        this._siteId = siteId;
    }

    // Constructor
    public Rhis(Cursor cursor) {
        this._rhisId = cursor.getInt(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_ID));
        this._rhisDate = cursor.getString(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_DATE));
        this._rhisCoralCover = cursor.getDouble(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_AVERAGE_CORAL_COVER));
        this._rhisCOTSAdult = cursor.getInt(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_COTS_ADULTS));
        this._rhisCOTSJuvenile = cursor.getInt(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_COTS_JUVENILES));
        this._rhisVisibility = cursor.getString(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_VISIBILITY));
        this._siteId = cursor.getInt(cursor.getColumnIndex(RhisEntry.RHIS_TABLE_COLUMN_SITE_ID));

    }

    //
    // SETTERS
    //

    // We do not provide any public setter functions because all data should be created
    // together, not edited piecemeal.

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each object.

    // Getting id
    public int getRhisId(){
        return this._rhisId;
    }

    // Getting date
    public String getRhisDate(){
        return this._rhisDate;
    }

    // Getting averageCoralCover
    public double getRhisCoralCover(){ return this._rhisCoralCover; }

    // Getting waterTemperature
    public int getRhisCOTSAdults(){
        return this._rhisCOTSAdult;
    }

    // Getting waterTemperature
    public int getRhisCOTSJuveniles(){
        return this._rhisCOTSJuvenile;
    }

    // Getting visibility
    public String getRhisVisibility(){
        return this._rhisVisibility;
    }

    // Getting siteId
    public Integer getSiteId(){
        return this._siteId;
    }

    //
    // DERIVED VALUES
    //

    // Although at the moment we do not provide derived values from these objects, in future we
    // are likely to.

    //
    // COMPARATORS
    //

    // Although at the moment we do not provide compartors for these objects, in future we
    // are likely to.

}
