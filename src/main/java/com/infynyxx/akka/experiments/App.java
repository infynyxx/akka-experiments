package com.infynyxx.akka.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class App 
{
    public static void main( String[] args )
    {

        File f = new File("logdata");
        List<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
        new LogProcessor(files).run();
    }
}
