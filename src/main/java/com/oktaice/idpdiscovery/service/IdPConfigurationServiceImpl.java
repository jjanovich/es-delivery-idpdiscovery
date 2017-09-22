package com.oktaice.idpdiscovery.service;

import com.oktaice.idpdiscovery.config.IdPConfiguration;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class IdPConfigurationServiceImpl implements IdPConfigurationService {

    //Auto-load the IdPConfiguration before IdP Discovery is available
    //This way, we have all configs from application.yml available :D
    private IdPConfiguration idPConfiguration;

    public IdPConfigurationServiceImpl(IdPConfiguration idPConfiguration) {
        this.idPConfiguration = idPConfiguration;
    }

    @Override
    public String getStrategy() {
        return idPConfiguration.getStrategy();
    }

    @Override
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
    }

    @Override
    public IdPConfiguration.ProviderConfiguration getProviderByCompany(String company) {
        IdPConfiguration.ProviderConfiguration userIdP = null;
        for (IdPConfiguration.ProviderConfiguration ip : idPConfiguration.getIdps()) {
            if (ip.getName().trim().equalsIgnoreCase(company)) {
                userIdP = ip;
                break;
            }
        }
        return userIdP;
    }

    @Override
    public IdPConfiguration.ProviderConfiguration getProviderByNetwork(HttpServletRequest request) {
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
    }
}
