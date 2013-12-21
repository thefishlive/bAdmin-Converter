package com.mojang.api.profiles;

public class Profile {
    private String id;
    private String name;

    public Profile(String name, String uuid) {
    	this.name = name;
    	this.id = uuid;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
