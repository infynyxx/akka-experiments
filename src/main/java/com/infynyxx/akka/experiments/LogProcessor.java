package com.infynyxx.akka.experiments;

import org.apache.pekko.actor.*;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class LogProcessor {

    private List<File> files;

    public LogProcessor(List<File> files) {
        this.files = Collections.unmodifiableList(files);
    }

    public boolean run() {
        // create akka actor system
        ActorSystem system = ActorSystem.create();

        final ActorRef listener  = system.actorOf(Props.create(Listener.class), "Listener");

        // create the master
        ActorRef master = system.actorOf(Props.create(Master.class, files, listener), "master");

        // start the calculation
        master.tell(new Process(), ActorRef.noSender());

        return true;
    }

    static class Process {

    }

    public static class Master extends UntypedAbstractActor {

        private final ActorRef listener;
        private final List<File> files;

        private final ActorRef workerRouter;

        private int numberOfResults = 0;
        private final long start = System.currentTimeMillis();

        private int linesCount = 0;

        public Master(final List<File> files, final ActorRef listener) {
            this.listener = listener;
            this.files = files;

            this.workerRouter = getContext().actorOf(Props.create(Worker.class));
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Process) {
                for (int start = 0; start < files.size(); start++) {
                    workerRouter.tell(new Work(start, files.get(start)), getSelf());
                }
            } else if (message instanceof Result) {
                Result result = (Result) message;
                numberOfResults += 1;
                linesCount += result.getLinesCount();

                if (numberOfResults == files.size()) {
                    Duration duration = Duration.ofMillis(System.currentTimeMillis() - start);
                    listener.tell(new FileProcessedAproximation(files.size(), linesCount, duration), getSelf());
                    getContext().stop(getSelf());
                }
            } else {
                unhandled(message);
            }
        }
    }

    public static class Worker extends UntypedAbstractActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Work) {
                Work work = (Work) message;
                FileProcessorResult fileProcessorResult = analyze(work.getFile());
                Duration duration = Duration.ofMillis(System.currentTimeMillis() - fileProcessorResult.getStarted());
                getSender().tell(new Result(fileProcessorResult.getLinesProcessed(), duration), getSelf());
            } else {
                unhandled(message);
            }
        }

        private FileProcessorResult analyze(File file) {
            FileProcessor fileProcessor = new FileProcessor(file);
            return fileProcessor.analyze();
        }
    }

    static class Work {
        private final int start;
        private final File file;

        public Work(int start, File file) {
            this.start = start;
            this.file = file;
        }

        public int getStart() {
            return start;
        }

        public File getFile() {
            return file;
        }
    }

    static class FileProcessedAproximation {

        private final int filesCount;
        private final int linesCount;
        private final Duration duration;

        public FileProcessedAproximation(int filesCount, int linesCount, Duration duration) {
            this.filesCount = filesCount;
            this.duration = duration;
            this.linesCount = linesCount;
        }

        public int getFilesCount() {
            return filesCount;
        }

        public Duration getDuration() {
            return duration;
        }

        public int getLinesCount() {
            return linesCount;
        }
    }

    static class Result {
        private final Duration duration;
        private final int linesCount;

        public Result(int linesCount, Duration duration) {
            this.duration = duration;
            this.linesCount = linesCount;
        }

        public Duration getDuration() {
            return duration;
        }

        public int getLinesCount() {
            return linesCount;
        }
    }

    public static class Listener extends UntypedAbstractActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof FileProcessedAproximation) {
                FileProcessedAproximation aproximation = (FileProcessedAproximation) message;
                System.out.println(String.format("Files Processed: %d\nLines Count: %d \nDuration: %s", aproximation.getFilesCount(), aproximation.getLinesCount(), aproximation.getDuration()));
                getContext().system().terminate();
            } else {
                unhandled(message);
            }
        }
    }
}
