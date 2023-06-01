# CheMin-PDS

This repository provides a utility `pds3to4` to convert PDS3 LBL files to
PDS4, XML-formatted labels, and automatically validates them with the
[NASA PDS Validate](https://github.com/NASA-PDS/validate) tool.

## Getting Started

You can build and test the project with [gradle](https://gradle.org/)

```shell
$ git clone https://github.com/39alpha/CheMin-PDS
$ cd CheMin-PDS
$ ./gradlew build
```

### Installation

The resulting `app/build/distributions` directory will contain a tarball and
zip archive. To install, you can extract one of the archives and copy the
contents to your path. At some point, we'll have an installation script.

### Usage

The build will create a `pds3to4` executable. This program will accept a
PDS3 LBL file or a directory, convert the files to PDS4 XML format and validate
the resulting XML using the
[NASA PDS Validate](https://github.com/NASA-PDS/validate) tool.

```shell
$ pds3to4 -h
Usage: pds3to4 [-dhnprV] [-f=FORMAT] [-l=TYPE] [-o=FILE] [-t=TEMPLATE]
               [PDS3-LABEL|DIR]
Convert PDS3 label files to PDS4
      [PDS3-LABEL|DIR]      path to label file
  -d, --delete-report       remove validation reports for successful validations
  -f, --format=FORMAT       path to fmt file
  -h, --help                Show this help message and exit.
  -l, --label-type=TYPE     label type (rda, re1 or min)
  -n, --no-validate         skip NASA PDS validation
  -o, --output=FILE         path to output file
  -p, --print-report        print validation reports to standard output instead
                              of to file
  -r, --recursive           process each .lbl file in a directory
  -t, --template=TEMPLATE   path to template file
  -V, --version             Print version information and exit.
```

**To convert a specific PDS3 LBL file to an PDS4 XML file**
```shell
$ pds3to4 <path/to/file.lbl>
```

**To convert all PDS3 LBL files in a directory**
```shell
$pds3to4 -r <path/to/dir>
```

For more usage details, see [examples](./examples).

## Templates and PDS3 Data Formats

[Velocity](https://velocity.apache.org) files are used to specify the format
of the resulting PDS4 XML files. You can find the bundled templates
[here](./app/src/main/resources/org/thirtyninealpharesearch/chemin/pds4).

The PDS Validate tool will inspect any data products to ensure that they are
consistently formatted based on the information provided in the PDS4 XML file.
The column names, their types and the format are taken from PDS FMT files.
You can find the bundled FMT files
[here](./app/src/main/resources/org/thirtyninealpharesearch/chemin/formats).
