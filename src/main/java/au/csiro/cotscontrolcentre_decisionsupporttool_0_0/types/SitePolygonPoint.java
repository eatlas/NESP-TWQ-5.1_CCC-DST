package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import java.util.Comparator;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.SitePolygonsEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class SitePolygonPoint implements Comparable<SitePolygonPoint> {

    // Private Variables
    // These are the components intrinsic to a SitePolygonPoint, rather than values that should be
    // derived from the Site itself.

    private int _sitePolygonsId;
    private int _sitePolygonPointOrder;
    private double _sitePolygonPointLatitude;
    private double _sitePolygonPointLongitude;
    private int _siteId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public SitePolygonPoint(){
    }

    // Constructor based on passing all required values
    public SitePolygonPoint(int sitePolygonsId, int sitePolygonPointOrder, double sitePolygonPointLatitude, double sitePolygonPointLongitude, int siteId ){

        this._sitePolygonsId = sitePolygonsId;
        this._sitePolygonPointOrder = sitePolygonPointOrder;
        this._sitePolygonPointLatitude = sitePolygonPointLatitude;
        this._sitePolygonPointLongitude = sitePolygonPointLongitude;
        this._siteId = siteId;

    }

    // Constructor based on passing a single Cursor to a SiteEntry
    public SitePolygonPoint(Cursor cursor) {

        this._sitePolygonsId = cursor.getInt(cursor.getColumnIndex(SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_ID));
        this._sitePolygonPointOrder = cursor.getInt(cursor.getColumnIndex(SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_POINT_ORDER));
        this._sitePolygonPointLatitude = cursor.getDouble(cursor.getColumnIndex(SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_POINT_LATITUDE));
        this._sitePolygonPointLongitude = cursor.getDouble(cursor.getColumnIndex(SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_POINT_LONGITUDE));
        this._siteId = cursor.getInt(cursor.getColumnIndex(SitePolygonsEntry.SITE_POLYGONS_TABLE_COLUMN_SITE_ID));

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each SitePolygonPoint object.

    // Getting id
    public int getSitePolygonsId(){
        return this._sitePolygonsId;
    }

    // Getting SiteName
    public int getSitePolygonPointOrder(){
        return this._sitePolygonPointOrder;
    }

    // Getting SiteId
    public double getSitePolygonPointLatitude(){
        return this._sitePolygonPointLatitude;
    }

    // Getting latitude
    public double getSitePolygonPointLongitude(){ return this._sitePolygonPointLongitude; }

    // Getting longitude
    public int getSiteId(){ return this._siteId; }


    //
    // COMPARATORS
    //

    @Override
    public int compareTo( SitePolygonPoint sitePolygonPoint ) {

        if ( sitePolygonPoint.getSitePolygonPointOrder() < this.getSitePolygonPointOrder() )

            return -1;

        else if ( sitePolygonPoint.getSitePolygonPointOrder() < this.getSitePolygonPointOrder()  )

            return 1;

        else

            return 0;

    }

    public static Comparator<SitePolygonPoint> getPolygonPointComparator() {

        return new Comparator<SitePolygonPoint>() {

            public int compare(SitePolygonPoint sitePolygonPoint1, SitePolygonPoint sitePolygonPoint2) {

                if ( sitePolygonPoint1.getSitePolygonPointOrder() < sitePolygonPoint2.getSitePolygonPointOrder() )

                    return -1;

                else if ( sitePolygonPoint1.getSitePolygonPointOrder() > sitePolygonPoint2.getSitePolygonPointOrder()  )

                    return 1;

                else

                    return 0;


            }

        };

    }

}
