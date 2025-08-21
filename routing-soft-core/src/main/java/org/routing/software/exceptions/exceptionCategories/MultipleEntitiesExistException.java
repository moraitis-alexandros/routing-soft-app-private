package org.routing.software.exceptions.exceptionCategories;

public class MultipleEntitiesExistException extends EntityGenericException{

    private static final String DEFAULT_CODE = "AlreadyExists";

    public MultipleEntitiesExistException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }

}