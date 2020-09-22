package co.casterlabs.miki;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import co.casterlabs.miki.parsing.MikiParsingException;
import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class Miki {
    public static final String VERSION = "1.0.0";

    private static final char SIGN = '%';
    private static final char OPENING = '[';
    private static final char CLOSING = ']';
    private static final char ESCAPE = '\\';

    private @NonNull List<String> variables;
    private @NonNull String template;

    private @Nullable String preformatted;

    public String format(@NonNull Map<String, String> variables) throws MikiTemplatingException {
        String result = this.template;

        for (String variable : this.variables) {
            String replacement = variables.get(variable);

            if (replacement != null) {
                String variableName = MikiUtil.getVariableName(SIGN, OPENING, CLOSING, variable);

                replacement = replacement.replace(String.valueOf(SIGN), String.valueOf(SIGN) + ESCAPE); // Escape signs that are in variables.

                result = result.replace(variableName, replacement);
            } else {
                throw new MikiTemplatingException("Supplied variables are missing the key: " + variable);
            }
        }

        return result.replace(SIGN + String.valueOf(ESCAPE), String.valueOf(SIGN)); // Unescape escaped signs.
    }

    public void preformat(@NonNull Map<String, String> variables) throws MikiTemplatingException {
        this.preformatted = this.format(variables);
    }

    public boolean isPreformatted() {
        return this.preformatted != null;
    }

    public static Miki parse(@NonNull String input) throws MikiParsingException {
        List<String> variables = new ArrayList<>();
        List<Integer> signPositions = MikiUtil.getSignPositions(input, SIGN, ESCAPE);

        for (int position : signPositions) {
            String variable = MikiUtil.readFrom(position, OPENING, CLOSING, ESCAPE, SIGN, input);

            if (variable != null) {
                variables.add(variable);
            }
        }

        return new Miki(variables, input);
    }
}
