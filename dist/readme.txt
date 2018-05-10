Extracts data from lds.org using LDSTools APIs

Requires JRE verion 8 or higher
To execute:
java -jar lds-tools-export-1.1.jar [username [password]] <verb> path-to-export-file
java -jar lds-tools-export-1.1.jar [username [password]] [--relocate relocate file] <verb> path-to-export-file

Parameters
  username      lds.org account username (optional)
  password      lds.org account password (optional)
  <verb>        determines output to generate (see "Verbs" below)

Verbs:
  --wlts        Generates a .csv file for import into the WLTS system (my-ward.com)
  --map         Generates a .kml file that maps every household
  --ministers   Generates a .kml file that maps ministering routes (requires Leadership level LDS account)
  --ministered  Generates a .csv file that contains all families being ministered to
  --help        Displays usage instructions
  --relocate    relocate_file is a json file that maps coupleName to fields to replace in the Household records
                execute --help for detailed description

Note: It is generally recommended to omit username and password arguments. You will be prompted for them so they will not persist in your command history.

example:
java -jar lds-tools-export-1.1.jar --wlts /temp/ward-export.csv

Notes:
* Don't use spaces in the file name or path.
* The export must be run by a clerk or bishopric member. Other members may not have access to the necessary data from the LDS Tools API and will receive an error.
