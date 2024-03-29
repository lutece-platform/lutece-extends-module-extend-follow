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

import java.util.List;

/**
 * ICommentDAO.
 */
public interface IFollowDAO
{
    /**
     * Delete.
     *
     * @param nIdFollow
     *            the n id Follow
     * @param plugin
     *            the plugin
     */
    void delete( int nIdFollow, Plugin plugin );

    /**
     * Delete by id hub resource.
     *
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param strExtendableResourceType
     *            the str extendable resource type
     * @param plugin
     *            the plugin
     */
    void deleteByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin );

    /**
     * Insert.
     *
     * @param follow
     *            the Follow
     * @param plugin
     *            the plugin
     */
    void insert( Follow follow, Plugin plugin );

    /**
     * Load.
     *
     * @param nIdFollow
     *            the n id Follow
     * @param plugin
     *            the plugin
     * @return the Follow
     */
    Follow load( int nIdFollow, Plugin plugin );

    /**
     * Store.
     *
     * @param follow
     *            the Follow
     * @param plugin
     *            the plugin
     */
    void store( Follow follow, Plugin plugin );

    /**
     * Select by id hub resource.
     *
     * @param strIdExtendableResource
     *            the str id extendable resource
     * @param strExtendableResourceType
     *            the str extendable resource type
     * @param plugin
     *            the plugin
     * @return the Follow
     */
    Follow loadByResource( String strIdExtendableResource, String strExtendableResourceType, Plugin plugin );

    /**
     *
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return List Follow
     */
    List<Follow> loadByFilter( FollowFilter filter, Plugin plugin );
}
