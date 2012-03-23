package com.infynyxx.akka.experiments;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class FileProcessor {

    private File file;
    private long started = System.currentTimeMillis();

    protected final static Logger logger = Logger.getLogger(FileProcessor.class.getName());

    public FileProcessor(File file) {
        this.file = file;
    }

    private int getFileChunks() {
        int defaultBlockSize = 1 * 2024 * 1024;
        return (int) (file.length() / defaultBlockSize);
    }

    public FileProcessorResult analyze() {
        int workerCount = Runtime.getRuntime().availableProcessors();
        final Gson gson = new Gson();
        int count = 0;
        try {
            final FileReader reader = new FileReader(this.file);
            final BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                // do something here
                count++;
                if (count > 0 && count % 50 == 0) {
                    logger.info(String.format("Thread Count: %d", Thread.activeCount()));
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        return new FileProcessorResult(count, started);
    }
}
