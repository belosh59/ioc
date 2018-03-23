=========================================================================================
Refactoring
=========================================================================================
1. XMLBeanDefinitionParser -> NPE surrounded with try /catch clause
2. XMLBeanDefinitionParser -> Import tag processing included
3. XMLBeanDefinitionParser.DOMParserContextXML(File contextXMLFile) -> equal("") replaced with isEmpty
4. Implement SAX Parser
5. Tests for Parsers are fixed / extended 
6. ClassPathApplicationContext -> changed methods access level to package
7. Test for Bean without default counstructor
8. ClassPathApplicationContext.createBeansFromBeanDefinition() -> added processing exception during bean instantiation. Tests are added for the same
9. ClassPathApplicationContext.injectDependencies() -> Added Byte and Short datatype. Check qestion #3
10. injectDependencies / injectRefDependencies fixed to go through the dependency Map
11. getBean methods refactored to use correct generics and interface calls
12. Added AllDataTypes test
13. Added Injector / ValueInjector / ReferenceInjector. User Template Pattern

=========================================================================================
Questions
=========================================================================================
1. Callback
2. Initialisation inside SAX Parser
3. ClassPathApplicationContext.injectDependencies() -> Do correctly process char type?

=========================================================================================
TODO
=========================================================================================
1. tests on grovy


10FHjfJvj1g