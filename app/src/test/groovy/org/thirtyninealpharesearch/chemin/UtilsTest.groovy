package org.thirtyninealpharesearch.chemin

import org.thirtyninealpharesearch.chemin.Utils;

import spock.lang.*

class UtilsTest extends Specification {
    def "wrap empty string"() {
        when:
        def text = ""
        def width = 100
        def prefix = ""
        def left_delimiter = ""
        def right_delimiter = ""

        def expected = ""

        then:
        Utils.wrap(text, width, prefix, left_delimiter, right_delimiter) == expected
    }

    def "wrap short string"() {
        when:
        def text = "This is a short string"
        def width = 100
        def prefix = ""
        def left_delimiter = ""
        def right_delimiter = ""

        def expected = "This is a short string"

        then:
        Utils.wrap(text, width, prefix, left_delimiter, right_delimiter) == expected
    }

    def "wrap string in middle of word"() {
        when:
        def text = "This is a short string"
        def width = 6
        def prefix = ""
        def left_delimiter = ""
        def right_delimiter = ""

        def expected = "This\nis a\nshort\nstring"

        then:
        Utils.wrap(text, width, prefix, left_delimiter, right_delimiter) == expected
    }

    def "wrap respects prefix"() {
        when:
        def text = "This is a short string"
        def width = 10
        def prefix = "  "
        def left_delimiter = ""
        def right_delimiter = ""

        def expected = "This is\n  a short\n  string"

        then:
        Utils.wrap(text, width, prefix, left_delimiter, right_delimiter) == expected
    }

    def "wrap respects delimiters "() {
        when:
        def text = "This table contains diffraction-all K-alpha diffraction data for the fourth scooped soil sample from the Rocknest target, analyzed in CheMin cell number 1a (Kapton window). The table represents results from sequences uploaded from sol00077 to sol00088, including 6,840 individual 10-second frames in 38 minor frames of 180 individual 10-second frames. CCD temperatures during data collection were ~-50 degrees centigrade. Column 1 of the table lists 2-theta from 3.00 to 51.95 degrees cobalt K-alpha, in increments of 0.05 degrees (980 entries). Column 2 lists the intensity of the diffraction for each 2-theta value in column 1."
        def width = 80
        def prefix = "    "
        def left_delimiter = "<description>"
        def right_delimiter = "</description>"

        def expected = "This table contains diffraction-all K-alpha diffraction data\n    for the fourth scooped soil sample from the Rocknest target, analyzed in\n    CheMin cell number 1a (Kapton window). The table represents results from\n    sequences uploaded from sol00077 to sol00088, including 6,840 individual\n    10-second frames in 38 minor frames of 180 individual 10-second frames. CCD\n    temperatures during data collection were ~-50 degrees centigrade. Column 1\n    of the table lists 2-theta from 3.00 to 51.95 degrees cobalt K-alpha, in\n    increments of 0.05 degrees (980 entries). Column 2 lists the intensity of\n    the diffraction for each 2-theta value in column 1."

        then:
        Utils.wrap(text, width, prefix, left_delimiter, right_delimiter) == expected
    }
}
