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

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.invariantproperties.sandbox.springentitylistener.domain.TwitterUser;

/**
 * Simple JPA EntityListener.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class JPAListener {
    @PrePersist
    public void prePersist(TwitterUser entity) {
        System.out.println("JPA PrePersist");
    }

    @PostPersist
    public void postPersist(TwitterUser entity) {
        System.out.println("JPA PostPersist");
    }

    @PreUpdate
    public void preUpdate(TwitterUser entity) {
        System.out.println("JPA PreUpdate");
    }

    @PostUpdate
    public void postUpdate(TwitterUser entity) {
        System.out.println("JPA PostUpdate");
    }

    @PreRemove
    public void preRemove(TwitterUser entity) {
        System.out.println("JPA PreRemove");
    }

    @PostRemove
    public void postRemove(TwitterUser entity) {
        System.out.println("JPA PostRemove");
    }

    @PostLoad
    public void postLoad(TwitterUser entity) {
        System.out.println("JPA PostLoad");
    }
}
