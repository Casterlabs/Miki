package co.casterlabs.miki.templating.variables;

import java.util.Map;

import co.casterlabs.miki.MikiUtil;
import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.ToString;

@ToString(callSuper = true)
public class MikiFileVariable extends MikiVariable {

    @Override
    public MikiVariable init(String key, String name) throws MikiTemplatingException {
        this.key = key;
        this.name = MikiUtil.unescapeString(name);

        return this;
    }

    @Override
    public String evaluate(Map<String, String> variables, Map<String, String> globals) throws MikiTemplatingException {
        return MikiUtil.getFromURI(this.name);
    }

}
