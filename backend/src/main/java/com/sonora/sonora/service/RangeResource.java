package com.sonora.sonora.service;

import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.io.File;

public class RangeResource implements Resource {

    private final Resource delegate;
    private final long     start;
    private final long     length;

    public RangeResource(Resource delegate, long start, long length) {
        this.delegate = delegate;
        this.start    = start;
        this.length   = length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = delegate.getInputStream();
        long skipped = is.skip(start);
        if (skipped < start) {
            throw new IOException("Impossible de positionner le flux à l'offset " + start);
        }
        return new LimitedInputStream(is, length);
    }

    @Override public boolean  exists()                                    { return delegate.exists(); }
    @Override public URL      getURL()    throws IOException              { return delegate.getURL(); }
    @Override public URI      getURI()    throws IOException              { return delegate.getURI(); }
    @Override public File     getFile()   throws IOException              { return delegate.getFile().getAbsoluteFile(); }    @Override public long     contentLength()                             { return length; }
    @Override public long     lastModified() throws IOException           { return delegate.lastModified(); }
    @Override public Resource createRelative(String p) throws IOException { return delegate.createRelative(p); }
    @Override public String   getFilename()                               { return delegate.getFilename(); }
    @Override public String   getDescription()                            { return delegate.getDescription(); }

    private static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        LimitedInputStream(InputStream in, long limit) {
            super(in);
            this.remaining = limit;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) return -1;
            int b = super.read();
            if (b >= 0) remaining--;
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) return -1;
            int toRead = (int) Math.min(len, remaining);
            int n = super.read(b, off, toRead);
            if (n >= 0) remaining -= n;
            return n;
        }
    }
}