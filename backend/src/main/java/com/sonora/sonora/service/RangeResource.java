package com.sonora.sonora.service;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Resource wrapper exposing only a byte range from a delegate resource.
 */
public class RangeResource implements Resource {

    /** Delegate resource. */
    private final Resource delegate;

    /** Start offset in bytes. */
    private final long start;

    /** Byte length available from the offset. */
    private final long length;

    /**
     * Creates a ranged resource.
     *
     * @param source source resource
     * @param offset range start offset
     * @param rangeLength range length in bytes
     */
    public RangeResource(final Resource source,
                         final long offset,
                         final long rangeLength) {
        this.delegate = source;
        this.start = offset;
        this.length = rangeLength;
    }

    /**
     * Returns an input stream positioned at the configured offset.
     *
     * @return ranged input stream
     * @throws IOException if stream access fails
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream inputStream = delegate.getInputStream();
        long skipped = inputStream.skip(start);
        if (skipped < start) {
            throw new IOException(
                    "Impossible de positionner le flux à l'offset " + start);
        }
        return new LimitedInputStream(inputStream, length);
    }

    /**
     * Indicates whether the delegate resource exists.
     *
     * @return true when delegate exists
     */
    @Override
    public boolean exists() {
        return delegate.exists();
    }

    /**
     * Returns delegate URL.
     *
     * @return resource URL
     * @throws IOException if URL cannot be resolved
     */
    @Override
    public URL getURL() throws IOException {
        return delegate.getURL();
    }

    /**
     * Returns delegate URI.
     *
     * @return resource URI
     * @throws IOException if URI cannot be resolved
     */
    @Override
    public URI getURI() throws IOException {
        return delegate.getURI();
    }

    /**
     * Returns delegate file.
     *
     * @return backing file
     * @throws IOException if file cannot be resolved
     */
    @Override
    public File getFile() throws IOException {
        return delegate.getFile().getAbsoluteFile();
    }

    /**
     * Returns the exposed content length.
     *
     * @return range length
     */
    @Override
    public long contentLength() {
        return length;
    }

    /**
     * Returns delegate last-modified timestamp.
     *
     * @return last modified timestamp
     * @throws IOException if metadata cannot be read
     */
    @Override
    public long lastModified() throws IOException {
        return delegate.lastModified();
    }

    /**
     * Creates a relative resource from the delegate.
     *
     * @param relativePath relative path
     * @return relative resource
     * @throws IOException if resource cannot be created
     */
    @Override
    public Resource createRelative(final String relativePath)
            throws IOException {
        return delegate.createRelative(relativePath);
    }

    /**
     * Returns delegate filename.
     *
     * @return filename
     */
    @Override
    public String getFilename() {
        return delegate.getFilename();
    }

    /**
     * Returns delegate description.
     *
     * @return description
     */
    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    /**
     * Input stream that limits reads to a configured remaining byte count.
     */
    private static final class LimitedInputStream extends FilterInputStream {

        /** Remaining readable bytes. */
        private long remaining;

        /**
         * Creates a new limited input stream.
         *
         * @param inputStream wrapped stream
         * @param limit maximum bytes allowed
         */
        LimitedInputStream(final InputStream inputStream, final long limit) {
            super(inputStream);
            this.remaining = limit;
        }

        /**
         * Reads the next byte if remaining bytes are available.
         *
         * @return next byte or -1 when exhausted
         * @throws IOException if read fails
         */
        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int value = super.read();
            if (value >= 0) {
                remaining--;
            }
            return value;
        }

        /**
         * Reads into a buffer without exceeding the remaining limit.
         *
         * @param bytes destination buffer
         * @param offset buffer offset
         * @param count requested byte count
         * @return number of bytes read or -1 when exhausted
         * @throws IOException if read fails
         */
        @Override
        public int read(final byte[] bytes,
                        final int offset,
                        final int count) throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int toRead = (int) Math.min(count, remaining);
            int readCount = super.read(bytes, offset, toRead);
            if (readCount >= 0) {
                remaining -= readCount;
            }
            return readCount;
        }
    }
}
