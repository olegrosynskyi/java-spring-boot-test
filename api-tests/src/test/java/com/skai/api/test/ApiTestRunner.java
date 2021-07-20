package com.skai.api.test;

import com.intuit.karate.Results;
import com.intuit.karate.junit5.Karate;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiTestRunner {

    @Test
    void runApiTests() {
        Results results = Karate.run()
                .outputJunitXml(true)
                .outputCucumberJson(true)
                .outputHtmlReport(false)
                .backupReportDir(false)
                .relativeTo(getClass())
                .parallel(3);
        generateReport(results.getReportDir());
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    private void generateReport(String reportsDirectory) {
        final List<String> jsonPaths = FileUtils.listFiles(new File(reportsDirectory), new String[]{"json"}, true).stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
        final Configuration config = new Configuration(new File(reportsDirectory), "API Tests");
        final ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }
}
