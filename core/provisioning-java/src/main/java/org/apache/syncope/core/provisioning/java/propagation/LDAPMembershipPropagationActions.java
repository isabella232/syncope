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
package org.apache.syncope.core.provisioning.java.propagation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.common.lib.types.AttributableType;
import org.apache.syncope.core.persistence.api.dao.UserDAO;
import org.apache.syncope.core.persistence.api.entity.group.Group;
import org.apache.syncope.core.persistence.api.entity.task.PropagationTask;
import org.apache.syncope.core.persistence.api.entity.user.User;
import org.apache.syncope.core.misc.jexl.JexlUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple action for propagating group memberships to LDAP groups, when the same resource is configured for both users
 * and groups.
 *
 * @see org.apache.syncope.core.sync.impl.LDAPMembershipSyncActions
 */
public class LDAPMembershipPropagationActions extends DefaultPropagationActions {

    protected static final Logger LOG = LoggerFactory.getLogger(LDAPMembershipPropagationActions.class);

    @Autowired
    protected UserDAO userDAO;

    /**
     * Allows easy subclassing for the ConnId AD connector bundle.
     *
     * @return the name of the attribute used to keep track of group memberships
     */
    protected String getGroupMembershipAttrName() {
        return "ldapGroups";
    }

    @Transactional(readOnly = true)
    @Override
    public void before(final PropagationTask task, final ConnectorObject beforeObj) {
        super.before(task, beforeObj);

        if (AttributableType.USER == task.getSubjectType() && task.getResource().getGmapping() != null) {
            User user = userDAO.find(task.getSubjectKey());
            if (user != null) {
                List<String> groupAccountLinks = new ArrayList<>();
                for (Group group : user.getGroups()) {
                    if (group.getResourceNames().contains(task.getResource().getKey())
                            && StringUtils.isNotBlank(task.getResource().getGmapping().getAccountLink())) {

                        LOG.debug("Evaluating accountLink for {}", group);

                        final JexlContext jexlContext = new MapContext();
                        JexlUtil.addFieldsToContext(group, jexlContext);
                        JexlUtil.addAttrsToContext(group.getPlainAttrs(), jexlContext);
                        JexlUtil.addDerAttrsToContext(group.getDerAttrs(), group.getPlainAttrs(), jexlContext);

                        final String groupAccountLink =
                                JexlUtil.evaluate(task.getResource().getGmapping().getAccountLink(), jexlContext);
                        LOG.debug("AccountLink for {} is '{}'", group, groupAccountLink);
                        if (StringUtils.isNotBlank(groupAccountLink)) {
                            groupAccountLinks.add(groupAccountLink);
                        }
                    }
                }
                LOG.debug("Group accountLinks to propagate for membership: {}", groupAccountLinks);

                Set<Attribute> attributes = new HashSet<Attribute>(task.getAttributes());

                Set<String> groups = new HashSet<String>(groupAccountLinks);
                Attribute ldapGroups = AttributeUtil.find(getGroupMembershipAttrName(), attributes);

                if (ldapGroups != null) {
                    for (Object obj : ldapGroups.getValue()) {
                        groups.add(obj.toString());
                    }
                }

                attributes.add(AttributeBuilder.build(getGroupMembershipAttrName(), groups));
                task.setAttributes(attributes);
            }
        } else {
            LOG.debug("Not about user, or group mapping missing for resource: not doing anything");
        }
    }
}
