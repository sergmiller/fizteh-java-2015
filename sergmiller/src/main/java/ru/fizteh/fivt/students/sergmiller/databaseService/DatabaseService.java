package ru.fizteh.fivt.students.sergmiller.databaseService;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;

/**
 * Created by sergmiller on 15.12.15.
 */


public class DatabaseService<T> implements Cloneable {

    private Class<T> workingClass;
    private Table annotation;
    private String table;
    private Field[] fields;
//    private List<String> columns;
//    private List<Object> values;

    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Table {
        String name();
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface Column {
        String name();
    }

    public DatabaseService(Class<T> workingClass) {
        this.workingClass = workingClass;
        annotation = workingClass.getAnnotation(Table.class);
        table = annotation.name();
        fields = workingClass.getDeclaredFields();
    }

    public final void createTable() {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:/tmp/lesson1")) {

            Statement statement = connection.createStatement();
            StringBuilder creationRequest = new StringBuilder();
            creationRequest.append("CREATE TABLE IF NOT EXISTS " + table + " (");

            int i = 0;

            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    if (i != 0) {
                        creationRequest.append(", ");
                    }
                    creationRequest.append(column.name() + " VARCHAR(255)");
                    ++i;
                }
            }


            creationRequest.append(" )");

            statement.execute(creationRequest.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public final <T> void insert(T note) {
        if (note.getClass() != workingClass) {
            System.err.println("wrong object");
            return;
        }

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();


        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columns.add(column.name());
                    values.add(field.get(note));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection("jdbc:h2:/tmp/lesson1")) {
            String columnsLine = columns.stream().collect(joining(", "));
            String valuesLine = values.stream()
                    .map(o -> "'" + o.toString() + "'")
                    .collect(joining(", "));

            PreparedStatement statement =
                    connection.prepareStatement(
                            "INSERT INTO " + table + " (" + columnsLine + ") "
                                    + "VALUES (" + valuesLine + ")");
            int updatedLines = statement.executeUpdate();

            System.out.println("Updated: " + updatedLines);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public final List<T> queryForAll() {
        List<T> answer = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:h2:/tmp/lesson1")) {
            try (ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + table)) {
                while (rs.next()) {

                    T instance = workingClass.newInstance();
                    //copy-paste
//                    for (int i = 0; i < fields.length; ++i) {
//                        if (fields[i].getClass().isAssignableFrom(Number.class)) {
//                            Long val = rs.getLong(i + 1);
//                            fields[i].set(instance, val);
//                        } else if (fields[i].getType() != String.class) {
//                            fields[i].set(instance, rs.getObject(i + 1));
//                        } else {
//                            Clob instance = rs.getClob(i + 1);
//                            fields[i].set(instance,
//                                    instance.getSubString(1, (int) instance.length()));
//                        }
//                    }
                    for (Field field : fields) {
                        Column column = field.getAnnotation(Column.class);
                        if (column == null) {
                            continue;
                        }
                        field.set(instance, rs.getObject(column.name()));
                    }
                    answer.add(instance);
                }
            }


        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return answer;
    }
}

