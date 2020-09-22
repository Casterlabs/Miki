package co.casterlabs.miki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.casterlabs.miki.parsing.MikiParsingException;
import lombok.NonNull;

public class MikiUtil {

    public static Map<String, String> parseKeys(@NonNull String input, char sign, char opening, char closing, char escape) throws MikiParsingException {
        Map<String, String> variables = new HashMap<>();
        List<Integer> signPositions = getSignPositions(input, sign, escape);

        for (int position : signPositions) {
            String variable = readFrom(position, opening, closing, escape, sign, input);

            if (variable != null) {
                variables.put(String.format("%c%c%s%c", sign, opening, variable, closing), variable);
            }
        }

        return variables;
    }

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

    public static String escapeString(@NonNull String str) {
        str = str.replace(String.valueOf(Miki.VARIABLE_SIGN), Miki.VARIABLE_SIGN + String.valueOf(Miki.ESCAPE));
        str = str.replace(String.valueOf(Miki.FILE_SIGN), Miki.FILE_SIGN + String.valueOf(Miki.ESCAPE));

        return str;
    }

    public static String unescapeString(@NonNull String str) {
        str = str.replace(Miki.VARIABLE_SIGN + String.valueOf(Miki.ESCAPE), String.valueOf(Miki.VARIABLE_SIGN));
        str = str.replace(Miki.FILE_SIGN + String.valueOf(Miki.ESCAPE), String.valueOf(Miki.FILE_SIGN));

        return str;
    }

    public static String readFrom(int start, char opening, char closing, char escape, char sign, @NonNull String str) throws MikiParsingException {
        StringBuilder sb = new StringBuilder();
        char[] chars = str.concat(" ").toCharArray();
        boolean escaped = false;

        if (chars[start + 1] == escape) {
            return null;
        } else if (chars[start + 1] != opening) {
            throw new MikiParsingException("Invalid sign at position: " + (start + 1));
        }

        for (int i = start + 1; i != (chars.length - 1); i++) {
            char current = chars[i];

            escaped = chars[i + 1] == escape;

            if (!escaped) {
                if (current == closing) {
                    return sb.toString();
                } else if (current == opening) {
                    continue;
                }
            }

            if (!escaped && (current == sign)) {
                throw new MikiParsingException("Came across another sign that was unescaped at: " + i);
            }

            if ((current != escape) || escaped) {
                sb.append(current);
            }
        }

        throw new MikiParsingException("Reached the end of the template with an unterminated variable.");
    }

}
