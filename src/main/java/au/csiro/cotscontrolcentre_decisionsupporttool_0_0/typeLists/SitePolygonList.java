package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.SitePolygon;

//
// The purpose of these custom TypeList types, encompassing each of our custom Types, is to provide
// functionality related to the physical characteristics of the custom Types. For example, it
// makes sense to be able to call a method that returns the most recent Dives performed at each
// Site in a list of Dives.
//
// There is an additional major benefit, in that we can store a HashMap of the indices of the
// TypeList when it is created. This allows us to write functions that can quickly find at item
// either by its index or by its Type identifier number. In the original data tables these two
// numbers are the same: i.e. the 131st Site has a SiteId of 131. However, in a generic SiteList
// this is not the case - if we make a list of Sites at a Reef, the Site with SiteId 131 might be
// the 10th item in that particular list.
//
// Implementation note: It is not clear to me whether it is more efficient to create a HashMap of
// < typeId, _typeListIndex >, as I do in this code, and then use that to look up the position of an
// item with a given typeId in the typeList, or whether I should make a HashMap <typeId, Type Object>
// directly. This way works for now, but we could do some performance testing later.
//
// Given the commonality between these TypeList classes, I should make a BaseTypeList class and
// then only include the code specific to each Type within the TypeClass, but I haven't got
// around to this simplification yet.
//

public class SitePolygonList implements
        Iterable<SitePolygon> {

    private List<SitePolygon> _sitePolygonList = new ArrayList<>();
    private HashMap<Integer, Integer> _sitePolygonListIndex = new HashMap< Integer, Integer >();

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public SitePolygonList(){
    }

    // Constructor
    public SitePolygonList( List<SitePolygon> sitePolygonList ){

        this._sitePolygonList = sitePolygonList;

        for ( int i = 0; i < sitePolygonList.size(); i++ ) {

            this._sitePolygonListIndex.put( sitePolygonList.get( i ).getSiteId(), i );

        }

    }

    //
    // SETTERS
    //

    public void add( SitePolygon sitePolygon ) {

        this._sitePolygonList.add( sitePolygon );

        this._sitePolygonListIndex.put( sitePolygon.getSiteId(), this._sitePolygonList.size() - 1 );

    }

    public void clear() {

        this._sitePolygonList.clear();

        this._sitePolygonListIndex.clear();

    }


    //
    // GETTERS
    //

    // Get SitePolygon from sitePolygonList by siteId
    public SitePolygon getSitePolygonBySiteId( int siteId ){

        Integer sitePolygonTableIndex  = this._sitePolygonListIndex.get( siteId );

        if ( sitePolygonTableIndex != null ) {

            return _sitePolygonList.get(sitePolygonTableIndex);

        } else {

            return null;

        }

    }

    // Get a list of SitePolygon from sitePolygonList by a list of siteIds
    public SitePolygonList getSitePolygonsBySiteIds( List<Integer> siteIds ){

        SitePolygonList returnSitePolygonList = new SitePolygonList();

        for ( int siteId : siteIds ) {

            int sitePolygonIndex = this._sitePolygonListIndex.get( siteId );

            returnSitePolygonList.add( this._sitePolygonList.get( sitePolygonIndex ) );

        }

        return returnSitePolygonList;

    }

    // Get copy of SitePolygonList
    public List<SitePolygon> getSitePolygonListCopy(){

        // Only ever return a copy of the _mantaList so we don't muck up the ordering of the
        // <key,value> pairs in _mantaListIndex

        List<SitePolygon> sitePolygonListCopy = new ArrayList<SitePolygon>( this._sitePolygonList );

        return sitePolygonListCopy;

    }

    public SitePolygon get( int i ) {

        return this._sitePolygonList.get( i );

    }

    //
    // DERIVED VALUES
    //

    public boolean isEmpty() {

        return this._sitePolygonList.isEmpty();

    }

    //
    // Iterators
    //

    @NonNull
    @Override
    public Iterator<SitePolygon> iterator() {

        return this._sitePolygonList.iterator();

    }

}
