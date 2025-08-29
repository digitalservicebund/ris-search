package de.bund.digitalservice.ris.search.service;

import lombok.Getter;

public interface Job {
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
