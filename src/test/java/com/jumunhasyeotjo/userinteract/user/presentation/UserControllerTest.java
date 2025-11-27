package com.jumunhasyeotjo.userinteract.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.user.application.UserService;
import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;
import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.service.UserDomainService;
import com.jumunhasyeotjo.userinteract.user.presentation.controller.UserController;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.ApproveReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean
    UserDomainService userDomainService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    UserService userService;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;
}