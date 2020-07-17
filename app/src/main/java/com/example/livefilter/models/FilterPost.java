package com.example.livefilter.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Filter")
@Parcel(analyze={FilterPost.class})
public class FilterPost extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_NAME = "filterName";
    public static final String KEY_BEFORE = "beforeImage";
    public static final String KEY_AFTER = "afterImage";

    public FilterPost() {
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getBefore() {
        return getParseFile(KEY_BEFORE);
    }

    public void setBefore(ParseFile parseFile) {
        put(KEY_BEFORE, parseFile);
    }

    public ParseFile getAfter() {
        return getParseFile(KEY_AFTER);
    }

    public void setAfter(ParseFile parseFile) {
        put(KEY_AFTER, parseFile);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER, parseUser);
    }

}
