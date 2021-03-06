package jobs.tests;

import jobs.db.JobDatabase;

import java.io.IOException;
import java.nio.file.*;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class TestMain {
    public static void cleanDatabaseFiles() {
        FileSystem fs = FileSystems.getDefault();

        try {
            Consumer<Path> deleteFile = path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    System.err.println("Unable to delete file " + path);
                }
            };

            // Clean up .csv and .db files
            Files.walk(Paths.get("."), 1)
                    .filter(fs.getPathMatcher("glob:**.{csv,db}")::matches)
                    .forEach(deleteFile);
        } catch (IOException e) {
            System.err.println("Unable to clean up data files");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        ServiceLoader<JobDatabase> loader = ServiceLoader.load(JobDatabase.class);

        cleanDatabaseFiles();

        for (JobDatabase db : loader) {
            System.out.println("Testing " + db.getClass().getSimpleName());
            new DatabaseTest(db).runAllTests();
        }
        System.out.println("Finished tests");
    }
}
