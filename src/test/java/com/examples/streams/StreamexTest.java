package com.examples.streams;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
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

        assertThat(StreamEx.of("a", "b").anyMatch("a"::equals)).isTrue();
        assertThat(StreamEx.of("a", "b").anyMatch("c"::equals)).isFalse();
        assertThat(StreamEx.of("a", "b").allMatch("a"::equals)).isFalse();
        assertThat(StreamEx.of("a", "b").allMatch("c"::equals)).isFalse();
        assertThat(StreamEx.of("a", "b").noneMatch("a"::equals)).isFalse();
        assertThat(StreamEx.of("a", "b").noneMatch("c"::equals)).isTrue();
        assertThat(StreamEx.of().noneMatch("a"::equals)).isTrue();
        assertThat(StreamEx.of().allMatch("a"::equals)).isTrue();
        assertThat(StreamEx.of().anyMatch("a"::equals)).isFalse();

        assertThat("abbccc").isEqualTo( StreamEx.of("a", "bb", "ccc").collect(StringBuilder::new, StringBuilder::append,
                StringBuilder::append).toString());
        assertThat(new String[] { "a", "b", "c" }).isEqualTo(StreamEx.of("a", "b", "c").toArray(String[]::new));
        assertThat(new Object[] { "a", "b", "c" }).isEqualTo(StreamEx.of("a", "b", "c").toArray());
        assertThat(3).isEqualTo(StreamEx.of("a", "b", "c").spliterator().getExactSizeIfKnown());

        assertThat(StreamEx.of("a", "b", "c").spliterator().hasCharacteristics(Spliterator.ORDERED)).isTrue();
        assertThat(StreamEx.of("a", "b", "c").unordered().spliterator().hasCharacteristics(Spliterator.ORDERED)).isFalse();
    }

    @Test
    public void testCovariance() {
        StreamEx<Number> stream = StreamEx.of(1, 2, 3);
        List<Number> list = stream.toList();
        assertThat(asList(1, 2, 3)).isEqualTo(list);

        StreamEx<Object> objStream = StreamEx.of(list.spliterator());
        List<Object> objList = objStream.toList();
        assertThat(asList(1, 2, 3)).isEqualTo(objList);
    }

    @Test
    public void testToList() {
        List<Integer> list = StreamEx.of(1, 2, 3).toList();
        // Test that returned list is mutable
        List<Integer> list2 = StreamEx.of(4, 5, 6).parallel().toList();
        list2.add(7);
        list.addAll(list2);
        assertThat(asList(1, 2, 3, 4, 5, 6, 7)).isEqualTo(list);
    }

    @Test
    public void testToArray() {
        Number[] numbers = StreamEx.of(1, 2, 3).toArray(Number.class);
        assertThat(new Number[] { 1, 2, 3 }).isEqualTo(numbers);
        assertThat(Number.class).isEqualTo(numbers.getClass().getComponentType());
        Integer[] emptyArray = {};
        assertThat(emptyArray).isEqualTo(StreamEx.of(1, 2, 3).filter(x -> x > 3).toArray(emptyArray));
        assertThat(new Integer[] { 1, 2, 3 }).isEqualTo(StreamEx.of(1, 2, 3).remove(x -> x > 3).toArray(emptyArray));
        assertThatThrownBy(() -> StreamEx.of().toArray(new Integer[1])).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testForEach() {
        List<Integer> list = new ArrayList<>();
        StreamEx.of(1, 2, 3).forEach(list::add);
        assertThat(asList(1, 2, 3)).isEqualTo(list);
        StreamEx.of(1, 2, 3).forEachOrdered(list::add);
        assertThat(asList(1, 2, 3, 1, 2, 3)).isEqualTo(list);
        StreamEx.of(1, 2, 3).parallel().forEachOrdered(list::add);
        assertThat(asList(1, 2, 3, 1, 2, 3, 1, 2, 3)).isEqualTo(list);
    }



}
