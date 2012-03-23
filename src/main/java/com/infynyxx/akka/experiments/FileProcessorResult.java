package com.infynyxx.akka.experiments;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class FileProcessorResult {
    private final int linesProcessed;
    private final long started;

    public FileProcessorResult(int linesProcessed, long started) {
        this.linesProcessed = linesProcessed;
        this.started = started;
    }

    public int getLinesProcessed() {
        return linesProcessed;
    }

    public long getStarted() {
        return started;
    }
}
