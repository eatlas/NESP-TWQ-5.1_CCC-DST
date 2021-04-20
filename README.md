# NESP TWQ 5.1 COTS Control Centre Decision Support Tool

The COTS Control Centre Decision Support Tool (CCC-DST) is part of the COTS Control Centre Decision Support System 
(CCC-DSS). The CCC-DSS is a combined hardware and software solution developed by CSIRO as part of the National
Environmental Science Program (NESP) Integrated Pest Management (IPM) Crown-of-thorns starfish (COTS) Research Program
to help guide on-water decision making and implement the ecologically-informed management program outlined in the 
report *An ecologically-based operational strategy for COTS Control: Integrated decision making from the site to the 
regional scale* (Fletcher, Bonin, & Westcott, 2020). 

The COTS Control Centre DSS is built around a fleet of 32 ruggedised Samsung Galaxy Tab Active2 Android tablets, along
with a suite of three data collection apps, developed for the Great Barrier Reef Marine Park Authority (GBRMPA) by
ThinkSpatial, and three decision support components developed by CSIRO as part of the NESP COTS IPM Research Program.
The fleet of tablets are able to be managed remotely, including locating hardware and updating software, using the
Samsung Knox Manage Enterprise Mobility Management platform, and run a custom kiosk launcher. Data is shared between the
apps that make up the CCC-DSS within a tablet using the Android file system, between tablets on a vessel independent of
cellular connectivity with the Android Nearby Communications protocol, and with GBRMPA’s Eye on the Reef Database when
cellular networking is available. 

In addition to designing and implementing the overarching CCC-DSS system, CSIRO has developed a suite of three software 
components, consisting of the main Decision Support Tool (CCC-DST), a Data Explorer functionality, which is currently 
implemented as part of the CCC-DST but may, in future, be separated into a second app, and a utility Data Sync Tool for 
sharing data between tablets when internet connectivity is not available. 

This repository contains the source code for the CCC-DST Android application.

For a detailed description of the application see the [technical report](https://bit.ly/319lvEy) on the 
[NESP TWQ Project page](https://nesptropical.edu.au/index.php/round-5-projects/project-5-1/).
