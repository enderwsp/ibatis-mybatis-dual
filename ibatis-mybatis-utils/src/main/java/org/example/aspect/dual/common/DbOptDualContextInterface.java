package org.example.aspect.dual.common;

import java.sql.Connection;
import java.sql.Statement;

public interface DbOptDualContextInterface {
    Connection getSlaveCnt();

    void slaveBind(Connection con);

    void openTx();

    void syncTxCommit();

    void syncTxRollbacl();

}
