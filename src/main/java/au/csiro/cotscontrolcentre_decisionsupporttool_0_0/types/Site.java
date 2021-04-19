package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.R;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data.COTSDataContract.SiteEntry;

/**
 * Created by fle125 on 31/03/2017.
 */

public class Site {

    // Private Variables
    // These are the components intrinsic to a Site, rather than values that should be derived
    // from the Dive or Voyage that reports its activities relative to a Site.
    // For instance - the Site latitude and longitude are intrinsic to a Site.
    // On the other hand, the number of CoTS removed on a specific Dive at that Site are a
    // Dive characteristic. We do not want to record the number of CoTS removed at a given Site,
    // but we may want to provide a method that can return the total number of CoTS ever removed
    // at that Site.

    private int _siteId;
    private String _siteName;
    private double _siteLatitude;
    private double _siteLongitude;
    private int _reefId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public Site(){
    }

    // Constructor
    public Site(int siteId, String siteName, double siteLatitude, double siteLongitude, int reefId ){

        this._siteId = siteId;
        this._siteName = siteName;
        this._siteLatitude = siteLatitude;
        this._siteLongitude = siteLongitude;
        this._reefId = reefId;

    }

    // Constructor
    public Site(Cursor cursor) {

        this._siteId = cursor.getInt(cursor.getColumnIndex(SiteEntry.SITE_TABLE_COLUMN_ID));
        this._siteName = cursor.getString(cursor.getColumnIndex(SiteEntry.SITE_TABLE_COLUMN_SITE_NAME));
        this._siteLatitude = cursor.getDouble(cursor.getColumnIndex(SiteEntry.SITE_TABLE_COLUMN_LATITUDE));
        this._siteLongitude = cursor.getDouble(cursor.getColumnIndex(SiteEntry.SITE_TABLE_COLUMN_LONGITUDE));
        this._reefId = cursor.getInt(cursor.getColumnIndex(SiteEntry.SITE_TABLE_COLUMN_REEF_ID));

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each Site object.

    // Getting id
    public int getSiteId(){
        return this._siteId;
    }

    // Getting siteName
    public String getSiteName(){
        return this._siteName;
    }

    // Getting latitude
    public double getSiteLatitude(){
        return this._siteLatitude;
    }

    // Getting longitude
    public double getSiteLongitude(){
        return this._siteLongitude;
    }

    // Getting Reef Id
    public Integer getReefId(){
        return this._reefId;
    }

}
