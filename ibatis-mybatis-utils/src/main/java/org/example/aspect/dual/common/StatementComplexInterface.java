package org.example.aspect.dual.common;

import java.sql.Connection;
import java.sql.Statement;

public interface StatementComplexInterface {

    void addStatement(RelatedStatement s);

    RelatedStatement getStatementForExe();

    boolean hasStatement();
}
