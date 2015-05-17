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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorUtil {

    private ConstructorUtil() {
    }

    /**
     * Invokes a constructor of a given class.
     * <p>
     * If the given types of the arguments are null, no arguments
     * are used to invoke the class.
     *
     * @param clazz    class whose constructor is getting invoked
     * @param argTypes types of arguments of the constructor
     * @param args     values of the arguments
     * @return returns an instance of the given class
     * @throws java.lang.NullPointerException              if clazz is null
     * @throws java.lang.NoSuchMethodException             if the constructor does not exist
     * @throws java.lang.SecurityException                 a security manager is present or the
     *                                                     classloader is different
     * @throws java.lang.IllegalArgumentException          if the number of actual and
     *                                                     formal parameters differ; if an unwrapping conversion for primitive
     *                                                     arguments fails; or if, after possible unwrapping, a parameter value
     *                                                     cannot be converted to the corresponding formal parameter type by a
     *                                                     method invocation conversion; if this constructor pertains to an enum
     *                                                     type.
     * @throws java.lang.IllegalAccessException            if the constructor is not
     *                                                     accessible
     * @throws java.lang.InstantiationException            if the class that declares the
     *                                                     underlying constructor represents an abstract class.
     * @throws java.lang.reflect.InvocationTargetException if the underlying
     *                                                     constructor throws an exception.
     * @throws java.lang.ExceptionInInitializerError       if the initialization
     *                                                     provoked by this method fails.
     */
    public static <T> T invokeConstructor(Class<T> clazz, Class[] argTypes, Object... args) {
        if (clazz == null) throw new NullPointerException("Constructor is null");

        try {
            if (argTypes != null) {
                Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            } else {
                return clazz.newInstance();
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
