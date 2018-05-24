# LDS Tools Export (aka wlts_import)
WLTS Import from LDS Tools API

## Project Resources
Source Repository: https://github.com/Speewack/wlts-import

Continuous Integration: https://travis-ci.org/Speewack/wlts-import/

Releases and Distributions: https://github.com/Speewack/wlts-import/releases

# Setup - Mac

## Get Maven build system

If you have Homebrew (https://brew.sh) you can install Maven via:

`brew install maven`

## Set your toolchain

Create or edit ~/.m2/toolchains.xml
```xml
<?xml version="1.0" encoding="UTF8"?>
<toolchains>
  <!-- JDK toolchains -->
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>1.8</version>
      <vendor>sun</vendor>
    </provides>
    <configuration>
      <jdkHome>/Library/Java/JavaVirtualMachines/jdk1.8.0_144.jdk/Contents/Home</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

To find the value for &lt;jdkHome&gt; you can run on the command line: echo $(/usr/libexec/java_home)

## Build

To build run:

`mvn clean package javadoc:javadoc pmd:pmd pmd:cpd`

The documentation is at: `target/site/apidocs/index.html`

## Usage

You can get the full usage by passing `--help`

`java -jar target/lds-tools-export-1.1.jar --help`

If you have admin access on LDS Tools:

`java -jar target/lds-tools-export-1.1.jar --wlts output.csv`

The following require leader access:

`java -jar target/lds-tools-export-1.1s.jar --routes map.kml`

`java -jar target/lds-tools-export-1.1s.jar --ministers ministers.kml`

`java -jar target/lds-tools-export-1.1s.jar --ministered ministered.csv`

The following do not require special access:

`java -jar target/lds-tools-export-1.1s.jar --map map.kml`

You can combine `--routes`, `--map`, `--minisetered`, `--wlts` and `--ministers` into one command:

`java -jar target/lds-tools-export-1.1s.jar --map map.kml --routes map.kml --ministers map.kml`

For `--routes`, `--ministers`, `--ministered` and `--map` you can override location of households
	(which may not have latitude/longitude, or may have incorrect location) by passing `--relocate`
	and then the path to a JSON file of the format:

```JSON
{
	"coupleName" : {
		"address" : "100 North Street",
		"state" : "ST",
		"postalCode" : "99999",
		"latitude" : 35.00000,
		"longitude" : -95.0000
	},
	"Smith, Joe & Jane" : {
		"postalCode" : "99999",
		"latitude" : 35.00000,
		"longitude" : -95.0000
	}
}
```

You only need to provide the information that is incorrect. Everything else will be gotten from the original source.

`java -jar target/lds-tools-export-1.1s.jar --relocate relocation.json --map map.kml --routes map.kml --ministers map.kml`
