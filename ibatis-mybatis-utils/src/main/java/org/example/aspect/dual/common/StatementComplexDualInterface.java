package org.example.aspect.dual.common;

public interface StatementComplexDualInterface {

    void addStatement(boolean sameInit,ParamAttachStatement s);

    ParamAttachStatement getStatementForExe(boolean sameInit);

    boolean hasStatement(boolean sameInit);
}
