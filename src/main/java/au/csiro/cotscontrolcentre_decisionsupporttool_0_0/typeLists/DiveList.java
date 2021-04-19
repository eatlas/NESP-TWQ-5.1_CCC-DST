package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Dive;

//
// The purpose of these custom TypeList types, encompassing each of our custom Types, is to provide
// functionality related to the physical characteristics of the custom Types. For example, it
// makes sense to be able to call a method that returns the most recent Dives performed at each
// Site in a list of Dives.
//
// There is an additional major benefit, in that we can store a HashMap of the indices of the
// TypeList when it is created. This allows us to write functions that can quickly find at item
// either by its index or by its Type identifier number. In the original data tables these two
// numbers are the same: i.e. the 131st Site in the database siteTable has a SiteId of 131.
// However, in a generic SiteList this is not the case - if we make a list of Sites at a Reef, the
// Site with SiteId 131 might be the 10th item in that particular list.
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
// TODO: Change implementation of common elements of TypeList classes via a BaseTypeClass

public class DiveList implements
        Iterable<Dive> {

    private List<Dive> _diveList = new ArrayList<Dive>();
    private HashMap<Integer, Integer> _diveListIndex = new HashMap< Integer, Integer >();

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public DiveList(){
    }

    // Constructor
    public DiveList(List<Dive> diveList ){

        this._diveList = diveList;

        for ( int i = 0; i < diveList.size(); i++ ) {

            this._diveListIndex.put( diveList.get(i).getDiveId(), i );

        }

    }

    //
    // SETTERS
    //

    public void add( Dive dive ) {

        this._diveList.add( dive );

        this._diveListIndex.put( dive.getDiveId(), this._diveList.size() - 1 );

    }

    public void clear() {

        this._diveList.clear();

        this._diveListIndex.clear();

    }

    //
    // GETTERS
    //

    public Dive get( int i ) {

        return this._diveList.get( i );

    }

    // Get Dive from diveList by diveId
    public Dive getDiveByDiveId( int diveId ){

        int diveTableIndex  = this._diveListIndex.get( diveId );

        return _diveList.get( diveTableIndex );

    }

    // Get a list of Dives from diveList by a list of diveIds
    public DiveList getDivesByDiveIds( List<Integer> diveIds ){

        DiveList returnDives = new DiveList();

        for ( int diveId : diveIds ) {

            int diveIndex = this._diveListIndex.get( diveId );

            returnDives.add( this._diveList.get( diveIndex ) );

        }

        return returnDives;

    }

    public DiveList getDivesOnMostRecentVoyage(){

        if ( this._diveList.isEmpty() ) {

            return ( new DiveList ( new ArrayList<Dive>() ) );

        } else {

            // Get a copy of the underlying list of Dives, so we don't sort the actual DiveList
            List<Dive> diveListCopy = new ArrayList<>(this._diveList);

            // Sort the copy
            Collections.sort(diveListCopy, Dive.getDiveDateComparator());

            // Find the voyageId of the most recent Dive
            int voyageId = diveListCopy.get(0).getVoyageId();

            // Return a list of the Dive with that voyageId
            return this.getDivesByVoyageId( voyageId );

        }

    }

    // Get copy of DiveList
    public List<Dive> getDiveListCopy(){

        // Only ever return a copy of the _mantaList so we don't muck up the ordering of the
        // <key,value> pairs in _mantaListIndex

        List<Dive> diveListCopy = new ArrayList<Dive>( this._diveList );

        return diveListCopy;

    }

    public DiveList getDivesByVoyageId( int voyageId ){

        DiveList returnDiveList = new DiveList();

        for ( Dive dive : this._diveList ){

            if ( dive.getVoyageId() == voyageId ){

                returnDiveList.add( dive );

            }

        }

        return returnDiveList;

    }

    public DiveList getDivesBySiteId( int siteId ){

        DiveList returnDiveList = new DiveList();

        for ( Dive dive : this._diveList ){

            if ( dive.getSiteId() == siteId ){

                returnDiveList.add( dive );

            }

        }

        return returnDiveList;

    }

    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort achieved across a list of Type

    public Integer getTotalCOTS(){

        if ( this._diveList.isEmpty() ) {

            return 0;

        } else {

            int totalCOTS = 0;

            for ( Dive dive : this._diveList ) {

                totalCOTS += dive.totalCoTS();

            }

            return totalCOTS;

        }

    }

    public boolean isEmpty() {

        return this._diveList.isEmpty();

    }


    @Nullable
    public Date getMostRecentDiveDate() {

        if ( this._diveList.isEmpty() ) {

            return null;

//            return ( new DiveList ( new ArrayList<Dive>() ) );

        } else {

            return getMostRecentDive().getDiveDateAsDate();

        }

    }

    @Nullable
    public Integer getMostRecentDiveVoyageId() {

        if ( this._diveList.isEmpty() ) {

            return null;

//            return ( new DiveList ( new ArrayList<Dive>() ) );

        } else {

            return getMostRecentDive().getVoyageId();

        }

    }

    @Nullable
    public Dive getMostRecentDive() {

        if ( this._diveList.isEmpty() ) {

            return null;

//            return ( new DiveList ( new ArrayList<Dive>() ) );

        } else {

            // Get a copy of the underlying list of Dives, so we don't sort the actual DiveList
            List<Dive> diveListCopy = new ArrayList<>( this._diveList );

            // Sort the copy
            Collections.sort( diveListCopy, Dive.getDiveDateComparator() );

            // Find the voyageId of the most recent Dive
            int voyageId = diveListCopy.get( 0 ).getVoyageId();

            // Return a list of the Dive with that voyageId
            return this.getDivesByVoyageId( voyageId ).get( 0 );

        }

    }

    // Get Dive from diveList by diveId
    public int getDiveListIndexByDiveId( int diveId ){

        return this._diveListIndex.get( diveId );

    }

    //
    // Iterators
    //

    @NonNull
    @Override
    public Iterator<Dive> iterator() {

        return this._diveList.iterator();

    }

    // This function should only be used on a DiveList describing Dives conducted at the same
    // Site on the same Voyage. At the moment we just post a Log message if this criterion is not
    // met - in future we should Throw and exception.
    // TODO: Update createCompositeDive to throw exception on inappropriate input Dives
    public Dive createCompositeDive (){

        int diveId;
        String diveDate;
        double diveAverageDepth = 0;
        int diveBottomTime = 0;
        int diveLessThanFifteenCentimetres = 0;
        int diveFifteenToTwentyFiveCentimetres = 0;
        int diveTwentyFiveToFortyCentimetres = 0;
        int diveGreaterThanFortyCentimetres = 0;
        int siteId;
        int voyageId;

        if ( !this._diveList.isEmpty() ) {

            diveDate = this._diveList.get(0).getDiveDate();
            siteId = this._diveList.get(0).getSiteId();
            voyageId = this._diveList.get(0).getVoyageId();

            for (Dive dive : this._diveList) {

                diveAverageDepth += dive.getDiveAverageDepth();
                diveBottomTime += dive.getBottomTime();
                diveLessThanFifteenCentimetres += dive.getLessThanFifteenCentimetres();
                diveFifteenToTwentyFiveCentimetres += dive.getFifteenToTwentyFiveCentimetres();
                diveTwentyFiveToFortyCentimetres += dive.getTwentyFiveToFortyCentimetres();
                diveGreaterThanFortyCentimetres += dive.getGreaterThanFortyCentimetres();

                if ((dive.getSiteId() != siteId) || (dive.getVoyageId() != voyageId)) {

                    Log.d("CCC_DIVE", "Dives should not have been combined because they span multiple Sites or Voyages");

                }

            }

            diveAverageDepth = diveAverageDepth / this._diveList.size();

            return new Dive(-1, diveDate, diveAverageDepth, diveBottomTime, diveLessThanFifteenCentimetres, diveFifteenToTwentyFiveCentimetres, diveTwentyFiveToFortyCentimetres, diveGreaterThanFortyCentimetres, siteId, voyageId);

        } else {

            return new Dive();

        }

    }

}
