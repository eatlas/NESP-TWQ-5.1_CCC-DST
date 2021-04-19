package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SitePolygonPointList;

/**
 * Created by fle125 on 31/03/2017.
 */

public class SitePolygon {

    // Private Variables
    // These are the components intrinsic to a SitePolygon, rather than values that should be
    // derived from the associated Site.

    private List<Integer> _sitePolygonPointOrderList;
    private List<LatLng> _sitePolygonPointLatLngs;
    private Integer _siteId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public SitePolygon(){
    };

    // Constructor based on passing all required values
    public SitePolygon(int siteId, List<Integer> sitePolygonPointOrderList, List<Double> sitePolygonPointLatitudeList, List<Double> sitePolygonPointLongitudeList ){

        this._siteId = siteId;

        List<LatLng> latLngList = null;

        for (int i = 0;i<sitePolygonPointOrderList.size();i++) {
            latLngList.add( new LatLng( sitePolygonPointLatitudeList.get( i ), sitePolygonPointLatitudeList.get( i ) ) );
        }
        this._sitePolygonPointLatLngs = latLngList;

    }

    public SitePolygon(List<SitePolygonPoint> sitePolygonPointList){

        this._siteId = sitePolygonPointList.get(0).getSiteId();

        List<LatLng> latLngList = new ArrayList<LatLng>();

        for (SitePolygonPoint sitePolygonPoint: sitePolygonPointList) {
            latLngList.add( new LatLng( sitePolygonPoint.getSitePolygonPointLatitude(), sitePolygonPoint.getSitePolygonPointLongitude() ) );
        }

        this._sitePolygonPointLatLngs = latLngList;

    }

    public SitePolygon( SitePolygonPointList sitePolygonPointList ){

        this._siteId = sitePolygonPointList.get(0).getSiteId();

        List<LatLng> latLngList = new ArrayList<LatLng>();

        for (SitePolygonPoint sitePolygonPoint: sitePolygonPointList) {
            latLngList.add( new LatLng( sitePolygonPoint.getSitePolygonPointLatitude(), sitePolygonPoint.getSitePolygonPointLongitude() ) );
        }

        this._sitePolygonPointLatLngs = latLngList;

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each Dive object.

    // Getting id
    public int getSiteId(){
        return this._siteId;
    }

    // Getting site polygon
    public List<LatLng> getSitePolygonPoints(){
        return this._sitePolygonPointLatLngs;
    }
    
}
