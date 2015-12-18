package ru.fizteh.fivt.students.sergmiller.collectionQLTests;

/**
 * Created by sergmiller on 18.12.15.
 */

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student;
import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student.student;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.Conditions.rlike;


@RunWith(MockitoJUnitRunner.class)
public class ConditionsTest extends TestCase {
    private Function<Student, String> function = Student::getName;

    @Test
    public void testRlike() throws Exception {
        List<Student> correct = new ArrayList<>();
        correct.add(student("miller", LocalDate.parse("1996-10-25"), "494"));
        correct.add(student("ivanov", LocalDate.parse("1997-01-01"), "495"));
        correct.add(student("zotov", LocalDate.parse("1997-03-18"), "496"));

        assertEquals(rlike(function, ".*er").test(correct.get(0)), true);
        assertEquals(rlike(function, ".*ov").test(correct.get(0)), false);
        assertEquals(rlike(function, ".*ov").test(correct.get(1)), true);
        assertEquals(rlike(function, ".*ov").test(correct.get(2)), true);
    }

//    @Test(expected = UnsupportedOperationException.class)
//    public void testLike() throws Exception {
//        Conditions.like(function, "zzz");
//    }
}