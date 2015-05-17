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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A Utility-class providing methods for various hashing
 * algorithms
 */
public class Hash {

    private Hash() {
    }

    /**
     * hashes the input using the given algorithm. It is not possible to revert
     * this process.
     *
     * @param algorithm the name of the algorithm
     * @param input     the bytes that should we hashed
     * @return the hash of the input
     * @throws java.lang.NullPointerException if the algorithm or the
     *                                        input is null
     */
    public static byte[] hash(String algorithm, byte[] input) {
        if (algorithm == null) throw new NullPointerException("algorithm must be set");
        if (input == null) throw new NullPointerException("input must be set");

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(input);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * hashes the input using the MD5-Algorithm. It is not possible to revert
     * this process. The MD5-Algorithm is a standard algorithm and is always
     * implemented
     *
     * @param input the bytes that should we hashed
     * @return the hash of the input
     * @throws java.lang.NullPointerException if the input is null
     */
    public static byte[] md5(byte[] input) {
        return hash("MD5", input);
    }

    /**
     * hashes the input using the SHA1-Algorithm. It is not possible to revert
     * this process. The SHA1-Algorithm is a standard algorithm and is always
     * implemented
     *
     * @param input the bytes that should we hashed
     * @return the hash of the input
     * @throws java.lang.NullPointerException if the input is null
     */
    public static byte[] sha1(byte[] input) {
        return hash("SHA-1", input);
    }

    /**
     * hashes the input using the SHA256-Algorithm. It is not possible to revert
     * this process. The SHA256-Algorithm is a standard algorithm and is always
     * implemented
     *
     * @param input the bytes that should we hashed
     * @return the hash of the input
     * @throws java.lang.NullPointerException if the input is null
     */
    public static byte[] sha256(byte[] input) {
        return hash("SHA-256", input);
    }

    /**
     * hashes the input using the SHA512-Algorithm. It is not possible to revert
     * this process. The SHA512-Algorithm is not a standard algorithm and must
     * not be implemented on every system
     *
     * @param input the bytes that should we hashed
     * @return the hash of the input
     * @throws java.lang.NullPointerException if the input is null
     */
    public static byte[] sha512(byte[] input) {
        return hash("SHA-512", input);
    }
}
