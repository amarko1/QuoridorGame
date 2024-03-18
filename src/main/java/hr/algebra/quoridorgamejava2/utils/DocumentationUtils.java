package hr.algebra.quoridorgamejava2.utils;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentationUtils {

    public static void generateHtmlDocumentationFile() {
        try {
            List<String> listOfClassFilePaths = Files.walk(Paths.get("target"))
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".class"))
                    .filter(f -> !f.endsWith("module-info.class"))
                    .toList();

            StringBuilder htmlBuilder = new StringBuilder();
            String htmlHeader = """
            <!DOCTYPE html>
            <html>
            <head>
            <title>Project Documentation</title>
            </head>
            <body>
            """;
            htmlBuilder.append(htmlHeader);

            for(String classFilePath : listOfClassFilePaths) {
                String[] pathTokens = classFilePath.split("classes");
                String secondToken = pathTokens[1];
                String fqnWithSlashes = secondToken.substring(1, secondToken.lastIndexOf('.'));
                String fqn = fqnWithSlashes.replace('\\', '.');
                Class<?> deserializedClass = Class.forName(fqn);

                htmlBuilder.append("<h2>").append(deserializedClass.getName()).append("</h2>");

                // Documenting fields
                htmlBuilder.append("<h3>Fields</h3>");
                Field[] fields = deserializedClass.getDeclaredFields();
                for(Field field : fields) {
                    htmlBuilder.append(getModifierString(field.getModifiers()))
                            .append(field.getType().getTypeName()).append(" ")
                            .append(field.getName()).append("<br>");
                }

                // Documenting methods
                htmlBuilder.append("<h3>Methods</h3>");
                Method[] methods = deserializedClass.getDeclaredMethods();
                for(Method method : methods) {
                    htmlBuilder.append(getModifierString(method.getModifiers()))
                            .append(method.getReturnType().getTypeName()).append(" ")
                            .append(method.getName()).append("()")
                            .append("<br>");
                }

                // Documenting constructors
                htmlBuilder.append("<h3>Constructors</h3>");
                Constructor<?>[] constructors = deserializedClass.getDeclaredConstructors();
                for(Constructor<?> constructor : constructors) {
                    htmlBuilder.append(getModifierString(constructor.getModifiers()))
                            .append(constructor.getDeclaringClass().getSimpleName())
                            .append("(");

                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    for(int i = 0; i < parameterTypes.length; i++) {
                        htmlBuilder.append(parameterTypes[i].getCanonicalName());
                        if (i < parameterTypes.length - 1) {
                            htmlBuilder.append(", ");
                        }
                    }

                    htmlBuilder.append(")")
                            .append("<br>");
                }
            }

            String htmlFooter = """
            </body>
            </html>
            """;
            htmlBuilder.append(htmlFooter);

            Path directoryPath = Paths.get("files");
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            Path htmlDocumentationFile = directoryPath.resolve("documentation.html");
            Files.write(htmlDocumentationFile, htmlBuilder.toString().getBytes());

            DialogUtils.showInformationDialog("File created!", "File", "Creation of HTML documentation file succeeded!");

        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showInformationDialog("File not created!", "File error", "Creation of HTML documentation file failed!");
            e.printStackTrace();
        }
    }

    private static String getModifierString(int modifiers) {
        StringBuilder modifierBuilder = new StringBuilder();
        if(Modifier.isPublic(modifiers)) modifierBuilder.append("public ");
        if(Modifier.isPrivate(modifiers)) modifierBuilder.append("private ");
        if(Modifier.isProtected(modifiers)) modifierBuilder.append("protected ");
        if(Modifier.isStatic(modifiers)) modifierBuilder.append("static ");
        if(Modifier.isFinal(modifiers)) modifierBuilder.append("final ");
        return modifierBuilder.toString();
    }



}
