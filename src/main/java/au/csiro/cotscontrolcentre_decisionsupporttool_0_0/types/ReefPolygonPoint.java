package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.database.Cursor;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.ReefPolygonsEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class ReefPolygonPoint {

    // Private Variables
    // These are the components intrinsic to a ReefPolygonPoint, rather than values that should be
    // derived from the Reef.

    private int _reefPolygonsId;
    private int _reefPolygonPointOrder;
    private double _reefPolygonPointLatitude;
    private double _reefPolygonPointLongitude;
    private int _reefId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public ReefPolygonPoint(){
    }

    // Constructor based on passing all required values
    public ReefPolygonPoint(int reefPolygonsId, int reefPolygonPointOrder, double reefPolygonPointLatitude, double reefPolygonPointLongitude, int reefId ){

        this._reefPolygonsId = reefPolygonsId;
        this._reefPolygonPointOrder = reefPolygonPointOrder;
        this._reefPolygonPointLatitude = reefPolygonPointLatitude;
        this._reefPolygonPointLongitude = reefPolygonPointLongitude;
        this._reefId = reefId;

    }

    // Constructor based on passing a single Cursor to a SiteEntry
    public ReefPolygonPoint(Cursor cursor) {

        this._reefPolygonsId = cursor.getInt(cursor.getColumnIndex(ReefPolygonsEntry.REEF_POLYGONS_TABLE_COLUMN_ID));
        this._reefPolygonPointOrder = cursor.getInt(cursor.getColumnIndex(ReefPolygonsEntry.REEF_POLYGONS_TABLE_COLUMN_POINT_ORDER));
        this._reefPolygonPointLatitude = cursor.getDouble(cursor.getColumnIndex(ReefPolygonsEntry.REEF_POLYGONS_TABLE_COLUMN_POINT_LATITUDE));
        this._reefPolygonPointLongitude = cursor.getDouble(cursor.getColumnIndex(ReefPolygonsEntry.REEF_POLYGONS_TABLE_COLUMN_POINT_LONGITUDE));
        this._reefId = cursor.getInt(cursor.getColumnIndex(ReefPolygonsEntry.REEF_POLYGONS_TABLE_COLUMN_REEF_ID));

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each ReefPolygonPoint object.

    // Getting id
    public int getReefPolygonsId(){
        return this._reefPolygonsId;
    }

    // Getting reefName
    public int getReefPolygonPointOrder(){
        return this._reefPolygonPointOrder;
    }

    // Getting reefId
    public double getReefPolygonPointLatitude(){
        return this._reefPolygonPointLatitude;
    }

    // Getting latitude
    public double getReefPolygonPointLongitude(){ return this._reefPolygonPointLongitude; }

    // Getting longitude
    public int getReefId(){ return this._reefId; }

}
