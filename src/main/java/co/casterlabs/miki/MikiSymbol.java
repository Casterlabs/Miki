package co.casterlabs.miki;

import co.casterlabs.miki.templating.variables.MikiConditionalVariable;
import co.casterlabs.miki.templating.variables.MikiFileVariable;
import co.casterlabs.miki.templating.variables.MikiGlobalVariable;
import co.casterlabs.miki.templating.variables.MikiVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MikiSymbol {
    VARIABLE_SIGN('%', MikiVariable.class),
    FILE_SIGN('@', MikiFileVariable.class),
    GLOBAL_SIGN('#', MikiGlobalVariable.class),
    CONDITIONAL_SIGN('^', MikiConditionalVariable.class);

    private @Getter char sign;
    private Class<? extends MikiVariable> clazz;

    public MikiVariable get() {
        try {
            return this.clazz.newInstance();
        } catch (Exception ignored) {
            return null;
        }
    }

}
