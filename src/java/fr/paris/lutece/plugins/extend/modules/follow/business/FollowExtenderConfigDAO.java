/*
 * Copyright (c) 2002-2022, Mairie de Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;

/**
 * This class provides Data Access methods for FollowExtenderConfig objects
 */
public final class FollowExtenderConfigDAO implements IExtenderConfigDAO<FollowExtenderConfig>
{
    // Constants
    private static final String SQL_QUERY_SELECT    = "SELECT id_extender, authenticated_mode FROM extend_follow_config WHERE id_extender = ?";
    private static final String SQL_QUERY_INSERT    = "INSERT INTO extend_follow_config ( id_extender, authenticated_mode ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE    = "DELETE FROM extend_follow_config WHERE id_extender = ? ";
    private static final String SQL_QUERY_UPDATE    = "UPDATE extend_follow_config SET authenticated_mode = ? WHERE id_extender = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FollowExtenderConfig followExtenderConfig )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, FollowPlugin.getPlugin( ) ) )
        {    
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, followExtenderConfig.getIdExtender( ) );
            daoUtil.setBoolean( ++nIndex, followExtenderConfig.isAuthenticatedMode( ) );
    
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FollowExtenderConfig load( int nId  )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, FollowPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );
    
            FollowExtenderConfig followExtenderConfig = null;
    
            if ( daoUtil.next( ) )
            {
                followExtenderConfig = new FollowExtenderConfig( );
    
                followExtenderConfig.setIdExtender( daoUtil.getInt( "id_extender" ) );
                followExtenderConfig.setAuthenticatedMode( daoUtil.getBoolean( "authenticated_mode" ) );
            }
    
            return followExtenderConfig;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nFollowExtenderConfigId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, FollowPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nFollowExtenderConfigId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( FollowExtenderConfig followExtenderConfig )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, FollowPlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setBoolean( nIndex++, followExtenderConfig.isAuthenticatedMode( ) );
            daoUtil.setInt( nIndex++, followExtenderConfig.getIdExtender( ) );
    
            daoUtil.executeUpdate( );
        }
    }

}
