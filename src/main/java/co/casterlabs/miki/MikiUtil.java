package co.casterlabs.miki;

import java.util.ArrayList;
import java.util.List;

import co.casterlabs.miki.parsing.MikiParsingException;
import lombok.NonNull;

public class MikiUtil {

    public static List<Integer> getSignPositions(@NonNull String str, char sign, char escape) {
        List<Integer> positions = new ArrayList<Integer>();
        char[] chars = str.toCharArray();

        for (int i = 0; i != chars.length; i++) {
            if ((chars[i] == sign) && (chars[i + 1] != escape)) {
                positions.add(i);
            }
        }

        return positions;
    }

    public static String getVariableName(char sign, char opening, char closing, @NonNull String name) {
        return new StringBuilder().append(sign).append(opening).append(name).append(closing).toString();
    }

    public static String readFrom(int start, char opening, char closing, char escape, char sign, @NonNull String str) throws MikiParsingException {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();
        boolean escaped = false;

        if (chars[start + 1] == escape) {
            return null;
        } else if (chars[start + 1] != opening) {
            throw new MikiParsingException("Invalid variable begin at position: " + (start + 1));
        }

        for (int i = start + 2; i != chars.length; i++) {
            char current = chars[i];

            if (current == closing) {
                return sb.toString();
            }

            if (!escaped && (current == sign)) {
                throw new MikiParsingException("Came across another sign that was unescaped at: " + i);
            }

            sb.append(current);

            if (escaped) { // If escaped, read an extra character (effectively ignore markers)
                sb.append(chars[i + 1]);
                i++;
            }

            escaped = chars[i] == escape;
        }

        throw new MikiParsingException("Reached the end of the template with an unterminated variable.");
    }

}
