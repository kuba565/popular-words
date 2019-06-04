package pl.sii;


import pl.sii.service.OwnFileReader;

import java.util.*;

public class PopularWords {

    public static void main(String[] args) {
        PopularWords popularWords = new PopularWords();
        Map<String, Long> result = popularWords.findOneThousandMostPopularWords();
        result.entrySet().forEach(System.out::println);
    }

    Map<String, Long> findOneThousandMostPopularWords() {
        return findOneThousandMostPopularWords("src/main/resources/3esl.txt");
    }

    Map<String, Long> findOneThousandMostPopularWords(String path) {
        var lines = new OwnFileReader().readWords(path);
        Map<String, Long> wordsAndFrequency = new HashMap<>();

        lines.forEach(word -> {
            wordsAndFrequency.computeIfPresent(word, (k, v) -> v + 1);
            wordsAndFrequency.putIfAbsent(word, 1L);
        });

        var list = new LinkedList<>(wordsAndFrequency.entrySet());

        list.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));

        Map<String, Long> map = new LinkedHashMap<>();

        list.stream().limit(1000).forEach(e -> map.put(e.getKey(), e.getValue()));

        return map;
    }
}
