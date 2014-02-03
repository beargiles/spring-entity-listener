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

import static com.invariantproperties.sandbox.springentitylistener.matcher.UserEquality.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.springentitylistener.domain.TwitterUser;
import com.invariantproperties.sandbox.springentitylistener.listener.ApplicationContext;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContext.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class UserServiceIntegrationTest {
    private static final Logger log = Logger.getLogger(UserServiceIntegrationTest.class.getName());

    @Resource
    private UserService dao;

    @Test
    public void testUserLifecycle() throws Exception {
        final String name = "Alice";
        final String emailAddress = "alice@example.com";
        final String password = "password";

        final TwitterUser expected = new TwitterUser();
        expected.setName(name);
        expected.setEmailAddress(emailAddress);
        expected.setPassword(password);

        assertNull(expected.getId());

        // create user
        TwitterUser actual = dao.createUser(name, emailAddress, password);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());

        // get user by id
        actual = dao.getUserById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get user by uuid
        actual = dao.getUserByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // update user
        expected.setName("Bob");
        expected.setEmailAddress("bob@example.com");
        expected.setPassword("new password");
        actual = dao.updateUser(actual, expected.getName(), expected.getEmailAddress(), expected.getPassword());
        assertThat(expected, equalTo(actual));

        // delete user
        dao.deleteUser(expected.getUuid());
        log.info("getUserByUuid(" + expected.getUuid() + ") is expected to fail.");
        actual = dao.getUserByUuid(expected.getUuid());
        assertNull(actual);
    }

    /**
     * @test getUserById() with unknown user.
     */
    @Test
    public void testGetUserByIdWhenUserIsNotKnown() {
        final Integer id = 1;
        final TwitterUser user = dao.getUserById(id);
        assertNull(user);
    }

    /**
     * @test getUserByUuid() with unknown user.
     */
    @Test
    public void testGetUserByUuidWhenUserIsNotKnown() {
        final String uuid = "missing";
        final TwitterUser user = dao.getUserByUuid(uuid);
        assertNull(user);
    }

    /**
     * Test updateUser() with unknown user.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateUserWhenUserIsNotFound() {
        final TwitterUser user = new TwitterUser();
        user.setUuid("missing");

        dao.updateUser(user, "Bob", "bob@example.com", "new password");
    }

    /**
     * Test deleteUser() with unknown user.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteUserWhenUserIsNotFound() {
        dao.deleteUser("missing");
    }
}