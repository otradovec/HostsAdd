package cz.mendelu.xotradov.hostsAdd;

import org.junit.Test;

public class MainTest {


    @Test
    public void start() {
        new Main().start("newHosts.txt", "cz/mendelu/xotradov/hostsAdd/hosts.txt");
    }
}