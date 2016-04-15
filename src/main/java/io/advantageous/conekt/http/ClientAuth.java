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

package io.advantageous.conekt.http;


/**
 * Configures the engine to require/request client authentication. Following are the options :
 * <p>
 * NONE - No client authentication is requested or required.
 * <p>
 * REQUEST - Accept authentication if presented by client. If this option is set and the client chooses
 * not to provide authentication information about itself, the negotiations will continue.
 * <p>
 * REQUIRED - Require client to present authentication, if not presented then negotiations will be declined.
 * <p>
 * Created by manishk on 10/2/2015.
 */
public enum ClientAuth {
    NONE, REQUEST, REQUIRED
}
