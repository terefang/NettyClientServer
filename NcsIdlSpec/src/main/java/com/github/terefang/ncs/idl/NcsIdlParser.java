package com.github.terefang.ncs.idl;

import com.github.terefang.ncs.idl.objects.*;
import com.github.terefang.ncs.idl.spec.Parser;
import com.github.terefang.ncs.idl.spec.ParserConstants;
import com.github.terefang.ncs.idl.spec.ParserTokenManager;
import com.github.terefang.ncs.idl.spec.Token;
import com.github.terefang.ncs.idl.spec.syntaxtree.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class NcsIdlParser extends Parser
{
    public NcsIdlParser(InputStream _stream, String _encoding) {
        super(_stream, Charset.forName(_encoding));
    }

    public NcsIdlParser(Reader _stream) {
        super(_stream);
    }

    public NcsIdlParser(ParserTokenManager _tm) {
        super(_tm);
    }

    public static NcsIdlParser from(File _file) throws FileNotFoundException
    {
        return new NcsIdlParser(new FileReader(_file));
    }

    List<AnnotSpec> annotationStack = new Vector<>();
    Stack<ModuleSpec> moduleStack = new Stack<>();
    ModuleSpec returnModule = null;

    public ModuleSpec parse() throws Exception
    {
        int depth = 0;
        boolean inInterface = false;
        InterfaceSpec interfaceSpec = null;

        do
        {
            Token token = this.getToken(1);

            //System.err.println(token.toString());
            //System.err.print("\tanno=");
            //System.err.println(annotationStack);
            //System.err.print("\tmod=");
            //System.err.println(moduleStack);
            //System.err.print("\tinif=");
            //System.err.println(inInterface);

            if(token.image != null && token.image.startsWith("#"))
            {
                ConstantDefinition constNode = this.ConstantDefinition();

            }
            else if(token.image != null && token.image.startsWith("@"))
            {
                Annotation anno = this.Annotation();
                annotationStack.add(IdlObjUtil.from(anno));
            }
            else if("struct".equalsIgnoreCase(token.image))
            {
                //token = this.getNextToken();
                StructOrUnionDefinition structNode = this.StructOrUnionDefinition();

                StructSpec structSpec = IdlObjUtil.from(annotationStack, structNode);
                annotationStack.clear();

                if(!moduleStack.empty())
                {
                    structSpec.setParentModule(moduleStack.peek());
                    moduleStack.peek().getStructList().add(structSpec);
                }
            }
            else if("enum".equalsIgnoreCase(token.image))
            {
                //token = this.getNextToken();
                EnumDefinition enumNode = this.EnumDefinition();

                EnumSpec enumSpec = IdlObjUtil.from(annotationStack, enumNode);
                annotationStack.clear();

                if(!moduleStack.empty())
                {
                    enumSpec.setParentModule(moduleStack.peek());
                    moduleStack.peek().getEnumList().add(enumSpec);
                }
            }
            else if(!inInterface && "interface".equalsIgnoreCase(token.image))
            {
                token = this.getNextToken();
                Literal name = this.Literal();
                token = this.getNextToken(); // {

                InterfaceSpec ifSpec = new InterfaceSpec();
                ifSpec.setIfaceName(((NodeToken)name.f0.choice).tokenImage);
                ifSpec.getAnnotList().addAll(annotationStack);
                this.annotationStack.clear();
                inInterface=true;
                interfaceSpec = ifSpec;

                if(!moduleStack.empty())
                {
                    ifSpec.setParentModule(moduleStack.peek());
                    ifSpec.setIfacePackage(moduleStack.peek().getPackageName());
                    moduleStack.peek().getIfaceList().add(ifSpec);
                }
            }
            else if(!inInterface && "module".equalsIgnoreCase(token.image))
            {
                token = this.getNextToken();
                Literal name = this.Literal();

                ModuleSpec pModuleSpec = new ModuleSpec();
                pModuleSpec.setPackageName(name.f0.choice.toString());

                pModuleSpec.getAnnotList().addAll(annotationStack);
                annotationStack.clear();

                token = this.getNextToken(); // {

                while(".".equalsIgnoreCase(token.image))
                {
                    token = this.getNextToken();
                    pModuleSpec.setPackageName(pModuleSpec.getPackageName()+"."+token.image);
                    token = this.getNextToken();
                }

                if(!moduleStack.empty())
                {
                    pModuleSpec.setParentModule(moduleStack.peek());
                    pModuleSpec.setPackageName(moduleStack.peek().getPackageName()+"."+pModuleSpec.getPackageName());
                    moduleStack.peek().getModuleList().add(pModuleSpec);
                }
                moduleStack.push(pModuleSpec);
                depth++;
            }
            else if(inInterface && "}".equalsIgnoreCase(token.image))
            {
                token = this.getNextToken();

                inInterface = false;
                interfaceSpec = null;
            }
            else if(inInterface)
            {
                MethodDefinition md = this.MethodDefinition();
                MethodSpec methodSpec = IdlObjUtil.from(annotationStack, md);
                interfaceSpec.getMethodList().add(methodSpec);
                annotationStack.clear();
            }
            else if("}".equalsIgnoreCase(token.image))
            {
                token = this.getNextToken();
                annotationStack.clear();
                if(!moduleStack.empty()) returnModule = moduleStack.pop();
                depth--;
            }
            else
            {
                token = this.getNextToken();
            }
        }
        while(token.kind != ParserConstants.EOF);

        return returnModule;
    }

}
