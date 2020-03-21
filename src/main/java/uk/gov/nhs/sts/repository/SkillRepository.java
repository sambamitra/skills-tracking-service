package uk.gov.nhs.sts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.nhs.sts.model.data.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

  Skill findByName(final String name);

}
