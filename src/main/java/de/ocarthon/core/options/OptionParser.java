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

package de.ocarthon.core.options;

import de.ocarthon.core.options.types.BooleanOption;
import de.ocarthon.core.options.types.FileOption;
import de.ocarthon.core.options.types.FloatOption;
import de.ocarthon.core.options.types.IntOption;
import de.ocarthon.core.options.types.LongOption;
import de.ocarthon.core.options.types.StringOption;
import de.ocarthon.core.utility.reflection.ConstructorUtil;
import de.ocarthon.core.utility.reflection.MethodUtil;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;

public class OptionParser {

    public static <T extends OptionParsable> T parse(Class<T> dataClass, String... options)
            throws OptionParseException {
        HashMap<String, String> optionPairs = new HashMap<>();
        String currentKey = null;
        String currentValue = null;

        for (String option : options) {
            if (option.startsWith("-")) {
                if (currentKey != null) {
                    optionPairs.put(currentKey, currentValue);
                }

                currentKey = option;
            } else if (currentKey != null) {
                if (option.startsWith("\"")) {
                    if (option.endsWith("\"")) {
                        currentValue = option.substring(1, option.length() - 1);
                    } else {
                        currentValue = option.substring(1);
                        continue;
                    }
                } else if (currentValue == null) {
                    currentValue = option;
                } else {
                    int position = option.indexOf("\"");
                    currentValue += " " + (position == -1 ? option : option.substring(0,
                            position));
                }
            }

            if (currentKey != null && currentValue != null) {
                optionPairs.put(currentKey, currentValue);

                currentKey = null;
                currentValue = null;
            }
        }

        if (currentKey != null) {
            optionPairs.put(currentKey, null);
        }

        T instance = ConstructorUtil.invokeConstructor(dataClass, null);

        for (Field field : dataClass.getDeclaredFields()) {
            field.setAccessible(true);

            OptionParseException.Type exceptionType = null;

            Annotation opAnno = null;
            Class<?> annotationType = null;
            String longName = null;
            String shortName = null;

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (MethodUtil.exists(annotation.getClass(), "longName")) {
                    opAnno = annotation;
                    annotationType = annotation.annotationType();
                    longName = (String) MethodUtil.invoke(annotation, "longName", null);
                    shortName = (String) MethodUtil.invoke(annotation, "shortName", null);
                    break;
                }
            }

            if (opAnno == null) continue;


            try {
                if (annotationType == BooleanOption.class) {
                    if (exists(optionPairs, longName, shortName)) {
                        field.setBoolean(instance, true);
                    } else {
                        field.setBoolean(instance, false);
                    }
                } else if (annotationType == FileOption.class) {
                    if (exists(optionPairs, longName, shortName)) {
                        File file = new File(getValue(optionPairs, longName, shortName));
                        field.set(instance, file);
                    }
                } else if (annotationType == StringOption.class) {
                    if (exists(optionPairs, longName, shortName)) {
                        String value = getValue(optionPairs, longName, shortName);
                        if (value != null) {
                            field.set(instance, value);
                        }
                    }
                } else if (annotationType == FloatOption.class
                        || annotationType == IntOption.class
                        || annotationType == LongOption.class) {
                    BigDecimal maxValue = new BigDecimal(MethodUtil.invoke(opAnno, "maxValue", null).toString());
                    BigDecimal minValue = new BigDecimal(MethodUtil.invoke(opAnno, "minValue", null).toString());

                    String sValue = getValue(optionPairs, longName, shortName);
                    if (sValue == null) continue;

                    Number value = null;

                    try {
                        if (annotationType == FloatOption.class) {
                            value = Float.parseFloat(sValue);
                        } else if (annotationType == IntOption.class) {
                            value = Integer.parseInt(sValue);
                        } else if (annotationType == LongOption.class) {
                            value = Long.parseLong(sValue);
                        }
                    } catch (NumberFormatException e) {
                        exceptionType = OptionParseException.Type.INVALID_VALUE;
                    }

                    if (value != null) {
                        BigDecimal bValue = new BigDecimal(value.toString());
                        if (bValue.compareTo(minValue) >= 0 && bValue.compareTo(maxValue) <= 0) {
                            field.set(instance, value);
                        } else {
                            exceptionType = OptionParseException.Type.NOT_IN_RANGE;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                exceptionType = OptionParseException.Type.ILLEGAL_ACCESS;
            }

            if (exceptionType != null) {
                throw new OptionParseException(exceptionType, longName, shortName);
            }

            field.setAccessible(false);
        }

        return instance;
    }

    private static boolean exists(HashMap<String, String> optionPairs, String longName, String shortName) {
        return optionPairs.containsKey("--" + longName) || (shortName != null
                && optionPairs.containsKey("-" + shortName));
    }

    private static String getValue(HashMap<String, String> optionPairs, String longName, String shortName) {
        String value = optionPairs.get("--" + longName);
        if (value == null && shortName != null) value = optionPairs.get("-" + shortName);
        return value;
    }
}
