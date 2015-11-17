package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

/**
 * Created by sergmiller on 11.11.15.
 */

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.twitterStream.JCommanderParser;
import java.util.ArrayList;
import java.util.List;


public class JCommanderParserTest extends TestCase {
    private JCommanderParser jCommanderParser;
    private JCommander jCommander;
    private String[] args;

    @Before
    public void setUp() throws Exception {
        args = new String[11];
        args[0] = "-q";
        args[1] = "doctorWho";
        args[2] = "-l";
        args[3] = "265";
        args[4] = "-p";
        args[5] = "London";
        args[6] = "--hideRetweets";
        args[7] = "-q";
        args[8] = "tardis";
        args[9] = "-s";
        args[10] = "-h";
    }

    @Test
    public void testJCommanderParser() throws Exception {
        jCommanderParser = new JCommanderParser();
        jCommander = new JCommander(jCommanderParser, args);
        List<String> parsedQuery = new ArrayList<>();
        parsedQuery.add("doctorWho");
        parsedQuery.add("tardis");
        assertEquals(jCommanderParser.getLimit(), new Integer(265));
        assertEquals(jCommanderParser.getQuery(), parsedQuery);
        assertEquals(jCommanderParser.getLocation(), "London");
        assertEquals(jCommanderParser.isHideRetweets(), true);
        assertEquals(jCommanderParser.isStream(), true);
        assertEquals(jCommanderParser.isHelp(), true);
    }
}