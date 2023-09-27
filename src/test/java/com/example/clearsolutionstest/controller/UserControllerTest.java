package com.example.clearsolutionstest.controller;

import com.example.clearsolutionstest.entity.User;
import com.example.clearsolutionstest.request.UserUpdateNonRequiredDataRequest;
import com.example.clearsolutionstest.service.UserService;
import java.text.SimpleDateFormat;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void testCreateUserWithValidData() throws Exception {
    User user = new User();
    user.setFirstname("string");
    user.setLastname("string");
    user.setBirthDate(new Date(2003 - 1900, 8, 27, 10, 52, 3));
    user.setEmail("email@example.com");

    when(userService.create(any(User.class))).thenReturn(user);

    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(
                "{\n" + "  \"birthDate\": \"2003-09-27T10:52:03.246Z\",\n"
                    + "  \"email\": \"email@example.com\",\n" + "  \"firstname\": \"string\",\n"
                    + "  \"id\": 0,\n" + "  \"lastname\": \"string\"\n" + "}"))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("string")).andReturn();
  }

  @Test
  public void testCreateUserWithInvalidData() throws Exception {
    when(userService.create(any(User.class))).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON)
        .content("{ \"firstName\": null }")).andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdateUserNonRequiredData() throws Exception {
    Long userId = 1L;
    UserUpdateNonRequiredDataRequest request = new UserUpdateNonRequiredDataRequest();
    request.setAddress("UpdatedAddress");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setAddress("UpdatedAddress");

    when(userService.updateUserNonRequiredData(userId, request)).thenReturn(updatedUser);

    mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON).content("{ \"address\": \"UpdatedAddress\" }"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("UpdatedAddress"));
  }

  @Test
  public void testUpdateUserNonRequiredDataNotFound() throws Exception {
    Long userId = 1L;
    UserUpdateNonRequiredDataRequest request = new UserUpdateNonRequiredDataRequest();
    request.setAddress("UpdatedAddress");

    when(userService.updateUserNonRequiredData(userId, request)).thenThrow(
        new NoSuchElementException());

    mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON).content("{ \"firstName\": \"UpdatedFirstName\" }"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testUpdateAllUserFieldsSuccess() throws Exception {
    Long userId = 1L;
    User updatedUserData = new User();
    updatedUserData.setFirstname("Updated Firstname");

    when(userService.update(eq(userId), any(User.class))).thenReturn(updatedUserData);

    mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON).content("{\"firstname\":\"Updated Firstname\"}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstname").value("Updated Firstname"));

    verify(userService, times(1)).update(eq(userId), any(User.class));
  }

  @Test
  public void testUpdateAllUserFieldsNoSuchElementException() throws Exception {
    Long userId = 1L;

    when(userService.update(eq(userId), any(User.class))).thenThrow(NoSuchElementException.class);

    mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());

    verify(userService, times(1)).update(eq(userId), any(User.class));
  }

  @Test
  public void testDeleteUser() throws Exception {
    Long userId = 1L;

    doNothing().when(userService).deleteUser(userId);

    mockMvc.perform(delete("/users/{userId}", userId)).andExpect(status().isNoContent());

    verify(userService, times(1)).deleteUser(userId);
  }

  @Test
  public void testSearchUsersByBirthDateRangeSuccess() throws Exception {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date fromDateAsDate = dateFormat.parse("2003-09-27");
    Date toDateAsDate = dateFormat.parse("2003-09-28");
    Pageable pageable = PageRequest.of(0, 10);

    when(userService.findUsersByBirthDateRange(eq(fromDateAsDate), eq(toDateAsDate),
        eq(pageable))).thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

    mockMvc.perform(MockMvcRequestBuilders.get("/users/search").param("from", "2003-09-27")
            .param("to", "2003-09-28").param("page", "0").param("size", "10"))
        .andExpect(status().isOk());

    verify(userService, times(1)).findUsersByBirthDateRange(eq(fromDateAsDate), eq(toDateAsDate),
        eq(pageable));
  }
}

