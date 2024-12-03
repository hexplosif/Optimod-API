package com.hexplosif.OptimodBackEnd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OptimodApiApplicationTests {

    @Test
    void contextLoads() {
    }



}


/*

If you want to execute multiple test classes in one go, it’s generally not typical to import test methods directly. Instead, you can create an overall suite of tests using @RunWith (in JUnit 4) or @TestInstance in JUnit 5, or use @Import for configuration.

If you want multiple test classes to run as part of one larger test suite, you can use the @Suite or JUnit's parameterized tests. Here’s an example of a suite in JUnit 5 using @TestInstance or @TestMethodOrder.

NODE CONTROLLER
createNode
	valid node
	null node
	incomplete object (ex : has id but not other fields)
	null body
getNode
	valid ID (normal ex : 2)
	valid ID but doesn't exist
	invalid ID (ex : q)
	null variable
getNodes
	check isOK
deleteNodes
	check
updateNode
	valid ID (normal ex : 2)
	valid ID but doesn't exist
	invalid ID (ex : q)
	null variable
	incomplete object (ex : has id but not other fields)
	valid node
	null node
	null body
deleteNode
	valid ID (normal ex : 2)
	valid ID but doesn't exist
	invalid ID (ex : q)
	null variable

SEGMENT CONTROLLER
tout pareil

DELIVERY REQUEST
tout pareil
 */