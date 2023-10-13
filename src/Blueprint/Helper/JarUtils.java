package Blueprint.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import Blueprint.Utils;

public class JarUtils {
	
	public static HashMap<String, ClassData> classes = new HashMap<>();
	
	public static void loadJarFile(String path) throws Exception {
		for(ClassData cd : JarUtils.processJarFile(path)) {
			classes.put(cd.getClassName(), cd);
		}
	}
	
	public static List<ClassData> processJarFile(String path) throws Exception{
		List<ClassData> classDataList = new ArrayList<>();
		
		try (JarFile jarFile = new JarFile(path)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while(entries.hasMoreElements()) {
			    JarEntry entry = entries.nextElement();
			    
			    if(entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
			        try(InputStream inputStream = jarFile.getInputStream(entry)) {
			            ClassData classData = JarUtils.processClass(inputStream);
			            classDataList.add(classData);
			        }
			    }
			}
		}
        
//        for(ClassData classData : classDataList) {
//            System.out.println("Class Name: " + classData.getClassName());
//            for(FieldData field : classData.getFields().values()) {
//                System.out.println("Field: " + field.getAccessModifiers() + " " + field.getFieldType() + " " + field.getFieldName());
//            }
//            for(MethodData method : classData.getMethods().values()) {
//                System.out.println("Method: " + method.getAccessModifiers() + " " + method.getReturnType() + " " + method.getMethodName());
//                for(ParameterData parameter : method.getParameters()) {
//                    System.out.println("- Parameter: " + parameter.getParameterType() + " " + parameter.getParameterName());
//                }
//            }
//            for(ConstructorData constructor : classData.getConstructors().values()) {
//                System.out.println("Constructor: " + constructor.getAccessModifier() + " " + constructor.getConstructorName());
//                for(ParameterData parameter : constructor.getParameters()) {
//                    System.out.println("- Parameter: " + parameter.getParameterType() + " " + parameter.getParameterName());
//                }
//            }
//            System.out.println();
//        }
        
        return classDataList;
	}

	public static ClassData processClass(InputStream inputStream) throws IOException {
        ClassReader reader = new ClassReader(inputStream);
        ClassData classData = new ClassData();

        reader.accept(new ClassVisitor(Opcodes.ASM7) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                classData.setClassName(name.replace("/", "."));
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            	String fieldModifier = getAccessModifier(access);
                String fieldType = descriptorToType(descriptor);
                FieldData fieldData = new FieldData(name, fieldModifier, fieldType, isStatic(access));
                classData.addField(fieldData);
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                String methodModifier = getAccessModifier(access);
                String returnType = descriptorToType(descriptor.split("\\)")[1]);
                
                List<ParameterData> parameters = new ArrayList<>();
                String parameterDescriptor = descriptor.substring(1, descriptor.indexOf(')'));
                String[] paramDescriptors = parameterDescriptor.split(";");
                for(String paramDesc : paramDescriptors) {
                    if(!paramDesc.isEmpty()) {
                    	String paramType = descriptorToType(paramDesc + ";");
                        ParameterData parameterData = new ParameterData(paramType, "value");
                        parameters.add(parameterData);
                    }
                }
                
                if(name.equals("<init>")) {
                	name = "<init>-";
                	for(int i = 1;i <= 32;i++) {
                		if(!classData.getConstructors().containsKey(name + i)) {
                			name = "<init>-" + i;
                			break;
                		}
                	}
                	ConstructorData constructorData = new ConstructorData(name, methodModifier, parameters);
                    classData.addConstructor(constructorData);
                }else {
                	MethodData methodData = new MethodData(name, methodModifier, returnType, parameters, isStatic(access));
                	methodData.setMethodName(methodData.toString());
                    classData.addMethod(methodData);
                }
                
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }, 0);

        return classData;
    }

	public static String getAccessModifier(int access) {
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            return "public";
        } else if ((access & Opcodes.ACC_PRIVATE) != 0) {
            return "private";
        } else if ((access & Opcodes.ACC_PROTECTED) != 0) {
            return "protected";
        } else {
            return "package-private";
        }
    }
    
	public static boolean isStatic(int access) {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    private static String descriptorToType(String descriptor) {
        if (descriptor.startsWith("[")) {
            return "[" + descriptorToType(descriptor.substring(1));
        }
        if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
            return descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
        }
        if (descriptor.equals("B")) {
            return "byte";
        }
        if (descriptor.equals("C")) {
            return "char";
        }
        if (descriptor.equals("D")) {
            return "double";
        }
        if (descriptor.equals("F")) {
            return "float";
        }
        if (descriptor.equals("I")) {
            return "int";
        }
        if (descriptor.equals("J")) {
            return "long";
        }
        if (descriptor.equals("S")) {
            return "short";
        }
        if (descriptor.equals("Z")) {
            return "boolean";
        }
        if (descriptor.equals("V")) {
            return "void";
        }
        return descriptor;
    }
	
    public static class FieldData {
    	
        private String fieldName;
        private String accessModifier;
        private String fieldType;
        
        private boolean isStatic;

        public FieldData(String fieldName, String accessModifier, String fieldType, boolean isStatic) {
            this.fieldName = fieldName;
            this.accessModifier = accessModifier;
            this.fieldType = fieldType;
            this.isStatic = isStatic;
        }

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getAccessModifier() {
			return accessModifier;
		}
		
		public String getAccessModifiers() {
			return accessModifier + (isStatic ? " static" : "");
		}

		public void setAccessModifier(String accessModifier) {
			this.accessModifier = accessModifier;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public boolean isStatic() {
			return isStatic;
		}

		public void setStatic(boolean isStatic) {
			this.isStatic = isStatic;
		}

    }
    
    public static class ClassData {
    	
        private String className;
        private HashMap<String, MethodData> methods = new HashMap<>();
        private HashMap<String, ConstructorData> constructors = new HashMap<>();
        private HashMap<String, FieldData> fields = new HashMap<>();

        public ClassData(String className) {
            this.className = className;
        }
        
        public ClassData() {
        	
        }

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}
		
		public HashMap<String, MethodData> getMethods() {
			return methods;
		}

		public MethodData getMethod(String methodName) {
			return methods.get(methodName);
		}

		public void addMethod(MethodData method) {
			this.methods.put(method.getMethodName(), method);
		}
		
		public HashMap<String, ConstructorData> getConstructors() {
			return constructors;
		}

		public void addConstructor(ConstructorData constructorData) {
			constructors.put(constructorData.getConstructorName(), constructorData);
		}

		public FieldData getField(String fieldName) {
			return fields.get(fieldName);
		}
		
		public HashMap<String, FieldData> getFields() {
			return fields;
		}
		
		public void addField(FieldData field) {
			this.fields.put(field.getFieldName(), field);
		}

    }

    public static class ParameterData {
    	
        private String parameterType;
        private String parameterName;

        public ParameterData(String parameterType, String parameterName) {
            this.parameterType = parameterType;
            this.parameterName = parameterName;
        }

		public String getParameterType() {
			return parameterType;
		}

		public void setParameterType(String parameterType) {
			this.parameterType = parameterType;
		}

		public String getParameterName() {
			return parameterName;
		}

		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}
		
    }

    public static class MethodData {
    	
        private String methodName;
        private String accessModifier;
        private String returnType;
        private List<ParameterData> parameters;
        
        private boolean isStatic;

        public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public String getAccessModifier() {
			return accessModifier;
		}
		
		public String getAccessModifiers() {
			return accessModifier + (isStatic ? " static" : "");
		}

		public void setAccessModifier(String accessModifier) {
			this.accessModifier = accessModifier;
		}

		public String getReturnType() {
			return returnType;
		}

		public void setReturnType(String returnType) {
			this.returnType = returnType;
		}

		public List<ParameterData> getParameters() {
			return parameters;
		}

		public void setParameters(List<ParameterData> parameters) {
			this.parameters = parameters;
		}

		public MethodData(String methodName, String accessModifier, String returnType, List<ParameterData> parameters, boolean isStatic) {
            this.methodName = methodName;
            this.accessModifier = accessModifier;
            this.returnType = returnType;
            this.parameters = parameters;
            this.isStatic = isStatic;
        }

		public boolean isStatic() {
			return isStatic;
		}

		public void setStatic(boolean isStatic) {
			this.isStatic = isStatic;
		}
		
		@Override
		public String toString() {
			String s = "";
			for(ParameterData pd : parameters) {
				s += "," + Utils.parseClassName(pd.parameterType);
			}
			return this.getMethodName() + "(" + (s.length() > 0 ? s.substring(1) : "") + ")";
		}
		
    }

    public static class ConstructorData {
    	
    	private String constructorName;
        private String accessModifier;
        private List<ParameterData> parameters;

		public String getAccessModifier() {
			return accessModifier;
		}

		public void setAccessModifier(String accessModifier) {
			this.accessModifier = accessModifier;
		}

		public List<ParameterData> getParameters() {
			return parameters;
		}

		public void setParameters(List<ParameterData> parameters) {
			this.parameters = parameters;
		}

		public String getConstructorName() {
			return constructorName;
		}

		public void setConstructorName(String constructorName) {
			this.constructorName = constructorName;
		}

		public ConstructorData(String constructorName, String accessModifier, List<ParameterData> parameters) {
			this.constructorName = constructorName;
            this.accessModifier = accessModifier;
            this.parameters = parameters;
        }
		
		@Override
		public String toString() {
			String s = "";
			for(ParameterData pd : parameters) {
				s += "," + Utils.parseClassName(pd.parameterType);
			}
			return "(" + (s.length() > 0 ? s.substring(1) : "") + ")";
		}
		
    }
	
}
