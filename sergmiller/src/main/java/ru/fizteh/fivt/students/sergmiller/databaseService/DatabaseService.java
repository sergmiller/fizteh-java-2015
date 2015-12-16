package ru.fizteh.fivt.students.sergmiller.databaseService;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.stream.Collectors.joining;

/**
 * Created by sergmiller on 15.12.15.
 */

@SuppressWarnings("Duplicates")
public class DatabaseService<T> {

    private Class<T> workingClass;
    private Table annotation;
    private String table;
    private Field[] fields;

    private static final String DATABASE_COONECTION_PATH = "jdbc:h2:./database/miniorm";


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

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface PrimaryKey {
    }


    public DatabaseService(Class<T> workingClass) {
        this.workingClass = workingClass;
        annotation = workingClass.getAnnotation(Table.class);
        table = annotation.name();
        fields = workingClass.getDeclaredFields();
    }

    public void createTable() throws SQLException {
        StringBuilder headBuilder = new StringBuilder();
        int i = 0;
        for (Field field : fields) {
            if (i == 0) {
                ++i;
            } else {
                headBuilder.append(", ");
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                headBuilder.append(column.name()).append(" ")
                        .append(H2TypeResolver.resolveType(field.getType()));
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    headBuilder.append(" NOT NULL PRIMARY KEY");
                }
            }
        }

        try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS " + table
                    + "(" + headBuilder.toString() + ")");
        }
    }

    public <T> void insert(T entity) throws SQLException, IllegalAccessException {
        if (entity.getClass() != workingClass) {
            System.err.println("wrong object");
            return;
        }

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();


        for (Field field : fields) {
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
                values.add(field.get(entity));
            }
        }

        String columnsLine = columns.stream().collect(joining(", "));
        String valuesLine = values.stream()
                .map(x -> " ? ")
                .collect(joining(", "));

        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("INSERT INTO ")
                .append(table)
                .append(" (").append(columnsLine).append(") ")
                .append("VALUES (").append(valuesLine).append(")");

        try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
            PreparedStatement insertStatement
                    = connection.prepareStatement(requestBuilder.toString());
            for (int i = 0; i < values.size(); ++i) {
                insertStatement.setObject(i + 1, values.get(i));
            }
            insertStatement.execute();
        }
    }

    public List<T> queryForAll() throws SQLException, InstantiationException, IllegalAccessException {
        try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = statement.executeQuery();
            return buildRequetAnswer(rs);
        }
    }

    public void dropTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
            connection.createStatement().execute("DROP TABLE IF EXISTS " + table);
        }
    }

    private <T> List<T> buildRequetAnswer(final ResultSet rs) throws SQLException,
            IllegalAccessException, InstantiationException {
        List<T> answer = new LinkedList<>();
        while (rs.next()) {
            T record = (T) workingClass.newInstance();
            for (Field field : fields) {
                //field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                field.set(record, rs.getObject(column.name()));
            }
            answer.add(record);
        }
        return answer;
    }
}

