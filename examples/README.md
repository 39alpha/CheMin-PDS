# Examples

This repo provides a single command (at least for now) called `pds3to4` which
converts PDS3 label files to the new PDS4 format.

You can get very basic help at the command line.

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

## Basic Example

To run the conversion on a single file

```shell
$ pds3to4 "00-basic/cma_404470826rda00790050104ch11503p1.lbl"
```

This will generate a PDS4, XML-formatted label and a plain-text report from the
PDS validate too, both in the same directory as the label file.

```plain
╭───┬──────────────────────────────────────────────────────────┬──────┬─────────╮
│ # │                           name                           │ type │  size   │
├───┼──────────────────────────────────────────────────────────┼──────┼─────────┤
│ 0 │ 00-basic/cma_404470826rda00790050104ch11503p1.csv        │ file │ 11.1 KB │
│ 1 │ 00-basic/cma_404470826rda00790050104ch11503p1.lbl        │ file │  3.9 KB │
│ 2 │ 00-basic/cma_404470826rda00790050104ch11503p1.xml        │ file │ 11.2 KB │
│ 3 │ 00-basic/cma_404470826rda00790050104ch11503p1.xml.report │ file │  1.1 KB │
╰───┴──────────────────────────────────────────────────────────┴──────┴─────────╯
```

You can automatically delete the `.report` file if the conversion is successful
by providing the `-d` flag.

```shell
$ pds3to4 -d "00-basic/cma_404470826rda00790050104ch11503p1.lbl"
```

```plain
╭───┬──────────────────────────────────────────────────────────┬──────┬─────────╮
│ # │                           name                           │ type │  size   │
├───┼──────────────────────────────────────────────────────────┼──────┼─────────┤
│ 0 │ 00-basic/cma_404470826rda00790050104ch11503p1.csv        │ file │ 11.1 KB │
│ 1 │ 00-basic/cma_404470826rda00790050104ch11503p1.lbl        │ file │  3.9 KB │
│ 2 │ 00-basic/cma_404470826rda00790050104ch11503p1.xml        │ file │ 11.2 KB │
╰───┴──────────────────────────────────────────────────────────┴──────┴─────────╯
```

## Delivery 32

The command can be run on a directory instead of just a file to convert all
`lbl` files. To do that, you'll need to provide the `-r` flag.

```shell
$ pds3to4 -r -d "01-delivery-32/"
```

```plain
╭────┬─────────────────────────────────────────────────────────┬──────┬─────────╮
│  # │                          name                           │ type │  size   │
├────┼─────────────────────────────────────────────────────────┼──────┼─────────┤
│  0 │ 01-delivery-32/CMB_718398059MIN36140971734CH00111P1.lbl │ file │  3.4 KB │
│  1 │ 01-delivery-32/CMB_718398059MIN36140971734CH00111P1.xml │ file │  8.0 KB │
│  2 │ 01-delivery-32/CMB_718398059RDA36140971734CH00111P1.lbl │ file │  3.1 KB │
│  3 │ 01-delivery-32/CMB_718398059RDA36140971734CH00111P1.xml │ file │  8.6 KB │
│  4 │ 01-delivery-32/CMB_718398430RE136140971734CH00111P1.lbl │ file │  4.4 KB │
│  5 │ 01-delivery-32/CMB_718398430RE136140971734CH00111P1.xml │ file │ 11.6 KB │
│  6 │ 01-delivery-32/CMB_720868365RE136420980270CH00111P1.lbl │ file │  2.9 KB │
│  7 │ 01-delivery-32/CMB_720868365RE136420980270CH00111P1.xml │ file │  7.5 KB │
│  8 │ 01-delivery-32/cmb_718398059min36140971734ch00111p1.csv │ file │   207 B │
│  9 │ 01-delivery-32/cmb_718398059rda36140971734ch00111p1.csv │ file │ 10.3 KB │
│ 10 │ 01-delivery-32/cmb_718398430re136140971734ch00111p1.csv │ file │ 20.0 KB │
│ 11 │ 01-delivery-32/cmb_720868365re136420980270ch00111p1.csv │ file │ 18.9 KB │
╰────┴─────────────────────────────────────────────────────────┴──────┴─────────╯
```

If you forget the `-r` flag, you'll get an error that looks something like this

```shell
$ pds3to4 -r "01-delivery-32/"
ERROR: 01-delivery-32/ failed to process
       01-delivery-32 (Is a directory)
```

## Parsing Errors

The utility makes use of a fairly strict PDS3 label parser. If it fails to read
the file, then you'll get an error that looks something like this:

```shell
$ pds3to4 -r "02-parsing-error/"
Failed to parse file:
  02-parsing-error/cma_404470826rda00790050104ch11503p1.lbl:5:77	missing ')' at '\r'

ERROR: 02-parsing-error/cma_404470826rda00790050104ch11503p1.lbl failed to process
       failed to parse "02-parsing-error/cma_404470826rda00790050104ch11503p1.lbl"
```

This tell us that there's a problem in the
`02-parsing-error/cma_404470826rda00790050104ch11503p1.lbl` file at line 5
around character 77. Taking a look at the file, we see that the `)` at the end
of the 5th line is missing.

```plain
   3   │ RECORD_BYTES                  = 12288
   4   │ FILE_RECORDS                  = 981
   5   │ ^HEADER                       = ("CMA_404470826RDA00790050104CH11503P1.CSV",1
   6   │ ^SPREADSHEET                  = ("CMA_404470826RDA00790050104CH11503P1.CSV",2)
   7   │ DATA_SET_ID                   = "MSL-M-CHEMIN-4-RDR-V1.0"
```

## Validation Errors

The utility uses the [PDS Validate]() validation tool to automatically check
the generated PDS4 XML file. If the validation fails, you'll see an error that
looks something like

```shell
$ pds3to4 -r -d `03-validation-error/`
ERROR: 03-validation-error/cma_404470826rda00790050104ch11503p1.lbl failed to process
       generated file failed to validate
```

The resulting XML file will be renamed to `*.xml.err` and the report will be
retained (even if the `-d` flag is provided)

```plain
╭───┬─────────────────────────────────────────────────────────────────────┬──────┬─────────╮
│ # │                                name                                 │ type │  size   │
├───┼─────────────────────────────────────────────────────────────────────┼──────┼─────────┤
│ 0 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.csv        │ file │ 11.1 KB │
│ 1 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.lbl        │ file │  3.9 KB │
│ 2 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.xml.err    │ file │ 11.2 KB │
│ 3 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.xml.report │ file │  1.6 KB │
╰───┴─────────────────────────────────────────────────────────────────────┴──────┴─────────╯
```

Taking a look at the report

```
...

Product Level Validation Results

  FAIL: file:03-validation-error/cma_404470826rda00790050104ch11503p1.xml
    Begin Content Validation: file:03-validation-error/cma_404470826rda00790050104ch11503p1.csv
      ERROR  [error.table.records_mismatch]   data object 2: Number of records read is not equal to the defined number of records in the label (expected 1000, got 980).
    End Content Validation: file:03-validation-error/cma_404470826rda00790050104ch11503p1.csv
        1 product validation(s) completed

...
```

tells us that the associated CSV file has 1000 rows in it, but the label file
says there should be 980.

If you are sure that the validation tool is wrong, you can simply remove the
report file and renamed the `*.xml.err` file to `*.xml`.

If you would like to run the conversion without using the validation tool, you
can provide the `-n` flag at the command line.

```shell
$ pds3to4 -r -n `03-validation-error/`
```

```plain
╭───┬──────────────────────────────────────────────────────────────┬──────┬─────────┬─────────────╮
│ # │                             name                             │ type │  size   │  modified   │
├───┼──────────────────────────────────────────────────────────────┼──────┼─────────┼─────────────┤
│ 0 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.csv │ file │ 11.1 KB │ an hour ago │
│ 1 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.lbl │ file │  3.9 KB │ an hour ago │
│ 2 │ 03-validation-error/cma_404470826rda00790050104ch11503p1.xml │ file │ 11.2 KB │ now         │
╰───┴──────────────────────────────────────────────────────────────┴──────┴─────────┴─────────────╯
```

## Custom Templates

By default, the utility uses [Velocity]() templates that are bundled with the
utility to generate the PDS4 output. You can override this entirely by
providing the `-t <template>` option at the command line.

**NOTE** Since the example template won't validate, we also provide the `-n`
flag.

```shell
$ pds3to4 -r -n -t `04-custom-template/template.vm` `04-custom-template/`
```

The example template looks like

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<Product_Observational>
  <Identification_Area>
    <logical_identifier>
      $label.LogicalIdentifier
    </logical_identifier>
  </Identification_Area>
</Product_Observational>
```

and the resulting file looks like this

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>

<Product_Observational>
  <Identification_Area>
    <logical_identifier>
      cma_404470826rda00790050104ch11503p1
    </logical_identifier>
  </Identification_Area>
</Product_Observational>
```

## Custom Column Formats

By default, the utility uses (slightly modified) the PD3-style fmt format files
bundled with the tool to determine what the columns of the CSV data file look
like. For the RDA products, the format is

```plain
OBJECT       = FIELD
 NAME        = "2-THETA"
 DATA_TYPE   = ASCII_REAL
 UNIT        = "DEGREES"
 BYTES       = 6
 FORMAT      = "F6.2"
 DESCRIPTION = "2-theta"
END_OBJECT   = FIELD

OBJECT       = FIELD
 NAME        = "INTENSITY"
 DATA_TYPE   = ASCII_REAL
 UNIT        = "COUNTS"
 BYTES       = 7
 FORMAT      = "F7.0"
 DESCRIPTION = "The intensity of the diffraction for each 2-theta value in column 1"
END_OBJECT   = FIELD
```

The `DESCRIPTION` property is something we've added to allow you to specify the
description of the column in the XML label. The resulting output is

```xml
<Record_Delimited>
  <fields>2</fields>
  <groups>0</groups>
  <Field_Delimited>
    <name>2-THETA</name>
    <field_number>1</field_number>
    <data_type>ASCII_Real</data_type>
    <maximum_field_length unit="byte">6</maximum_field_length>
    <unit>degrees</unit>
    <description>2-theta</description>
  </Field_Delimited>
  <Field_Delimited>
    <name>INTENSITY</name>
    <field_number>2</field_number>
    <data_type>ASCII_Real</data_type>
    <maximum_field_length unit="byte">7</maximum_field_length>
    <unit>counts</unit>
    <description>The intensity of the diffraction for each 2-theta value in column 1</description>
  </Field_Delimited>
</Record_Delimited>
```

If FMT file does not have a `DESCRIPTION` property, then the `NAME` is used
for the `<description></description>` in the output.

You can provide your own format, if you'd like, using the `-f <format>` option.

**NOTE** Since the result will not validate, we provide the `-n` flag.

```shell
$ pds3to4 -r -d -n -f `05-custom-format/format.fmt` `05-custom-format/`
```

If `05-custom-format/format.fmt` has the following content (which it does),

```plain
OBJECT       = FIELD
 NAME        = "TWO-THETA"
 DATA_TYPE   = ASCII_REAL
 UNIT        = "DEGREES"
 BYTES       = 6
 FORMAT      = "F6.2"
 DESCRIPTION = "2-theta"
END_OBJECT   = FIELD
```

Then the resulting record properties will be

```xml
<Record_Delimited>
  <fields>1</fields>
  <groups>0</groups>
  <Field_Delimited>
    <name>TWO-THETA</name>
    <field_number>1</field_number>
    <data_type>ASCII_Real</data_type>
    <maximum_field_length unit="byte">6</maximum_field_length>
    <unit>degrees</unit>
    <description>2-theta</description>
  </Field_Delimited>
</Record_Delimited>
```
