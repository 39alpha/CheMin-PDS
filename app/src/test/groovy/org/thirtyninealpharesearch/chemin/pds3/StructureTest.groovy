package org.thirtyninealpharesearch.chemin.pds3

import spock.lang.Specification

class StructureTest extends Specification {
    def "parses xrd.fmt"() {
        setup:
        def filename = "src/test/data/pds3/xrd.fmt"

        when:
        def fmt = Structure.parseFile filename

        then:
        fmt != null
        fmt.getFilename() == filename

        fmt.getObjects() != null

        def objects = fmt.getObjects()
        objects.size() == 2

        def twoTheta = objects.get 0
        twoTheta.getName() == "2-THETA"
        twoTheta.getDataType() == "ASCII_Real"
        twoTheta.getUnit() == "DEGREES"
        twoTheta.getBytes() == 6
        twoTheta.getFormat() == "F6.2"
        twoTheta.getDescription() == "2-theta"

        def intensity = objects.get 1
        intensity.getName() == "INTENSITY"
        intensity.getDataType() == "ASCII_Real"
        intensity.getUnit() == "COUNT"
        intensity.getBytes() == 7
        intensity.getFormat() == "F7.0"
        intensity.getDescription() == "The intensity of the diffraction for each 2-theta value in column 1"
    }

    def "parses min.fmt"() {
        setup:
        def filename = "src/test/data/pds3/min.fmt"

        when:
        def fmt = Structure.parseFile filename

        then:
        fmt != null
        fmt.getFilename() == filename

        fmt.getObjects() != null

        def objects = fmt.getObjects()
        objects.size() == 3

        def mineral = objects.get 0
        mineral.getName() == "MINERAL"
        mineral.getDataType() == "ASCII_String"
        mineral.getUnit() == "TEXT"
        mineral.getBytes() == 16
        mineral.getFormat() == "A16"
        mineral.getDescription() == "Mineral identification"

        def percent = objects.get 1
        percent.getName() == "PERCENT"
        percent.getDataType() == "ASCII_Real"
        percent.getUnit() == "WEIGHT_PERCENT"
        percent.getBytes() == 9
        percent.getFormat() == "F9.2"
        percent.getDescription() == "The mineral abundances in weight percent"

        def error = objects.get 2
        error.getName() == "ERROR"
        error.getDataType() == "ASCII_Real"
        error.getUnit() == "ESTIMATED_ERROR"
        error.getBytes() == 7
        error.getFormat() == "F7.2"
        error.getDescription() == "The estimated 2-sigma analytical errors"
    }

    def "parses edh.fmt"() {
        setup:
        def filename = "src/test/data/pds3/edh.fmt"

        when:
        def fmt = Structure.parseFile filename

        then:
        fmt != null
        fmt.getFilename() == filename

        fmt.getObjects() != null

        def objects = fmt.getObjects()
        objects.size() == 2

        def energy = objects.get 0
        energy.getName() == "ENERGY"
        energy.getDataType() == "ASCII_Real"
        energy.getUnit() == "KEV"
        energy.getBytes() == 8
        energy.getFormat() == "F8.5"
        energy.getDescription() == "Energy in keV converted from digital number (DN)"

        def intensity = objects.get 1
        intensity.getName() == "INTENSITY"
        intensity.getDataType() == "ASCII_Real"
        intensity.getUnit() == "COUNT"
        intensity.getBytes() == 8
        intensity.getFormat() == "F8.0"
        intensity.getDescription() == "The log-scale intensity for each value of keV in column 1"
    }
}
