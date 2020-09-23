package co.casterlabs.miki.templating.variables;

import java.util.Map;

import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MikiVariable {
    protected String key;
    protected String name;

    public MikiVariable init(String key, String name) {
        this.key = key;
        this.name = MikiUtil.unescapeString(name);

        return this;
    }

    public String evaluate(Map<String, String> variables) throws MikiTemplatingException {
        String variable = variables.get(this.name);

        if (variable != null) {
            return variable;
        } else {
            throw new MikiTemplatingException("Supplied variables are missing the key: " + this.name);
        }
    }

    public boolean requireGlobal() {
        return false;
    }

}
