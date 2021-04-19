package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.SitePolygon;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.SitePolygonPoint;

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

// This may seem a little pointless, but the goal here is to provide a couple of simple classes
// that allow all the SitePolygonPoints belonging to a specific siteId to be extracted, and
// also to extract them in sorted order

public class SitePolygonPointList implements
        Iterable<SitePolygonPoint> {

    private List<SitePolygonPoint> _sitePolygonPointList = new ArrayList<>();

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public SitePolygonPointList(){
    }

    // Constructor
    public SitePolygonPointList(List<SitePolygonPoint> sitePolygonPointList ){

        this._sitePolygonPointList = sitePolygonPointList;

    }

    // Constructor
    public SitePolygonPointList( SitePolygonPoint sitePolygonPoint ){

        List<SitePolygonPoint> sitePolygonPointList = new ArrayList<>();
        sitePolygonPointList.add( sitePolygonPoint );

        this._sitePolygonPointList = sitePolygonPointList;

    }

    //
    // SETTERS
    //

    public void add( SitePolygonPoint sitePolygonPoint ) {

        this._sitePolygonPointList.add( sitePolygonPoint );

    }

    public void addAll( SitePolygonPointList sitePolygonPointList ) {

        this._sitePolygonPointList.addAll( sitePolygonPointList.getSitePolygonPointsList() );

    }

    public void clear() {

        this._sitePolygonPointList.clear();

    }

    //
    // GETTERS
    //

    // Get copy of SitePolygonList
    public SitePolygonPointList getSitePolygonPointListCopy(){

        // Only ever return a copy of the _mantaList so we don't muck up the ordering of the
        // <key,value> pairs in _mantaListIndex

        SitePolygonPointList sitePolygonPointListCopy = new SitePolygonPointList( this._sitePolygonPointList);

        return sitePolygonPointListCopy;

    }

    public SitePolygonPoint get( int i ) {

        return this._sitePolygonPointList.get( i );

    }

    public SitePolygonPointList getSitePolygonPointsBySiteId( int siteId ){

        SitePolygonPointList returnSitePolygonPointList = new SitePolygonPointList();

        for ( SitePolygonPoint sitePolygonPoint : this._sitePolygonPointList ){

            if ( sitePolygonPoint.getSiteId() == siteId ){

                returnSitePolygonPointList.add( sitePolygonPoint );

            }

        }

        return returnSitePolygonPointList;

    }

    public List<SitePolygonPoint> getSitePolygonPointsList(){

        return this._sitePolygonPointList;

    }

    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort achieved across a list of Type

    public boolean isEmpty() {

        return this._sitePolygonPointList.isEmpty();

    }

    // Returns a sorted copy of the list - we could perhaps avoid the copy in future.
    public SitePolygonPointList getSitePolygonPointsBySiteIdSorted( int siteId ){

        SitePolygonPointList sitePolygonPointsBySiteId = this.getSitePolygonPointsBySiteId( siteId );

        List<SitePolygonPoint> sitePolygonPointsBySiteIdList = sitePolygonPointsBySiteId.getSitePolygonPointsList();

        Collections.sort( sitePolygonPointsBySiteIdList, SitePolygonPoint.getPolygonPointComparator() );

        return ( new SitePolygonPointList( sitePolygonPointsBySiteIdList ) );

    }

    public List<Integer> getSiteIdsOfSitePolygonPointsInList(){

        List<Integer> returnSiteIdList = new ArrayList<>();

        Set<Integer> siteIdsNoRepeats = new HashSet<>();

        for ( SitePolygonPoint sitePolygonPoint : this._sitePolygonPointList ){

            siteIdsNoRepeats.add( sitePolygonPoint.getSiteId() );

        }

        returnSiteIdList.addAll( siteIdsNoRepeats );

        return returnSiteIdList;

    }

    public SitePolygonList getAllSitePolygons(){

        HashMap<Integer, SitePolygonPointList> sitePolygonPointLists = new HashMap<>();

        SitePolygonList returnSitePolygonList = new SitePolygonList();

        // Based on the premise that most of hte PolygonPoint should be in order of the Polgyons
        // they belong to, and that updating the HashMap is expensive, we cycle through all the
        // sitePolygonPoints in the list, adding runs of sitePolygonPoints with the same siteId
        // to a temporary list, and we only update the HashMap when the next siteId is different
        // from the preceding one.
        int lastSiteId = this._sitePolygonPointList.get(0).getSiteId();
        SitePolygonPointList sitePolygonPointList = new SitePolygonPointList();

        for ( SitePolygonPoint sitePolygonPoint : this ){

            int siteId = sitePolygonPoint.getSiteId();

            if ( siteId == lastSiteId ){

                sitePolygonPointList.add( sitePolygonPoint );

            } else {

                if ( sitePolygonPointLists.containsKey( lastSiteId ) ){

                    sitePolygonPointLists.get( lastSiteId ).addAll( sitePolygonPointList );

                } else {
//
                    sitePolygonPointLists.put( lastSiteId, sitePolygonPointList );

                }

                lastSiteId = siteId;

                sitePolygonPointList = new SitePolygonPointList();

                sitePolygonPointList.add( sitePolygonPoint );

            }


        }

        // Make sure you add the last Sites details
        if ( sitePolygonPointLists.containsKey( lastSiteId ) ){

            sitePolygonPointLists.get( lastSiteId ).addAll( sitePolygonPointList );

        } else {
//
            sitePolygonPointLists.put( lastSiteId, sitePolygonPointList );

        }



        // Now, turn each of those into a SitePolygon
        for ( Map.Entry<Integer,SitePolygonPointList> sitePolygonPointListEntry : sitePolygonPointLists.entrySet() ){

            SitePolygonPointList sitePolygonPointList2 = sitePolygonPointListEntry.getValue();

            List<SitePolygonPoint> sitePolygonPointListList = sitePolygonPointList2.getSitePolygonPointsList();

            Collections.sort( sitePolygonPointListList, SitePolygonPoint.getPolygonPointComparator() );

            returnSitePolygonList.add( new SitePolygon( sitePolygonPointListList ) );

        }

        return returnSitePolygonList;

    }

    //
    // Iterators
    //

    @NonNull
    @Override
    public Iterator<SitePolygonPoint> iterator() {

        return this._sitePolygonPointList.iterator();

    }

}
