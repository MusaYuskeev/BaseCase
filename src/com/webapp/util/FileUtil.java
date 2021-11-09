package com.webapp.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static void main(String[] args) throws IOException {
        File dir = new File("C:\\Users\\Муса\\IdeaProjects\\basejava\\src");
        printDir(dir, "");
    }

    private static void printDir(File dir, String level) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                System.out.println(level + file.getName());
                printDir(file, level + " ");
            } else if (file.isFile()) {
                System.out.println(level + file.getName());
            }
        }


    }
}


