package com.github.terefang.ncs.idl.objects;

import lombok.Data;

import java.util.List;
import java.util.Vector;

@Data
public class InterfaceSpec extends AbstractSpec
{
    String ifacePackage = "iface";
    String ifaceName = "Interface";
    List<MethodSpec> methodList = new Vector();
    List<AnnotSpec> annotList = new Vector();

    public InterfaceSpec() {   }

}
