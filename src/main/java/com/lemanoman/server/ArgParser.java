package com.lemanoman.server;

import org.apache.commons.cli.*;

public class ArgParser {

    public ArgParser(OnCommandAction onCommandAction, String... args) {
        Options options = new Options();

        Option serverOption = new Option("s", "server", false, "Start the server");
        options.addOption(serverOption);

        Option clientOption = new Option("c", "client", false, "Start the client with host");

        options.addOption(clientOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        if (cmd.hasOption("server")) {
            onCommandAction.serverStart();
        } else if (cmd.hasOption("client")) {
            String host = cmd.getOptionValue("client");
            onCommandAction.clientStart(host, "/home/kevim/teste-server/bigfile.deb", "/home/kevim/teste-client/bigfile.deb");
        } else {
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

    }
}
