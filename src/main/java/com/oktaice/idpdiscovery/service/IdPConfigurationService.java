package com.oktaice.idpdiscovery.service;

import com.oktaice.idpdiscovery.config.IdPConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IdPConfigurationService {

    IdPConfiguration.ProviderConfiguration getProviderByUserId(String userId);
    IdPConfiguration.ProviderConfiguration getProviderByCompany(String company);
    IdPConfiguration.ProviderConfiguration getProviderByNetwork(HttpServletRequest request);
    String getStrategy();
    List<IdPConfiguration.ProviderConfiguration> getIdps();
}
