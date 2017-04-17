package whilelang.io;

import static whilelang.util.SyntaxError.internalFailure;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jasm.io.*;
import jasm.lang.*;
import jasm.lang.Bytecode.BinOp;
import jasm.lang.Bytecode.Load;
import jasm.lang.Bytecode.LoadConst;
import jasm.lang.Bytecode.Return;
import jasm.lang.JvmType.Bool;
import jasm.lang.JvmType.Int;
import jasm.attributes.*;
import jasm.attributes.StackMapTable.Frame;
import whilelang.lang.Expr;
import whilelang.lang.Stmt;
import whilelang.lang.Type;
import whilelang.lang.WhileFile;
import whilelang.lang.WhileFile.TypeDecl;
import whilelang.util.Pair;

/**
 * Responsible for translating a While source file into a JVM Class file.
 * 
 * @author David J. Pearce
 * 
 */
public class ClassFileWriter {
	
	 jasm.io.ClassFileWriter writer;
	 WhileFile sourceFile;
	 public static JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang", "System");
	 public static JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io", "PrintStream");
	 jasm.lang.ClassFile cf;
	 ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
	 private HashMap<String, WhileFile.Decl> declarations;
	 private HashMap<String, JvmType> decltypes = new HashMap<String, JvmType>();
	 private WhileFile file;
	 ClassFile.Method method;
	 String methodName = "";
	
	public ClassFileWriter(File classFile) {
//		 writer = new jasm.io.ClassFileWriter(new FileInputStream(classFile));
		try {
			writer = new jasm.io.ClassFileWriter(new FileOutputStream(classFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(WhileFile sourceFile) throws IOException {
		this.sourceFile = sourceFile;
//		 jasm.lang.ClassFile cf;
		
		// TODO: implement this method!! 
		

		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);

		// ================================================================================
		// Create an empty class file
		// ================================================================================
		ClassFile cf = new ClassFile(
				49,                                 // Java 1.5 or later
				new JvmType.Clazz("",sourceFile.filename.substring(0,sourceFile.filename.lastIndexOf('.'))), // class is HelloWorld
				JvmTypes.JAVA_LANG_OBJECT,          // superclass is Object
				Collections.EMPTY_LIST,             // implements no interfaces
				modifiers);                         // which is public


	
		
		
		
		
		
		
		ClassFile.Method method = null;
		
		
		declarations = new HashMap<String,WhileFile.Decl>();
		for(WhileFile.Decl decl : sourceFile.declarations) {
			declarations.put(decl.name(), decl);
			if(decl instanceof WhileFile.TypeDecl){
				WhileFile.TypeDecl wt = (WhileFile.TypeDecl)decl;
				JvmType jt = JvmTypes.T_VOID;
				if(wt.type instanceof Type.Int){
					jt = JvmTypes.T_INT;
				}else if(wt.type instanceof Type.Bool){
					jt = JvmTypes.T_BOOL;
				}else if(wt.type instanceof Type.Char){
					jt = JvmTypes.T_CHAR;
				}else if(wt.type instanceof Type.Strung){
					jt = JvmTypes.JAVA_LANG_STRING;
				}else if(wt.type instanceof Type.Real){
					jt = JvmTypes.T_DOUBLE;
				}
				decltypes.put(wt.name, jt);
			}
		}
		

		
		
		
		
		for(WhileFile.Decl decl : sourceFile.declarations){
//			if(decl instanceof WhileFile.ConstDecl){
//				continue;
//			}
			if(decl instanceof WhileFile.FunDecl){
				if(decl.name().equals("main")){
					WhileFile.FunDecl fd = (WhileFile.FunDecl) decl;
					methodName = "main";
					bytecodes = new ArrayList<Bytecode>();
					modifiers = new ArrayList<Modifier>(modifiers); 
					modifiers.add(Modifier.ACC_STATIC);
					method = new ClassFile.Method(
							"main",                                              // main method
							new JvmType.Function(                                // is function
									JvmTypes.T_VOID,                             // from void
									new JvmType.Array(JvmTypes.JAVA_LANG_STRING) // to array of String
									),
									modifiers                                     // which is static public
							);
		
					cf.methods().add(method);
					
					
					for(WhileFile.Decl cdecl : sourceFile.declarations){
						if(cdecl instanceof WhileFile.ConstDecl){
							WhileFile.ConstDecl wc = (WhileFile.ConstDecl)cdecl;
							
						}
					}
					
					
					execute(fd);
					bytecodes.add(new Bytecode.Return(null));
					method.attributes().add(new Code(bytecodes, Collections.EMPTY_LIST, method));
				}else{
					WhileFile.FunDecl fd = (WhileFile.FunDecl) decl;
					
					
					ArrayList<JvmType> pl = new ArrayList<JvmType>();
					for(int i = 0; i < fd.parameters.size();i++){
						Object o = fd.parameters.get(i);
						WhileFile.Parameter wp = (WhileFile.Parameter) o;
						if(wp.type instanceof Type.Bool){
							pl.add(JvmTypes.T_BOOL);
						}else if (wp.type instanceof Type.Int){
							pl.add(JvmTypes.T_INT);
						}else if (wp.type instanceof Type.Real){
							pl.add(JvmTypes.T_DOUBLE);
						}else if(wp.type instanceof Type.Strung){
							pl.add(JvmTypes.JAVA_LANG_STRING);
						}else if(wp.type instanceof Type.Char){
							pl.add(JvmTypes.T_CHAR);
						}else if(wp.type instanceof Type.Named){
							Type.Named tn = (Type.Named)wp.type;
							pl.add(decltypes.get(tn.getName()));
						}
					}
					
					JvmType jt = JvmTypes.T_VOID;
					if(fd.ret instanceof Type.Bool){
					   jt = JvmTypes.T_BOOL;
					}else if (fd.ret instanceof Type.Int){
						jt = JvmTypes.T_INT;
					}else if(fd.ret instanceof Type.Real){
						jt = JvmTypes.T_DOUBLE;
					}else if(fd.ret instanceof Type.Strung){
						jt = JvmTypes.JAVA_LANG_STRING;
					}else if(fd.ret instanceof Type.Char){
						jt = JvmTypes.T_CHAR;
					}else if(fd.ret instanceof Type.Named){
						Type.Named tn = (Type.Named)fd.ret;
						jt = decltypes.get(tn.getName());
					}
					
//					if(fd.ret instanceof Type.Bool){
					bytecodes = new ArrayList<Bytecode>();
					modifiers = new ArrayList<Modifier>(modifiers); 
					modifiers.add(Modifier.ACC_STATIC);
					methodName = fd.name;
					method = new ClassFile.Method(
							fd.name,                                              // main method
							new JvmType.Function(                                // is function
									jt,                            // from void
									pl// to array of String
									),
									modifiers                                     // which is static public
							);
					
					cf.methods().add(method);
//					}
					
					
//					List<Expr> arguments = fd.parameters;
//					Object[] values = new Object[arguments.size()];
//					for (int i = 0; i != values.length; ++i) {
//						// We need to perform a deep clone here to ensure the value
//						// semantics used in While are preserved.
//						values[i] = deepClone(execute(arguments.get(i), frame,framelist));
//					}
					
					
					execute(fd); 
//					Object o = execute(fd);
//					JvmType.Bool bl = 
//					bytecodes.add(new Bytecode.Return(o));
//					bytecodes.add(new Bytecode.Return(null));
					method.attributes().add(new Code(bytecodes, Collections.EMPTY_LIST, method));
				}
			}
		}
		
		
		
		
		
		
		
		
		
		
//		
//		System.out.println("aa");
//		bytecodes.add(new Bytecode.Return(null));
//		method.attributes().add(new Code(bytecodes, Collections.EMPTY_LIST, method));
		 writer.write(cf);
	}
	
	
	
	
	public void run(WhileFile wf) {
		// First, initialise the map of declaration names to their bodies.
		declarations = new HashMap<String,WhileFile.Decl>();
		for(WhileFile.Decl decl : wf.declarations) {
			declarations.put(decl.name(), decl);
		}
		this.file = wf;
		
		// Second, pick the main method (if one exits) and execute it
		WhileFile.Decl main = declarations.get("main");
		if(main instanceof WhileFile.FunDecl) {
			WhileFile.FunDecl fd = (WhileFile.FunDecl) main;
			
			
//			modifiers = new ArrayList<Modifier>(modifiers); 
//			modifiers.add(Modifier.ACC_STATIC);
//			ClassFile.Method method = new ClassFile.Method(
//					"main",                                              // main method
//					new JvmType.Function(                                // is function
//							JvmTypes.T_VOID,                             // from void
//							new JvmType.Array(JvmTypes.JAVA_LANG_STRING) // to array of String
//							),
//							modifiers                                     // which is static public
//					);
//
//			cf.methods().add(method);
			
			execute(fd);
		} else {
			System.out.println("Cannot find a main() function");
		}
	}
	
	/**
	 * Execute a given function with the given argument values. If the number of
	 * arguments is incorrect, then an exception is thrown.
	 * 
	 * @param function
	 *            Function declaration to execute.
	 * @param arguments
	 *            Array of argument values.
	 */
	private Object execute(WhileFile.FunDecl function, Object... arguments) {
		
		// First, sanity check the number of arguments
//		if(function.parameters.size() != arguments.length){
//			throw new RuntimeException(
//					"invalid number of arguments supplied to execution of function \""
//							+ function.name + "\"");
//		}
		
		// Second, construct the stack frame in which this function will
		// execute.
		HashMap<String,Object> frame = new HashMap<String,Object>();
		ArrayList<String> framelist = new ArrayList<String>();
		
		for(WhileFile.Decl cdecl : sourceFile.declarations){
			if(cdecl instanceof WhileFile.ConstDecl){
				WhileFile.ConstDecl wc = (WhileFile.ConstDecl)cdecl;
				frame.put(wc.name, wc.constant);
				framelist.add(wc.name);
			}else if(cdecl instanceof WhileFile.TypeDecl){
				WhileFile.TypeDecl wt = (WhileFile.TypeDecl)cdecl;
				JvmType jt = JvmTypes.T_VOID;
				if(wt.type instanceof Type.Int){
					jt = JvmTypes.T_INT;
				}else if(wt.type instanceof Type.Bool){
					jt = JvmTypes.T_BOOL;
				}else if(wt.type instanceof Type.Char){
					jt = JvmTypes.T_CHAR;
				}else if(wt.type instanceof Type.Strung){
					jt = JvmTypes.JAVA_LANG_STRING;
				}else if(wt.type instanceof Type.Real){
					jt = JvmTypes.T_DOUBLE;
				}
				decltypes.put(wt.name, jt);
			}
		}
		
		
		for(int i=0;i!=arguments.length;++i) {
			WhileFile.Parameter parameter = function.parameters.get(i);
			frame.put(parameter.name,arguments[i]);
			framelist.add(parameter.name);
		}
		
		if(arguments.length <= 0){
			for(int i=0;i!=function.parameters.size();++i) {
				WhileFile.Parameter parameter = function.parameters.get(i);
				
				framelist.add(parameter.name);
			}
		}
		// Third, execute the function body!
		return execute(function.statements,frame,framelist);
	}
	
	private Object execute(List<Stmt> block, HashMap<String,Object> frame, ArrayList<String> framelist) {
		for(int i=0;i!=block.size();i=i+1) {			
			Object r = execute(block.get(i),frame,framelist);			
			if(r != null) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Execute a given statement in a given stack frame.
	 * 
	 * @param stmt
	 *            Statement to execute.
	 * @param frame
	 *            Stack frame mapping variables to their current value.
	 * @return
	 */
	private Object execute(Stmt stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {		
		if(stmt instanceof Stmt.Assign) {
			return execute((Stmt.Assign) stmt,frame,framelist);
		} else if(stmt instanceof Stmt.For) {
			return execute((Stmt.For) stmt,frame,framelist);
		} else if(stmt instanceof Stmt.While) {
			return execute((Stmt.While) stmt,frame,framelist);
		} else if(stmt instanceof Stmt.IfElse) {
			return execute((Stmt.IfElse) stmt,frame,framelist);
		} else if(stmt instanceof Stmt.Return) {
			return execute((Stmt.Return) stmt,frame,framelist);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			return execute((Stmt.VariableDeclaration) stmt,frame,framelist);
		} else if(stmt instanceof Stmt.Print) {
			return execute((Stmt.Print) stmt,frame,framelist);
		} else if(stmt instanceof Expr.Invoke) {
			return execute((Expr.Invoke) stmt,frame,framelist);
		} else {
			internalFailure("unknown statement encountered (" + stmt + ")", file.filename,stmt);
			return null;
		} 
	}
	
	private Object execute(Stmt.Assign stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {
//		JvmType.Function jt = JvmType.Bool;
//		JvmType.Function ftype = new JvmType.Function(new JvmType.Bool());
//		bytecodes.add(new Bytecode.Load(0, ftype));
//		bytecodes.add(new Bytecode.Store(0, ftype));
		
		
		
		Expr lhs = stmt.getLhs();
		if(lhs instanceof Expr.Variable) {
			Expr.Variable ev = (Expr.Variable) lhs;
			Object rhs = execute(stmt.getRhs(),frame,framelist);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			frame.put(ev.getName(),deepClone(rhs));
			
			
			int pos = framelist.indexOf(ev.getName())+1;
			if(rhs instanceof Boolean){
				boolean bl = (boolean) rhs;
			bytecodes.add(new Bytecode.LoadConst(bl));
			bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_BOOL));
			}else if(rhs instanceof Integer){
				int i = (int) rhs;
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_INT));
			}
			
			
			
			
		} else if(lhs instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) lhs;
			Map<String,Object> src = (Map) execute(ra.getSource(),frame,framelist);
			Object rhs = execute(stmt.getRhs(),frame,framelist);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.put(ra.getName(), deepClone(rhs));
		} else if(lhs instanceof Expr.IndexOf) {
			Expr.IndexOf io = (Expr.IndexOf) lhs;
			ArrayList<Object> src = (ArrayList) execute(io.getSource(),frame,framelist);
			Integer idx = (Integer) execute(io.getIndex(),frame,framelist);
			Object rhs = execute(stmt.getRhs(),frame,framelist);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.set(idx,deepClone(rhs));
		} else {
			internalFailure("unknown lval encountered (" + lhs + ")", file.filename,stmt);
		}
//		
		return null;
	}
	
	private Object execute(Stmt.For stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {
		execute(stmt.getDeclaration(),frame,framelist);
		while((Boolean) execute(stmt.getCondition(),frame,framelist)) {
			Object ret = execute(stmt.getBody(),frame,framelist);
			if(ret != null) {
				return ret;
			}
			execute(stmt.getIncrement(),frame,framelist);
		}
		return null;
	}
	
	private Object execute(Stmt.While stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {
		while((Boolean) execute(stmt.getCondition(),frame,framelist)) {
			Object ret = execute(stmt.getBody(),frame,framelist);
			if(ret != null) {
				return ret;
			}
		}
		return null;
	}
	
	private Object execute(Stmt.IfElse stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {
//		bytecodes.add(new Bytecode.Load(1,JvmTypes.T_BOOL));
//		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ,""));
//		bytecodes.add(new Bytecode.LoadConst("aaa"));
//		bytecodes.add(new Bytecode.Return(JvmTypes.JAVA_LANG_STRING));
		if(stmt.getCondition() instanceof Expr.Variable){
			execute(stmt.getCondition(),frame,framelist);
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, "17"));
			execute(stmt.getTrueBranch(),frame,framelist);
			
			
			
		}
		
		
		
		
		
		
		boolean condition = (Boolean) execute(stmt.getCondition(),frame,framelist);
		
		
		
		
		if(condition) {
			return execute(stmt.getTrueBranch(),frame,framelist);
		} else {
			return execute(stmt.getFalseBranch(),frame,framelist);
		}
//		return null;
	}
	
	private Object execute(Stmt.Return stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {
		Expr re = stmt.getExpr();
		if(re != null) {
//			return execute(re,frame,framelist);
			if((re instanceof Expr.Variable)||(re instanceof Expr.Constant)){
				Object o = execute(re,frame,framelist);
				
				if(o instanceof Boolean){
					boolean oo = (boolean) o;
					
						bytecodes.add(new Bytecode.LoadConst(oo));
						bytecodes.add(new Bytecode.Return(JvmTypes.T_BOOL));
					
				}else if (o instanceof Integer){
					int oo = (int) o;
					bytecodes.add(new Bytecode.LoadConst(oo));
					bytecodes.add(new Bytecode.Return(JvmTypes.T_INT));
				}else if(o instanceof String){
					String oo = (String)o;
					bytecodes.add(new Bytecode.LoadConst(oo));
					bytecodes.add(new Bytecode.Return(JvmTypes.JAVA_LANG_STRING));
				}else{
					bytecodes.add(new Bytecode.Load(0, JvmTypes.T_INT));// jia shang leixing panduan
					bytecodes.add(new Bytecode.Return(JvmTypes.T_INT));
				}
			}else if(re instanceof Expr.IndexOf){
				Expr.IndexOf ei = (Expr.IndexOf) re;
//				Object o = execute(ei,frame,framelist);
				WhileFile.Decl wf = declarations.get(methodName);
				 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
				 JvmType jt = JvmTypes.T_VOID;
				 
				 
				 if(wfdecl.ret instanceof Type.Bool){
					 jt = JvmTypes.T_BOOL;
				 }else if(wfdecl.ret instanceof Type.Int){
					 jt = JvmTypes.T_INT;
				 }else if(wfdecl.ret instanceof Type.Real){
					 jt = JvmTypes.T_DOUBLE;
				 }else if(wfdecl.ret instanceof Type.Strung){
					 jt = JvmTypes.JAVA_LANG_STRING;
				 }else if(wfdecl.ret instanceof Type.Char){
					 jt = JvmTypes.T_CHAR;
				 }
				 
				 if(wfdecl.ret instanceof Type.Char){
				
					JvmType.Clazz owner = new JvmType.Clazz("java.lang.String");
					int pos = framelist.indexOf(((Expr.Variable)ei.getSource()).getName());
					bytecodes.add(new Bytecode.Load(pos,JvmTypes.JAVA_LANG_STRING));
					 pos = framelist.indexOf(((Expr.Variable)ei.getIndex()).getName());
					bytecodes.add(new Bytecode.Load(pos,JvmTypes.T_INT));
					bytecodes.add(new Bytecode.Invoke(owner, "charAt", new JvmType.Function(JvmTypes.T_CHAR,JvmTypes.T_INT), Bytecode.InvokeMode.VIRTUAL));
				 
				 }
				 
				 
//				 bytecodes.add(new Bytecode.LoadConst('a'));
				 bytecodes.add(new Bytecode.Return(jt));
				
			}else if(re instanceof Expr.Unary){
				Expr.Unary eu = (Expr.Unary) re;
				WhileFile.Decl wf = declarations.get(methodName);
				 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
				 JvmType jt = JvmTypes.T_VOID;
				 
				 
				 if(wfdecl.ret instanceof Type.Bool){
					 jt = JvmTypes.T_BOOL;
				 }else if(wfdecl.ret instanceof Type.Int){
					 jt = JvmTypes.T_INT;
				 }else if(wfdecl.ret instanceof Type.Real){
					 jt = JvmTypes.T_DOUBLE;
				 }
				if(eu.getOp() == Expr.UOp.NEG){
					bytecodes.add(new Bytecode.Load(0,jt));
					bytecodes.add(new Bytecode.Neg(jt));
					bytecodes.add(new Bytecode.Return(jt));
				}
			}else if(re instanceof Expr.Binary){
				Expr.Binary eb = (Expr.Binary) re;
				
				WhileFile.Decl wf = declarations.get(methodName);
				 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
				 JvmType jt = JvmTypes.T_VOID;
				 
				 
				 if(wfdecl.ret instanceof Type.Bool){
					 jt = JvmTypes.T_BOOL;
				 }else if(wfdecl.ret instanceof Type.Int){
					 jt = JvmTypes.T_INT;
				 }else if(wfdecl.ret instanceof Type.Real){
					 jt = JvmTypes.T_DOUBLE;
				 }else if(wfdecl.ret instanceof Type.Strung){
					 jt = JvmTypes.JAVA_LANG_STRING;
				 }else if(wfdecl.ret instanceof Type.Char){
					 jt = JvmTypes.T_CHAR;
				 }else if(wfdecl.ret instanceof Type.Named){
						Type.Named tn = (Type.Named)wfdecl.ret;
						jt = decltypes.get(tn.getName());
					}
				
				
				
//				if(o instanceof Double){
//					if(eb.getOp() == Expr.BOp.DIV){
//						if(eb.getLhs() instanceof Expr.Variable){
//							Expr.Variable ev = (Expr.Variable) eb.getLhs();
//							int pos = framelist.indexOf(ev.getName());
//							bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_DOUBLE));
//						}else if(eb.getLhs() instanceof Expr.Constant){
//							
//							bytecodes.add(new Bytecode.LoadConst(eb.getLhs()));
//						}
//						
//						if(eb.getRhs() instanceof Expr.Variable){
//							Expr.Variable ev = (Expr.Variable) eb.getRhs();
//							int pos = framelist.indexOf(ev.getName());
//							bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_DOUBLE));
//						}else if(eb.getRhs() instanceof Expr.Constant){
//							Expr.Constant ec = (Expr.Constant)eb.getRhs();
//							
//							bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
//						}
//						
//						bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.DIV, JvmTypes.T_DOUBLE));
//					}
//
//					bytecodes.add(new Bytecode.Return(JvmTypes.T_DOUBLE));
//				}else if(re instanceof Expr.Invoke){
//					execute(re,frame,framelist);
//					Expr.Invoke ei = (Expr.Invoke)re;
//					WhileFile.Decl wf = declarations.get(ei.getName());
//					 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
//					 JvmType jt = JvmTypes.T_VOID;
//					 if(wfdecl.ret instanceof Type.Bool){
//						 jt = JvmTypes.T_BOOL;
//					 }else if(wfdecl.ret instanceof Type.Int){
//						 jt = JvmTypes.T_INT;
//					 }else if(wfdecl.ret instanceof Type.Real){
//						 jt = JvmTypes.T_DOUBLE;
//					 }
//					 
//					 bytecodes.add(new Bytecode.Return(jt));
//				
//				}
//				 Object aa = eb.getRhs();
//				 if((eb.getOp() == Expr.BOp.DIV)&&(aa instanceof Expr.Binary)){
//					 
//				 }
				
				if(eb.getOp() == Expr.BOp.DIV){
					if(eb.getLhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getLhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getLhs() instanceof Expr.Constant){
						
						bytecodes.add(new Bytecode.LoadConst(eb.getLhs()));
					}else if(eb.getLhs() instanceof Expr.Cast){
						Expr.Cast ec = (Expr.Cast) eb.getLhs();
						if(ec.getSource() instanceof Expr.Variable){
							Expr.Variable ev = (Expr.Variable) ec.getSource();
							String name = ev.getName();
							execute((Expr.Variable)ec.getSource(),frame,framelist);
							
							
							
							
							WhileFile.Decl wf1 = declarations.get(methodName);
							 WhileFile.FunDecl wfdecl1= (WhileFile.FunDecl) wf1;
							 Type tt = null;
							 for(int i = 0; i < wfdecl1.parameters.size();i++){
								 if(wfdecl1.parameters.get(i).name.equals(name)){
									 tt = wfdecl1.parameters.get(i).type;
								 }
							 }
							 
							 
							
							 JvmType jt1 = JvmTypes.JAVA_LANG_OBJECT;
							 
							 if(tt instanceof Type.Bool){
								 jt1 = JvmTypes.T_BOOL;
							 }else if(tt instanceof Type.Int){
								 jt1 = JvmTypes.T_INT;
							 }else if(tt instanceof Type.Real){
								 jt1 = JvmTypes.T_DOUBLE;
							 }else if(tt instanceof Type.Strung){
								 jt1 = JvmTypes.JAVA_LANG_STRING;
							 }else if(tt instanceof Type.Char){
								 jt1 = JvmTypes.T_CHAR;
							 }else if(tt instanceof Type.Named){
									Type.Named tn = (Type.Named)wfdecl.ret;
									jt1 = decltypes.get(tn.getName());
							 }
							
							JvmType jt2 = JvmTypes.JAVA_LANG_OBJECT;
							
							if(ec.getType() instanceof Type.Bool){
								jt2 = JvmTypes.T_BOOL;
							}else if(ec.getType() instanceof Type.Int){
								 jt2 = JvmTypes.T_INT;
							 }else if(ec.getType() instanceof Type.Real){
								 jt2 = JvmTypes.T_DOUBLE;
							 }else if(ec.getType() instanceof Type.Strung){
								 jt2 = JvmTypes.JAVA_LANG_STRING;
							 }else if(ec.getType() instanceof Type.Char){
								 jt2 = JvmTypes.T_CHAR;
							 }else if(ec.getType() instanceof Type.Named){
									Type.Named tn = (Type.Named)wfdecl.ret;
									jt2 = decltypes.get(tn.getName());
							 }
							 
							int pos = framelist.indexOf(name);
							bytecodes.add(new Bytecode.Load(pos, jt1));
							
							
							 JvmType.Int i = new JvmType.Int();
							 JvmType.Double d = new JvmType.Double();
							 bytecodes.add(new Bytecode.Conversion(i,d));
							 
							 
							 
							 
							
						}
						
						
						
						
						
					}
					
					if(eb.getRhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getRhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getRhs() instanceof Expr.Constant){
						Expr.Constant ec = (Expr.Constant)eb.getRhs();
						
						bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
					}
					
					bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.DIV, jt));
				}else if(eb.getOp() == Expr.BOp.SUB){
					if(eb.getLhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getLhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getLhs() instanceof Expr.Constant){
						Expr.Constant ec = (Expr.Constant)eb.getLhs();
						
						bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
					}
					
					if(eb.getRhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getRhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getRhs() instanceof Expr.Constant){
						Expr.Constant ec = (Expr.Constant)eb.getRhs();
						
						bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
					}
					
					bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.SUB,jt));
				}else if(eb.getOp() == Expr.BOp.MUL){
					if(eb.getLhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getLhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getLhs() instanceof Expr.Constant){
						Expr.Constant ec = (Expr.Constant)eb.getLhs();
						
						bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
					}
					
					if(eb.getRhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getRhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getRhs() instanceof Expr.Constant){
						Expr.Constant ec = (Expr.Constant)eb.getRhs();
						
						bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
					}
					
					bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.MUL,jt));
				}else if(eb.getOp() == Expr.BOp.APPEND){
					
					bytecodes.add(new Bytecode.Load(1,JvmTypes.T_CHAR));
					
//					bytecodes.add(new Bytecode.New(JvmType.Reference()));
					JvmType.Clazz owner1 = new JvmType.Clazz("java.lang.Character");
//					bytecodes.add(new Bytecode.Invoke(owner1, "toString", new JvmType.Function(JvmTypes.JAVA_LANG_STRING), Bytecode.InvokeMode.VIRTUAL));
					bytecodes.add(new Bytecode.Load(0,JvmTypes.JAVA_LANG_STRING));
					bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.ADD, JvmTypes.JAVA_LANG_STRING));
				}
				if(eb.getOp() == Expr.BOp.ADD){
					if(eb.getLhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getLhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getLhs() instanceof Expr.Constant){
						
						bytecodes.add(new Bytecode.LoadConst(eb.getLhs()));
					}
					
					if(eb.getRhs() instanceof Expr.Variable){
						Expr.Variable ev = (Expr.Variable) eb.getRhs();
						int pos = framelist.indexOf(ev.getName());
						bytecodes.add(new Bytecode.Load(pos, jt));
					}else if(eb.getRhs() instanceof Expr.Constant){
						Expr.Constant ec = (Expr.Constant)eb.getRhs();
						
						bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
					}
					
					bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.ADD, jt));
				}

				bytecodes.add(new Bytecode.Return(jt));
			}else if(re instanceof Expr.Invoke){
				execute(re,frame,framelist);
				Expr.Invoke ei = (Expr.Invoke)re;
				WhileFile.Decl wf = declarations.get(ei.getName());
				 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
				 JvmType jt = JvmTypes.T_VOID;
				 if(wfdecl.ret instanceof Type.Bool){
					 jt = JvmTypes.T_BOOL;
				 }else if(wfdecl.ret instanceof Type.Int){
					 jt = JvmTypes.T_INT;
				 }else if(wfdecl.ret instanceof Type.Real){
					 jt = JvmTypes.T_DOUBLE;
				 }
				 
				 bytecodes.add(new Bytecode.Return(jt));
			}
			
			return null;
			
		} else {
			return Collections.EMPTY_SET; // used to indicate a function has returned
		}
	}
	
	private Object execute(Stmt.VariableDeclaration stmt,
			HashMap<String, Object> frame, ArrayList<String> framelist) {
		Expr re = stmt.getExpr();
		Object value;
		if (re != null) {
			value = execute(re, frame,framelist);
		} else {
			value = Collections.EMPTY_SET; // used to indicate a variable has
											// been declared
		}
		// We need to perform a deep clone here to ensure the value
		// semantics used in While are preserved.
		frame.put(stmt.getName(), deepClone(value));
		framelist.add(stmt.getName());
//		if(stmt.getType() instanceof Type.Bool){
//			if(re instanceof Expr.Constant){
//				if(((Expr.Constant) re).getValue().equals("true")){
//					JvmType.Function ftype = new JvmType.Function(new JvmType.Bool());
//					bytecodes.add(new Bytecode.LoadConst(1));
//					bytecodes.add(new Bytecode.Store(1, ftype));
//				}
//			}
//		}
//		
		if(re instanceof Expr.Binary){
			Expr.Binary eb = (Expr.Binary) re;
			if(eb.getOp() == Expr.BOp.MUL){
				if(eb.getLhs() instanceof Expr.Variable){
					Expr.Variable ev = (Expr.Variable) eb.getLhs();
					int pos = framelist.indexOf(ev.getName())+1;
					bytecodes.add(new Bytecode.Load(0, JvmTypes.T_INT));
				}else if(eb.getLhs() instanceof Expr.Constant){
					
					bytecodes.add(new Bytecode.LoadConst(eb.getLhs()));
				}
				
				if(eb.getRhs() instanceof Expr.Variable){
					Expr.Variable ev = (Expr.Variable) eb.getRhs();
					int pos = framelist.indexOf(ev.getName())+1;
					bytecodes.add(new Bytecode.Load(1, JvmTypes.T_INT));
				}else if(eb.getRhs() instanceof Expr.Constant){
					Expr.Constant ec = (Expr.Constant)eb.getRhs();
					
					bytecodes.add(new Bytecode.LoadConst(ec.getValue()));
				}
				
//				bytecodes.add(new Bytecode.Load(0, JvmTypes.T_INT));
//				bytecodes.add(new Bytecode.Load(1, JvmTypes.T_INT));
				bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.DIV, JvmTypes.T_INT));
			}
		}
		
		
		int pos = framelist.indexOf(stmt.getName())+1;
		if(value instanceof Boolean){
			boolean bl = (boolean) value;
			bytecodes.add(new Bytecode.LoadConst(bl));
			bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_BOOL));
		}else if(value instanceof Integer){
			int i = (int) value;
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_INT));
		}else{
			if(re instanceof Expr.Invoke){
				Expr.Invoke ei = (Expr.Invoke)re;
				
				 WhileFile.Decl wf = declarations.get(ei.getName());
				 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
				 
				 JvmType jt = JvmTypes.T_VOID;
				 if(wfdecl.ret instanceof Type.Bool){
					 jt = JvmTypes.T_BOOL;
				 }else if(wfdecl.ret instanceof Type.Int){
					 jt = JvmTypes.T_INT;
				 }
				bytecodes.add(new Bytecode.Store(pos, jt));
			}
			
		}
		
		
		
		return null;
	}
	
	private Object execute(Stmt.Print stmt, HashMap<String,Object> frame, ArrayList<String> framelist) {
//		String str = toString(execute(stmt.getExpr(),frame));
//		System.out.println(str);
//		JvmType.Function ftype = new JvmType.Function(new JvmType.Bool());
		
//		bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
//				JAVA_IO_PRINTSTREAM,
//				Bytecode.FieldMode.STATIC));
//		
//		bytecodes.add(new Bytecode.Load(1, ftype));
//		
//		bytecodes
//		.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
//				new JvmType.Function(JvmTypes.T_VOID,
//						JvmTypes.JAVA_LANG_STRING),
//						Bytecode.InvokeMode.VIRTUAL));
		
		
		
		if(stmt.getExpr() instanceof Expr.Variable) {
			Expr.Variable ev = (Expr.Variable) stmt.getExpr();
			Object value = execute(stmt.getExpr(),frame,framelist);

			
			int pos = framelist.indexOf(ev.getName())+1;
			if(value instanceof Boolean){
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				
				bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_BOOL));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.T_BOOL),
								Bytecode.InvokeMode.VIRTUAL));
			}else if(value instanceof Integer){
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				
				bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_INT));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.T_INT),
								Bytecode.InvokeMode.VIRTUAL));
			}else if(value instanceof Double){
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				
				bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_DOUBLE));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.T_DOUBLE),
								Bytecode.InvokeMode.VIRTUAL));
			}else if(value instanceof String){
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				
				bytecodes.add(new Bytecode.Load(pos, JvmTypes.JAVA_LANG_STRING));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.JAVA_LANG_STRING),
								Bytecode.InvokeMode.VIRTUAL));
			}else if(value instanceof ArrayList){
				ArrayList a = (ArrayList)value;
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				
				bytecodes.add(new Bytecode.LoadConst(a.toString()));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.JAVA_LANG_STRING),
								Bytecode.InvokeMode.VIRTUAL));
			}else{
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				
				bytecodes.add(new Bytecode.Load(1, JvmTypes.T_INT));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.T_INT),
								Bytecode.InvokeMode.VIRTUAL));
			}
	
		}else if(stmt.getExpr() instanceof Expr.Constant){
//			Object value = stmt.getExpr().toString();
			Object value = execute(stmt.getExpr(),frame,framelist);
			bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
					JAVA_IO_PRINTSTREAM,
					Bytecode.FieldMode.STATIC));
			
			bytecodes.add(new Bytecode.LoadConst(value));
			
			bytecodes
			.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
					new JvmType.Function(JvmTypes.T_VOID,
							JvmTypes.JAVA_LANG_STRING),
							Bytecode.InvokeMode.VIRTUAL));
		}else if(stmt.getExpr() instanceof Expr.Unary){
			Object value = execute(stmt.getExpr(),frame,framelist);
			
			if(value instanceof Boolean){
				bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
				boolean bl = (boolean) value;
				bytecodes.add(new Bytecode.LoadConst(bl));
				
				bytecodes
				.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
						new JvmType.Function(JvmTypes.T_VOID,
								JvmTypes.T_BOOL),
								Bytecode.InvokeMode.VIRTUAL));
			}
		}else if(stmt.getExpr() instanceof Expr.Binary){
			Object value = execute(stmt.getExpr(),frame,framelist);
			bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
					JAVA_IO_PRINTSTREAM,
					Bytecode.FieldMode.STATIC));
			
			bytecodes.add(new Bytecode.LoadConst(value));
			
			bytecodes
			.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
					new JvmType.Function(JvmTypes.T_VOID,
							JvmTypes.T_DOUBLE),
							Bytecode.InvokeMode.VIRTUAL));
		}else if(stmt.getExpr() instanceof Expr.Invoke){
			Expr.Invoke ei = (Expr.Invoke) stmt.getExpr();
			 JvmType.Clazz owner = new JvmType.Clazz(sourceFile.filename.substring(0,sourceFile.filename.lastIndexOf('.')));
			 bytecodes.add(new Bytecode.GetField(JAVA_LANG_SYSTEM, "out",
						JAVA_IO_PRINTSTREAM,
						Bytecode.FieldMode.STATIC));
			 WhileFile.Decl wf = declarations.get(ei.getName());
			 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
			 
			 ArrayList<JvmType> pl = new ArrayList<JvmType>();
				for(int i = 0; i < wfdecl.parameters.size();i++){
					Object o = wfdecl.parameters.get(i);
					WhileFile.Parameter wp = (WhileFile.Parameter) o;
					if(wp.type instanceof Type.Bool){
						pl.add(JvmTypes.T_BOOL);
					}else if (wp.type instanceof Type.Int){
						pl.add(JvmTypes.T_INT);
					}else if(wp.type instanceof Type.Real){
						pl.add(JvmTypes.T_DOUBLE);
					}else if(wp.type instanceof Type.Strung){
						pl.add(JvmTypes.JAVA_LANG_STRING);
					}else if(wp.type instanceof Type.Char){
						pl.add(JvmTypes.T_CHAR);
					}else if(wp.type instanceof Type.Named){
						Type.Named tn = (Type.Named)wp.type;
						pl.add(decltypes.get(tn.getName()));
					}
				}
			 
			 
			 
			 JvmType jt = JvmTypes.T_VOID;
			 if(wfdecl.ret instanceof Type.Bool){
				 jt = JvmTypes.T_BOOL;
			 }else if(wfdecl.ret instanceof Type.Int){
				 jt = JvmTypes.T_INT;
			 }else if(wfdecl.ret instanceof Type.Real){
					jt = JvmTypes.T_DOUBLE;
			 }else if(wfdecl.ret instanceof Type.Strung){
				 jt = JvmTypes.JAVA_LANG_STRING;
			 }else if(wfdecl.ret instanceof Type.Char){
				 jt = JvmTypes.T_CHAR;
			 }else if(wfdecl.ret instanceof Type.Named){
				 Type.Named tn = (Type.Named)wfdecl.ret;
				 jt = decltypes.get(tn.getName());
			 }
			 
			 
			 List<Expr> arguments = ei.getArguments();
				Object[] values = new Object[arguments.size()];
				for (int i = 0; i != values.length; ++i) {
					// We need to perform a deep clone here to ensure the value
					// semantics used in While are preserved.
					values[i] = deepClone(execute(arguments.get(i), frame,framelist));
				}
			 
				for(int i = 0; i != values.length; ++i){
					if(values[i] instanceof Integer){
						bytecodes.add(new Bytecode.LoadConst(values[i]));
//						bytecodes.add(new Bytecode.LoadConst(1));
					}else if(values[i] instanceof Double){
						bytecodes.add(new Bytecode.LoadConst(values[i]));
					}else if(values[i] instanceof String){
						bytecodes.add(new Bytecode.LoadConst(values[i]));
					}else if(values[i] instanceof Character){
						bytecodes.add(new Bytecode.LoadConst(values[i]));
					}
				}
			 
			 
			 
			 bytecodes.add(new Bytecode.Invoke(owner, ei.getName(), new JvmType.Function(jt,pl), Bytecode.InvokeMode.STATIC));
			
			bytecodes
			.add(new Bytecode.Invoke(JAVA_IO_PRINTSTREAM, "println",
					new JvmType.Function(JvmTypes.T_VOID,
							jt),
							Bytecode.InvokeMode.VIRTUAL));
		}
		
		
		
		
		
		
		
		return null;
	}
	
	/**
	 * Execute a given expression in a given stack frame.
	 * 
	 * @param expr
	 *            Expression to execute.
	 * @param frame
	 *            Stack frame mapping variables to their current value.
	 * @return
	 */
	private Object execute(Expr expr, HashMap<String,Object> frame, ArrayList<String> framelist) {
		if(expr instanceof Expr.Binary) {
			return execute((Expr.Binary) expr,frame,framelist);
		} else if(expr instanceof Expr.Cast) {
			return execute((Expr.Cast) expr,frame,framelist);
		} else if(expr instanceof Expr.Constant) {
			return execute((Expr.Constant) expr,frame,framelist);
		} else if(expr instanceof Expr.Invoke) {
			return execute((Expr.Invoke) expr,frame,framelist);
		} else if(expr instanceof Expr.IndexOf) {
			return execute((Expr.IndexOf) expr,frame,framelist);
		} else if(expr instanceof Expr.ListConstructor) {
			return execute((Expr.ListConstructor) expr,frame,framelist);
		} else if(expr instanceof Expr.RecordAccess) {
			return execute((Expr.RecordAccess) expr,frame,framelist);
		} else if(expr instanceof Expr.RecordConstructor) {
			return execute((Expr.RecordConstructor) expr,frame,framelist);
		} else if(expr instanceof Expr.Unary) {
			return execute((Expr.Unary) expr,frame,framelist);
		} else if(expr instanceof Expr.Variable) {
			return execute((Expr.Variable) expr,frame,framelist);
		} else {
			internalFailure("unknown expression encountered (" + expr + ")", file.filename,expr);
			return null;
		} 
	}
	
	private Object execute(Expr.Binary expr, HashMap<String,Object> frame, ArrayList<String> framelist) {
		// First, deal with the short-circuiting operators first
		Object lhs = execute(expr.getLhs(), frame,framelist);
		
		switch (expr.getOp()) {
		case AND:
			return ((Boolean)lhs) && ((Boolean)execute(expr.getRhs(), frame,framelist));
		case OR:
			return ((Boolean)lhs) || ((Boolean)execute(expr.getRhs(), frame,framelist));
		}
		
		// Second, deal the rest.		
		Object rhs = execute(expr.getRhs(), frame,framelist);
		
		switch (expr.getOp()) {
		case ADD:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) + ((Integer)rhs);
			} else {
				return ((Double)lhs) + ((Double)rhs);
			}
		case SUB:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) - ((Integer)rhs);
			} else {
				return ((Double)lhs) - ((Double)rhs);
			}
		case MUL:
//			if(expr.getLhs() instanceof Expr.Variable){
//				Expr.Variable ev = (Expr.Variable) expr.getLhs();
//				int pos = framelist.indexOf(ev.getName());
//				bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_INT));
//			}else if(expr.getLhs() instanceof Expr.Constant){
//				
//				bytecodes.add(new Bytecode.LoadConst(expr.getLhs()));
//			}
//			
//			if(expr.getRhs() instanceof Expr.Variable){
//				Expr.Variable ev = (Expr.Variable) expr.getRhs();
//				int pos = framelist.indexOf(ev.getName());
//				bytecodes.add(new Bytecode.Load(pos, JvmTypes.T_INT));
//			}else if(expr.getRhs() instanceof Expr.Constant){
//				
//				bytecodes.add(new Bytecode.LoadConst(expr.getRhs()));
//			}
//			
////			bytecodes.add(new Bytecode.Load(0, JvmTypes.T_INT));
////			bytecodes.add(new Bytecode.Load(1, JvmTypes.T_INT));
//			bytecodes.add(new Bytecode.BinOp(Bytecode.BinOp.DIV, JvmTypes.T_INT));
			if(lhs instanceof Integer) {
				return ((Integer)lhs) * ((Integer)rhs);
			} else {
				return ((Double)lhs) * ((Double)rhs);
			}
		case DIV:
			if(lhs instanceof Integer) {
				
				
				
				
				return ((Integer)lhs) / ((Integer)rhs);
			} else {
				return ((Double)lhs) / ((Double)rhs);
			}
		case REM:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) % ((Integer)rhs);
			} else {
				return ((Double)lhs) % ((Double)rhs);
			}
		case EQ:
			return lhs.equals(rhs);
		case NEQ:
			return !lhs.equals(rhs);
		case LT:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) < ((Integer)rhs);
			} else {
				return ((Double)lhs) < ((Double)rhs);
			}
		case LTEQ:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) <= ((Integer)rhs);
			} else {
				return ((Double)lhs) <= ((Double)rhs);
			}
		case GT:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) > ((Integer)rhs);
			} else {
				return ((Double)lhs) > ((Double)rhs);
			}
		case GTEQ:
			if(lhs instanceof Integer) {
				return ((Integer)lhs) >= ((Integer)rhs);
			} else {
				return ((Double)lhs) >= ((Double)rhs);
			}
		case APPEND:
			if(lhs instanceof String && rhs instanceof String) {
				return ((String)lhs) + ((String)rhs);
			} else if(lhs instanceof String) {
				return ((String)lhs) + toString(rhs);
			} else if(rhs instanceof String) {
				return toString(lhs) + ((String)rhs);
			} else if(lhs instanceof ArrayList && rhs instanceof ArrayList) {
				ArrayList l = (ArrayList) lhs;
				l.addAll((ArrayList)rhs);
				return l;
			}
		}

		internalFailure("unknown binary expression encountered (" + expr + ")",
				file.filename, expr);
		return null;
	}
	
	private Object execute(Expr.Cast expr, HashMap<String, Object> frame, ArrayList<String> framelist) {
		Object rhs = execute(expr.getSource(), frame,framelist);		
		// TODO: we need to actually implement casting here!
		return rhs;
	}
	
	private Object execute(Expr.Constant expr, HashMap<String,Object> frame, ArrayList<String> framelist) {
		return expr.getValue();
	}
	
	private Object execute(Expr.Invoke expr, HashMap<String, Object> frame, ArrayList<String> framelist) {
//		List<Expr> arguments = expr.getArguments();
//		Object[] values = new Object[arguments.size()];
//		for (int i = 0; i != values.length; ++i) {
//			// We need to perform a deep clone here to ensure the value
//			// semantics used in While are preserved.
//			values[i] = deepClone(execute(arguments.get(i), frame,framelist));
//		}
//		WhileFile.FunDecl fun = (WhileFile.FunDecl) declarations.get(expr
//				.getName());
//		return execute(fun, values);
		
		
//		Expr.Invoke ei = (Expr.Invoke) expr;
//		 JvmType.Clazz owner = new JvmType.Clazz(sourceFile.filename.substring(0,sourceFile.filename.lastIndexOf('.')));
//		 
//		 
//		bytecodes.add(new Bytecode.Invoke(owner, ei.getName(), new JvmType.Function(JvmTypes.T_VOID), Bytecode.InvokeMode.STATIC));
		
	
		Expr.Invoke ei = (Expr.Invoke) expr;
		 JvmType.Clazz owner = new JvmType.Clazz(sourceFile.filename.substring(0,sourceFile.filename.lastIndexOf('.')));
		 
		 WhileFile.Decl wf = declarations.get(ei.getName());
		 WhileFile.FunDecl wfdecl= (WhileFile.FunDecl) wf;
		 
		 ArrayList<JvmType> pl = new ArrayList<JvmType>();
			for(int i = 0; i < wfdecl.parameters.size();i++){
				Object o = wfdecl.parameters.get(i);
				WhileFile.Parameter wp = (WhileFile.Parameter) o;
				if(wp.type instanceof Type.Bool){
					pl.add(JvmTypes.T_BOOL);
				}else if (wp.type instanceof Type.Int){
					pl.add(JvmTypes.T_INT);
				}else if(wp.type instanceof Type.Real){
					pl.add(JvmTypes.T_DOUBLE);
				}else if(wp.type instanceof Type.Char){
					pl.add(JvmTypes.T_CHAR);
				}else if(wp.type instanceof Type.Named){
					Type.Named tn = (Type.Named)wp.type;
					pl.add(decltypes.get(tn.getName()));
				}
			}
		 
		 
		 
		 JvmType jt = JvmTypes.T_VOID;
		 if(wfdecl.ret instanceof Type.Bool){
			 jt = JvmTypes.T_BOOL;
		 }else if(wfdecl.ret instanceof Type.Int){
			 jt = JvmTypes.T_INT;
		 }else if(wfdecl.ret instanceof Type.Real){
			 jt = JvmTypes.T_DOUBLE;
		 }else if(wfdecl.ret instanceof Type.Char){
			 jt = JvmTypes.T_CHAR;
		 }else if(wfdecl.ret instanceof Type.Named){
				Type.Named tn = (Type.Named)wfdecl.ret;
				jt = decltypes.get(tn.getName());
		 }
		 
		 
		 List<Expr> arguments = ei.getArguments();
			Object[] values = new Object[arguments.size()];
			for (int i = 0; i != values.length; ++i) {
				// We need to perform a deep clone here to ensure the value
				// semantics used in While are preserved.
				values[i] = deepClone(execute(arguments.get(i), frame,framelist));
			}
		 
			for(int i = 0; i != values.length; ++i){
//				if(values[i] instanceof Integer){
					if(values[i] == null){
						bytecodes.add(new Bytecode.Load(0,JvmTypes.T_INT));//xu yao xiu gai
					}else{
						bytecodes.add(new Bytecode.LoadConst(values[i]));
					}
//					bytecodes.add(new Bytecode.Load(0,JvmTypes.T_INT));
//				}
			}
		 
		 
		 
		 bytecodes.add(new Bytecode.Invoke(owner, ei.getName(), new JvmType.Function(jt,pl), Bytecode.InvokeMode.STATIC));
		
		
		
		
		
		return null;
	}
	
	private Object execute(Expr.IndexOf expr, HashMap<String,Object> frame, ArrayList<String> framelist) {
		Object _src = execute(expr.getSource(),frame,framelist);
		int idx = (Integer) execute(expr.getIndex(),frame,framelist);
		if(_src instanceof String) {
//			Expr.IndexOf ei = (Expr.IndexOf)expr;
//			JvmType.Clazz owner = new JvmType.Clazz("java.lang.String");
//			int pos = framelist.indexOf(ei.getSource())+1;
//			bytecodes.add(new Bytecode.Load(pos,JvmTypes.T_LONG));
//			 pos = framelist.indexOf(ei.getIndex())+1;
//			bytecodes.add(new Bytecode.Load(pos,JvmTypes.T_INT));
//			bytecodes.add(new Bytecode.Invoke(owner, "charAt", new JvmType.Function(JvmTypes.JAVA_LANG_CHARACTER,JvmTypes.JAVA_LANG_STRING), Bytecode.InvokeMode.VIRTUAL));
			
			String src = (String) _src;
			return src.charAt(idx);
		} else {
			ArrayList<Object> src = (ArrayList<Object>) _src;
			return src.get(idx);
		}
	}
	
	private Object execute(Expr.ListConstructor expr,
			HashMap<String, Object> frame, ArrayList<String> framelist) {
		List<Expr> es = expr.getArguments();
		ArrayList<Object> ls = new ArrayList<Object>();
		for (int i = 0; i != es.size(); ++i) {
			ls.add(execute(es.get(i), frame,framelist));
		}
		return ls;
	}
	
	private Object execute(Expr.RecordAccess expr, HashMap<String, Object> frame, ArrayList<String> framelist) {
		HashMap<String, Object> src = (HashMap) execute(expr.getSource(), frame,framelist);
		return src.get(expr.getName());
	}
	
	private Object execute(Expr.RecordConstructor expr, HashMap<String,Object> frame, ArrayList<String> framelist) {
		List<Pair<String,Expr>> es = expr.getFields();
		HashMap<String,Object> rs = new HashMap<String,Object>();
		
		for(Pair<String,Expr> e : es) {
			rs.put(e.first(),execute(e.second(),frame,framelist));
		}
		
		return rs;
	}
	
	private Object execute(Expr.Unary expr, HashMap<String, Object> frame, ArrayList<String> framelist) {
		Object value = execute(expr.getExpr(), frame,framelist);
		switch (expr.getOp()) {
		case NOT:
			return !((Boolean) value);
		case NEG:
			if (value instanceof Double) {
				return -((Double) value);
			} else {
				return -((Integer) value);
			}
		case LENGTHOF:
			if(value instanceof String) {
				return ((String) value).length();
			} else {
				return ((ArrayList) value).size();
			}
		}

		internalFailure("unknown unary expression encountered (" + expr + ")",
				file.filename, expr);
		return null;
	}
	
	private Object execute(Expr.Variable expr, HashMap<String,Object> frame, ArrayList<String> framelist) {
		Expr.Variable ev = (Expr.Variable) expr;
		String name = ev.getName();
		Object o = frame.get(name);
		
		if(o instanceof Integer){
			int i = (int)o;
			
		}else if(o instanceof Expr.Constant){
			Expr.Constant ec = (Expr.Constant)o;
			int pos = framelist.indexOf(name)+1;
			Object oo = ec.getValue();
			if(oo instanceof Double){
				bytecodes.add(new Bytecode.LoadConst(oo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_DOUBLE));
			}else if(oo instanceof Integer){
				bytecodes.add(new Bytecode.LoadConst(oo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_INT));
			}else if(oo instanceof String){
				bytecodes.add(new Bytecode.LoadConst(oo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.JAVA_LANG_STRING));
			}else if(oo instanceof Boolean){
				bytecodes.add(new Bytecode.LoadConst(oo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_BOOL));
			}else if(oo instanceof Character){
				bytecodes.add(new Bytecode.LoadConst(oo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_CHAR));
			}else if(oo instanceof Expr.Constant){
				
				execute((Expr.Constant)oo,frame,framelist);
			}else if(oo instanceof Expr.Variable){
				execute((Expr.Variable)oo,frame,framelist);
			}else if(oo instanceof Expr.Binary){
				Object ooo = execute((Expr.Binary)oo,frame,framelist);
				if(ooo instanceof Double){
					bytecodes.add(new Bytecode.LoadConst(ooo));
					bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_DOUBLE));
				}else if(ooo instanceof Integer){
					bytecodes.add(new Bytecode.LoadConst(ooo));
					bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_INT));
				}else if(ooo instanceof String){
					bytecodes.add(new Bytecode.LoadConst(ooo));
					bytecodes.add(new Bytecode.Store(pos, JvmTypes.JAVA_LANG_STRING));
				}else if(ooo instanceof Boolean){
					bytecodes.add(new Bytecode.LoadConst(ooo));
					bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_BOOL));
				}else if(ooo instanceof Character){
					bytecodes.add(new Bytecode.LoadConst(ooo));
					bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_CHAR));
				}
			}
			
			return ec.getValue();
		}else if(o instanceof Expr.Binary){
			int pos = framelist.indexOf(name)+1;
			Object ooo = execute((Expr.Binary)o,frame,framelist);
			if(ooo instanceof Double){
				bytecodes.add(new Bytecode.LoadConst(ooo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_DOUBLE));
			}else if(ooo instanceof Integer){
				bytecodes.add(new Bytecode.LoadConst(ooo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_INT));
			}else if(ooo instanceof String){
				bytecodes.add(new Bytecode.LoadConst(ooo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.JAVA_LANG_STRING));
			}else if(ooo instanceof Boolean){
				bytecodes.add(new Bytecode.LoadConst(ooo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_BOOL));
			}else if(ooo instanceof Character){
				bytecodes.add(new Bytecode.LoadConst(ooo));
				bytecodes.add(new Bytecode.Store(pos, JvmTypes.T_CHAR));
			}
			return ooo;
		}
		
		
		
		
		
		return frame.get(expr.getName());
	}
	
	/**
	 * Perform a deep clone of the given object value. This is either a
	 * <code>Boolean</code>, <code>Integer</code>, <code>Double</code>,
	 * <code>Character</code>, <code>String</code>, <code>ArrayList</code> (for
	 * lists) or <code>HaspMap</code> (for records). Only the latter two need to
	 * be cloned, since the others are immutable.
	 * 
	 * @param o
	 * @return
	 */
	private Object deepClone(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> l = (ArrayList) o;
			ArrayList<Object> n = new ArrayList<Object>();
			for (int i = 0; i != l.size(); ++i) {
				n.add(deepClone(l.get(i)));
			}
			return n;
		} else if (o instanceof HashMap) {
			HashMap<String, Object> m = (HashMap) o;
			HashMap<String, Object> n = new HashMap<String, Object>();
			for (String field : m.keySet()) {
				n.put(field, deepClone(m.get(field)));
			}
			return n;
		} else {
			// other cases can be ignored
			return o;
		}
	}
	
	/**
	 * Convert the given object value to a string. This is either a
	 * <code>Boolean</code>, <code>Integer</code>, <code>Double</code>,
	 * <code>Character</code>, <code>String</code>, <code>ArrayList</code> (for
	 * lists) or <code>HaspMap</code> (for records). The latter two must be
	 * treated recursively.
	 * 
	 * @param o
	 * @return
	 */
	private String toString(Object o) {
		if (o instanceof ArrayList) {
			ArrayList<Object> l = (ArrayList) o;
			String r = "[";
			for (int i = 0; i != l.size(); ++i) {
				if(i != 0) {
					r = r + ", ";
				}
				r += toString(l.get(i));
			}
			return r + "]";
		} else if (o instanceof HashMap) {
			HashMap<String, Object> m = (HashMap) o;
			String r = "{";
			boolean firstTime = true;
			ArrayList<String> fields = new ArrayList<String>(m.keySet());
			Collections.sort(fields);
			for (String field : fields) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += field + ":" + toString(m.get(field));
			}
			return r + "}";
		} else if(o != null) {
			// other cases can use their default toString methods.
			return o.toString();
		} else {
			return "null";
		}
	}
	
	
	
	
}
