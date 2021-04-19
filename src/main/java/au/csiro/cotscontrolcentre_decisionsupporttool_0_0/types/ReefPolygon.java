package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fle125 on 31/03/2017.
 */

public class ReefPolygon {

    // Private Variables
    // These are the components intrinsic to a ReefPolygon, rather than values that should be
    // derived from the Reef.

    private List<Integer> _reefPolygonPointOrderList;
    private List<LatLng> _reefPolygonPointLatLngs;
    private Integer _reefId;

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public ReefPolygon(){
    };

    // Constructor based on passing all required values
    public ReefPolygon(int reefId, List<Integer> reefPolygonPointOrderList, List<Double> reefPolygonPointLatitudeList, List<Double> reefPolygonPointLongitudeList ){

        this._reefId = reefId;

        List<LatLng> latLngList = new ArrayList<>();

        for (int i = 0;i<reefPolygonPointOrderList.size();i++) {
            latLngList.add( new LatLng( reefPolygonPointLatitudeList.get( i ), reefPolygonPointLongitudeList.get( i ) ) );
        }
        this._reefPolygonPointLatLngs = latLngList;

    }

    // Constructor based on passing all required values
    public ReefPolygon(List<ReefPolygonPoint> reefPolygonPointList ){

        if ( !reefPolygonPointList.isEmpty() ){

            this._reefId = reefPolygonPointList.get( 0 ).getReefId();

            List<LatLng> latLngList = new ArrayList<>();

            for ( ReefPolygonPoint reefPolygonPoint : reefPolygonPointList ){

                latLngList.add( new LatLng( reefPolygonPoint.getReefPolygonPointLatitude(), reefPolygonPoint.getReefPolygonPointLongitude() ) );

            }

            this._reefPolygonPointLatLngs = latLngList;

        }

    }

    //
    // GETTERS
    //

    // We provide individual public getter functions so that pieces of data can be read from
    // each ReefPolygon object.

    // Getting id
    public int getReefId(){
        return this._reefId;
    }

    // Getting reef polygon
    public List<LatLng> getReefPolygonPoints(){
        return this._reefPolygonPointLatLngs;
    }
    
}
