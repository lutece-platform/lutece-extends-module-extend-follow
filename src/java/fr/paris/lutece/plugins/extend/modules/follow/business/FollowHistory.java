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

/**
 *
 * Follow
 *
 */
public class FollowHistory
{
    private int _nIdFollowHistory;
    private long _lIdExtenderHistory;
    private int _nFollowValue;

    /**
     * @return the _nIdFollowHistory
     */
    public int getIdFollowHistory( )
    {
        return _nIdFollowHistory;
    }

    /**
     * @param nIdFollowHistory
     *            the _nIdFollowHistory to set
     */
    public void setIdFollowHistory( int nIdFollowHistory )
    {
        this._nIdFollowHistory = nIdFollowHistory;
    }

    /**
     * @return the _nIdExtenderHistory
     */
    public long getIdExtenderHistory( )
    {
        return _lIdExtenderHistory;
    }

    /**
     * @param lIdExtenderHistory
     *            the _nIdExtenderHistory to set
     */
    public void setIdExtenderHistory( long lIdExtenderHistory )
    {
        this._lIdExtenderHistory = lIdExtenderHistory;
    }

    /**
     * @return the _nFollowValue
     */
    public int getFollowValue( )
    {
        return _nFollowValue;
    }

    /**
     * @param nFollowValue
     *            the _nFollowValue to set
     */
    public void setFollowValue( int nFollowValue )
    {
        this._nFollowValue = nFollowValue;
    }
}
