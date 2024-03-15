package org.example.aspect.dual.common;

import javax.sql.DataSource;

public interface DBDualDataSourceInterface extends DataSource {
    String getMainDsId();
    String getSlaveDsId();
    DataSource getMainDs();
    DataSource getSlaveDs();
}
