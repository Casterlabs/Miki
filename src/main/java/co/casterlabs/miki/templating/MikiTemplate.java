package co.casterlabs.miki.templating;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.variables.MikiScriptVariable;
import co.casterlabs.miki.templating.variables.MikiVariable;
import co.casterlabs.miki.templating.variables.scripting.ScriptProvider;
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
        return this.formatAsWeb(variables, globals, new WebRequest()).getResult();
    }

    public WebResponse formatAsWeb(@NonNull Map<String, String> variables, @NonNull Map<String, String> globals, @NonNull WebRequest request) throws MikiTemplatingException {
        WebResponse response = new WebResponse();
        String result = this.template;

        globals = MikiUtil.lowercaseMap(globals);

        for (MikiVariable variable : this.variables) {
            String replacement = null;

            if (variable instanceof MikiScriptVariable) {
                ScriptProvider provider = ((MikiScriptVariable) variable).evaluateAsWeb(variables, globals, request);

                replacement = provider.getResult();

                response.setMime(provider.getMime());
                response.setStatus(provider.getStatus());
                response.getHeaders().putAll(provider.getHeaders());
            } else {
                replacement = variable.evaluate(variables, globals);
            }

            if (replacement != null) {
                replacement = MikiUtil.escapeString(replacement);
                result = result.replace(variable.getKey(), replacement);
            } else {
                throw new MikiTemplatingException("Supplied variables are missing the key: " + variable);
            }
        }

        response.setResult(MikiUtil.unescapeString(result));

        return response;
    }

    public String format(@NonNull Map<String, String> variables) throws MikiTemplatingException {
        return this.format(variables, Collections.emptyMap());
    }

}
