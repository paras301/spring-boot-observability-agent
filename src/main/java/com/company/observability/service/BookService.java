package com.company.observability.service;

import com.company.observability.model.Book;
import com.company.observability.repository.BookRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public int countBooks() {
        return (int) bookRepository.count();
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    private void publishCustomMetric(String metricName, String description, Set<Tag> tags, double value) {
        Gauge.builder(metricName, () -> value)
                .description(description)
                .tags(tags)
                .register(io.micrometer.core.instrument.Metrics.globalRegistry);
    }

    private Set<Tag> convertMapToTags(java.util.Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> Tag.of(entry.getKey(), entry.getValue()))
                .collect(java.util.stream.Collectors.toSet());
    }

    @Scheduled(fixedDelay = 5000)
    public void publishMetrics() {
        publishBookCountMetric();
        publishBookCountByTitleMetric();
    }

    public void publishBookCountMetric() {
        int bookCount = countBooks();
        Set<Tag> tags = convertMapToTags(java.util.Map.of("env", "dev"));
        publishCustomMetric("book.count", "Count of books", tags, bookCount);
    }

    public void publishBookCountByTitleMetric() {
        List<Book> books = findAll();

        // Using Java streams to count occurrences
        Map<String, Long> titleCountMap = books.stream()
                .collect(Collectors.groupingBy(
                        Book::getTitle,
                        Collectors.counting()
                ));

        titleCountMap.forEach((title, count) -> {
            Set<Tag> tags = convertMapToTags(java.util.Map.of("env", "dev", "title", title));
            publishCustomMetric("book.count.by.title", "Count of books by title", tags, count);
        });
    }

}

