/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.tools.AbstractToolController2;

public class ReindexContentToolController
    extends AbstractToolController2
{
    private ReindexContentToolService reindexContentToolService;

    private List<String> logEntries = new ArrayList<String>();

    private Boolean reindexingInProgress = Boolean.FALSE;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        if ( req.getParameter( "reindex" ) != null )
        {
            startReindexAllContentTypes();
            redirectToReferrer( req, res );
        }

        final HashMap<String, Object> model = new HashMap<String, Object>();
        model.put( "reindexInProgress", reindexingInProgress );
        model.put( "reindexLog", logEntries );
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );

        renderView(req, res, model, "reindexContentPage" );
    }

    private synchronized void startReindexAllContentTypes()
    {

        if ( reindexingInProgress )
        {
            return;
        }

        reindexingInProgress = Boolean.TRUE;

        Thread reindexThread = new Thread( new Runnable()
        {
            public void run()
            {

                try
                {
                    reindexContentToolService.reindexAllContent( logEntries );
                }
                finally
                {
                    reindexingInProgress = Boolean.FALSE;
                }
            }
        }, "Reindex Content Thread" );

        reindexThread.start();
    }

    @Autowired
    public void setReindexContentToolService( ReindexContentToolService value )
    {
        this.reindexContentToolService = value;
    }
}
