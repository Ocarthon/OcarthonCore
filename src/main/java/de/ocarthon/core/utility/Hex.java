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

package de.ocarthon.core.utility;

public class Hex {
    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    /**
     * converts the given bytes into a hex string. The characters from a to f
     * are lowercase.
     *
     * @param bytes input bytes
     * @return hex string
     */
    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, false);
    }

    /**
     * converts the given bytes into a hex string.
     *
     * @param bytes       input bytes
     * @param toUpperCase uppercase or lowercase characters
     * @return hex string
     */
    public static String toHexString(byte[] bytes, boolean toUpperCase) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String hex = new String(hexChars);
        return (toUpperCase ? hex.toUpperCase() : hex);
    }

    /**
     * converts a hex string back to a byte array
     *
     * @param input hex string
     * @return byte array
     */
    public static byte[] fromHexString(String input) {
        final int length = input.length();

        if (length % 2 > 0) throw new IllegalArgumentException("input needs to be even: " + input);

        byte[] bytes = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            int h = hexToBin(input.charAt(i));
            int l = hexToBin(input.charAt(i + 1));
            if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character: " + input);
            bytes[i / 2] = (byte) (h * 16 + l);
        }

        return bytes;
    }

    /**
     * converts an integer into a hexadecimal string
     *
     * @param in Integer
     * @return Hexadecimal String
     */
    public static String toHex(int in) {
        return Integer.toHexString(in);
    }

    /**
     * converts a single character to a byte
     *
     * @param ch character
     * @return byte representing the character
     */
    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }
}
