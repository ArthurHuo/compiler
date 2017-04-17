package whilelang.testing.tests;

import org.junit.*;

import whilelang.testing.TestHarness;

public class ClassFileValidTests extends TestHarness {
 public ClassFileValidTests() {
  super("tests/valid","tests/valid","sysout");
 }

 @Test public void BoolAssign_Valid_1() { runClassFileTest("BoolAssign_Valid_1"); }
 @Test public void BoolAssign_Valid_2() { runClassFileTest("BoolAssign_Valid_2"); }
 @Test public void BoolAssign_Valid_3() { runClassFileTest("BoolAssign_Valid_3"); } 
 @Test public void BoolAssign_Valid_4() { runClassFileTest("BoolAssign_Valid_4"); } 
 @Test public void BoolIfElse_Valid_1() { runClassFileTest("BoolIfElse_Valid_1"); }
 @Test public void BoolIfElse_Valid_2() { runClassFileTest("BoolIfElse_Valid_2"); } 
 @Test public void BoolList_Valid_1() { runClassFileTest("BoolList_Valid_1"); } 
 @Test public void BoolList_Valid_2() { runClassFileTest("BoolList_Valid_2"); }  
 @Test public void BoolRecord_Valid_1() { runClassFileTest("BoolRecord_Valid_1"); }
 @Test public void BoolRecord_Valid_2() { runClassFileTest("BoolRecord_Valid_2"); }
 @Test public void BoolReturn_Valid_1() { runClassFileTest("BoolReturn_Valid_1"); } 
 @Test public void Cast_Valid_1() { runClassFileTest("Cast_Valid_1"); } 
 @Test public void Cast_Valid_2() { runClassFileTest("Cast_Valid_2"); }
 @Test public void Cast_Valid_3() { runClassFileTest("Cast_Valid_3"); } 
 @Test public void Cast_Valid_4() { runClassFileTest("Cast_Valid_4"); }  
 @Test public void Char_Valid_1() { runClassFileTest("Char_Valid_1"); }
 @Test public void Char_Valid_2() { runClassFileTest("Char_Valid_2"); }
 @Test public void Char_Valid_3() { runClassFileTest("Char_Valid_3"); }
 @Test public void Const_Valid_1() { runClassFileTest("Const_Valid_1"); }
 @Test public void Const_Valid_2() { runClassFileTest("Const_Valid_2"); }
 @Test public void Const_Valid_3() { runClassFileTest("Const_Valid_3"); }
 @Test public void Const_Valid_4() { runClassFileTest("Const_Valid_4"); }
 @Test public void Define_Valid_1() { runClassFileTest("Define_Valid_1"); } 
 @Test public void Define_Valid_2() { runClassFileTest("Define_Valid_2"); } 
 @Test public void Function_Valid_1() { runClassFileTest("Function_Valid_1"); }
 @Test public void Function_Valid_2() { runClassFileTest("Function_Valid_2"); } 
 @Test public void Function_Valid_4() { runClassFileTest("Function_Valid_4"); }
 @Test public void IfElse_Valid_1() { runClassFileTest("IfElse_Valid_1"); }
 @Test public void IfElse_Valid_2() { runClassFileTest("IfElse_Valid_2"); }
 @Test public void IfElse_Valid_3() { runClassFileTest("IfElse_Valid_3"); } 
 @Test public void IntDefine_Valid_1() { runClassFileTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1() { runClassFileTest("IntDiv_Valid_1"); }
 @Test public void IntDiv_Valid_2() { runClassFileTest("IntDiv_Valid_2"); } 
 @Test public void IntEquals_Valid_1() { runClassFileTest("IntEquals_Valid_1"); }
 @Test public void IntMul_Valid_1() { runClassFileTest("IntMul_Valid_1"); }
 @Test public void LengthOf_Valid_1() { runClassFileTest("LengthOf_Valid_1"); }
 @Test public void LengthOf_Valid_5() { runClassFileTest("LengthOf_Valid_5"); }
 @Test public void ListAccess_Valid_1() { runClassFileTest("ListAccess_Valid_1"); } 
 @Test public void ListAccess_Valid_3() { runClassFileTest("ListAccess_Valid_3"); }
 @Test public void ListAccess_Valid_4() { runClassFileTest("ListAccess_Valid_4"); }  
 @Test public void ListAppend_Valid_1() { runClassFileTest("ListAppend_Valid_1"); }
 @Test public void ListAppend_Valid_2() { runClassFileTest("ListAppend_Valid_2"); }
 @Test public void ListAppend_Valid_3() { runClassFileTest("ListAppend_Valid_3"); }
 @Test public void ListAppend_Valid_4() { runClassFileTest("ListAppend_Valid_4"); }
 @Test public void ListAppend_Valid_5() { runClassFileTest("ListAppend_Valid_5"); }
 @Test public void ListAppend_Valid_6() { runClassFileTest("ListAppend_Valid_6"); }
 @Test public void ListAppend_Valid_7() { runClassFileTest("ListAppend_Valid_7"); } 
 @Test public void ListAssign_Valid_1() { runClassFileTest("ListAssign_Valid_1"); }
 @Test public void ListAssign_Valid_2() { runClassFileTest("ListAssign_Valid_2"); }
 @Test public void ListAssign_Valid_3() { runClassFileTest("ListAssign_Valid_3"); }
 @Test public void ListAssign_Valid_4() { runClassFileTest("ListAssign_Valid_4"); }
 @Test public void ListAssign_Valid_5() { runClassFileTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6() { runClassFileTest("ListAssign_Valid_6"); }
 @Test public void ListAssign_Valid_10() { runClassFileTest("ListAssign_Valid_10"); }
 @Test public void ListConversion_Valid_1() { runClassFileTest("ListConversion_Valid_1"); }
 @Test public void ListEmpty_Valid_1() { runClassFileTest("ListEmpty_Valid_1"); }
 @Test public void ListEquals_Valid_1() { runClassFileTest("ListEquals_Valid_1"); }
 @Test public void ListGenerator_Valid_1() { runClassFileTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2() { runClassFileTest("ListGenerator_Valid_2"); }
 @Test public void ListGenerator_Valid_3() { runClassFileTest("ListGenerator_Valid_3"); } 
 @Test public void ListLength_Valid_1() { runClassFileTest("ListLength_Valid_1"); }
 @Test public void ListLength_Valid_2() { runClassFileTest("ListLength_Valid_2"); }
 @Test public void MultiLineComment_Valid_1() { runClassFileTest("MultiLineComment_Valid_1"); }
 @Test public void MultiLineComment_Valid_2() { runClassFileTest("MultiLineComment_Valid_2"); }
 @Test public void RealDiv_Valid_1() { runClassFileTest("RealDiv_Valid_1"); } 
 @Test public void RealDiv_Valid_3() { runClassFileTest("RealDiv_Valid_3"); } 
 @Test public void RealDiv_Valid_4() { runClassFileTest("RealDiv_Valid_4"); }
 @Test public void RealNeg_Valid_1() { runClassFileTest("RealNeg_Valid_1"); } 
 @Test public void RealSub_Valid_1() { runClassFileTest("RealSub_Valid_1"); }
 @Test public void RealSub_Valid_2() { runClassFileTest("RealSub_Valid_2"); } 
 @Test public void Real_Valid_1() { runClassFileTest("Real_Valid_1"); }
 @Test public void RecordAccess_Valid_2() { runClassFileTest("RecordAccess_Valid_2"); }
 @Test public void RecordAssign_Valid_1() { runClassFileTest("RecordAssign_Valid_1"); }
 @Test public void RecordAssign_Valid_2() { runClassFileTest("RecordAssign_Valid_2"); }
 @Test public void RecordAssign_Valid_3() { runClassFileTest("RecordAssign_Valid_3"); }
 @Test public void RecordAssign_Valid_4() { runClassFileTest("RecordAssign_Valid_4"); }
 @Test public void RecordAssign_Valid_5() { runClassFileTest("RecordAssign_Valid_5"); }
 @Test public void RecordAssign_Valid_6() { runClassFileTest("RecordAssign_Valid_6"); }
 @Test public void RecordDefine_Valid_1() { runClassFileTest("RecordDefine_Valid_1"); } 
 @Test public void Remainder_Valid_1() { runClassFileTest("Remainder_Valid_1"); } 
 @Test public void SingleLineComment_Valid_1() { runClassFileTest("SingleLineComment_Valid_1"); } 
 @Test public void String_Valid_1() { runClassFileTest("String_Valid_1"); }
 @Test public void String_Valid_2() { runClassFileTest("String_Valid_2"); }
 @Test public void String_Valid_3() { runClassFileTest("String_Valid_3"); }
 @Test public void String_Valid_4() { runClassFileTest("String_Valid_4"); } 
 
 @Test public void Switch_Valid_1() { runClassFileTest("Switch_Valid_1"); }
 @Test public void Switch_Valid_2() { runClassFileTest("Switch_Valid_2"); }
 @Test public void Switch_Valid_3() { runClassFileTest("Switch_Valid_3"); }
 @Test public void Switch_Valid_4() { runClassFileTest("Switch_Valid_4"); } 
 @Test public void Switch_Valid_6() { runClassFileTest("Switch_Valid_6"); }
 @Test public void Switch_Valid_7() { runClassFileTest("Switch_Valid_7"); }
 @Test public void Switch_Valid_8() { runClassFileTest("Switch_Valid_8"); }
 
 @Test public void TypeEquals_Valid_1_RuntimeTest() { runClassFileTest("TypeEquals_Valid_1"); }
 @Test public void TypeEquals_Valid_2_RuntimeTest() { runClassFileTest("TypeEquals_Valid_2"); }
 @Test public void TypeEquals_Valid_5_RuntimeTest() { runClassFileTest("TypeEquals_Valid_5"); }
 @Test public void TypeEquals_Valid_8_RuntimeTest() { runClassFileTest("TypeEquals_Valid_8"); } 
 @Test public void TypeEquals_Valid_9_RuntimeTest() { runClassFileTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_11_RuntimeTest() { runClassFileTest("TypeEquals_Valid_11"); }
 @Test public void TypeEquals_Valid_14_RuntimeTest() { runClassFileTest("TypeEquals_Valid_14"); } 
 @Test public void TypeEquals_Valid_16_RuntimeTest() { runClassFileTest("TypeEquals_Valid_16"); } 
 @Test public void TypeEquals_Valid_20_RuntimeTest() { runClassFileTest("TypeEquals_Valid_20"); }
 
 @Test public void UnionType_Valid_1() { runClassFileTest("UnionType_Valid_1"); }
 @Test public void UnionType_Valid_2() { runClassFileTest("UnionType_Valid_2"); }
 @Test public void UnionType_Valid_4() { runClassFileTest("UnionType_Valid_4"); }
 @Test public void UnionType_Valid_5() { runClassFileTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6() { runClassFileTest("UnionType_Valid_6"); }
 @Test public void UnionType_Valid_7() { runClassFileTest("UnionType_Valid_7"); }
 @Test public void UnionType_Valid_8() { runClassFileTest("UnionType_Valid_8"); }
 @Test public void UnionType_Valid_9() { runClassFileTest("UnionType_Valid_9"); }
 @Test public void While_Valid_1() { runClassFileTest("While_Valid_1"); }
 @Test public void While_Valid_2() { runClassFileTest("While_Valid_2"); }
 @Test public void While_Valid_4() { runClassFileTest("While_Valid_4"); }
 @Test public void While_Valid_6() { runClassFileTest("While_Valid_6"); }
}
