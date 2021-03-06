/*
 * Copyright 2015 The original authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.sundr.codegen.utils;

import io.sundr.Function;
import io.sundr.codegen.Constants;
import io.sundr.codegen.DefinitionRepository;
import io.sundr.codegen.functions.Collections;
import io.sundr.codegen.model.ClassRef;
import io.sundr.codegen.model.PrimitiveRef;
import io.sundr.codegen.model.Property;
import io.sundr.codegen.model.TypeDef;
import io.sundr.codegen.model.TypeDefBuilder;
import io.sundr.codegen.model.TypeParamDef;
import io.sundr.codegen.model.TypeParamDefBuilder;
import io.sundr.codegen.model.TypeParamRef;
import io.sundr.codegen.model.TypeParamRefBuilder;
import io.sundr.codegen.model.TypeRef;

import javax.lang.model.element.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.sundr.codegen.model.TypeDef.OBJECT;


public final class TypeUtils {

    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    private static final String JAVA_UTIL_OPTIONAL = "java.util.Optional";
    private static final String JAVA_UTIL_OPTIONAL_INT = "java.util.OptionalInt";
    private static final String JAVA_UTIL_OPTIONAL_DOUBLE = "java.util.OptionalDouble";
    private static final String JAVA_UTIL_OPTIONAL_LONG = "java.util.OptionalLong";
    private static final String OTHER = "other";
    private static final String DOT_REGEX = "\\.";
    
    private TypeUtils() {
        //Utility Class
    }

    /**
     * Checks if a {@link TypeDef} is an instance of an other {@link TypeDef}.
     * @param type          The type to compare.
     * @param targetType    The target type.
     * @param function
     * @return  true if match, false otherwise.
     */
    public static boolean isInstanceOf(TypeRef type, TypeDef targetType, Function<TypeRef, Boolean> function) {
        if (type instanceof ClassRef) {
            ClassRef classRef = (ClassRef) type;
            TypeDef definition = classRef.getDefinition();
            if (definition.getFullyQualifiedName().equals(targetType.getFullyQualifiedName())) {
                return true;
            }

            for (TypeRef i : definition.getImplementsList()) {
                if (function.apply(i)) {
                    return true;
                }
            }

            for (TypeRef e : definition.getExtendsList()) {
                if (function.apply(e)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static TypeParamDef getParameterDefinition(TypeRef typeRef, Collection<TypeParamDef> parameters) {
        String name;
        if (typeRef instanceof ClassRef) {
            name = ((ClassRef)typeRef).getName();
        } else if (typeRef instanceof TypeParamRef) {
            name = ((TypeParamRef)typeRef).getName();
        } else if (typeRef instanceof PrimitiveRef) {
            name = ((PrimitiveRef)typeRef).getName();
        } else {
            name = typeRef.toString();
        }

        for (TypeParamDef parameter : parameters) {
            if (parameter.getName().equals(name)) {
                return parameter;
            }
        }
        return null;
    }

    /**
     * Creates a new generic TypeParamRef.
     * @param letter       The letter of the type.
     * @return             The param reference.
     */
    public static TypeParamRef newTypeParamRef(String letter) {
        return new TypeParamRefBuilder().withName(letter).build();
    }

    /**
     * Creates a new TypeParamDef.
     * @param letter       The letter of the type.
     * @return             The param definition.
     */
    public static TypeParamDef newTypeParamDef(String letter) {
        return new TypeParamDefBuilder().withName(letter).build();
    }

    /**
     * Removes parameters from a TypeDef.
     * @param base       The base type.
     * @return           The unwraped TypeDef.
     */
    public static TypeDef unwrapGeneric(TypeDef base) {
        return new TypeDefBuilder(base).withParameters().build();
    }

    /**
     * Sets one {@link io.sundr.codegen.model.TypeDef} as a generic of an other.
     *
     * @param base          The base type.
     * @param parameters    The parameter types.
     * @return              The generic type.
     */
    public static TypeDef typeGenericOf(TypeDef base, TypeParamDef... parameters) {
        return new TypeDefBuilder(base)
                .withParameters(parameters)
                .build();
    }

    /**
     * Sets one {@link io.sundr.codegen.model.TypeDef} as a super class of an other.
     *
     * @param base       The base type.
     * @param superClass The super type.
     * @return           The updated type definition.
     */
    public static TypeDef typeExtends(TypeDef base, ClassRef superClass) {
        return new TypeDefBuilder(base)
                .withExtendsList(superClass)
                .build();
    }

    /**
     * Sets one {@link io.sundr.codegen.model.TypeDef} as an interface of an other.
     *
     * @param base       The base type.
     * @param superClass The super type.
     * @return           The updated type definition.
     */
    public static TypeDef typeImplements(TypeDef base, ClassRef... superClass) {
        return new TypeDefBuilder(base)
                .withImplementsList(superClass)
                .build();
    }


    public static int modifiersToInt(Modifier... modifiers) {
        return modifiersToInt(Arrays.asList(modifiers));
    }

    public static int modifiersToInt(Collection<Modifier> modifiers) {
        int result = 0;

        for (Modifier m : modifiers) {
            switch (m) {
                case ABSTRACT:
                    result = result | java.lang.reflect.Modifier.ABSTRACT;
                    break;
                case FINAL:
                    result = result | java.lang.reflect.Modifier.FINAL;
                    break;
                case NATIVE:
                    result = result | java.lang.reflect.Modifier.NATIVE;
                    break;
                case PRIVATE:
                    result = result | java.lang.reflect.Modifier.PRIVATE;
                    break;
                case PROTECTED:
                    result = result | java.lang.reflect.Modifier.PROTECTED;
                    break;
                case PUBLIC:
                    result = result | java.lang.reflect.Modifier.PUBLIC;
                    break;
                case STATIC:
                    result = result | java.lang.reflect.Modifier.STATIC;
                    break;
                case SYNCHRONIZED:
                    result = result | java.lang.reflect.Modifier.SYNCHRONIZED;
                    break;
                case TRANSIENT:
                    result = result | java.lang.reflect.Modifier.TRANSIENT;
                    break;
            }
        }

        return result;
    }


    public static String fullyQualifiedNameDiff(String left, String right) {
        String[] lparts = left.split(DOT_REGEX);
        String[] rparts = right.split(DOT_REGEX);

        for (int l = lparts.length - 1, r = rparts.length - 1; l >= 0 && r >= 0; r--, l--) {
            if (!lparts[l].equals(rparts[r])) {
                return rparts[r];
            }
        }
        return OTHER;
    }

    /**
     * Checks a {@link TypeRef} is of an abstract type.
     * @param   typeRef The type to check.
     * @return  True if its an abstract type.
     */
    public static boolean isAbstract(TypeRef  typeRef) {
        DefinitionRepository repository =  DefinitionRepository.getRepository();
        TypeDef def = repository.getDefinition(typeRef);
        if (def == null && typeRef instanceof ClassRef) {
            def = ((ClassRef)typeRef).getDefinition();
        }
        return def != null ? def.isAbstract() : false;
    }

    /**
     * Checks if {@link TypeRef} is of an concrete
     * @param   typeRef The type to check.
     * @return  True if its an concrete type.
     */
    public static boolean isConcrete(TypeRef  typeRef) {
        DefinitionRepository repository =  DefinitionRepository.getRepository();
        TypeDef def = repository.getDefinition(typeRef);
        if (def == null && typeRef instanceof ClassRef) {
            def = ((ClassRef)typeRef).getDefinition();
        }
        return def != null ? !def.isAbstract() && !def.isInterface() : false;
    }
    /**
     * Checks if a {@link TypeRef} is a primitive type.
     * @param type  The type to check.
     * @return      True if its a primitive type.
     */
    public static boolean isPrimitive(TypeRef type) {
        return type instanceof PrimitiveRef;
    }

    /**
     * Checks if a {@link TypeRef} is a {@link Map}.
     * @param type  The type to check.
     * @return      True if its a Map.
     */
    public static boolean isMap(TypeRef type) {
        return Collections.IS_MAP.apply(type);
    }

    /**
     * Checks if a {@link TypeRef} is a {@link java.util.List}.
     * @param type  The type to check.
     * @return      True if its a List.
     */
    public static boolean isList(TypeRef type) {
        return Collections.IS_LIST.apply(type);
    }

    /**
     * Checks if a {@link TypeRef} is a {@link java.util.Set}.
     * @param type  The type to check.
     * @return      True if its a Set.
     */
    public static boolean isSet(TypeRef type) {
        return Collections.IS_SET.apply(type);
    }

    /**
     * Checks if a {@link TypeRef} is a {@link Collection}.
     * @param type  The type to check.
     * @return      True if its a Collection.
     */
    public static boolean isCollection(TypeRef type) {
        return Collections.IS_COLLECTION.apply(type);
    }

    /**
     * Checks if a {@link TypeRef} is a {@link Boolean} or boolean.
     * @param type  The type to check.
     * @return      True if its a {@link Boolean} or boolean.
     */
    public static boolean isBoolean(TypeRef type) {
        if (type instanceof PrimitiveRef) {
            return Constants.PRIMITIVE_BOOLEAN_REF.equals(type);
        } else if (!(type instanceof ClassRef)) {
            return false;
        } else {
            return Constants.BOOLEAN_REF.equals(type);
        }
    }

    /**
     * Checks if a {@link TypeRef} is an array.
     * @param type  The type to check.
     * @return      True if its an array.B
     */
    public static boolean isArray(TypeRef type) {

        if (type instanceof ClassRef) {
            return ((ClassRef)type).getDimensions() > 0;
        } else if (type instanceof PrimitiveRef) {
            return ((PrimitiveRef)type).getDimensions() > 0;
        } else if (type instanceof TypeParamRef) {
            return ((TypeParamRef)type).getDimensions() > 0;
        } else {
            return false;
        }
    }

    /**
     * Checks if a {@link TypeRef} is a {@link java.util.Optional}.
     * @param type  The type to check.
     * @return      True if its a {@link java.util.Optional}.
     */
    public static boolean isOptional(TypeRef type) {
        if (!(type instanceof ClassRef)) {
            return false;
        }

        return JAVA_UTIL_OPTIONAL.equals(((ClassRef)type).getDefinition().getFullyQualifiedName());
    }

    /**
     * Checks if a {@link TypeRef} is a {@link java.util.OptionalInt}.
     * @param type  The type to check.
     * @return      True if its a {@link java.util.OptionalInt}.
     */
    public static boolean isOptionalInt(TypeRef type) {
        if (!(type instanceof ClassRef)) {
            return false;
        }

        return JAVA_UTIL_OPTIONAL_INT.equals(((ClassRef)type).getDefinition().getFullyQualifiedName());
    }

    /**
     * Checks if a {@link TypeRef} is a {@link java.util.OptionalDouble}.
     * @param type  The type to check.
     * @return      True if its a {@link java.util.OptionalDouble}.
     */
    public static boolean isOptionalDouble(TypeRef type) {
        if (!(type instanceof ClassRef)) {
            return false;
        }

        return JAVA_UTIL_OPTIONAL_DOUBLE.equals(((ClassRef)type).getDefinition().getFullyQualifiedName());
    }

    /**
     * Checks if a {@link TypeRef} is a {@link java.util.OptionalLong}.
     * @param type  The type to check.
     * @return      True if its a {@link java.util.OptionalLong}.
     */
    public static boolean isOptionalLong(TypeRef type) {
        if (!(type instanceof ClassRef)) {
            return false;
        }

        return JAVA_UTIL_OPTIONAL_LONG.equals(((ClassRef)type).getDefinition().getFullyQualifiedName());
    }

    /**
     * Check if method exists on the specified type.
     * @param typeDef   The type.
     * @param method    The method name.
     * @return          True if method is found, false otherwise.
     */
    public static boolean hasMethod(TypeDef typeDef, String method) {
       return unrollHierarchy(typeDef)
       .stream()
       .flatMap(h -> h.getMethods().stream())
       .filter(m -> method.equals(m.getName()))
       .findAny()
       .isPresent();
    }

    /**
     * Checks if property exists on the specified type.
     * @param typeDef   The type.
     * @param property  The property name.
     * @return          True if method is found, false otherwise.
     */
    public static boolean hasProperty(TypeDef typeDef, String property) {
       return unrollHierarchy(typeDef)
       .stream()
       .flatMap(h -> h.getProperties().stream())
       .filter(p -> property.equals(p.getName()))
       .findAny()
       .isPresent();
    }

    /**
     * All properties (including inherited).
     * @param typeDef   The type.
     * @return          A list with all properties.
     */
    public static List<Property> allProperties(TypeDef typeDef) {
        return unrollHierarchy(typeDef)
                .stream()
                .flatMap(h -> h.getProperties().stream())
                .collect(Collectors.toList());
    }

    /**
     * Unrolls the hierararchy of a specified type.
     * @param typeDef       The specified type.
     * @return              A set that contains all the hierarching (including the specified type).
     */
    public static Set<TypeDef> unrollHierarchy(TypeDef typeDef) {
        if (OBJECT.equals(typeDef)) {
            return new HashSet<>();
        }
        Set<TypeDef> hierarchy = new HashSet<>();
        hierarchy.add(typeDef);
        hierarchy.addAll(typeDef.getExtendsList().stream().flatMap(s -> unrollHierarchy(s.getDefinition()).stream()).collect(Collectors.toSet()));
        return hierarchy;
    }

    /***
      *  A utility that tries to get a fully qualified class name from an unknown object.
      * @param o    The object.
     * @return      The classname if found or the string representation of the object.
     */
    public static String toClassName(Object o) {
        if (o instanceof String)  {
            return (String) o;
        } else if (o instanceof ClassRef) {
           return  ((ClassRef) o).getFullyQualifiedName();
        } else if (o instanceof TypeDef) {
           return ((TypeDef) o).getFullyQualifiedName();
        } else {
            return String.valueOf(o);
        }
    }

    public static void visitParents(TypeDef type, List<TypeDef> types) {
        visitParents(type, types, new ArrayList<>());
    }

    public static void visitParents(TypeDef type, List<TypeDef> types, List<TypeDef> visited) {
        if (type == null || JAVA_LANG_OBJECT.equals(type.getFullyQualifiedName())) {
            return;
        }

        if (visited.contains(type)) {
            return;
        }

        visited.add(type);
        List<TypeRef> allRefs = new ArrayList<>();
        allRefs.addAll(type.getImplementsList());
        allRefs.addAll(type.getExtendsList());
        for (TypeRef ref : allRefs) {
            TypeDef parent = DefinitionRepository.getRepository().getDefinition(ref);
            visitParents(parent, types, visited);
        }
        types.add(type);
    }
}
