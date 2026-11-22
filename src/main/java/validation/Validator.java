package validation;

import validation.annotation.Email;
import validation.annotation.NotNull;
import validation.annotation.Range;
import validation.annotation.Size;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Validator {

    private Validator() {
    }

    public static ValidationResult validate(Object object) {
        Objects.requireNonNull(object, "object must not be null");

        List<ValidationError> errors = new ArrayList<>();

        Class<?> clazz = object.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isSynthetic()) continue;
                if (Modifier.isStatic(field.getModifiers())) continue;

                boolean hasAnyConstraint =
                        field.isAnnotationPresent(NotNull.class)
                                || field.isAnnotationPresent(Size.class)
                                || field.isAnnotationPresent(Range.class)
                                || field.isAnnotationPresent(Email.class);

                if (!hasAnyConstraint) continue;

                Object value = readField(field, object, clazz);
                validateField(field, value, errors);
            }
            clazz = clazz.getSuperclass();
        }

        return new ValidationResult(errors);
    }

    private static Object readField(Field field, Object object, Class<?> declaringClass) {
        try {
            if (!field.trySetAccessible()) {
                throw new ValidationConfigurationException(
                        "Field '" + field.getName() + "' of class '"
                                + declaringClass.getName() + "' is not accessible");
            }
        } catch (RuntimeException ex) {
            throw new ValidationConfigurationException(
                    "Cannot make field '" + field.getName() + "' of class '"
                            + declaringClass.getName() + "' accessible", ex);
        }
        try {
            return field.get(object);
        } catch (IllegalAccessException ex) {
            throw new ValidationConfigurationException(
                    "Cannot read field '" + field.getName() + "' of class '"
                            + declaringClass.getName() + "'", ex);
        }
    }

    private static void validateField(Field field, Object value, List<ValidationError> errors) {
        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null && value == null && !field.getType().isPrimitive()) {
            errors.add(new ValidationError(field.getName(), "NotNull", notNull.message()));
        }

        Size size = field.getAnnotation(Size.class);
        if (size != null) {
            validateSize(field, value, size, errors);
        }

        Range range = field.getAnnotation(Range.class);
        if (range != null) {
            validateRange(field, value, range, errors);
        }

        Email email = field.getAnnotation(Email.class);
        if (email != null) {
            validateEmail(field, value, email, errors);
        }
    }

    private static void validateSize(Field field, Object value, Size size, List<ValidationError> errors) {
        Class<?> type = field.getType();
        if (!CharSequence.class.isAssignableFrom(type)) {
            throw new ValidationConfigurationException(
                    "@Size is not supported for field '" + field.getName()
                            + "' of type " + type.getName());
        }
        if (size.min() < 0 || size.min() > size.max()) {
            throw new ValidationConfigurationException(
                    "Invalid @Size bounds on field '" + field.getName()
                            + "': min=" + size.min() + ", max=" + size.max());
        }
        if (value == null) return;

        int length = ((CharSequence) value).length();
        if (length < size.min() || length > size.max()) {
            errors.add(new ValidationError(field.getName(), "Size", size.message()));
        }
    }

    private static void validateRange(Field field, Object value, Range range, List<ValidationError> errors) {
        Class<?> type = field.getType();
        boolean numeric = type.isPrimitive()
                ? type != boolean.class && type != char.class
                : Number.class.isAssignableFrom(type);
        if (!numeric) {
            throw new ValidationConfigurationException(
                    "@Range is not supported for field '" + field.getName()
                            + "' of type " + type.getName());
        }
        if (Double.isNaN(range.min()) || Double.isNaN(range.max())
                || Double.isInfinite(range.min()) || Double.isInfinite(range.max())
                || range.min() > range.max()) {
            throw new ValidationConfigurationException(
                    "Invalid @Range bounds on field '" + field.getName()
                            + "': min=" + range.min() + ", max=" + range.max());
        }
        if (value == null) return;

        double v = ((Number) value).doubleValue();
        if (Double.isNaN(v) || v < range.min() || v > range.max()) {
            errors.add(new ValidationError(field.getName(), "Range", range.message()));
        }
    }

    private static void validateEmail(Field field, Object value, Email email, List<ValidationError> errors) {
        Class<?> type = field.getType();
        if (!CharSequence.class.isAssignableFrom(type)) {
            throw new ValidationConfigurationException(
                    "@Email is not supported for field '" + field.getName()
                            + "' of type " + type.getName());
        }
        if (value == null) return;

        String s = value.toString();
        if (!isValidEmail(s)) {
            errors.add(new ValidationError(field.getName(), "Email", email.message()));
        }
    }

    private static boolean isValidEmail(String s) {
        if (s.isEmpty() || s.isBlank()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) return false;
        }
        int at = s.indexOf('@');
        if (at <= 0 || at != s.lastIndexOf('@') || at == s.length() - 1) return false;

        String local = s.substring(0, at);
        String domain = s.substring(at + 1);
        if (local.isEmpty() || domain.isEmpty()) return false;

        int dot = domain.indexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }
}