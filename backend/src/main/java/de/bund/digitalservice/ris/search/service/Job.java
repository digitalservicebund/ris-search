package de.bund.digitalservice.ris.search.service;

import lombok.Getter;

/** Represents a generic job interface for execution tasks. */
public interface Job {

  /**
   * Enum representing return codes for job execution.
   *
   * <p>- ReturnCode: Represents the return code of the job. - SUCCESS: Indicates that the job
   * executed successfully. - ERROR: Represents that the job execution encountered an error.
   */
  @Getter
  enum ReturnCode {
    SUCCESS(0),
    ERROR(1);

    private final int value;

    ReturnCode(final int value) {
      this.value = value;
    }
  }

  ReturnCode runJob();
}
