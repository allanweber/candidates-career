package com.allanweber.candidatescareer.core.utilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrimmableTest {

    @Test
    void given_trimmable_class_should_trim_all_strings() {

        String oneSpace = " 1 space in between ";
        String twoSpaces = "  2 space in between  ";
        Sample sample = Sample.builder()
                .string1(oneSpace)
                .string2(twoSpaces)
                .number(1)
                .inner1(
                        Inner1.builder()
                                .string1(oneSpace)
                                .string2(twoSpaces)
                                .number(1)
                                .inner2(
                                        Inner1.builder()
                                                .string1(oneSpace)
                                                .string2(twoSpaces)
                                                .number(1)
                                                .build()
                                )
                                .build()
                )
                .build();

        assertEquals(oneSpace, sample.getString1());
        assertEquals(twoSpaces, sample.getString2());

        assertEquals(oneSpace, sample.getInner1().getString1());
        assertEquals(twoSpaces, sample.getInner1().getString2());

        assertEquals(oneSpace, sample.getInner1().getInner2().getString1());
        assertEquals(twoSpaces, sample.getInner1().getInner2().getString2());

        sample.trim();

        assertEquals(oneSpace.trim(), sample.getString1());
        assertEquals(twoSpaces.trim(), sample.getString2());

        assertEquals(oneSpace.trim(), sample.getInner1().getString1());
        assertEquals(twoSpaces.trim(), sample.getInner1().getString2());

        assertEquals(oneSpace.trim(), sample.getInner1().getInner2().getString1());
        assertEquals(twoSpaces.trim(), sample.getInner1().getInner2().getString2());
    }

    @AllArgsConstructor
    @Builder
    @Getter
    static class Sample implements Trimmable {
        private final String string1;
        private final String string2;
        private final Integer number;
        private final Inner1 inner1;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    static class Inner1 {
        private final String string1;
        private final String string2;
        private final Integer number;
        private final Inner1 inner2;
    }
}