package co.casterlabs.miki;

import java.io.File;

import co.casterlabs.miki.json.MikiFileAdapter;

public class Example {

    public static void main(String[] args) throws Exception {
        MikiFileAdapter miki = MikiFileAdapter.readFile(new File("example/errorpage.miki"));

        System.out.println(miki.format());
    }

}
