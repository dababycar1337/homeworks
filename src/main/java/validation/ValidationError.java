package validation;

import java.util.Objects;

public final class ValidationError {
    private final String fieldName;
    private final String annotationName;
    private final String message;

    public ValidationError(String fieldName, String annotationName, String message) {
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName");
        this.annotationName = Objects.requireNonNull(annotationName, "annotationName");
        this.message = Objects.requireNonNull(message, "message");
    }

    public String fieldName() {
        return fieldName;
    }

    public String annotationName() {
        return annotationName;
    }

    public String message() {
        return message;
    }

    public String getFieldName() { return fieldName; }
    public String getAnnotationName() { return annotationName; }
    public String getMessage() { return message; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationError that)) return false;
        return fieldName.equals(that.fieldName)
                && annotationName.equals(that.annotationName)
                && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, annotationName, message);
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "fieldName='" + fieldName + '\'' +
                ", annotationName='" + annotationName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}