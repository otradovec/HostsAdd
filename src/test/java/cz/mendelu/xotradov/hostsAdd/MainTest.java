package cz.mendelu.xotradov.hostsAdd;

import org.junit.Test;
import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void isFirstBeforeSecond() {
        Main main = new Main();
        assertTrue(main.isFirstBeforeSecond("0.0.0.0 a.cz","0.0.0.0 b.cz"));
        assertTrue(main.isFirstBeforeSecond("0.0.0.0 a.eu","0.0.0.0 b.cz"));
        assertTrue(main.isFirstBeforeSecond("0.0.0.0 b.cz","0.0.0.0 b.eu"));
        assertTrue(main.isFirstBeforeSecond("0.0.0.0 azzzzzz.cz","0.0.0.0 b.cz"));
        assertFalse(main.isFirstBeforeSecond("0.0.0.0 b.cz","0.0.0.0 b.cz"));
        assertFalse(main.isFirstBeforeSecond("0.0.0.0 c.cz","0.0.0.0 b.cz"));
        assertFalse(main.isFirstBeforeSecond("0.0.0.0 bbbb.bbb.bb","0.0.0.0 a.cz"));
    }

    @Test
    public void start() {
        new Main().start( "src/test/resources/cz/mendelu/xotradov/hostsAdd/newHosts.txt", "src/test/resources/cz/mendelu/xotradov/hostsAdd/hosts.txt");
    }


}