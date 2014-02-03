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
package com.invariantproperties.sandbox.springentitylistener.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.invariantproperties.sandbox.springentitylistener.annotation.SpringEntityListeners;
import com.invariantproperties.sandbox.springentitylistener.listener.JPAListener;
import com.invariantproperties.sandbox.springentitylistener.listener.PasswordListener;
import com.invariantproperties.sandbox.springentitylistener.listener.SpringListener;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Entity
@Table(name = "twitter_user")
@SecondaryTable(name = "twitter_pw", pkJoinColumns = @PrimaryKeyJoinColumn(name = "twitter_user_id"))
@EntityListeners(JPAListener.class)
@SpringEntityListeners({ SpringListener.class, PasswordListener.class })
public class TwitterUser {
    private Integer id;
    private String uuid;
    private String name;
    private String emailAddress;
    private String encryptedPassword;
    private String salt;
    transient private String password;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(length = 40)
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "password", table = "twitter_pw", length = 80)
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @Column(name = "salt", table = "twitter_pw", length = 40)
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Transient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @PrePersist
    public void prepersist() {
        if (getUuid() == null) {
            setUuid(UUID.randomUUID().toString());
        }
    }
}
