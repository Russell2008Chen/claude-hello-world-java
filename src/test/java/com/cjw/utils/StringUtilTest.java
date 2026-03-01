package com.cjw.utils;                                                               

import org.junit.Test;                                                               
import static org.junit.Assert.*;                                                    

/**                                                                                  
 * StringUtil工具类的单元测试                                                        
 */                                                                                  
public class StringUtilTest {                                                        

    @Test                                                                            
    public void testIsBlankWithNull() {                                              
        assertTrue(StringUtil.isBlank(null));                                        
    }                                                                                

    @Test                                                                            
    public void testIsBlankWithEmptyString() {                                       
        assertTrue(StringUtil.isBlank(""));                                          
    }                                                                                

    @Test                                                                            
    public void testIsBlankWithWhitespace() {                                        
        assertTrue(StringUtil.isBlank("   "));                                       
        assertTrue(StringUtil.isBlank("\t"));                                        
        assertTrue(StringUtil.isBlank("\n"));                                        
    }                                                                                

    @Test                                                                            
    public void testIsBlankWithNonBlankString() {                                    
        assertFalse(StringUtil.isBlank("Hello"));                                    
        assertFalse(StringUtil.isBlank("Hello World!"));                             
        assertFalse(StringUtil.isBlank(" a "));                                      
    }                                                                                

    @Test                                                                            
    public void testIsNotBlankWithNull() {                                           
        assertFalse(StringUtil.isNotBlank(null));                                    
    }                                                                                

    @Test                                                                            
    public void testIsNotBlankWithEmptyString() {                                    
        assertFalse(StringUtil.isNotBlank(""));                                      
    }                                                                                

    @Test                                                                            
    public void testIsNotBlankWithWhitespace() {                                     
        assertFalse(StringUtil.isNotBlank("   "));                                   
    }                                                                                

    @Test                                                                            
    public void testIsNotBlankWithNonBlankString() {                                 
        assertTrue(StringUtil.isNotBlank("Hello"));                                  
        assertTrue(StringUtil.isNotBlank("Hello World!"));                           
    }                                                                                

    @Test                                                                            
    public void testLengthWithNull() {                                               
        assertEquals(0, StringUtil.length(null));                                    
    }                                                                                

    @Test                                                                            
    public void testLengthWithEmptyString() {                                        
        assertEquals(0, StringUtil.length(""));                                      
    }                                                                                

    @Test                                                                            
    public void testLengthWithNonEmptyString() {                                     
        assertEquals(5, StringUtil.length("Hello"));                                 
        assertEquals(11, StringUtil.length("Hello World"));                          
    }                                                                                

    @Test                                                                            
    public void testToUpperCaseWithNull() {                                          
        assertNull(StringUtil.toUpperCase(null));                                    
    }                                                                                

    @Test                                                                            
    public void testToUpperCaseWithString() {                                        
        assertEquals("HELLO", StringUtil.toUpperCase("hello"));                      
        assertEquals("HELLO WORLD", StringUtil.toUpperCase("Hello World"));          
        assertEquals("123", StringUtil.toUpperCase("123"));                          
    }                                                                                

    @Test                                                                            
    public void testToLowerCaseWithNull() {                                          
        assertNull(StringUtil.toLowerCase(null));                                    
    }                                                                                

    @Test                                                                            
    public void testToLowerCaseWithString() {                                        
        assertEquals("hello", StringUtil.toLowerCase("HELLO"));                      
        assertEquals("hello world", StringUtil.toLowerCase("Hello World"));          
        assertEquals("123", StringUtil.toLowerCase("123"));                          
    }                                                                                
}     