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

public class OptionParseException extends IllegalArgumentException {
    private Type type;
    private String longName;
    private String shortName;

    public OptionParseException(Type type, String longName, String shortName) {
        super("Error parsing option --" + longName + (shortName != null &&
                !shortName.isEmpty() ? " / -" + shortName : "") + " : " + type.name());
        this.type = type;
        this.longName = longName;
        this.shortName = shortName;
    }

    public Type getType() {
        return type;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public enum Type {
        INVALID_VALUE,
        NOT_IN_RANGE,
        ILLEGAL_ACCESS
    }
}
