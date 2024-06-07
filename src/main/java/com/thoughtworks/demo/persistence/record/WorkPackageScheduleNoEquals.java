package com.thoughtworks.demo.persistence.record;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkPackageScheduleNoEquals  {
    private String scheduleDate;
    private Integer repeatTimes;
    private Integer repeatInterval;
    private Address location;
}
