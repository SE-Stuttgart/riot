package de.uni_stuttgart.riot.server.commons.db;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

public class CreateBuilder {
    
    public static String buildCreateStatment(Collection<Class> classes){
        StringBuilder result = new StringBuilder();
        result.append(CreateBuilder.dropTables(classes));
        Iterator<Class> i = classes.iterator();
        while(i.hasNext()) {
            Class currnet = i.next();
            result.append("CREATE TABLE ");
            result.append(currnet.getSimpleName());
            result.append("\n(\n");
            Collection<Field> fields = new LinkedList<Field>();
            fields = SQLQueryUtil.getAllFields(currnet,fields);
            result.append("id SERIAL NOT NULL, \n");
            Iterator<Field> i2 = fields.iterator();
            while(i2.hasNext()){
                Field current = i2.next();
                result.append(current.getName());
                result.append(" ");
                String fieldType = current.getType().getSimpleName();
                if(fieldType.equalsIgnoreCase(String.class.getSimpleName())){
                    result.append("varchar(256) ");
                } else if(fieldType.equalsIgnoreCase(Integer.class.getSimpleName()) || fieldType.equalsIgnoreCase(Long.class.getSimpleName())){
                    result.append("int ");
                } else if(fieldType.equalsIgnoreCase(Boolean.class.getSimpleName())){
                    result.append("boolean ");
                } else if(fieldType.equalsIgnoreCase(Timestamp.class.getSimpleName())){
                    result.append("timestamp ");
                }
                result.append("NOT NULL,\n");
                if(current.getName().endsWith("ID")){
                    String table = current.getName().substring(0, current.getName().length() - 2);
                    result.append("FOREIGN KEY (");
                    result.append(current.getName());
                    result.append(") REFERENCES ");
                    result.append(table);
                    result.append("(id) ON DELETE CASCADE,\n");
                }
            }
            result.append("PRIMARY KEY (id)\n);\n\n");
        }
        return result.toString();
    }

    private static String dropTables(Collection<Class> classes) {
        StringBuilder result = new StringBuilder();
        result.append("DROP TABLE IF EXISTS \n");
        Iterator<Class> i = classes.iterator();
        while(i.hasNext()) {
            Class<Storable> currnet = i.next();
            result.append(currnet.getSimpleName());
            if(i.hasNext()){
                result.append(",\n");
            } else {
                result.append(";\n\n");
            }
        }
        return result.toString();
    }

}
