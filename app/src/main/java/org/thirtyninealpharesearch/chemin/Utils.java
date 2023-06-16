package org.thirtyninealpharesearch.chemin;

import java.lang.Math;
import java.util.ArrayList;

public class Utils {
    public static String wrap(
        String text,
        int width,
        String prefix,
        String left_delimiter,
        String right_delimiter
    ) {
        text = text.replaceAll("[\\r\\n]+", " ").replaceAll("\\s+", " ").trim();
        text = text + right_delimiter;

        int prefix_length = prefix.length();
        int left_delimiter_length = left_delimiter.length();

        ArrayList<String> lines = new ArrayList<String>();
        while (text.length() > 0) {
            int line_length = width - prefix_length;
            if (lines.isEmpty()) {
                line_length -= left_delimiter_length;
            }

            if (text.length() <= line_length) {
                String line = text;
                if (!lines.isEmpty()) {
                    line = prefix + line;
                }
                line = line.substring(0, line.length() - right_delimiter.length());
                lines.add(line);
                break;
            }

            int i = Math.min(line_length, text.length() - 1);
            while (i >= 1 && text.charAt(i) != ' ') {
                --i;
            }
            if (i == 0) {
                while (i < text.length() && text.charAt(i) != ' ') {
                    ++i;
                }
            }

            String line = text.substring(0, i);
            if (!lines.isEmpty()) {
                line = prefix + line;
            }

            if (i >= text.length()) {
                text = "";
                line = line.substring(0, line.length() - right_delimiter.length());
            } else {
                text = text.substring(i + 1);
            }

            lines.add(line);
        }

        text = "";
        if (lines.isEmpty()) {
            return text;
        }

        text = lines.get(0);
        lines.remove(0);
        for (String line : lines) {
            text += "\n" + line;
        }

        return text;
    }
}
