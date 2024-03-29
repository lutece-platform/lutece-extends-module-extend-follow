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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Follow objects.
 */
public class FollowHistoryDAO implements IFollowHistoryDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_follow_history ) FROM extend_follow_history ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_follow_history ( id_follow_history, id_extender_history, follow_value ) "
            + " VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_FIND_BY_EXTENDER_HISTORY_ID = " SELECT id_follow_history, id_extender_history, follow_value FROM extend_follow_history WHERE id_extender_history = ?";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = " DELETE FROM extend_follow_history WHERE id_follow_history "
            + "IN (SELECT id_history FROM extend_resource_extender_history WHERE extender_type = ? AND resource_type = ?)";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_follow_history WHERE id_follow_history = ? ";

    /**
     * Generates a new primary key.
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    @Override
    public void remove( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public void removeByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_RESOURCE, plugin );
        daoUtil.setString( 1, strIdExtendableResource );
        daoUtil.setString( 2, strExtendableResourceType );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public void create( FollowHistory followHistory, Plugin plugin )
    {
        followHistory.setIdFollowHistory( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, followHistory.getIdFollowHistory( ) );
        daoUtil.setLong( nIndex++, followHistory.getIdExtenderHistory( ) );
        daoUtil.setInt( nIndex++, followHistory.getFollowValue( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public FollowHistory findByHistoryExtenderId( long lIdHistoryExtenderId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_EXTENDER_HISTORY_ID, plugin );

        daoUtil.setLong( 1, lIdHistoryExtenderId );

        daoUtil.executeQuery( );

        FollowHistory followHistory = null;

        if ( daoUtil.next( ) )
        {
            followHistory = new FollowHistory( );
            followHistory.setIdFollowHistory( daoUtil.getInt( 1 ) );
            followHistory.setIdExtenderHistory( daoUtil.getLong( 2 ) );
            followHistory.setFollowValue( daoUtil.getInt( 3 ) );
        }

        daoUtil.free( );

        return followHistory;
    }
}
