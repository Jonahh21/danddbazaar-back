package com.dandbazaar.back.common.reporegister;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class RepositoryRegistry implements ApplicationListener<ContextRefreshedEvent> {

    private final ApplicationContext ctx;
    private final Map<Class<?>, JpaRepository<?, ?>> repos = new ConcurrentHashMap<>();

    public RepositoryRegistry(ApplicationContext ctx) {
        this.ctx = ctx;
    }



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, Object> beans = ctx.getBeansWithAnnotation(Registered.class);
        beans.forEach((name, bean) -> {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Registered reg = targetClass.getAnnotation(Registered.class);
            if (reg == null) return;

            Class<?> entityClass = reg.value();

            try {
                ResolvableType rt = ResolvableType.forClass(bean.getClass().asSubclass(JpaRepository.class));
                Class<?> repoEntity = rt.getGeneric(0).resolve();

                if (repoEntity != null && !repoEntity.equals(entityClass)) {
                    System.err.printf("Warning: repo %s declara %s en @Registered pero generics muestran %s%n",
                    name, entityClass.getSimpleName(), repoEntity.getSimpleName());
                }
            } catch (Exception ignored) {}

            if (reg.autoRegister()) {
                @SuppressWarnings("unchecked")
                JpaRepository<?, ?> repoCast = (JpaRepository<?, ?>) bean;
                repos.put(entityClass, repoCast);
            }
        });
    }

    public Optional<JpaRepository<?, ?>> getRepositoryFor(Class<?> entity) {
        return Optional.ofNullable(repos.get(entity));
    }
}
