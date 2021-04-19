package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import java.util.Locale;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.ReefEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Reef {

    // Private Variables
    // These are the components intrinsic to a Reef, rather than values that should be derived
    // from the Dive or Voyage that reports its activities relative to a Reef.
    // For instance - the Reef latitude and longitude are intrinsic to a Reef.
    // On the other hand, the number of CoTS removed on a specific Dive at that Reef are a
    // Dive characteristic. We do not want to record the number of CoTS removed at a given Reef,
    // but we may want to provide a method that can return the total number of CoTS ever removed
    // at that Reef.

    private int _reefId;
    private String _reefReefName;
    private String _reefReefId;
    private double _reefLatitude;
    private double _reefLongitude;

    // Empty constructor
    public Reef(){
    }

    // Constructor based on passing all required values
    public Reef(int reefId, String reefReefName, String reefReefId, double reefLatitude, double reefLongitude ){

        this._reefId = reefId;
        this._reefReefName = reefReefName;
        this._reefReefId = reefReefId;
        this._reefLatitude = reefLatitude;
        this._reefLongitude = reefLongitude;

    }

    // Constructor based on passing a single Cursor to a SiteEntry
    public Reef(Cursor cursor) {

        this._reefId = cursor.getInt(cursor.getColumnIndex(ReefEntry.REEF_TABLE_COLUMN_ID));
        this._reefReefName = cursor.getString(cursor.getColumnIndex(ReefEntry.REEF_TABLE_COLUMN_REEF_NAME));
        this._reefReefId = cursor.getString(cursor.getColumnIndex(ReefEntry.REEF_TABLE_COLUMN_REEF_ID));
        this._reefLatitude = cursor.getDouble(cursor.getColumnIndex(ReefEntry.REEF_TABLE_COLUMN_LATITUDE));
        this._reefLongitude = cursor.getDouble(cursor.getColumnIndex(ReefEntry.REEF_TABLE_COLUMN_LONGITUDE));

    }

    // We do provide individual public getter functions so that pieces of data can be read from
    // each Rhis object.

    // Getting id
    public int getReefId(){
        return this._reefId;
    }

    // Getting reefName
    public String getReefName(){
        return this._reefReefName;
    }

    // Getting reefId
    public String getReefReefId(){
        return this._reefReefId;
    }

    // Getting latitude
    public double getReefLatitude(){ return this._reefLatitude; }

    // Getting longitude
    public double getReefLongitude(){ return this._reefLongitude; }

    public String formattedIcon() {
        return ( "R" );
    }

    public String formattedHeading() {
        return ( this._reefReefName );
    }

    public String formattedSubHeading() {
        return ( "Lat: " + String.format(Locale.US, "%.2f", this.getReefLatitude()) + ", Long: " + String.format(Locale.US, "%.2f", this.getReefLongitude()) );
    }

//    public int totalRemoved() {
//
//        List<Site> SiteList = getSites();
//
//        int totalRemovedResult = 0;
//
//        for ( Site site : SiteList ) {
//
//            List<Dive> containingDiveList = site.getContainingDives();
//
//            for ( Dive dive : containingDiveList ) {
//                totalRemovedResult += dive.totalCoTS();
//            }
//
//        }
//
//        return totalRemovedResult;
//
//    }

//    public int avoidedDamage() {
//
//        List<Site> SiteList = getSites();
//
//        int avoidedDamageResult = 0;
//
//        for ( Site site : SiteList ) {
//
//            List<Dive> containingDiveList = site.getContainingDives();
//
//            for ( Dive dive : containingDiveList ) {
//                avoidedDamageResult += dive.avoidedDamage();
//            }
//
//        }
//
//        return avoidedDamageResult;
//
//    }


//    public String formattedTotalRemoved() {
//
//        return FormatNumbers.formatNumber( totalRemoved() );
//
//    }
//
//    public String formattedAvoidedDamage() {
//
//        return FormatNumbers.formatNumber( avoidedDamage() );
//
//    }

}
