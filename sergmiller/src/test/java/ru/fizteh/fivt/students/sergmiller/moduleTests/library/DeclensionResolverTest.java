//package ru.fizteh.fivt.students.sergmiller.moduleTests.library;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.twitterStream.DeclensionResolver;

/**
 * Created by sergmiller on 19.10.15.
 */

public class DeclensionResolverTest extends TestCase {
    @Test
    public void testGetDeclensionFormRetweets() throws Exception{
        DeclensionResolver declensionResolver = new DeclensionResolver();
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 1), "ретвит");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 2), "ретвита");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 5), "ретвитов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 9), "ретвитов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 11), "ретвитов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 13), "ретвитов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 22), "ретвита");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.RETWEET, 104), "ретвита");
    }
    @Test
    public void testGetDeclensionFormMinutes() throws Exception{
        DeclensionResolver declensionResolver = new DeclensionResolver();
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 1), "минуту");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 2), "минуты");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 5), "минут");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 9), "минут");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 11), "минут");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 13), "минут");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 22), "минуты");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.MINUTE, 104), "минуты");
    }
    @Test
    public void testGetDeclensionFormHours() throws Exception{
        DeclensionResolver declensionResolver = new DeclensionResolver();
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 1), "час");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 2), "часа");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 5), "часов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 9), "часов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 11), "часов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 13), "часов");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 22), "часа");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.HOUR, 104), "часа");
    }
    @Test
    public void testGetDeclensionFormDays() throws Exception{
        DeclensionResolver declensionResolver = new DeclensionResolver();
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 1), "день");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 2), "дня");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 5), "дней");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 9), "дней");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 11), "дней");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 13), "дней");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 22), "дня");
        assertEquals(declensionResolver.getDeclensionForm(DeclensionResolver.Word.DAY, 104), "дня");
    }
}
