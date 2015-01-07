package de.uni_stuttgart.riot.server.commons.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
	 * Returns the corresponding wrapper type if it is a primitive type; otherwise returns type itself.
	 * <p>
	 * 
	 * @param type
	 * @return
	 */
	private static Class<?> wrapType(Class<?> type) {
		if (type == boolean.class) {
			return Boolean.class;
		} else if (type == int.class) {
			return Integer.class;
		} else if (type == long.class) {
			return Long.class;
		} else if (type == short.class) {
			return Short.class;
		} else if (type == float.class) {
			return Float.class;
		} else if (type == double.class) {
			return Double.class;
		} else if (type == byte.class) {
			return Byte.class;
		} else if (type == char.class) {
			return Character.class;
		} else {
			return type;
		}
	}



	/**
	 * Help method to build the INSERT Statement. Uses all attributes of the Class, except the <code>id</code> attribute.<br>
	 * TODO: include fields of superclass, discard static and not serializable fields; Caching for statements
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

		for (Field field : clazz.getDeclaredFields()) {
			if (!"id".equalsIgnoreCase(field.getName())) {
				nameList.add(field.getName());
				valuesList.add(":" + field.getName());
			}
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

		for (Field field : clazz.getDeclaredFields()) {
			if (!"id".equalsIgnoreCase(field.getName())) {
				nameValueList.add(field.getName() + " = :" + field.getName());
			}
		}

		String nameValues = StringUtils.join(nameValueList, ", ");
		return "UPDATE " + tableName + " SET " + nameValues + " WHERE id = :id";
	}

	public static String buildGetById(String tableName){
		return "SELECT FROM "+tableName+" WHERE id = :id";
	}
	
	public static String buildDelete(String tableName){
		return "DELETE FROM "+tableName+" WHERE id = :id";
	}

	public static String buildGetAll(String tableName){
		return "SELECT * FROM "+tableName;
	}

	public static String buildFindByParam(Collection<SearchParameter> params,boolean or,String tableName){
		return "SELECT * FROM "+tableName+" "+SQLQueryUtil.getWherePart(params, or);
	}

	private static String getWherePart(Collection<SearchParameter> params, boolean or) {
		StringBuffer res = new StringBuffer();
		res.append("WHERE ");
		Iterator<SearchParameter> i = params.iterator();
		while (i.hasNext()) {
			SearchParameter current = i.next();
			res.append(current.getValueName());
			res.append(" = "+current.getValue()); //FIXME injection
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
}