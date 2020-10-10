package co.casterlabs.miki.templating.variables.scripting;

import javax.script.ScriptException;

public interface ScriptProvider {

    public void eval(String script) throws ScriptException;

    public String getResult();

}
