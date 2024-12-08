package com.lemanoman.server;

public interface OnCommandAction {
        void serverStart();
        void clientStart(String host, String filePath, String destPath);
    }