package pl.snowdog.dzialajlokalnie.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.QuoteSpan;
import android.text.style.StyleSpan;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bartek on 23.07.15.
 */
public class StringUtil {

    public static String oneLine(String s) {
        if (s == null) {
            return null;
        }

        return s.replaceAll("\n", " ");
    }

    public static String date2String(Date date) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(date);
    }

    public static SpannableString styleString(String string) {
        if (string == null) {
            return null;
        }

        String[] lines = string.split("\n");
        boolean[] quoteLines = new boolean[lines.length];

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("> ")) {
                quoteLines[i] = true;
                lines[i] = lines[i].replaceFirst("> ", "");
            }
            lines[i] = lines[i] + "\n";
            stringBuilder.append(lines[i]);
        }

        SpannableString spannableString = new SpannableString(stringBuilder.toString());

        int startIndex = 0;
        for (int i = 0; i < lines.length; i++) {
            if (quoteLines[i]) {
                spannableString.setSpan(new QuoteSpan(Color.parseColor("#FF5252")),
                        startIndex, startIndex+lines[i].length(), 0);
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC),
                        startIndex, startIndex+lines[i].length(), 0);
            }
            startIndex += lines[i].length();
        }


        return spannableString;
    }
}
