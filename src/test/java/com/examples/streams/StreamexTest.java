package com.examples.streams;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StreamexTest {

    @Test
    public void testCreate() {
        assertThat(asList()).isEqualTo(StreamEx.empty().toList());
        assertThat(asList("a")).isEqualTo(StreamEx.of("a").toList());
        assertThat(asList("a")).isEqualTo(StreamEx.of(Optional.of("a")).toList());
        assertThat(asList()).isEqualTo(StreamEx.of(Optional.ofNullable(null)).toList());
        assertThat(asList()).isEqualTo(StreamEx.ofNullable(null).toList());
        assertThat(asList("a")).isEqualTo(StreamEx.ofNullable("a").toList());
        assertThat(asList("a")).isEqualTo(StreamEx.ofNullable("a").toList());
        assertThat(asList((String) null)).isEqualTo(StreamEx.of((String) null).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.of("a", "b").toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.of(asList("a", "b")).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.of(Stream.of("a", "b")).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.split("a,b", ",").toList());
        assertThat(asList("a", "c", "d")).isEqualTo(StreamEx.split("abcBd", Pattern.compile("b", Pattern.CASE_INSENSITIVE))
                .toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.ofLines(new StringReader("a\nb")).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.ofLines(new BufferedReader(new StringReader("a\nb"))).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.ofLines(getReader()).toList());
        assertThat(asList("a", "a", "a", "a")).isEqualTo(StreamEx.generate(() -> "a").limit(4).toList());
        assertThat(asList("a", "a", "a", "a")).isEqualTo( StreamEx.constant("a", 4).toList());
        assertThat(asList("c", "d", "e")).isEqualTo(StreamEx.of("abcdef".split(""), 2, 5).toList());
        assertThat(asList("a1", "b2", "c3")).isEqualTo(StreamEx.zip(asList("a", "b", "c"), asList(1, 2, 3), (s, i) -> s + i).toList());
        assertThat(asList("a1", "b2", "c3")).isEqualTo(StreamEx.zip(new String[]{"a", "b", "c"}, new Integer[]{1, 2, 3}, (s, i) -> s + i).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.of(asList("a", "b").spliterator()).toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.of(asList("a", "b").iterator()).toList());
        assertThat(asList()).isEqualTo(StreamEx.of(Collections.emptyIterator()).toList());
        assertThat(asList()).isEqualTo(StreamEx.of(Collections.emptyIterator()).parallel().toList());
        assertThat(asList("a", "b")).isEqualTo(StreamEx.of(new Vector<>(asList("a", "b")).elements()).toList());
        assertThat(asList("a", "b", "c", "d")).isEqualTo(StreamEx.ofReversed(asList("d", "c", "b", "a")).toList());
        assertThat(asList("a", "b", "c", "d")).isEqualTo(StreamEx.ofReversed(new String[]{"d", "c", "b", "a"}).toList());
    }

    private static Reader getReader() {
        return new BufferedReader(new StringReader("a\nb"));
    }

    @Test
    public void testBasics() {
        assertThat(StreamEx.of("a").isParallel()).isFalse();
        assertThat(StreamEx.of("a").parallel().isParallel()).isTrue();
        assertThat(StreamEx.of("a").parallel().sequential().isParallel()).isFalse();
        AtomicInteger i = new AtomicInteger();
        try (Stream<String> s = StreamEx.of("a").onClose(i::incrementAndGet)) {
            assertThat(1).isEqualTo(s.count());
        }

        assertThat(asList(1, 2)).isEqualTo(StreamEx.of("a", "bb").map(String::length).toList());
        assertThat(StreamEx.empty().findAny().isPresent()).isFalse();

        assertThat("a").isEqualTo(StreamEx.of("a").findAny().get());
        assertThat(StreamEx.empty().findFirst().isPresent()).isFalse();
        assertThat("a").isEqualTo(StreamEx.of("a", "b").findFirst().get());
        assertThat(asList("b", "c")).isEqualTo(StreamEx.of("a", "b", "c").skip(1).toList());

    }
}
