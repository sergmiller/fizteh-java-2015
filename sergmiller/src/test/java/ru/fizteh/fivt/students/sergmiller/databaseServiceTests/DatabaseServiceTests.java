package ru.fizteh.fivt.students.sergmiller.databaseServiceTests;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.databaseService.DatabaseService;
import ru.fizteh.fivt.students.sergmiller.databaseService.H2TypeResolver;

import java.util.*;

/**
 * Created by sergmiller on 16.12.15.
 */
public class DatabaseServiceTests extends TestCase {
    @DatabaseService.Table
    public static class SimpleStudent {
        public SimpleStudent() {
        }

        ;

        public SimpleStudent(String newName, int newAge, Date newBirthDate) {
            userName = newName;
            userAge = newAge;
            userDateOfBirth = newBirthDate;
            dummy = 528491;
        }

        @DatabaseService.Column
        @DatabaseService.PrimaryKey
        public String userName;

        @DatabaseService.Column
        public int userAge;

        @DatabaseService.Column
        public Date userDateOfBirth;

        public int dummy;

        @Override
        public String toString() {
            return "User{" +
                    "name='" + userName + "'" +
                    ", age=" + userAge +
                    ", birthday=" + userDateOfBirth.toString() +
                    '}';
        }
    }

    @Test
    public void testCreation() throws Exception {
        DatabaseService<SimpleStudent> service = new DatabaseService<>(SimpleStudent.class);
        service.dropTable();
        service.createTable();
        service.dropTable();
    }

    @Test
    public void testOperetions() throws Exception {

        DatabaseService<SimpleStudent> service = new DatabaseService<>(SimpleStudent.class);
        service.dropTable();
        service.createTable();
        service.insert(new SimpleStudent("Serg", 19, new Date(1345633237)));
        service.insert(new SimpleStudent("Andrew", 18, new Date(1345332323)));
        service.insert(new SimpleStudent("Daniel", 17, new Date(13323237)));
        service.update(new SimpleStudent("Max", 20, new Date(144255427)));
        service.delete("Max");
        service.delete("Other");
        service.delete("Andrew");

        assertEquals(service.queryById("Andrew"), new LinkedList<>());
        assertEquals(service.queryById(""), new LinkedList<>());
        assertEquals("[User{name='Serg', age=19, birthday=1970-01-16},"
                        + " User{name='Daniel', age=17, birthday=1970-01-01}]",
                service.queryForAll().toString());

        service.dropTable();

    }

    @Test
    public void testInitTable() throws Exception {
        DatabaseService<SimpleStudent> databaseService = new DatabaseService(SimpleStudent.class);
        databaseService.dropTable();
        databaseService.createTable();
        databaseService.insert(new SimpleStudent("Bob", 21, new Date((1345633237))));

        databaseService.insert(new SimpleStudent("Alice", 19, new Date(913456332)));

        List<SimpleStudent> users = databaseService.queryForAll();

        assertEquals("User{name='Bob', age=21, birthday=1970-01-16}", users.get(0).toString());
        assertEquals("User{name='Alice', age=19, birthday=1970-01-11}", users.get(1).toString());
        assertEquals(2, users.size());

        users = databaseService.queryById("Bob");

        assertEquals(1, users.size());
        assertEquals("User{name='Bob', age=21, birthday=1970-01-16}", users.get(0).toString());

        databaseService.delete("Bob");

        users = databaseService.queryForAll();

        assertEquals("User{name='Alice', age=19, birthday=1970-01-11}", users.get(0).toString());
        assertEquals(1, users.size());

        databaseService.insert(new SimpleStudent("Bob", 15, new Date((1345633237))));

        databaseService.update(new SimpleStudent("Bob", 50, new Date((10000))));

        users = databaseService.queryForAll();

        assertEquals("User{name='Alice', age=19, birthday=1970-01-11}", users.get(0).toString());
        assertEquals("User{name='Bob', age=50, birthday=1970-01-01}", users.get(1).toString());
        assertEquals(2, users.size());

        databaseService.dropTable();
        databaseService.createTable();

        databaseService.insert(new SimpleStudent("Serg", 19, new Date(1345633237)));
        users = databaseService.queryForAll();

        assertEquals("User{name='Serg', age=19, birthday=1970-01-16}", users.get(0).toString());
        assertEquals(1, users.size());

        databaseService.dropTable();
    }
}
