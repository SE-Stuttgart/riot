package de.uni_stuttgart.riot.server.commons.db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;

/**
 * This class is used to create the sql2o queries for every operation in {@link DAO}. <br>
 * It is used every {@link SqlQueryDAO}
 */
public class QueryBuilderImpl implements QueryBuilder {

    @Override
    public Query buildDelete(String tableName, Storable t, Connection connection) throws SQLException {
        return connection.createQuery(buildDelete(tableName)).addParameter("id", t.getId());
    }

    @Override
    public Query buildDelete(String tableName, long id, Connection connection) throws SQLException {
        return connection.createQuery(buildDelete(tableName)).addParameter("id", id);
    }

    @Override
    public Query buildInsert(String tableName, Storable t, Connection connection) throws SQLException {
        return connection.createQuery(buildInsertStatement(t.getClass(), tableName)).bind(t);
    }

    @Override
    public Query buildUpdate(String tableName, Storable t, Connection connection) throws SQLException {
        return connection.createQuery(buildUpdateStatement(t.getClass(), tableName)).bind(t);
    }

    @Override
    public Query buildFindByID(String tableName, Long id, Connection connection) throws SQLException {
        return connection.createQuery(buildGetById(tableName)).addParameter("id", id);
    }

    @Override
    public Query buildFindBySearchParam(String tableName, Collection<SearchParameter> params, Connection connection, boolean or) throws SQLException {
        Query q = connection.createQuery(buildFindByParam(params, or, tableName));
        int x = 0;
        for (SearchParameter searchParameter : params) {
            q.addParameter(searchParameter.getValueName() + x, searchParameter.getValue());
            x = x + 1;
        }
        return q;
    }

    @Override
    public Query buildFindAll(String tableName, Connection connection) throws SQLException {
        return connection.createQuery(buildGetAll(tableName));
    }

    @Override
    public Query buildFindWithPagination(String tableName, Connection connection, int offset, int limit) throws SQLException {
        return connection.createQuery(buildFindWithPagination(tableName)).addParameter("offset", offset).addParameter("limit", limit);
    }

    @Override
    public Query buildFindWithFiltering(String tableName, Connection connection, FilteredRequest filter, Class<?> clazz) throws SQLException, DatasourceFindException {
        Query q = connection.createQuery(buildFilteringStatement(clazz, filter, tableName));
        int x = 0;
        for (FilterAttribute filterAttribute : filter.getFilterAttributes()) {
            q.addParameter(filterAttribute.getFieldName() + x, filterAttribute.getValue());
            x = x + 1;
        }
        return q;
    }

    @Override
    public Query buildGetTotal(String tableName, Connection connection) throws SQLException {
        return connection.createQuery(buildGetTotal(tableName));
    }

    @Override
    public Query buildTotalFoundElements(String tableName, Connection connection, FilteredRequest filter, Class<?> clazz) throws SQLException, DatasourceFindException {
        Query q = connection.createQuery(buildCountResultStatement(clazz, filter, tableName));
        int x = 0;
        for (FilterAttribute filterAttribute : filter.getFilterAttributes()) {
            q.addParameter(filterAttribute.getFieldName() + x, filterAttribute.getValue());
            x = x + 1;
        }
        return q;
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
    private String buildInsertStatement(Class<?> clazz, String tableName) {
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
    private String buildUpdateStatement(Class<?> clazz, String tableName) {
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
    private String buildGetById(String tableName) {
        return "SELECT * FROM " + tableName + " WHERE id = :id";
    }

    /**
     * Builds delete statement.
     * 
     * @param tableName
     *            the table name
     * @return statement
     */
    private String buildDelete(String tableName) {
        return "DELETE FROM " + tableName + " WHERE id = :id";
    }

    /**
     * Builds getAll statement.
     * 
     * @param tableName
     *            the table name
     * @return statement
     */
    private String buildGetAll(String tableName) {
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
    private String buildFindWithPagination(String tableName) {
        return "SELECT * FROM " + tableName + " LIMIT :limit OFFSET :offset";
    }

    /**
     * Builds the get total statement.
     *
     * @param tableName
     *            the table name
     * @return the string
     */
    private String buildGetTotal(String tableName) {
        return "SELECT COUNT(*) FROM " + tableName;
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
    private String buildFindByParam(Collection<SearchParameter> params, boolean or, String tableName) {
        return "SELECT * FROM " + tableName + " " + getWherePart(params, or);
    }

    /**
     * builds SELECT Statement based on given filter attribute list and table name.
     * 
     * @param clazz
     *            the Class for which the filter should apply.
     * @param filter
     *            object containing Filter Attributes
     * @param tableName
     *            the table name
     * @return statement in format: SELECT * FROM -tableName- WHERE name1 -op- :value1 AND [OR] ... [LIMIT...]
     * @throws DatasourceFindException
     *             if filter contains invalid elements.
     */

    private String buildFilteringStatement(Class<?> clazz, FilteredRequest filter, String tableName) throws DatasourceFindException {
        validateFilter(filter, clazz);
        List<String> sqlFilterList = createSqlFilterList(filter);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + tableName);

        if (filter.getFilterAttributes().size() > 0) {
            sb.append(" WHERE ");
            if (filter.isOrMode()) {
                sb.append(StringUtils.join(sqlFilterList, " OR "));
            } else {
                sb.append(StringUtils.join(sqlFilterList, " AND "));
            }
        }

        if (filter.getLimit() > 0 && filter.getOffset() >= 0) {
            // pagination
            sb.append(" LIMIT " + filter.getOffset() + "," + filter.getLimit());
        }

        return sb.toString();
    }

    private String buildCountResultStatement(Class<?> clazz, FilteredRequest filter, String tableName) throws DatasourceFindException {
        List<String> sqlFilterList = createSqlFilterList(filter);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM " + tableName + " ");

        if (sqlFilterList.size() > 0) {
            sb.append("WHERE ");
            if (filter.isOrMode()) {
                sb.append(StringUtils.join(sqlFilterList, " OR "));
            } else {
                sb.append(StringUtils.join(sqlFilterList, " AND "));
            }
        }
        return sb.toString();
    }

    /**
     * Converts given filter operator to SQL conform operator.
     * 
     * @param filterOperator
     *            supported filter operator. Valid operators are "eq" (equals), "ne" (not equals), "gt" (greater than), "ge" (greater than
     *            or equals), "lt" (lower than), "le" (lower than or equals). See {@link FilterOperator}.
     * @return SQL operator.
     */
    private String getSQLOperator(FilterOperator filterOperator) {
        switch (filterOperator) {
        case EQ:
            return "=";
        case NE:
            return "!=";
        case GT:
            return ">";
        case GE:
            return ">=";
        case LT:
            return "<";
        case LE:
            return "<=";
        case CT:
            return " LIKE "; // needs whitespace for concatenation
        default:
            return "=";
        }
    }

    private List<String> createSqlFilterList(FilteredRequest filter) {
        List<String> sqlFilterList = new ArrayList<String>();
        int x = 0;
        for (FilterAttribute filterAttribute : filter.getFilterAttributes()) {
            sqlFilterList.add(createSQLFilterString(filterAttribute, x));
            x++;
        }
        return sqlFilterList;
    }

    /**
     * 
     * @param filter
     *            the filterattribute
     * @return A filter string in the format
     * 
     *         <pre>
     * fieldname operator :fieldnameN
     * </pre>
     * 
     *         e.g. "id = :id1"
     */
    private String createSQLFilterString(FilterAttribute filter, int fieldNumber) {
        String valueStr = ":" + filter.getFieldName() + fieldNumber;
        if (filter.getOperator().equals(FilterAttribute.FilterOperator.CT)) {
            // the contains operand needs a different format
            valueStr = "%" + valueStr + "%";
        }
        return filter.getFieldName() + getSQLOperator(filter.getOperator()) + valueStr;
    }

    /**
     * Validates each filter attribute if: field exists and value has correct type.
     * 
     * @param clazz
     *            the Class for which the filter should apply.
     * @param filterAttributes
     *            list containing filter attributes to validate
     * @throws DatasourceFindException
     *             if filter contains invalid elements.
     */
    private void validateFilter(FilteredRequest filter, Class<?> clazz) throws DatasourceFindException {
        // checks if offset and limit are correct
        if (filter.getLimit() < 0 || filter.getOffset() < 0) {
            // negative values not allowed
            // FIXME: this should be assured at filterattribute creation time
            throw new DatasourceFindException("Invalid limit | offset values at request");
        }

        // checks if field exists and value has correct type.
        for (FilterAttribute filterAttribute : filter.getFilterAttributes()) {
            // checks filter operator
            if (filterAttribute.getOperator() == null) {
                // FIXME: this should be assured at filterattribute creation time
                throw new DatasourceFindException("Filter operator is null.");
            }

            try {
                // if field in filter doesn't exist, it throws exception
                Field field = getDeclaredField(clazz, filterAttribute.getFieldName());
                Object value = filterAttribute.getValue();

                // validate value according to field type (throws exception if not expected type)
                if (value == null || !(ClassUtils.primitiveToWrapper(field.getType()) == value.getClass() || field.getType().isInstance(value))) {
                    // FIXME: should be an exception which maps to a HTTP400
                    throw new DatasourceFindException("Wrong value type for filter value (" + value + "). Expected type: " + field.getType() + ".");
                }
            } catch (NoSuchFieldException | SecurityException e) {
                // FIXME: should be an exception which maps to a HTTP400
                throw new DatasourceFindException("Filter attribute '" + filterAttribute.getFieldName() + "' doesn't exist.", e);
            }
        }
    }

    private Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        for (Field field : getAllFields(clazz, new ArrayList<Field>())) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        throw new NoSuchFieldException(fieldName + " not found in " + clazz.getName());
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
    private String getWherePart(Collection<SearchParameter> params, boolean or) {
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
    private Collection<Field> getAllFields(Class<?> clazz, Collection<Field> current) {
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
