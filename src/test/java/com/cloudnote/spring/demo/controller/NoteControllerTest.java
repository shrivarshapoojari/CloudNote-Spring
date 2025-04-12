package com.cloudnote.spring.demo.controller;





import com.cloudnote.spring.demo.model.Note;
import com.cloudnote.spring.demo.security.jwt.JwtUtils;
import com.cloudnote.spring.demo.service.NoteService;
import com.cloudnote.spring.demo.service.impl.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
public class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteService noteService;


//    @MockBean
//    UserDetailsService userDetailsService;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser", roles = {"ROLE_USER"})
    void testCreateNote() throws Exception {
        // Mocked Note
        Note note = new Note(1L, "testUser", "My new note");

        // Mocking noteService
        Mockito.when(noteService.createNoteForUser(eq("testUser"), eq("My new note")))
                .thenReturn(note);

        // Performing POST request and asserting the response
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("My new note"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteId").value(1))
                .andExpect(jsonPath("$.userName").value("testUser"))
                .andExpect(jsonPath("$.content").value("My new note"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"ROLE_USER"})
    void testGetUserNotes() throws Exception {
        List<Note> notes = Arrays.asList(
                new Note(1L, "testUser", "Note 1"),
                new Note(2L, "testUser", "Note 2")
        );

        // Mocking noteService
        Mockito.when(noteService.getNotesForUser("testUser")).thenReturn(notes);

        // Performing GET request and asserting the response
        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].content").value("Note 1"))
                .andExpect(jsonPath("$[1].content").value("Note 2"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"ROLE_USER"})
    void testUpdateNote() throws Exception {
        Note updatedNote = new Note(1L, "testUser", "Updated note");

        // Mocking noteService
        Mockito.when(noteService.updateNoteForUser(eq(1L), eq("Updated note"), eq("testUser")))
                .thenReturn(updatedNote);

        // Performing PUT request and asserting the response
        mockMvc.perform(put("/api/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Updated note"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noteId").value(1))
                .andExpect(jsonPath("$.content").value("Updated note"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"ROLE_USER"})
    void testDeleteNote() throws Exception {
        // Mocking noteService
        Mockito.doNothing().when(noteService).deleteNoteForUser(1L, "testUser");

        // Performing DELETE request and asserting the response
        mockMvc.perform(delete("/api/notes/1"))
                .andExpect(status().isOk());
    }
}
