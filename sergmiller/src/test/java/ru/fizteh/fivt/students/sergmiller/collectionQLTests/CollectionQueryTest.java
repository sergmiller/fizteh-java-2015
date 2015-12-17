
package ru.fizteh.fivt.students.sergmiller.collectionQLTests;

import junit.framework.TestCase;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ru.fizteh.fivt.students.sergmiller.collectionquery.Aggregates.avg;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.Aggregates.count;
import static ru.fizteh.fivt.students.sergmiller.collectionQLTests.CollectionQueryTest.Student.student;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.Conditions.rlike;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.OrderByConditions.asc;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.OrderByConditions.desc;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.Sources.list;
import static ru.fizteh.fivt.students.sergmiller.collectionquery.impl.FromStmt.from;


/**
 * Created by sergmiller on 17.12.15.
 */


public class CollectionQueryTest extends TestCase {
    public static class Student {
        private final String name;

        private final LocalDate dateOfBith;

        private final String group;

        public String getName() {
            return name;
        }

        public Student(String name, LocalDate dateOfBith, String group) {
            this.name = name;
            this.dateOfBith = dateOfBith;
            this.group = group;
        }

        public LocalDate getDateOfBith() {
            return dateOfBith;
        }

        public String getGroup() {
            return group;
        }

        public long age() {
            return ChronoUnit.YEARS.between(getDateOfBith(), LocalDateTime.now());
        }

        public static Student student(String name, LocalDate dateOfBith, String group) {
            return new Student(name, dateOfBith, group);
        }
    }

    public static class Group {
        private final String group;
        private final String mentor;

        public Group(String group, String mentor) {
            this.group = group;
            this.mentor = mentor;
        }

        public String getGroup() {
            return group;
        }

        public String getMentor() {
            return mentor;
        }
    }


    public static class Statistics {

        private final String group;
        private final Long count;
        private final Double age;

        public String getGroup() {
            return group;
        }

        public Long getCount() {
            return count;
        }

        public Double getAge() {
            return age;
        }

        public Statistics(String group, Long count, Double age) {
            this.group = group;
            this.count = count;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Statistics{"
                    + "group='" + group + '\''
                    + ", count=" + count
                    + ", age=" + age
                    + '}';
        }
    }

    @Test
    public void testBaseTestCollectionQuery() throws Exception {
        Iterable<Statistics> statistics =
                from(list(
                        student("ivanov", LocalDate.parse("1986-08-06"), "494"),
                        student("sidorov", LocalDate.parse("1986-08-06"), "495"),
                        student("smith", LocalDate.parse("1986-08-06"), "495"),
                        student("petrov", LocalDate.parse("2006-08-06"), "494")))
                        .select(Statistics.class, Student::getGroup, count(Student::getGroup), avg(Student::age))
                        .where(rlike(Student::getName, ".*ov").and(s -> s.age() > 20))
                        .groupBy(Student::getGroup)
                        .having(s -> s.getCount() > 0)
                        .orderBy(asc(Statistics::getGroup), desc(Statistics::getCount))
                        .limit(100)
                        .union()
                        .from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
                        .selectDistinct(Statistics.class, s -> "all", count(s -> 1), avg(Student::age))
                        .execute();
        assertEquals("[Statistics{group='494', count=2, age=29.0},"
                + " Statistics{group='all', count=1, age=30.0}]", statistics.toString());

//        Iterable<Tuple<String, String>> mentorsByStudent =
//                from(list(student("ivanov", LocalDate.parse("1985-08-06"), "494")))
//                        .join(list(new Group("494", "mr.sidorov")))
//                        .on((s, g) -> Objects.equals(s.getGroup(), g.getGroup()))
//                        .select(sg -> sg.getFirst().getName(), sg -> sg.getSecond().getMentor())
//                        .execute();
//        System.out.println(mentorsByStudent);
    }


}