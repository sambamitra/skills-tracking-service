package uk.gov.nhs.sts.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.nhs.sts.model.data.Person;
import uk.gov.nhs.sts.model.data.PersonSkill;
import uk.gov.nhs.sts.model.data.Skill;
import uk.gov.nhs.sts.model.data.SkillLevel;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryTest {

  @Autowired
  private PersonRepository personRepository;

  @Autowired
  private SkillRepository skillRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void shouldSavePerson() {
    // given
    final Person person = Person.builder().name("Samba").build();

    // when
    final Person persisted = this.personRepository.saveAndFlush(person);

    // then
    final Person actual = this.entityManager.find(Person.class, persisted.getId());
    assertEquals(actual, persisted);
  }

  @Test
  public void shouldFetchPerson() {
    // given
    final Person person = Person.builder().name("Samba").build();

    // when
    final Person persisted = this.entityManager.persistAndFlush(person);

    // then
    final Optional<Person> actual = this.personRepository.findById(persisted.getId());
    assertEquals(actual.get(), persisted);
  }

  @Test
  public void shouldFetchPersonByStaffNumber() {
    // given
    final Person person = Person.builder().name("Samba").staffNumber("1").build();

    // when
    final Person persisted = this.entityManager.persistAndFlush(person);

    // then
    final Person actual = this.personRepository.findByStaffNumber("1");
    assertEquals(actual, persisted);
  }

  @Test
  public void shouldSavePersonWithSkills() {
    // given
    final Skill skill = Skill.builder().name("Java").build();
    this.skillRepository.saveAndFlush(skill);

    final Person person = Person.builder().name("Samba").staffNumber("1").build();
    this.personRepository.saveAndFlush(person);
    final PersonSkill personSkill =
        PersonSkill.builder().person(person).skill(skill).level(SkillLevel.EXPERT).build();
    person.setPersonSkills(new HashSet<>(Arrays.asList(personSkill)));

    // when
    final Person persisted = this.personRepository.saveAndFlush(person);

    // then
    final Person actual = this.entityManager.find(Person.class, persisted.getId());
    final Skill actualSkill = this.skillRepository.findByName(skill.getName());
    assertNotNull("Skill should be created", actualSkill);
    assertEquals(person.getName(), actual.getName());
    assertEquals(SkillLevel.EXPERT, actual.getPersonSkills().iterator().next().getLevel());
    assertEquals("Java", actual.getPersonSkills().iterator().next().getSkill().getName());
  }

}
