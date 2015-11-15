

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.twitterStream.TimeResolver;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by alex on 08.11.15.
 */
public class TimeResolverTest extends TestCase {
    private Date[] dates;
    private LocalDateTime[] localDateTime;

    @Before
    public void setUp() throws Exception {
        dates = new Date[6];
        localDateTime = new LocalDateTime[6];
        dates[0] = new Date(2014, 9, 12, 10, 30, 10);
        dates[1] = new Date(2014, 9, 12, 10, 29, 10);
        dates[2] = new Date(2014, 9, 12, 10, 20, 10);
        dates[3] = new Date(2014, 9, 12, 8, 20, 10);
        dates[4] = new Date(2014, 9, 11, 23, 20, 10);
        dates[5] = new Date(2014, 9, 1, 23, 20, 10);
        for (int i = 0; i < dates.length; ++i) {
            localDateTime[i] = dates[i].toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    @Test
    public void testDeterminerDifference() throws Exception {
        String[] results = new String[5];
        for (int i = 0; i < dates.length - 1; ++i) {
            results[i] = TimeResolver.getTime(localDateTime[i + 1], localDateTime[i]);
        }

        assertEquals(results[0], "Только что");
        assertEquals(results[1], "9 минут назад");
        assertEquals(results[2], "2 часа назад");
        assertEquals(results[3], "Вчера");
        assertEquals(results[4], "10 дней назад");
    }

}