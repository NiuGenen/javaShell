package ng.aop.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ng.cmd.FileSystem;

public class FIleSystemTest {

	private String absolute_path_test_01 = "C:\\\\asd";			//TRUE
	private String absolute_path_test_02 = "C:\\";				//TRUE
	private String absolute_path_test_03 = "D:\\\\asds\\asd\\";	//TRUE

	private String relative_path_test_01 = "..\\asd";			//TRUE
	private String relative_path_test_02 = ".\\asd";			//TRUE
	private String relative_path_test_03 = "\\asd";				//FALSE
	private String relative_path_test_04 = "asd\\asd";			//TRUE
	private String relative_path_test_05 = "asd\\asd?";			//FALSE
	
	@Test
	public void test_absolute_path(){
		assertTrue( "C:\\\\".matches("[c-zC-Z]:\\\\{1,2}") );	//TRUE
		assertTrue( FileSystem.if_path_absolute(absolute_path_test_01) );
		assertTrue( FileSystem.if_path_absolute(absolute_path_test_02) );
		assertTrue( FileSystem.if_path_absolute(absolute_path_test_03) );
	}
	
	@Test
	public void test_relative_path(){
		assertTrue( FileSystem.if_path_relative(relative_path_test_01) );
		assertTrue( FileSystem.if_path_relative(relative_path_test_02) );
		assertTrue( FileSystem.if_path_relative(relative_path_test_04) );
		
		assertFalse( FileSystem.if_path_relative(relative_path_test_03) );
		assertFalse( FileSystem.if_path_relative(relative_path_test_05) );
	}

	private String root_path_01 = "C:";		//invalid root path
	private String root_path_02 = "C:\\";
	private String root_path_03 = "C:\\\\";
	private String root_path_04 = "C:\\sad\\";
	private String root_path_05 = "C:\\\\sad\\asd";

	private String rlt_path_01 = "qwe";
	private String rlt_path_02 = "qwe\\";
	private String rlt_path_03 = ".\\qwe";
	private String rlt_path_04 = "..\\qwe";
	private String rlt_path_05 = "..\\..\\qwe";
	
	@Test
	public void test_combine_path(){
		assertEquals(null, FileSystem.combine_path(root_path_01, rlt_path_01) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_02, rlt_path_01) );
		assertEquals("C:\\\\qwe", FileSystem.combine_path(root_path_03, rlt_path_01) );
		assertEquals("C:\\sad\\qwe", FileSystem.combine_path(root_path_04, rlt_path_01) );
		assertEquals("C:\\\\sad\\asd\\qwe", FileSystem.combine_path(root_path_05, rlt_path_01) );

		assertEquals(null, FileSystem.combine_path(root_path_01, rlt_path_02) );
		assertEquals("C:\\qwe\\", FileSystem.combine_path(root_path_02, rlt_path_02) );
		assertEquals("C:\\\\qwe\\", FileSystem.combine_path(root_path_03, rlt_path_02) );
		assertEquals("C:\\sad\\qwe\\", FileSystem.combine_path(root_path_04, rlt_path_02) );
		assertEquals("C:\\\\sad\\asd\\qwe\\", FileSystem.combine_path(root_path_05, rlt_path_02) );

		assertEquals(null, FileSystem.combine_path(root_path_01, rlt_path_03) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_02, rlt_path_03) );
		assertEquals("C:\\\\qwe", FileSystem.combine_path(root_path_03, rlt_path_03) );
		assertEquals("C:\\sad\\qwe", FileSystem.combine_path(root_path_04, rlt_path_03) );
		assertEquals("C:\\\\sad\\asd\\qwe", FileSystem.combine_path(root_path_05, rlt_path_03) );

		assertEquals(null, FileSystem.combine_path(root_path_01, rlt_path_04) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_02, rlt_path_04) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_03, rlt_path_04) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_04, rlt_path_04) );
		assertEquals("C:\\\\sad\\qwe", FileSystem.combine_path(root_path_05, rlt_path_04) );

		assertEquals(null, FileSystem.combine_path(root_path_01, rlt_path_05) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_02, rlt_path_05) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_03, rlt_path_05) );
		assertEquals("C:\\qwe", FileSystem.combine_path(root_path_04, rlt_path_05) );
		assertEquals("C:\\\\qwe", FileSystem.combine_path(root_path_05, rlt_path_05) );
	}
	
	@Test
	public void test_combine_path_2(){
		assertEquals("C:\\\\", FileSystem.combine_path("C:\\\\asd", "..\\"));
		assertEquals("C:\\", FileSystem.combine_path("C:\\asd", "..\\"));
	}
}
