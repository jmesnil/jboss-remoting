/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.remoting3.remote;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jboss.marshalling.NioByteInput;

final class InboundReplyInputHandler implements NioByteInput.InputHandler {
    private final int rid;
    private final OutboundRequest outboundRequest;

    InboundReplyInputHandler(final OutboundRequest outboundRequest, final int rid) {
        this.outboundRequest = outboundRequest;
        this.rid = rid;
    }

    public void acknowledge() throws IOException {
        final RemoteConnectionHandler connectionHandler = outboundRequest.getRemoteConnectionHandler();
        final ByteBuffer buffer = connectionHandler.getBufferPool().allocate();
        try {
            buffer.putInt(RemoteConnectionHandler.LENGTH_PLACEHOLDER);
            buffer.put(RemoteProtocol.REQUEST_ACK_CHUNK);
            buffer.putInt(rid);
            buffer.flip();
            connectionHandler.sendBlocking(buffer);
            connectionHandler.flushBlocking();
        } finally {
            connectionHandler.getBufferPool().free(buffer);
        }
    }

    public void close() throws IOException {
    }
}