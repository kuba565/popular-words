package pl.sii.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class OwnFileReader {
    public List<String> readWords(String path) {
        List<String> lines = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(path); Scanner sc = new Scanner(inputStream)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().toLowerCase();

                if (line.isEmpty()) {
                    continue;
                }

                lines.add(line);

                Stream.of("-", " ").forEach(separator -> {
                    if (line.contains(separator)) {
                        Collections.addAll(lines, line.split(separator));
                    }
                });

                Stream.of("'s", "'m", "'ve", "'re", "'d", "'ll", "'t").forEach(contraction -> {
                    if (line.contains(contraction)) {
                        lines.add(contraction);
                        lines.add(line.split(contraction)[0]);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

}
