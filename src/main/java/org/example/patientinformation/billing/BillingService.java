package org.example.patientinformation.billing;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.example.patientinformation.FirestoreContext;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class BillingService {

    private final Firestore db = FirestoreContext.getFirestore();

    //Load every invoice for a particular user
    public List<Invoice> findAll(String userEmail)
        throws InterruptedException, ExecutionException {

        ApiFuture<QuerySnapshot> q =
                db.collection("users").document(userEmail)
                        .collection("invoices").get();

        return q.get().toObjects(Invoice.class);
    }

    //Create/update an invoice. For Doctor accounts only
    public void save(String userEmail, Invoice inv) {
        inv.setUpdatedAt(Timestamp.now());
        db.collection("users").document(userEmail)
                .collection("billing").document(inv.getId())
                .set(inv, SetOptions.merge());
    }
}
