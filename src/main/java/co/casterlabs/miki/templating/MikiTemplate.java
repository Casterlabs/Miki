package co.casterlabs.miki.templating;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.variables.MikiVariable;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class MikiTemplate {
    private @NonNull List<MikiVariable> variables;
    private @NonNull String template;

    public String format(@NonNull Map<String, String> variables, @NonNull Map<String, String> globals) throws MikiTemplatingException {
        String result = this.template;

        globals = MikiUtil.lowercaseMap(globals);

        for (MikiVariable variable : this.variables) {
            String replacement = null;

            replacement = variable.evaluate(variables, globals);

            if (replacement != null) {
                replacement = MikiUtil.escapeString(replacement);
                result = result.replace(variable.getKey(), replacement);
            } else {
                throw new MikiTemplatingException("Supplied variables are missing the key: " + variable);
            }
        }

        return result;
    }

    public String format(@NonNull Map<String, String> variables) throws MikiTemplatingException {
        return this.format(variables, Collections.emptyMap());
    }

}
