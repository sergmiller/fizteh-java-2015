package ru.fizteh.fivt.students.sergmiller.collectionQLTests;

/**
 * Created by sergmiller on 18.12.15.
 */

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student;
import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student.student;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.Conditions.rlike;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.Conditions.like;


public class ConditionsTest extends TestCase {
    private Function<Student, String> function = Student::getName;
    List<Student> correct = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        correct.add(student("miller", LocalDate.parse("1996-10-25"), "494"));
        correct.add(student("ivanov", LocalDate.parse("1997-01-01"), "495"));
        correct.add(student("zotov", LocalDate.parse("1997-03-18"), "496"));
    }

    @Test
    public void testRlike() throws Exception {
        assertEquals(rlike(function, ".*er").test(correct.get(0)), true);
        assertEquals(rlike(function, ".*ov").test(correct.get(0)), false);
        assertEquals(rlike(function, ".*ov").test(correct.get(1)), true);
        assertEquals(rlike(function, ".*ov").test(correct.get(2)), true);

    }

    public void testLike() throws Exception {
        assertEquals(like(function, "miller").test(correct.get(0)), true);
        assertEquals(like(function, "ivanov").test(correct.get(0)), false);
        assertEquals(like(function, "ivanov").test(correct.get(1)), true);
        assertEquals(like(function, "zotov").test(correct.get(2)), true);
    }
}