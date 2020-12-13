package co.casterlabs.miki;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.casterlabs.miki.parsing.MikiParsingException;
import co.casterlabs.miki.templating.MikiTemplate;
import co.casterlabs.miki.templating.MikiTemplatingException;
import co.casterlabs.miki.templating.variables.MikiVariable;
import lombok.NonNull;

public class Miki {
    public static final String VERSION = "1.8.0";
    @Deprecated
    public static boolean ideEnviroment = false;

    public static final char OPENING = '[';
    public static final char CLOSING = ']';
    public static final char ESCAPE = '\\';

    public static MikiTemplate parse(@NonNull String input) throws MikiParsingException, MikiTemplatingException {
        List<MikiVariable> variables = new ArrayList<>();

        for (MikiSymbol symbol : MikiSymbol.values()) {
            for (Map.Entry<String, String> variable : MikiUtil.parseKeys(input, symbol.getSign(), OPENING, CLOSING, ESCAPE).entrySet()) {
                variables.add(symbol.get().init(variable.getKey(), variable.getValue()));
            }
        }

        return new MikiTemplate(variables, input);
    }
}
