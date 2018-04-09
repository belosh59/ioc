### Refactored:
* XMLBeanDefinitionParser -> NPE surrounded with try /catch clause
* XMLBeanDefinitionParser -> Import tag processing included
* XMLBeanDefinitionParser.DOMParserContextXML(File contextXMLFile) -> equal("") replaced with isEmpty
* Implement SAX Parser
* Tests for Parsers are fixed / extended
* ClassPathApplicationContext -> changed methods access level to package
* Test for Bean without default counstructor
* ClassPathApplicationContext.createBeansFromBeanDefinition() -> added processing exception during bean instantiation. Tests are added for the same
* ClassPathApplicationContext.injectDependencies() -> Added Byte and Short datatype. Check qestion #3
* injectDependencies / injectRefDependencies fixed to go through the dependency Map
* getBean methods refactored to use correct generics and interface calls
* Added AllDataTypes test
* Added Injector / ValueInjector / ReferenceInjector. User Template Pattern

/* After  17.03.18*/
* Refactored names of packages and classes
* equals comparison corrected

/* After 09.04.18*/
* Tests on groovy
* BeanFactoryPostProcessor processing implemented
* BeanPostProcessor processing implemented

### Questions
* What is the reason for BeanPostProcessor methods to return object.