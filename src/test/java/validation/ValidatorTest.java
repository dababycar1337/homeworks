package validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import validation.annotation.Email;
import validation.annotation.NotNull;
import validation.annotation.Range;
import validation.annotation.Size;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    static class ValidUser {
        @NotNull(message = "Имя обязательно")
        @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
        private String name;

        @NotNull(message = "Email обязателен")
        @Email(message = "Некорректный email")
        private String email;

        @Range(min = 0, max = 150, message = "Возраст должен быть от 0 до 150")
        private Integer age;

        ValidUser(String name, String email, Integer age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
    }

    static class SizeModel {
        @Size(min = 3, max = 5, message = "size")
        private String value;

        SizeModel(String value) {
            this.value = value;
        }
    }

    static class RangeModel {
        @Range(min = 0, max = 150, message = "range")
        private Integer value;

        RangeModel(Integer value) {
            this.value = value;
        }
    }

    static class EmailModel {
        @Email(message = "email")
        private String value;

        EmailModel(String value) {
            this.value = value;
        }
    }

    static class NotNullModel {
        @NotNull(message = "not null")
        private String value;

        NotNullModel(String value) {
            this.value = value;
        }
    }

    static class MultipleModel {
        @NotNull(message = "n")
        private String a;

        @Size(min = 2, max = 4, message = "s")
        private String b;

        @Range(min = 1, max = 3, message = "r")
        private Integer c;

        MultipleModel(String a, String b, Integer c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    static class BadSizeType {
        @Size(min = 1, max = 3, message = "s")
        private Integer value = 1;
    }

    static class BadRangeType {
        @Range(min = 1, max = 3, message = "r")
        private String value = "1";
    }

    static class BadEmailType {
        @Email(message = "e")
        private Integer value = 1;
    }

    static class BadSizeBounds {
        @Size(min = 5, max = 1, message = "s")
        private String value = "abc";
    }

    static class BadSizeNegativeMin {
        @Size(min = -1, max = 10, message = "s")
        private String value = "abc";
    }

    static class BadRangeBounds {
        @Range(min = 10, max = 1, message = "r")
        private Integer value = 5;
    }

    static class BadRangeNaN {
        @Range(min = Double.NaN, max = 10, message = "r")
        private Integer value = 5;
    }

    static class BadRangeInfinite {
        @Range(min = Double.NEGATIVE_INFINITY, max = 10, message = "r")
        private Integer value = 5;
    }

    static class Base {
        @NotNull(message = "base")
        private String inherited;

        Base(String inherited) {
            this.inherited = inherited;
        }
    }

    static class Derived extends Base {
        @NotNull(message = "own")
        private String own;

        @NotNull(message = "static")
        private static String staticField = null;

        Derived(String inherited, String own) {
            super(inherited);
            this.own = own;
        }
    }

    static class AllGood extends Base {
        AllGood(String inherited) {
            super(inherited);
        }
    }

    @Test
    void validObjectReturnsTrueAndEmptyErrors() {
        ValidUser user = new ValidUser("Alice", "alice@example.com", 30);
        ValidationResult result = Validator.validate(user);
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void notNullDetectsViolation() {
        ValidationResult result = Validator.validate(new NotNullModel(null));
        assertEquals(1, result.getErrors().size());
        ValidationError e = result.getErrors().get(0);
        assertEquals("value", e.fieldName());
        assertEquals("NotNull", e.annotationName());
        assertEquals("not null", e.message());
    }

    @Test
    void sizeDetectsViolation() {
        ValidationResult result = Validator.validate(new SizeModel("ab"));
        assertEquals(1, result.getErrors().size());
        assertEquals("Size", result.getErrors().get(0).annotationName());
    }

    @Test
    void rangeDetectsViolation() {
        ValidationResult result = Validator.validate(new RangeModel(200));
        assertEquals(1, result.getErrors().size());
        assertEquals("Range", result.getErrors().get(0).annotationName());
    }

    @Test
    void emailDetectsViolation() {
        ValidationResult result = Validator.validate(new EmailModel("invalid-email"));
        assertEquals(1, result.getErrors().size());
        assertEquals("Email", result.getErrors().get(0).annotationName());
    }

    @Test
    void collectsMultipleViolations() {
        ValidationResult result = Validator.validate(new MultipleModel(null, "a", 200));
        Set<String> annotations = result.getErrors().stream()
                .map(ValidationError::annotationName)
                .collect(Collectors.toSet());
        assertEquals(Set.of("NotNull", "Size", "Range"), annotations);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcde"})
    void sizeBoundariesPass(String value) {
        assertTrue(Validator.validate(new SizeModel(value)).isValid());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "abcdef"})
    void sizeOutOfBoundsFail(String value) {
        assertFalse(Validator.validate(new SizeModel(value)).isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 150})
    void rangeBoundariesPass(int value) {
        assertTrue(Validator.validate(new RangeModel(value)).isValid());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 151})
    void rangeOutOfBoundsFail(int value) {
        assertFalse(Validator.validate(new RangeModel(value)).isValid());
    }

    @Test
    void nullObjectThrowsNpe() {
        assertThrows(NullPointerException.class, () -> Validator.validate(null));
    }

    @Test
    void invalidSizeBoundsThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadSizeBounds()));
    }

    @Test
    void negativeSizeMinThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadSizeNegativeMin()));
    }

    @Test
    void invalidRangeBoundsThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadRangeBounds()));
    }

    @Test
    void nanRangeBoundThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadRangeNaN()));
    }

    @Test
    void infiniteRangeBoundThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadRangeInfinite()));
    }

    @Test
    void unsupportedSizeTypeThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadSizeType()));
    }

    @Test
    void unsupportedRangeTypeThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadRangeType()));
    }

    @Test
    void unsupportedEmailTypeThrowsConfigException() {
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new BadEmailType()));
    }

    @Test
    void unsupportedSizeTypeWithNullValueStillThrows() {
        class NullSizeOnInteger {
            @Size(min = 1, max = 3, message = "s")
            private Integer value = null;
        }
        assertThrows(ValidationConfigurationException.class,
                () -> Validator.validate(new NullSizeOnInteger()));
    }

    @Test
    void validatesInheritedAndPrivateFieldsAndIgnoresStatic() {
        ValidationResult result = Validator.validate(new Derived(null, null));
        Set<String> fields = result.getErrors().stream()
                .map(ValidationError::fieldName)
                .collect(Collectors.toSet());
        assertEquals(Set.of("inherited", "own"), fields);
    }

    @Test
    void inheritedValidObjectPasses() {
        assertTrue(Validator.validate(new AllGood("x")).isValid());
    }

    @Test
    void errorsListIsImmutable() {
        ValidationResult result = Validator.validate(new MultipleModel(null, "ab", 200));
        List<ValidationError> errors = result.getErrors();
        assertThrows(UnsupportedOperationException.class,
                () -> errors.add(new ValidationError("x", "y", "z")));
        assertThrows(UnsupportedOperationException.class,
                () -> errors.clear());
    }

    @ParameterizedTest
    @CsvSource({
            "user@example.com, true",
            "a@b.c, true",
            "invalid, false",
            "'', false",
            "'   ', false",
            "user @example.com, false",
            "user@, false",
            "@example.com, false",
            "user@example, false"
    })
    void emailFormat(String value, boolean expectedValid) {
        assertEquals(expectedValid, Validator.validate(new EmailModel(value)).isValid());
    }
}