package io.pivotal.pde.demo.tracker.gemfire;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


public interface CheckInRepository extends CrudRepository<CheckIn, Long> {

	List<CheckIn> findByPlateStartsWithIgnoreCase(String plate);
}
