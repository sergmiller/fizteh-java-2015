package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

/**
 * Created by sergmiller on 17.11.15.
 */

import com.beust.jcommander.JCommander;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TwitterStreamRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JCommander.class, TwitterStreamRunner.class})
public class TwitterRunnerTest extends TestCase {
    private String[] args;
    JCommander jCommanderWithUsage;

    @Before
    public void setUp() throws Exception {
        args = new String[3];
        args[0] = "-q";
        args[1] = "query";
        args[2] = "--help";
        jCommanderWithUsage = PowerMockito.mock(JCommander.class);
        PowerMockito.whenNew(JCommander.class).withAnyArguments().thenReturn(jCommanderWithUsage);
        doNothing().when(jCommanderWithUsage).usage(any(StringBuilder.class));
    }

    @Test
    public void testGetHelp() throws Exception {
        TwitterStreamRunner twitterStreamRunner = new TwitterStreamRunner();
        twitterStreamRunner.main(args);

        verify(jCommanderWithUsage).usage(any(StringBuilder.class));
    }
}