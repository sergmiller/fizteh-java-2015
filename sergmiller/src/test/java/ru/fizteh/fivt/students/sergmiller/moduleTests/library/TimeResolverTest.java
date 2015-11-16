

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TimeResolver;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sergmiller on 19.10.15.
 */

public class TimeResolverTest extends TestCase {
    private List<LocalDateTime> testedDates = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        testedDates.add(LocalDateTime.of(2014, 9, 12, 10, 30, 10));
        testedDates.add(LocalDateTime.of(2014, 9, 12, 10, 29, 10));
        testedDates.add(LocalDateTime.of(2014, 9, 12, 10, 20, 10));
        testedDates.add(LocalDateTime.of(2014, 9, 12, 8, 20, 10));
        testedDates.add(LocalDateTime.of(2014, 9, 11, 23, 20, 10));
        testedDates.add(LocalDateTime.of(2014, 9, 1, 23, 20, 10));
    }

    @Test
    public void testDeterminerDifference() throws Exception {
        String[] results = new String[5];
        for (int i = 0; i < testedDates.size() - 1; ++i) {
            results[i] = TimeResolver.getTime(testedDates.get(i+1), testedDates.get(i));
        }

        assertEquals(results[0], "Только что");
        assertEquals(results[1], "9 минут назад");
        assertEquals(results[2], "2 часа назад");
        assertEquals(results[3], "Вчера");
        assertEquals(results[4], "10 дней назад");
    }

}