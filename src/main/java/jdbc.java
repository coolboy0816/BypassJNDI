import java.util.ArrayList;
import java.util.List;

public class jdbc {

    private static List<String> jdbcClass = new ArrayList();

    public List<String> getJdbcClass(){
        return jdbcClass;
    }

    static {
        jdbcClass.add("org.apache.tomcat.jdbc.pool.DataSourceFactory");
        jdbcClass.add("org.apache.commons.dbcp.BasicDataSourceFactory");
        jdbcClass.add("org.apache.commons.dbcp2.BasicDataSourceFactory");
        jdbcClass.add("org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory");
        jdbcClass.add("org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory");
    }
}
