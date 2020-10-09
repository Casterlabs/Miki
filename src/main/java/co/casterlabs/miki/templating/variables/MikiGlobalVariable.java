package co.casterlabs.miki.templating.variables;

import java.util.Map;

import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.ToString;

@ToString(callSuper = true)
public class MikiGlobalVariable extends MikiVariable {

    @Override
    public String evaluate(Map<String, String> variables, Map<String, String> globals) throws MikiTemplatingException {
        return globals.getOrDefault(this.name, "");
    }

}
