package org.example;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CollectionPerformanceExperimentTest {
    @Disabled("Абсолютные тайминги зависят от JVM/ОС; запускайте main вручную")
    @Test
    void runExperiment() {
        CollectionPerformanceExperiment.main(new String[0]);
    }
}