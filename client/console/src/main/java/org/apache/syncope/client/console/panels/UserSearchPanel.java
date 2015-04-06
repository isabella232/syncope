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
package org.apache.syncope.client.console.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.syncope.client.console.rest.GroupRestClient;
import org.apache.syncope.client.lib.SyncopeClient;
import org.apache.syncope.common.lib.search.AbstractFiqlSearchConditionBuilder;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.types.AttributableType;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class UserSearchPanel extends AbstractSearchPanel {

    private static final long serialVersionUID = -1769527800450203738L;

    @SpringBean
    private GroupRestClient groupRestClient;

    public static class Builder implements Serializable {

        private static final long serialVersionUID = 6308997285778809578L;

        private String id;

        private String fiql = null;

        private boolean required = true;

        public Builder(final String id) {
            this.id = id;
        }

        public Builder fiql(final String fiql) {
            this.fiql = fiql;
            return this;
        }

        public Builder required(final boolean required) {
            this.required = required;
            return this;
        }

        public UserSearchPanel build() {
            return new UserSearchPanel(this);
        }
    }

    private UserSearchPanel(final Builder builder) {
        super(builder.id, AttributableType.USER, builder.fiql, builder.required);
    }

    @Override
    protected void populate() {
        super.populate();

        this.types = new LoadableDetachableModel<List<SearchClause.Type>>() {

            private static final long serialVersionUID = 5275935387613157437L;

            @Override
            protected List<SearchClause.Type> load() {
                List<SearchClause.Type> result = new ArrayList<SearchClause.Type>();
                result.add(SearchClause.Type.ATTRIBUTE);
                result.add(SearchClause.Type.MEMBERSHIP);
                result.add(SearchClause.Type.RESOURCE);
                return result;
            }
        };

        this.groupNames = new LoadableDetachableModel<List<String>>() {

            private static final long serialVersionUID = 5275935387613157437L;

            @Override
            protected List<String> load() {
                List<GroupTO> groupTOs = groupRestClient.list();

                List<String> result = new ArrayList<>(groupTOs.size());
                for (GroupTO group : groupTOs) {
                    result.add(group.getDisplayName());
                }

                return result;
            }
        };
    }

    @Override
    protected AbstractFiqlSearchConditionBuilder getSearchConditionBuilder() {
        return SyncopeClient.getUserSearchConditionBuilder();
    }

}
