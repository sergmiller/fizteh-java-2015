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
    private ArrayList<String> fieldsNames;
    private int primaryKeyFieldId;

    private static final String DATABASE_COONECTION_PATH = "jdbc:h2:./database/miniorm";


    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface Table {
        String name() default "";
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface Column {
        String name() default "";
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface PrimaryKey {
    }


    private void validation() throws DatabaseServiceException {
        int primaryFieldsCounter = 0;
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i].getAnnotatedType() == null) {
                continue;
            }

            if (fields[i].isAnnotationPresent(PrimaryKey.class)) {
                if (!fields[i].isAnnotationPresent(Column.class)) {
                    throw new DatabaseServiceException("@PrimaryKey field must have @Column annotation\n");
                }
                ++primaryFieldsCounter;
                primaryKeyFieldId = i;
            }

            if (primaryFieldsCounter > 1) {
                throw new DatabaseServiceException("@PrimaryKey field must be one\n");
            }

            if (H2TypeResolver.resolveType(fields[i].getType()) == null) {
                throw new DatabaseServiceException(fields[i].getType().toString() + "isn't supported in SQL\n");
            }
        }

        if (primaryFieldsCounter == 0) {
            throw new DatabaseServiceException("@PrimaryKey field must exist\n");
        }
    }

    private void buildFieldsNames() {
        fieldsNames = new ArrayList<>();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                fieldsNames.add(null);
            } else {
                if (column.name().length() > 0) {
                    fieldsNames.add(getSnakeCase(field.getName()));
                } else {
                    fieldsNames.add(column.name());
                }
            }
        }
    }

    private String getSnakeCase(final String s) {
        StringBuilder sSnakeCase = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            sSnakeCase.append(s.charAt(i));
            if (i < s.length() - 1 && Character.isUpperCase(s.charAt(i + 1))) {
                sSnakeCase.append(" ");
            }
        }
        return sSnakeCase.toString().toLowerCase();
    }

    public DatabaseService(Class<T> workingClass) throws DatabaseServiceException {
        this.workingClass = workingClass;
        annotation = workingClass.getAnnotation(Table.class);
        if (annotation.name().length() > 0) {
            table = annotation.name();
        } else {
            table = getSnakeCase(workingClass.getSimpleName());
        }
        fields = workingClass.getDeclaredFields();
        validation();
        buildFieldsNames();
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

    public <T> void insert(T entity) throws DatabaseServiceException {
        if (entity.getClass() != workingClass) {
            throw new DatabaseServiceException("wrong object");
        }

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columns.add(column.name());
                    values.add(field.get(entity));
                }
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseServiceException(e.getMessage());
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
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public <T, K> List<T> queryById(K key) throws DatabaseServiceException {
        try {
            String name = fieldsNames.get(primaryKeyFieldId);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT * FROM ")
                    .append(table)
                    .append(" WHERE ")
                    .append(name)
                    .append(" = ?");
            try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
                PreparedStatement statement = connection.prepareStatement(stringBuilder.toString());
                statement.setObject(1, key);
                ResultSet rs = statement.executeQuery();
                return buildRequestAnswer(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public <K> boolean delete(K key) throws DatabaseServiceException {
        try {
            String name = fieldsNames.get(primaryKeyFieldId);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DELETE FROM ")
                    .append(table)
                    .append(" WHERE ")
                    .append(name)
                    .append(" = ?");
            try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
                PreparedStatement statement = connection.prepareStatement(stringBuilder.toString());
                statement.setObject(1, key);
                return (statement.executeUpdate() > 0);
            }
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public <T> void update(T entity) throws DatabaseServiceException {
        try {
            if (delete(fields[primaryKeyFieldId].get(entity))) {
                insert(entity);
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public List<T> queryForAll() throws DatabaseServiceException {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT * FROM ").append(table);
            try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
                PreparedStatement statement = connection.prepareStatement(stringBuilder.toString());
                ResultSet rs = statement.executeQuery();
                return buildRequestAnswer(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }

    public void dropTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_COONECTION_PATH)) {
            connection.createStatement().execute("DROP TABLE IF EXISTS " + table);
        }
    }

    private <T> List<T> buildRequestAnswer(final ResultSet rs) throws DatabaseServiceException {
        try {
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
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            throw new DatabaseServiceException(e.getMessage());
        }
    }
}

