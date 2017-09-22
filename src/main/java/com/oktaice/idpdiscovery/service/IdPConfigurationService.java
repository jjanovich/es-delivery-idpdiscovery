package com.oktaice.idpdiscovery.service;

import com.oktaice.idpdiscovery.config.IdPConfiguration;

import javax.servlet.http.HttpServletRequest;

public interface IdPConfigurationService {

    IdPConfiguration.ProviderConfiguration getProviderByMail(String email);
    IdPConfiguration.ProviderConfiguration getProviderByCompany(String company);
    IdPConfiguration.ProviderConfiguration getProviderByNetwork(HttpServletRequest request);
    String getStrategy();
}
