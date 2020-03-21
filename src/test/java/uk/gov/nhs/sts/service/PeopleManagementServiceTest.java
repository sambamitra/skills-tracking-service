package uk.gov.nhs.sts.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.nhs.sts.model.data.Person;
import uk.gov.nhs.sts.model.data.PersonSkill;
import uk.gov.nhs.sts.model.data.Skill;
import uk.gov.nhs.sts.model.data.SkillLevel;
import uk.gov.nhs.sts.model.dto.PersonDTO;
import uk.gov.nhs.sts.model.dto.PersonSkillDTO;
import uk.gov.nhs.sts.model.dto.SkillDTO;
import uk.gov.nhs.sts.repository.PersonRepository;
import uk.gov.nhs.sts.repository.SkillRepository;

@RunWith(MockitoJUnitRunner.class)
public class PeopleManagementServiceTest {

  @Mock
  private PersonRepository personRepository;

  @Mock
  private SkillRepository skillRepository;

  @InjectMocks
  private PeopleManagementService service;

  @Test
  public void fetchPersonByStaffNumberShouldReturnNullIfNoPersonExits() {
    // given
    given(this.personRepository.findByStaffNumber(anyString())).willReturn(null);

    // when
    final PersonDTO person = this.service.fetchPersonByStaffNumber("1");

    // then
    assertNull(person);
  }

  @Test
  public void fetchPersonByStaffNumberShouldReturnValidPersonIfPersonExits() {
    // given
    final PersonSkill personSkill = PersonSkill.builder().level(SkillLevel.PRACTITIONER)
        .skill(Skill.builder().name("Physics").build()).build();
    final Person expected = Person.builder().staffNumber("1").name("Samba")
        .personSkills(new HashSet<>(Arrays.asList(personSkill))).build();
    given(this.personRepository.findByStaffNumber(anyString())).willReturn(expected);

    // when
    final PersonDTO actual = this.service.fetchPersonByStaffNumber("1");

    // then
    assertNotNull(actual);
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getPersonSkills().iterator().next().getSkill().getName(),
        actual.getPersonSkills().get(0).getSkillName());
    assertEquals(expected.getPersonSkills().iterator().next().getLevel(),
        actual.getPersonSkills().get(0).getSkillLevel());
  }

  @Test
  public void fetchSkillBySkillNameShouldReturnNullIfNoSkillExits() {
    // given
    given(this.skillRepository.findByName(anyString())).willReturn(null);

    // when
    final SkillDTO skill = this.service.fetchSkillByName("Physics");

    // then
    assertNull(skill);
  }

  @Test
  public void fetchSkillBySkillNameShouldReturnValidSkillIfSkillExits() {
    // given
    final Skill expected = Skill.builder().name("Physics").build();
    given(this.skillRepository.findByName(anyString())).willReturn(expected);

    // when
    final SkillDTO actual = this.service.fetchSkillByName("Physics");

    // then
    assertNotNull(actual);
    assertEquals(expected.getName(), actual.getName());
  }

  @Test
  public void shouldFetchPeople() {
    // given
    final PersonSkill personSkill = PersonSkill.builder().level(SkillLevel.PRACTITIONER)
        .skill(Skill.builder().name("Physics").build()).build();
    final Person person = Person.builder().staffNumber("1").name("Samba")
        .personSkills(new HashSet<>(Arrays.asList(personSkill))).build();
    given(this.personRepository.findAll()).willReturn(Arrays.asList(person));

    // when
    final List<PersonDTO> actual = this.service.fetchPeople();

    // then
    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertEquals(person.getStaffNumber(), actual.get(0).getStaffNumber());
  }

  @Test
  public void shouldFetchSkills() {
    // given
    final Skill skill = Skill.builder().name("Wizardry").build();
    final List<Skill> skills = Arrays.asList(skill);
    given(this.skillRepository.findAll()).willReturn(skills);

    // when
    final List<SkillDTO> actual = this.service.fetchSkills();

    // then
    assertNotNull(actual);
    assertEquals(skills.size(), actual.size());
    assertEquals(skills.get(0).getName(), actual.get(0).getName());
  }

  @Test
  public void shouldCreatePersonWithSkillWhenSkillAlreadyExists() {
    // given
    final PersonSkillDTO personSkillDto =
        PersonSkillDTO.builder().skillName("Coding").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO personDto = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(personSkillDto)).build();

    final Skill skill = Skill.builder().name("Coding").build();
    given(this.skillRepository.findByName("Coding")).willReturn(skill);
    final ArgumentCaptor<Person> personArgument = ArgumentCaptor.forClass(Person.class);

    final PersonSkill personSkill =
        PersonSkill.builder().level(SkillLevel.EXPERT).skill(skill).build();
    final Person expected = Person.builder().name("Samba").staffNumber("1")
        .personSkills(new HashSet<>(Arrays.asList(personSkill))).build();

    // when
    this.service.createPerson(personDto);

    // then
    verify(this.personRepository, times(2)).saveAndFlush(personArgument.capture());
    final Person actual = personArgument.getAllValues().get(1);
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getStaffNumber(), actual.getStaffNumber());
    assertEquals(expected.getPersonSkills().iterator().next().getLevel(),
        actual.getPersonSkills().iterator().next().getLevel());
  }

  @Test
  public void shouldCreatePersonWithSkillWhenSkillDoesNotAlreadyExist() {
    // given
    final PersonSkillDTO personSkillDto =
        PersonSkillDTO.builder().skillName("Coding").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO personDto = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(personSkillDto)).build();

    given(this.skillRepository.findByName("Coding")).willReturn(null);
    final ArgumentCaptor<Person> personArgument = ArgumentCaptor.forClass(Person.class);
    final ArgumentCaptor<Skill> skillArgument = ArgumentCaptor.forClass(Skill.class);

    final Skill skill = Skill.builder().name("Coding").build();
    final PersonSkill personSkill =
        PersonSkill.builder().level(SkillLevel.EXPERT).skill(skill).build();
    final Person expected = Person.builder().name("Samba").staffNumber("1")
        .personSkills(new HashSet<>(Arrays.asList(personSkill))).build();

    // when
    this.service.createPerson(personDto);

    // then
    verify(this.personRepository, times(2)).saveAndFlush(personArgument.capture());
    verify(this.skillRepository, times(1)).saveAndFlush(skillArgument.capture());
    final Person actual = personArgument.getAllValues().get(1);
    final Skill actualSkill = skillArgument.getValue();
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getStaffNumber(), actual.getStaffNumber());
    assertEquals(expected.getPersonSkills().iterator().next().getLevel(),
        actual.getPersonSkills().iterator().next().getLevel());
    assertEquals(skill.getName(), actualSkill.getName());
  }

  @Test
  public void shouldUpdatePersonWithSkills() {
    // given
    final PersonSkillDTO personSkillDto =
        PersonSkillDTO.builder().skillName("Coding").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO personDto = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(personSkillDto)).build();

    final Skill skill = Skill.builder().name("Coding").build();
    given(this.skillRepository.findByName("Coding")).willReturn(skill);
    final ArgumentCaptor<Person> personArgument = ArgumentCaptor.forClass(Person.class);

    final PersonSkill personSkill =
        PersonSkill.builder().level(SkillLevel.PRACTITIONER).skill(skill).build();
    final Person expected = Person.builder().name("Samba").staffNumber("1")
        .personSkills(new HashSet<>(Arrays.asList(personSkill))).build();
    given(this.personRepository.findByStaffNumber("1")).willReturn(expected);

    // when
    this.service.updatePerson(personDto, "1");

    // then
    verify(this.personRepository, times(1)).saveAndFlush(personArgument.capture());
    final Person actual = personArgument.getValue();
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getStaffNumber(), actual.getStaffNumber());
  }

  @Test
  public void shouldCreateSkill() {
    // given
    final SkillDTO skillDto = SkillDTO.builder().name("Photography").build();
    final Skill expected = Skill.builder().name("Photography").build();
    final ArgumentCaptor<Skill> skillArgument = ArgumentCaptor.forClass(Skill.class);

    // when
    this.service.createSkill(skillDto);

    // then
    verify(this.skillRepository, times(1)).save(skillArgument.capture());
    final Skill actual = skillArgument.getValue();
    assertEquals(expected.getName(), actual.getName());
  }

  @Test
  public void shouldUpdateSkill() {
    // given
    final SkillDTO skillDto = SkillDTO.builder().name("Photographer").build();
    final Skill expected = Skill.builder().name("Photography").build();
    given(this.skillRepository.findByName("Photography")).willReturn(expected);

    // when
    this.service.updateSkill(skillDto, "Photography");

    // then
    verify(this.skillRepository, times(1)).save(expected);
  }

  @Test
  public void shouldDeletePerson() {
    // given
    final PersonDTO personDto = PersonDTO.builder().name("Samba").staffNumber("1").build();
    final Person person = Person.builder().name("Samba").staffNumber("1").build();
    given(this.personRepository.findByStaffNumber(personDto.getStaffNumber())).willReturn(person);

    // when
    this.service.deletePerson(personDto);

    // then
    verify(this.personRepository, times(1)).delete(person);
  }

  @Test
  public void shouldDeleteSkill() {
    // given
    final SkillDTO skillDto = SkillDTO.builder().name("Maths").build();
    final Skill skill = Skill.builder().name("Maths").build();
    given(this.skillRepository.findByName(skillDto.getName())).willReturn(skill);

    // when
    this.service.deleteSkill(skillDto);

    // then
    verify(this.skillRepository, times(1)).delete(skill);
  }
}
