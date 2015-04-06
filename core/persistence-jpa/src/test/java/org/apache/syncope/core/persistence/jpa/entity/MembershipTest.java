/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.core.persistence.jpa.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.apache.syncope.core.persistence.api.dao.MembershipDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.membership.Membership;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.persistence.jpa.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MembershipTest extends AbstractTest {

    @Autowired
    private MembershipDAO membershipDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private GroupDAO groupDAO;

    @Test
    public void findAll() {
        List<Membership> list = membershipDAO.findAll();
        assertEquals(7, list.size());
    }

    @Test
    public void find() {
        Membership membership = membershipDAO.find(1L);
        assertNotNull("did not find expected membership", membership);
    }

    @Test
    public void save() {
        User user = userDAO.find(4L);
        Group group = groupDAO.find(1L);

        Membership membership = entityFactory.newEntity(Membership.class);
        membership.setUser(user);
        membership.setGroup(group);

        membership = membershipDAO.save(membership);

        Membership actual = membershipDAO.find(membership.getKey());
        assertNotNull("expected save to work", actual);
    }

    @Test
    public void delete() {
        Membership membership = membershipDAO.find(4L);
        membershipDAO.delete(membership.getKey());

        Membership actual = membershipDAO.find(4L);
        assertNull("delete did not work", actual);
    }
}
