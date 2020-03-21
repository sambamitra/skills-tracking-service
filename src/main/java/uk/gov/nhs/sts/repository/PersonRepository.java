package uk.gov.nhs.sts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.nhs.sts.model.data.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

  Person findByStaffNumber(final String staffNumber);

}
