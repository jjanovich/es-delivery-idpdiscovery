package com.oktaice.idpdiscovery.controller;

import com.oktaice.idpdiscovery.config.IdPConfiguration;
import com.oktaice.idpdiscovery.service.IdPConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IdPDiscoveryController {

    private IdPConfigurationService idPConfigurationService;

    public IdPDiscoveryController(IdPConfigurationService idPConfigurationService) {
        this.idPConfigurationService = idPConfigurationService;
    }

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
        switch (idPConfigurationService.getStrategy()) {
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
        switch (idPConfigurationService.getStrategy()) {
            case "MAIL":
                idp = idPConfigurationService.getProviderByMail(info);
                break;
            case "COMPANY":
                idp = idPConfigurationService.getProviderByCompany(info);
                break;
            case "NETWORK":
                idp = idPConfigurationService.getProviderByNetwork(request);
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
}
