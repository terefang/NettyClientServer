package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
public class ModuleSpec  extends AbstractSpec
{
    String packageName = "MODULE";
    List<AnnotSpec> annotList = new Vector();

    List<EnumSpec> enumList = new Vector();
    List<StructSpec> structList = new Vector();

    List<InterfaceSpec> ifaceList = new Vector();

    List<ModuleSpec> moduleList = new Vector();

    public ModuleSpec() {    }

}
