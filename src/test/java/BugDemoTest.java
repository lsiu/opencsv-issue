import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;

public class BugDemoTest {

    @Test
    public void test() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:example;INIT=RUNSCRIPT FROM 'classpath:sql/create-database.sql'");
        //noinspection SqlResolve,SqlNoDataSourceInspection
        final String sql = "select * from example";
        {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt(1);
                String text = rs.getString(2);
                System.out.format("%d\t%s\n", id, text);
            }
        }

        ResultSet rs = conn.createStatement().executeQuery(sql);
        ResultSetHelperService rsHelper = new ResultSetHelperService();
        File file = new File("test.csv");
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new RuntimeException("Cannot delete file:" + file.getAbsolutePath());
            }
        }

        // if you open 'test.csv', with excel, it looks proper, i.e. second row text ends with one backslash
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
        int rows = 0;
        while (rs.next()) {
            rows++;
            String[] values = rsHelper.getColumnValues(rs);
            csvWriter.writeNext(values);
        }
        csvWriter.flush();

        CSVReader csvReader = new CSVReader(new FileReader(file));
        // to get this to work, we cannot just use the default reader like we did with the default writer, we need to "disable" the escape char
        // CSVReader csvReader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(new CSVParserBuilder().withEscapeChar((char)0).build()).build();
        List<String[]> allLines = csvReader.readAll();

        Assertions.assertEquals(rows, allLines.size());
    }
}
