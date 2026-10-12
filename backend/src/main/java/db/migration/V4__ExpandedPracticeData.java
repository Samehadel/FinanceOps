package db.migration;

import com.financeops.billing.PracticeDataset;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.core.io.ClassPathResource;
import java.util.zip.CRC32;

/** Add richer fictional records to existing workspaces without resetting any data. */
public class V4__ExpandedPracticeData extends BaseJavaMigration {
    @Override
    public Integer getChecksum() {
        try (var input = new ClassPathResource("db/practice/expanded-records.sql").getInputStream()) {
            CRC32 checksum = new CRC32();
            checksum.update(input.readAllBytes());
            return (int) checksum.getValue();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Practice fixtures could not be loaded", exception);
        }
    }

    @Override
    public void migrate(Context context) throws Exception {
        PracticeDataset.seed(context.getConnection(), null);
    }
}
