# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- The intensity unit in chemin_xrd.fmt from "COUNTS" to "COUNT"

## [v0.1.0]

### Added

- The `Utils` class with the static `wrap` method to wrap text to a width.
- The `Label.Object.getWrappedDescription` method to get the object's
  description wrapped to a particular width.
- The `-x` and `-s` flags to allow the user to specify the schema to be used
  for validation.

### Changed

- The templates to reference the PDS4_MSL_1J00_1100 schema and include Jennifer
  Ward's suggested changes.

### Fixed

- The `Structure.g4` grammar's to parse parentheses in quoted strings.

## [v0.0.2]

### Added

- <File_Area_Observational_Supplemental> section to all templates
- Revision notes in <Modification_History>

### Fixed

- Correctly set the <msl:sol_number>

## [v0.0.1]

- Initial release of pds3to4
