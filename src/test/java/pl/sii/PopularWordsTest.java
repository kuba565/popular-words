package pl.sii;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PopularWordsTest {
    private static final PopularWords testee = new PopularWords();

    @Test
    public void shouldReturnOneThousandMostPopularWords() throws IOException {
        //given
        var wordsFrequencyListCreatedByAdamKilgarriff = getWordsFrequencyListCreatedByAdamKilgarriff();

        //when
        var result = testee.findOneThousandMostPopularWords();

        //then
        assertFalse(result.isEmpty());
        assertEquals(1000, result.size());
        compareWordListsFrequency(wordsFrequencyListCreatedByAdamKilgarriff, result);
    }

    @Test
    public void shouldHandleHyphenSeparatedWords() {
        //when
        var result = testee.findOneThousandMostPopularWords("src/test/resources/shortlist.txt");

        //then
        assertEquals(6L, (long) result.get("asd"));
    }

    @Test
    public void shouldHandleApostropheSeparatedWords() {
        //when
        var result = testee.findOneThousandMostPopularWords("src/test/resources/shortlist.txt");

        //then
        assertThat(result.get("'s"), is(2L));
    }


    @Test
    public void shouldHandleComplexSeparators() {
        //when
        var result = testee.findOneThousandMostPopularWords("src/test/resources/shortlist.txt");

        //then
        assertThat(result.get("'s"), is(2L));
        assertThat(result.get("mak"), is(1L));
        assertThat(result.get("asd's"), is(2L));
        assertThat(result.get("asd"), is(6L));
        assertThat(result.get("asd's-mak"), is(1L));
        assertThat(result.get("'ll"), is(1L));
        assertThat(result.get("as't"), is(1L));
        assertThat(result.get("as"), is(1L));
    }

    @Test
    public void shouldHandleEmptyLines() {
        //when
        var result = testee.findOneThousandMostPopularWords("src/test/resources/shortlist.txt");

        //then
        assertThat(result.get(""), CoreMatchers.nullValue());
    }


    private void compareWordListsFrequency(Map<String, Long> wordsFrequencyListCreatedByAdamKilgarriff, Map<String, Long> result) {
        long totalFrequencyByKilgarriff = wordsFrequencyListCreatedByAdamKilgarriff.values().stream().reduce(0L, Long::sum);
        long totalFrequencyInAResult = result.values().stream().reduce(0L, Long::sum);
        System.out.println("totalFrequencyByKilgarriff = " + totalFrequencyByKilgarriff);
        System.out.println("totalFrequencyInAResult = " + totalFrequencyInAResult);

        result.forEach((key, value) -> {
            BigDecimal valueUsagePercentage = calculatePercentage(value, totalFrequencyInAResult);
            if (wordsFrequencyListCreatedByAdamKilgarriff.containsKey(key)) {
                BigDecimal kilgarriffUsagePercentage = calculatePercentage(wordsFrequencyListCreatedByAdamKilgarriff.get(key), totalFrequencyByKilgarriff);
                BigDecimal diff = kilgarriffUsagePercentage.subtract(valueUsagePercentage);
                System.out.println(key + "," + valueUsagePercentage + "%," + kilgarriffUsagePercentage + "%," + (new BigDecimal(0.5).compareTo(diff.abs()) > 0) + " " + diff);
            }
        });
    }

    private BigDecimal calculatePercentage(double obtained, double total) {
        return new BigDecimal(obtained * 100 / total).setScale(4, RoundingMode.HALF_UP);
    }

    private Map<String, Long> getWordsFrequencyListCreatedByAdamKilgarriff() throws IOException {
        Map<String, Long> result = new HashMap<>();

        List<String> lines = Files.readAllLines(Path.of("src/test/resources/all.num"));

        lines.stream().skip(1).forEach(line -> {
            String[] data = line.split(" ");
            Long occurrences = Long.valueOf(data[0]);
            String word = data[1];
            result.put(word, occurrences);
        });

        return result;
    }
}
