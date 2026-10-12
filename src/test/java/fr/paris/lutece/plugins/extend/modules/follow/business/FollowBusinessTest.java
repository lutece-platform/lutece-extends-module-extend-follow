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
package fr.paris.lutece.plugins.extend.modules.follow.business;

import fr.paris.lutece.plugins.extend.business.extender.config.IExtenderConfigDAO;
import fr.paris.lutece.plugins.extend.modules.follow.service.FollowPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests the persistence of the follows, their history and the extender configuration.
 */
public class FollowBusinessTest extends LuteceTestCase
{
    private static final String RESOURCE_TYPE = "followTestType";
    private static final String ID_RESOURCE_1 = "1001";
    private static final String ID_RESOURCE_2 = "1002";
    private static final int ID_EXTENDER = 99001;

    @Inject
    private IFollowDAO _followDAO;

    @Inject
    private IFollowHistoryDAO _followHistoryDAO;

    @Inject
    private IExtenderConfigDAO<FollowExtenderConfig> _configDAO;

    /**
     * Creates, reads, updates, filters and deletes follows.
     */
    @Test
    public void testFollow( )
    {
        Plugin plugin = FollowPlugin.getPlugin( );

        Follow follow1 = newFollow( ID_RESOURCE_1 );
        Follow follow2 = newFollow( ID_RESOURCE_2 );
        _followDAO.insert( follow1, plugin );
        _followDAO.insert( follow2, plugin );

        assertTrue( follow1.getIdFollow( ) > 0 );
        assertNotEquals( follow1.getIdFollow( ), follow2.getIdFollow( ) );

        Follow stored = _followDAO.load( follow1.getIdFollow( ), plugin );
        assertEquals( ID_RESOURCE_1, stored.getIdExtendableResource( ) );
        assertEquals( 1, stored.getFollowCount( ) );

        follow1.setFollowCount( 3 );
        _followDAO.store( follow1, plugin );
        assertEquals( 3, _followDAO.loadByResource( ID_RESOURCE_1, RESOURCE_TYPE, plugin ).getFollowCount( ) );

        FollowFilter filter = new FollowFilter( );
        filter.setExtendableResourceType( RESOURCE_TYPE );
        List<Follow> listFollows = _followDAO.loadByFilter( filter, plugin );
        assertEquals( 2, listFollows.size( ) );

        _followDAO.deleteByResource( ID_RESOURCE_1, RESOURCE_TYPE, plugin );
        assertNull( _followDAO.load( follow1.getIdFollow( ), plugin ) );
        assertNotNull( _followDAO.load( follow2.getIdFollow( ), plugin ) );

        _followDAO.delete( follow2.getIdFollow( ), plugin );
        assertNull( _followDAO.load( follow2.getIdFollow( ), plugin ) );
    }

    /**
     * Creates, finds and removes a follow history.
     */
    @Test
    public void testFollowHistory( )
    {
        Plugin plugin = FollowPlugin.getPlugin( );
        long lIdExtenderHistory = 880001L;

        FollowHistory history = new FollowHistory( );
        history.setIdExtenderHistory( lIdExtenderHistory );
        history.setFollowValue( 1 );
        _followHistoryDAO.create( history, plugin );
        assertTrue( history.getIdFollowHistory( ) > 0 );

        FollowHistory stored = _followHistoryDAO.findByHistoryExtenderId( lIdExtenderHistory, plugin );
        assertEquals( history.getIdFollowHistory( ), stored.getIdFollowHistory( ) );
        assertEquals( 1, stored.getFollowValue( ) );

        _followHistoryDAO.remove( history.getIdFollowHistory( ), plugin );
        assertNull( _followHistoryDAO.findByHistoryExtenderId( lIdExtenderHistory, plugin ) );
    }

    /**
     * Creates, updates and deletes an extender configuration.
     */
    @Test
    public void testExtenderConfig( )
    {
        FollowExtenderConfig config = new FollowExtenderConfig( );
        config.setIdExtender( ID_EXTENDER );
        config.setAuthenticatedMode( true );
        _configDAO.insert( config );
        assertTrue( _configDAO.load( ID_EXTENDER ).isAuthenticatedMode( ) );

        config.setAuthenticatedMode( false );
        _configDAO.store( config );
        assertFalse( _configDAO.load( ID_EXTENDER ).isAuthenticatedMode( ) );

        _configDAO.delete( ID_EXTENDER );
        assertNull( _configDAO.load( ID_EXTENDER ) );
    }

    /**
     * Builds a follow of the test resource type.
     *
     * @param strIdResource
     *            the id of the resource
     * @return the follow
     */
    private static Follow newFollow( String strIdResource )
    {
        Follow follow = new Follow( );
        follow.setIdExtendableResource( strIdResource );
        follow.setExtendableResourceType( RESOURCE_TYPE );
        follow.setFollowCount( 1 );

        return follow;
    }
}
