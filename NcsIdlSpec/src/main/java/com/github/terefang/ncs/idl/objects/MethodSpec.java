package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
public class MethodSpec  extends AbstractSpec
{

    String methodType = "MTYPE";
    boolean methodArrayType = false;

    String methodName = "METHOD";

    List<AnnotSpec> annotList = new Vector();

    List<ParamSpec> paramList = new Vector();

    public MethodSpec() {    }

}
