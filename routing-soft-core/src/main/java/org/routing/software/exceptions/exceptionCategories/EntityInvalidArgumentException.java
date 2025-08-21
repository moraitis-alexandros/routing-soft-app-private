package org.routing.software.exceptions.exceptionCategories;

public class EntityInvalidArgumentException  extends EntityGenericException  {

    private static final String DEFAULT_CODE = "InvalidArgument";

    public EntityInvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
