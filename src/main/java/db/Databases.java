package db;

import api.configs.Config;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class Databases {

    private Databases() {}

    public static DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(Config.getProperty("db.username"));
        ds.setPassword(Config.getProperty("db.password"));
        ds.setUrl(Config.getProperty("db.url"));
        return ds;
    }

}
