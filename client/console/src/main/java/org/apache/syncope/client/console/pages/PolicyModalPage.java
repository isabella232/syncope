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
package org.apache.syncope.client.console.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.syncope.client.console.commons.Constants;
import org.apache.syncope.client.console.panels.NotificationPanel;
import org.apache.syncope.client.console.panels.PolicyBeanPanel;
import org.apache.syncope.client.console.rest.PolicyRestClient;
import org.apache.syncope.client.console.wicket.markup.html.form.ActionLink;
import org.apache.syncope.client.console.wicket.markup.html.form.ActionLinksPanel;
import org.apache.syncope.client.console.wicket.markup.html.form.AjaxDropDownChoicePanel;
import org.apache.syncope.client.console.wicket.markup.html.form.AjaxPalettePanel;
import org.apache.syncope.client.console.wicket.markup.html.form.AjaxTextFieldPanel;
import org.apache.syncope.common.lib.to.AbstractPolicyTO;
import org.apache.syncope.common.lib.to.AccountPolicyTO;
import org.apache.syncope.common.lib.to.PasswordPolicyTO;
import org.apache.syncope.common.lib.to.ResourceTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.SyncPolicyTO;
import org.apache.syncope.common.lib.types.AccountPolicySpec;
import org.apache.syncope.common.lib.types.PasswordPolicySpec;
import org.apache.syncope.common.lib.types.PolicySpec;
import org.apache.syncope.common.lib.types.PolicyType;
import org.apache.syncope.common.lib.types.SyncPolicySpec;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Modal window with Resource form.
 */
public class PolicyModalPage<T extends AbstractPolicyTO> extends BaseModalPage {

    private static final long serialVersionUID = -7325772767481076679L;

    private static final int WIN_HEIGHT = 600;

    private static final int WIN_WIDTH = 1100;

    @SpringBean
    private PolicyRestClient policyRestClient;

    public PolicyModalPage(final PageReference pageRef, final ModalWindow window, final T policyTO) {
        super();

        final Form<?> form = new Form<>(FORM);
        form.setOutputMarkupId(true);
        add(form);

        final AjaxTextFieldPanel policyid =
                new AjaxTextFieldPanel("key", "key", new PropertyModel<String>(policyTO, "key"));
        policyid.setEnabled(false);
        policyid.setStyleSheet("ui-widget-content ui-corner-all short_fixedsize");
        form.add(policyid);

        final AjaxTextFieldPanel description = new AjaxTextFieldPanel("description", "description",
                new PropertyModel<String>(policyTO, "description"));
        description.addRequiredLabel();
        description.setStyleSheet("ui-widget-content ui-corner-all medium_dynamicsize");
        form.add(description);

        final AjaxDropDownChoicePanel<PolicyType> type =
                new AjaxDropDownChoicePanel<>("type", "type", new PropertyModel<PolicyType>(policyTO, "type"));
        switch (policyTO.getType()) {
            case GLOBAL_ACCOUNT:
            case ACCOUNT:
                type.setChoices(Arrays.asList(new PolicyType[] { PolicyType.GLOBAL_ACCOUNT, PolicyType.ACCOUNT }));
                break;

            case GLOBAL_PASSWORD:
            case PASSWORD:
                type.setChoices(Arrays.asList(new PolicyType[] { PolicyType.GLOBAL_PASSWORD, PolicyType.PASSWORD }));
                break;

            case GLOBAL_SYNC:
            case SYNC:
                type.setChoices(Arrays.asList(new PolicyType[] { PolicyType.GLOBAL_SYNC, PolicyType.SYNC }));

            default:
        }
        type.setChoiceRenderer(new PolicyTypeRenderer());
        type.addRequiredLabel();
        form.add(type);

        // Authentication resources - only for AccountPolicyTO
        Fragment fragment;
        if (policyTO instanceof AccountPolicyTO) {
            fragment = new Fragment("forAccountOnly", "authResourcesFragment", form);

            final List<String> resourceNames = new ArrayList<>();
            for (ResourceTO resource : resourceRestClient.getAll()) {
                resourceNames.add(resource.getKey());
            }
            fragment.add(new AjaxPalettePanel<>("authResources",
                    new PropertyModel<List<String>>(policyTO, "resources"),
                    new ListModel<>(resourceNames)));
        } else {
            fragment = new Fragment("forAccountOnly", "emptyFragment", form);
        }
        form.add(fragment);
        //

        final PolicySpec policy = getPolicySpecification(policyTO);

        form.add(new PolicyBeanPanel("panel", policy));

        final ModalWindow mwindow = new ModalWindow("metaEditModalWin");
        mwindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        mwindow.setInitialHeight(WIN_HEIGHT);
        mwindow.setInitialWidth(WIN_WIDTH);
        mwindow.setCookieName("meta-edit-modal");
        add(mwindow);

        List<IColumn<String, String>> resColumns = new ArrayList<>();
        resColumns.add(new AbstractColumn<String, String>(new StringResourceModel("name", this, null, "")) {

            private static final long serialVersionUID = 2054811145491901166L;

            @Override
            public void populateItem(final Item<ICellPopulator<String>> cellItem,
                    final String componentId, final IModel<String> rowModel) {

                cellItem.add(new Label(componentId, rowModel.getObject()));
            }
        });
        resColumns.add(new AbstractColumn<String, String>(new StringResourceModel("actions", this, null, "")) {

            private static final long serialVersionUID = 2054811145491901166L;

            @Override
            public String getCssClass() {
                return "action";
            }

            @Override
            public void populateItem(final Item<ICellPopulator<String>> cellItem, final String componentId,
                    final IModel<String> model) {

                final String resource = model.getObject();

                final ActionLinksPanel panel = new ActionLinksPanel(componentId, model, getPageReference());
                panel.add(new ActionLink() {

                    private static final long serialVersionUID = -3722207913631435501L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        mwindow.setPageCreator(new ModalWindow.PageCreator() {

                            private static final long serialVersionUID = -7834632442532690940L;

                            @Override
                            public Page createPage() {
                                return new ResourceModalPage(PolicyModalPage.this.getPageReference(),
                                        mwindow, resourceRestClient.read(resource), false);
                            }
                        });

                        mwindow.show(target);
                    }
                }, ActionLink.ActionType.EDIT, "Resources");

                cellItem.add(panel);
            }
        });
        ISortableDataProvider<String, String> resDataProvider = new SortableDataProvider<String, String>() {

            private static final long serialVersionUID = 8263758912838836438L;

            @Override
            public Iterator<? extends String> iterator(final long first, final long count) {
                return policyTO.getKey() == 0
                        ? Collections.<String>emptyList().iterator()
                        : policyRestClient.getPolicy(policyTO.getKey()).
                        getUsedByResources().subList((int) first, (int) first + (int) count).iterator();
            }

            @Override
            public long size() {
                return policyTO.getKey() == 0
                        ? 0
                        : policyRestClient.getPolicy(policyTO.getKey()).
                        getUsedByResources().size();
            }

            @Override
            public IModel<String> model(final String object) {
                return new Model<>(object);
            }
        };
        final AjaxFallbackDefaultDataTable<String, String> resources =
                new AjaxFallbackDefaultDataTable<>("resources", resColumns, resDataProvider, 10);
        form.add(resources);

        List<IColumn<GroupTO, String>> groupColumns = new ArrayList<>();
        groupColumns.add(new PropertyColumn<GroupTO, String>(new ResourceModel("key", "key"), "key", "key"));
        groupColumns.add(new PropertyColumn<GroupTO, String>(new ResourceModel("name", "name"), "name", "name"));
        groupColumns.add(new AbstractColumn<GroupTO, String>(new StringResourceModel("actions", this, null, "")) {

            private static final long serialVersionUID = 2054811145491901166L;

            @Override
            public String getCssClass() {
                return "action";
            }

            @Override
            public void populateItem(final Item<ICellPopulator<GroupTO>> cellItem, final String componentId,
                    final IModel<GroupTO> model) {

                final GroupTO group = model.getObject();

                final ActionLinksPanel panel = new ActionLinksPanel(componentId, model, getPageReference());
                panel.add(new ActionLink() {

                    private static final long serialVersionUID = -3722207913631435501L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        mwindow.setPageCreator(new ModalWindow.PageCreator() {

                            private static final long serialVersionUID = -7834632442532690940L;

                            @Override
                            public Page createPage() {
                                return new GroupModalPage(PolicyModalPage.this.getPageReference(), mwindow, group);
                            }
                        });

                        mwindow.show(target);
                    }
                }, ActionLink.ActionType.EDIT, "Groups");

                cellItem.add(panel);
            }
        });
        ISortableDataProvider<GroupTO, String> groupDataProvider = new SortableDataProvider<GroupTO, String>() {

            private static final long serialVersionUID = 8263758912838836438L;

            @Override
            public Iterator<? extends GroupTO> iterator(final long first, final long count) {
                List<GroupTO> groups = new ArrayList<>();

                if (policyTO.getKey() > 0) {
                    for (Long groupId : policyRestClient.getPolicy(policyTO.getKey()).getUsedByGroups().
                            subList((int) first, (int) first + (int) count)) {

                        groups.add(groupRestClient.read(groupId));
                    }
                }

                return groups.iterator();
            }

            @Override
            public long size() {
                return policyTO.getKey() == 0
                        ? 0
                        : policyRestClient.getPolicy(policyTO.getKey()).getUsedByGroups().size();
            }

            @Override
            public IModel<GroupTO> model(final GroupTO object) {
                return new Model<>(object);
            }
        };
        final AjaxFallbackDefaultDataTable<GroupTO, String> groups =
                new AjaxFallbackDefaultDataTable<>("groups", groupColumns, groupDataProvider, 10);
        form.add(groups);

        mwindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {

            private static final long serialVersionUID = 8804221891699487139L;

            @Override
            public void onClose(final AjaxRequestTarget target) {
                target.add(resources);
                target.add(groups);
                if (isModalResult()) {
                    info(getString(Constants.OPERATION_SUCCEEDED));
                    feedbackPanel.refresh(target);
                    setModalResult(false);
                }
            }
        });

        final AjaxButton submit = new IndicatingAjaxButton(APPLY, new ResourceModel(APPLY)) {

            private static final long serialVersionUID = -958724007591692537L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                setPolicySpecification(policyTO, policy);

                try {
                    if (policyTO.getKey() > 0) {
                        policyRestClient.updatePolicy(policyTO);
                    } else {
                        policyRestClient.createPolicy(policyTO);
                    }
                    ((BasePage) pageRef.getPage()).setModalResult(true);

                    window.close(target);
                } catch (Exception e) {
                    LOG.error("While creating policy", e);

                    error(getString(Constants.ERROR) + ": " + e.getMessage());
                    ((NotificationPanel) getPage().get(Constants.FEEDBACK)).refresh(target);
                }
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                ((NotificationPanel) getPage().get(Constants.FEEDBACK)).refresh(target);
            }
        };
        form.add(submit);

        final IndicatingAjaxButton cancel = new IndicatingAjaxButton(CANCEL, new ResourceModel(CANCEL)) {

            private static final long serialVersionUID = -958724007591692537L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                window.close(target);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
            }
        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);
    }

    private PolicySpec getPolicySpecification(final AbstractPolicyTO policyTO) {
        PolicySpec spec;

        switch (policyTO.getType()) {
            case GLOBAL_ACCOUNT:
            case ACCOUNT:
                spec = ((AccountPolicyTO) policyTO).getSpecification() != null
                        ? ((AccountPolicyTO) policyTO).getSpecification()
                        : new AccountPolicySpec();
                break;

            case GLOBAL_PASSWORD:
            case PASSWORD:
                spec = ((PasswordPolicyTO) policyTO).getSpecification() != null
                        ? ((PasswordPolicyTO) policyTO).getSpecification()
                        : new PasswordPolicySpec();
                break;

            case GLOBAL_SYNC:
            case SYNC:
            default:
                spec = ((SyncPolicyTO) policyTO).getSpecification() != null
                        ? ((SyncPolicyTO) policyTO).getSpecification()
                        : new SyncPolicySpec();
        }

        return spec;
    }

    private void setPolicySpecification(final AbstractPolicyTO policyTO, final PolicySpec specification) {
        switch (policyTO.getType()) {
            case GLOBAL_ACCOUNT:
            case ACCOUNT:
                if (!(specification instanceof AccountPolicySpec)) {
                    throw new ClassCastException("policy is type Account, but spec is not: "
                            + specification.getClass().getName());
                }
                ((AccountPolicyTO) policyTO).setSpecification((AccountPolicySpec) specification);
                break;

            case GLOBAL_PASSWORD:
            case PASSWORD:
                if (!(specification instanceof PasswordPolicySpec)) {
                    throw new ClassCastException("policy is type Password, but spec is not: "
                            + specification.getClass().getName());
                }
                ((PasswordPolicyTO) policyTO).setSpecification((PasswordPolicySpec) specification);
                break;

            case GLOBAL_SYNC:
            case SYNC:
                if (!(specification instanceof SyncPolicySpec)) {
                    throw new ClassCastException("policy is type Sync, but spec is not: "
                            + specification.getClass().getName());
                }
                ((SyncPolicyTO) policyTO).setSpecification((SyncPolicySpec) specification);

            default:
        }
    }

    private class PolicyTypeRenderer extends ChoiceRenderer<PolicyType> {

        private static final long serialVersionUID = -8993265421104002134L;

        @Override
        public Object getDisplayValue(final PolicyType object) {
            return getString(object.name());
        }
    };
}
