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
package com.invariantproperties.sandbox.springentitylistener.listener;

import javax.annotation.Resource;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.stereotype.Component;

import com.invariantproperties.sandbox.springentitylistener.domain.TwitterUser;
import com.invariantproperties.sandbox.springentitylistener.service.EncryptorBean;

/**
 * Listener that transparently handles password encryption.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Component
public class PasswordListener {

    @Resource
    private EncryptorBean encryptor;

    /**
     * Decrypt password after loading.
     */
    @PostLoad
    @PostUpdate
    public void decryptPassword(TwitterUser user) {
        user.setPassword(null);

        if (user.getEncryptedPassword() != null) {
            user.setPassword(encryptor.decryptString(user.getEncryptedPassword(), user.getSalt()));
        }

        // obviously we would never do this in practice
        System.out.printf("decrypted password '%s'\n", user.getPassword());
    }

    /**
     * Decrypt password before persisting
     */
    @PrePersist
    @PreUpdate
    public void encryptPassword(TwitterUser user) {
        user.setEncryptedPassword(null);
        user.setSalt(null);

        if (user.getPassword() != null) {
            String[] elements = encryptor.encryptString(user.getPassword());
            user.setEncryptedPassword(elements[0]);
            user.setSalt(elements[1]);
        }

        // obviously we would never do this in practice
        System.out.printf("encrypted password '%s' to '%s'\n", user.getPassword(), user.getEncryptedPassword());
    }
}