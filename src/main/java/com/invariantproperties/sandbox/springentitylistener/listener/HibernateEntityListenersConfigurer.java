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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configure Spring-aware entity listeners. This implementation is
 * hibernate-specific.
 * 
 * See: http://deepintojee.wordpress.com/2012/02
 * /05/spring-managed-event-listeners-with-jpa/
 * 
 * Another approach follows, but it doesn't support Spring injection.
 * http://stackoverflow.com/questions/8616146/eventlisteners-using-hibernate
 * -4-0-with-spring-3-1-0-release
 * 
 * @author louis.gueye@gmail.com (see above)
 * @author Bear Giles <bgiles@coyotesong.com>
 */
// @Component
public class HibernateEntityListenersConfigurer {
    private static final Logger log = LoggerFactory.getLogger(HibernateEntityListenersConfigurer.class);

    @Resource
    private EntityManagerFactory emf;

    @Resource
    private HibernateEntityListenersAdapter listener;

    @PostConstruct
    public void registerListeners() {
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) emf;
        SessionFactory sf = hemf.getSessionFactory();
        EventListenerRegistry registry = ((SessionFactoryImpl) sf).getServiceRegistry().getService(
                EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(listener);
        registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(listener);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(listener);
        registry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(listener);
        registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(listener);
        registry.getEventListenerGroup(EventType.POST_COMMIT_DELETE).appendListener(listener);
        registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(listener);
    }
}
