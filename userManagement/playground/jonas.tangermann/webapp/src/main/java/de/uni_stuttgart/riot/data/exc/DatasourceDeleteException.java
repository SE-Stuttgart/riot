package de.uni_stuttgart.riot.data.exc;

public class DatasourceDeleteException extends DatasourceException {
	
	public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = "Object does not exist in datasource";

	
	public DatasourceDeleteException(String massage) {
		super(massage);
	}

}
