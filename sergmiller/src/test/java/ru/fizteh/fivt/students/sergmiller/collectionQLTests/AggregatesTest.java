package ru.fizteh.fivt.students.sergmiller.collectionQLTests;

import org.mockito.runners.MockitoJUnitRunner;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.fizteh.fivt.students.sergmiller.collectionquery.Aggregates;
import ru.fizteh.fivt.students.sergmiller.collectionquery.agregatesImpl.Aggregator;

import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

//some copy-paste tests
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class AggregatesTest extends TestCase {

    private List<CollectionQueryBaseTest.Student> correct, emptylist;
    private Function<CollectionQueryBaseTest.Student, Long> functionAge;
    private Function<CollectionQueryBaseTest.Student, String> functionName, functionGroup;
    private CollectionQueryBaseTest.Student student;
    private long maxAge;
    private long minAge;
    private long middleAge;
    private double avgAge;

    @Before
    public void setUp() throws Exception {
        correct = new ArrayList<>();
        emptylist = new ArrayList<>();
        correct.add(new Student("miller", LocalDate.parse("1996-10-25"), "494"));
        correct.add(new Student("ivanov", LocalDate.parse("1997-01-01"), "495"));
        correct.add(new Student("zotov", LocalDate.parse("1997-03-18"), "496"));
        functionAge = Student::age;
        functionName = Student::getName;
        functionGroup = Student::getGroup;
        student = new Student("superman", LocalDate.parse("1970-01-01"), "495");
        maxAge = ChronoUnit.YEARS.between(LocalDate.parse("1996-10-25"), LocalDateTime.now());
        minAge = ChronoUnit.YEARS.between(LocalDate.parse("1997-03-18"), LocalDateTime.now());
        middleAge = ChronoUnit.YEARS.between(LocalDate.parse("1997-01-01"), LocalDateTime.now());
        avgAge = ((double) maxAge + (double) middleAge + (double) minAge) / 3;
    }

    @Test
    public void testMax() throws Exception {
        assertEquals(((Aggregator) Aggregates.max(functionGroup)).apply(correct), "496");
        assertEquals(((Aggregator) Aggregates.max(functionName)).apply(correct), "zotov");
        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(correct), maxAge);

        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(student), student.age());
        assertEquals(((Aggregator) Aggregates.max(functionAge)).apply(emptylist), null);
    }

    @Test
    public void testMin() throws Exception {
        assertEquals(((Aggregator) Aggregates.min(functionGroup)).apply(correct), "494");
        assertEquals(((Aggregator) Aggregates.min(functionName)).apply(correct), "ivanov");
        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(correct), minAge);

        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(student), student.age());
        assertEquals(((Aggregator) Aggregates.min(functionAge)).apply(emptylist), null);
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(((Aggregator) Aggregates.count(functionName)).apply(correct), 3L);
        assertEquals(((Aggregator) Aggregates.count(functionAge)).apply(emptylist), 0L);
    }

    @Test
    public void testAvg() throws Exception {
        assertEquals(((Aggregator) Aggregates.avg(functionAge)).apply(correct), avgAge);

        assertEquals(((Aggregator) Aggregates.avg(functionAge)).apply(student), new Double(student.age()));
    }
}
