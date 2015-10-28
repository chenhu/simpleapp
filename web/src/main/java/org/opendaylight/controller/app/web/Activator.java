
/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.app.web;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleActivator;
import org.osgi.service.http.HttpService;

public class Activator implements BundleActivator {
    private ServiceTracker httpTracker;

    public void start(BundleContext context) throws Exception {
        httpTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
            public void removedService(ServiceReference reference, Object service) {

                try {
                    ((HttpService) service).unregister("/task");
                } catch (IllegalArgumentException exception) {
                    // Ignore; servlet registration probably failed earlier on...
                }
            }

            public Object addingService(ServiceReference reference) {
                // HTTP service is available, register our servlet...
                HttpService httpService = (HttpService) this.context.getService(reference);
                try {
                    httpService.registerResources("/task", "/pages",  null);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return httpService;
            }
        };
        httpTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        httpTracker.close();
    }
}