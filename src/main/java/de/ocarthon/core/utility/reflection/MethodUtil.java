/*
 *    Copyright 2015 Ocarthon (Philip Standt)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.ocarthon.core.utility.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodUtil {

    private MethodUtil() {
    }

    /**
     * Invokes a method with the given name on the given object
     * <p>
     * If the given types of the arguments are null, no arguments
     * are used to invoke the method.
     *
     * @param object     the object whose method gets invoked
     * @param methodName the name the method
     * @param argTypes   types of arguments of the method
     * @param args       values of the arguments
     * @return the return value of the method
     * @throws java.lang.NullPointerException              if the object or
     *                                                     the method name is null
     * @throws java.lang.NoSuchMethodException             if the given method
     *                                                     does not exist
     * @throws java.lang.SecurityException                 a security manager is present or the
     *                                                     classloader is different
     * @throws java.lang.IllegalAccessException            if the method is not accessible
     * @throws java.lang.reflect.InvocationTargetException if the underlying
     *                                                     constructor throws an exception.
     */
    public static Object invoke(Object object, String methodName, Class[] argTypes, Object... args) {
        if (object == null) {
            throw new NullPointerException("the given object is null");
        }

        try {
            Class<?> clazz = (object instanceof Class) ? ((Class) object)
                    : object.getClass();

            Method method = clazz.getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);

            return method.invoke(object, args);
        } catch (NoSuchMethodException | InvocationTargetException
                | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Determines if the gives method exists
     *
     * @param clazz          Class
     * @param methodName     Name of the method
     * @param parameterTypes Parametertypes
     * @return whether or not the method exists
     */
    public static boolean exists(Class<?> clazz, String methodName, Class... parameterTypes) {
        if (clazz == null || methodName == null) throw new NullPointerException("class or name of the method is null!");

        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            return method != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
