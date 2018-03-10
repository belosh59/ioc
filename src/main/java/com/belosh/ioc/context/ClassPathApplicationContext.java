package com.belosh.ioc.context;

import com.belosh.ioc.Injector.Injector;
import com.belosh.ioc.Injector.ReferenceInjector;
import com.belosh.ioc.Injector.ValueInjector;
import com.belosh.ioc.entity.Bean;
import com.belosh.ioc.exceptions.BeanInstantiationException;
import com.belosh.ioc.exceptions.BeanNotFoundException;
import com.belosh.ioc.parser.BeanDefinition;
import com.belosh.ioc.parser.BeanDefinitionReader;
import com.belosh.ioc.parser.BeanDefinitionSAXParser;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {
    private BeanDefinitionReader reader;
    private List<Bean> beans = new ArrayList<>();
    private List<BeanDefinition> beanDefinitions;
    private Map<BeanDefinition, Bean> beanDefinitionToBeanMap = new HashMap<>();

    public ClassPathApplicationContext(String... paths) {
        // Setting type of the Parser
        for (String path : paths) {
            setReader(new BeanDefinitionSAXParser(path));
            beanDefinitions = reader.readBeanDefinitions();
        }
        createBeansFromBeanDefinition();
        new ValueInjector().injectDependencies(beanDefinitionToBeanMap);
        new ReferenceInjector().injectDependencies(beanDefinitionToBeanMap);
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
                beans.add(bean);
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

    public <T> T getBean(Class<T> clazz) {
        int beansCount = 0;
        T returnBean = null;
        for (Bean bean : beans) {
            if (clazz.isInstance(bean.getValue())) {
                beansCount++;
                returnBean = clazz.cast(bean.getValue());
            }
        }
        if (beansCount > 1) {
            throw new BeanNotFoundException("There's more that 1 bean registered for the " + clazz);
        } else if (beansCount == 0) {
            throw new BeanNotFoundException("Bean was not found for class: " + clazz);
        } else {
            return returnBean;
        }
    }

    public <T> T getBean(String id, Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id)) {
                try {
                    return clazz.cast(bean.getValue());
                } catch (ClassCastException e) {
                    throw new BeanNotFoundException("No such bean was registered for class: " + clazz + " with id: " + id);
                }
            }
        }
        throw new BeanNotFoundException("No such bean was registered for class: " + clazz + " with id: " + id);
    }

    public Object getBean(String id) {
        for (Bean bean : beans) {
            if (bean.getId().equals(id)) {
                return bean.getValue();
            }
        }
        throw new BeanNotFoundException("Bean was not found with id: " + id);
    }

    public List<String> getBeanNames() {
        List<String> names = new ArrayList<>();
        for (Bean bean : beans) {
            names.add(bean.getId());
        }
        return names;
    }

    public void setReader(BeanDefinitionReader reader) {
        this.reader = reader;
    }
}
