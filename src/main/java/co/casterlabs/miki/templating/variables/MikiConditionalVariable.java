package co.casterlabs.miki.templating.variables;

import java.util.Map;

import co.casterlabs.miki.templating.MikiTemplatingException;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
public class MikiConditionalVariable extends MikiVariable {
    private Operator operand;
    private String expectedValue;
    private String[] out;

    @Override
    public MikiConditionalVariable init(String key, String sequence) {
        String[] sides = sequence.split("\\?");

        if (sides.length == 2) {
            this.operand = Operator.get(sides[0]); // left side of ?

            String[] conditionals = sides[0].split(this.operand.symbol);

            if (conditionals.length != 2) {
                throw new IllegalArgumentException("Invalid amount of conditionals. Expected 2, got " + conditionals.length + " from: " + sequence);
            }

            this.expectedValue = conditionals[1];

            super.init(key, conditionals[0]);

            this.out = sides[1].split(":");

            if (this.out.length != 2) {
                throw new IllegalArgumentException("Invalid amount of outputs. Expected 2, got " + this.out.length + " from: " + sequence);
            }
        } else {
            throw new IllegalArgumentException("Invalid amount of arguments. Expected 2, got " + sides.length + " from: " + sequence);
        }

        return this;
    }

    @Override
    public String evaluate(Map<String, String> variables, Map<String, String> globals) throws MikiTemplatingException {
        String variable = variables.get(this.name);

        if (variable != null) {
            boolean result = variable.equals(this.expectedValue);

            if (this.operand == Operator.NOT_EQUALS) {
                result = !result;
            }

            return this.out[result ? 0 : 1];
        } else {
            throw new MikiTemplatingException("Supplied variables are missing the key for conditional: " + this.name);
        }
    }

    @AllArgsConstructor
    private static enum Operator {
        EQUALS("=="),
        NOT_EQUALS("!=");

        private String symbol;

        public static Operator get(String input) {
            for (Operator op : Operator.values()) {
                if (input.contains(op.symbol)) {
                    return op;
                }
            }

            throw new IllegalArgumentException("No valid operand: " + input);
        }
    }

}
