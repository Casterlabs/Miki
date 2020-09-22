package co.casterlabs.miki;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.casterlabs.miki.parsing.MikiParsingException;
import co.casterlabs.miki.templating.MikiTemplate;
import co.casterlabs.miki.templating.variables.MikiFileVariable;
import co.casterlabs.miki.templating.variables.MikiVariable;
import lombok.NonNull;

public class Miki {
    public static final String VERSION = "1.0.0";
    public static final char VARIABLE_SIGN = '%';
    public static final char FILE_SIGN = '@';
    public static final char OPENING = '[';
    public static final char CLOSING = ']';
    public static final char ESCAPE = '\\';

    public static MikiTemplate parse(@NonNull String input) throws MikiParsingException {
        List<MikiVariable> variables = new ArrayList<>();

        for (Map.Entry<String, String> variable : MikiUtil.parseKeys(input, VARIABLE_SIGN, OPENING, CLOSING, ESCAPE).entrySet()) {
            variables.add(new MikiVariable(variable.getKey(), variable.getValue()));
        }

        for (Map.Entry<String, String> variable : MikiUtil.parseKeys(input, FILE_SIGN, OPENING, CLOSING, ESCAPE).entrySet()) {
            variables.add(new MikiFileVariable(variable.getKey(), variable.getValue()));
        }

        return new MikiTemplate(variables, input);
    }
}
