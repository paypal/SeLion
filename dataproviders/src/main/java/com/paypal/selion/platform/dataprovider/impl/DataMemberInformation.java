/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.platform.dataprovider.impl;

import java.lang.reflect.Field;

/**
 * A Simple POJO class that represents some basic information with respect to a data member. This is internally used by
 * {@link AbstractExcelDataProvider#prepareObject(Object, Field[], java.util.List)}.
 * 
 */
class DataMemberInformation {
    private final Field field;
    private final Object userProvidedObject;
    private final Object objectToSetDataInto;
    private final String dataToUse;

    public DataMemberInformation(Field eachField, Object userObj, Object objectToReturn, String data) {
        this.field = eachField;
        this.userProvidedObject = userObj;
        this.objectToSetDataInto = objectToReturn;
        this.dataToUse = data;
    }

    /**
     * @return the field
     */
    public Field getField() {
        return field;
    }

    /**
     * @return the userProvidedObject
     */
    public Object getUserProvidedObject() {
        return userProvidedObject;
    }

    /**
     * @return the objectToSetDataInto
     */
    public Object getObjectToSetDataInto() {
        return objectToSetDataInto;
    }

    /**
     * @return the dataToUse
     */
    public String getDataToUse() {
        return dataToUse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataMemberInformation [");
        if (field != null) {
            builder.append("field=");
            builder.append(field);
            builder.append(", ");
        }
        if (userProvidedObject != null) {
            builder.append("userProvidedObject=");
            builder.append(userProvidedObject);
            builder.append(", ");
        }
        if (objectToSetDataInto != null) {
            builder.append("objectToSetDataInto=");
            builder.append(objectToSetDataInto);
            builder.append(", ");
        }
        if (dataToUse != null) {
            builder.append("dataToUse=");
            builder.append(dataToUse);
        }
        builder.append("]");
        return builder.toString();
    }

}
