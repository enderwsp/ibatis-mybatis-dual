package org.example.aspect.dual.common;

import java.util.LinkedList;
import java.util.Queue;

public class DualDBRelatedOpts implements StatementComplexDualInterface {

    private boolean sameWithInit = true;
    private Queue<ParamAttachStatement> mainSts = new LinkedList<>();
    private Queue<ParamAttachStatement> slaveSts = new LinkedList<>();

    public void switchSlave() {
        sameWithInit = !sameWithInit;
    }

    public void setSameWithInit(boolean sameWithInit) {
        this.sameWithInit = sameWithInit;
    }

    @Override
    public void addStatement(boolean sameInit, ParamAttachStatement s) {
        boolean b = sameInit ? slaveSts.add(s) : mainSts.add(s);
    }

    @Override
    public ParamAttachStatement getStatementForExe(boolean sameInit) {
        return sameInit ? slaveSts.poll() : mainSts.poll();
    }

    @Override
    public boolean hasStatement(boolean sameInit) {
        return sameInit ? !slaveSts.isEmpty() : !mainSts.isEmpty();
    }
}
