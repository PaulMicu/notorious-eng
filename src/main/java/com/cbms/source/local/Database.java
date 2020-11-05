package com.cbms.source.local;

import java.io.*;
import java.sql.*;

public class Database {
    private static final String URL = "jdbc:h2:mem:cbms_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private Connection conn = null;

    public Database() {
        this.conn = getConnection();
        createTables();
        insertData();
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);
            System.out.println("Connection Created\n");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void createTables() {
        try {
            Statement stmt = this.conn.createStatement();
            //Make sure the naming convention is up to par variable name lowercase, sql statement uppercase

            //Train is TRUE, Test is FALSE

            stmt.execute("CREATE TABLE dataset(" +
                    "dataset_id INT AUTO_INCREMENT," +
                    "test_or_train BOOL," +
                    "name CHAR(5)," +
                    "PRIMARY KEY (dataset_id))");
            System.out.println("Dataset table created");


            stmt.execute("CREATE TABLE operational_condition(" +
                    "oc_name VARCHAR(50)," +
                    "oc_description VARCHAR(300)," +
                    "PRIMARY KEY (oc_name))");
            System.out.println("Operational Conditions Table created");


            stmt.execute("CREATE TABLE measured_in(" +
                    "dataset_id INT," +
                    "oc_name VARCHAR(50)," +
                    "PRIMARY KEY (dataset_id, oc_name)," +
                    "FOREIGN KEY (dataset_id) REFERENCES dataset (dataset_id)," +
                    "FOREIGN KEY (oc_name) REFERENCES operational_condition (oc_name))");
            System.out.println("Measured In table created");


            stmt.execute("CREATE TABLE systems(" +
                    "dataset_id INT," +
                    "unit_nb INT," +
                    "name VARCHAR(50)," +
                    "type VARCHAR(20)," +
                    "description VARCHAR(300)," +
                    "sn VARCHAR(20)," +
                    "manufacturer VARCHAR(20)," +
                    "category VARCHAR(20)," +
                    "site VARCHAR(20),location VARCHAR(20)," +
                    "PRIMARY KEY (dataset_id, unit_nb)," +
                    "FOREIGN KEY (dataset_id) REFERENCES dataset(dataset_id))");
            System.out.println("Systems table created");


            stmt.execute("CREATE TABLE sensor(" +
                    "sensor_nb INT," +
                    "sensor_id INT," +
                    "type VARCHAR(20)," +
                    "location VARCHAR(20)," +
                    "PRIMARY KEY (sensor_nb))");
            System.out.println("Sensors table created");


            stmt.execute("CREATE TABLE measure(" +
                    "dataset_id INT," +
                    "unit_nb INT," +
                    "sensor_nb INT," +
                    "time INT," +
                    "sensor_value DOUBLE," +
                    "PRIMARY KEY (dataset_id, unit_nb, sensor_nb, time)," +
                    "FOREIGN KEY (dataset_id, unit_nb ) REFERENCES systems" +
                    "(dataset_id,unit_nb )," +
                    "FOREIGN KEY (sensor_nb) REFERENCES sensor(sensor_nb))");
            System.out.println("Measure table created");


            stmt.execute("CREATE TABLE model(" +
                    "name VARCHAR(20)," +
                    "description VARCHAR(300)," +
                    "PRIMARY KEY (name))");
            System.out.println("Models table created");


            stmt.execute("CREATE TABLE calculates_rul(" +
                    "dataset_id INT," +
                    "unit_nb INT," +
                    "model_name VARCHAR(20)," +
                    "timestamp DATETIME," +
                    "rul DOUBLE," +
                    "PRIMARY KEY (dataset_id, unit_nb, model_name, timestamp)," +
                    "FOREIGN KEY (dataset_id,unit_nb) REFERENCES systems(dataset_id,unit_nb)," +
                    "FOREIGN KEY (model_name) REFERENCES model(name))");
            System.out.println("Calculates Ruls table created\n");



            //EXAMPLE CODE
//            stmt.execute("CREATE TABLE test(id INT PRIMARY KEY, name VARCHAR(20))");
//            System.out.println("Table created");
//            stmt.executeUpdate("INSERT INTO test VALUES(1, 'one')");
//            ResultSet rs = stmt.executeQuery("SELECT * FROM Test");
//            while (rs.next())
//                System.out.println(rs.getString("NAME") +" " + rs.getString("id"));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertData() {
        try {
            Statement stmt = this.conn.createStatement();

            //INSERT TO TABLE DATASET
            int train_test = 0;

            int textIndex = 1;

            int count = 0;

            for (int i = 1; i < 9; i++) {
                if (train_test == 1) {
                    train_test = 0;
                } else if (train_test == 0) {
                    train_test = 1;
                }

                if (count % 2 == 0 && count != 0) {
                    textIndex++;
                }
                //System.out.println(i+" "+train_test+" "+"FD00"+textIndex);
                String name = "FD00" + textIndex;
                stmt.executeUpdate("INSERT INTO dataset(test_or_train,name) VALUES (" + train_test + ",'" + name + "' )");
                count++;
            }

            System.out.println("Data inserted into dataset");
            ResultSet dataRS = stmt.executeQuery("SELECT * FROM dataset");
            while (dataRS.next())
                System.out.println(dataRS.getString("dataset_id") + " " + dataRS.getString("test_or_train") + " " + dataRS.getString("name"));

            dataRS.close();
            System.out.println();

            //INSERT TO THE TABLE OPERATIONAL_CONDITION
            stmt.executeUpdate("INSERT INTO operational_condition(oc_name,oc_description) " +
                    "VALUES " +
                    "('Conditions: ONE','(Sea Level)')," +
                    "('Conditions: SIX',' ')," +
                    "('Fault Modes: ONE','(HPC Degradation)')," +
                    "('Fault Modes: TWO','(HPC Degradation, Fan Degradation)')");

            System.out.println("Data inserted into operational_condition");
            ResultSet opcRS = stmt.executeQuery("SELECT * FROM operational_condition");
            while (opcRS.next())
                System.out.println(opcRS.getString("oc_name") + " " + opcRS.getString("oc_description"));

            opcRS.close();
            System.out.println();

            //INSERTING DATA INTO MEASURED_IN TABLE
            stmt.executeUpdate("INSERT INTO measured_in(dataset_id, oc_name) " +
                    "VALUES" +
                    "(1, 'Conditions: ONE')," +
                    "(1, 'Fault Modes: ONE')," +
                    "(2, 'Conditions: ONE')," +
                    "(2, 'Fault Modes: ONE')," +
                    "(3, 'Conditions: SIX')," +
                    "(3, 'Fault Modes: ONE')," +
                    "(4, 'Conditions: SIX')," +
                    "(4, 'Fault Modes: ONE')," +
                    "(5, 'Conditions: ONE')," +
                    "(5, 'Fault Modes: TWO')," +
                    "(6, 'Conditions: ONE')," +
                    "(6, 'Fault Modes: TWO')," +
                    "(7, 'Conditions: SIX')," +
                    "(7, 'Fault Modes: TWO')," +
                    "(8, 'Conditions: SIX')," +
                    "(8, 'Fault Modes: TWO')");

            System.out.println("Data inserted into measured_in");
            ResultSet measureInRS = stmt.executeQuery("SELECT * FROM measured_in");
            while (measureInRS.next())
                System.out.println(measureInRS.getString("dataset_id") + " " + measureInRS.getString("oc_name"));

            measureInRS.close();
            System.out.println();

            //INSERTING DATA INTO SYSTEMS TABLE
            for (int i = 1; i <= 100; i++) {
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (1, " + i + ")");
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (2, " + i + ")");
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (5, " + i + ")");
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (6, " + i + ")");
            }
            for (int i = 1; i <= 260; i++) {
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (3, " + i + ")");
            }
            for (int i = 1; i <= 259; i++) {
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (4, " + i + ")");
            }
            for (int i = 1; i <= 248; i++) {
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (8, " + i + ")");
            }
            for (int i = 1; i <= 249; i++) {
                stmt.executeUpdate("INSERT INTO systems(dataset_id, unit_nb)" +
                        "VALUES (7, " + i + ")");
            }

            System.out.println("Data inserted into systems");
            ResultSet sysRS = stmt.executeQuery("SELECT * FROM systems ORDER BY dataset_id ASC");
            while (sysRS.next())
                System.out.println(sysRS.getString("dataset_id") + " " + sysRS.getString("unit_nb"));

            sysRS.close();
            System.out.println();

            //INSERTING DATA INTO SENSOR TABLE
            for (int i = 1; i <= 21; i++) {
                stmt.executeUpdate("INSERT INTO sensor(sensor_nb)" +
                        "VALUES (" + i + ")");
            }

            System.out.println("Data inserted into sensor");
            ResultSet sensorRS = stmt.executeQuery("SELECT * FROM sensor");
            while (sensorRS.next())
                System.out.println(sensorRS.getString("sensor_nb"));

            sensorRS.close();
            System.out.println();

            //INSERTING DATA INTO MODEL TABLE
            stmt.executeUpdate("INSERT INTO model(name, description) " +
                    "VALUES " +
                    "('Linear', 'lorem ipsum')," +
                    "('LSTM', 'lorem ipsum')");

            System.out.println("Data inserted into model");
            ResultSet modelRS = stmt.executeQuery("SELECT * FROM model");
            while (modelRS.next())
                System.out.println(modelRS.getString("name") + " " + modelRS.getString("description"));

            modelRS.close();
            System.out.println();

            //INSERTING DATA INTO MEASURE TABLE
            for (int j = 1; j <= 8; j++) {
                //Iterating through the different dataset text files to read from them given on the dataset_ID identified in dataset table
                String f = new File("").getAbsolutePath();
                if (j % 2 == 0) {
                    //For train files
                    f = f.concat("/Dataset/Test/test_FD00" + j / 2 + ".txt");
                } else {
                    //For Test files
                    f = f.concat("/Dataset/Train/train_FD00" + (j + 1) / 2 + ".txt");
                }
                BufferedReader br = null;
                br = new BufferedReader(new FileReader(f));

                while (br.ready()) {
                    String check = null;
                    try {
                        check = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String[] tokenizedTerms = check.split(" ");
                    //Read each file line in split the different values into separate objects
                    for (int i = 1; i <= 21; i++) {
                        //Insert an entry into the table for each sensor given on file line
                        stmt.executeUpdate("INSERT INTO measure(dataset_id, unit_nb, sensor_nb, time, sensor_value) VALUES (" + j + ", " + tokenizedTerms[0] + ", " + i + "," + tokenizedTerms[1] + ", " + tokenizedTerms[i + 4] + " )");
                    }
                }
            }

            System.out.println("Data inserted into measure");

            ResultSet measureRS = stmt.executeQuery("SELECT * FROM measure WHERE dataset_id=7");
            while (measureRS.next())
                System.out.println(measureRS.getString("dataset_id") + " " + measureRS.getString("unit_nb") + " " + measureRS.getString("sensor_nb") + " " + measureRS.getString("time") + " " + measureRS.getString("sensor_value"));

            measureRS.close();
            System.out.println();

            stmt.close();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteData() {

    }

}
