package com.oktaice.idpdiscovery.controller;

import com.oktaice.idpdiscovery.config.IdPConfiguration;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IdPDiscovery {

    //Auto-load the IdPConfiguration before IdP Discovery is available
    //This way, we have all configs from application.yml available :D
    @Autowired
    private IdPConfiguration idPConfiguration;

    /**
     * Controller for the index page.
     * Based on what kind of strategy we are using, it will redirect to a different index page
     *
     * @return page that gathers user information for idp discovery
     */
    @RequestMapping("/")
    public String welcome(ModelMap model, HttpServletRequest request) {
        //TODO: test error page with arguments
        String welcomePage = "error?errorMessage=\"Discovery Kind undefined\"";
        switch (idPConfiguration.getStrategy()) {
            case "MAIL":
                welcomePage = "mail";
                break;
            case "COMPANY":
                welcomePage = "company";
                break;
            case "NETWORK":
                //TODO: find a way to hum straight to findIDP
                //this.findIdp(model, "", request);
                break;
        }
        return welcomePage;
    }//welcome

    /**
     * Discovers the correct IdP for Login based on the information collected
     * @param info
     * @return
     */
    @RequestMapping(value = "/idp", method = RequestMethod.POST)
    public String findIdp(ModelMap model, @RequestParam String info, HttpServletRequest request) {
        String destination = null;
        IdPConfiguration.ProviderConfiguration idp = null;

        //Find IDP, based on the strategy selected
        switch (idPConfiguration.getStrategy()) {
            case "MAIL":
                idp = this.getProviderByMail(info);
                break;
            case "COMPANY":
                idp = this.getProviderByCompany(info);
                break;
            case "NETWORK":
                idp = this.getProviderByNetwork(request);
                break;
        }


        //after finding the idp, defines the destination based on the IdP type
        if (idp.getType().equalsIgnoreCase("okta")) {
            //If idp type is okta, compile a custom sign-in widget
            model.put("idp", idp);
            destination = "login";
        } else if (idp.getType().equalsIgnoreCase("adfs")) {
            //If idp type is adfs, redirect to ADFS for an IDP initiated Sign On
            destination = "redirect:" + idp.getUrl() + "?loginToRp=" + idp.getAcs();
        } else if (idp.getType().equalsIgnoreCase("saml")) {
            //If idp type is saml, redirect to the IdP for a SAML standard IdP initiated Sign On
            destination = "redirect:" + idp.getUrl() + "?redirecturl=" + idp.getAcs();
        }
        return destination;
    }//findIdp

    /**
     * Find identity provider for email
     * @param email
     * @return identity provider
     */
    public IdPConfiguration.ProviderConfiguration getProviderByMail(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        IdPConfiguration.ProviderConfiguration userIdP = null;
        for (IdPConfiguration.ProviderConfiguration ip : idPConfiguration.getIdps()) {
            if (ip.getMailDomain().contains(domain)) {
                userIdP = ip;
                break;
            }
        }
        return userIdP;
    }//getProviderByMail

    /**
     * Find identity provider for company
     * @param company
     * @return identity provider
     */
    public IdPConfiguration.ProviderConfiguration getProviderByCompany(String company){
        IdPConfiguration.ProviderConfiguration userIdP = null;
        for (IdPConfiguration.ProviderConfiguration ip : idPConfiguration.getIdps()) {
            if (ip.getName().trim().equalsIgnoreCase(company)) {
                userIdP = ip;
                break;
            }
        }
        return userIdP;
    }//getProviderByCompany

    /**
     * Extract ip from request and them find the identity provider per cidr
     * @param request
     * @return identity provider
     */
    public IdPConfiguration.ProviderConfiguration getProviderByNetwork(HttpServletRequest request){
        String userIp = request.getHeader("X-FORWARDED-FOR");
        if (userIp == null || "".equals(userIp)) {
            userIp = request.getRemoteAddr();
        }
        System.out.println("user ip address: "+userIp);

        IdPConfiguration.ProviderConfiguration userIdP = null;
        for (IdPConfiguration.ProviderConfiguration ip : idPConfiguration.getIdps()) {
            if (new SubnetUtils(ip.getName()).getInfo().isInRange(userIp)) {
                userIdP = ip;
                break;
            }
        }
        return userIdP;
    }//getProviderByNetwork

}
