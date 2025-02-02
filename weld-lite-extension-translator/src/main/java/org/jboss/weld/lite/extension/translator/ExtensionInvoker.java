package org.jboss.weld.lite.extension.translator;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.SkipIfPortableExtensionPresent;
import jakarta.enterprise.inject.spi.DefinitionException;
import jakarta.enterprise.inject.spi.DeploymentException;
import jakarta.interceptor.Interceptor;

import org.jboss.weld.lite.extension.translator.logging.LiteExtensionTranslatorLogger;

class ExtensionInvoker {

    private static final int DEFAULT_PRIORITY = Interceptor.Priority.APPLICATION + 500;

    private final Map<String, Class<?>> extensionClasses = new HashMap<>();
    private final Map<Class<?>, Object> extensionClassInstances = new HashMap<>();

    // used from WFLY to initiate with already known collection of extensions
    ExtensionInvoker(Collection<Class<? extends BuildCompatibleExtension>> extensions) {
        for (Class<? extends BuildCompatibleExtension> extensionClass : extensions) {
            SkipIfPortableExtensionPresent skip = extensionClass.getAnnotation(SkipIfPortableExtensionPresent.class);
            if (skip != null) {
                // TODO only if the corresponding portable extension exists!
                continue;
            }

            try {
                BuildCompatibleExtension extensionInstance = SecurityActions.getConstructor(extensionClass).newInstance();
                extensionClasses.put(extensionClass.getName(), extensionClass);
                extensionClassInstances.put(extensionClass, extensionInstance);
            } catch (InvocationTargetException e) {
                throw LiteExtensionTranslatorLogger.LOG.unableToInstantiateObject(extensionClass, e.getCause().toString(), e);
            } catch (ReflectiveOperationException e) {
                throw LiteExtensionTranslatorLogger.LOG.unableToInstantiateObject(extensionClass, e.toString(), e);
            }

        }
    }

    List<java.lang.reflect.Method> findExtensionMethods(Class<? extends Annotation> annotation) {
        return extensionClasses.values()
                .stream()
                .flatMap(it -> Arrays.stream(it.getDeclaredMethods()))
                .filter(it -> it.getAnnotation(annotation) != null)
                .sorted((m1, m2) -> {
                    if (m1.equals(m2)) {
                        return 0;
                    }

                    int p1 = getExtensionMethodPriority(m1);
                    int p2 = getExtensionMethodPriority(m2);

                    // must _not_ return 0 if priorities are equal, because that isn't consistent
                    // with the `equals` method
                    return p1 < p2 ? -1 : 1;
                })
                .collect(Collectors.toList());
    }

    private int getExtensionMethodPriority(java.lang.reflect.Method method) {
        Priority priority = method.getAnnotation(Priority.class);
        if (priority != null) {
            return priority.value();
        }
        return DEFAULT_PRIORITY;
    }

    void callExtensionMethod(java.lang.reflect.Method method, List<Object> arguments) throws ReflectiveOperationException {
        Class<?> extensionClass = extensionClasses.get(method.getDeclaringClass().getName());
        Object extensionClassInstance = extensionClassInstances.get(extensionClass);

        try {
            method.invoke(extensionClassInstance, arguments.toArray());
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof DefinitionException) {
                throw (DefinitionException) e.getCause();
            } else if (e.getCause() instanceof DeploymentException) {
                throw (DeploymentException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    void clear() {
        extensionClasses.clear();
        extensionClassInstances.clear();
    }
}
