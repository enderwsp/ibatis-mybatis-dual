package org.example.aspect.dual.common;

import java.sql.Statement;

public interface DualDBExtendInterface {
    Object paramPrepare(String setedMainDbId
            , String setedSlaveDbId
            , String initMainDbId
            , String initSlaveDbId
            , String currentDbId
            , String currentSqlId
            , ParamAttachStatement[] originPss
            , Statement currentStt);
}
