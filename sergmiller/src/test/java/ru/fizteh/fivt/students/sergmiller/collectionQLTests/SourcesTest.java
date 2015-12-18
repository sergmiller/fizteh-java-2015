package ru.fizteh.fivt.students.sergmiller.collectionQLTests;

/**
 * Created by sergmiller on 18.12.15.
 */

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import ru.fizteh.fivt.students.sergmiller.collectionquery.Sources;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student;
import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryBaseTest.Student.student;

@RunWith(MockitoJUnitRunner.class)
public class SourcesTest extends TestCase {

    @Test
    public void testList() throws Exception {
        List<Student> correct = new ArrayList<>();
        correct.add(student("minkin", LocalDate.parse("2012-12-17"), "494"));
        correct.add(student("ivanov", LocalDate.parse("2011-12-17"), "495"));
        correct.add(student("stepanov", LocalDate.parse("2010-12-17"), "495"));

        List<Student> result = Sources.list(
                student("minkin", LocalDate.parse("2012-12-17"), "494"),
                student("ivanov", LocalDate.parse("2011-12-17"), "495"),
                student("stepanov", LocalDate.parse("2010-12-17"), "495"));
        assertEquals(correct.size(), result.size());

        assertEquals(correct, result);
    }
}