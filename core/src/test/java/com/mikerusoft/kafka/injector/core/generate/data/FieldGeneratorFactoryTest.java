package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.properties.Field;
import com.mikerusoft.kafka.injector.core.properties.GeneratorType;
import com.mikerusoft.kafka.injector.core.utils.Pair;
import lombok.Data;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.mikerusoft.kafka.injector.core.generate.data.FieldGeneratorFactoryTest.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FieldGeneratorFactoryTest {

    @Data
    static class Value {
        private String value;
    }

    @Data
    static class AnotherValue {
        private Value anotherValue;
    }

    @Nested
    class NestedGeneratorTest {

        @Test
        void withNestedObjectInsideNestedList_withoutNestedFieldsInObject_expectedRuntimeException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () ->
                    FieldGeneratorFactory.generateFieldValue(
                        Field.builder().type(GeneratorType.NESTED_LIST).cast(Value.class).nestedFields(new Field[] {
                            Field.builder().type(GeneratorType.NESTED_OBJECT).cast(Value.class).build()
                        }).build()
                    )
            );
        }

        @Test
        void withNestedObject_whenNestedFieldsEmpty_expectedRuntimeException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () ->
                    FieldGeneratorFactory.generateFieldValue(Field.builder().type(GeneratorType.NESTED_OBJECT)
                            .cast(Value.class).build())
            );
        }

        @Test
        void withNestedList_whenNestedFieldsEmpty_expectedRuntimeException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () ->
                    FieldGeneratorFactory.generateFieldValue(Field.builder().type(GeneratorType.NESTED_LIST)
                            .cast(Value.class).build())
            );
        }

        @Test
        void withStringInsideObjectWhichIsInsideList_expectedListOfObjectOfString() {
            /*
            - name: "someMethodInMainBuilder"
            type: "nested_object"
            cast: "nested_list"
            nestedFields:
                - name: "add"
                type: "nested_object"
                cast: Value
                nestedFields:
                    - name: "setValue"
                    type: fixed
                    cast: java.lang.String
                    value: "Hello
            */
            Field field = Field.builder().type(GeneratorType.NESTED_LIST).cast(Value.class).nestedFields(new Field[] {
                    Field.builder().type(GeneratorType.NESTED_OBJECT).cast(Value.class).nestedFields(new Field[]{
                            Field.builder().name("setValue").cast(String.class).type(GeneratorType.FIXED).value("Hello").build()
                    }).build()
            }).build();
            Object o = FieldGeneratorFactory.generateFieldValue(field);
            assertThat(o).isNotNull().isInstanceOf(List.class);
            List list = (List) o;
            assertThat(list).hasSize(1);
            assertThat(list.get(0)).isNotNull().isInstanceOf(Value.class);
            Value value = (Value) list.get(0);
            assertThat(value.getValue()).isNotBlank().isEqualTo("Hello");
        }

        @Test
        void withListOfFieldsOfString_expectedGeneratedListOfStringsWithSize1() {
            /*
            - name: "someMethodInMainBuilder"
            type: "nested_list"
            cast: "String"
            nestedFields:
                - name: "setAnotherValues"
                type: "nested_object"
                cast: "String"
                value: "Hello"
            */
            Field field = Field.builder().cast(String.class).type(GeneratorType.NESTED_LIST).nestedFields(new Field[] {
                Field.builder().cast(String.class).type(GeneratorType.FIXED).value("Hello").build()
            }).build();
            Object o = FieldGeneratorFactory.generateFieldValue(field);
            assertThat(o).isNotNull().isInstanceOf(List.class);
            List list = (List) o;
            assertThat(list).hasSize(1);
            assertThat(list.get(0)).isNotNull().isInstanceOf(String.class).isEqualTo("Hello");
        }

        @Test
        void withObjectInsideObjectAndString_expectedGeneratedObjectOfObjectWithString() {

            /*
            - name: "someMethodInMainBuilder"
            type: "nested_object"
            cast: "AnotherValue"
            nestedFields:
                - name: "setAnotherValue"
                type: "nested_object"
                cast: Value
                nestedFields:
                    - name: "setValue"
                    type: fixed
                    cast: java.lang.String
                    value: "Hello
            */
            Field field = Field.builder().cast(AnotherValue.class).type(GeneratorType.NESTED_OBJECT).nestedFields(new Field[] {
                Field.builder().name("setAnotherValue").type(GeneratorType.NESTED_OBJECT).cast(Value.class).nestedFields(new Field[]{
                    Field.builder().name("setValue").cast(String.class).type(GeneratorType.FIXED).value("Hello").build()
                }).build()
            }).build();
            Object o = FieldGeneratorFactory.generateFieldValue(field);
            assertThat(o).isNotNull().isInstanceOf(AnotherValue.class);
            Value value = ((AnotherValue)o).getAnotherValue();
            assertThat(value).isNotNull();
            assertThat(value.getValue()).isNotEmpty().isEqualTo("Hello");
        }
    }

    @Nested
    class RegexGeneratorTest {

        @Test
        void whenRegexIsStringAndCastToInt_expectedNumberFormatException() {
            assertThrows(
                NumberFormatException.class,
                () -> {
                    Field field = Field.builder().cast(Integer.class).name("siteIdField").type(GeneratorType.REGEX).value("[a-z]").build();
                    FieldGeneratorFactory.generateFieldValue(field);
                }
            );
        }

        @Test
        void whenRegexNotSet_expectedNumberFormatException() {
            assertThrows(
                NumberFormatException.class,
                () -> {
                    Field field = Field.builder().cast(Integer.class).name("siteIdField").type(GeneratorType.REGEX).value("").build();
                    FieldGeneratorFactory.generateFieldValue(field);
                }
            );
        }

        @Test
        void whenRegexEmpty_expectedNumberFormatException() {
            assertThrows(
                NumberFormatException.class,
                () -> {
                    Field field = Field.builder().cast(Integer.class).name("siteIdField").value("").type(GeneratorType.REGEX).build();
                    FieldGeneratorFactory.generateFieldValue(field);
                }
            );
        }

        @Test
        void whenRegexNumbersAndIntType_expectedGeneratedValueInRange_loop() {
            IntStream.range(0, 100).mapToObj(i -> Field.builder()
                .cast(Integer.TYPE).name("siteIdField").type(GeneratorType.REGEX).value("[0-9]{1,2}").build()
            ).map(f -> FieldGeneratorFactory.generateFieldValue(f)).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Integer.class);
                assertRange((int)value, 0, 100);
            });
        }

        @Test
        void whenRegexNumbersAndLongType_expectedGeneratedValueInRange_loop() {
            IntStream.range(0, 100).mapToObj(i -> Field.builder()
                .cast(Long.class).name("siteIdField").type(GeneratorType.REGEX).value("([1-9])([0-9]{2,2})").build()
            ).map(f -> FieldGeneratorFactory.generateFieldValue(f)).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Long.class);
                assertRange((long)value, 100, 1000);
            });
        }
    }

    @Nested
    class ValueFromListGeneratorTest {

        @Test
        void whenValueIsNullCastToInt_expectedNumberFormatException() {
            assertThrows(
                NumberFormatException.class,
                () -> {
                    String str = null;
                    ValueFromListGenerator valueFromListGenerator = new ValueFromListGenerator<>(Integer.class, str);
                    valueFromListGenerator.generate();
                }
            );
        }

        @Test
        void whenValueIsNullCastToString_expectedAlwaysEmptyString() {
            String str = null;
            ValueFromListGenerator valueFromListGenerator = new ValueFromListGenerator<>(String.class, str);
            IntStream.range(0,2).mapToObj(i -> valueFromListGenerator.generate())
                .forEach(generated -> assertThat(generated).isNotNull().isEqualTo(""));
        }

        @Test
        void whenValueIsEmptyString_expectedAlwaysEmptyString() {
            ValueFromListGenerator valueFromListGenerator = new ValueFromListGenerator<>(String.class, "");
            IntStream.range(0,2).mapToObj(i -> valueFromListGenerator.generate())
                .forEach(generated -> assertThat(generated).isNotNull().isEqualTo(""));
        }

        @Test
        void whenCastString_expectedSerialGenerationOfValues() {
            ValueFromListGenerator valueFromListGenerator = new ValueFromListGenerator<>(String.class, "0,1");
            IntStream.range(0,3).mapToObj(i -> Pair.of(String.valueOf(i%2), valueFromListGenerator.generate()))
                    .forEach(p -> assertThat(p.getRight()).isNotNull().isEqualTo(p.getLeft()));
        }

        @Test
        void whenCastInt_expectedSerialGenerationOfValues() {
            ValueFromListGenerator valueFromListGenerator = new ValueFromListGenerator<>(Integer.class, "0,1");
            IntStream.range(0,3).mapToObj(i -> Pair.of(i%2, valueFromListGenerator.generate()))
                    .forEach(p -> assertThat(p.getRight()).isNotNull().isEqualTo(p.getLeft()));
        }

        @Test
        void withListSize6_whenCastInt_expectedSerialGenerationOfValues() {
            ValueFromListGenerator valueFromListGenerator = new ValueFromListGenerator<>(Integer.class, "0,1,2,3,4,5");
            IntStream.range(0,10).mapToObj(i -> Pair.of(i%6, valueFromListGenerator.generate()))
                    .forEach(p -> assertThat(p.getRight()).isNotNull().isEqualTo(p.getLeft()));
        }
    }

    @Nested
    class RandomValueFromListTest {

        @Test
        void whenValueIsNullCastToInt_expectedNumberFormatException() {
            assertThrows (
                NumberFormatException.class,
                () -> {
                    String str = null;
                    RandomValueFromValueFromList valueFromListGenerator = new RandomValueFromValueFromList<>(Integer.class, str);
                    valueFromListGenerator.generate();
                }
            );
        }

        @Test
        void whenValueIsNullCastToString_expectedAlwaysEmptyString() {
            org.junit.jupiter.api.Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new RandomValueFromValueFromList<>(String.class, (String)null)
            );
        }

        @Test
        void whenValueIsEmptyString_expectedAlwaysEmptyString() {
            RandomValueFromValueFromList valueFromListGenerator = new RandomValueFromValueFromList<>(String.class, "");
            IntStream.range(0,2).mapToObj(i -> valueFromListGenerator.generate())
                    .forEach(generated -> assertThat(generated).isNotNull().isEqualTo(""));
        }

        @Test
        void when2ValuesCastToString_expectedOnly1And2() {
            RandomValueFromValueFromList listGenerator = new RandomValueFromValueFromList<>(String.class, "1,2");
            IntStream.range(0, 4).mapToObj(i -> listGenerator.generate()).forEach(v -> {
                assertThat(v).isNotNull().isInstanceOf(String.class).isIn("1", "2");
            });
        }

        @Test
        void when2ValuesCastToInt_expectedOnly1And2() {
            RandomValueFromValueFromList listGenerator = new RandomValueFromValueFromList<>(Integer.class, "1,2");
            IntStream.range(0, 4).mapToObj(i -> listGenerator.generate()).forEach(v -> {
                assertThat(v).isNotNull().isInstanceOf(Integer.class).isIn(1, 2);
            });
        }
    }

    @Nested
    class NullGeneratorTest {

        @Test
        void whenNullValueGenerator_expectedAlwaysReturnsNull() {
            NullValueGenerator nvg = new NullValueGenerator();
            IntStream.range(0, 7).mapToObj(i -> nvg.generate()).forEach(v -> assertThat(v).isNull());
        }
    }

    @Nested
    class FixedValueGeneratorTest {

        @Test
        void whenStringNull_expectedAlwaysReturnsNull() {
            String str = null;
            FixedValueGenerator fvg = new FixedValueGenerator<>(str, String.class);
            IntStream.range(0, 7).mapToObj(i -> fvg.generate()).forEach(v -> assertThat(v).isNull());
        }

        @Test
        void whenStringValue_expectedAlwaysReturnsNull() {
            FixedValueGenerator fvg = new FixedValueGenerator<>("1", String.class);
            IntStream.range(0, 7).mapToObj(i -> fvg.generate()).forEach(v -> assertThat(v).isNotNull().isEqualTo("1"));
        }

        @Test
        void whenIntegerValue_expectedAlwaysReturnsNull() {
            FixedValueGenerator fvg = new FixedValueGenerator<>(1, Integer.class);
            IntStream.range(0, 7).mapToObj(i -> fvg.generate()).forEach(v -> assertThat(v).isNotNull().isEqualTo(new Integer(1)));
        }

        @Test
        void whenPrimitiveIntValue_expectedAlwaysReturnsNull() {
            FixedValueGenerator fvg = new FixedValueGenerator<>(1, Integer.TYPE);
            IntStream.range(0, 7).mapToObj(i -> fvg.generate()).forEach(v -> assertThat(v).isNotNull().isEqualTo(1));
        }

        @Test
        void withStringAsParameter_whenPrimitiveIntValue_expectedAlwaysReturnsNull() {
            FixedValueGenerator fvg = new FixedValueGenerator<>("1", Integer.TYPE);
            IntStream.range(0, 7).mapToObj(i -> fvg.generate()).forEach(v -> assertThat(v).isNotNull().isEqualTo(1));
        }
    }

    @Nested
    class PrimitiveIntNumberRangeGeneratorTest {

        @Test
        void whenInvalidRange_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PrimitiveIntNumberRangeGenerator.factory(Long.class, "15,10")
            );
        }

        @Test
        void whenLongRange_expectedGeneratedInRange() {
            PrimitiveNumberRangeGenerator prir = PrimitiveIntNumberRangeGenerator.factory(Long.class, "10,15");
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Long.class);
                assertRange((long)value, 10, 15);
            });
        }

        @Test
        void whenIntRange_expectedGeneratedInRange() {
            PrimitiveNumberRangeGenerator prir = PrimitiveIntNumberRangeGenerator.factory(Integer.class, "10,15");
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Integer.class);
                assertRange((int)value, 10, 15);
            });
        }
    }

    @Nested
    class FloatingPrimitiveNumberRangeGeneratorTest {

        @Test
        void whenInvalidRange_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PrimitiveIntNumberRangeGenerator.factory(Long.class, "15,10")
            );
        }

        @Test
        void whenLongRange_expectedGeneratedInRange() {
            PrimitiveNumberRangeGenerator prir = PrimitiveIntNumberRangeGenerator.factory(Double.class, "10,15");
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Double.class);
                assertRange(value.doubleValue(), 10, 15);
            });
        }

        @Test
        void whenIntRange_expectedGeneratedInRange() {
            PrimitiveNumberRangeGenerator prir = PrimitiveIntNumberRangeGenerator.factory(Float.class, "10,15");
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Float.class);
                assertRange(value.doubleValue(), 10, 15);
            });
        }
    }

    @Nested
    class SequentialRangeGeneratorTest {

        @Test
        void whenPrimitiveIntAndValidRange_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SequentialRangeGenerator<>("1,3", Integer.TYPE)
            );
        }

        @Test
        void whenPrimitiveLong_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SequentialRangeGenerator<>("1,4", Long.TYPE)
            );
        }

        @Test
        void whenNonNumericValueInRange_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SequentialRangeGenerator<>("fasfasfa", Long.class)
            );
        }

        @Test
        void whenOnlyOneValueInRange_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new SequentialRangeGenerator<>("1", Long.class)
            );
        }

        @Test
        void withLong_whenRange_expectedGeneratedSequentialValues() {
            SequentialRangeGenerator<Long> generate = new SequentialRangeGenerator<>("2,100000", Long.class);
            LongStream.range(2, 100000).forEach(i -> {
                Long value = generate.generate();
                assertThat(value).isNotNull().isInstanceOf(Long.class);
                assertEquals(value.longValue(), i);
            });
        }

        @Test
        void withInt_whenRange_expectedGeneratedSequentialValues() {
            SequentialRangeGenerator<Integer> generate = new SequentialRangeGenerator<>("1,3", Integer.class);
            IntStream.range(1, 3).forEach(i -> {
                Integer value = generate.generate();
                assertThat(value).isNotNull().isInstanceOf(Integer.class);
                assertEquals(value.intValue(), i);
            });
        }

        @Test
        void withFactory_whenCastToLongTYPEAndValidRange_expectedGeneratedSequentialValues() {
            ValueGenerator<?> generator = FieldGeneratorFactory.valueGenerator(
                Field.builder().cast(Long.TYPE).name("setValue").type(GeneratorType.SEQUENTIAL_RANGE).value("1,3").build()
            );
            IntStream.range(1, 3).forEach(i -> {
                Object value = generator.generate();
                assertThat(value).isNotNull().isInstanceOf(Long.class);
                assertEquals( ((Long)value).intValue(), i);
            });
        }

    }

    @Nested
    class RandomPrimitiveValueGeneratedTest {

        @Test
        void whenInt_expectedGeneratedRandomInt() {
            RandomPrimitiveValueGenerated prir = new RandomPrimitiveValueGenerated(Integer.class);
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Integer.class);
                assertRange(value.intValue(), 0, Integer.MAX_VALUE);
            });
        }

        @Test
        void whenLong_expectedGeneratedRandomLong() {
            RandomPrimitiveValueGenerated prir = new RandomPrimitiveValueGenerated(Long.class);
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Long.class);
                assertRange(value.longValue(), 0, Long.MAX_VALUE);
            });
        }

        @Test
        void whenDouble_expectedGeneratedRandomDouble() {
            RandomPrimitiveValueGenerated prir = new RandomPrimitiveValueGenerated(Double.class);
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Double.class);
                assertRange(value.doubleValue(), 0, 1);
            });
        }

        @Test
        void whenFloat_expectedGeneratedRandomFloat() {
            RandomPrimitiveValueGenerated prir = new RandomPrimitiveValueGenerated(Float.class);
            IntStream.range(0, 7).mapToObj(i -> prir.generate()).forEach(value -> {
                assertThat(value).isNotNull().isInstanceOf(Float.class);
                assertRange(value.floatValue(), 0, 1);
            });
        }
    }

    @Nested
    class TimestampGeneratorTest {

        @Test
        void whenValueIsNull_expectedCurrentTimestamp() {
            long now = System.currentTimeMillis() - 1000;
            long generated = new TimestampGenerator(null).generate();
            assertsGreater(generated, now, false);
        }

        @Test
        void whenValueIsEmpty_expectedCurrentTimestamp() {
            long now = System.currentTimeMillis() - 1000;
            long generated = new TimestampGenerator(null).generate();
            assertsGreater(generated, now, false);
        }

        @Test
        void whenInvalidDateFormat_expectedDateTimeParseException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                DateTimeParseException.class,
                () -> new TimestampGenerator("blabla").generate()
            );
        }

        @Test
        void withValidDateFormat_whenFormatIsNotSupportedByApp_expectedDateTimeParseException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                    DateTimeParseException.class,
                    () -> new TimestampGenerator("2018-07-11 12:30").generate()
            );
        }

        @Test
        void whenValidDateAndAppFormat_expectedGeneratedValidDate() throws Exception {
            long expected = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2018-07-11 12:30:45.123").getTime();
            long generated = new TimestampGenerator("2018-07-11 12:30:45.123").generate();
            assertEquals(expected, generated);
        }
    }

    @Nested
    class RegexTimestampGeneratorTest {

        @Test
        void whenValueIsNull_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new RegexTimestampGenerator(null)
            );
        }

        @Test
        void whenValueIsEmpty_expectedIllegalArgumentException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new RegexTimestampGenerator("")
            );
        }

        @Test
        void whenNonRegexTextInvalidDateFormat_expectedDateTimeParseException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                DateTimeParseException.class,
                () -> new RegexTimestampGenerator("blabla").generate()
            );
        }

        @Test
        void withValidDateFormat_whenFormatIsNotSupportedByApp_expectedDateTimeParseException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                DateTimeParseException.class,
                () -> new RegexTimestampGenerator("2018\\-07\\-11 12\\:30").generate()
            );
        }

        @Test
        void withRegex_whenValidDateAndAppFormat_expectedGeneratedValidDate() throws Exception {
            long expectedLess = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2018-07-11 12:30:45.000").getTime();
            long expectedMore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2018-07-11 12:30:45.999").getTime();

            IntStream.range(0, 3).forEach(i -> {
                long generated = new RegexTimestampGenerator("2018\\-07\\-11 12\\:30\\:45\\.[0-9]{3,3}").generate();
                assertsLess(expectedLess, generated, true);
                assertsGreater(expectedMore, generated, true);
            });
        }

        @Test
        void withNonRegex_whenValidDateAndAppFormat_expectedGeneratedValidDate() throws Exception {
            long expected = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2018-07-11 12:30:45.123").getTime();
            long generated = new RegexTimestampGenerator("2018\\-07\\-11 12\\:30\\:45\\.123").generate();
            assertEquals(expected, generated);
        }
    }

    @Nested
    class FactoryTest {

        @Test
        void typeEmpty_expectedFixedValueGenerator() {
            assertThat(
                FieldGeneratorFactory.valueGenerator(
                    Field.builder().cast(Integer.class).name("siteIdField").type(GeneratorType.EMPTY).value("[a-z]").build()
                )
            ).isNotNull().isInstanceOf(FixedValueGenerator.class);
        }

        @Test
        void typeRegex_expectedRegexGenerator() {
            assertThat(
                FieldGeneratorFactory.valueGenerator(
                    Field.builder().cast(Integer.class).name("siteIdField").type(GeneratorType.REGEX).value("[a-z]").build()
                )
            ).isNotNull().isInstanceOf(RegexGenerator.class);
        }

        @Test
        void typeRangeCastInt_expectedPrimitiveIntNumberRangeGenerator() {
            assertThat(
                FieldGeneratorFactory.valueGenerator(
                    Field.builder().cast(Integer.class).name("siteIdField").type(GeneratorType.RANGE).value("10,15").build()
                )
            ).isNotNull().isInstanceOf(PrimitiveNumberRangeGenerator.class).isInstanceOf(PrimitiveIntNumberRangeGenerator.class);
        }

        @Test
        void typeRangeCastFloat_expectedPrimitiveIntNumberRangeGenerator() {
            assertThat(
                FieldGeneratorFactory.valueGenerator(
                    Field.builder().cast(Float.class).name("siteIdField").type(GeneratorType.RANGE).value("10,15").build()
                )
            ).isNotNull().isInstanceOf(PrimitiveNumberRangeGenerator.class).isInstanceOf(FloatingPrimitiveNumberRangeGenerator.class);
        }

        @Test
        void whenUsingTheSameFieldExactly_expectedSameValueGenerator() {
            Field field = Field.builder().cast(Float.class).name("siteIdField").type(GeneratorType.RANGE).value("10,15").build();
            ValueGenerator<?> valueGenerator1 = FieldGeneratorFactory.valueGenerator(field);
            ValueGenerator<?> valueGenerator2 = FieldGeneratorFactory.valueGenerator(field);
            assertThat(valueGenerator2).isNotNull();
            assertThat(valueGenerator1).isNotNull().isSameAs(valueGenerator2);
        }

        @Test
        void whenUsingDifferentFieldWithSameValues_expectedDifferentGenerators() {
            Field field1 = Field.builder().cast(Float.class).name("siteIdField").type(GeneratorType.RANGE).value("10,15").build();
            Field field2 = Field.builder().cast(Float.class).name("siteIdField").type(GeneratorType.RANGE).value("10,15").build();
            ValueGenerator<?> valueGenerator1 = FieldGeneratorFactory.valueGenerator(field1);
            ValueGenerator<?> valueGenerator2 = FieldGeneratorFactory.valueGenerator(field2);
            assertThat(valueGenerator2).isNotNull();
            assertThat(valueGenerator1).isNotNull().isNotSameAs(valueGenerator2);
        }
    }

    static class Assertions {
        static void assertsLess(long actual, long comparedTo, boolean includeEquals) {
            long result = actual - comparedTo;
            if (result == 0 && !includeEquals) {
                throw new AssertionFailedError("Value " + actual + " is equal to " + comparedTo,
                        actual + "<" + comparedTo , actual + "=" + comparedTo);
            }

            if (result > 0) {
                throw new AssertionFailedError("Value " + actual + " less then " + comparedTo,
                        actual + "<" + (includeEquals ? "=" : "") + comparedTo , actual + ">" + (includeEquals ? "=" : "") + comparedTo);
            }
        }

        static void assertsGreater(long actual, long comparedTo, boolean includeEquals) {
            long result = actual - comparedTo;
            if (result == 0 && !includeEquals) {
                throw new AssertionFailedError("Value " + actual + " is equal to " + comparedTo,
                        actual + ">" + comparedTo , actual + "=" + comparedTo);
            }

            if (result < 0) {
                throw new AssertionFailedError("Value " + actual + " greater then " + comparedTo,
                        actual + ">" + (includeEquals ? "=" : "") + comparedTo , actual + "<" + (includeEquals ? "=" : "") + comparedTo);
            }
        }

        static void assertRange(long value, long min_inclusive, long max_exclusive) {
            if (value >= max_exclusive || value < min_inclusive)
                throw new AssertionFailedError("Should be between " + min_inclusive + "(inclusive) and " + max_exclusive + " (exclusive), but actual " + value,
                        "[" + min_inclusive + " .. " + max_exclusive + "]", String.valueOf(value));
        }

        static void assertRange(double value, double min_exclusive, double max_exclusive) {
            if (value >= max_exclusive || value <= min_exclusive)
                throw new AssertionFailedError("Should be between " + min_exclusive + " and " + max_exclusive + " (exclusive), but actual " + value,
                        "[" + min_exclusive + " .. " + max_exclusive + "]", String.valueOf(value));
        }
    }
}