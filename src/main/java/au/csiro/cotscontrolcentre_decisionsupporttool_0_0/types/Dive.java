package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.MainActivity;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.DiveEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Dive implements Comparable<Dive> {

    // Private Variables
    // These are the components intrinsic to a Dive, rather than values that should be derived
    // from the Voyage on which the Dive occurred, or during a nearby Rhis, for instance.
    // For instance - the Dive date and the number of CoTS removed are intrinsic to a specific dive.
    // On the other hand, the vessel that the Dive occurred on is determined by the Voyage of which
    // it was a part. Similarly, the average coral cover is determined by the Rhis that is assigned
    // to this Dive. We do not want to record the vessel or the average coral cover as part of the
    // Dive, but we do want to provide the underlying structure across the various classes that
    // could return them, given a Dive, with its diveId , siteid and voyageId, a list of Voyages,
    // each with its vesselId, and a list of Rhis that occurred at siteIds.

    private int _diveId;
    private String _diveDate;
    private double _diveAverageDepth;
    private int _diveBottomTime;
    private int _diveLessThanFifteenCentimetres;
    private int _diveFifteenToTwentyFiveCentimetres;
    private int _diveTwentyFiveToFortyCentimetres;
    private int _diveGreaterThanFortyCentimetres;
    private int _siteId;
    private int _voyageId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public Dive(){
    }

    // Component constructor
    public Dive(int diveId, String diveDate, double diveAverageDepth, int diveBottomTime, int diveLessThanFifteenCentimetres, int diveFifteenToTwentyFiveCentimetres, int diveTwentyFiveToFortyCentimetres, int diveGreaterThanFortyCentimetres, int siteId, int voyageId){
        this._diveId = diveId;
        this._diveDate = diveDate;
        this._diveAverageDepth = diveAverageDepth;
        this._diveBottomTime = diveBottomTime;
        this._diveLessThanFifteenCentimetres = diveLessThanFifteenCentimetres;
        this._diveFifteenToTwentyFiveCentimetres = diveFifteenToTwentyFiveCentimetres;
        this._diveTwentyFiveToFortyCentimetres = diveTwentyFiveToFortyCentimetres;
        this._diveGreaterThanFortyCentimetres = diveGreaterThanFortyCentimetres;
        this._siteId = siteId;
        this._voyageId = voyageId;
    }

    // Cursor constructor
    public Dive(Cursor cursor) {
        this._diveId = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_ID));
        this._diveDate = cursor.getString(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_DATE));
        this._diveAverageDepth = cursor.getDouble(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_AVERAGE_DEPTH));
        this._diveBottomTime = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_BOTTOM_TIME));
        this._diveLessThanFifteenCentimetres = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_LESS_THAN_FIFTEEN_CENTIMETRES));
        this._diveFifteenToTwentyFiveCentimetres = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_FIFTEEN_TO_TWENTY_FIVE_CENTIMETRES));
        this._diveTwentyFiveToFortyCentimetres = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_TWENTY_FIVE_TO_FORTY_CENTIMETRES));
        this._diveGreaterThanFortyCentimetres = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_GREATER_THAN_FORTY_CENTIMETRES));
        this._siteId = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_SITE_ID));
        this._voyageId = cursor.getInt(cursor.getColumnIndex(DiveEntry.DIVE_TABLE_COLUMN_VOYAGE_ID));
    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each Dive object.

    public int getDiveId(){
        return this._diveId;
    }

    public String getDiveDate(){
        return this._diveDate;
    }

    // At the moment, all dates are stored as YYYY-MM-DD strings, based on the structure available
    // to us in the GBRMPA Eye-On-The-Reef database exports. In future, this may be refined to
    // store date as UNIX time instead, to facilitate better range searches. For now, because we
    // often need to access the diveDate as a Date object for comparison or calculation, we provide
    // a method that: 1) checks that the diveDate is not null, and if it's OK, returns the diveDate
    // as a Date object.
    public Date getDiveDateAsDate() {

        SimpleDateFormat sdfDiveDate = new SimpleDateFormat( "yyyy-MM-dd");
        Date returnDate = null;

        try {

            returnDate = sdfDiveDate.parse( this._diveDate );

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return returnDate;

    }

    // Getting averageDepth
    public double getDiveAverageDepth(){
        return this._diveAverageDepth;
    }

    // Getting bottomTime
    public int getBottomTime(){
        return this._diveBottomTime;
    }

    // Getting lessThanFifteenCentimetres
    public int getLessThanFifteenCentimetres(){
        return this._diveLessThanFifteenCentimetres;
    }

    // Getting fifteenToTwentyFiveCentimetres
    public int getFifteenToTwentyFiveCentimetres() { return this._diveFifteenToTwentyFiveCentimetres; }

    // Getting twentyFiveToFortyCentimetres
    public int getTwentyFiveToFortyCentimetres(){
        return this._diveTwentyFiveToFortyCentimetres;
    }

    // Getting greaterThanFortyCentimetres
    public int getGreaterThanFortyCentimetres(){
        return this._diveGreaterThanFortyCentimetres;
    }

    // Getting siteId
    public int getSiteId(){ return this._siteId; }

    // Getting siteId
    public int getVoyageId(){ return this._voyageId; }


    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort

    // Total number of CoTS removed
    public int totalCoTS(){

        int totalCoTS =
                this._diveLessThanFifteenCentimetres +
                this._diveFifteenToTwentyFiveCentimetres +
                this._diveTwentyFiveToFortyCentimetres +
                this._diveGreaterThanFortyCentimetres;

        return totalCoTS;
    }

    public double cpue(){

        double totalCoTS = this.totalCoTS();
        double bottomTime = this._diveBottomTime;
        double cpue = totalCoTS / bottomTime;
        return cpue;

    }

    //
    // This derived function estimates how much coral the COTS removed would consume each day,
    // in centimetres-squared, had they not been removed, based on their size class. The data is
    // approximate, and comes from fitting a quadratic damage function vs COTS size to the
    // data in Figure 4 of Keesing and Lucas (1992)
    public double avoidedDamage(){

        return (
                22 * this.getLessThanFifteenCentimetres() +
                57 * this.getFifteenToTwentyFiveCentimetres() +
                149 * this.getTwentyFiveToFortyCentimetres() +
                348 * this.getGreaterThanFortyCentimetres()
                ) / 10000d;
    }


    public long numberOfDaysSinceDive(){

        Date diveDate = this.getDiveDateAsDate();

        Calendar todaysDate = Calendar.getInstance();
        long timeSinceDive = todaysDate.getTime().getTime() - diveDate.getTime();
        long numberOfDaysSinceDive = TimeUnit.MILLISECONDS.toDays( timeSinceDive );

        return numberOfDaysSinceDive;

    }

    public boolean belowEcologicalThreshold(){

        return ( this.cpue() <= MainActivity.ecologicalThresholdCPUE );

    }


    //
    // COMPARATORS
    //

    @Override
    public int compareTo(Dive dive) {

        Date thisDiveDate = null;
        Date diveDiveDate = null;
        SimpleDateFormat sdfDiveDate = new SimpleDateFormat( "yyyy-MM-dd");

        try {

            thisDiveDate = sdfDiveDate.parse(this._diveDate);
            diveDiveDate = sdfDiveDate.parse(dive.getDiveDate());

        } catch (Exception e) {

            e.printStackTrace();

        }


        if ( diveDiveDate.after( thisDiveDate ) )

            return -1;

        else if ( diveDiveDate.before( thisDiveDate ) )

            return 1;

        else

            return 0;

    }

    public static Comparator<Dive> getDiveDateComparator() {

        return new Comparator<Dive>() {

            public int compare(Dive dive1, Dive dive2) {

                Date dive1Date = null;
                Date dive2Date = null;
                SimpleDateFormat sdfDiveDate = new SimpleDateFormat("yyyy-MM-dd");

                try {

                    dive1Date = sdfDiveDate.parse( dive1.getDiveDate() );
                    dive2Date = sdfDiveDate.parse( dive2.getDiveDate() );

                } catch (Exception e) {

                    e.printStackTrace();

                }


                if ( dive1Date.after( dive2Date ) )

                    return -1;

                else if ( dive1Date.before( dive2Date ) )

                    return 1;

                else

                    return 0;

            }

        };

    }


    public static Comparator<Dive> getDiveCullNumberComparator() {

        return new Comparator<Dive>() {

            public int compare(Dive dive1, Dive dive2) {

                if ( dive1.totalCoTS() > dive2.totalCoTS() )

                    return -1;

                else if ( dive1.totalCoTS() < dive2.totalCoTS() )

                    return 1;

                else

                    return 0;

            }

        };

    }

}
