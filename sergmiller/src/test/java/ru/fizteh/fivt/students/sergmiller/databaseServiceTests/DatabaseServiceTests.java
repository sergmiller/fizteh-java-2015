package ru.fizteh.fivt.students.sergmiller.databaseServiceTests;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.databaseService.DatabaseService;

import java.util.Iterator;
import java.util.List;

/**
 * Created by sergmiller on 16.12.15.
 */
public class DatabaseServiceTests extends TestCase {
    @DatabaseService.Table(name = "users")
    public static class User {

        @DatabaseService.Column(name = "name")
        String name;

        @DatabaseService.Column(name = "age")
        String age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(String age) {
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
        DatabaseService databaseService = new DatabaseService(User.class);
        databaseService.createTable();
        User user = new User();
        user.setName("Alice");
        user.setAge("19");

        databaseService.insert(user);

        user.setName("Bob");
        user.setAge("21");

        databaseService.insert(user);

        List users = databaseService.queryForAll();

        Iterator<User> it = users.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
