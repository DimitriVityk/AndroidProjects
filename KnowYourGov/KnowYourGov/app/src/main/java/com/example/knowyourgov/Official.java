package com.example.knowyourgov;

import java.io.Serializable;
import java.util.Objects;

public class Official implements Serializable, Comparable<Official> {
    private String office;
    private String name;
    private String party;
    private String officeAddress;
    private String phoneNumber;
    private String email;
    private String websiteURL;
    private String facebook;
    private String twitter;
    private String youtube;
    private String photoURL;

    public Official(String office, String name, String party, String officeAddress, String phoneNumber, String email, String websiteURL, String facebook, String twitter, String youtube, String photoURL)
    {
        this.office = office;
        this.name = name;
        this.party = party;
        this.officeAddress = officeAddress;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.websiteURL = websiteURL;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
        this.photoURL = photoURL;
    }

    public String getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Official official = (Official) o;
        return office.equals(official.office) &&
                name.equals(official.name) &&
                Objects.equals(party, official.party) &&
                Objects.equals(phoneNumber, official.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(office, name, party, phoneNumber);
    }


    @Override
    public int compareTo(Official o) {
        return 0;
    }
}
