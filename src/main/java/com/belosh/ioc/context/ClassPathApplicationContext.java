package com.belosh.ioc.context;

import com.belosh.ioc.injector.Injector;
import com.belosh.ioc.injector.ReferenceInjector;
import com.belosh.ioc.injector.ValueInjector;
import com.belosh.ioc.entity.Bean;
import com.belosh.ioc.exception.BeanInstantiationException;
import com.belosh.ioc.exception.BeanNotFoundException;
import com.belosh.ioc.entity.BeanDefinition;
import com.belosh.ioc.processor.BeanFactoryPostProcessor;
import com.belosh.ioc.processor.BeanPostProcessor;
import com.belosh.ioc.processor.PostConstruct;
import com.belosh.ioc.reader.BeanDefinitionReader;
import com.belosh.ioc.reader.SAXBeanDefinitionReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassPathApplicationContext implements ApplicationContext {
    private BeanDefinitionReader reader;
    private List<BeanDefinition> beanDefinitions;
    private Map<BeanDefinition, Bean> beanDefinitionToBeanMap;
    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
    private List<BeanPostProcessor> beanPostProcessors;
    private List<String> beanNames;

    public ClassPathApplicationContext(String... paths) {
        beanDefinitionToBeanMap = new HashMap<>();
        beanFactoryPostProcessors = new ArrayList<>();
        beanPostProcessors = new ArrayList<>();
        beanNames = new ArrayList<>();

        // Setting type of the Parser
        for (String path : paths) {
            setReader(new SAXBeanDefinitionReader(path));
            beanDefinitions = reader.readBeanDefinitions();
        }

        createProcessorsFromBeanDefinition();
        postProcessBeanFactory();

        createBeansFromBeanDefinition();
        injectDependencies(new ValueInjector(beanDefinitionToBeanMap));
        injectDependencies(new ReferenceInjector(beanDefinitionToBeanMap));

        postProcessBefore();
        postConstruct();
        postProcessAfter();
    }

    public <T> T getBean(Class<T> clazz) {
        int beansCount = 0;
        T returnBean = null;

        for (Bean bean : beanDefinitionToBeanMap.values()) {
            if (clazz.isInstance(bean.getValue())) {
                beansCount++;
                returnBean = clazz.cast(bean.getValue());
            }
        }

        if (beansCount > 1) {
            throw new BeanNotFoundException("There's more that 1 bean registered for the " + clazz);
        } else if (beansCount == 0) {
            throw new BeanNotFoundException("Bean was not found for class: " + clazz);
        }

        return returnBean;
    }

    public <T> T getBean(String id, Class<T> clazz) {
        for (Bean bean : beanDefinitionToBeanMap.values()) {
            if (bean.getId().equals(id)) {
                try {
                    return clazz.cast(bean.getValue());
                } catch (ClassCastException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new BeanNotFoundException("No such bean was registered for class: " + clazz + " with id: " + id);
    }

    public Object getBean(String id) {
        for (Bean bean : beanDefinitionToBeanMap.values()) {
            if (bean.getId().equals(id)) {
                return bean.getValue();
            }
        }
        throw new BeanNotFoundException("Bean was not found with id: " + id);
    }

    public List<String> getBeanNames() {
        if (!beanNames.isEmpty()) {
            return beanNames;
        }
        for (Bean bean : beanDefinitionToBeanMap.values()) {
            beanNames.add(bean.getId());
        }
        return beanNames;
    }

    private void setReader(BeanDefinitionReader reader) {
        this.reader = reader;
    }

    private void createBeansFromBeanDefinition() {
        for(BeanDefinition beanDefinition : beanDefinitions) {
            try {
                // prepare
                String className = beanDefinition.getBeanClassName();
                Class<?> clazz = Class.forName(className);

                // bean creation
                Bean bean = new Bean();
                bean.setId(beanDefinition.getId());
                bean.setValue(clazz.getConstructor().newInstance());

                //save bean
                beanDefinitionToBeanMap.put(beanDefinition, bean);
            } catch (NoSuchMethodException e) {
                throw new BeanInstantiationException("Default constructor not found for " + beanDefinition.getBeanClassName(), e);
            } catch (ClassNotFoundException e) {
                throw new BeanInstantiationException("Incorrect class declared in beans configuration xml file", e);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new BeanInstantiationException("Issue during bean instantiation", e);
            }

        }
    }

    private void injectDependencies(Injector injector) {
        injector.injectDependencies();
    }

    private void postConstruct() {
        for(Bean bean : beanDefinitionToBeanMap.values()) {
            Object object = bean.getValue();
            Class<?> clazz = object.getClass();
            Method[] declaredMethods = clazz.getDeclaredMethods();

            try {
                for (Method method : declaredMethods) {
                    // javax.annotation.PostConstruct throws NoSuchClassDefFound
                    if (method.isAnnotationPresent(PostConstruct.class)) {
                        if (Modifier.isPrivate(method.getModifiers())) {
                            method.setAccessible(true);
                            method.invoke(object);
                            method.setAccessible(false);
                        } else {
                            method.invoke(object);
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanInstantiationException("Issue during bean instantiation", e);
            }
        }
    }

    private void createProcessorsFromBeanDefinition() {
        for(Iterator<BeanDefinition> iterator = beanDefinitions.iterator(); iterator.hasNext(); ) {
            BeanDefinition beanDefinition = iterator.next();
            try {
                // prepare
                String className = beanDefinition.getBeanClassName();
                Class<?> clazz = Class.forName(className);

                // get processors
                if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
                    BeanFactoryPostProcessor instance = (BeanFactoryPostProcessor) clazz.getConstructor().newInstance();
                    beanFactoryPostProcessors.add(instance);
                    iterator.remove();
                }
                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                    BeanPostProcessor instance = (BeanPostProcessor) clazz.getConstructor().newInstance();
                    beanPostProcessors.add(instance);
                    iterator.remove();
                }
            } catch (NoSuchMethodException e) {
                throw new BeanInstantiationException("Default constructor not found for " + beanDefinition.getBeanClassName(), e);
            } catch (ClassNotFoundException e) {
                throw new BeanInstantiationException("Incorrect class declared in beans configuration xml file", e);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new BeanInstantiationException("Issue during bean instantiation", e);
            }
        }
    }

    private void postProcessBeanFactory() {
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanDefinitions);
        }
    }

    private void postProcessBefore() {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            for (Bean bean : beanDefinitionToBeanMap.values()) {
                beanPostProcessor.postProcessBeforeInitialization(bean.getValue(), bean.getId());
            }
        }
    }

    private void postProcessAfter() {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            for (Bean bean : beanDefinitionToBeanMap.values()) {
                beanPostProcessor.postProcessAfterInitialization(bean.getValue(), bean.getId());
            }
        }
    }

}
