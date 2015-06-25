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

import java.io.File;

public class TestOptionClass implements OptionParsable {

    @IntOption(longName = "illegalTest")
    public static final int illegalTestInt = 0;
    @BooleanOption(longName = "bool")
    public boolean testBoolean;
    @BooleanOption(longName = "testBool")
    public boolean testBoolean2;
    @FileOption(longName = "file")
    public File testFile;
    @FloatOption(longName = "float", minValue = 1f)
    public float testFloat;
    @IntOption(longName = "int")
    public int testInt;
    @LongOption(longName = "long")
    public long testLong;
    @StringOption(longName = "string", shortName = "s")
    public String testString;
}
