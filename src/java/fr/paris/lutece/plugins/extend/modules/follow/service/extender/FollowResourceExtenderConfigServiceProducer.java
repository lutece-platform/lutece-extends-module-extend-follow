/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.extend.modules.follow.service.extender;

import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfig;
import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfigDAO;
import fr.paris.lutece.plugins.extend.modules.follow.business.FollowExtenderConfig;
import fr.paris.lutece.plugins.extend.modules.follow.util.constants.FollowConstants;
import fr.paris.lutece.plugins.extend.service.extender.IResourceExtenderService;
import fr.paris.lutece.plugins.extend.service.extender.config.IResourceExtenderConfigService;
import fr.paris.lutece.plugins.extend.service.extender.config.ResourceExtenderConfigService;
import fr.paris.lutece.portal.service.cache.Lutece107Cache;
import fr.paris.lutece.portal.service.cache.LuteceCache;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Produces the configuration service of the follow extender.
 */
@ApplicationScoped
public class FollowResourceExtenderConfigServiceProducer
{
    @Inject
    @LuteceCache( cacheName = "extendConfigCache", keyType = String.class, valueType = Object.class, enable = true )
    private Lutece107Cache<String, Object> _extendConfigCache;

    @Inject
    private IExtenderConfigDAO<FollowExtenderConfig> _followExtenderConfigDAO;

    @Inject
    private IResourceExtenderService _resourceExtenderService;

    /**
     * Produces the configuration service backed by the follow configuration DAO.
     *
     * @return the configuration service
     */
    @Produces
    @ApplicationScoped
    @Named( FollowConstants.BEAN_CONFIG_SERVICE )
    @SuppressWarnings( "unchecked" )
    public IResourceExtenderConfigService produceFollowResourceExtenderConfigService( )
    {
        ResourceExtenderConfigService configService = new ResourceExtenderConfigService( );
        configService.setExtenderConfigDAO( (IExtenderConfigDAO<IExtenderConfig>) (IExtenderConfigDAO<?>) _followExtenderConfigDAO );
        configService.setExtenderService( _resourceExtenderService );
        configService.setExtenderCache( _extendConfigCache );

        return configService;
    }
}
