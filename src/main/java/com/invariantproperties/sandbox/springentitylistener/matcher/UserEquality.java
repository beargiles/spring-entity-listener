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
package com.invariantproperties.sandbox.springentitylistener.matcher;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.invariantproperties.sandbox.springentitylistener.domain.TwitterUser;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 * 
 */
public class UserEquality extends TypeSafeMatcher<TwitterUser> {
    private final TwitterUser expected;

    private UserEquality(TwitterUser user) {
        this.expected = user;
    }

    @Override
    public boolean matchesSafely(TwitterUser actual) {
        if (actual == null) {
            return false;
        }

        EqualsBuilder eq = new EqualsBuilder();
        eq.append(expected.getId(), actual.getId());
        eq.append(expected.getUuid(), actual.getUuid());
        eq.append(expected.getName(), actual.getName());
        eq.append(expected.getEmailAddress(), actual.getEmailAddress());
        eq.append(expected.getPassword(), actual.getPassword());

        return eq.isEquals();
    }

    public void describeTo(Description description) {
        description.appendText("user comparison");
    }

    @Factory
    public static <T> Matcher<TwitterUser> equalTo(TwitterUser user) {
        return new UserEquality(user);
    }
}
