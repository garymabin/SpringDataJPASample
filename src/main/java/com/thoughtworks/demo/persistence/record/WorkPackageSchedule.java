package com.thoughtworks.demo.persistence.record;

import lombok.Builder;
import lombok.Getter;

@Builder
public record WorkPackageSchedule(String scheduleDate, Integer repeatTimes, Integer repeatInterval)  {
}
