package org.jboss.errai.bus.client.types.handlers;

import org.jboss.errai.bus.client.types.TypeHandler;

import java.util.Collection;

public class CollectionToDoubleArray implements TypeHandler<Collection, Integer[]> {
    public Integer[] getConverted(Collection in) {
        if (in == null) return null;
        Integer[] newArray = new Integer[in.size()];

        int i = 0;
        for (Object o : in) {
           newArray[i++] = ((Double)o).intValue();
        }

        return newArray;
    }
}