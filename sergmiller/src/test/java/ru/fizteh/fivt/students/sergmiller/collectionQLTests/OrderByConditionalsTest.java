package ru.fizteh.fivt.students.sergmiller.collectionQLTests;

/**
 * Created by sergmiller on 18.12.15.
 */


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.collectionquery.OrderByConditions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student;
import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student.student;


public class OrderByConditionalsTest extends TestCase {
    private Function<Student, String> function = Student::getName;
    List<Student> correct = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        correct.add(student("miller", LocalDate.parse("1996-10-25"), "494"));
        correct.add(student("ivanov", LocalDate.parse("1997-01-01"), "495"));
        correct.add(student("zotov", LocalDate.parse("1997-03-18"), "496"));
    }

    @Test
    public void testAsc() throws Exception {
        assertTrue(OrderByConditions.asc(function).compare(correct.get(0), correct.get(2)) < 0);
        assertTrue(OrderByConditions.asc(function).compare(correct.get(1), correct.get(0)) < 0);
        assertTrue(OrderByConditions.asc(function).compare(correct.get(0), correct.get(0)) == 0);
    }

    @Test
    public void testDesc() throws Exception {
        assertTrue(OrderByConditions.desc(function).compare(correct.get(0), correct.get(2)) > 0);
        assertTrue(OrderByConditions.desc(function).compare(correct.get(1), correct.get(0)) > 0);
        assertTrue(OrderByConditions.desc(function).compare(correct.get(0), correct.get(0)) == 0);
    }
}