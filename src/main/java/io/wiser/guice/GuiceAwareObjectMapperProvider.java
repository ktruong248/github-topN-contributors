package io.wiser.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.module.guice.GuiceAnnotationIntrospector;
import com.fasterxml.jackson.module.guice.GuiceInjectableValues;
import com.google.inject.Injector;
import io.wiser.jackson.JsonHelper;

import javax.inject.Inject;
import javax.inject.Provider;

public class GuiceAwareObjectMapperProvider implements Provider<ObjectMapper> {
    private final Injector injector;

    @Inject
    GuiceAwareObjectMapperProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public ObjectMapper get() {
        ObjectMapper mapper = JsonHelper.buildObjectMapper();
        final GuiceAnnotationIntrospector guiceIntrospector = new GuiceAnnotationIntrospector();
        mapper.setInjectableValues(new GuiceInjectableValues(injector));
        mapper.setAnnotationIntrospectors(
                new AnnotationIntrospectorPair(
                        guiceIntrospector, mapper.getSerializationConfig().getAnnotationIntrospector()
                ),
                new AnnotationIntrospectorPair(
                        guiceIntrospector, mapper.getDeserializationConfig().getAnnotationIntrospector()
                ));
        return mapper;
    }
}