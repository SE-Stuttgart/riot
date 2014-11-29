package de.uni_stuttgart.riot.data.exc;

public class DatasourceFindException extends DatasourceException {
	public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = "There is no object stored in the datasource assosiated with the given ID or parameter";

	public DatasourceFindException(String massage) {
		super(massage);
	}
}
