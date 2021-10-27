package io.skai.template.config.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.kenshoo.metrics3.graphite.UDPMinimalWithDeltaGraphite;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.dropwizard.DropwizardClock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.autoconfigure.metrics.export.ConditionalOnEnabledMetricsExport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnEnabledMetricsExport("graphite")
public class GraphiteMetricsExportConfig {

    @Bean
    public GraphiteMeterRegistry graphiteMeterRegistry(GraphiteConfig config, Clock clock) {
        MetricRegistry metricRegistry = new MetricRegistry();
        HierarchicalNameMapper nameMapper = nameMapper(config);
        GraphiteReporter reporter = reporter(config, metricRegistry, clock);
        GraphiteMeterRegistry registry = new GraphiteMeterRegistry(config, clock, nameMapper, metricRegistry, reporter);
        registry.config().namingConvention(new GraphiteHierarchicalNamingConvention(NamingConvention.dot));
        return registry;
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(@Value("${management.metrics.export.graphite.secret}") String secret,
                                                             @Value("${spring.application.name}") String appName,
                                                             @Value("${spring.environment.type}") String environmentType) {
        return r -> r.config().commonTags(List.of(
                Tag.of("prefix", secret),
                Tag.of("appName", appName),
                Tag.of("environmentType", environmentType)
        ));
    }

    private HierarchicalNameMapper nameMapper(GraphiteConfig config) {
        return config.graphiteTagsEnabled()
                ? new GraphiteDimensionalNameMapper()
                : new GraphiteHierarchicalNameMapper(config.tagsAsPrefix());
    }

    private GraphiteReporter reporter(GraphiteConfig config, MetricRegistry metricRegistry, Clock clock) {
        UDPMinimalWithDeltaGraphite graphiteUDP = new UDPMinimalWithDeltaGraphite(config.host(), config.port());
        return GraphiteReporter.forRegistry(metricRegistry)
                .withClock(new DropwizardClock(clock))
                .convertRatesTo(config.rateUnits())
                .convertDurationsTo(config.durationUnits())
                .addMetricAttributesAsTags(config.graphiteTagsEnabled())
                .build(graphiteUDP);
    }
}
