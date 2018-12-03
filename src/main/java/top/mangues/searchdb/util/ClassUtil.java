package top.mangues.searchdb.util;

public class ClassUtil {
    public static Boolean isBasic(Class clazz) {
//        if (cl instanceof Integer
//                || cl instanceof String
//                || cl instanceof Long
//                || cl instanceof BigDecimal
//                || cl instanceof Character
//                || cl instanceof Byte
//                || cl instanceof Float
//                || cl instanceof Short
//                || cl instanceof Double) {
//            return true;
//        }else {
//            return false;
//        }
        return clazz.isPrimitive() || clazz == String.class;
    }
}
