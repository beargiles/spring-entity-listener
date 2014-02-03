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
package com.invariantproperties.sandbox.springentitylistener.persistence;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class ObjectNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String action;
    private Integer id;
    private String uuid;

    public ObjectNotFoundException(String action, Integer id) {
        this.id = id;
    }

    public ObjectNotFoundException(String action, String uuid) {
        this.uuid = uuid;
    }

    public String getAction() {
        return action;
    }

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return String.format("object %s not found for %s", (id == null) ? id : uuid, action);
    }
}
