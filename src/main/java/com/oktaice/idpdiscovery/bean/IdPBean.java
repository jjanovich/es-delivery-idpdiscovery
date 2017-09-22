package com.oktaice.idpdiscovery.bean;

/**
 * Bean that represents an IdP and its properties
 */
public class IdPBean {

    private String name;
    private String type;
    private String url;
    private String acs;

    //TODO: Implement constructor with a config array from Spring Boot?
    public IdPBean(String name, String type, String url, String acs){
        this.name = name;
        this.type = type;
        this.url = url;
        if(acs == null){
            this.acs = url;
        }else{
            this.acs = acs;
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getAcs() {
        return acs;
    }


}
