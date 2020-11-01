package com.examples.streams;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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
    }
}
