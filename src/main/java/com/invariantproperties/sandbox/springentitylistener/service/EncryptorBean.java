/*
 * This code was written by Bear Giles <bgiles@coyotesong.com> and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.sandbox.springentitylistener.service;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 *
 */
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.owasp.esapi.codecs.Base64;
import org.springframework.stereotype.Component;

/**
 * Encryptor bean.
 * 
 * If you get a
 * "Could not initialize class org.owasp.esapi.reference.crypto.JavaEncryptor"
 * you may need to set the Encryptor.MasterKey and Encryptor.MasterSalt values
 * in ESAPI.properties.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Component
public class EncryptorBean {
    private static final String PBE_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String ALGORITHM = "AES";

    // hardcoded for demonstration use. See
    // http://invariantproperties.com/2013/09/29/creating-password-based-encryption-keys/
    // for alternatives
    private static final String SALT = "WR9bdtN3tMHg75PDK9PoIQ==";
    private static final char[] PASSWORD = "password".toCharArray();

    // the key
    private transient SecretKey key;

    static {
        Provider bc = new BouncyCastleProvider();
        if (Security.getProvider(bc.getName()) == null) {
            Security.addProvider(bc);
        }
    }

    /**
     * Constructor creates secret key. In production we may want to avoid
     * keeping the secret key hanging around in memory for very long.
     */
    public EncryptorBean() {
        try {
            // create the PBE key
            KeySpec spec = new PBEKeySpec(PASSWORD, Base64.decode(SALT), 10000, 128);
            key = SecretKeyFactory.getInstance(PBE_ALGORITHM).generateSecret(spec);
        } catch (SecurityException ex) {
            // handle appropriately...
            System.out.println("encryptor bean ctor exception: " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            // handle appropriately...
            System.out.println("encryptor bean ctor exception: " + ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            // handle appropriately...
            System.out.println("encryptor bean ctor exception: " + ex.getMessage());
        }
    }

    /**
     * Decrypt string
     */
    public String decryptString(String ciphertext, String salt) {
        String plaintext = null;

        if (ciphertext != null) {
            try {
                // Encryptor encryptor = JavaEncryptor.getInstance();
                // CipherText ct =
                // CipherText.fromPortableSerializedBytes(Base64.decode(ciphertext));
                // plaintext = encryptor.decrypt(key, ct).toString();
                IvParameterSpec iv = new IvParameterSpec(Base64.decode(salt));
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, iv);

                plaintext = new String(cipher.doFinal(Base64.decode(ciphertext)));
            } catch (NoSuchAlgorithmException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (NoSuchPaddingException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (InvalidKeyException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (BadPaddingException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (IllegalBlockSizeException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }

        return plaintext;
    }

    /**
     * Encrypt string. We should use a container class to contain the ciphertext
     * and key but an array is good enough for testing.
     */
    public String[] encryptString(String plaintext) {
        String ciphertext = null;
        String salt = null;

        if (plaintext != null) {
            try {
                // Encryptor encryptor = JavaEncryptor.getInstance();
                // CipherText ct = encryptor.encrypt(key, new
                // PlainText(plaintext));
                // ciphertext =
                // Base64.encodeBytes(ct.asPortableSerializedByteArray());
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                ciphertext = Base64.encodeBytes(cipher.doFinal(plaintext.getBytes()));
                salt = Base64.encodeBytes(cipher.getIV());
            } catch (NoSuchAlgorithmException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (NoSuchPaddingException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (InvalidKeyException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (BadPaddingException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (IllegalBlockSizeException e) {
                // handle exception. Perhaps set value to null?
                System.out.println("decryption exception: " + e.getMessage());
            } catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }

        return new String[] { ciphertext, salt };
    }
}