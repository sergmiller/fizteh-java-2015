package ru.fizteh.fivt.students.sergmiller.databaseServiceTests;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.databaseService.DatabaseService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sergmiller on 16.12.15.
 */
public class DatabaseServiceTests extends TestCase {
    @DatabaseService.Table(name = "users")
    public static class User {
        public User() {
        }

        ;
        @DatabaseService.Column(name = "name")
        @DatabaseService.PrimaryKey
        public String name;

        @DatabaseService.Column(name = "age")
        public int age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    @Test
    public void testInitTable() throws Exception {
        DatabaseService<User> databaseService = new DatabaseService(User.class);
        databaseService.dropTable();
        databaseService.createTable();
        User user1 = new User();
        user1.setName("Bob");
        user1.setAge(21);

        databaseService.insert(user1);

        User user2 = new User();
        user2.setName("Alice");
        user2.setAge(19);

        databaseService.insert(user2);

        List<User> users = databaseService.queryForAll();

        assertEquals("User{name='Bob', age=21}", users.get(0).toString());
        assertEquals("User{name='Alice', age=19}", users.get(1).toString());
        assertEquals(2, users.size());

        List<User> request = databaseService.queryById("Bob");

        assertEquals(1, request.size());
        assertEquals("User{name='Bob', age=21}", request.get(0).toString());

        databaseService.delete("Bob");

        users = databaseService.queryForAll();

        assertEquals("User{name='Alice', age=19}", users.get(0).toString());
        assertEquals(1, users.size());

        databaseService.insert(user1);

        User newUser1 = new User();
        newUser1.setName("Bob");
        newUser1.setAge(35);

        databaseService.update(newUser1);

        users = databaseService.queryForAll();

        assertEquals("User{name='Alice', age=19}", users.get(0).toString());
        assertEquals("User{name='Bob', age=35}", users.get(1).toString());
        assertEquals(2, users.size());

        databaseService.dropTable();
        databaseService.createTable();

        User user3 = new User();
        user3.setName("Serg");
        user3.setAge(19);

        databaseService.insert(user3);

        users = databaseService.queryForAll();

        assertEquals("User{name='Serg', age=19}", users.get(0).toString());
        assertEquals(1, users.size());

        databaseService.dropTable();
    }
}
