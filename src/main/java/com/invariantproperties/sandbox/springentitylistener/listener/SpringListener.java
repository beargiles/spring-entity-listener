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
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.springframework.stereotype.Component;

import com.invariantproperties.sandbox.springentitylistener.domain.TwitterUser;

/**
 * Simple Spring EntityListener.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Component
public class SpringListener {
    @Resource(name = "springListenerKey")
    private String key;

    @PrePersist
    public void prePersist(TwitterUser entity) {
        System.out.println("Spring PrePersist, key: " + key);
    }

    @PostPersist
    public void postPersist(TwitterUser entity) {
        System.out.println("Spring PostPersist, key: " + key);
    }

    @PreUpdate
    public void preUpdate(TwitterUser entity) {
        System.out.println("Spring PreUpdate, key: " + key);
    }

    @PostUpdate
    public void postUpdate(TwitterUser entity) {
        System.out.println("Spring PostUpdate, key: " + key);
    }

    @PreRemove
    public void preRemove(TwitterUser entity) {
        System.out.println("Spring PreRemove, key: " + key);
    }

    @PostRemove
    public void postRemove(TwitterUser entity) {
        System.out.println("Spring PostRemove, key: " + key);
    }

    @PostLoad
    public void postLoad(TwitterUser entity) {
        System.out.println("Spring PostLoad, key: " + key);
    }
}
