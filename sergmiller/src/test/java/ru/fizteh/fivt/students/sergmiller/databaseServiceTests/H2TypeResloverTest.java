package ru.fizteh.fivt.students.sergmiller.databaseServiceTests;

import junit.framework.TestCase;
import org.junit.Test;
import ru.fizteh.fivt.students.sergmiller.databaseService.H2TypeResolver;

import java.util.Date;

/**
 * Created by sergmiller on 17.12.15.
 */
public class H2TypeResloverTest extends TestCase {
    public class MyTestClass {
    }

    @Test
    public void testClasses() {
        assertEquals(H2TypeResolver.resolveType(Byte.class), "TINYINT");
        assertEquals(H2TypeResolver.resolveType(String.class), "VARCHAR(2000)");
        assertEquals(H2TypeResolver.resolveType(Double.class), "DOUBLE");
        assertEquals(H2TypeResolver.resolveType(Date.class), "DATE");
    }

    @Test
    public void testPrimitives() {
        assertEquals(H2TypeResolver.resolveType(long.class), "BIGINT");
        assertEquals(H2TypeResolver.resolveType(double.class), "DOUBLE");
        assertEquals(H2TypeResolver.resolveType(short.class), "INTEGER");
    }

    @Test
    public void testFail() {
        assertNull(H2TypeResolver.resolveType(MyTestClass.class));
    }
}
