package org.example.patientInformation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.example.patientinformation.FirestoreContext;
import org.example.patientinformation.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import org.example.patientinformation.PatientRecord;

public class UserServiceTest {

    private Firestore mockDb;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        mockDb = mock(Firestore.class);
        FirestoreContext.setTestFirestore(mockDb); // inject mock Firestore
        userService = new UserService();           // initializes with mocked Firestore
    }

    @AfterEach
    public void tearDown() {
        FirestoreContext.clearTestFirestore(); // clean up
    }

    @Test
    public void testAddPatient_noExceptionThrown() throws Exception {
        // Arrange
        PatientRecord newPatient = new PatientRecord(
                "Test User",
                30,
                "Flu",
                "Tamiflu",
                125.50,
                "2025-06-01",
                "test@example.com",
                "Female"
        );

        CollectionReference mockUsers = mock(CollectionReference.class);
        DocumentReference mockDocRef = mock(DocumentReference.class);
        ApiFuture<WriteResult> mockWriteResult = mock(ApiFuture.class);

        when(mockDb.collection("users")).thenReturn(mockUsers);
        when(mockUsers.document("test@example.com")).thenReturn(mockDocRef);
        when(mockDocRef.set(any(Map.class))).thenReturn(mockWriteResult);
        when(mockWriteResult.get()).thenReturn(null);


        // Act & Assert
        assertDoesNotThrow(() -> userService.addPatient(newPatient));
    }



    @Test
    public void testGetAllPatients_returnsEmptyOnException() throws Exception {
        CollectionReference mockUsers = mock(CollectionReference.class);
        Query mockQuery = mock(Query.class);
        ApiFuture<QuerySnapshot> mockFuture = mock(ApiFuture.class);

        when(mockDb.collection("users")).thenReturn(mockUsers);
        when(mockUsers.get()).thenReturn(mockFuture);
        when(mockFuture.get()).thenThrow(new RuntimeException("Simulated failure"));

        List<Map<String, Object>> result = userService.getAllPatients();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

}


