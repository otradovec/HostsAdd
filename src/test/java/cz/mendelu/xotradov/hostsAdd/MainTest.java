package cz.mendelu.xotradov.hostsAdd;

import org.junit.Test;

public class MainTest {


    @Test
    public void start() {
        new Main().start( "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt", "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt");
    }
}