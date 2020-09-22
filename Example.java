package co.casterlabs.miki;

import java.util.HashMap;
import java.util.Map;

public class Example {

    public static void main(String[] args) throws Exception {
        String template = "%\\\\[This is how you escape variables]\n\nIt will not treat variable text as a variable itself, it will replace all variables that match:\n(Using this as a test: %\\[test1]=%\\[test2] %\\[test2]=(test 2))\n%[test1] %[test2]";
        Miki miki = Miki.parse(template);

        Map<String, String> variables = new HashMap<>();

        variables.put("test1", "%[test2]");
        variables.put("test2", "(test 2)");

        System.out.println(miki.format(variables));

        // Output:
        // %\[This is how you escape variables]
        //
        // It will not treat variable text as a variable itself, it will replace all variables that match:
        // (Using this as a test: %[test1]=%[test2] %[test2]=(test 2))
        // %[test2] (test 2)

    }

}
