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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

/**
 * Adapter that allows a Hibernate event listener to call a standard JPA
 * EntityListener.
 * 
 * For simplicity only a single bean of each class is supported. It is not
 * difficult to support multiple beans, just messy.
 * 
 * Each listener can have multiple methods with the same annotation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@Component
public class HibernateEntityListenersAdapter implements PostInsertEventListener, PreInsertEventListener,
        PreUpdateEventListener, PostUpdateEventListener, PreDeleteEventListener, PostDeleteEventListener,
        PostLoadEventListener {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(HibernateEntityListenersAdapter.class);

    @Resource
    private List<Object> listeners;

    @Resource
    private EntityManagerFactory emf;

    private Map<Class, Map<Method, Object>> preInsert = new LinkedHashMap<Class, Map<Method, Object>>();
    private Map<Class, Map<Method, Object>> postInsert = new LinkedHashMap<Class, Map<Method, Object>>();
    private Map<Class, Map<Method, Object>> preUpdate = new LinkedHashMap<Class, Map<Method, Object>>();
    private Map<Class, Map<Method, Object>> postUpdate = new LinkedHashMap<Class, Map<Method, Object>>();
    private Map<Class, Map<Method, Object>> preRemove = new LinkedHashMap<Class, Map<Method, Object>>();
    private Map<Class, Map<Method, Object>> postRemove = new LinkedHashMap<Class, Map<Method, Object>>();
    private Map<Class, Map<Method, Object>> postLoad = new LinkedHashMap<Class, Map<Method, Object>>();

    private EventListenerRegistry registry;

    /**
     * Default constructor.
     */
    public HibernateEntityListenersAdapter() {

    }

    /**
     * Constructor taking arguments
     */
    public HibernateEntityListenersAdapter(List<Object> listeners, EntityManagerFactory emf) {
        this.listeners = listeners;
        this.emf = emf;
        findMethods();
    }

    @PostConstruct
    public void findMethods() {
        for (Object listener : listeners) {
            findMethodsForListener(listener);
        }

        HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) emf;
        SessionFactory sf = hemf.getSessionFactory();
        registry = ((SessionFactoryImpl) sf).getServiceRegistry().getService(EventListenerRegistry.class);
    }

    public void findMethodsForListener(Object listener) {
        Class<?> c = listener.getClass();
        for (Method m : c.getMethods()) {
            if (Void.TYPE.equals(m.getReturnType())) {
                Class<?>[] types = m.getParameterTypes();
                if (types.length == 1) {
                    // check for all annotations now...
                    if (m.getAnnotation(PrePersist.class) != null) {
                        if (!preInsert.containsKey(types[0])) {
                            preInsert.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        preInsert.get(types[0]).put(m, listener);
                    }

                    if (m.getAnnotation(PostPersist.class) != null) {
                        if (!postInsert.containsKey(types[0])) {
                            postInsert.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        postInsert.get(types[0]).put(m, listener);
                    }

                    if (m.getAnnotation(PreUpdate.class) != null) {
                        if (!preUpdate.containsKey(types[0])) {
                            preUpdate.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        preUpdate.get(types[0]).put(m, listener);
                    }

                    if (m.getAnnotation(PostUpdate.class) != null) {
                        if (!postUpdate.containsKey(types[0])) {
                            postUpdate.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        postUpdate.get(types[0]).put(m, listener);
                    }

                    if (m.getAnnotation(PreRemove.class) != null) {
                        if (!preRemove.containsKey(types[0])) {
                            preRemove.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        preRemove.get(types[0]).put(m, listener);
                    }

                    if (m.getAnnotation(PostRemove.class) != null) {
                        if (!postRemove.containsKey(types[0])) {
                            postRemove.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        postRemove.get(types[0]).put(m, listener);
                    }

                    if (m.getAnnotation(PostLoad.class) != null) {
                        if (!postLoad.containsKey(types[0])) {
                            postLoad.put(types[0], new LinkedHashMap<Method, Object>());
                        }
                        postLoad.get(types[0]).put(m, listener);
                    }
                }
            }
        }
    }

    /**
     * Execute the listeners. We need to check the entity's class, parent
     * classes, and interfaces.
     * 
     * @param map
     * @param entity
     */
    private void execute(Map<Class, Map<Method, Object>> map, Object entity) {
        if (entity.getClass().isAnnotationPresent(Entity.class)) {

            // check for hits on this class or its superclasses.
            for (Class c = entity.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                if (map.containsKey(c)) {
                    for (Map.Entry<Method, Object> entry : map.get(c).entrySet()) {
                        try {
                            entry.getKey().invoke(entry.getValue(), entity);
                        } catch (InvocationTargetException e) {
                            // log it
                        } catch (IllegalAccessException e) {
                            // log it
                        }
                    }
                }
            }

            // check for hits on interfaces.
            for (Class c : entity.getClass().getInterfaces()) {
                if (map.containsKey(c)) {
                    for (Map.Entry<Method, Object> entry : map.get(c).entrySet()) {
                        try {
                            entry.getKey().invoke(entry.getValue(), entity);
                        } catch (InvocationTargetException e) {
                            // log it
                        } catch (IllegalAccessException e) {
                            // log it
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.hibernate.event.spi.PostDeleteEventListener#onPostDelete(org.hibernate
     *      .event.spi.PostDeleteEvent)
     */
    @Override
    public void onPostDelete(PostDeleteEvent event) {
        execute(postRemove, event.getEntity());
    }

    /**
     * @see org.hibernate.event.spi.PreDeleteEventListener#onPreDelete(org.hibernate
     *      .event.spi.PreDeleteEvent)
     */
    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        execute(preRemove, event.getEntity());
        return false;
    }

    /**
     * @see org.hibernate.event.spi.PreInsertEventListener#onPreInsert(org.hibernate
     *      .event.spi.PreInsertEvent)
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        execute(preInsert, event.getEntity());
        return false;
    }

    /**
     * @see org.hibernate.event.spi.PostInsertEventListener#onPostInsert(org.hibernate
     *      .event.spi.PostInsertEvent)
     */
    @Override
    public void onPostInsert(PostInsertEvent event) {
        execute(postInsert, event.getEntity());
    }

    /**
     * @see org.hibernate.event.spi.PreUpdateEventListener#onPreUpdate(org.hibernate
     *      .event.spi.PreUpdateEvent)
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        execute(preUpdate, event.getEntity());
        return false;
    }

    /**
     * @see org.hibernate.event.spi.PostUpdateEventListener#onPostUpdate(org.hibernate
     *      .event.spi.PostUpdateEvent)
     */
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        execute(postUpdate, event.getEntity());
    }

    /**
     * @see org.hibernate.event.spi.PostLoadEventListener#onPostLoad(org.hibernate
     *      .event.spi.PostLoadEvent)
     */
    @Override
    public void onPostLoad(PostLoadEvent event) {
        execute(postLoad, event.getEntity());
    }
}
