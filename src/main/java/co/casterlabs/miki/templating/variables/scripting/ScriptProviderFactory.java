package co.casterlabs.miki.templating.variables.scripting;

import javax.script.ScriptException;

public interface ScriptProviderFactory {

    public ScriptProvider newInstance() throws ScriptException;

    public String getName();

    public String getCompliance();

    public String[] getLimitations();

}
