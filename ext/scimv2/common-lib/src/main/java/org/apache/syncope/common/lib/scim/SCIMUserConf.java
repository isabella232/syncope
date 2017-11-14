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
package org.apache.syncope.common.lib.scim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.syncope.common.lib.scim.types.EmailCanonicalType;
import org.apache.syncope.common.lib.scim.types.IMCanonicalType;
import org.apache.syncope.common.lib.scim.types.PhoneNumberCanonicalType;
import org.apache.syncope.common.lib.scim.types.PhotoCanonicalType;

public class SCIMUserConf implements Serializable {

    private static final long serialVersionUID = -2700011089067219156L;

    private SCIMUserNameConf name;

    private String displayName;

    private String nickName;

    private String profileUrl;

    private String title;

    private String userType;

    private String preferredLanguage;

    private String locale;

    private String timezone;

    private final List<SCIMComplexConf<EmailCanonicalType>> emails = new ArrayList<>();

    private final List<SCIMComplexConf<PhoneNumberCanonicalType>> phoneNumbers = new ArrayList<>();

    private final List<SCIMComplexConf<IMCanonicalType>> ims = new ArrayList<>();

    private final List<SCIMComplexConf<PhotoCanonicalType>> photos = new ArrayList<>();

    private final List<SCIMUserAddressConf> addresses = new ArrayList<>();

    private final List<String> x509Certificates = new ArrayList<>();

    public SCIMUserNameConf getName() {
        return name;
    }

    public void setName(final SCIMUserNameConf name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(final String userType) {
        this.userType = userType;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(final String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public List<SCIMComplexConf<EmailCanonicalType>> getEmails() {
        return emails;
    }

    public List<SCIMComplexConf<PhoneNumberCanonicalType>> getPhoneNumbers() {
        return phoneNumbers;
    }

    public List<SCIMComplexConf<IMCanonicalType>> getIms() {
        return ims;
    }

    public List<SCIMComplexConf<PhotoCanonicalType>> getPhotos() {
        return photos;
    }

    public List<SCIMUserAddressConf> getAddresses() {
        return addresses;
    }

    public List<String> getX509Certificates() {
        return x509Certificates;
    }

}
