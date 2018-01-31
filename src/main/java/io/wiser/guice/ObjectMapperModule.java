package io.wiser.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ObjectMapperModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ObjectMapper.class).toProvider(GuiceAwareObjectMapperProvider.class).in(Singleton.class);
        bind(JsonConfigurer.class).asEagerSingleton();
    }
}