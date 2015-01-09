package de.uni_stuttgart.riot.server.commons.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class for building SQL Queries.
 *
 */
public final class SQLQueryUtil {

    private SQLQueryUtil() {
    }

    /**
     * Help method to build the INSERT Statement. Uses all attributes of the Class, except the <code>id</code> attribute.<br>
     * TODO: Caching for statements
     * 
     * @param clazz
     *            the Class modeling the table.
     * @param tableName
     *            the table name
     * @return statement in format: INSERT INTO -tableName- (name1, name2, nameX) VALUES (:name1, :name2, :nameX)
     */
    public static String buildInsertStatement(Class<?> clazz, String tableName) {
        List<String> nameList = new ArrayList<String>();
        List<String> valuesList = new ArrayList<String>();

        for (Field field : getAllFields(clazz, new LinkedList<Field>())) {
            nameList.add(field.getName());
            valuesList.add(":" + field.getName());
        }
        String names = StringUtils.join(nameList, ", ");
        String values = StringUtils.join(valuesList, ", ");
        return "INSERT INTO " + tableName + "(" + names + ") VALUES (" + values + ")";
    }

    /**
     * Help method to build the UPDATE Statement.
     * 
     * @param clazz
     *            the Class modeling the table.
     * @param tableName
     *            the table name
     * @return statement in format: UPDATE -tableName- SET name1 = :name1, name2 = :name2, nameX = nameX WHERE id = :id
     */
    public static String buildUpdateStatement(Class<?> clazz, String tableName) {
        List<String> nameValueList = new ArrayList<String>();

        for (Field field : getAllFields(clazz, new LinkedList<Field>())) {
            nameValueList.add(field.getName() + " = :" + field.getName());
        }

        String nameValues = StringUtils.join(nameValueList, ", ");
        return "UPDATE " + tableName + " SET " + nameValues + " WHERE id = :id";
    }

    /**
     * Builds getById statement.
     * 
     * @param tableName
     *            the table name
     * @return statement
     */
    public static String buildGetById(String tableName) {
        return "SELECT * FROM " + tableName + " WHERE id = :id";
    }

    /**
     * Builds delete statement.
     * 
     * @param tableName
     *            the table name
     * @return statement
     */
    public static String buildDelete(String tableName) {
        return "DELETE FROM " + tableName + " WHERE id = :id";
    }

    /**
     * Builds getAll statement.
     * 
     * @param tableName
     *            the table name
     * @return statement
     */
    public static String buildGetAll(String tableName) {
        return "SELECT * FROM " + tableName;
    }

    /**
     * Builds getAll statement using LIMIT clause.
     * 
     * @param tableName
     *            the table name
     * 
     * @return statement
     */
    public static String buildFindWithPagination(String tableName) {
        return "SELECT * FROM " + tableName + " LIMIT :limit OFFSET :offset";
    }

    /**
     * Builds getById statement.
     * 
     * @param params
     *            the search parameters.
     * 
     * @param or
     *            true if search shall use OR.
     * @param tableName
     *            the table name
     * @return statement
     */
    public static String buildFindByParam(Collection<SearchParameter> params, boolean or, String tableName) {
        return "SELECT * FROM " + tableName + " " + SQLQueryUtil.getWherePart(params, or);
    }

    /**
     * Builds WHERE Clause.
     * 
     * @param params
     *            search parameters
     * @param or
     *            true if search shall use OR
     * @return
     */
    private static String getWherePart(Collection<SearchParameter> params, boolean or) {
        StringBuilder res = new StringBuilder();
        res.append("WHERE ");
        Iterator<SearchParameter> i = params.iterator();
        int x = 0;
        while (i.hasNext()) {
            SearchParameter current = i.next();
            res.append(current.getValueName());
            res.append(" = :" + current.getValueName() + x);
            x = x + 1;
            if (i.hasNext()) {
                if (or) {
                    res.append(" OR ");
                } else {
                    res.append(" AND ");
                }
            }
        }
        return res.toString();
    }

    /**
     * Retrieves Field objects reflecting all the fields declared by the Class object including inherited fields.<br>
     * It excludes "id", static and transient fields.
     * 
     * @param clazz
     *            Class object to retrieve fields
     * @param current
     *            currently retrieved Fields, used recursively
     * @return Collection of Field Objects.
     */
    public static Collection<Field> getAllFields(Class<?> clazz, Collection<Field> current) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!"id".equalsIgnoreCase(field.getName()) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                current.add(field);
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            getAllFields(superClazz, current);
        }
        return current;
    }
}
