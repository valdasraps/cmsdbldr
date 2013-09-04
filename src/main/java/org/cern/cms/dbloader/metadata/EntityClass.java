package org.cern.cms.dbloader.metadata;

import java.io.PrintStream;
import java.io.PrintWriter;

import javassist.CtClass;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ClassFilePrinter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityClass<T> {
		
	private final CtClass cc;
	private final ClassFile cf;
	private final Class<? extends T> c;

	public final void outputClassFile(PrintStream out) {
		try {
			
			PrintWriter pw = new PrintWriter(out);
			ClassFilePrinter.print(cf, pw);
			pw.flush();
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
}
