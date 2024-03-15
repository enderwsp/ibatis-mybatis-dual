package org.example.aspect.dual.common;

public enum TxStatus {
    DEFAULT(0),
    OPEN(1),
    COMMIT(2),
    ROLLBACK(-1);

    TxStatus(int i) {

    }

    public static TxStatus type(int i) {
        switch (i) {
            case 1:
                return TxStatus.OPEN;
            case 2:
                return TxStatus.COMMIT;
            case -1:
                return TxStatus.ROLLBACK;
            case 0:
                return TxStatus.DEFAULT;
        }
        return TxStatus.DEFAULT;
    }
}
