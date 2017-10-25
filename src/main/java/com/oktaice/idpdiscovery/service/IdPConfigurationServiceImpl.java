package com.oktaice.idpdiscovery.service;

import com.oktaice.idpdiscovery.config.IdPConfiguration;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public List<IdPConfiguration.ProviderConfiguration> getIdps(){ return idPConfiguration.getIdps(); }

    @Override
    public IdPConfiguration.ProviderConfiguration getProviderByUserId(String userid) {
        String domain = null;
        if(userid.contains("@")){
            domain = userid.substring(userid.indexOf('@') + 1);
        }else{
            domain = userid.substring(0, userid.indexOf('\\'));
        }
        IdPConfiguration.ProviderConfiguration userIdP = null;
        for (IdPConfiguration.ProviderConfiguration ip : idPConfiguration.getIdps()) {
            if (ip.getUseridPattern().contains(domain)) {
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
        //TODO: If you want to overwrite the ip, this is the place
        //userIp = "127.0.0.1";
        System.out.println("user ip address: "+userIp);

        IdPConfiguration.ProviderConfiguration userIdP = null;
        for (IdPConfiguration.ProviderConfiguration ip : idPConfiguration.getIdps()) {
            IpAddressMatcher matcher = new IpAddressMatcher(ip.getCidr());
            if(matcher.matches(userIp)){
                userIdP = ip;
                break;
            }
        }
        return userIdP;


    }
}
