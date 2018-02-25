package context;

import entiry.Bean;
import exceptions.BeanInstantioationException;
import parser.BeanDefinition;
import parser.BeanDefinitionReader;
import parser.XMLBeanDefinitionParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext<T> implements ApplicationContext<T> {
    private String path;
    private BeanDefinitionReader reader;
    private List<Bean> beans;
    private List<BeanDefinition> beanDefinitions;

    public ClassPathApplicationContext(String path) {
        this.path = path;
        XMLBeanDefinitionParser xmlBeanDefinitionParser = new XMLBeanDefinitionParser();
        xmlBeanDefinitionParser.setPath(path);
        setBeanDefinitionReader(xmlBeanDefinitionParser);
        createBeansFromBeanDefinition();
    }

    public void createBeansFromBeanDefinition() {
        beans = new ArrayList<>();
        beanDefinitions = reader.readBeanDefinitions();
        for(BeanDefinition beanDefinition : beanDefinitions) {
            Bean bean = new Bean();
            bean.setId(beanDefinition.getId());
            try {
                Class clazz = this.getClass().getClassLoader().loadClass(beanDefinition.getBeanClassName());
                bean.setValue(clazz.newInstance());
                injectDependencies(bean.getValue(), beanDefinition.getDependencies());
                injectRefDependencies(bean.getValue(), beanDefinition.getRefDependencies());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BeanInstantioationException();
            }
            beans.add(bean);
        }
    }

    public void injectDependencies(Object object, Map<String, String> dependencies) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                Class fieldType = field.getType();
                Method method = clazz.getDeclaredMethod("set" + capitalize(field.getName()), fieldType);
                if (boolean.class.equals(fieldType)) {
                    method.invoke(object, Boolean.parseBoolean(dependencies.get(field.getName())));
                } else if (int.class.equals(fieldType)) {
                    method.invoke(object, Integer.parseInt(dependencies.get(field.getName())));
                } else if (double.class.equals(fieldType)) {
                    method.invoke(object, Double.parseDouble(dependencies.get(field.getName())));
                } else if (long.class.equals(fieldType)) {
                    method.invoke(object, Long.parseLong(dependencies.get(field.getName())));
                } else {
                    method.invoke(object, dependencies.get(field.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BeanInstantioationException();
            }
        }
    }

    public void injectRefDependencies(Object object, Map<String, String> refDependencies) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                Method method = clazz.getDeclaredMethod("set" + capitalize(field.getName()), field.getType());
                String refId = refDependencies.get(field.getName());
                for (Bean bean : beans) {
                    if (bean.getId().equals(refId)) {
                        method.invoke(object, bean.getValue());
                        return;
                    }
                }
                for(BeanDefinition beanDefinition : beanDefinitions) {
                    if (beanDefinition.getId().equals(refId)) {
                        Class ref = this.getClass().getClassLoader().loadClass(beanDefinition.getBeanClassName());
                        method.invoke(object, ref.newInstance());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new BeanInstantioationException();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public T getBean(Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getValue().getClass().equals(clazz)) {
                return (T) bean.getValue();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public T getBean(String name, Class<T> clazz) {
        for (Bean bean : beans) {
            if (bean.getId().equals(name)) {
                return (T)bean.getValue();
            }
        }
        return null;
    }

    public Object getBean(String name) {
        for (Bean bean : beans) {
            if (bean.getId().equals(name)) {
                return bean.getValue();
            }
        }
        return null;
    }

    public List<String> getBeanNames() {
        List<String> names = new ArrayList<>();
        for (Bean bean : beans) {
            names.add(bean.getId());
        }
        return names;
    }

    public void setBeanDefinitionReader(BeanDefinitionReader beanDefinitionReader) {
        reader = beanDefinitionReader;
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
