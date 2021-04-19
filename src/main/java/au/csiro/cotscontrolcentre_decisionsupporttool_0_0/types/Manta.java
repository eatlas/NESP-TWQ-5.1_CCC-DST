package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.MainActivity;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.MantaEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Manta implements Comparable<Manta> {

    // Private Variables
    // These are the components intrinsic to a Manta, rather than values that should be derived
    // from the Site at which the Manta occurred or the Voyage that the Manta occurred on.
    // For instance - the Manta Start and Stop latitude and longitude are intrinsic to a Manta.
    // On the other hand, the name of the Reef at which the Manta occurred is a function of the Site
    // near which the Manta occurred.

    private int _mantaId;
    private String _mantaDate;
    private double _mantaStartLat;
    private double _mantaStartLong;
    private double _mantaStopLat;
    private double _mantaStopLong;
    private double _mantaMeanLat;
    private double _mantaMeanLong;
    private int _mantaCots;
    private String _mantaScars;
    private int _siteId;
    private int _voyageId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public Manta(){
    }

    // Constructor based on passing all required values
    public Manta(int mantaId, String mantaDate, double mantaStartLat, double mantaStartLong, double mantaStopLat, double mantaStopLong, double mantaMeanLat, double mantaMeanLong, int mantaCots, String mantaScars, Integer siteId, Integer voyageId ){

        this._mantaId = mantaId;
        this._mantaDate = mantaDate;
        this._mantaStartLat = mantaStartLat;
        this._mantaStartLong = mantaStartLong;
        this._mantaStopLat = mantaStopLat;
        this._mantaStopLong = mantaStopLong;
        this._mantaMeanLat = mantaMeanLat;
        this._mantaMeanLong = mantaMeanLong;
        this._mantaCots = mantaCots;
        this._mantaScars = mantaScars;
        this._siteId = siteId;
        this._voyageId = voyageId;

    }

    // Constructor based on passing a single Cursor to a SiteEntry
    public Manta(Cursor cursor) {

        this._mantaId = cursor.getInt(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_ID));
        this._mantaDate = cursor.getString(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_DATE));
        this._mantaStartLat = cursor.getDouble(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_START_LAT));
        this._mantaStartLong = cursor.getDouble(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_START_LONG));
        this._mantaStopLat = cursor.getDouble(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_STOP_LAT));
        this._mantaStopLong = cursor.getDouble(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_STOP_LONG));
        this._mantaMeanLat = cursor.getDouble(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_MEAN_LAT));
        this._mantaMeanLong = cursor.getDouble(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_MEAN_LONG));
        this._mantaCots = cursor.getInt(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_COTS));
        this._mantaScars = cursor.getString(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_SCARS));
        this._siteId = cursor.getInt(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_SITE_ID));
        this._voyageId = cursor.getInt(cursor.getColumnIndex(MantaEntry.MANTA_TABLE_COLUMN_VOYAGE_ID));

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each Manta object.

    // Getting id
    public int getMantaId(){
        return this._mantaId;
    }

    // Getting date
    public String getMantaDate(){
        return this._mantaDate;
    }

    // At the moment, all dates are stored as YYYY-MM-DD strings, based on the structure available
    // to us in the GBRMPA Eye-On-The-Reef database exports. In future, this may be refined to
    // store date as UNIX time instead, to facilitate better range searches. For now, because we
    // often need to access the typeDate as a Date object for comparison or calculation, we provide
    // a method that: 1) checks that the typeDate is not null, and if it's OK, returns the typeDate
    // as a Date object.
    public Date getMantaDateAsDate() {

        SimpleDateFormat sdfDiveDate = new SimpleDateFormat( "yyyy-MM-dd");
        Date returnDate = null;

        try {

            returnDate = sdfDiveDate.parse( this._mantaDate );

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return returnDate;

    }

    // Getting start latitude
    public double getMantaStartLat(){
        return this._mantaStartLat;
    }

    // Getting start longitude
    public double getMantaStartLong(){
        return this._mantaStartLong;
    }

    // Getting stop latitude
    public double getMantaStopLat(){
        return this._mantaStopLat;
    }

    // Getting stop longitude
    public double getMantaStopLong(){
        return this._mantaStopLong;
    }

    // Getting stop latitude
    public double getMantaMeanLat(){
        return this._mantaMeanLat;
    }

    // Getting stop longitude
    public double getMantaMeanLong(){
        return this._mantaMeanLong;
    }

    public LatLng getMantaMeanLatLng() { return new LatLng( this._mantaMeanLat, this._mantaMeanLong ); }

    // Getting cots
    public int getMantaCOTS(){
        return this._mantaCots;
    }

    // Getting scars
    public String getMantaScars(){
        return this._mantaScars;
    }

    // Getting site id
    public int getSiteId(){
        return this._siteId;
    }

    // Getting voyage id
    public int getVoyageId(){
        return this._voyageId;
    }


    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort


    public long numberOfDaysSinceManta(){

        Date mantaDate = this.getMantaDateAsDate();

        Calendar todaysDate = Calendar.getInstance();
        long timeSinceManta = todaysDate.getTime().getTime() - mantaDate.getTime();
        long numberOfDaysSinceManta = TimeUnit.MILLISECONDS.toDays( timeSinceManta );

        return numberOfDaysSinceManta;

    }

    public boolean belowEcologicalThreshold(){

        boolean mantaCOTSAboveEcologicalThreshold = this._mantaCots <= MainActivity.ecologicalThresholdMantaCOTS;
        boolean mantaScarsAboveEcologicalThreshold = this._mantaScars.equals( "a" );

        return ( mantaCOTSAboveEcologicalThreshold && mantaScarsAboveEcologicalThreshold  );

    }


    //
    // COMPARATORS
    //

    @Override
    public int compareTo(Manta manta) {

        Date thisMantaDate = null;
        Date mantaMantaDate = null;
        SimpleDateFormat sdfDiveDate = new SimpleDateFormat( "yyyy-MM-dd");

        try {

            thisMantaDate = sdfDiveDate.parse(this._mantaDate);
            mantaMantaDate = sdfDiveDate.parse(manta.getMantaDate());

        } catch (Exception e) {

            e.printStackTrace();

        }


        if ( mantaMantaDate.after( thisMantaDate ) )

            return -1;

        else if ( mantaMantaDate.before( thisMantaDate ) )

            return 1;

        else

            return 0;

    }


    public static Comparator<Manta> getMantaDateComparator() {

        return new Comparator<Manta>() {

            public int compare(Manta manta1, Manta manta2) {

                Date manta1Date = null;
                Date manta2Date = null;
                SimpleDateFormat sdfDiveDate = new SimpleDateFormat("yyyy-MM-dd");

                try {

                    manta1Date = sdfDiveDate.parse(manta1.getMantaDate());
                    manta2Date = sdfDiveDate.parse(manta2.getMantaDate());

                } catch (Exception e) {

                    e.printStackTrace();

                }


                if ( manta1Date.after( manta2Date ) )

                    return -1;

                else if ( manta1Date.before( manta2Date ) )

                    return 1;

                else

                    return 0;

            }

        };

    }

    public static Comparator<Manta> getMantaCOTSNumberComparator() {

        return new Comparator<Manta>() {

            public int compare(Manta manta1, Manta manta2) {

                if ( manta1.getMantaCOTS() > manta2.getMantaCOTS() )

                    return -1;

                else if ( manta1.getMantaCOTS() < manta2.getMantaCOTS() )

                    return 1;

                else

                    return 0;

            }

        };

    }

}
