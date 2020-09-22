package co.casterlabs.miki.templating;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

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

    private @Nullable String preformatted;

    public String format(@NonNull Map<String, String> variables) throws MikiTemplatingException {
        String result = this.template;

        for (MikiVariable variable : this.variables) {
            String replacement = variable.evaluate(variables);

            if (replacement != null) {
                replacement = MikiUtil.escapeString(replacement);
                result = result.replace(variable.getKey(), replacement);
            } else {
                throw new MikiTemplatingException("Supplied variables are missing the key: " + variable);
            }
        }

        return MikiUtil.unescapeString(result);
    }

    public void preformat(@NonNull Map<String, String> variables) throws MikiTemplatingException {
        this.preformatted = this.format(variables);
    }

    public boolean isPreformatted() {
        return this.preformatted != null;
    }

}
