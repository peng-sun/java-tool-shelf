package javatoolshelf.config;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassMap {

    public static final List<Class<?>> comparableClassList = ImmutableList.of(
            Byte.class,
            Integer.class,
            Long.class,
            Double.class,
            Character.class,
            Float.class,
            Boolean.class,
            String.class,
            File.class
    );

    class Item {
        public final String path;
        public final Object value;

        public Item(String path, Object value) {
            this.path = path;
            this.value = value;
        }
    }

    private final Object original;
    private final List<String> ignoredMethods;

    private Map<String, Object> map = new HashMap<>();

    public ClassMap(Object obj) {
        this(obj, new ArrayList<String>());
    }

    public ClassMap(Object obj,
                    List<String> ignoredMethods) {
        this.original = obj;
        this.ignoredMethods = ignoredMethods;

        populateMap();
    }

    private void populateMap() {
        List<Item> queue = new ArrayList<>();

        queue.add(new Item("root", original));
        while (!queue.isEmpty()) {
            Item item = queue.get(0);
            Object obj = item.value;
            String path = item.path;
            queue.remove(0);
            if (obj == null || isComparable(obj)) {
                map.put(path, obj);
                continue;
            }

            // handle list and array
            if (obj instanceof Collection) {
                obj = ((Collection<?>) obj).toArray();
            }
            if (obj.getClass().isArray()) {
                int size = Array.getLength(obj);
                for (int i = 0; i < size; i++) {
                    queue.add(new Item(
                            path + "." + StringUtils.removeEndIgnoreCase(obj.getClass().getSimpleName(),"[]") + "[" + i + "]",
                            Array.get(obj, i)));
                }
                continue;
            }


            List<Method> methods = getGetMethodsWithNoArgs(obj);

            for (Method method : methods) {

                try {
                    method.setAccessible(true);
                    Object o = method.invoke(obj);
                    queue.add(new Item(path + "." + method.getName(), o));
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    ignored.printStackTrace();
                }

            }
        }
    }

    private List<Method> getGetMethodsWithNoArgs(Object obj) {
        List<Method> methods = Arrays.asList(obj.getClass().getMethods());
        List<Method> getMethonds = methods.stream()
                .filter(m -> StringUtils.startsWith(m.getName(), "get") && m.getParameterCount() == 0 && !isIgnoredMethod(m))
                .collect(Collectors.toList());

        return getMethonds;
    }

    private boolean isIgnoredMethod(Method method) {
        String modifiers = Modifier.toString(method.getModifiers());
        if (modifiers.contains("static") || modifiers.contains("native")) {
            return true;
        }

        for (String ignored : ignoredMethods) {
            if (method.toString().contains(ignored)) {
                return true;
            }
        }

        return false;
    }

    private boolean isComparable(Object obj) {
        Class clazz = obj.getClass();
        if (comparableClassList.contains(clazz) || clazz.isPrimitive() || clazz.isEnum()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String path = entry.getKey();
            Object value = entry.getValue();
            sb.append(path);
            sb.append(": ");
            sb.append(isComparable(value) ? value.toString() : value.hashCode());
            sb.append("\n");
        }

        return new String(sb);
    }

}
