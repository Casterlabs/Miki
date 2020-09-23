package co.casterlabs.miki.templating.variables;

import java.util.Map;

import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.ToString;

@ToString(callSuper = true)
public class MikiGlobalVariable extends MikiVariable {

    @Override
    public String evaluate(Map<String, String> variables) throws MikiTemplatingException {
        return variables.getOrDefault(this.name, "");
    }

    @Override
    public boolean requireGlobal() {
        return true;
    }

}
