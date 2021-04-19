package au.csiro.cotscontrolcentre_decisionsupporttool_0_0;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Manta;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.MantaList;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.types.Reef;
import au.csiro.cotscontrolcentre_decisionsupporttool_0_0.typeLists.SiteList;

import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class FindMantaNearestSite {

    public static String findRandomMantaSite( Context context, Reef reef, SiteList siteList, MantaList mantaList ) {

        int mantaRow = new Random().nextInt(mantaList.size());

        Manta manta = mantaList.getMantaByIndex(mantaRow);

        int reefId = reef.getReefId();

        double mantaMeanLatitude = manta.getMantaMeanLat();
        double mantaMeanLongitude = manta.getMantaMeanLong();

        int siteId = findNearestSiteId( context, reefId, mantaMeanLatitude, mantaMeanLongitude );

        String returnTest = "Recorded nearest siteId = " + Integer.toString( manta.getSiteId() ) + ", calculated nearest siteId = " + Integer.toString( siteId );

        return returnTest;

    }

    public static int findNearestSiteId( Context context, int reefId, double latitude, double longitude ) {

        // Read data from file
        int resourceId = ( (Activity) context).getResources().getIdentifier( "r" + Integer.toString( reefId ), "raw", "au.csiro.cotscontrolcentre_decisionsupporttool_0_0" );
        InputStream is = ( (Activity) context).getResources().openRawResource( resourceId );
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        ArrayList<ArrayList<Integer>> returnArray = new ArrayList<>();

        // Read the headers
        String reefID;
        String reefName;
        double startLat = 0;
        double startLong = 0;
        double resolution = 1;
        int rows = 0;
        int columns = 0;

        try {

            // Read the headers
            reefID = findHeaderValue( reader.readLine() );
            reefName = findHeaderValue( reader.readLine() );
            startLat = Double.parseDouble( findHeaderValue( reader.readLine() ) ) ;
            startLong = Double.parseDouble( findHeaderValue( reader.readLine() ) );
            resolution = Double.parseDouble( findHeaderValue( reader.readLine() ) );
            rows = Integer.parseInt( findHeaderValue( reader.readLine() ) );
            columns = Integer.parseInt( findHeaderValue( reader.readLine() ) );

            int i = 0;

            while ( ( line = reader.readLine() ) != null ) {

                returnArray.add( new ArrayList<Integer>() );

                // Split the line into values using the comma as a separator
                String[] strArray = line.split(",");

                for (String str : strArray) {

                    returnArray.get( i ).add( Integer.parseInt(str) );

                }

                i++;

            }

        } catch ( IOException e1 ) {

            Log.e("ReadCSVFile", "Error" + line, e1);

            e1.printStackTrace();

        }

        // Load data into array structure
//        ArrayList<ArrayList<Integer>> nearestSiteArray = readNearestSiteArrayFromCSV( context, reefId );

        // Compared to the original Mathematica table, this process has:
        // 1) Loaded rows into rows and columns into columns;
        // 2) Loaded the data into the array top-to-bottom, in terms of the sequence of rows;
        // 3) Loaded the data in the same left-to-right order, in terms a single row
        //
        // That is, everything is loaded as it would display in the Mathematica file.
        //
        // The original Mathematica table had the southernmost latitude in row 1, and the
        // westernmost longitude in the first column. startLat refers to this southernmost latitude.
        // startLong refers to the westernmost longitude.
        //
        // Therefore, when we calculate our latitude index, we need to count forwards from the first
        // row. When we calculate our longitude index, we can count forward from the 0th column.

        // Calculate row
        int rowIndex = (int) min( floor( ( latitude - startLat ) / resolution ), rows );

        // Calculate column
        int columnIndex = (int) min( floor( ( longitude - startLong ) / resolution ), columns );

        int returnRowColumn = returnArray.get( rowIndex ).get( columnIndex );

        // Return answer
        return returnRowColumn;

    }

    private static String findHeaderValue( String line ){

        String[] lineSplit = line.split(",");

        String returnString = "";

        int i = 0;

        for ( String str : lineSplit ){

            if ( i > 0 ){ returnString = str; }

            i++;

        }

        return returnString;

    }

    private static ArrayList<ArrayList<Integer>> readNearestSiteArrayFromCSV(Context context, int reefId ) {

        int resourceId = ( (Activity) context).getResources().getIdentifier( "r" + Integer.toString( reefId ), "raw", "au.csiro.cotscontrolcentre_decisionsupporttool_0_0" );
        InputStream is = ( (Activity) context).getResources().openRawResource( resourceId );
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        ArrayList<Integer> arrayLine = new ArrayList<>();
        ArrayList<ArrayList<Integer>> returnArray = new ArrayList<>();

        try {

            // Read past the headers
            for ( int i = 1; i <= 7; i++ ){
                reader.readLine();
            }

            while ( ( line = reader.readLine() ) != null ) {

                arrayLine.clear();

                // Split the line into values using the comma as a separator
                String[] strArray = line.split(",");

                for (String str : strArray) {

                    arrayLine.add( Integer.parseInt(str) );

                }

                returnArray.add(arrayLine);

            }

        } catch ( IOException e1 ) {

            Log.e("ReadCSVFile", "Error" + line, e1);

            e1.printStackTrace();

        }

        return returnArray;

    }

}
