package uk.gov.nhs.sts.repository;

import static org.junit.Assert.assertEquals;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.nhs.sts.model.data.Skill;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SkillRepositoryTest {

  @Autowired
  private SkillRepository skillRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void shouldSaveSkill() {
    // given
    final Skill skill = Skill.builder().name("Coding").build();

    // when
    final Skill persisted = this.skillRepository.saveAndFlush(skill);

    // then
    final Skill actual = this.entityManager.find(Skill.class, persisted.getId());
    assertEquals(actual, persisted);
  }

  @Test
  public void shouldFetchSkill() {
    // given
    final Skill skill = Skill.builder().name("Physics").build();

    // when
    final Skill persisted = this.entityManager.persistAndFlush(skill);

    // then
    final Optional<Skill> actual = this.skillRepository.findById(persisted.getId());
    assertEquals(actual.get(), persisted);
  }

  @Test
  public void shouldFetchSkillByName() {
    // given
    final Skill skill = Skill.builder().name("Running").build();

    // when
    final Skill persisted = this.entityManager.persistAndFlush(skill);

    // then
    final Skill actual = this.skillRepository.findByName("Running");
    assertEquals(actual, persisted);
  }

}
