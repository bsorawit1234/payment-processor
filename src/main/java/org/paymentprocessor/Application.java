package org.paymentprocessor;

import org.paymentprocessor.parser.JsonPaymentParser;

import java.io.File;

public class Application {
     static void main(String[] args) {
        String inputFileName = null;
        String outputFileName = null;
        String baseDirectory = "data";

        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--input") && i+1 < args.length) {
                inputFileName = args[i+1];
            } else if(args[i].equals("--output") && i+1 < args.length) {
                outputFileName = args[i+1];
            }
        }

        if(inputFileName == null || outputFileName == null) {
            System.err.println("error");
            System.exit(1);
        }


        File inputFile = new File(baseDirectory, inputFileName);
        File outputFile = new File(baseDirectory, outputFileName);

        try {
            JsonPaymentParser parser = new JsonPaymentParser();

            var requests = parser.readInputFile(inputFile.getPath());
            for (var request : requests) {
                System.out.println(request);
            }

        } catch (Exception e) {
            System.err.println("Application failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
