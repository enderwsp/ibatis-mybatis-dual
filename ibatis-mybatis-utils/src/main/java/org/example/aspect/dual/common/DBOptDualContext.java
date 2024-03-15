package org.example.aspect.dual.common;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Queue;

public class DBOptDualContext implements DbOptDualContextInterface, StatementComplexInterface {

    private Connection slave;
    private TxStatus nextStt = TxStatus.DEFAULT;
    private TxStatus preStt = TxStatus.DEFAULT;
    private Queue<RelatedStatement> sts = new LinkedList<>();

    @Override
    public Connection getSlaveCnt() {
        return slave;
    }

    @Override
    public void slaveBind(Connection con) {
        slave = con;
    }

    @Override
    public void openTx() {
        nextStt = TxStatus.DEFAULT;
    }

    @Override
    public void syncTxCommit() {
        nextStt = TxStatus.COMMIT;
    }

    @Override
    public void syncTxRollbacl() {
        nextStt = TxStatus.ROLLBACK;
    }

    @Override
    public RelatedStatement getStatementForExe() {
        return hasStatement() ? sts.poll() : null;
    }

    @Override
    public boolean hasStatement() {
        return sts != null && !sts.isEmpty();
    }

    @Override
    public void addStatement(RelatedStatement s) {
        sts.add(s);
    }
}
