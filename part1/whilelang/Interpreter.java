// This file is part of the WhileLang Compiler (wlc).
//
// The WhileLang Compiler is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// The WhileLang Compiler is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE. See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public
// License along with the WhileLang Compiler. If not, see
// <http://www.gnu.org/licenses/>
//
// Copyright 2013, David James Pearce.

package whilelang;

import java.util.*;

import whilelang.lang.*;
import whilelang.util.Pair;
import static whilelang.util.SyntaxError.*;

/**
 * A simple interpreter for WhileLang programs, which executes them in their
 * Abstract Syntax Tree form directly. The interpreter is not designed to be
 * efficient in anyway, however it's purpose is to provide a reference
 * implementation for the language.
 * 
 * @author David J. Pearce
 * 
 */
public class Interpreter {
	private HashMap<String, WhileFile.Decl> declarations;
	private HashMap<String,Expr> cons; //xiu gai guo
	private WhileFile file;
	
	public void run(WhileFile wf) {
		// First, initialise the map of declaration names to their bodies.
		declarations = new HashMap<String,WhileFile.Decl>();
		for(WhileFile.Decl decl : wf.declarations) {
			declarations.put(decl.name(), decl);
		}
		this.file = wf;
		
		cons = new HashMap<String,Expr>();//xiu gai guo
		for(WhileFile.Decl decl : wf.declarations){
		
		if(decl instanceof WhileFile.ConstDecl){

			cons.put(decl.name(), ((WhileFile.ConstDecl)decl).constant);
			
			
			
			}
		}// xiu gai guo
		
		

		
		
		
		// Second, pick the main method (if one exits) and execute it
		WhileFile.Decl main = declarations.get("main");
		if(main instanceof WhileFile.FunDecl) {
			WhileFile.FunDecl fd = (WhileFile.FunDecl) main;
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
		if(function.parameters.size() != arguments.length){
			throw new RuntimeException(
					"invalid number of arguments supplied to execution of function \""
							+ function.name + "\"");
		}
		
		// Second, construct the stack frame in which this function will
		// execute.
		HashMap<String,Object> frame = new HashMap<String,Object>();
		for(int i=0;i!=arguments.length;++i) {
			WhileFile.Parameter parameter = function.parameters.get(i);
			frame.put(parameter.name,arguments[i]);
		}
		
		// Third, execute the function body!
		return execute(function.statements,frame);
	}
	
	private Object execute(List<Stmt> block, HashMap<String,Object> frame) {
		for(int i=0;i!=block.size();i=i+1) {			
			Object r = execute(block.get(i),frame);			
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
	private Object execute(Stmt stmt, HashMap<String,Object> frame) {		
		
		if(stmt instanceof Stmt.Assign) {
			
			return execute((Stmt.Assign) stmt,frame);
		} else if(stmt instanceof Stmt.For) {
			return execute((Stmt.For) stmt,frame);
		} else if(stmt instanceof Stmt.Switch){//xiu gai guo xin jia de
			
			return execute((Stmt.Switch) stmt,frame);
		} else if(stmt instanceof Stmt.Break){
			
			return new Stmt.Break();
		} else if(stmt instanceof Stmt.While) {
			return execute((Stmt.While) stmt,frame);
		} else if(stmt instanceof Stmt.IfElse) {
			return execute((Stmt.IfElse) stmt,frame);
		} else if(stmt instanceof Stmt.Return) {
			return execute((Stmt.Return) stmt,frame);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			return execute((Stmt.VariableDeclaration) stmt,frame);
		} else if(stmt instanceof Stmt.Print) {

			return execute((Stmt.Print) stmt,frame);
		} else if(stmt instanceof Expr.Invoke) {
			return execute((Expr.Invoke) stmt,frame);
		} else {
		
			internalFailure("unknown statement encountered (" + stmt + ")", file.filename,stmt);
			return null;
		} 
	}
	
	private Object execute(Stmt.Assign stmt, HashMap<String,Object> frame) {	
		Expr lhs = stmt.getLhs();
		
		if(lhs instanceof Expr.Variable) {
			
			Expr.Variable ev = (Expr.Variable) lhs;
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			frame.put(ev.getName(),deepClone(rhs));
		} else if(lhs instanceof Expr.RecordAccess) {
			
			Expr.RecordAccess ra = (Expr.RecordAccess) lhs;
			Map<String,Object> src = (Map) execute(ra.getSource(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.put(ra.getName(), deepClone(rhs));
		} else if(lhs instanceof Expr.IndexOf) {
		
			
			
			Expr.IndexOf io = (Expr.IndexOf) lhs;
			Object o = execute(io.getSource(),frame);

			if(o.getClass().equals(String.class)){
				String s = (String) o;
				char[] c = s.toCharArray();
				Integer idx = (Integer) execute(io.getIndex(),frame);
				Object rhs = execute(stmt.getRhs(),frame);
				
				c[idx] = (Character)rhs;
				final String ss = String.valueOf(c);

				
				

				String aa = io.getSource().toString();

				frame.remove(aa);

				
				frame.put(aa, ss);

				
				
				

			}else{
				
			ArrayList<Object> src = (ArrayList) execute(io.getSource(),frame);
			Integer idx = (Integer) execute(io.getIndex(),frame);
			Object rhs = execute(stmt.getRhs(),frame);
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			src.set(idx,deepClone(rhs));}
		} else {
			internalFailure("unknown lval encountered (" + lhs + ")", file.filename,stmt);
		}
		
		return null;
	}
	
	private Object execute(Stmt.For stmt, HashMap<String,Object> frame) {
		execute(stmt.getDeclaration(),frame);
		while((Boolean) execute(stmt.getCondition(),frame)) {
			Object ret = execute(stmt.getBody(),frame);
			if(ret != null) {
				return ret;
			}
			execute(stmt.getIncrement(),frame);
		}
		return null;
	}
	
	private Object execute(Stmt.While stmt, HashMap<String,Object> frame) {
		while((Boolean) execute(stmt.getCondition(),frame)) {
			Object ret = execute(stmt.getBody(),frame);
			if(ret != null) {
				return ret;
			}
		}
		return null;
	}
	
	
	private Object execute(Stmt.Switch stmt, HashMap<String,Object> frame){
		int size = stmt.getStatements().size();
		Stmt.Default df = null;
		for(int y = 0; y < size; y++){
			if(stmt.getStatements().get(y) instanceof Stmt.Default){
				df = (Stmt.Default)stmt.getStatements().get(y);
				break;
			}
		}




		
		for(int i = 0; i < size; i++){
			
			if(stmt.getStatements().get(i) instanceof Stmt.Default){
				
				continue;
			}
			Expr switchver = stmt.getCondition();

			Object o = execute((Expr)switchver,frame);
			
			Stmt.Case c = (Stmt.Case)stmt.getStatements().get(i);
			
			
			
			Object oo = execute((Expr)(c.getCondition()),frame);

			if(o instanceof Integer){
				int n = (Integer)(o);
				
				int m = (Integer)(oo);
				

				if(n == m){

					
					Object j =execute((List<Stmt>)(c.getStatements()),frame);
					if(j instanceof Stmt.Break){
						df = null;
						break;
					}
					return j;
					
				}
			}else if(o instanceof Double){
				double n = (Double)(o);
				
				double m = (Double)(oo);
				

				if(n == m){

					
					Object j =execute((List<Stmt>)(c.getStatements()),frame);
					if(j instanceof Stmt.Break){
						df = null;
						break;
					}
					return j;
					
				}
			}else if(o instanceof List){
				
				List n = (List)(o);
				
				List m = (List)(oo);
				if(n.equals(m)){
					Object j =execute((List<Stmt>)(c.getStatements()),frame);
					if(j instanceof Stmt.Break){
						df = null;
						break;
					}
					return j;
				}
			}else if(o.getClass().equals(Character.class)){
				
				Character n = (Character)(o);
				
				Character m = (Character)(oo);
				

				if(n.equals(m)){
					
					
					Object j =execute((List<Stmt>)(c.getStatements()),frame);
					if(j instanceof Stmt.Break){
						
						df = null;
						break;
					}
					return j;
					
				}
			}
			
			
		}

		if(df != null){
			Object z = execute((List<Stmt>)(df.getStatements()),frame);
			if(z instanceof Stmt.Break){
				
				df = null;
				return null;
			}
			return z;
		}else{
			return null;
		}
		

	}
	
	
	
	
	private Object execute(Stmt.IfElse stmt, HashMap<String,Object> frame) {
		
		boolean condition = (Boolean) execute(stmt.getCondition(),frame);
		if(condition) {
			return execute(stmt.getTrueBranch(),frame);
		} else {
			return execute(stmt.getFalseBranch(),frame);
		}
	}
	
	private Object execute(Stmt.Return stmt, HashMap<String,Object> frame) {
		
		Expr re = stmt.getExpr();
		
		if(re != null) {
			return execute(re,frame);
		} else {
			return Collections.EMPTY_SET; // used to indicate a function has returned
		}
	}
	
	private Object execute(Stmt.VariableDeclaration stmt,
			HashMap<String, Object> frame) {
		Expr re = stmt.getExpr();
		Object value;
		if (re != null) {
			value = execute(re, frame);
		} else {
			value = Collections.EMPTY_SET; // used to indicate a variable has
											// been declared
		}
		// We need to perform a deep clone here to ensure the value
		// semantics used in While are preserved.
		frame.put(stmt.getName(), deepClone(value));
		return null;
	}
	
	private Object execute(Stmt.Print stmt, HashMap<String,Object> frame) {
	
		String str = toString(execute(stmt.getExpr(),frame));
		System.out.println(str);
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
	private Object execute(Expr expr, HashMap<String,Object> frame) {
		
		if(expr instanceof Expr.Binary) {
			return execute((Expr.Binary) expr,frame);
		} else if(expr instanceof Expr.Isof){// xiu gai guo xin jia de
			
			return execute((Expr.Isof) expr, frame);
		} else if(expr instanceof Expr.Cast) {
			return execute((Expr.Cast) expr,frame);
		} else if(expr instanceof Expr.Constant) {
			return execute((Expr.Constant) expr,frame);
		} else if(expr instanceof Expr.Invoke) {
			return execute((Expr.Invoke) expr,frame);
		} else if(expr instanceof Expr.IndexOf) {
			return execute((Expr.IndexOf) expr,frame);
		} else if(expr instanceof Expr.ListConstructor) {
			return execute((Expr.ListConstructor) expr,frame);
		} else if(expr instanceof Expr.RecordAccess) {
			return execute((Expr.RecordAccess) expr,frame);
		} else if(expr instanceof Expr.RecordConstructor) {
			return execute((Expr.RecordConstructor) expr,frame);
		} else if(expr instanceof Expr.Unary) {
			return execute((Expr.Unary) expr,frame);
		} else if(expr instanceof Expr.Variable) {
			
			return execute((Expr.Variable) expr,frame);
		} else {
			internalFailure("unknown expression encountered (" + expr + ")", file.filename,expr);
			return null;
		} 
	}
	
	private Object execute(Expr.Binary expr, HashMap<String,Object> frame) {

		// First, deal with the short-circuiting operators first
		Object lhs = execute(expr.getLhs(), frame);
		
		switch (expr.getOp()) {
		case AND:
			return ((Boolean)lhs) && ((Boolean)execute(expr.getRhs(), frame));
		case OR:
			return ((Boolean)lhs) || ((Boolean)execute(expr.getRhs(), frame));
		}
		
		// Second, deal the rest.	

		Object rhs = execute(expr.getRhs(), frame);

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

				ArrayList l  = (ArrayList)deepClone((ArrayList) lhs);// xiu gai guo yin wei ListAppend_Valid_3.while
				ArrayList r  = (ArrayList)deepClone((ArrayList) rhs);
				l.addAll(r);
				return l;
			}
		}
	

		internalFailure("unknown binary expression encountered (" + expr + ")",
				file.filename, expr);
		return null;
	}
	
	
	private Object execute(Expr.Isof expr, HashMap<String,Object> frame) {
		
		// First, deal with the short-circuiting operators first
		Object lhs = execute(expr.getLhs(), frame);
		
		if(expr instanceof Expr.Isof){
			Type t = ((Expr.Isof)expr).getRhs();

			if(lhs instanceof Integer){
				if(t instanceof Type.Int){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Int){
						return true;
					}else{
						return false;
					}
						
				} else if(t instanceof Type.Union){// xu yao chong fu
					Type.Union tu = (Type.Union) t;
					List<Type> at = tu.getBounds();
					for(int i = 0; i < at.size(); i++){
						
						if(at.get(i) instanceof Type.Int){
							return true;
						}						
					}
					
				} else{
					return false;
				}
				
				
			} else if(lhs instanceof ArrayList){
				
				if(t instanceof Type.List){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.List){
						return true;
					}else{
						return false;
					}
				} else{
					return false;
				}
			} else if(lhs instanceof HashMap){
				
				if(t instanceof Type.Record){

					Iterator iter = ((HashMap)lhs).entrySet().iterator();
					
					HashMap tt = new HashMap(((Type.Record)t).getFields());
					Iterator iter_t = ((HashMap)tt).entrySet().iterator();
					while(iter.hasNext()){
						Map.Entry entry = (Map.Entry) iter.next(); 
						 Object key = entry.getKey(); 
						 if(!iter_t.hasNext()){
							 return false;
						 }
						 Map.Entry entry_t = (Map.Entry) iter_t.next(); 
						 Object key_t = entry.getKey(); 
						 if(!key.equals(key_t)){
							 return false;
						 }

						 
					}
					if(iter_t.hasNext()){
						return false;
					}
//					if(expr.getLhs()){/////mei ge guan jian zi xiang tong bing qi shu liang xiagn tong cai pan duan liang ge record xiang tong
//						
//					}
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Record){
						return true;
					}else{
						return false;
					}
				} else{
					return false;
				}
			} else if(lhs instanceof Double){
				if(t instanceof Type.Real){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Real){
						return true;
					}else{
						return false;
					}
				} else{
					return false;
				}
			}else if(lhs == null){
				
				if(t instanceof Type.Null){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Null){
						return true;
					}else{
						return false;
					}
				} else{
					return false;
				}
			}else if(lhs instanceof Boolean){
				if(t instanceof Type.Bool){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Bool){
						return true;
					}else{
						return false;
					}
				} else{
					return false;
				}
			}else if(lhs instanceof String){
				if(t instanceof Type.Strung){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Strung){
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else if(lhs instanceof Character){
				if(t instanceof Type.Char){
					return true;
				}else if(t instanceof Type.Named){
					Object o = declarations.get(t.toString());
					WhileFile.TypeDecl td = (WhileFile.TypeDecl)o;
					if(td.getType() instanceof Type.Char){
						return true;
					}else{
						return false;
					}
				} else{
					return false;
				}
			}
			
			
			
			
			


			
		}
		
		internalFailure("unknown instanceof expression encountered (" + expr + ")",
				file.filename, expr);
		return null;
	}
	
	
	
	
	private Object execute(Expr.Cast expr, HashMap<String, Object> frame) {
		Object rhs = execute(expr.getSource(), frame);		
		// TODO: we need to actually implement casting here!
		
		Object t = expr.getType();
		if(rhs instanceof Integer){
			int nm = (Integer) rhs;
			if(t instanceof Type.Real){
				Double d = new Double(nm);
				return d;
			}
		} else if(rhs instanceof ArrayList){
		
			if(((ArrayList) rhs).get(0) != null){
				
				if(((ArrayList) rhs).get(0) instanceof Integer){
					
					if(((Type.List)t).getElement() instanceof Type.Real){
						
						int su = ((ArrayList) rhs).size();
						
						Double[] d = new Double[su];
						ArrayList al = new ArrayList(su);
						for(int i = 0; i < su; i++){
							d[i] = new Double((Integer)((ArrayList) rhs).get(i));
							al.add(d[i]);
							
						}
						
						return al;
					}
				
				}
			
			}
			
			
		}else if(rhs instanceof HashMap){// xiu gai guo xin jia de

			if(expr.getType() instanceof Type.Named){// zan shi bu zhi chi zi ding yi lei xing de qiang zhi zhuan huan
				return rhs;
			} 

			Iterator iter = ((HashMap)rhs).entrySet().iterator();
			HashMap tt = new HashMap(((Type.Record)t).getFields());
			Iterator iter_t = ((HashMap)tt).entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next(); 
				 Object key = entry.getKey(); 
				 Object val = entry.getValue();
				 
				 

				 if(val instanceof Integer){
				    	
				    	int n = (Integer) val;

						Map.Entry entry_t = (Map.Entry) iter_t.next(); 
						
					    Object key_t = entry_t.getKey(); 
					    Object val_t = entry_t.getValue();
					    
					   
				    	if(val_t instanceof Type.Real){

				    		Double d = new Double((Integer)val);
			    		    
			    		    entry.setValue(d);
				    		while (iter.hasNext()) { 
				    			entry = (Map.Entry) iter.next(); 
				    			Object value = entry.getValue();
				    		    d = new Double((Integer)value);
				    		    
				    		    entry.setValue(d);
				    		} 
				    		
				    				
				    	}else if (val_t instanceof Type.Named){
				    		
				    		return rhs;
				    	}
				    }
			}
			return rhs;
		    



		}
		
		
		
		
		
		return rhs;
	}
	
	private Object execute(Expr.Constant expr, HashMap<String,Object> frame) {
		return expr.getValue();
	}
	
	private Object execute(Expr.Invoke expr, HashMap<String, Object> frame) {
		List<Expr> arguments = expr.getArguments();
		Object[] values = new Object[arguments.size()];
		for (int i = 0; i != values.length; ++i) {
			// We need to perform a deep clone here to ensure the value
			// semantics used in While are preserved.
			values[i] = deepClone(execute(arguments.get(i), frame));
		}
		WhileFile.FunDecl fun = (WhileFile.FunDecl) declarations.get(expr
				.getName());
		return execute(fun, values);
	}
	
	private Object execute(Expr.IndexOf expr, HashMap<String,Object> frame) {
		Object _src = execute(expr.getSource(),frame);
		int idx = (Integer) execute(expr.getIndex(),frame);
		if(_src instanceof String) {
			String src = (String) _src;
			return src.charAt(idx);
		} else {
			ArrayList<Object> src = (ArrayList<Object>) _src;
			return src.get(idx);
		}
	}
	
	private Object execute(Expr.ListConstructor expr,
			HashMap<String, Object> frame) {
		List<Expr> es = expr.getArguments();
		ArrayList<Object> ls = new ArrayList<Object>();
		for (int i = 0; i != es.size(); ++i) {
			ls.add(execute(es.get(i), frame));
		}
		return ls;
	}
	
	private Object execute(Expr.RecordAccess expr, HashMap<String, Object> frame) {
		HashMap<String, Object> src = (HashMap) execute(expr.getSource(), frame);
		return src.get(expr.getName());
	}
	
	private Object execute(Expr.RecordConstructor expr, HashMap<String,Object> frame) {
		List<Pair<String,Expr>> es = expr.getFields();
		HashMap<String,Object> rs = new HashMap<String,Object>();
		
		for(Pair<String,Expr> e : es) {
			rs.put(e.first(),execute(e.second(),frame));
		}
		
		return rs;
	}
	
	private Object execute(Expr.Unary expr, HashMap<String, Object> frame) {
		
		Object value = execute(expr.getExpr(), frame);
		
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
	
	private Object execute(Expr.Variable expr, HashMap<String,Object> frame) {
		
		if(cons.get(expr.getName()) != null){  //xiu gai guo
			Object o = cons.get(expr.getName());
			if(o instanceof Expr.Binary){
				
				return execute(cons.get(expr.getName()),frame);
				
			}else if(o instanceof Expr.Constant){
				
				return ((Expr.Constant) o).getValue();
				
			}else if(o instanceof Expr.ListConstructor){

				
				return execute((Expr.ListConstructor)o,frame);
				
			}
			
			if(o instanceof Expr.RecordConstructor){
				
				return execute((Expr.RecordConstructor)o,frame);
				
			}
			return cons.get(expr.getName());
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
