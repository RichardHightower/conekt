/*
 *
 *  * Copyright (c) 2011-2016 The original author or authors
 *  * This project contains modified work from the Vert.x Project.
 *  * The Vert.x project Copyright is owned by Red Hat and/or the
 *  * original authors of the Vert.x project including Tim Fox, Julien Vet,
 *  * Norman Maurer, and many others.
 *  * We have left the original author tags on this MODIFIED COPY/FORK.
 *  *
 *  * Modified work is Copyright (c) 2015-2016 Rick Hightower and Geoff Chandler.
 *  * ------------------------------------------------------
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the Eclipse Public License v1.0
 *  * and Apache License v2.0 which accompanies this distribution.
 *  *
 *  *     The Eclipse Public License is available at
 *  *     http://www.eclipse.org/legal/epl-v10.html
 *  *
 *  *     The Apache License v2.0 is available at
 *  *     http://www.opensource.org/licenses/apache2.0.php
 *  *
 *  * You may elect to redistribute this code under either of these licenses.
 *
 */
package io.advantageous.conekt.dns;


/**
 * Represent a Service-Record (SRV) which was resolved for a domain.
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public interface SrvRecord {

    /**
     * Returns the priority for this service record.
     */
    int priority();

    /**
     * Returns the weight of this service record.
     */
    int weight();

    /**
     * Returns the port the service is running on.
     */
    int port();

    /**
     * Returns the name for the server being queried.
     */
    String name();

    /**
     * Returns the protocol for the service being queried (i.e. "_tcp").
     */
    String protocol();

    /**
     * Returns the service's name (i.e. "_http").
     */
    String service();

    /**
     * Returns the name of the host for the service.
     */
    String target();
}
