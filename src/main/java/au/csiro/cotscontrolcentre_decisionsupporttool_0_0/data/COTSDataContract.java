package au.csiro.cotscontrolcentre_decisionsupporttool_0_0.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fle125 on 27/03/2017.
 */

public class COTSDataContract {

    public static final String CONTENT_AUTHORITY = "au.csiro.cotscontrolcentre_decisionsupporttool_0_0";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REEFS = "reefs";
    public static final String PATH_REEF_POLYGONS = "reefpolygons";
    public static final String PATH_SITES = "sites";
    public static final String PATH_SITE_POLYGONS = "sitepolygons";
    public static final String PATH_VESSELS = "vessels";
    public static final String PATH_VOYAGES = "voyages";
    public static final String PATH_DIVES = "dives";
    public static final String PATH_MANTAS = "mantas";
    public static final String PATH_RHIS = "rhis";

    public static final String PATH_CONTROLLED = "controlled";

    public static final String PATH_WITH_CHARACTERISTIC = "with";
    public static final String PATH_BY_MEMBER_OF = "in";
    public static final String PATH_BY_CONTAINS = "contains";

    public static final String PATH_MODIFIER_ID = "id";
    public static final String PATH_MODIFIER_SITENAME = "sitename";

    public static final String PATH_MODIFIER_MOST_RECENT = "mostrecent";
    public static final String PATH_MODIFIER_AT_DATE = "atdate";

    public static final String PATH_NUMERIC_QUERY = "#";
    public static final String PATH_STRING_QUERY = "*";

    //        Inner class that defines the table contents of the Reef Table
    public static final class ReefEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Reefs from the Reef table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REEFS)
                .build();

        public static final String REEF_TABLE_NAME = "reefTable";

        public static final String REEF_TABLE_COLUMN_ID = "__reefId";
        public static final String REEF_TABLE_COLUMN_REEF_NAME = "reefReefName";
        public static final String REEF_TABLE_COLUMN_REEF_ID = "reefReefId";
        public static final String REEF_TABLE_COLUMN_LATITUDE = "reefLatitude";
        public static final String REEF_TABLE_COLUMN_LONGITUDE = "reefLongitude";

    }

    public static final class ReefPolygonsEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Reefs from the Reef table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REEF_POLYGONS)
                .build();

        public static final String REEF_POLYGONS_TABLE_NAME = "reefPolygonsTable";

        public static final String REEF_POLYGONS_TABLE_COLUMN_ID = "__reefPolygonsId";
        public static final String REEF_POLYGONS_TABLE_COLUMN_POINT_ORDER = "reefPolygonPointOrder";
        public static final String REEF_POLYGONS_TABLE_COLUMN_POINT_LATITUDE = "reefPolygonPointLatitude";
        public static final String REEF_POLYGONS_TABLE_COLUMN_POINT_LONGITUDE = "reefPolygonPointLongitude";
        public static final String REEF_POLYGONS_TABLE_COLUMN_REEF_ID = "__reefId";

    }

    //    Inner class that defines the table contents of the Site table
    public static final class SiteEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Sites from the Dive table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SITES)
                .build();

        public static final String SITE_TABLE_NAME = "siteTable";

        public static final String SITE_TABLE_COLUMN_ID = "__siteId";
        public static final String SITE_TABLE_COLUMN_SITE_NAME = "siteSiteName";
        public static final String SITE_TABLE_COLUMN_LATITUDE = "siteLatitude";
        public static final String SITE_TABLE_COLUMN_LONGITUDE = "siteLongitude";
        public static final String SITE_TABLE_COLUMN_REEF_ID = "__reefId";
        public static final String SITE_TABLE_COLUMN_DIVE_IDS_OF_DIVES_AT_SITE = "diveIdsOfDivesAtSite";
        public static final String SITE_TABLE_COLUMN_MANTA_IDS_OF_MANTAS_AT_SITE = "mantaIdsOfMantasAtSite";

    }

    public static final class SitePolygonsEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Reefs from the Reef table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SITE_POLYGONS)
                .build();

        public static final String SITE_POLYGONS_TABLE_NAME = "sitePolygonsTable";

        public static final String SITE_POLYGONS_TABLE_COLUMN_ID = "__sitePolygonsId";
        public static final String SITE_POLYGONS_TABLE_COLUMN_POINT_ORDER = "sitePolygonPointOrder";
        public static final String SITE_POLYGONS_TABLE_COLUMN_POINT_LATITUDE = "sitePolygonPointLatitude";
        public static final String SITE_POLYGONS_TABLE_COLUMN_POINT_LONGITUDE = "sitePolygonPointLongitude";
        public static final String SITE_POLYGONS_TABLE_COLUMN_SITE_ID = "__siteId";

    }

    //    Inner class that defines the table contents of the Site table
    public static final class VesselEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Sites from the Dive table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VESSELS)
                .build();

        public static final String VESSEL_TABLE_NAME = "vesselTable";

        public static final String VESSEL_TABLE_COLUMN_ID = "__vesselId";
        public static final String VESSEL_TABLE_COLUMN_VESSEL_NAME = "vesselName";
        public static final String VESSEL_TABLE_COLUMN_VESSEL_SHORT_NAME = "vesselShortName";

    }

    /* Inner class that defines the table contents of the voyage table */
    public static final class VoyageEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Voyage table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VOYAGES)
                .build();

        public static final String VOYAGE_TABLE_NAME = "voyageTable";

        public static final String VOYAGE_TABLE_COLUMN_ID = "__voyageId";
        public static final String VOYAGE_TABLE_COLUMN_VOYAGE_NUMBER = "voyageVesselVoyageNumber";
        public static final String VOYAGE_TABLE_COLUMN_START_DATE = "voyageStartDate";
        public static final String VOYAGE_TABLE_COLUMN_STOP_DATE = "voyageStopDate";
        public static final String VOYAGE_TABLE_COLUMN_VESSEL_ID = "__vesselId";
        // The column names below represent SQL calculation columns, not columns in the data table
        public static final String VOYAGE_TABLE_COLUMN_REEF_IDS_OF_REEFS_DURING_VOYAGE = "reefIdsOfReefsDuringVoyage";
        public static final String VOYAGE_TABLE_COLUMN_SITE_IDS_OF_SITES_DURING_VOYAGE = "siteIdsOfReefsDuringVoyage";
        public static final String VOYAGE_TABLE_COLUMN_DIVE_IDS_OF_DIVES_DURING_VOYAGE = "diveIdsOfDivesDuringVoyage";
        public static final String VOYAGE_TABLE_COLUMN_MANTA_IDS_OF_MANTAS_DURING_VOYAGE = "mantaIdsOfMantasDuringVoyage";

    }


    /* Inner class that defines the table contents of the dive table */
    public static final class DiveEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Dive table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_DIVES)
                .build();

        public static final String DIVE_TABLE_NAME = "diveTable";

        public static final String DIVE_TABLE_COLUMN_ID = "__diveId";
        public static final String DIVE_TABLE_COLUMN_DATE = "diveDate";
        public static final String DIVE_TABLE_COLUMN_AVERAGE_DEPTH = "diveAverageDepth";
        public static final String DIVE_TABLE_COLUMN_BOTTOM_TIME = "diveBottomTime";
        public static final String DIVE_TABLE_COLUMN_LESS_THAN_FIFTEEN_CENTIMETRES = "diveLessThanFifteenCentimetres";
        public static final String DIVE_TABLE_COLUMN_FIFTEEN_TO_TWENTY_FIVE_CENTIMETRES = "diveFifteenToTwentyFiveCentimetres";
        public static final String DIVE_TABLE_COLUMN_TWENTY_FIVE_TO_FORTY_CENTIMETRES = "diveTwentyFiveToFortyCentimetres";
        public static final String DIVE_TABLE_COLUMN_GREATER_THAN_FORTY_CENTIMETRES = "diveGreaterThanFortyCentimetres";
        public static final String DIVE_TABLE_COLUMN_SITE_ID = "__siteId";
        public static final String DIVE_TABLE_COLUMN_VOYAGE_ID = "__voyageId";

    }


    //    Inner class that defines the table contents of the RHIS table
    public static final class MantaEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Reefs from the Dive table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MANTAS)
                .build();

    //      For the moment, the Reefs query will simply run on the Dive table, extracting the Reef details
    //      from it. In future, the database structure will be updated and it will run on a dedicated Reefs table.
    //      For the moment we keep references to all the Dive columns, but in future we will
    //      rationalise these to the actual entries in the Reef table.

        public static final String MANTA_TABLE_NAME = "mantaTable";

        public static final String MANTA_TABLE_COLUMN_ID = "__mantaId";
        public static final String MANTA_TABLE_COLUMN_DATE = "mantaDate";
        public static final String MANTA_TABLE_COLUMN_START_LAT = "mantaStartLatitude";
        public static final String MANTA_TABLE_COLUMN_START_LONG = "mantaStartLongitude";
        public static final String MANTA_TABLE_COLUMN_STOP_LAT = "mantaStopLatitude";
        public static final String MANTA_TABLE_COLUMN_STOP_LONG = "mantaStopLongitude";
        public static final String MANTA_TABLE_COLUMN_MEAN_LAT = "mantaMeanLatitude";
        public static final String MANTA_TABLE_COLUMN_MEAN_LONG = "mantaMeanLongitude";
        public static final String MANTA_TABLE_COLUMN_COTS = "mantaCOTS";
        public static final String MANTA_TABLE_COLUMN_SCARS = "mantaScars";
        public static final String MANTA_TABLE_COLUMN_SITE_ID = "__siteId";
        public static final String MANTA_TABLE_COLUMN_VOYAGE_ID = "__voyageId";

    }


    //    Inner class that defines the table contents of the RHIS table
    public static final class RhisEntry implements BaseColumns {

        //      The base CONTENT_URI used to query the Sites from the Dive table from the content provider
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RHIS)
                .build();

//      For the moment, the Rhis query will simply run on the Dive table, extracting the Rhis details
//      from it. In future, the database structure will be updated and it will run on a dedicated Rhis table.
//      For the moment we keep references to all the Dive columns, but in future we will
//      rationalise these to the actual entries in the Rhis table.

        public static final String RHIS_TABLE_NAME = "rhisTable";

        public static final String RHIS_TABLE_COLUMN_ID = "__rhisId";
        public static final String RHIS_TABLE_COLUMN_DATE = "rhisDate";
        public static final String RHIS_TABLE_COLUMN_AVERAGE_CORAL_COVER = "rhisCoralCover";
        public static final String RHIS_TABLE_COLUMN_COTS_ADULTS = "rhisCOTSAdults";
        public static final String RHIS_TABLE_COLUMN_COTS_JUVENILES = "rhisCOTSJuveniles";
        public static final String RHIS_TABLE_COLUMN_VISIBILITY = "rhisVisibility";
        public static final String RHIS_TABLE_COLUMN_SITE_ID = "__siteId";

    }

}
