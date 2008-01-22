// Version:   $Id: $
// Copyright: Copyright(c) 2007 Trace Financial Limited
package org.liquibase.maven.plugins;

import java.sql.Connection;
import java.text.*;
import liquibase.exception.LiquibaseException;
import liquibase.migrator.Migrator;
import org.apache.maven.plugin.MojoFailureException;

/**
 * A Maven Mojo for invoking Liquibase rollbacks on a database.
 * @author Peter Murray
 * @goal rollback
 */
public class LiquibaseRollback extends ConfigurableLiquibaseMojo {

  private enum RollbackType {

    TAG, DATE, COUNT
  }

  /** @parameter expression="${liquibase.rollbackTag}" */
  private String rollbackTag;

  /** @parameter expression="${liquibase.rollbackCount}" default-value="-1" */
  private int rollbackCount;

  /** @parameter expression="${liquibase.rollbackDate}" */
  private String rollbackDate;

  /** The type of the rollback that is being performed. */
  private RollbackType type;

  @Override
  protected void checkRequiredParametersAreSpecified() throws MojoFailureException {
    super.checkRequiredParametersAreSpecified();

    if (rollbackCount == -1 && rollbackDate == null && rollbackTag == null) {
      throw new MojoFailureException("One of the rollback options must be specified, "
                                     + "please specify one of rollbackTag, rollbackCount "
                                     + "or rollbackDate");
    }

    if (rollbackCount == 0) {
      throw new MojoFailureException("A rollback count of 0 is meaningless, please "
                                     + "select a value greater than 0");
    }

    String message = "Cannot specify multiple rollbackXXX options, please select only"
                     + " one of rollbackTag, rollbackCount, rollbackDate.";

    if (rollbackCount > 0) {
      if (rollbackDate != null || rollbackTag != null) {
        throw new MojoFailureException(message);
      }
      type = RollbackType.COUNT;
    } else if (rollbackDate != null) {
      if (rollbackTag != null || rollbackCount > 0) {
        throw new MojoFailureException(message);
      }
      type = RollbackType.DATE;
    } else if (rollbackTag != null) {
      if (rollbackCount > 0 || rollbackDate != null) {
        throw new MojoFailureException(message);
      }
      type = RollbackType.TAG;
    }
  }

  @Override
  protected void printSettings(String indent) {
    super.printSettings(indent);
    getLog().info(indent + "rollback Count: " + rollbackCount);
    getLog().info(indent + "rollback Date: " + rollbackDate);
    getLog().info(indent + "rollback Tag: " + rollbackTag);
  }

  @Override
  protected void configureMigrator(Migrator migrator, Connection connection)
          throws LiquibaseException {
    super.configureMigrator(migrator, connection);
//    migrator.setMode(Migrator.Mode.EXECUTE_ROLLBACK_MODE);
  }

  protected void performLiquibaseTask(Migrator migrator) throws LiquibaseException {
    switch (type) {
      case COUNT: {
        migrator.rollback(rollbackCount, contexts);
        break;
      }
      case DATE: {
        DateFormat format = DateFormat.getDateInstance();
//        try {
//          migrator.setRollbackToDate(format.parse(rollbackDate));
//        }
//        catch (ParseException e) {
//          String message = "Error parsing rollbackDate: " + e.getMessage();
//          if (format instanceof SimpleDateFormat) {
//            message += "\nDate must match pattern: " + ((SimpleDateFormat)format).toPattern();
//          }
//          throw new LiquibaseException(message, e);
//        }
        break;
      }
      case TAG: {
//        migrator.setRollbackToTag(rollbackTag);
        break;
      }
      default: {
        throw new IllegalStateException("Unexpected rollback type, " + type);
      }
    }

  }
}
