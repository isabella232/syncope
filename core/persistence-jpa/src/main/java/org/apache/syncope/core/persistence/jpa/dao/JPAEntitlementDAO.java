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
package org.apache.syncope.core.persistence.jpa.dao;

import java.util.List;
import javax.persistence.TypedQuery;
import org.apache.syncope.core.persistence.api.GroupEntitlementUtil;
import org.apache.syncope.core.persistence.api.dao.EntitlementDAO;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.persistence.api.entity.Entitlement;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.jpa.entity.JPAEntitlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JPAEntitlementDAO extends AbstractDAO<Entitlement, String> implements EntitlementDAO {

    @Autowired
    private GroupDAO groupDAO;

    @Override
    public Entitlement find(final String name) {
        return entityManager.find(JPAEntitlement.class, name);
    }

    @Override
    public List<Entitlement> findAll() {
        TypedQuery<Entitlement> query = entityManager.createQuery(
                "SELECT e FROM " + JPAEntitlement.class.getSimpleName() + " e", Entitlement.class);

        return query.getResultList();
    }

    @Override
    public Entitlement save(final Entitlement entitlement) {
        return entityManager.merge(entitlement);
    }

    @Override
    public Entitlement saveGroupEntitlement(final Group group) {
        Entitlement groupEnt = new JPAEntitlement();
        groupEnt.setKey(GroupEntitlementUtil.getEntitlementNameFromGroupKey(group.getKey()));
        groupEnt.setDescription("Entitlement for managing group " + group.getKey());

        return save(groupEnt);
    }

    @Override
    public void delete(final String name) {
        Entitlement entitlement = find(name);
        if (entitlement == null) {
            return;
        }

        delete(entitlement);
    }

    @Override
    public void delete(final Entitlement entitlement) {
        if (entitlement == null) {
            return;
        }

        for (Group group : groupDAO.findByEntitlement(entitlement)) {
            group.removeEntitlement(entitlement);
            groupDAO.save(group);
        }

        entityManager.remove(entitlement);
    }
}
