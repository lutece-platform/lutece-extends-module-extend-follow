/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.extend.modules.follow.service;

import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistory;
import fr.paris.lutece.plugins.extend.business.extender.history.ResourceExtenderHistoryFilter;
import fr.paris.lutece.plugins.extend.modules.follow.business.Follow;
import fr.paris.lutece.plugins.extend.modules.follow.business.FollowFilter;
import fr.paris.lutece.plugins.extend.modules.follow.business.FollowHistory;
import fr.paris.lutece.plugins.extend.modules.follow.business.IFollowDAO;
import fr.paris.lutece.plugins.extend.modules.follow.service.extender.FollowResourceExtender;
import fr.paris.lutece.plugins.extend.service.extender.history.IResourceExtenderHistoryService;
import fr.paris.lutece.portal.service.security.LuteceUser;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * followService
 *
 */
public class FollowService implements IFollowService
{
    /** The Constant BEAN_SERVICE. */
    public static final String BEAN_SERVICE = "extendfollow.followService";
    @Inject
    private IFollowDAO _followDAO;
    @Inject
    private IResourceExtenderHistoryService _resourceExtenderHistoryService;
    @Inject
    private IFollowHistoryService _followHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FollowPlugin.TRANSACTION_MANAGER )
    public void create( Follow follow )
    {
        _followDAO.insert( follow, FollowPlugin.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FollowPlugin.TRANSACTION_MANAGER )
    public void update( Follow follow )
    {
        _followDAO.store( follow, FollowPlugin.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FollowPlugin.TRANSACTION_MANAGER )
    public synchronized void doFollow( String strIdExtendableResource, String strExtendableResourceType,
        int nVoteValue, HttpServletRequest request )
    {
        Follow follow = findByResource( strIdExtendableResource, strExtendableResourceType );

        // Create the follow if not exists
        if ( follow == null )
        {
            follow = new Follow(  );
            follow.setIdExtendableResource( strIdExtendableResource );
            follow.setExtendableResourceType( strExtendableResourceType );
            follow.setFollowCount( 1 );
            create( follow );
        }
        else
        {
            follow.setFollowCount( follow.getFollowCount(  ) + 1 );
            update( follow );
        }

        ResourceExtenderHistory history = _resourceExtenderHistoryService.create( FollowResourceExtender.RESOURCE_EXTENDER,
                strIdExtendableResource, strExtendableResourceType, request );

        FollowHistory followHistory = new FollowHistory(  );
        followHistory.setIdExtenderHistory( history.getIdHistory(  ) );
        followHistory.setFollowValue( nVoteValue );
        _followHistoryService.create( followHistory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FollowPlugin.TRANSACTION_MANAGER )
    public synchronized void doCancelFollow( LuteceUser user, String strIdExtendableResource,
        String strExtendableResourceType )
    {
        ResourceExtenderHistoryFilter resourceExtenderHistoryFilter = new ResourceExtenderHistoryFilter(  );
        resourceExtenderHistoryFilter.setUserGuid( user.getName(  ) );
        resourceExtenderHistoryFilter.setIdExtendableResource( strIdExtendableResource );

        List<ResourceExtenderHistory> histories = _resourceExtenderHistoryService.findByFilter( resourceExtenderHistoryFilter );

        if ( CollectionUtils.isNotEmpty( histories ) )
        {
            for ( ResourceExtenderHistory history : histories )
            {
                FollowHistory followHistory = _followHistoryService.findByHistoryExtenderId( history.getIdHistory(  ) );

                if ( followHistory != null )
                {
                    _followHistoryService.remove( followHistory.getIdFollowHistory(  ) );

                    Follow follow = findByResource( strIdExtendableResource, strExtendableResourceType );
                    follow.setFollowCount( follow.getFollowCount(  ) - 1 );
                    update( follow );
                }

                _resourceExtenderHistoryService.remove( Integer.valueOf( "" + history.getIdHistory(  ) ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FollowPlugin.TRANSACTION_MANAGER )
    public void remove( int nIdFollow )
    {
        _followDAO.delete( nIdFollow, FollowPlugin.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FollowPlugin.TRANSACTION_MANAGER )
    public void removeByResource( String strIdExtendableResource, String strExtendableResourceType )
    {
        _followDAO.deleteByResource( strIdExtendableResource, strExtendableResourceType, FollowPlugin.getPlugin(  ) );
    }

    // GET

    /**
     * {@inheritDoc}
     */
    @Override
    public Follow findByPrimaryKey( int nIdFollow )
    {
        return _followDAO.load( nIdFollow, FollowPlugin.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Follow findByResource( String strIdExtendableResource, String strExtendableResourceType )
    {
        return _followDAO.loadByResource( strIdExtendableResource, strExtendableResourceType, FollowPlugin.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Follow> findByFilter( FollowFilter filter )
    {
        return _followDAO.loadByFilter( filter, FollowPlugin.getPlugin(  ) );
    }
}
