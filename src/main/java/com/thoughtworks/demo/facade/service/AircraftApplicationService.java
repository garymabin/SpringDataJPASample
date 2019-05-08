package com.thoughtworks.demo.facade.service;


import com.thoughtworks.demo.facade.transformers.AircraftViewTransformer;
import com.thoughtworks.demo.facade.view.AircarftView;
import com.thoughtworks.demo.persistence.dao.AircraftDAO;
import com.thoughtworks.demo.persistence.dao.TaskDAO;
import com.thoughtworks.demo.persistence.record.AircraftRecord;
import com.thoughtworks.demo.persistence.record.TaskRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AircraftApplicationService {

    private final AircraftDAO aircraftDAO;

    private final TaskDAO taskDAO;

    @Autowired
    public AircraftApplicationService(AircraftDAO aircraftDAO, TaskDAO taskDAO) {
        this.aircraftDAO = aircraftDAO;
        this.taskDAO = taskDAO;
    }

    public AircarftView createNewWorkPackageForAircraft(String tail, String workPackageName, String... taskNames) throws NoSuchElementException {
        Optional<AircraftRecord> aircraftAggregate = aircraftDAO.findByTailNumberWithWorkPackages(tail);
        aircraftAggregate.orElseThrow().addNewWorkPackage(workPackageName, taskDAO.findAllByNameIn(Arrays.asList(taskNames)).toArray(new TaskRecord[]{}));

        return AircraftViewTransformer.transformAggregateToAircraftView(aircraftAggregate.get());
    }
}
