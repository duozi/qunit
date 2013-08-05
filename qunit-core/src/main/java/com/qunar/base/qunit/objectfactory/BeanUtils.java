package com.qunar.base.qunit.objectfactory;

import com.qunar.base.qunit.model.KeyValueStore;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BeanUtils {
    private final static List<InstanceFactory> REGISTEDFACTORIES = new ArrayList<InstanceFactory>();

    private final static ObjectFactory DEFAULTFACTORY = new ObjectFactory();

    static {
        REGISTEDFACTORIES.add(new IntFactory());
        REGISTEDFACTORIES.add(new LongFactory());
        REGISTEDFACTORIES.add(new DoubleFactory());
        REGISTEDFACTORIES.add(new CharFactory());
        REGISTEDFACTORIES.add(new StringFactory());
        REGISTEDFACTORIES.add(new FloatFactory());
        REGISTEDFACTORIES.add(new ArrayFactory());
        REGISTEDFACTORIES.add(new BooleanFactory());
        REGISTEDFACTORIES.add(new MapFactory());
        REGISTEDFACTORIES.add(new DateTimeFactory());
        REGISTEDFACTORIES.add(new BigIntegerFactory());
        REGISTEDFACTORIES.add(new BigDecimalFactory());
        REGISTEDFACTORIES.add(new EnumFactory());
        REGISTEDFACTORIES.add(new ListFactory());
        REGISTEDFACTORIES.add(new StreamFactory());
    }

    /*
   按顺序匹配参数
    */
    public static Object[] getParameters(List<KeyValueStore> params, Type[] parameterTypes) {
//        if (parameterTypes.length != params.size()) {
//            throw new IllegalArgumentException();
//        }
        Object[] result = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            KeyValueStore keyValuePair = params.get(i);
            result[i] = create(parameterTypes[i], keyValuePair.getValue());
        }
        return result;
    }

    public static <T> T create(Type type, Object value) {
        for (InstanceFactory factory : REGISTEDFACTORIES) {
            if (factory.support(type)) {
                return (T) factory.create(type, value);
            }
        }
        return (T) DEFAULTFACTORY.create(type, value);
    }
}
