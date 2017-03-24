package com.rayxxzhang.simplespring.core;

import com.rayxxzhang.simplespring.core.annotations.*;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Ray on 3/23.
 */
public class AnnotationConfigApplicationContext implements ApplicationContext {
    private final Object config;
    private Map<String, Class<?>> lazyNameComponents;
    private Set<Class<?>> lazyClassComponents;
    private Map<String, BeanInfo> lazyNameBeans;
    private Map<Class<?>, BeanInfo> lazyClassBeans;
    private Map<Class<?>, Object> classComponents;
    private Map<String, Object> nameComponents;

    public AnnotationConfigApplicationContext(Class<?> clazz) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        this.config = clazz.newInstance();
        lazyNameComponents = new HashMap<>();
        lazyClassComponents = new HashSet<>();
        lazyClassBeans = new HashMap<>();
        lazyNameBeans = new HashMap<>();
        classComponents = new HashMap<>();
        nameComponents = new HashMap<>();

        ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
        if(componentScan == null) {
            throw new IllegalArgumentException("Annotations @Configuration not found");
        }
        // Scan bean
        Method[] methods = clazz.getMethods();
        for(Method method: methods) {
            Bean bean = method.getAnnotation(Bean.class);
            if(bean != null) {
                BeanInfo beanInfo;
                String name = bean.name();
                if (!name.isEmpty()) {
                    beanInfo = new BeanInfo(config, method, name);
                    lazyNameBeans.put(name, beanInfo);
                } else {
                    beanInfo = new BeanInfo(config, method);
                }
                lazyClassBeans.put(method.getReturnType(), beanInfo);
            }
        }
        String[] packageNames;
        if(componentScan.basePackages().length != 0) {
            packageNames = componentScan.basePackages();
        } else if(componentScan.basePackageClasses().length != 0) {
            Set<String> pNameSet = new HashSet<>();
            for(Class<?> c: componentScan.basePackageClasses()) {
                pNameSet.add(c.getPackage().getName());
            }
            packageNames = pNameSet.toArray(new String[0]);
        } else {
            packageNames = new String[] {clazz.getPackage().getName()};
        }
        for(String packageItem : packageNames) {
            getClasses(clazz, packageItem).forEach(cls -> {
                Component component = cls.getAnnotation(Component.class);
                if(component != null) {
                    String componentName = component.name();
                    if (!componentName.isEmpty()) {
                        lazyNameComponents.put(componentName, cls);
                    }
                    lazyClassComponents.add(cls);
                }
            });
        }
        // Create beans
        Set<Map.Entry<Class<?>, BeanInfo>> bEntries = lazyClassBeans.entrySet();
        for(Map.Entry<Class<?>, BeanInfo> entry: bEntries) {
            Object o = entry.getValue().getConfig();
            Method method = entry.getValue().getMethod();
            String name = entry.getValue().getName();
            if (method.getAnnotation(Lazy.class) == null || !method.getAnnotation(Lazy.class).value()) {
                Class<?>[] params = method.getParameterTypes();
                Object[] args = new Object[method.getParameterCount()];
                for (int i = 0; i < params.length; ++i) {
                    args[i] = getBean(params[i]);
                }
                Object obj = method.invoke(o, args);
                if (name != null && !name.isEmpty()) {
                    nameComponents.put(name, obj);
                }
                classComponents.put(method.getReturnType(), obj);
            }
        }
        // Create components
        for (Class<?> cls : lazyClassComponents) {
            if (cls.getAnnotation(Lazy.class) == null || !cls.getAnnotation(Lazy.class).value()) {
                Object obj = getBean(cls);
                classComponents.put(cls, obj);
            }
        }
    }

    private static List<Class<?>> getClasses(Class<?> clazz, String packageItem) throws IOException, ClassNotFoundException {
        String basePath;
        File baseFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());
        if(baseFile.isDirectory()) {
            basePath = baseFile.getAbsolutePath() + File.separator;
        } else {
            basePath = baseFile.getName();
        }
        final StandardJavaFileManager fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);
        return StreamSupport.stream(
                fileManager.list(StandardLocation.CLASS_PATH, packageItem, Collections.singleton(JavaFileObject.Kind.CLASS), true)
                        .spliterator(), false)
                            .map(javaFileObject -> {
                                try {

                                    final String className = javaFileObject.getName()
                                            .replace(basePath, "")
                                            .replace(".class", "")
                                            .replace(")", "")
                                            .replace("(", "")
                                            .replace(File.separator, ".")
                                            .replace("/", ".");
                                    return Class.forName(className);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                            })
                            .collect(Collectors.toCollection(ArrayList::new));
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> cls) {
        T obj = (T) classComponents.get(cls);
        if(obj != null) {
            return obj;
        }
        try {
            if(lazyClassComponents.contains(cls)) {
                for(Constructor<?> constructor :cls.getConstructors()) {
                    Autowired autowired = constructor.getAnnotation(Autowired.class);
                    if(autowired != null) {
                        // Autowired in constructor
                        Class<?>[] params = constructor.getParameterTypes();
                        Object[] args = new Object[constructor.getParameterCount()];
                        for (int i = 0; i < params.length; ++i) {
                            args[i] = getBean(params[i]);
                        }
                        obj = (T) constructor.newInstance(args);
                        break;
                    }
                }
                if(obj == null) {
                    // Use default constructor
                    obj = cls.newInstance();
                }
                classComponents.put(cls, obj);
                // Scan dependencies
                for (Field f : cls.getDeclaredFields()) {
                    if (f.getAnnotation(Autowired.class) != null) {
                        Class<?> c = f.getType();
                        if (lazyClassComponents.contains(c)) {
                            f.set(obj, getBean(c));
                        }
                    }
                }
                for (Method m : cls.getDeclaredMethods()) {
                    Class<?>[] params = m.getParameterTypes();
                    Object[] args = new Object[m.getParameterCount()];
                    for (int i = 0; i < params.length; ++i) {
                        args[i] = getBean(params[i]);
                    }
                    m.invoke(obj, args);
                }
            } else if (lazyClassBeans.containsKey(cls)) {
                BeanInfo beanInfo = lazyClassBeans.get(cls);
                Object o = beanInfo.getConfig();
                Method method = beanInfo.getMethod();
                String name = beanInfo.getName();
                Class<?>[] params = method.getParameterTypes();
                Object[] args = new Object[method.getParameterCount()];
                for (int i = 0; i < params.length; ++i) {
                    args[i] = getBean(params[i]);
                }
                obj = (T) method.invoke(o, args);
                classComponents.put(cls, obj);
                if (name != null && !name.isEmpty()) {
                    nameComponents.put(name, obj);
                }
                classComponents.put(method.getReturnType(), obj);
            }
            return obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
