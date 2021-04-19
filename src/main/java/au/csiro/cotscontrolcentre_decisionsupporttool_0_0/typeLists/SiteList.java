package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Site;

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
// Note that we do not extend one of Android's list classes because: 1) that is not considered
// best practice, and 2) it would require a lot of boilerplate overridden function definitions.
//

public class SiteList implements Iterable<Site> {

    private List<Site> _siteList = new ArrayList<Site>();
    private HashMap<Integer, Integer> _siteListIndex = new HashMap< Integer, Integer >();

    private boolean bufferDiveChanges = true;
    private boolean bufferMantaChanges = true;
    private HashMap<Integer, DiveList> bufferedMostRecentDiveAtEachSite = new HashMap< Integer, DiveList >();
    private HashMap<Integer, List<Integer>> bufferedMostRecentMantasAtEachSite = new HashMap< Integer, List<Integer> >();

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public SiteList(){

        this.bufferDiveChanges = true;
        this.bufferMantaChanges = true;

    }

    // Constructor
    public SiteList( List<Site> siteList ){

        this._siteList = siteList;

        for ( int i = 0; i < siteList.size(); i++ ) {

            this._siteListIndex.put( siteList.get(i).getSiteId(), i );

        }

        this.bufferDiveChanges = true;
        this.bufferMantaChanges = true;

    }

    //
    // SETTERS
    //

    public void add( Site site ) {

        this._siteList.add( site );

        this._siteListIndex.put( site.getSiteId(), this._siteList.size() - 1 );

        this.bufferDiveChanges = true;
        this.bufferMantaChanges = true;

    }

    public void clear() {

        this._siteList.clear();

        this._siteListIndex.clear();

        this.bufferDiveChanges = true;
        this.bufferMantaChanges = true;

    }

    //
    // GETTERS
    //

    public void getSiteBySiteId(){

    }

    // Get Site from siteList by siteId
    public Site getSiteBySiteId( int siteId ){

        int siteTableIndex  = this._siteListIndex.get( siteId );

        return _siteList.get( siteTableIndex );

    }

    // Get a list of Sites from siteList by a list of siteIds
    public SiteList getSiteBySiteId( List<Integer> siteIds ){

        SiteList returnSites = new SiteList();

        for ( int siteId : siteIds ) {

            int siteIndex = this._siteListIndex.get( siteId );

            returnSites.add( this._siteList.get( siteIndex ) );

        }

        return returnSites;

    }

    // Get Site from siteList by siteId
    public int getSiteListIndexBySiteId( int siteId ){

        return this._siteListIndex.get( siteId );

    }

    public void updateDiveAndMantaChanges(){

        this.bufferDiveChanges = true;
        this.bufferMantaChanges = true;

    }

    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort achieved across a list of Type

    public Integer size() {

        return this._siteList.size();

    }

    public boolean isEmpty(){

        return this._siteList.isEmpty();

    }

    //
    // Iterators
    //

    @NonNull
    @Override
    public Iterator<Site> iterator() {

        return this._siteList.iterator();

    }

    public int findSiteIdBySiteName( String siteName ){

        int returnId = 0;

        for ( Site site : _siteList ){

            if ( site.getSiteName().equals( siteName ) ){

                returnId = site.getSiteId();

            }

        }

        return returnId;

    }

}
