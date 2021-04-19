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

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Manta;

//
// The purpose of these custom TypeList types, encompassing each of our custom Types, is to provide
// functionality related to the physical characteristics of the custom Types. For example, it
// makes sense to be able to call a method that returns the most recent Dives performed at each
// Site in a list of Mantas.
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

public class MantaList implements Iterable<Manta> {

    private List<Manta> _mantaList = new ArrayList<Manta>();
    private HashMap<Integer, Integer> _mantaListIndex = new HashMap< Integer, Integer >();

    //
    // CONSTRUCTORS
    //

    // Empty constructor
    public MantaList(){
    }

    // Constructor
    public MantaList( List<Manta> mantaList ){

        this._mantaList = mantaList;

        for ( int i = 0; i < mantaList.size(); i++ ) {

            this._mantaListIndex.put( mantaList.get(i).getMantaId(), i );

        }

    }

    //
    // SETTERS
    //

    // This function simply adds a manta to MantaList.
    public void add( Manta manta ) {

        this._mantaList.add( manta );

        this._mantaListIndex.put( manta.getMantaId(), this._mantaList.size() - 1 );

    }

    public void clear() {

        this._mantaList.clear();

        this._mantaListIndex.clear();

    }

    //
    // GETTERS
    //

    public Manta get( int i ) {

        return this._mantaList.get( i );

    }

    // Get entire Manta List
    public List<Manta> getMantaListCopy(){

        // Only ever return a copy of the _mantaList so we don't muck up the ordering of the
        // <key,value> pairs in _mantaListIndex

        List<Manta> mantaListCopy = new ArrayList( this._mantaList );

        return mantaListCopy;

    }

    // Get Manta from mantaList by mantaId
    public Manta getMantaByMantaId( int mantaId ){

        int mantaTableIndex  = this._mantaListIndex.get( mantaId );

        return _mantaList.get( mantaTableIndex );

    }

    // Get a list of Mantas from mantaList by a list of mantaIds
    public MantaList getMantaByMantaId( List<Integer> mantaIds ){

        MantaList returnMantas = new MantaList();

        for ( int mantaId : mantaIds ) {

            int mantaIndex = this._mantaListIndex.get( mantaId );

            returnMantas.add( this._mantaList.get( mantaIndex ) );

        }

        return returnMantas;

    }

    public MantaList getMantasOnMostRecentVoyage(){

        if ( this._mantaList.isEmpty() ) {

            return ( new MantaList ( new ArrayList<Manta>() ) );

        } else {

            // Get a copy of the underlying list of Dives, so we don't sort the actual DiveList
            List<Manta> mantaListCopy = new ArrayList<>(this._mantaList);

            // Sort the copy
            Collections.sort( mantaListCopy, Manta.getMantaDateComparator() );

            // Find the voyageId of the most recent Dive
            int voyageId = mantaListCopy.get(0).getVoyageId();

            // Return a list of the Dive with that voyageId
            return this.getMantasByVoyageId( voyageId );

        }

    }

    public MantaList getMantasByVoyageId( int voyageId ){

        MantaList returnMantaList = new MantaList();

        for ( Manta manta : this._mantaList ){

            if ( manta.getVoyageId() == voyageId ){

                returnMantaList.add( manta );

            }

        }

        return returnMantaList;

    }

    public MantaList getMantasBySiteId( int siteId ){

        MantaList returnMantaList = new MantaList();

        for ( Manta manta : this._mantaList ){

            if ( manta.getSiteId() == siteId ){

                returnMantaList.add( manta );

            }

        }

        return returnMantaList;

    }

    //
    // DERIVED VALUES
    //

    // We also provide functions for derived values, like the total number of CoTS removed
    // and the catch per unit effort achieved across a list of Type

    public boolean isEmpty() {

        return this._mantaList.isEmpty();

    }

    public int size() {

        return this._mantaList.size();

    }


    public Integer getTotalCOTS(){

        if ( this._mantaList.isEmpty() ) {

            return 0;

        } else {

            int totalCOTS = 0;

            for ( Manta manta : this._mantaList ) {

                totalCOTS += manta.getMantaCOTS();

            }

            return totalCOTS;

        }

    }

    public String getTotalScars() {

        if ( this._mantaList.isEmpty() ) {

            return "a";

        } else {

            String totalScars = "a";

            for ( Manta manta : this._mantaList ) {

                if ( totalScars.equals( "a" ) && manta.getMantaScars().equals( "a" ) ){

                    totalScars = "a";

                } else if ( totalScars.equals( "a" ) && manta.getMantaScars().equals( "p" ) ) {

                    totalScars = "p";

                } else if ( totalScars.equals( "p" ) && manta.getMantaScars().equals( "a" ) ) {

                    totalScars = "p";

                } else {

                    totalScars = "c";

                }

            }

            return totalScars;

        }

    }

    @Nullable
    public Date getMostRecentMantaDate() {

        if ( this._mantaList.isEmpty() ) {

            return null;

        } else {

            return getMostRecentManta().getMantaDateAsDate();

        }

    }

    @Nullable
    public Integer getMostRecentMantaVoyageId() {

        if ( this._mantaList.isEmpty() ) {

            return null;

//            return ( new DiveList ( new ArrayList<Dive>() ) );

        } else {

            return getMostRecentManta().getVoyageId();

        }

    }

    @Nullable
    public Manta getMostRecentManta() {

        if ( this._mantaList.isEmpty() ) {

            return null;

//            return ( new DiveList ( new ArrayList<Dive>() ) );

        } else {

            // Get a copy of the underlying list of Dives, so we don't sort the actual DiveList
            List<Manta> mantaListCopy = new ArrayList<>( this._mantaList );

            // Sort the copy
            Collections.sort( mantaListCopy, Manta.getMantaDateComparator() );

            // Find the voyageId of the most recent Dive
            int voyageId = mantaListCopy.get( 0 ).getVoyageId();

            // Return a list of the Dive with that voyageId
            return this.getMantasByVoyageId( voyageId ).get( 0 );

        }

    }


    // Get Manta from mantaList by mantaId
    public int getMantaListIndexByMantaId( int mantaId ){

        return this._mantaListIndex.get( mantaId );

    }

    public Manta getMantaByIndex( int index ){

        return _mantaList.get( index );

    }

    //
    // Iterators
    //

    @NonNull
    @Override
    public Iterator<Manta> iterator() {

        return this._mantaList.iterator();

    }

    // This function should only be used on a MantaList describing Mantas conducted at the same
    // Site on the same Voyage. At the moment we just post a Log message - in future we should
    // Throw and exception.
    public Manta createCompositeManta (){

        int mantaId;
        String mantaDate;
        double mantaStartLat = 0;
        double mantaStartLong = 0;
        double mantaStopLat = 0;
        double mantaStopLong = 0;
        double mantaMeanLat = 0;
        double mantaMeanLong = 0;
        int mantaCots = 0;
        String mantaScars = "a";
        int siteId;
        int voyageId;

        if ( !this._mantaList.isEmpty() ) {

            mantaDate = this._mantaList.get(0).getMantaDate();
            siteId = this._mantaList.get(0).getSiteId();
            voyageId = this._mantaList.get(0).getVoyageId();

            mantaCots = this.getTotalCOTS();
            mantaScars = this.getTotalScars();

            // This assignment of lats and longs depends on the order in which mantas are listed and
            // the relative lat and long of the points - but as we don't use this for anything at the
            // moment, it's not important that it's deterministic.
            mantaStartLat = this._mantaList.get(0).getMantaStartLat();
            mantaStartLong = this._mantaList.get(0).getMantaStartLong();
            mantaStopLat = this._mantaList.get(this._mantaList.size() - 1).getMantaStartLat();
            mantaStopLong = this._mantaList.get(this._mantaList.size() - 1).getMantaStartLong();
            mantaMeanLat = (mantaStartLat + mantaStopLat) / 2;
            mantaMeanLong = (mantaStartLong + mantaStopLong) / 2;


            for (Manta manta : this._mantaList) {

                if ((manta.getSiteId() != siteId) || (manta.getVoyageId() != voyageId)) {

                    Log.d("CCC_MANTA", "Mantas should not have been combined because they span multiple Sites or Voyages");

                }

            }

            return new Manta(-1, mantaDate, mantaStartLat, mantaStartLong, mantaStopLat, mantaStopLong, mantaMeanLat, mantaMeanLong, mantaCots, mantaScars, siteId, voyageId);

        } else {

            return new Manta();

        }

    }


}
