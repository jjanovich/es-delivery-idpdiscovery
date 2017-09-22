package com.oktaice.idpdiscovery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Load IDP Configurations from the application.yml file
 */
@Configuration
@ConfigurationProperties
public class IdPConfiguration {

    private String strategy;
    private String indexLogo;
    private List<ProviderConfiguration> idps;

    //getters and setters
    public String getStrategy() {
        return strategy;
    }//getStrategy

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }//setStrategy

    public String getIndexLogo() {
        return indexLogo;
    }

    public void setIndexLogo(String indexLogo) {
        this.indexLogo = indexLogo;
    }

    public List<ProviderConfiguration> getIdps() {
        return idps;
    }

    public void setIdps(List<ProviderConfiguration> idps) {
        this.idps = idps;
    }

    public static class ProviderConfiguration {


        //idps:
        //  -
        //    name: "Okta Central"
        //    type: "okta"
        //    logo: "central.png"
        //    url: "https://oktacentral.oktapreview.com"
        //    acs:

        private String name;
        private String type;
        private String logo;
        private String url;
        private String acs;
        private List<String> mailDomain;

        public ProviderConfiguration(String name, String type, String logo,
                                     String url, String acs, List<String> mailDomain) {
            this.name = name;
            this.type = type;
            this.logo = logo;
            this.url = url;
            this.acs = acs;
            this.mailDomain = mailDomain;
        }//ProviderConfiguration

        public ProviderConfiguration() {
        }

        // getters, setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAcs() {
            return acs;
        }

        public void setAcs(String acs) {
            this.acs = acs;
        }

        public List<String> getMailDomain() {
            return mailDomain;
        }

        public void setMailDomain(List<String> mailDomain) {
            this.mailDomain = mailDomain;
        }
    }
}
