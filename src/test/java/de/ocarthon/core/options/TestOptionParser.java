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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestOptionParser {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testOptionParserConstructor() {
        new OptionParser();
    }

    @Test
    public void testOptionParser1() throws Exception {
        String[] parts = parts("--bool --file test.txt --float 1.0 --int 123 --long 532 -s \"test test \" -s2 \"test\"");
        OptionParser.parse(TestOptionClass.class, parts);
    }

    @Test
    public void testOptionParserBoolean() throws Exception {
        String[] parts = parts("--bool");
        TestOptionClass toc = OptionParser.parse(TestOptionClass.class, parts);
        Assert.assertTrue(toc.testBoolean);
        Assert.assertFalse(toc.testBoolean2);
    }

    @Test
    public void testOptionParserNumberInvalidValue() throws Exception {
        String[] parts = parts("--float test");

        exception.expect(OptionParseException.class);
        exception.expectMessage("Error parsing option --float : INVALID_VALUE");
        OptionParser.parse(TestOptionClass.class, parts);
    }

    @Test
    public void testOptionParserNumberNotInRange() throws Exception {
        String[] parts = parts("--float 0");

        exception.expect(OptionParseException.class);
        exception.expectMessage("Error parsing option --float : NOT_IN_RANGE");
        OptionParser.parse(TestOptionClass.class, parts);
    }

    @Test
    public void testOptionParserIllegalAccess() throws Exception {
        String[] parts = parts("--illegalTest 1");

        exception.expect(OptionParseException.class);
        exception.expectMessage("Error parsing option --illegalTest : ILLEGAL_ACCESS");
        OptionParser.parse(TestOptionClass.class, parts);
    }

    @Test
    public void testOptionParserException() throws Exception {
        try {
            String[] parts = parts("--float 0");
            OptionParser.parse(TestOptionClass.class, parts);
        } catch (OptionParseException e) {
            Assert.assertEquals(e.getType(), OptionParseException.Type.NOT_IN_RANGE);
            Assert.assertEquals(e.getLongName(), "float");
            Assert.assertTrue(e.getShortName().isEmpty());
        }
    }

    private String[] parts(String string) {
        return string.split(" ");
    }
}
