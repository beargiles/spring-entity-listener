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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.invariantproperties.sandbox.springentitylistener.annotation.SpringEntityListeners;

/**
 * Automatically configure Spring-aware entity listeners for every @Entity class
 * annotated with the @SpringEntityListener annotation. This implementation is
 * hibernate-specific.
 * 
 * See: http://deepintojee.wordpress.com/2012/02
 * /05/spring-managed-event-listeners-with-jpa/
 * 
 * @author louis.gueye@gmail.com (see above)
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Component
public class SpringEntityListenersConfigurer implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringEntityListenersConfigurer.class);

    private ApplicationContext context;

    @Resource
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void registerListeners() {
        // get registry so we can add listeners.
        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) entityManagerFactory;
        SessionFactory sf = hemf.getSessionFactory();
        EventListenerRegistry registry = ((SessionFactoryImpl) sf).getServiceRegistry().getService(
                EventListenerRegistry.class);

        final Set<Object> listeners = new HashSet<Object>();

        EntityManager entityManager = null;
        try {
            entityManager = hemf.createEntityManager();
            // for every entity known to the system...
            for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {

                // ... register event listeners for it.
                if (entity.getJavaType().isAnnotationPresent(SpringEntityListeners.class)) {
                    SpringEntityListeners annotation = (SpringEntityListeners) entity.getJavaType().getAnnotation(
                            SpringEntityListeners.class);
                    for (Class<?> beanClass : annotation.value()) {
                        Map<String, ?> map = context.getBeansOfType(beanClass);
                        listeners.addAll(map.values());
                    }
                }
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

        // register adapter and listeners.
        HibernateEntityListenersAdapter adapter = new HibernateEntityListenersAdapter(new ArrayList<Object>(listeners),
                entityManagerFactory);
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(adapter);
        registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(adapter);
        registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(adapter);
        registry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(adapter);
        registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(adapter);
        registry.getEventListenerGroup(EventType.POST_COMMIT_DELETE).appendListener(adapter);
        registry.getEventListenerGroup(EventType.POST_LOAD).appendListener(adapter);
    }
}
