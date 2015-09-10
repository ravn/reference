package org.apache.http.impl.conn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionOutputBuffer;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is a straight copy of ManagedHttpClientConnectionFactory, but it takes a chunksize param. This allows
 * you to control the size of the http chunks.
 */
public  class ChunkyManagedHttpClientConnectionFactory extends ManagedHttpClientConnectionFactory {

    private static final AtomicLong COUNTER = new AtomicLong();
    private final int chunkSize;

    private final Log log = LogFactory.getLog(DefaultManagedHttpClientConnection.class);
    private final Log headerlog = LogFactory.getLog("org.apache.http.headers");
    private final Log wirelog = LogFactory.getLog("org.apache.http.wire");

    private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
    private final HttpMessageParserFactory<HttpResponse> responseParserFactory;
    private final ContentLengthStrategy incomingContentStrategy;
    private final ContentLengthStrategy outgoingContentStrategy;


    public ChunkyManagedHttpClientConnectionFactory(final HttpMessageWriterFactory<HttpRequest> requestWriterFactory,
                                                    final HttpMessageParserFactory<HttpResponse> responseParserFactory,
                                                    final ContentLengthStrategy incomingContentStrategy,
                                                    final ContentLengthStrategy outgoingContentStrategy,
                                                    int chunkSize) {
        super();
        this.chunkSize = chunkSize;
        this.requestWriterFactory = requestWriterFactory != null ? requestWriterFactory :
                DefaultHttpRequestWriterFactory.INSTANCE;
        this.responseParserFactory = responseParserFactory != null ? responseParserFactory :
                DefaultHttpResponseParserFactory.INSTANCE;
        this.incomingContentStrategy = incomingContentStrategy != null ? incomingContentStrategy :
                LaxContentLengthStrategy.INSTANCE;
        this.outgoingContentStrategy = outgoingContentStrategy != null ? outgoingContentStrategy :
                StrictContentLengthStrategy.INSTANCE;
    }


    public ChunkyManagedHttpClientConnectionFactory(
            final HttpMessageWriterFactory<HttpRequest> requestWriterFactory,
            final HttpMessageParserFactory<HttpResponse> responseParserFactory,
            int chunkSize) {
        this(requestWriterFactory, responseParserFactory, null, null,chunkSize);
    }

    public ChunkyManagedHttpClientConnectionFactory(
            final HttpMessageParserFactory<HttpResponse> responseParserFactory,
            int chunkSize) {
        this(null, responseParserFactory, chunkSize);
    }

    public ChunkyManagedHttpClientConnectionFactory(int chunkSize) {
        this(null, null, chunkSize);
    }

    @Override
    public ManagedHttpClientConnection create(HttpRoute route, ConnectionConfig config) {
        final ConnectionConfig cconfig = config != null ? config : ConnectionConfig.DEFAULT;
        CharsetDecoder chardecoder = null;
        CharsetEncoder charencoder = null;
        final Charset charset = cconfig.getCharset();
        final CodingErrorAction malformedInputAction = cconfig.getMalformedInputAction() != null ?
                cconfig.getMalformedInputAction() : CodingErrorAction.REPORT;
        final CodingErrorAction unmappableInputAction = cconfig.getUnmappableInputAction() != null ?
                cconfig.getUnmappableInputAction() : CodingErrorAction.REPORT;
        if (charset != null) {
            chardecoder = charset.newDecoder();
            chardecoder.onMalformedInput(malformedInputAction);
            chardecoder.onUnmappableCharacter(unmappableInputAction);
            charencoder = charset.newEncoder();
            charencoder.onMalformedInput(malformedInputAction);
            charencoder.onUnmappableCharacter(unmappableInputAction);
        }
        final String id = "http-outgoing-" + Long.toString(COUNTER.getAndIncrement());
        return new ChunkyLoggingManagedHttpClientConnection(
                id,
                log,
                headerlog,
                wirelog,
                cconfig.getBufferSize(),
                cconfig.getFragmentSizeHint(),
                chardecoder,
                charencoder,
                cconfig.getMessageConstraints(),
                incomingContentStrategy,
                outgoingContentStrategy,
                requestWriterFactory,
                responseParserFactory,
                chunkSize);
    }

    static class ChunkyLoggingManagedHttpClientConnection extends LoggingManagedHttpClientConnection {
        private final int chunkSize;


        public ChunkyLoggingManagedHttpClientConnection(String id, Log log, Log headerlog, Log wirelog, int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder, CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory, HttpMessageParserFactory<HttpResponse> responseParserFactory, int chunkSize) {
            super(id, log, headerlog, wirelog, buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
            this.chunkSize = chunkSize;
        }


        @Override
        protected OutputStream createOutputStream(long len, SessionOutputBuffer outbuffer) {
            if (len == ContentLengthStrategy.CHUNKED) {
                return new ChunkedOutputStream(chunkSize, outbuffer);
            }
            return super.createOutputStream(len, outbuffer);
        }
    }
}
