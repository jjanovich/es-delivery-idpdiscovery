package com.oktaice.idpdiscovery.controller;

import com.oktaice.idpdiscovery.config.IdPConfiguration;
import com.oktaice.idpdiscovery.service.IdPConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.http.HTTPException;
import java.security.InvalidParameterException;

@Controller
public class IdPDiscoveryController {

    private IdPConfigurationService idPConfigurationService;

    public IdPDiscoveryController(IdPConfigurationService idPConfigurationService) {
        this.idPConfigurationService = idPConfigurationService;
    }

    /**
     * Controller for the index page. This is where the app starts working
     * Figures out what discovery strategy is being used and redirect user to the proper page
     * @return page to capture information for idp discovery
     */
    @RequestMapping("/")
    public String welcome(ModelMap model, HttpServletRequest request) {
        String welcomePage = null;
        //CHECK WHAT IDP STRATEGY I'M USING
        switch (idPConfigurationService.getStrategy()) {
            case "USERID": //REDIRECT TO THE USERID CAPTURE FORM
                welcomePage = "userid";
                break;
            case "COMPANY": //REDIRECT TO THE COMPANY CAPTURE FORM
                welcomePage = "company";
                model.put("idpConfig",idPConfigurationService);
                break;
            case "NETWORK": //REDIRECT TO THE IP ADDRESS CAPTURE METHOD
                welcomePage = this.findIdp(model, "", request);
                break;
            default:
                throw new InvalidParameterException("Discovery kind undefined:"+ idPConfigurationService.getStrategy());
        }
        //GO TO IDP DISCOVERY
        return welcomePage;
    }//welcome

    /**
     * Discovers the IdP based on the information collected
     * @param info
     * @return
     */
    @RequestMapping(value = "/idp", method = RequestMethod.POST)
    public String findIdp(ModelMap model, @RequestParam String info, HttpServletRequest request) {
        String destination = null;
        IdPConfiguration.ProviderConfiguration idp = null;

        //FIGURES OUT THE USER IDP
        switch (idPConfigurationService.getStrategy()) {
            case "USERID": //GET PROVIDER BY USERID
                idp = idPConfigurationService.getProviderByUserId(info);
                model.put("username",info);
                break;
            case "COMPANY": //GET PROVIDER BY COMPANY NAME
                idp = idPConfigurationService.getProviderByCompany(info);
                break;
            case "NETWORK": //GET PROVIDER BY NETWORK
                idp = idPConfigurationService.getProviderByNetwork(request);
                break;
        }

        //REDIRECT USERS BASED ON THEIR IDP TYPE
        if (idp.getType().equalsIgnoreCase("okta")) {//IDP TYPE = OKTA: COMPILE SIGN-IN WIDGET USING THE IDP INFORMATION
            model.put("idp", idp);
            destination = "login";//THE LOGIN.HTML TEMPLATE GETS COMPILED
        } else if (idp.getType().equalsIgnoreCase("adfs")) {//IDP TYPE = ADFS: REDIRECT USER TO ADFS IDP INITIATED SSO
            destination = "redirect:" + idp.getUrl() + "?loginToRp=" + idp.getAcs();
        } else if (idp.getType().equalsIgnoreCase("saml")) {//IDP TYPE = SAML (E.G. ORACLE ACCESS MANAGER, TIVOLI AM, OR NOVELL): REDIRECT USER TO SAML PROVIDER IDP INITIATED SSO
            destination = "redirect:" + idp.getUrl() + "?redirecturl=" + idp.getAcs();
        } else {
            //IF NO IDP IS IDENTIFIED, JUST GO LOGIN. YOU CAN IMPLEMENT A HONEYPOT HERE ;)
            destination = "login";
        }
        //GO TO THE DESTINATION: OSW OR IDP INITIATED SSO
        return destination;
    }//findIdp
}
