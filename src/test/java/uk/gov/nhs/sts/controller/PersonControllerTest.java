package uk.gov.nhs.sts.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.nhs.sts.model.data.SkillLevel;
import uk.gov.nhs.sts.model.dto.PersonDTO;
import uk.gov.nhs.sts.model.dto.PersonSkillDTO;
import uk.gov.nhs.sts.service.PeopleManagementService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PeopleManagementService service;

  private static final String BASE_URI = "/api/people/";

  @Test
  public void getPersonShouldReturnOkIfPersonExists() throws Exception {
    // given
    final PersonSkillDTO skill =
        PersonSkillDTO.builder().skillName("Java").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO person = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(skill)).build();
    given(this.service.fetchPersonByStaffNumber("1")).willReturn(person);

    // when/then
    this.mockMvc.perform(get(BASE_URI + "1")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk()).andExpect(content().json(
            "{\"name\":\"Samba\",\"staffNumber\":\"1\",\"personSkills\":[{\"skillName\":\"Java\",\"skillLevel\":\"EXPERT\"}]}"));
  }

  @Test
  public void getPersonShouldReturnNotFoundIfPersonDoesNotExist() throws Exception {
    // given
    given(this.service.fetchPersonByStaffNumber("1")).willReturn(null);

    // when/then
    this.mockMvc.perform(get(BASE_URI + "1")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void getPeopleShouldReturnOk() throws Exception {
    // given
    final PersonSkillDTO skill =
        PersonSkillDTO.builder().skillName("Java").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO person = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(skill)).build();
    given(this.service.fetchPeople()).willReturn(Arrays.asList(person));

    // when/then
    this.mockMvc.perform(get(BASE_URI)).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk()).andExpect(content().json(
            "[{\"name\":\"Samba\",\"staffNumber\":\"1\",\"personSkills\":[{\"skillName\":\"Java\",\"skillLevel\":\"EXPERT\"}]}]"));

  }

  @Test
  public void createPersonShouldReturnBadRequestIfRequestHasMissingMandatoryFields()
      throws Exception {
    // given
    final PersonDTO person = PersonDTO.builder().name("Samba").build();
    final String json = json(person);

    // when/then
    this.mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());

  }

  @Test
  public void createPersonShouldReturnConflictIfPersonAlreadyExists() throws Exception {
    // given
    final PersonDTO person = PersonDTO.builder().name("Samba").staffNumber("1").build();
    final String json = json(person);
    given(this.service.fetchPersonByStaffNumber("1")).willReturn(person);

    // when/then
    this.mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isConflict());

  }

  @Test
  public void createPersonShouldReturnCreated() throws Exception {
    // given
    final PersonSkillDTO skill =
        PersonSkillDTO.builder().skillName("Java").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO person = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(skill)).build();
    final String json = json(person);

    // when/then
    this.mockMvc.perform(post(BASE_URI).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isCreated())
        .andExpect(content().json("{\"name\":\"Samba\",\"staffNumber\":\"1\"}"));

  }

  @Test
  public void updatePersonShouldReturnBadRequestIfRequestHasMissingMandatoryFields()
      throws Exception {
    // given
    final PersonDTO person = PersonDTO.builder().name("Samba").build();
    final String json = json(person);

    // when/then
    this.mockMvc.perform(put(BASE_URI + "1").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isBadRequest());

  }

  @Test
  public void updatePersonShouldReturnNotFoundIfPersonDoesNotExist() throws Exception {
    // given
    final PersonSkillDTO skill =
        PersonSkillDTO.builder().skillName("Java").skillLevel(SkillLevel.EXPERT).build();
    final PersonDTO person = PersonDTO.builder().name("Samba").staffNumber("1")
        .personSkills(Arrays.asList(skill)).build();
    final String json = json(person);
    given(this.service.fetchPersonByStaffNumber("1")).willReturn(null);

    // when/then
    this.mockMvc.perform(put(BASE_URI + "1").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isNotFound());

  }

  @Test
  public void updatePersonShouldReturnOk() throws Exception {
    // given
    final PersonDTO person = PersonDTO.builder().name("Sam").staffNumber("1").build();
    final String json = json(person);
    given(this.service.fetchPersonByStaffNumber("1")).willReturn(person);

    // when/then
    this.mockMvc.perform(put(BASE_URI + "1").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
        .andExpect(content().json("{\"name\":\"Sam\",\"staffNumber\":\"1\"}"));

  }

  @Test
  public void deletePersonShouldReturnNotFoundIfPersonDoesNotExist() throws Exception {
    // given
    given(this.service.fetchPersonByStaffNumber("1")).willReturn(null);

    // when/then
    this.mockMvc.perform(delete(BASE_URI + "1")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());

  }

  @Test
  public void deletePersonShouldReturnNoContent() throws Exception {
    // given
    given(this.service.fetchPersonByStaffNumber("1"))
        .willReturn(PersonDTO.builder().name("Samba").staffNumber("1").build());

    // when/then
    this.mockMvc.perform(delete(BASE_URI + "1")).andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNoContent());

  }

  private static String json(Object o) throws IOException {
    ObjectMapper map = new ObjectMapper();
    return map.writeValueAsString(o);
  }


}
