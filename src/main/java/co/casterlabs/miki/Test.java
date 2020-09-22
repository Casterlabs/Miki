package co.casterlabs.miki;

import java.util.Collections;

import co.casterlabs.miki.templating.MikiTemplate;

public class Test {

    public static void main(String[] args) throws Exception {
        String template = "@[https://google.com]%[1]";
        MikiTemplate miki = Miki.parse(template);

        System.out.println(miki.format(Collections.singletonMap("1", "\ntest!")));
    }

}
