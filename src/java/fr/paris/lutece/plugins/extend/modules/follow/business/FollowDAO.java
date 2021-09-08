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

import fr.paris.lutece.plugins.extend.business.extender.ResourceExtenderDTOFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for follow objects.
 */
public class FollowDAO implements IFollowDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_follow ) FROM extend_follow ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO extend_follow ( id_follow, id_resource, resource_type, follow_count ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_follow, id_resource, resource_type, follow_count FROM extend_follow ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_follow = ? ";
    private static final String SQL_QUERY_SELECT_BY_RESOURCE = SQL_QUERY_SELECT_ALL + " WHERE id_resource = ? AND resource_type = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM extend_follow WHERE id_follow = ? ";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = " DELETE FROM extend_follow WHERE resource_type = ? ";
    private static final String SQL_QUERY_FILTER_ID_RESOURCE = " AND id_resource = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE extend_follow SET id_resource = ?, resource_type = ?, follow_count = ? WHERE id_follow = ?  ";

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

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Follow follow, Plugin plugin )
    {
        int nNewPrimaryKey = newPrimaryKey( plugin );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        follow.setIdFollow( nNewPrimaryKey );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, follow.getIdFollow( ) );
        daoUtil.setString( nIndex++, follow.getIdExtendableResource( ) );
        daoUtil.setString( nIndex++, follow.getExtendableResourceType( ) );
        daoUtil.setInt( nIndex, follow.getFollowCount( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Follow load( int nIdfollow, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdfollow );
        daoUtil.executeQuery( );

        Follow follow = null;

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            follow = new Follow( );
            follow.setIdFollow( daoUtil.getInt( nIndex++ ) );
            follow.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            follow.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            follow.setFollowCount( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return follow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdfollow, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdfollow );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        int nIndex = 1;
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_DELETE_BY_RESOURCE );

        if ( !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource ) )
        {
            sbSql.append( SQL_QUERY_FILTER_ID_RESOURCE );
        }

        DAOUtil daoUtil = new DAOUtil( sbSql.toString( ), plugin );
        daoUtil.setString( nIndex++, strExtendableResourceType );

        if ( !ResourceExtenderDTOFilter.WILDCARD_ID_RESOURCE.equals( strIdExtendableResource ) )
        {
            daoUtil.setString( nIndex, strIdExtendableResource );
        }

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Follow follow, Plugin plugin )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( nIndex++, follow.getIdExtendableResource( ) );
        daoUtil.setString( nIndex++, follow.getExtendableResourceType( ) );
        daoUtil.setInt( nIndex++, follow.getFollowCount( ) );

        daoUtil.setInt( nIndex, follow.getIdFollow( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Follow loadByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin )
    {
        Follow follow = null;

        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RESOURCE, plugin );
        daoUtil.setString( nIndex++, strIdExtendableResource );
        daoUtil.setString( nIndex, strExtendableResourceType );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nIndex = 1;

            follow = new Follow( );
            follow.setIdFollow( daoUtil.getInt( nIndex++ ) );
            follow.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            follow.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            follow.setFollowCount( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return follow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Follow> loadByFilter( FollowFilter filter, Plugin plugin )
    {
        List<Follow> listFollow = new ArrayList<Follow>( );
        DAOUtil daoUtil = new DAOUtil( filter.buildSQLQuery( SQL_QUERY_SELECT_ALL ), plugin );
        filter.setFilterValues( daoUtil );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;
            Follow follow = new Follow( );
            follow.setIdFollow( daoUtil.getInt( nIndex++ ) );
            follow.setIdExtendableResource( daoUtil.getString( nIndex++ ) );
            follow.setExtendableResourceType( daoUtil.getString( nIndex++ ) );
            follow.setFollowCount( daoUtil.getInt( nIndex ) );

            listFollow.add( follow );
        }

        daoUtil.free( );

        return listFollow;
    }
}
