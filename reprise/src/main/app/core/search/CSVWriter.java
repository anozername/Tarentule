package main.app.core.search;
import main.Main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.util.Iterator;
import java.util.stream.Stream;

public class CSVWriter {
    private String name;
    private static final String csvFile = Main.file_path;

    public CSVWriter(String name) {
        this.name = name;
    }

    public void writeCSVFile(int from, int to) {
        Path path = Paths.get(csvFile);
        Stream<String> line;
        try (Stream<String> lines = Files.lines(path)) {
            line = lines.skip(from);
            Iterator<String> iter = line.iterator();
            try (FileWriter writer = new FileWriter(name)) {
                for (int i = 1; i < to; i++) {
                    writer.write(i + "," + iter.next() + "\n");
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
