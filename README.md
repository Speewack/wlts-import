# wlts-import
WLTS Import from LDS Tools API

# Setup - Mac

## Get Maven build system

If you have Homebrew (https://brew.sh) you can install Maven via:

`brew install maven`

## Build

To build run:

`mvn clean package assembly:single`

## Test

If you have admin access on LDS Tools:

`java -jar target/lds-tools-export-1.0-jar-with-dependencies.jar <username> output.csv`

If you do not have admin access:

`java -jar target/lds-tools-export-1.0-jar-with-dependencies.jar --minimal <username> output.csv`

*Note:* --minimal is not available at this time